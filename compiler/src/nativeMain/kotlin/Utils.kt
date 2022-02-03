package com.gabrielleeg1.plank.compiler

import com.gabrielleeg1.plank.analyzer.UnitType
import com.gabrielleeg1.plank.analyzer.element.TypedAccessExpr
import com.gabrielleeg1.plank.analyzer.element.TypedExpr
import com.gabrielleeg1.plank.grammar.element.Identifier
import org.plank.llvm4k.ir.AllocaInst
import org.plank.llvm4k.ir.Constant
import org.plank.llvm4k.ir.LoadInst
import org.plank.llvm4k.ir.StructType
import org.plank.llvm4k.ir.Type
import org.plank.llvm4k.ir.Value

fun CodegenContext.castClosure(closure: Value, type: Type): LoadInst {
  type as StructType

  return createLoad(createBitCast(closure, type.pointer()))
}

fun CodegenContext.createUnit(): Constant {
  return (UnitType.typegen() as StructType).getConstant(i8.getConstant(0))
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
  ref: Boolean = false,
  name: String = "v.${struct.name}",
  generateGEPName: (Int, String) -> String = { idx, value -> "$value.[$idx]" }
): Value {
  val instance = createAlloca(struct, name = name)

  arguments.forEachIndexed { idx, value ->
    val field = getField(instance, idx, name = generateGEPName(idx, name))

    createStore(value, field)
  }

  return if (ref) {
    instance
  } else {
    createLoad(instance, "load.$name")
  }
}

fun CodegenContext.getField(value: Value, idx: Int, name: String? = null): Value {
  return createGEP(value, i32.getConstant(0), i32.getConstant(idx), name = name)
}

fun CodegenContext.findField(receiver: TypedExpr, name: Identifier): Value {
  val struct = when (receiver) {
    is TypedAccessExpr -> receiver.name.text
    else -> receiver.type.name.text
  }

  val instance = when (receiver) {
    is TypedAccessExpr -> findSymbol(receiver.name.text)
    else -> receiver.codegen()
  }

  val alloca = when (instance) {
    is AllocaInst -> instance
    else -> alloca(instance)
  }

  if (!receiver.type.isInstance<com.gabrielleeg1.plank.analyzer.StructType>()) {
    codegenError("Unresolved type `${receiver.type.name.text}`")
  }

  val propertyIndex = receiver.type
    .cast<com.gabrielleeg1.plank.analyzer.StructType>()!!.properties.entries
    .indexOfFirst { it.key == name }

  return getField(alloca, propertyIndex, "$struct.${name.text}")
}
