package org.plank.analyzer.infer

import org.plank.syntax.element.Identifier

sealed class TyError(override val message: String) : Throwable()

class UnificationFail(a: Ty, b: Ty) : TyError("Unable to unify $a and $b")
class InfiniteTy(variable: VarTy, ty: Ty) : TyError("Infinite type $variable occurs in $ty")
class UnboundVar(name: Identifier) : TyError("Unbound variable $name")
class LitNotSupported(value: Any) : TyError("Literal $value not supported")
class CanNotUngeneralize(ty: Ty) : TyError("Can not ungeneralize $ty")
class IsNotConstructor(ty: Ty) : TyError("$ty is not a constructor")
class IsNotCallable(ty: Ty) : TyError("$ty is not callable")
class IncorrectEnumArity(arity: Int, variant: String) :
  TyError("Incorrect arity $arity for variant $variant")
