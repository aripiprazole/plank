package org.plank.analyzer

import org.plank.analyzer.infer.Module
import org.plank.analyzer.infer.Scope
import org.plank.analyzer.infer.Ty
import org.plank.analyzer.infer.TyInfo
import org.plank.analyzer.infer.VarTy
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.Location
import org.plank.syntax.message.CompilerLogger
import kotlin.reflect.KClass

sealed class AnalyzerViolation(val message: String, var location: Location) {
  fun render(logger: CompilerLogger) {
    logger.severe(message, location)
  }

  fun withLocation(location: Location): AnalyzerViolation {
    this.location = location
    return this
  }

  override fun toString(): String = message
}

class UnsupportedConstType(type: KClass<*>, location: Location = Location.Generated) :
  AnalyzerViolation("Unsupported const type $type", location)

class TypeMismatch(expected: Ty, got: Ty, location: Location = Location.Generated) :
  AnalyzerViolation("Type mismatch: expected $expected, got $got", location)

class TypeIsInfinite(name: VarTy, ty: Ty, location: Location = Location.Generated) :
  AnalyzerViolation("Type $ty is infinite", location)

class TypeIsNotCallable(ty: Ty, location: Location = Location.Generated) :
  AnalyzerViolation("Type $ty is not callable", location)

class TypeIsNotStruct(ty: Ty, location: Location = Location.Generated) :
  AnalyzerViolation("Type $ty is not a struct and can not be instantiated", location)

class TypeIsNotStructAndCanNotGet(
  name: Identifier,
  ty: Ty,
  location: Location = Location.Generated,
) : AnalyzerViolation(
  "Can not get property `${name.text}` from type $ty because it is not a struct or a module",
  location
)

class TypeIsNotPointer(ty: Ty, location: Location = Location.Generated) :
  AnalyzerViolation("Type $ty is not a pointer and can not be dereferenced", location)

class TypeInfoCanNotBeDestructured(info: TyInfo, location: Location = Location.Generated) :
  AnalyzerViolation("Type $info is not a enum member and can not be destructured", location)

class ScopeIsNotReturnable(scope: Scope, location: Location = Location.Generated) :
  AnalyzerViolation("Can not return in a not function scope `${scope.name.text}`", location)

class UnresolvedVariable(
  name: Identifier,
  module: Module? = null,
  location: Location = Location.Generated,
) : AnalyzerViolation(
  when (module) {
    null -> "Unresolved variable `${name.text}`"
    else -> "Unresolved variable `${name.text}` in ${module.name.text}"
  },
  location,
)

class UnresolvedType(ty: Ty, location: Location = Location.Generated) :
  AnalyzerViolation("Unresolved type `$ty`", location)

class UnresolvedTypeAccess(name: Identifier, location: Location = Location.Generated) :
  AnalyzerViolation("Unresolved type `${name.text}`", location)

class UnresolvedModule(name: Identifier, location: Location = Location.Generated) :
  AnalyzerViolation("Unresolved module `${name.text}`", location)

class CanNotReassignImmutableVariable(
  name: Identifier,
  location: Location = Location.Generated,
) : AnalyzerViolation("Can not reassign immutable variable `${name.text}`", location)

class CanNotReassignImmutableStructMember(
  name: Identifier,
  info: TyInfo,
  location: Location = Location.Generated,
) : AnalyzerViolation("Can not reassign immutable property `${name.text}` of struct $info", location)

class UnresolvedStructMember(
  name: Identifier,
  info: TyInfo,
  location: Location = Location.Generated,
) : AnalyzerViolation("Unresolved member `${name.text}` in struct `${info.name.text}`", location)

class UnresolvedEnumVariant(
  name: Identifier,
  info: TyInfo,
  location: Location = Location.Generated,
) : AnalyzerViolation("Unresolved variant `${name.text}` of enum `${info.name.text}`", location)

class IncorrectArity(expected: Int, got: Int, location: Location = Location.Generated) :
  AnalyzerViolation("Incorrect arity: expected $expected, got $got", location)

class IncorrectEnumArity(
  expected: Int,
  got: Int,
  name: Identifier,
  location: Location = Location.Generated,
) : AnalyzerViolation(
  "Expecting $expected fields when matching `${name.text}`, got $got fields instead",
  location
)
