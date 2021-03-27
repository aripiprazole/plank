// This is a generated file. Not intended for manual editing.
package com.lorenzoog.plank.intellijplugin.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.lorenzoog.jplank.intellijplugin.psi.PlankElementType;
import com.lorenzoog.jplank.intellijplugin.psi.PlankTokenType;
import com.lorenzoog.jplank.intellijplugin.psi.impl.*;
import com.lorenzoog.plank.intellijplugin.psi.impl.*;

public interface PlankTypes {

  IElementType ARGUMENT = new PlankElementType("ARGUMENT");
  IElementType ARGUMENTS = new PlankElementType("ARGUMENTS");
  IElementType ARRAY_TYPE_DEF = new PlankElementType("ARRAY_TYPE_DEF");
  IElementType ASSIGN = new PlankElementType("ASSIGN");
  IElementType ASSIGN_EXPR = new PlankElementType("ASSIGN_EXPR");
  IElementType CALLABLE_TYPE_DEF = new PlankElementType("CALLABLE_TYPE_DEF");
  IElementType CALL_EXPR = new PlankElementType("CALL_EXPR");
  IElementType CLASS_DECL = new PlankElementType("CLASS_DECL");
  IElementType COMPARISON = new PlankElementType("COMPARISON");
  IElementType DECL = new PlankElementType("DECL");
  IElementType ELSE_BRANCH = new PlankElementType("ELSE_BRANCH");
  IElementType EQUALITY = new PlankElementType("EQUALITY");
  IElementType EXPR = new PlankElementType("EXPR");
  IElementType EXPR_STMT = new PlankElementType("EXPR_STMT");
  IElementType FACTOR = new PlankElementType("FACTOR");
  IElementType FIELD = new PlankElementType("FIELD");
  IElementType FUN_DECL = new PlankElementType("FUN_DECL");
  IElementType FUN_HEADER = new PlankElementType("FUN_HEADER");
  IElementType GENERIC_ACCESS_TYPE_DEF = new PlankElementType("GENERIC_ACCESS_TYPE_DEF");
  IElementType GENERIC_TYPE_DEF = new PlankElementType("GENERIC_TYPE_DEF");
  IElementType GET = new PlankElementType("GET");
  IElementType IDENTIFIER_EXPR = new PlankElementType("IDENTIFIER_EXPR");
  IElementType IF_EXPR = new PlankElementType("IF_EXPR");
  IElementType IMPORTS = new PlankElementType("IMPORTS");
  IElementType IMPORT_DIRECTIVE = new PlankElementType("IMPORT_DIRECTIVE");
  IElementType INSTANCE_ARGUMENTS = new PlankElementType("INSTANCE_ARGUMENTS");
  IElementType LET_DECL = new PlankElementType("LET_DECL");
  IElementType NAME_TYPE_DEF = new PlankElementType("NAME_TYPE_DEF");
  IElementType NATIVE_FUN_DECL = new PlankElementType("NATIVE_FUN_DECL");
  IElementType PARAMETER = new PlankElementType("PARAMETER");
  IElementType POINTER = new PlankElementType("POINTER");
  IElementType POINTER_TYPE_DEF = new PlankElementType("POINTER_TYPE_DEF");
  IElementType PRIMARY = new PlankElementType("PRIMARY");
  IElementType RETURN_STMT = new PlankElementType("RETURN_STMT");
  IElementType SET = new PlankElementType("SET");
  IElementType SIZEOF_EXPR = new PlankElementType("SIZEOF_EXPR");
  IElementType STMT = new PlankElementType("STMT");
  IElementType TERM = new PlankElementType("TERM");
  IElementType THEN_BRANCH = new PlankElementType("THEN_BRANCH");
  IElementType TYPE_DEF = new PlankElementType("TYPE_DEF");
  IElementType UNARY = new PlankElementType("UNARY");

