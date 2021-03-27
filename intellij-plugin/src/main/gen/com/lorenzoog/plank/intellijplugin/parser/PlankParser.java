// This is a generated file. Not intended for manual editing.
package com.lorenzoog.plank.intellijplugin.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;

import static com.intellij.lang.parser.GeneratedParserUtilBase.*;

import com.intellij.lang.parser.GeneratedParserUtilBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;
import com.lorenzoog.plank.intellijplugin.psi.PlankTypes;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class PlankParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return program(b, l + 1);
  }

  /* ********************************************************** */
  // IDENTIFIER COLON expr
  public static boolean argument(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "argument")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, PlankTypes.IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, PlankTypes.IDENTIFIER, PlankTypes.COLON);
    r = r && expr(b, l + 1);
    exit_section_(b, m, PlankTypes.ARGUMENT, r);
    return r;
  }

  /* ********************************************************** */
  // LPAREN ( expr ( COMMA expr ) * ) ? RPAREN
  public static boolean arguments(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arguments")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, PlankTypes.LPAREN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.LPAREN);
    r = r && arguments_1(b, l + 1);
    r = r && GeneratedParserUtilBase.consumeToken(b, PlankTypes.RPAREN);
    exit_section_(b, m, PlankTypes.ARGUMENTS, r);
    return r;
  }

  // ( expr ( COMMA expr ) * ) ?
  private static boolean arguments_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arguments_1")) return false;
    arguments_1_0(b, l + 1);
    return true;
  }

  // expr ( COMMA expr ) *
  private static boolean arguments_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arguments_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1);
    r = r && arguments_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( COMMA expr ) *
  private static boolean arguments_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arguments_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!arguments_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "arguments_1_0_1", c)) break;
    }
    return true;
  }

  // COMMA expr
  private static boolean arguments_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arguments_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.COMMA);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LBRACKET typeDef RBRACKET
  public static boolean arrayTypeDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arrayTypeDef")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, PlankTypes.LBRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.LBRACKET);
    r = r && typeDef(b, l + 1);
    r = r && GeneratedParserUtilBase.consumeToken(b, PlankTypes.RBRACKET);
    exit_section_(b, m, PlankTypes.ARRAY_TYPE_DEF, r);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER EQUAL assignExpr
  public static boolean assign(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assign")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, PlankTypes.IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, PlankTypes.IDENTIFIER, PlankTypes.EQUAL);
    r = r && assignExpr(b, l + 1);
    exit_section_(b, m, PlankTypes.ASSIGN, r);
    return r;
  }

  /* ********************************************************** */
  // assign | set | equality
  public static boolean assignExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assignExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PlankTypes.ASSIGN_EXPR, "<assign expr>");
    r = assign(b, l + 1);
    if (!r) r = set(b, l + 1);
    if (!r) r = equality(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // pointer ( instanceArguments | ( arguments | get ) *  )
  public static boolean callExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "callExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PlankTypes.CALL_EXPR, "<call expr>");
    r = pointer(b, l + 1);
    r = r && callExpr_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // instanceArguments | ( arguments | get ) *
  private static boolean callExpr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "callExpr_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = instanceArguments(b, l + 1);
    if (!r) r = callExpr_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( arguments | get ) *
  private static boolean callExpr_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "callExpr_1_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!callExpr_1_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "callExpr_1_1", c)) break;
    }
    return true;
  }

  // arguments | get
  private static boolean callExpr_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "callExpr_1_1_0")) return false;
    boolean r;
    r = arguments(b, l + 1);
    if (!r) r = get(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // LPAREN ( typeDef ( COMMA typeDef ) * ) ? RPAREN ARROW_LEFT typeDef
  public static boolean callableTypeDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "callableTypeDef")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, PlankTypes.LPAREN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.LPAREN);
    r = r && callableTypeDef_1(b, l + 1);
    r = r && consumeTokens(b, 0, PlankTypes.RPAREN, PlankTypes.ARROW_LEFT);
    r = r && typeDef(b, l + 1);
    exit_section_(b, m, PlankTypes.CALLABLE_TYPE_DEF, r);
    return r;
  }

  // ( typeDef ( COMMA typeDef ) * ) ?
  private static boolean callableTypeDef_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "callableTypeDef_1")) return false;
    callableTypeDef_1_0(b, l + 1);
    return true;
  }

  // typeDef ( COMMA typeDef ) *
  private static boolean callableTypeDef_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "callableTypeDef_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = typeDef(b, l + 1);
    r = r && callableTypeDef_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( COMMA typeDef ) *
  private static boolean callableTypeDef_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "callableTypeDef_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!callableTypeDef_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "callableTypeDef_1_0_1", c)) break;
    }
    return true;
  }

  // COMMA typeDef
  private static boolean callableTypeDef_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "callableTypeDef_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.COMMA);
    r = r && typeDef(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // TYPE IDENTIFIER EQUAL LBRACE ( field ( COMMA field ) * ) ? RBRACE SEMICOLON
  public static boolean classDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classDecl")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, PlankTypes.TYPE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, PlankTypes.TYPE, PlankTypes.IDENTIFIER, PlankTypes.EQUAL, PlankTypes.LBRACE);
    r = r && classDecl_4(b, l + 1);
    r = r && consumeTokens(b, 0, PlankTypes.RBRACE, PlankTypes.SEMICOLON);
    exit_section_(b, m, PlankTypes.CLASS_DECL, r);
    return r;
  }

  // ( field ( COMMA field ) * ) ?
  private static boolean classDecl_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classDecl_4")) return false;
    classDecl_4_0(b, l + 1);
    return true;
  }

  // field ( COMMA field ) *
  private static boolean classDecl_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classDecl_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = field(b, l + 1);
    r = r && classDecl_4_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( COMMA field ) *
  private static boolean classDecl_4_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classDecl_4_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!classDecl_4_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "classDecl_4_0_1", c)) break;
    }
    return true;
  }

  // COMMA field
  private static boolean classDecl_4_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classDecl_4_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.COMMA);
    r = r && field(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // term ( ( GREATER | GREATER_EQUAL | LESS | LESS_EQUAL ) term ) *
  public static boolean comparison(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comparison")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PlankTypes.COMPARISON, "<comparison>");
    r = term(b, l + 1);
    r = r && comparison_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ( GREATER | GREATER_EQUAL | LESS | LESS_EQUAL ) term ) *
  private static boolean comparison_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comparison_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!comparison_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "comparison_1", c)) break;
    }
    return true;
  }

  // ( GREATER | GREATER_EQUAL | LESS | LESS_EQUAL ) term
  private static boolean comparison_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comparison_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = comparison_1_0_0(b, l + 1);
    r = r && term(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // GREATER | GREATER_EQUAL | LESS | LESS_EQUAL
  private static boolean comparison_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comparison_1_0_0")) return false;
    boolean r;
    r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.GREATER);
    if (!r) r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.GREATER_EQUAL);
    if (!r) r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.LESS);
    if (!r) r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.LESS_EQUAL);
    return r;
  }

  /* ********************************************************** */
  // letDecl WS *
  //        | funDecl WS *
  //        | nativeFunDecl WS *
  //        | classDecl WS *
  public static boolean decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decl")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PlankTypes.DECL, "<decl>");
    r = decl_0(b, l + 1);
    if (!r) r = decl_1(b, l + 1);
    if (!r) r = decl_2(b, l + 1);
    if (!r) r = decl_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // letDecl WS *
  private static boolean decl_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decl_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = letDecl(b, l + 1);
    r = r && decl_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // WS *
  private static boolean decl_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decl_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!GeneratedParserUtilBase.consumeToken(b, PlankTypes.WS)) break;
      if (!empty_element_parsed_guard_(b, "decl_0_1", c)) break;
    }
    return true;
  }

  // funDecl WS *
  private static boolean decl_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decl_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = funDecl(b, l + 1);
    r = r && decl_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // WS *
  private static boolean decl_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decl_1_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!GeneratedParserUtilBase.consumeToken(b, PlankTypes.WS)) break;
      if (!empty_element_parsed_guard_(b, "decl_1_1", c)) break;
    }
    return true;
  }

  // nativeFunDecl WS *
  private static boolean decl_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decl_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = nativeFunDecl(b, l + 1);
    r = r && decl_2_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // WS *
  private static boolean decl_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decl_2_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!GeneratedParserUtilBase.consumeToken(b, PlankTypes.WS)) break;
      if (!empty_element_parsed_guard_(b, "decl_2_1", c)) break;
    }
    return true;
  }

  // classDecl WS *
  private static boolean decl_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decl_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = classDecl(b, l + 1);
    r = r && decl_3_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // WS *
  private static boolean decl_3_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decl_3_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!GeneratedParserUtilBase.consumeToken(b, PlankTypes.WS)) break;
      if (!empty_element_parsed_guard_(b, "decl_3_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // ELSE expr
  //              | ELSE LBRACE ( stmt * ) ? RBRACE
  public static boolean elseBranch(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "elseBranch")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, PlankTypes.ELSE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = elseBranch_0(b, l + 1);
    if (!r) r = elseBranch_1(b, l + 1);
    exit_section_(b, m, PlankTypes.ELSE_BRANCH, r);
    return r;
  }

  // ELSE expr
  private static boolean elseBranch_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "elseBranch_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.ELSE);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ELSE LBRACE ( stmt * ) ? RBRACE
  private static boolean elseBranch_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "elseBranch_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, PlankTypes.ELSE, PlankTypes.LBRACE);
    r = r && elseBranch_1_2(b, l + 1);
    r = r && GeneratedParserUtilBase.consumeToken(b, PlankTypes.RBRACE);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( stmt * ) ?
  private static boolean elseBranch_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "elseBranch_1_2")) return false;
    elseBranch_1_2_0(b, l + 1);
    return true;
  }

  // stmt *
  private static boolean elseBranch_1_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "elseBranch_1_2_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!stmt(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "elseBranch_1_2_0", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // comparison ( ( EQUAL_EQUAL | BANG_EQUAL ) comparison ) *
  public static boolean equality(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "equality")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PlankTypes.EQUALITY, "<equality>");
    r = comparison(b, l + 1);
    r = r && equality_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ( EQUAL_EQUAL | BANG_EQUAL ) comparison ) *
  private static boolean equality_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "equality_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!equality_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "equality_1", c)) break;
    }
    return true;
  }

  // ( EQUAL_EQUAL | BANG_EQUAL ) comparison
  private static boolean equality_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "equality_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = equality_1_0_0(b, l + 1);
    r = r && comparison(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EQUAL_EQUAL | BANG_EQUAL
  private static boolean equality_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "equality_1_0_0")) return false;
    boolean r;
    r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.EQUAL_EQUAL);
    if (!r) r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.BANG_EQUAL);
    return r;
  }

  /* ********************************************************** */
  // assignExpr
  //        | ifExpr
  //        | sizeofExpr
  public static boolean expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PlankTypes.EXPR, "<expr>");
    r = assignExpr(b, l + 1);
    if (!r) r = ifExpr(b, l + 1);
    if (!r) r = sizeofExpr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // expr SEMICOLON
  public static boolean exprStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exprStmt")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PlankTypes.EXPR_STMT, "<expr stmt>");
    r = expr(b, l + 1);
    r = r && GeneratedParserUtilBase.consumeToken(b, PlankTypes.SEMICOLON);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // unary ( ( STAR | SLASH ) unary ) *
  public static boolean factor(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "factor")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PlankTypes.FACTOR, "<factor>");
    r = unary(b, l + 1);
    r = r && factor_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ( STAR | SLASH ) unary ) *
  private static boolean factor_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "factor_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!factor_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "factor_1", c)) break;
    }
    return true;
  }

  // ( STAR | SLASH ) unary
  private static boolean factor_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "factor_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = factor_1_0_0(b, l + 1);
    r = r && unary(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // STAR | SLASH
  private static boolean factor_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "factor_1_0_0")) return false;
    boolean r;
    r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.STAR);
    if (!r) r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.SLASH);
    return r;
  }

  /* ********************************************************** */
  // MUTABLE ? parameter
  public static boolean field(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field")) return false;
    if (!nextTokenIs(b, "<field>", PlankTypes.IDENTIFIER, PlankTypes.MUTABLE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PlankTypes.FIELD, "<field>");
    r = field_0(b, l + 1);
    r = r && parameter(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // MUTABLE ?
  private static boolean field_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_0")) return false;
    GeneratedParserUtilBase.consumeToken(b, PlankTypes.MUTABLE);
    return true;
  }

  /* ********************************************************** */
  // funHeader LBRACE stmt * RBRACE
  public static boolean funDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "funDecl")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, PlankTypes.FUN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = funHeader(b, l + 1);
    r = r && GeneratedParserUtilBase.consumeToken(b, PlankTypes.LBRACE);
    r = r && funDecl_2(b, l + 1);
    r = r && GeneratedParserUtilBase.consumeToken(b, PlankTypes.RBRACE);
    exit_section_(b, m, PlankTypes.FUN_DECL, r);
    return r;
  }

  // stmt *
  private static boolean funDecl_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "funDecl_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!stmt(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "funDecl_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // FUN IDENTIFIER LPAREN ( parameter ( COMMA parameter ) * ) ? RPAREN COLON typeDef
  public static boolean funHeader(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "funHeader")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, PlankTypes.FUN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, PlankTypes.FUN, PlankTypes.IDENTIFIER, PlankTypes.LPAREN);
    r = r && funHeader_3(b, l + 1);
    r = r && consumeTokens(b, 0, PlankTypes.RPAREN, PlankTypes.COLON);
    r = r && typeDef(b, l + 1);
    exit_section_(b, m, PlankTypes.FUN_HEADER, r);
    return r;
  }

  // ( parameter ( COMMA parameter ) * ) ?
  private static boolean funHeader_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "funHeader_3")) return false;
    funHeader_3_0(b, l + 1);
    return true;
  }

  // parameter ( COMMA parameter ) *
  private static boolean funHeader_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "funHeader_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parameter(b, l + 1);
    r = r && funHeader_3_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( COMMA parameter ) *
  private static boolean funHeader_3_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "funHeader_3_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!funHeader_3_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "funHeader_3_0_1", c)) break;
    }
    return true;
  }

  // COMMA parameter
  private static boolean funHeader_3_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "funHeader_3_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.COMMA);
    r = r && parameter(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // APHOSTROPHE IDENTIFIER
  public static boolean genericAccessTypeDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "genericAccessTypeDef")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, PlankTypes.APHOSTROPHE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, PlankTypes.APHOSTROPHE, PlankTypes.IDENTIFIER);
    exit_section_(b, m, PlankTypes.GENERIC_ACCESS_TYPE_DEF, r);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER LESS ( typeDef ( COMMA typeDef ) * ) ? GREATER
  public static boolean genericTypeDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "genericTypeDef")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, PlankTypes.IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, PlankTypes.IDENTIFIER, PlankTypes.LESS);
    r = r && genericTypeDef_2(b, l + 1);
    r = r && GeneratedParserUtilBase.consumeToken(b, PlankTypes.GREATER);
    exit_section_(b, m, PlankTypes.GENERIC_TYPE_DEF, r);
    return r;
  }

  // ( typeDef ( COMMA typeDef ) * ) ?
  private static boolean genericTypeDef_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "genericTypeDef_2")) return false;
    genericTypeDef_2_0(b, l + 1);
    return true;
  }

  // typeDef ( COMMA typeDef ) *
  private static boolean genericTypeDef_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "genericTypeDef_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = typeDef(b, l + 1);
    r = r && genericTypeDef_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( COMMA typeDef ) *
  private static boolean genericTypeDef_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "genericTypeDef_2_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!genericTypeDef_2_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "genericTypeDef_2_0_1", c)) break;
    }
    return true;
  }

  // COMMA typeDef
  private static boolean genericTypeDef_2_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "genericTypeDef_2_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.COMMA);
    r = r && typeDef(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // DOT IDENTIFIER
  public static boolean get(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "get")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, PlankTypes.DOT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, PlankTypes.DOT, PlankTypes.IDENTIFIER);
    exit_section_(b, m, PlankTypes.GET, r);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER
  public static boolean identifierExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "identifierExpr")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, PlankTypes.IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.IDENTIFIER);
    exit_section_(b, m, PlankTypes.IDENTIFIER_EXPR, r);
    return r;
  }

  /* ********************************************************** */
  // IF LPAREN expr RPAREN thenBranch elseBranch?
  public static boolean ifExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ifExpr")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, PlankTypes.IF)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, PlankTypes.IF, PlankTypes.LPAREN);
    r = r && expr(b, l + 1);
    r = r && GeneratedParserUtilBase.consumeToken(b, PlankTypes.RPAREN);
    r = r && thenBranch(b, l + 1);
    r = r && ifExpr_5(b, l + 1);
    exit_section_(b, m, PlankTypes.IF_EXPR, r);
    return r;
  }

  // elseBranch?
  private static boolean ifExpr_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ifExpr_5")) return false;
    elseBranch(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // IMPORT IDENTIFIER SEMICOLON
  public static boolean importDirective(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importDirective")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, PlankTypes.IMPORT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, PlankTypes.IMPORT, PlankTypes.IDENTIFIER, PlankTypes.SEMICOLON);
    exit_section_(b, m, PlankTypes.IMPORT_DIRECTIVE, r);
    return r;
  }

  /* ********************************************************** */
  // importDirective *
  public static boolean imports(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "imports")) return false;
    Marker m = enter_section_(b, l, _NONE_, PlankTypes.IMPORTS, "<imports>");
    while (true) {
      int c = current_position_(b);
      if (!importDirective(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "imports", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // LBRACE ( argument ( COMMA argument ) * ) ? RBRACE
  public static boolean instanceArguments(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "instanceArguments")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, PlankTypes.LBRACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.LBRACE);
    r = r && instanceArguments_1(b, l + 1);
    r = r && GeneratedParserUtilBase.consumeToken(b, PlankTypes.RBRACE);
    exit_section_(b, m, PlankTypes.INSTANCE_ARGUMENTS, r);
    return r;
  }

  // ( argument ( COMMA argument ) * ) ?
  private static boolean instanceArguments_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "instanceArguments_1")) return false;
    instanceArguments_1_0(b, l + 1);
    return true;
  }

  // argument ( COMMA argument ) *
  private static boolean instanceArguments_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "instanceArguments_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = argument(b, l + 1);
    r = r && instanceArguments_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( COMMA argument ) *
  private static boolean instanceArguments_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "instanceArguments_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!instanceArguments_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "instanceArguments_1_0_1", c)) break;
    }
    return true;
  }

  // COMMA argument
  private static boolean instanceArguments_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "instanceArguments_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.COMMA);
    r = r && argument(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LET MUTABLE? IDENTIFIER EQUAL expr SEMICOLON
  //           | LET MUTABLE? IDENTIFIER COLON typeDef EQUAL expr SEMICOLON
  public static boolean letDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "letDecl")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, PlankTypes.LET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = letDecl_0(b, l + 1);
    if (!r) r = letDecl_1(b, l + 1);
    exit_section_(b, m, PlankTypes.LET_DECL, r);
    return r;
  }

  // LET MUTABLE? IDENTIFIER EQUAL expr SEMICOLON
  private static boolean letDecl_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "letDecl_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.LET);
    r = r && letDecl_0_1(b, l + 1);
    r = r && consumeTokens(b, 0, PlankTypes.IDENTIFIER, PlankTypes.EQUAL);
    r = r && expr(b, l + 1);
    r = r && GeneratedParserUtilBase.consumeToken(b, PlankTypes.SEMICOLON);
    exit_section_(b, m, null, r);
    return r;
  }

  // MUTABLE?
  private static boolean letDecl_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "letDecl_0_1")) return false;
    GeneratedParserUtilBase.consumeToken(b, PlankTypes.MUTABLE);
    return true;
  }

  // LET MUTABLE? IDENTIFIER COLON typeDef EQUAL expr SEMICOLON
  private static boolean letDecl_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "letDecl_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.LET);
    r = r && letDecl_1_1(b, l + 1);
    r = r && consumeTokens(b, 0, PlankTypes.IDENTIFIER, PlankTypes.COLON);
    r = r && typeDef(b, l + 1);
    r = r && GeneratedParserUtilBase.consumeToken(b, PlankTypes.EQUAL);
    r = r && expr(b, l + 1);
    r = r && GeneratedParserUtilBase.consumeToken(b, PlankTypes.SEMICOLON);
    exit_section_(b, m, null, r);
    return r;
  }

  // MUTABLE?
  private static boolean letDecl_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "letDecl_1_1")) return false;
    GeneratedParserUtilBase.consumeToken(b, PlankTypes.MUTABLE);
    return true;
  }

  /* ********************************************************** */
  // IDENTIFIER
  public static boolean nameTypeDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nameTypeDef")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, PlankTypes.IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.IDENTIFIER);
    exit_section_(b, m, PlankTypes.NAME_TYPE_DEF, r);
    return r;
  }

  /* ********************************************************** */
  // NATIVE funHeader SEMICOLON
  public static boolean nativeFunDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nativeFunDecl")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, PlankTypes.NATIVE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.NATIVE);
    r = r && funHeader(b, l + 1);
    r = r && GeneratedParserUtilBase.consumeToken(b, PlankTypes.SEMICOLON);
    exit_section_(b, m, PlankTypes.NATIVE_FUN_DECL, r);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER COLON typeDef
  public static boolean parameter(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, PlankTypes.IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, PlankTypes.IDENTIFIER, PlankTypes.COLON);
    r = r && typeDef(b, l + 1);
    exit_section_(b, m, PlankTypes.PARAMETER, r);
    return r;
  }

  /* ********************************************************** */
  // STAR expr
  //           | AMPERSAND expr
  //           | primary
  public static boolean pointer(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pointer")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PlankTypes.POINTER, "<pointer>");
    r = pointer_0(b, l + 1);
    if (!r) r = pointer_1(b, l + 1);
    if (!r) r = primary(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // STAR expr
  private static boolean pointer_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pointer_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.STAR);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // AMPERSAND expr
  private static boolean pointer_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pointer_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.AMPERSAND);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // STAR typeDef
  public static boolean pointerTypeDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pointerTypeDef")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, PlankTypes.STAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.STAR);
    r = r && typeDef(b, l + 1);
    exit_section_(b, m, PlankTypes.POINTER_TYPE_DEF, r);
    return r;
  }

  /* ********************************************************** */
  // STRING
  //           | INT
  //           | DECIMAL
  //           | TRUE
  //           | FALSE
  //           | LPAREN expr RPAREN
  //           | identifierExpr
  public static boolean primary(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primary")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PlankTypes.PRIMARY, "<primary>");
    r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.STRING);
    if (!r) r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.INT);
    if (!r) r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.DECIMAL);
    if (!r) r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.TRUE);
    if (!r) r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.FALSE);
    if (!r) r = primary_5(b, l + 1);
    if (!r) r = identifierExpr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // LPAREN expr RPAREN
  private static boolean primary_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primary_5")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.LPAREN);
    r = r && expr(b, l + 1);
    r = r && GeneratedParserUtilBase.consumeToken(b, PlankTypes.RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // imports ? decl * EOF
  static boolean program(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "program")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = program_0(b, l + 1);
    r = r && program_1(b, l + 1);
    r = r && GeneratedParserUtilBase.consumeToken(b, PlankTypes.EOF);
    exit_section_(b, m, null, r);
    return r;
  }

  // imports ?
  private static boolean program_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "program_0")) return false;
    imports(b, l + 1);
    return true;
  }

  // decl *
  private static boolean program_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "program_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!decl(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "program_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // RETURN expr ? SEMICOLON
  public static boolean returnStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "returnStmt")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, PlankTypes.RETURN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.RETURN);
    r = r && returnStmt_1(b, l + 1);
    r = r && GeneratedParserUtilBase.consumeToken(b, PlankTypes.SEMICOLON);
    exit_section_(b, m, PlankTypes.RETURN_STMT, r);
    return r;
  }

  // expr ?
  private static boolean returnStmt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "returnStmt_1")) return false;
    expr(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ( callExpr DOT ) ? IDENTIFIER EQUAL assignExpr
  public static boolean set(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "set")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PlankTypes.SET, "<set>");
    r = set_0(b, l + 1);
    r = r && consumeTokens(b, 0, PlankTypes.IDENTIFIER, PlankTypes.EQUAL);
    r = r && assignExpr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( callExpr DOT ) ?
  private static boolean set_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "set_0")) return false;
    set_0_0(b, l + 1);
    return true;
  }

  // callExpr DOT
  private static boolean set_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "set_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = callExpr(b, l + 1);
    r = r && GeneratedParserUtilBase.consumeToken(b, PlankTypes.DOT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // SIZEOF typeDef
  public static boolean sizeofExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sizeofExpr")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, PlankTypes.SIZEOF)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.SIZEOF);
    r = r && typeDef(b, l + 1);
    exit_section_(b, m, PlankTypes.SIZEOF_EXPR, r);
    return r;
  }

  /* ********************************************************** */
  // decl
  //        | ifExpr WS *
  //        | exprStmt WS *
  //        | returnStmt WS *
  public static boolean stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stmt")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PlankTypes.STMT, "<stmt>");
    r = decl(b, l + 1);
    if (!r) r = stmt_1(b, l + 1);
    if (!r) r = stmt_2(b, l + 1);
    if (!r) r = stmt_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ifExpr WS *
  private static boolean stmt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stmt_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ifExpr(b, l + 1);
    r = r && stmt_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // WS *
  private static boolean stmt_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stmt_1_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!GeneratedParserUtilBase.consumeToken(b, PlankTypes.WS)) break;
      if (!empty_element_parsed_guard_(b, "stmt_1_1", c)) break;
    }
    return true;
  }

  // exprStmt WS *
  private static boolean stmt_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stmt_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = exprStmt(b, l + 1);
    r = r && stmt_2_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // WS *
  private static boolean stmt_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stmt_2_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!GeneratedParserUtilBase.consumeToken(b, PlankTypes.WS)) break;
      if (!empty_element_parsed_guard_(b, "stmt_2_1", c)) break;
    }
    return true;
  }

  // returnStmt WS *
  private static boolean stmt_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stmt_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = returnStmt(b, l + 1);
    r = r && stmt_3_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // WS *
  private static boolean stmt_3_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stmt_3_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!GeneratedParserUtilBase.consumeToken(b, PlankTypes.WS)) break;
      if (!empty_element_parsed_guard_(b, "stmt_3_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // factor ( ( MINUS | PLUS | CONCAT ) factor ) *
  public static boolean term(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "term")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PlankTypes.TERM, "<term>");
    r = factor(b, l + 1);
    r = r && term_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ( MINUS | PLUS | CONCAT ) factor ) *
  private static boolean term_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "term_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!term_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "term_1", c)) break;
    }
    return true;
  }

  // ( MINUS | PLUS | CONCAT ) factor
  private static boolean term_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "term_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = term_1_0_0(b, l + 1);
    r = r && factor(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // MINUS | PLUS | CONCAT
  private static boolean term_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "term_1_0_0")) return false;
    boolean r;
    r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.MINUS);
    if (!r) r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.PLUS);
    if (!r) r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.CONCAT);
    return r;
  }

  /* ********************************************************** */
  // LBRACE ( stmt * ) ? RBRACE
  public static boolean thenBranch(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "thenBranch")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, PlankTypes.LBRACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.LBRACE);
    r = r && thenBranch_1(b, l + 1);
    r = r && GeneratedParserUtilBase.consumeToken(b, PlankTypes.RBRACE);
    exit_section_(b, m, PlankTypes.THEN_BRANCH, r);
    return r;
  }

  // ( stmt * ) ?
  private static boolean thenBranch_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "thenBranch_1")) return false;
    thenBranch_1_0(b, l + 1);
    return true;
  }

  // stmt *
  private static boolean thenBranch_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "thenBranch_1_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!stmt(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "thenBranch_1_0", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // nameTypeDef
  //           | callableTypeDef
  //           | arrayTypeDef
  //           | genericAccessTypeDef
  //           | genericTypeDef
  //           | pointerTypeDef
  public static boolean typeDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typeDef")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PlankTypes.TYPE_DEF, "<type def>");
    r = nameTypeDef(b, l + 1);
    if (!r) r = callableTypeDef(b, l + 1);
    if (!r) r = arrayTypeDef(b, l + 1);
    if (!r) r = genericAccessTypeDef(b, l + 1);
    if (!r) r = genericTypeDef(b, l + 1);
    if (!r) r = pointerTypeDef(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ( BANG | MINUS ) unary | callExpr
  public static boolean unary(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unary")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, PlankTypes.UNARY, "<unary>");
    r = unary_0(b, l + 1);
    if (!r) r = callExpr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( BANG | MINUS ) unary
  private static boolean unary_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unary_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = unary_0_0(b, l + 1);
    r = r && unary(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // BANG | MINUS
  private static boolean unary_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unary_0_0")) return false;
    boolean r;
    r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.BANG);
    if (!r) r = GeneratedParserUtilBase.consumeToken(b, PlankTypes.MINUS);
    return r;
  }

}
