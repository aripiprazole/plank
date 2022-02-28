package org.plank.analyzer.checker

import org.plank.analyzer.AnalyzerViolation
import org.plank.analyzer.element.ResolvedDecl
import org.plank.analyzer.element.ResolvedExprStmt
import org.plank.analyzer.element.ResolvedFunctionBody
import org.plank.analyzer.element.ResolvedLetDecl
import org.plank.analyzer.element.ResolvedNoBody
import org.plank.analyzer.element.ResolvedPlankElement
import org.plank.analyzer.element.ResolvedPlankFile
import org.plank.analyzer.element.ResolvedStmt
import org.plank.analyzer.element.TypedConstExpr
import org.plank.analyzer.element.TypedExpr
import org.plank.analyzer.infer.Infer
import org.plank.analyzer.infer.Scheme
import org.plank.analyzer.infer.Subst
import org.plank.analyzer.infer.Ty
import org.plank.analyzer.infer.ftv
import org.plank.analyzer.infer.inferExpr
import org.plank.analyzer.infer.undefTy
import org.plank.analyzer.resolver.FileScope
import org.plank.analyzer.resolver.ResolveResult
import org.plank.analyzer.resolver.Scope
import org.plank.syntax.element.Expr
import org.plank.syntax.element.Location
import org.plank.syntax.element.PlankElement
import org.plank.syntax.element.PlankFile
import org.plank.syntax.element.toIdentifier
import org.plank.syntax.message.SimpleCompilerLogger
import kotlin.reflect.KClass

fun ResolveResult.typeCheck(logger: SimpleCompilerLogger? = null): ResolvedPlankFile {
  return TypeCheck(this, logger).check()
}

class TypeCheck(result: ResolveResult, val logger: SimpleCompilerLogger?) {
  val dependencies = result.dependencies
  val file = result.file
  val tree = result.tree

  val infer = Infer()

  var violations: List<AnalyzerViolation> = emptyList()
  var scope: Scope = tree.globalScope

  fun check(): ResolvedPlankFile {
    val dependencies = dependencies
      .map { it.scope }
      .filterIsInstance<FileScope>()
      .dropLast(1)
      .map {
        checkFile(it.file)
      }

    return checkFile(file).copy(dependencies = dependencies, analyzerViolations = violations)
  }

  fun checkFile(file: PlankFile): ResolvedPlankFile {
    runCatching {
      val module = requireNotNull(tree.findModule(file.module)) { "Could not find file in tree" }

      return scoped(module.scope) {
        val program = file.program.map(::checkStmt).filterIsInstance<ResolvedDecl>()

        ResolvedPlankFile(program, module, tree, file)
      }
    }.onFailure { error ->
      if (logger != null) {
        violations.forEach { it.render(logger) }
      }

      throw error
    }

    error("unreachable")
  }

  fun Ty.generalize(): Scheme {
    return Scheme(ftv().sorted().filter { it !in scope.names }.toSet(), this)
  }

  fun infer(expr: Expr): Pair<Ty, Subst> {
    return with(infer) { inferExpr(scope.asTyEnv(), expr) }
  }

  inline fun <reified A : ResolvedPlankElement> violate(
    el: PlankElement,
    violation: AnalyzerViolation,
    fn: () -> A = { violatedElement(el.location, A::class) },
  ): A {
    return fn().also { violations = violations + violation.withLocation(el.location) }
  }

  @Suppress("Unchecked_Cast")
  fun <A : ResolvedPlankElement> violatedElement(loc: Location, type: KClass<A>): A {
    return when (type) {
      ResolvedFunctionBody::class -> ResolvedNoBody(loc) as A
      TypedExpr::class -> TypedConstExpr(Unit, undefTy, location = loc) as A
      ResolvedStmt::class -> {
        ResolvedExprStmt(TypedConstExpr(Unit, undefTy, location = loc), loc) as A
      }
      ResolvedDecl::class -> {
        ResolvedLetDecl(
          name = "<undef>".toIdentifier(),
          value = TypedConstExpr(Unit, undefTy, location = loc),
          scheme = Scheme(undefTy),
          ty = undefTy,
          location = loc,
        ) as A
      }
      else -> error("Unsupported type: $type")
    }
  }

  inline fun <A> scoped(scope: Scope, block: Scope.() -> A): A = this.scope.let { oldScope ->
    this.scope = scope
    val value = scope.block()
    this.scope = oldScope
    value
  }
}
