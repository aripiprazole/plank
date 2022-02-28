package org.plank.analyzer

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.plank.analyzer.infer.AppTy
import org.plank.analyzer.infer.ConstTy
import org.plank.analyzer.infer.FunTy
import org.plank.analyzer.infer.Infer
import org.plank.analyzer.infer.PtrTy
import org.plank.analyzer.infer.Scheme
import org.plank.analyzer.infer.Ty
import org.plank.analyzer.infer.TyEnv
import org.plank.analyzer.infer.TyError
import org.plank.analyzer.infer.VarTy
import org.plank.analyzer.infer.ap
import org.plank.analyzer.infer.arr
import org.plank.analyzer.infer.i16Ty
import org.plank.analyzer.infer.i32Ty
import org.plank.analyzer.infer.inferStmts
import org.plank.analyzer.infer.nullEnv
import org.plank.analyzer.infer.runInfer
import org.plank.analyzer.infer.strTy
import org.plank.analyzer.infer.unify
import org.plank.analyzer.infer.unitTy
import org.plank.syntax.element.AccessExpr
import org.plank.syntax.element.AccessTypeRef
import org.plank.syntax.element.AssignExpr
import org.plank.syntax.element.BlockExpr
import org.plank.syntax.element.CallExpr
import org.plank.syntax.element.ConstExpr
import org.plank.syntax.element.DerefExpr
import org.plank.syntax.element.EnumDecl
import org.plank.syntax.element.EnumVariantPattern
import org.plank.syntax.element.Expr
import org.plank.syntax.element.ExprBody
import org.plank.syntax.element.FunDecl
import org.plank.syntax.element.GenericTypeRef
import org.plank.syntax.element.GetExpr
import org.plank.syntax.element.GroupExpr
import org.plank.syntax.element.IdentPattern
import org.plank.syntax.element.IfExpr
import org.plank.syntax.element.InstanceExpr
import org.plank.syntax.element.LetDecl
import org.plank.syntax.element.MatchExpr
import org.plank.syntax.element.ModuleDecl
import org.plank.syntax.element.NoBody
import org.plank.syntax.element.PointerTypeRef
import org.plank.syntax.element.RefExpr
import org.plank.syntax.element.SetExpr
import org.plank.syntax.element.SizeofExpr
import org.plank.syntax.element.Stmt
import org.plank.syntax.element.ThenBranch
import org.plank.syntax.element.UnitTypeRef
import org.plank.syntax.element.toIdentifier
import org.plank.syntax.element.toQualifiedPath
import kotlin.test.assertEquals

class InferTest {
  @Test
  fun `test sizeof expr`() {
    expectTy(i32Ty, SizeofExpr(AccessTypeRef("foo".toQualifiedPath())))
  }

  @Test
  fun `test access expr`() {
    expectTy(i32Ty, AccessExpr("foo".toIdentifier())) {
      put("foo", Scheme(i32Ty))
    }
  }

  @Test
  fun `test get expr`() {
    expectTy(strTy, GetExpr(AccessExpr("person".toIdentifier()), "name".toIdentifier())) {
      val person = ConstTy("Person")

      put("person", Scheme(person))
      put("Person.name", Scheme(person arr strTy))
    }
  }

  @Test
  fun `test set expr`() {
    expectTy(
      strTy,
      SetExpr(AccessExpr("person".toIdentifier()), "name".toIdentifier(), ConstExpr("foo"))
    ) {
      val person = ConstTy("Person")

      put("person", Scheme(person))
      put("Person.name", Scheme(person arr strTy))
    }
  }

  @Test
  fun `test instance expr`() {
    expectTy(
      ConstTy("foo"),
      InstanceExpr(
        AccessTypeRef("foo".toQualifiedPath()),
        mapOf("name".toIdentifier() to ConstExpr(1))
      )
    )
  }

  @Test
  fun `test block expr`() {
    expectTy(i32Ty, BlockExpr(ConstExpr(10)))
  }

  @Test
  fun `test block without value`() {
    expectTy(unitTy, BlockExpr())
  }

