package com.lorenzoog.jplank.compiler

import com.lorenzoog.jplank.analyzer.Builtin
import com.lorenzoog.jplank.analyzer.type.PkArray
import com.lorenzoog.jplank.analyzer.type.PkCallable
import com.lorenzoog.jplank.analyzer.type.PkStructure
import com.lorenzoog.jplank.analyzer.type.PkType
import io.vexelabs.bitbuilder.llvm.ir.Type

class TypeMapper {
  fun map(context: PlankContext, type: PkType?): Type? {
    return when (type) {
      null -> context.runtime.types.void
      Builtin.Void -> context.runtime.types.void
      Builtin.Int -> context.runtime.types.int
      Builtin.Double -> context.runtime.types.double
      Builtin.Bool -> context.runtime.types.i1
      Builtin.String -> context.runtime.types.string
      is PkArray -> mapPkArray(context, type)
      is PkStructure -> mapPkStructure(context, type)
      is PkCallable -> mapPkCallable(context, type)
    }
  }

  private fun mapPkArray(context: PlankContext, array: PkArray): Type? {
    return map(context, array.inner)?.getPointerType()
  }

  private fun mapPkStructure(context: PlankContext, structure: PkStructure): Type? {
    return context.findStructure(structure.name)
  }

  private fun mapPkCallable(context: PlankContext, callable: PkCallable): Type? {
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
