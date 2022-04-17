package org.plank.analyzer.infer

import org.plank.syntax.element.Identifier

sealed class TyError(override val message: String) : Throwable()

class UnificationFail(val a: Ty, val b: Ty) : TyError("Unable to unify $a and $b")
class InfiniteTy(val variable: VarTy, val ty: Ty) : TyError("Infinite type $variable occurs in $ty")
class UnboundVar(val name: Identifier) : TyError("Unbound variable $name")
class LitNotSupported(val value: Any) : TyError("Literal $value not supported")
class CanNotUngeneralize(val ty: Ty) : TyError("Can not ungeneralize $ty")
class IsNotConstructor(val ty: Ty) : TyError("$ty is not a constructor")
class IsNotCallable(val ty: Ty) : TyError("$ty is not callable")
class IncorrectEnumArity(val arity: Int, val expected: Int, val variant: String) :
  TyError("Incorrect arity $arity for variant $variant")
