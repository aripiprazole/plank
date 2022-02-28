package org.plank.codegen

import org.plank.analyzer.element.TypedAccessExpr
import org.plank.analyzer.element.TypedExpr
import org.plank.analyzer.resolver.StructInfo
import org.plank.llvm4k.ir.AddrSpace
import org.plank.llvm4k.ir.AllocaInst
import org.plank.llvm4k.ir.Constant
import org.plank.llvm4k.ir.Function
import org.plank.llvm4k.ir.LoadInst
import org.plank.llvm4k.ir.StructType
import org.plank.llvm4k.ir.Type
import org.plank.llvm4k.ir.Value
import org.plank.syntax.element.Identifier

expect fun CodegenContext.unsafeAlloca(value: Value): AllocaInst

expect fun CodegenContext.unsafeFunction(value: Value): Function

fun CodegenContext.castClosure(closure: Value, type: Type): LoadInst {
  type as StructType

  return createLoad(createBitCast(closure, type.pointer(AddrSpace.Generic)))
}

fun CodegenContext.createUnit(): Constant {
  return unit.getConstant(i8.getConstant(0, false), isPacked = false)
}

fun CodegenContext.getOrCreateStruct(name: String, builder: StructType.() -> Unit): StructType {
  return currentModule.getTypeByName(name) ?: createNamedStruct(name, builder)
}

fun CodegenContext.alloca(value: Value, name: String? = null): AllocaInst {
  val alloca = createAlloca(value.type, name = name)
  createStore(value, alloca)
  return alloca
}

inline fun CodegenContext.instantiate(
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

fun CodegenContext.getField(value: Value, idx: Int, name: String? = null): Value {
  return createGEP(value, i32.getConstant(0, false), i32.getConstant(idx, false), name = name)
}

fun CodegenContext.findField(receiver: TypedExpr, info: StructInfo, name: Identifier): Value {
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