  @Test
  fun `test assign expr`() {
    expectTy(i32Ty, AssignExpr("foo".toIdentifier(), ConstExpr(10))) {
      put("foo", Scheme(i32Ty))
    }
  }

  @Test
  fun `test ref expr`() {
    expectTy(PtrTy(i32Ty), RefExpr(AccessExpr("foo".toIdentifier()))) {
      put("foo", Scheme(i32Ty))
    }
  }

  @Test
  fun `test deref expr`() {
    expectTy(i32Ty, DerefExpr(AccessExpr("foo".toIdentifier()))) {
      put("foo", Scheme(PtrTy(i32Ty)))
    }
  }

  @Test
  fun `test deref expr fail with no pointer`() {
    val env = TyEnv {
      put("foo", Scheme(i32Ty))
    }

    expectFail(DerefExpr(AccessExpr("foo".toIdentifier())), env) {
      "Unable to unify Int32 and *'a"
    }
  }

  @Test
  fun `test group expr`() {
    expectTy(FunTy(i32Ty, i32Ty), GroupExpr(AccessExpr("foo".toIdentifier()))) {
      put("foo", Scheme(i32Ty arr i32Ty))
    }
  }

  @Test
  fun `test if expr`() {
    expectTy(i32Ty, IfExpr(ConstExpr(true), ThenBranch(ConstExpr(1)), ThenBranch(ConstExpr(2))))
  }

  @Test
  fun `test if expr fail with no bool in cond`() {
    expectFail(IfExpr(ConstExpr(10), ThenBranch(ConstExpr(1)), ThenBranch(ConstExpr(2)))) {
      "Unable to unify Int32 and Bool"
    }
  }

  @Test
  fun `test if expr fail with different types in thenBranch and elseBranch`() {
    expectFail(IfExpr(ConstExpr(10), ThenBranch(ConstExpr(1)), ThenBranch(ConstExpr(false)))) {
      "Unable to unify Int32 and Bool"
    }
  }

  @Test
  fun `test if expr with only thenBranch`() {
    expectTy(unitTy, IfExpr(ConstExpr(true), ThenBranch(ConstExpr(1)), null))
  }

  @Test
  fun `test match expr with named tuple pattern`() {
    expectEnv(
      EnumDecl(
        name = "Person".toIdentifier(),
        members = listOf(
          EnumDecl.Member(
            name = "MkPerson".toIdentifier(),
            PointerTypeRef(AccessTypeRef("Char".toQualifiedPath())),
          ),
        ),
      ),
      LetDecl(
        name = "foo".toIdentifier(),
        value = MatchExpr(
          subject = CallExpr(AccessExpr("MkPerson".toIdentifier()), ConstExpr("John")),
          patterns = mapOf(
            EnumVariantPattern(
              type = "MkPerson".toQualifiedPath(),
              IdentPattern("name".toIdentifier()),
            ) to AccessExpr("name".toIdentifier()),
          ),
        ),
      )
    ) { env ->
      expectSchemeEquals(env, strTy, env.lookup("foo"))
    }
  }

  @Test
  fun `test call expr`() {
    expectTy(i32Ty, CallExpr(AccessExpr("foo".toIdentifier()), listOf(ConstExpr(10)))) {
      put("foo", Scheme(i32Ty arr i32Ty))
    }
  }

  @Test
  fun `test call expr with scheme`() {
    expectTy(i32Ty, CallExpr(AccessExpr("foo".toIdentifier()), listOf(ConstExpr(10)))) {
      put("foo", Scheme(setOf("a"), VarTy("a") arr i32Ty))
    }
  }

  @Test
  fun `test call expr fail with incorrect argument types`() {
    val env = TyEnv {
      put("foo", Scheme(i16Ty arr i32Ty))
    }

    expectFail(CallExpr(AccessExpr("foo".toIdentifier()), listOf(ConstExpr(10))), env) {
      "Unable to unify Int16 and Int32"
    }
  }

  @Test
  fun `test let decl`() {
    expectEnv(LetDecl("foo".toIdentifier(), ConstExpr(10))) { env ->
      assertEquals(Scheme(i32Ty), env.lookup("foo"))
    }
  }

