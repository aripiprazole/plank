package org.plank.analyzer.checker

import kotlin.reflect.KClass
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
import org.plank.analyzer.infer.CanNotUngeneralize
import org.plank.analyzer.infer.IncorrectEnumArity
import org.plank.analyzer.infer.Infer
import org.plank.analyzer.infer.InfiniteTy
import org.plank.analyzer.infer.IsNotCallable
import org.plank.analyzer.infer.IsNotConstructor
import org.plank.analyzer.infer.LitNotSupported
import org.plank.analyzer.infer.Scheme
import org.plank.analyzer.infer.Subst
import org.plank.analyzer.infer.Ty
import org.plank.analyzer.infer.TyError
import org.plank.analyzer.infer.UnboundVar
import org.plank.analyzer.infer.UnificationFail
import org.plank.analyzer.infer.inferExpr
import org.plank.analyzer.infer.nullSubst
import org.plank.analyzer.infer.undefTy
import org.plank.analyzer.resolver.ResolveResult
import org.plank.syntax.element.Expr
import org.plank.syntax.element.Loc
import org.plank.syntax.element.PlankElement
import org.plank.syntax.element.PlankFile
import org.plank.syntax.element.toIdentifier
import org.plank.syntax.message.CompilerLogger
import org.plank.syntax.message.NoopCompilerLogger

fun ResolveResult.typeCheck(logger: CompilerLogger = NoopCompilerLogger): ResolvedPlankFile {
  return TypeCheck(this, logger).check()
}

class TypeCheck(result: ResolveResult, val logger: CompilerLogger) {
  private val infer = Infer()

  val dependencies = result.dependencies
  val file = result.file
  val tree = result.tree

  val violations: MutableSet<CheckViolation> = mutableSetOf()
  var scope: Scope = GlobalScope

  fun check(): ResolvedPlankFile {
    val dependencies = dependencies
      .map { it.scope }
      .filterIsInstance<org.plank.analyzer.resolver.FileScope>()
      .dropLast(1)
      .map {
        checkFile(it.file)
      }

    return checkFile(file).copy(dependencies = dependencies, checkViolations = violations.toList())
  }

  fun checkFile(file: PlankFile): ResolvedPlankFile {
    runCatching {
      val module = requireNotNull(tree.findModule(file.module)) { "Could not find file in tree" }

      return scoped(FileScope(file, scope).also(scope::createModule)) {
        val program = file.program.map(::checkStmt).filterIsInstance<ResolvedDecl>()

        ResolvedPlankFile(program, module, tree, file)
      }
    }.onFailure { error ->
      violations.forEach { it.render(logger) }

      throw error
    }

    error("unreachable")
  }

  fun Ty.generalize(): Scheme {
    return with(infer) { scope.asTyEnv().generalize(this@generalize) }
  }

  fun fresh(): Ty = infer.fresh()

  fun instantiate(scheme: Scheme): Ty = try {
    infer.instantiate(scheme)
  } catch (error: TyError) {
    violations += error.asViolation()
    undefTy
  }

  fun unify(a: Ty, b: Ty): Subst = try {
    org.plank.analyzer.infer.unify(a, b)
  } catch (error: TyError) {
    violations += error.asViolation()
    nullSubst()
  }

  fun infer(expr: Expr): Pair<Ty, Subst> = try {
    with(infer) { inferExpr(scope.asTyEnv(), expr) }
  } catch (error: TyError) {
    violations += error.asViolation()
    undefTy to nullSubst()
  }

  inline fun <reified A : ResolvedPlankElement> violate(
    el: PlankElement,
    violation: CheckViolation,
    fn: () -> A = { violatedElement(el.loc, A::class) },
  ): A {
    return fn().also { violations += violation.withLocation(el.loc) }
  }

  @Suppress("Unchecked_Cast")
  fun <A : ResolvedPlankElement> violatedElement(loc: Loc, type: KClass<A>): A {
    return when (type) {
      ResolvedFunctionBody::class -> ResolvedNoBody(loc) as A
      TypedExpr::class -> TypedConstExpr(Unit, undefTy, loc = loc) as A
      ResolvedStmt::class -> {
        ResolvedExprStmt(TypedConstExpr(Unit, undefTy, loc = loc), loc) as A
      }

      ResolvedDecl::class -> {
        ResolvedLetDecl(
          name = "<undef>".toIdentifier(),
          value = TypedConstExpr(Unit, undefTy, loc = loc),
          scheme = Scheme(undefTy),
          ty = undefTy,
          loc = loc,
        ) as A
      }

      else -> error("Unsupported type: $type")
    }
  }

  fun TyError.asViolation(): CheckViolation = when (this) {
    is CanNotUngeneralize -> UnresolvedType(ty)
    is IncorrectEnumArity -> IncorrectEnumArity(expected, arity, variant.toIdentifier())
    is InfiniteTy -> TypeIsInfinite(variable, ty)
    is IsNotCallable -> TypeIsNotCallable(ty)
    is IsNotConstructor -> TypeIsNotStruct(ty)
    is LitNotSupported -> UnsupportedConstType(value::class)
    is UnboundVar -> UnresolvedVariable(name)
    is UnificationFail -> TypeMismatch(a, b)
  }

  inline fun <A> scoped(scope: Scope, block: Scope.() -> A): A = this.scope.let { oldScope ->
    this.scope = scope
    val value = scope.block()
    this.scope = oldScope
    value
  }
}
