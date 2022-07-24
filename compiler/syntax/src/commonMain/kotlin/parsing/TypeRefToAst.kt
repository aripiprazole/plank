package org.plank.syntax.parsing

import org.plank.parser.PlankParser.AccessTypeRefContext
import org.plank.parser.PlankParser.ApplyTypeRefContext
import org.plank.parser.PlankParser.FunctionTypeRefContext
import org.plank.parser.PlankParser.GroupTypeRefContext
import org.plank.parser.PlankParser.PointerTypeRefContext
import org.plank.parser.PlankParser.PrimaryTypeRefContext
import org.plank.parser.PlankParser.TypePrimaryContext
import org.plank.parser.PlankParser.TypeRefContext
import org.plank.parser.PlankParser.UnitTypeRefContext
import org.plank.syntax.element.AccessTypeRef
import org.plank.syntax.element.ApplyTypeRef
import org.plank.syntax.element.FunctionTypeRef
import org.plank.syntax.element.PlankFile
import org.plank.syntax.element.PointerTypeRef
import org.plank.syntax.element.TypeRef
import org.plank.syntax.element.UnitTypeRef

fun TypePrimaryContext.typeRefToAst(file: PlankFile): TypeRef = when (this) {
  is GroupTypeRefContext -> type!!.typeRefToAst(file)
  is UnitTypeRefContext -> UnitTypeRef(treeLoc(file))
  is PointerTypeRefContext -> PointerTypeRef(type!!.typeRefToAst(file), treeLoc(file))
  is AccessTypeRefContext -> AccessTypeRef(path!!.pathToAst(file), treeLoc(file))
  is ApplyTypeRefContext -> {
    val arguments = findTypeRef().map { it.typeRefToAst(file) }
    val path = path!!.pathToAst(file)

    ApplyTypeRef(AccessTypeRef(path, path.loc), arguments, treeLoc(file))
  }

  else -> error("Unsupported primary type ref ${this::class.simpleName}")
}

fun TypeRefContext.typeRefToAst(file: PlankFile): TypeRef = when (this) {
  is PrimaryTypeRefContext -> value!!.typeRefToAst(file)
  is FunctionTypeRefContext -> {
    FunctionTypeRef(
      parameterType = parameter!!.typeRefToAst(file),
      returnType!!.typeRefToAst(file),
      treeLoc(file),
    )
  }

  else -> error("Unsupported type ref ${this::class.simpleName}")
}
