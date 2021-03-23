package com.lorenzoog.jplank.compiler

import com.lorenzoog.jplank.analyzer.Builtin
import com.lorenzoog.jplank.analyzer.type.PlankType
import com.lorenzoog.jplank.compiler.llvm.orNull
import org.llvm4j.llvm4j.Type

class TypeMapper {
  fun map(context: PlankContext, type: PlankType?): Type? {
    return when (type) {
      Builtin.Void -> context.runtime.types.void
      Builtin.Int -> context.runtime.types.int
      Builtin.Double -> context.runtime.types.double
      Builtin.Numeric -> context.runtime.types.double
      Builtin.Bool -> context.runtime.types.i1
      Builtin.Char -> context.runtime.types.i8
      is PlankType.Array -> {
        map(context, type.inner)?.let(context.llvm::getPointerType)?.orNull()
      }
      is PlankType.Struct -> {
        context.findStructure(type.name)
      }
      is PlankType.Callable -> {
        val returnType =
          context.map(type.returnType)
            ?: return context.report("returnType is null {$type}")

        context.llvm.getFunctionType(
          returnType,
          *type.parameters
            .mapIndexed { index, type ->
              context.map(type)
                ?: return context.report("failed to handle argument with index $index {$type}")
            }
            .toTypedArray()
        )
      }
      is PlankType.Pointer -> {
        map(context, type.inner)?.let(context.llvm::getPointerType)?.orNull()
      }
      else -> context.runtime.types.void
    }
  }
}