  @Test
  fun `test enum decl`() {
    expectEnv(
      EnumDecl(
        name = "Person".toIdentifier(),
        members = listOf(
          EnumDecl.Member(
            name = "MkPerson".toIdentifier(),
            PointerTypeRef(AccessTypeRef("Char".toQualifiedPath())),
          ),
        ),
      ),
      LetDecl(
        "foo".toIdentifier(),
        CallExpr(AccessExpr("MkPerson".toIdentifier()), ConstExpr("John"))
      )
    ) { env ->
      expectSchemeEquals(env, ConstTy("Person"), env.lookup("foo"))
    }
  }

  @Test
  fun `test enum decl with generics`() {
    expectEnv(
      EnumDecl(
        name = "Person".toIdentifier(),
        generics = setOf("a".toIdentifier()),
        members = listOf(
          EnumDecl.Member(
            name = "MkPerson".toIdentifier(),
            PointerTypeRef(AccessTypeRef("Char".toQualifiedPath())),
          ),
        ),
      ),
      LetDecl(
        "foo".toIdentifier(),
        CallExpr(AccessExpr("MkPerson".toIdentifier()), ConstExpr("John"))
      )
    ) { env ->
      expectSchemeEquals(env, AppTy(ConstTy("Person"), VarTy("a")), env.lookup("foo"))
    }
  }

  @Test
  fun `test fun decl`() {
    expectEnv(
      FunDecl(
        name = "foo".toIdentifier(),
        parameters = mapOf("x".toIdentifier() to AccessTypeRef("Int32".toQualifiedPath())),
        returnType = AccessTypeRef("Int32".toQualifiedPath()),
        body = ExprBody(ConstExpr(0)),
      ),
    ) { env ->
      expectSchemeEquals(env, i32Ty arr i32Ty, env.lookup("foo"))
    }
  }

  @Test
  fun `test module decl`() {
    expectEnv(
      ModuleDecl(
        path = "Foo".toQualifiedPath(),
        FunDecl(
          name = "bar".toIdentifier(),
          parameters = mapOf(),
          returnType = UnitTypeRef(),
          body = NoBody(),
        )
      ),
    ) { env ->
      expectSchemeEquals(env, unitTy arr unitTy, env.lookup("Foo.bar"))
    }
  }

  @Test
  fun `test fun decl generic`() {
    expectEnv(
      FunDecl(
        name = "foo".toIdentifier(),
        parameters = mapOf("x".toIdentifier() to GenericTypeRef("a".toIdentifier())),
        returnType = AccessTypeRef("Int32".toQualifiedPath()),
        body = ExprBody(ConstExpr(0)),
      ),
    ) { env ->
      expectSchemeEquals(env, VarTy("a") arr i32Ty, env.lookup("foo"))
    }
  }
}

fun expectEnv(vararg stmts: Stmt, env: TyEnv = nullEnv(), builder: Infer.(env: TyEnv) -> Unit) {
  with(Infer()) {
    builder(inferStmts(env, stmts.toList()))
  }
}

fun expectTy(expected: Ty, expr: Expr, env: TyEnv = nullEnv()) {
  assertEquals(expected, runInfer(expr, env).first)
}

fun expectTy(expected: Ty, expr: Expr, builder: MutableMap<String, Scheme>.() -> Unit) {
  assertEquals(expected, runInfer(expr, TyEnv(builder)).first)
}

fun expectFail(expr: Expr, env: TyEnv = nullEnv(), lazyMessage: () -> Any? = { null }) {
  assertThrows<TyError>(lazyMessage().toString()) {
    runInfer(expr, env)
  }
}

fun Infer.expectSchemeEquals(expected: Scheme, actual: Scheme?) {
  val t1 = instantiate(expected)
  val t2 = instantiate(requireNotNull(actual))
  val s = unify(t1, t2)

  assertEquals(t1 ap s, t2)
}

fun Infer.expectSchemeEquals(env: TyEnv, expected: Ty, actual: Scheme?) {
  val t1 = instantiate(env.generalize(expected))
  val t2 = instantiate(requireNotNull(actual))
  val s = unify(t1, t2)

  assertEquals(t1 ap s, t2)
}
