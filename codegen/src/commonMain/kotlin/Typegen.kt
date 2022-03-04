package org.plank.codegen

import org.plank.analyzer.checker.lastName
import org.plank.analyzer.checker.replaceLastName
import org.plank.analyzer.infer.AppTy
import org.plank.analyzer.infer.ConstTy
import org.plank.analyzer.infer.FunTy
import org.plank.analyzer.infer.PtrTy
import org.plank.analyzer.infer.Ty
import org.plank.analyzer.infer.boolTy
import org.plank.analyzer.infer.chainExecution
import org.plank.analyzer.infer.charTy
import org.plank.analyzer.infer.i16Ty
import org.plank.analyzer.infer.i32Ty
import org.plank.analyzer.infer.i8Ty
import org.plank.analyzer.infer.unify
import org.plank.analyzer.infer.unitTy
import org.plank.codegen.scope.CodegenCtx
import org.plank.codegen.type.CodegenType
import org.plank.codegen.type.RankedType
import org.plank.llvm4k.ir.AddrSpace
import org.plank.llvm4k.ir.Type
import org.plank.syntax.element.toQualifiedPath
import org.plank.llvm4k.ir.FunctionType as LLVMFunctionType

@Suppress("Detekt.ComplexMethod")
fun CodegenCtx.typegen(ty: Ty): Type {
  return when (ty) {
    unitTy -> unit
    boolTy -> i1
    charTy -> i8
    i8Ty -> i8
    i16Ty -> i16
    i32Ty -> i32
    is PtrTy -> ty.arg.typegen().pointer(AddrSpace.Generic)
    is ConstTy -> {
      val path = ty.name.toQualifiedPath()
      val name = path.last()
      val module = findModule(path.dropLast().text) ?: this

      module.findType(name.text)?.get()
        ?: codegenError("Unresolved type `${ty.name}`")
    }
    is FunTy -> {
      val returnTy = ty.returnTy.typegen()
      val parameterTy = ty.parameterTy.typegen()

      val functionType = if (parameterTy.kind == Type.Kind.Void) {
        LLVMFunctionType(returnTy, i8.pointer(AddrSpace.Generic))
      } else {
        LLVMFunctionType(returnTy, i8.pointer(AddrSpace.Generic), parameterTy)
      }

      getOrCreateStruct("$ty") {
        elements = listOf(functionType.pointer(AddrSpace.Generic), i8.pointer(AddrSpace.Generic))
      }
    }
    is AppTy -> {
      val codegenType = codegenType(ty) as RankedType
      val schemeTy = codegenType.scheme.ty.chainExecution().last()

      val subst = unify(schemeTy, ty.replaceLastName(codegenType.scheme.ty.lastName()))

      codegenType.get(subst)
    }
    else -> codegenError("Unsupported type `$ty`")
  }
}

fun CodegenCtx.codegenType(ty: Ty): CodegenType = when (ty) {
  is AppTy -> codegenType(ty.fn)
  is ConstTy -> {
    val path = ty.name.toQualifiedPath()
    val name = path.last()
    val module = findModule(path.dropLast().text) ?: this

    module.findType(name.text)
      ?: codegenError("Unresolved type `${ty.name}` with $subst")
  }
  else -> codegenError("Can not get a codegen type of ${ty::class.simpleName}($ty)")
}
