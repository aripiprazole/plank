package com.lorenzoog.plank.compiler.instructions.element

import com.lorenzoog.plank.compiler.CompilerContext
import com.lorenzoog.plank.compiler.buildAlloca
import com.lorenzoog.plank.compiler.buildLoad
import com.lorenzoog.plank.compiler.buildReturn
import com.lorenzoog.plank.compiler.buildStore
import com.lorenzoog.plank.compiler.instructions.CodegenError
import com.lorenzoog.plank.compiler.instructions.llvmError
import com.lorenzoog.plank.compiler.instructions.unresolvedTypeError
import com.lorenzoog.plank.compiler.instructions.unresolvedVariableError
import com.lorenzoog.plank.grammar.element.Decl
import com.lorenzoog.plank.shared.Either
import com.lorenzoog.plank.shared.Left
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either
import org.llvm4j.llvm4j.Constant
import org.llvm4j.llvm4j.Function
import org.llvm4j.optional.Err
import org.llvm4j.optional.Ok

class IREnumConstructor(
  private val member: Decl.EnumDecl.Member,
  override val descriptor: Decl.EnumDecl,
) : IRFunction() {
  override val name = member.name.text
  override val mangledName = "${descriptor.name.text}_$name" // TODO: mangle properly

  override fun accessIn(context: CompilerContext): Function? {
    return context.module.getFunction(mangledName).toNullable()
  }

  override fun CompilerContext.codegen(): Either<CodegenError, Function> = either {
    val parameters = member.fields
      .map(binding::visit)
      .map { !it.toType() }

    val returnType = !binding.visit(descriptor).toType()
    val functionType = context.getFunctionType(
      returnType,
      *parameters.toTypedArray(),
      isVariadic = false
    )

    val struct = findStruct(mangledName) ?: return Left(unresolvedTypeError(name))

    val function = module.addFunction(mangledName, functionType)

    createNestedScope(descriptor.name.text) {
      context.newBasicBlock("entry")
        .also(function::addBasicBlock)
        .also(builder::positionAfter)

      val arguments = function.getParameters().mapIndexed { index, parameter ->
        parameter.setName("$index")

        val type = parameter.getType()
        val variable = buildAlloca(type, parameter.getName())

        buildStore(variable, parameter)

        Constant(buildLoad(variable).ref)
      }

      val instance = struct.getConstant(*arguments.toTypedArray(), isPacked = false)

      buildReturn(
        when (instance) {
          is Ok -> instance.value
          is Err -> {
            return Left(llvmError(instance.error.message ?: "failed to create enum instance"))
          }
        }
      )
    }

    Right(function)
  }
}
