package org.plank.syntax.parsing

import org.plank.parser.PlankParser.AttrAccessExprContext
import org.plank.parser.PlankParser.AttrContext
import org.plank.parser.PlankParser.AttrDecimalExprContext
import org.plank.parser.PlankParser.AttrExprContext
import org.plank.parser.PlankParser.AttrFalseExprContext
import org.plank.parser.PlankParser.AttrIntExprContext
import org.plank.parser.PlankParser.AttrStringExprContext
import org.plank.parser.PlankParser.AttrTrueExprContext
import org.plank.syntax.element.AccessAttributeExpr
import org.plank.syntax.element.Attribute
import org.plank.syntax.element.AttributeExpr
import org.plank.syntax.element.BoolAttributeExpr
import org.plank.syntax.element.DecimalAttributeExpr
import org.plank.syntax.element.IntAttributeExpr
import org.plank.syntax.element.PlankFile
import org.plank.syntax.element.StringAttributeExpr

fun AttrContext.attrToAst(file: PlankFile): Attribute = Attribute(
  name = name!!.tokenToAst(file),
  arguments = findAttrExpr().map { it.attrExprToAst(file) },
  loc = treeLoc(file),
)

fun AttrExprContext.attrExprToAst(file: PlankFile): AttributeExpr<*> = when (this) {
  is AttrIntExprContext -> IntAttributeExpr(value!!.text!!.toInt(), treeLoc(file))
  is AttrDecimalExprContext -> DecimalAttributeExpr(value!!.text!!.toDouble(), treeLoc(file))
  is AttrAccessExprContext -> AccessAttributeExpr(value!!.tokenToAst(file), treeLoc(file))
  is AttrTrueExprContext -> BoolAttributeExpr(true, treeLoc(file))
  is AttrFalseExprContext -> BoolAttributeExpr(false, treeLoc(file))
  is AttrStringExprContext -> StringAttributeExpr(
    value = value!!.text!!.substring(1, value!!.text!!.length - 1),
    loc = treeLoc(file),
  )
  else -> error("Unsupported attr expr ${this::class.simpleName}")
}
