package com.gabrielleeg1.plank.compiler.instructions.element

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import com.gabrielleeg1.plank.analyzer.EnumMember
import com.gabrielleeg1.plank.analyzer.EnumType
import com.gabrielleeg1.plank.analyzer.PlankType
import com.gabrielleeg1.plank.analyzer.element.ResolvedEnumDecl
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.buildBitcast
import com.gabrielleeg1.plank.compiler.buildLoad
import com.gabrielleeg1.plank.compiler.buildReturn
import com.gabrielleeg1.plank.compiler.builder.getField
import com.gabrielleeg1.plank.compiler.builder.getInstance
import com.gabrielleeg1.plank.compiler.instructions.CodegenError
import com.gabrielleeg1.plank.compiler.instructions.unresolvedTypeError
import com.gabrielleeg1.plank.grammar.element.Identifier
import org.llvm4j.llvm4j.Function
import org.llvm4j.llvm4j.NamedStructType

class IREnumConstructor(
  private val member: EnumMember,
  override val descriptor: ResolvedEnumDecl,
) : IRFunction() {
  override val name = member.name.text
  override val mangledName = "${descriptor.name.text}_$name" // TODO: mangle properly

  override fun accessIn(context: CompilerContext): Function? {
    return context.module.getFunction(mangledName).toNullable()
  }

  override fun CompilerContext.codegen(): Either<CodegenError, Function> = either.eager {
    val parameters = member.fields.map { it.toType().bind() }

    val enum = descriptor.type.cast()
      ?: unresolvedTypeError(name)
        .left()
        .bind<EnumType>()

    val functionType = context.getFunctionType(
      enum.toType().bind(),
      *parameters.toTypedArray(),
      isVariadic = false
    )

    val struct = findStruct(mangledName)
      ?: unresolvedTypeError(name).left().bind<NamedStructType>()

    val function = module.addFunction(mangledName, functionType)

    createNestedScope(descriptor.name.text) {
      context.newBasicBlock("entry")
        .also(function::addBasicBlock)
        .also(builder::positionAfter)

      val arguments = function.getParameters()

      val index = runtime.types.tag.getConstant(enum.tag(Identifier(name)))
      val instance = getInstance(struct, index, *arguments, isPointer = true).bind()

      val bitcast = buildBitcast(instance, enum.toType().bind())

      debug {
        printf(
          "Creating enum member $name with tag %d (%d) of ${enum.name}",
          buildLoad(getField(bitcast, 0).bind()),
          index
        )
      }

      buildReturn(bitcast)
    }

    function
  }
}
