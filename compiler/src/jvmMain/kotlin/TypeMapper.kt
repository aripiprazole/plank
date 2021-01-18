package com.lorenzoog.jplank.compiler

import com.lorenzoog.jplank.analyzer.Builtin
import com.lorenzoog.jplank.analyzer.type.PkType
import io.vexelabs.bitbuilder.llvm.ir.Type

class TypeMapper {
  fun map(context: PlankContext, type: PkType?): Type? {
    return when (type) {
      null -> context.runtime.types.void
      Builtin.Void -> context.runtime.types.void
      Builtin.Int -> context.runtime.types.int
      Builtin.Double -> context.runtime.types.double
      Builtin.Numeric -> context.runtime.types.double
      Builtin.Bool -> context.runtime.types.i1
      Builtin.Char.pointer -> context.runtime.types.string
      Builtin.Any -> context.runtime.types.any.getPointerType()
      is PkType.Generic -> context.runtime.types.any.getPointerType()
      is PkType.Array -> mapPkArray(context, type)
      is PkType.Struct -> mapPkStructure(context, type)
      is PkType.Callable -> mapPkCallable(context, type)
      is PkType.Pointer -> mapPkPtr(context, type)
    }
  }

  private fun mapPkPtr(context: PlankContext, ptr: PkType.Pointer): Type? {
    return map(context, ptr.inner)?.getPointerType()
  }

  private fun mapPkArray(context: PlankContext, array: PkType.Array): Type? {
    return map(context, array.inner)?.getPointerType()
  }

  private fun mapPkStructure(context: PlankContext, structure: PkType.Struct): Type? {
    return context.findStructure(structure.name)
  }

  private fun mapPkCallable(context: PlankContext, callable: PkType.Callable): Type? {
    val returnType =
      context.map(callable.returnType) ?: return context.report("returnType is null {$callable}")

    return context.llvm.getFunctionType(
      returnType,
      *callable.parameters
        .mapIndexed { index, type ->
          context.map(type)
            ?: return context.report("failed to handle argument with index $index {$callable}")
        }
        .toTypedArray(),
      variadic = false
    )
  }
}