  IElementType AMPERSAND = new PlankTokenType("AMPERSAND");
  IElementType APHOSTROPHE = new PlankTokenType("APHOSTROPHE");
  IElementType ARROW_LEFT = new PlankTokenType("ARROW_LEFT");
  IElementType BANG = new PlankTokenType("BANG");
  IElementType BANG_EQUAL = new PlankTokenType("BANG_EQUAL");
  IElementType COLON = new PlankTokenType("COLON");
  IElementType COMMA = new PlankTokenType("COMMA");
  IElementType CONCAT = new PlankTokenType("CONCAT");
  IElementType DECIMAL = new PlankTokenType("DECIMAL");
  IElementType DOT = new PlankTokenType("DOT");
  IElementType ELSE = new PlankTokenType("ELSE");
  IElementType EOF = new PlankTokenType("EOF");
  IElementType EQUAL = new PlankTokenType("EQUAL");
  IElementType EQUAL_EQUAL = new PlankTokenType("EQUAL_EQUAL");
  IElementType FALSE = new PlankTokenType("FALSE");
  IElementType FUN = new PlankTokenType("FUN");
  IElementType GREATER = new PlankTokenType("GREATER");
  IElementType GREATER_EQUAL = new PlankTokenType("GREATER_EQUAL");
  IElementType IDENTIFIER = new PlankTokenType("IDENTIFIER");
  IElementType IF = new PlankTokenType("IF");
  IElementType IMPORT = new PlankTokenType("IMPORT");
  IElementType INT = new PlankTokenType("INT");
  IElementType LBRACE = new PlankTokenType("LBRACE");
  IElementType LBRACKET = new PlankTokenType("LBRACKET");
  IElementType LESS = new PlankTokenType("LESS");
  IElementType LESS_EQUAL = new PlankTokenType("LESS_EQUAL");
  IElementType LET = new PlankTokenType("LET");
  IElementType LPAREN = new PlankTokenType("LPAREN");
  IElementType MINUS = new PlankTokenType("MINUS");
  IElementType MUTABLE = new PlankTokenType("MUTABLE");
  IElementType NATIVE = new PlankTokenType("NATIVE");
  IElementType PLUS = new PlankTokenType("PLUS");
  IElementType RBRACE = new PlankTokenType("RBRACE");
  IElementType RBRACKET = new PlankTokenType("RBRACKET");
  IElementType RETURN = new PlankTokenType("RETURN");
  IElementType RPAREN = new PlankTokenType("RPAREN");
  IElementType SEMICOLON = new PlankTokenType("SEMICOLON");
  IElementType SIZEOF = new PlankTokenType("SIZEOF");
  IElementType SLASH = new PlankTokenType("SLASH");
  IElementType STAR = new PlankTokenType("STAR");
  IElementType STRING = new PlankTokenType("STRING");
  IElementType TRUE = new PlankTokenType("TRUE");
  IElementType TYPE = new PlankTokenType("TYPE");
  IElementType WS = new PlankTokenType("WS");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == ARGUMENT) {
        return new PlankArgumentImpl(node);
      }
      else if (type == ARGUMENTS) {
        return new PlankArgumentsImpl(node);
      }
      else if (type == ARRAY_TYPE_DEF) {
        return new PlankArrayTypeDefImpl(node);
      }
      else if (type == ASSIGN) {
        return new PlankAssignImpl(node);
      }
      else if (type == ASSIGN_EXPR) {
        return new PlankAssignExprImpl(node);
      }
      else if (type == CALLABLE_TYPE_DEF) {
        return new PlankCallableTypeDefImpl(node);
      }
      else if (type == CALL_EXPR) {
        return new PlankCallExprImpl(node);
      }
      else if (type == CLASS_DECL) {
        return new PlankClassDeclImpl(node);
      }
      else if (type == COMPARISON) {
        return new PlankComparisonImpl(node);
      }
      else if (type == DECL) {
        return new PlankDeclImpl(node);
      }
      else if (type == ELSE_BRANCH) {
        return new PlankElseBranchImpl(node);
      }
      else if (type == EQUALITY) {
        return new PlankEqualityImpl(node);
      }
      else if (type == EXPR) {
        return new PlankExprImpl(node);
      }
      else if (type == EXPR_STMT) {
        return new PlankExprStmtImpl(node);
      }
      else if (type == FACTOR) {
        return new PlankFactorImpl(node);
      }
      else if (type == FIELD) {
        return new PlankFieldImpl(node);
      }
      else if (type == FUN_DECL) {
        return new PlankFunDeclImpl(node);
      }
      else if (type == FUN_HEADER) {
        return new PlankFunHeaderImpl(node);
      }
      else if (type == GENERIC_ACCESS_TYPE_DEF) {
        return new PlankGenericAccessTypeDefImpl(node);
      }
      else if (type == GENERIC_TYPE_DEF) {
        return new PlankGenericTypeDefImpl(node);
      }
      else if (type == GET) {
        return new PlankGetImpl(node);
      }
      else if (type == IDENTIFIER_EXPR) {
        return new PlankIdentifierExprImpl(node);
      }
      else if (type == IF_EXPR) {
        return new PlankIfExprImpl(node);
      }
      else if (type == IMPORTS) {
        return new PlankImportsImpl(node);
      }
      else if (type == IMPORT_DIRECTIVE) {
        return new PlankImportDirectiveImpl(node);
      }
      else if (type == INSTANCE_ARGUMENTS) {
        return new PlankInstanceArgumentsImpl(node);
      }
      else if (type == LET_DECL) {
        return new PlankLetDeclImpl(node);
      }
      else if (type == NAME_TYPE_DEF) {
        return new PlankNameTypeDefImpl(node);
      }
      else if (type == NATIVE_FUN_DECL) {
        return new PlankNativeFunDeclImpl(node);
      }
      else if (type == PARAMETER) {
        return new PlankParameterImpl(node);
      }
      else if (type == POINTER) {
        return new PlankPointerImpl(node);
      }
      else if (type == POINTER_TYPE_DEF) {
        return new PlankPointerTypeDefImpl(node);
      }
      else if (type == PRIMARY) {
        return new PlankPrimaryImpl(node);
      }
      else if (type == RETURN_STMT) {
        return new PlankReturnStmtImpl(node);
      }
      else if (type == SET) {
        return new PlankSetImpl(node);
      }
      else if (type == SIZEOF_EXPR) {
        return new PlankSizeofExprImpl(node);
      }
      else if (type == STMT) {
        return new PlankStmtImpl(node);
      }
      else if (type == TERM) {
        return new PlankTermImpl(node);
      }
      else if (type == THEN_BRANCH) {
        return new PlankThenBranchImpl(node);
      }
      else if (type == TYPE_DEF) {
        return new PlankTypeDefImpl(node);
      }
      else if (type == UNARY) {
        return new PlankUnaryImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
