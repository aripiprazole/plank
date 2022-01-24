package com.gabrielleeg1.plank.compiler.instructions.element

import com.gabrielleeg1.plank.analyzer.EnumMember
import com.gabrielleeg1.plank.analyzer.EnumType
import com.gabrielleeg1.plank.analyzer.element.ResolvedEnumDecl
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.builder.buildBitcast
import com.gabrielleeg1.plank.compiler.builder.buildLoad
import com.gabrielleeg1.plank.compiler.builder.buildReturn
import com.gabrielleeg1.plank.compiler.builder.getField
import com.gabrielleeg1.plank.compiler.builder.getInstance
import com.gabrielleeg1.plank.compiler.createScopeContext
import com.gabrielleeg1.plank.compiler.debug
import com.gabrielleeg1.plank.compiler.instructions.unresolvedTypeError
import com.gabrielleeg1.plank.grammar.element.Identifier
import org.llvm4j.llvm4j.AllocaInstruction
import org.llvm4j.llvm4j.Function

class IREnumConstructor(
  private val member: EnumMember,
  private val descriptor: ResolvedEnumDecl,
) : IRFunction {
  override val name = member.name.text
  override val mangledName = "${descriptor.name.text}_$name" // TODO: mangle properly

  override fun accessIn(context: CompilerContext): AllocaInstruction? {
    TODO()
  }

  override fun CompilerContext.codegen(): Function {
    val parameters = member.fields.map { it.typegen() }

    val enum = descriptor.type.cast<EnumType>() ?: unresolvedTypeError(name)

    val functionType = context.getFunctionType(
      enum.typegen(),
      *parameters.toTypedArray(),
      isVariadic = false
    )

    val struct = findStruct(mangledName) ?: unresolvedTypeError(name)

    val function = module.addFunction(mangledName, functionType)

    createScopeContext(descriptor.name.text) {
      context.newBasicBlock("entry")
        .also(function::addBasicBlock)
        .also(builder::positionAfter)

      val arguments = function.getParameters()

      val index = runtime.types.tag.getConstant(enum.tag(Identifier(name)))
      val instance = getInstance(struct, index, *arguments, isPointer = true)

      val bitcast = buildBitcast(instance, enum.typegen())

      debug {
        printf(
          "Creating enum member $name with tag %d (%d) of ${enum.name}",
          buildLoad(getField(bitcast, 0)),
          index
        )
      }

      buildReturn(bitcast)
    }

    return function
  }
}
