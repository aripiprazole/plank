package org.plank.analyzer

import org.plank.analyzer.infer.Ty
import org.plank.analyzer.infer.VarTy
import org.plank.analyzer.resolver.Module
import org.plank.analyzer.resolver.Scope
import org.plank.analyzer.resolver.TyInfo
import org.plank.syntax.element.GeneratedLoc
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.Loc
import org.plank.syntax.message.CompilerLogger
import kotlin.reflect.KClass

sealed class AnalyzerViolation(val message: String, var loc: Loc) {
  fun render(logger: CompilerLogger) {
    logger.severe(message, loc)
  }

  fun withLocation(loc: Loc): AnalyzerViolation {
    this.loc = loc
    return this
  }

  override fun toString(): String = message
}

class UnsupportedConstType(type: KClass<*>, loc: Loc = GeneratedLoc) :
  AnalyzerViolation("Unsupported const type $type", loc)

class TypeMismatch(expected: Ty, got: Ty, loc: Loc = GeneratedLoc) :
  AnalyzerViolation("Type mismatch: expected $expected, got $got", loc)

class TypeIsInfinite(name: VarTy, ty: Ty, loc: Loc = GeneratedLoc) :
  AnalyzerViolation("Type $name $ty is infinite", loc)

class TypeIsNotCallable(ty: Ty, loc: Loc = GeneratedLoc) :
  AnalyzerViolation("Type $ty is not callable", loc)

class TypeIsNotStruct(ty: Ty, loc: Loc = GeneratedLoc) :
  AnalyzerViolation("Type $ty is not a struct and can not be instantiated", loc)

class TypeIsNotStructAndCanNotGet(
  name: Identifier,
  ty: Ty,
  loc: Loc = GeneratedLoc,
) : AnalyzerViolation(
  "Can not get property `${name.text}` from type $ty because it is not a struct or a module",
  loc,
)

class TypeIsNotPointer(ty: Ty, loc: Loc = GeneratedLoc) :
  AnalyzerViolation("Type $ty is not a pointer and can not be dereferenced", loc)

class TypeInfoCanNotBeDestructured(info: TyInfo, loc: Loc = GeneratedLoc) :
  AnalyzerViolation("Type $info is not a enum member and can not be destructured", loc)

class ScopeIsNotReturnable(scope: Scope, loc: Loc = GeneratedLoc) :
  AnalyzerViolation("Can not return in a not function scope `${scope.name.text}`", loc)

class Redeclaration(name: Identifier, loc: Loc = GeneratedLoc) :
  AnalyzerViolation("Redeclaration of `${name.text}`", loc)

class UnresolvedVariable(
  name: Identifier,
  module: Module? = null,
  loc: Loc = GeneratedLoc,
) : AnalyzerViolation(
  when (module) {
    null -> "Unresolved variable `${name.text}`"
    else -> "Unresolved variable `${name.text}` in ${module.name.text}"
  },
  loc,
)

class UnresolvedType(ty: Ty, loc: Loc = GeneratedLoc) :
  AnalyzerViolation("Unresolved type `$ty`", loc)

class UnresolvedTypeAccess(name: Identifier, loc: Loc = GeneratedLoc) :
  AnalyzerViolation("Unresolved type `${name.text}`", loc)

class UnresolvedModule(name: Identifier, loc: Loc = GeneratedLoc) :
  AnalyzerViolation("Unresolved module `${name.text}`", loc)

class CanNotReassignImmutableVariable(
  name: Identifier,
  loc: Loc = GeneratedLoc,
) : AnalyzerViolation("Can not reassign immutable variable `${name.text}`", loc)

class CanNotReassignImmutableStructMember(
  name: Identifier,
  info: TyInfo,
  loc: Loc = GeneratedLoc,
) :
  AnalyzerViolation("Can not reassign immutable property `${name.text}` of struct $info", loc)

class UnresolvedStructMember(
  name: Identifier,
  info: TyInfo,
  loc: Loc = GeneratedLoc,
) : AnalyzerViolation("Unresolved member `${name.text}` in struct `${info.name.text}`", loc)

class UnresolvedEnumVariant(
  name: Identifier,
  loc: Loc = GeneratedLoc,
) : AnalyzerViolation("Unresolved variant `${name.text}`", loc)

class IncorrectArity(expected: Int, got: Int, loc: Loc = GeneratedLoc) :
  AnalyzerViolation("Incorrect arity: expected $expected, got $got", loc)

class IncorrectEnumArity(
  expected: Int,
  got: Int,
  name: Identifier,
  loc: Loc = GeneratedLoc,
) : AnalyzerViolation(
  "Expecting $expected fields when matching `${name.text}`, got $got fields instead",
  loc
)
