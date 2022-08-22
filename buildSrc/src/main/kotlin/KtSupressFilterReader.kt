package org.plank.build

import java.io.FilterReader
import java.io.Reader
import java.io.StringReader

class KtSuppressFilterReader(
  reader: Reader,
) : FilterReader(StringReader(SUPPRESS_ANNOT_HEADER + reader.readText())) {
  companion object {
    private val SUPPRESS_ANNOT_HEADER = arrayOf(
      "UNNECESSARY_NOT_NULL_ASSERTION",
      "UNUSED_PARAMETER",
      "USELESS_CAST",
      "UNUSED_VALUE",
      "VARIABLE_WITH_REDUNDANT_INITIALIZER",
      "PARAMETER_NAME_CHANGED_ON_OVERRIDE",
      "SENSELESS_COMPARISON",
      "UNCHECKED_CAST",
      "UNUSED",
      "RemoveRedundantQualifierName",
      "RedundantCompanionReference",
      "RedundantVisibilityModifier",
      "FunctionName",
      "SpellCheckingInspection",
      "RedundantExplicitType",
      "ConvertSecondaryConstructorToPrimary",
      "ConstantConditionIf",
      "CanBeVal",
      "LocalVariableName",
      "RemoveEmptySecondaryConstructorBody",
      "LiftReturnOrAssignment",
      "MemberVisibilityCanBePrivate",
      "RedundantNullableReturnType",
      "OverridingDeprecatedMember",
      "EnumEntryName",
      "RemoveExplicitTypeArguments",
      "PrivatePropertyName",
      "ProtectedInFinal",
      "MoveLambdaOutsideParentheses",
      "UnnecessaryImport",
      "KotlinRedundantDiagnosticSuppress",
      "ClassName",
      "CanBeParameter",
      "Detekt.MaximumLineLength",
      "Detekt.MaxLineLength",
      "Detekt.FinalNewline",
      "ktlint",
    ).joinToString(separator = ", ", prefix = "@file:Suppress(", postfix = ")\n\n") { "\"$it\"" }
  }
}
