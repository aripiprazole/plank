package org.plank.codegen.intrinsics

import org.plank.codegen.CodegenContext
import org.plank.codegen.ExecContext
import org.plank.codegen.createUnit
import org.plank.codegen.mangle
import org.plank.llvm4k.Context
import org.plank.llvm4k.ir.AddrSpace
import org.plank.llvm4k.ir.Argument
import org.plank.llvm4k.ir.Function
import org.plank.llvm4k.ir.FunctionType
import org.plank.llvm4k.ir.Linkage
import org.plank.llvm4k.ir.Type
import org.plank.syntax.message.lineSeparator
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

class Intrinsics(builder: Intrinsics.() -> Unit = {}) {
  private val modules: MutableMap<String, IntrinsicModule.() -> Unit> = linkedMapOf()

  init {
    builder()
  }

  fun module(name: String, builder: IntrinsicModule.() -> Unit) {
    modules[name] = builder
  }

  fun toFunctionMap(context: CodegenContext): Map<String, IntrinsicFunction> {
    return modules
      .flatMap { (name, builder) ->
        val module = IntrinsicModule(name, context).apply(builder)

        module.toFunctionMap().map { Triple(module, it.key, it.value) }
      }
      .associate { (module, name, function) -> "${module.name}.$name" to function }
  }

  operator fun Intrinsics.unaryPlus() {
    this@Intrinsics.modules += this.modules
  }
}

class IntrinsicModule(val name: String, val context: CodegenContext) : Context by context {
  private val functions: MutableMap<String, IntrinsicFunction> = linkedMapOf()

  val module by context::currentModule
  val unit by context::unit

  fun function(
    returnType: Type,
    vararg parameters: Type,
    varargs: Boolean = false,
    builder: IntrinsicFunction.() -> Unit = {},
  ) = PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, Function>> { _, property ->
    val type = FunctionType(returnType, *parameters, isVarargs = varargs)

    functions[property.name] = IntrinsicFunction().apply(builder)

    ReadOnlyProperty { _, _ ->
      context.currentModule.getFunction(property.name)
        ?: context.currentModule.addFunction(property.name, type)
    }
  }

  fun toFunctionMap(): Map<String, IntrinsicFunction> = functions
}

class IntrinsicFunction {
  private var function: (Function.() -> Unit)? = null
  private var entry: (ExecContext.() -> Unit)? = null

  fun function(builder: Function.() -> Unit) {
    function = builder
  }

  fun entry(builder: ExecContext.(List<Argument>) -> Unit) {
    entry = {
      builder(function.arguments.drop(1))
    }
  }

  fun build(context: ExecContext) {
    function?.invoke(context.function)
    entry?.invoke(context)
  }
}

@Suppress("UNUSED_VARIABLE", "UnusedPrivateMember", "LocalVariableName", "VariableNaming")
val DefaultIntrinsics = Intrinsics {
  module("Std.IO") {
    val printf by function(i32, i8.pointer(AddrSpace.Generic), varargs = true) {
      function {
        linkage = Linkage.External
      }
    }

    val println by function(unit, i8.pointer(AddrSpace.Generic)) {
      entry { (msg) ->
        createCall(printf, createGlobalStringPtr("%s$lineSeparator", mangle("println.str")), msg)
        createRet(createUnit())
      }
    }

    val print_int by function(unit, i32) {
      entry { (msg) ->
        createCall(printf, createGlobalStringPtr("%d$lineSeparator", mangle("print_int.str")), msg)
        createRet(createUnit())
      }
    }

    val print_bool by function(unit, i32) {
      entry { (msg) ->
        createCall(printf, createGlobalStringPtr("%d$lineSeparator", mangle("print_bool.str")), msg)
        createRet(createUnit())
      }
    }

    val print by function(unit, i8.pointer(AddrSpace.Generic)) {
      entry { (msg) ->
        createCall(printf, createGlobalStringPtr("%s", mangle("print.str")), msg)
        createRet(createUnit())
      }
    }
  }
}
