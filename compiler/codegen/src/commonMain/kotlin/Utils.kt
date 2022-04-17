package org.plank.codegen

import org.plank.analyzer.checker.StructInfo
import org.plank.analyzer.element.TypedAccessExpr
import org.plank.analyzer.element.TypedExpr
import org.plank.analyzer.infer.Subst
import org.plank.analyzer.infer.VarTy
import org.plank.codegen.scope.CodegenCtx
import org.plank.llvm4k.ir.AllocaInst
import org.plank.llvm4k.ir.Constant
import org.plank.llvm4k.ir.Function
import org.plank.llvm4k.ir.LoadInst
import org.plank.llvm4k.ir.StructType
import org.plank.llvm4k.ir.Type
import org.plank.llvm4k.ir.Value
import org.plank.syntax.element.Identifier

expect fun CodegenCtx.unsafeAlloca(value: Value): AllocaInst

expect fun CodegenCtx.unsafeFunction(value: Value): Function

infix fun Subst.ap(other: Subst): Subst {
  return Subst {
    val map = (this@ap compose other).toMap()

    other.toMap().forEach { (key, _) ->
      map[key]?.let {
        if (it is VarTy) {
          this@ap[key.name]?.let { ty -> put(key, ty) }
        } else {
          put(key, it)
        }
      }
    }
  }
}

fun CodegenCtx.castClosure(closure: Value, type: Type): LoadInst {
  type as StructType

  return createLoad(createBitCast(closure, type.pointer()))
}

fun CodegenCtx.createUnit(): Constant {
  return unit.getConstant(i8.getConstant(0, false), isPacked = false)
}

fun CodegenCtx.getOrCreateStruct(name: String, builder: StructType.() -> Unit): StructType {
  return currentModule.getTypeByName(name) ?: createNamedStruct(name, builder)
}

fun CodegenCtx.alloca(value: Value, name: String? = null): AllocaInst {
  val alloca = createAlloca(value.type, name = name)
  createStore(value, alloca)
  return alloca
}

inline fun CodegenCtx.instantiate(
  struct: StructType,
  vararg arguments: Value,
  name: String? = null,
  generateGEPName: (Int, String) -> String? = { _, _ -> null },
): AllocaInst {
  val instance = createAlloca(struct, name = name)

  arguments.forEachIndexed { idx, value ->
    val field = getField(instance, idx, name = generateGEPName(idx, name ?: "v"))

    createStore(value, field)
  }

  return instance
}

fun CodegenCtx.getField(value: Value, idx: Int, name: String? = null): Value {
  return createGEP(value, i32.getConstant(0, false), i32.getConstant(idx, false), name = name)
}

fun CodegenCtx.findField(receiver: TypedExpr, info: StructInfo, name: Identifier): Value {
  val struct = when (receiver) {
    is TypedAccessExpr -> receiver.name.text
    else -> receiver.ty.toString()
  }

  val instance = when (receiver) {
    is TypedAccessExpr -> getSymbol(this, receiver.name.text)
    else -> receiver.codegen()
  }

  val alloca = when (instance) {
    is AllocaInst -> instance
    else -> alloca(instance)
  }

  val propertyIndex = info.members.entries.indexOfFirst { it.key == name }

  return getField(alloca, propertyIndex, "$struct.${name.text}")
}
