package org.plank.analyzer

sealed interface TypeInfo

object FunctionInfo : TypeInfo

object StructInfo : TypeInfo

object StructMemberInfo : TypeInfo

object EnumInfo : TypeInfo

object EnumMemberInfo : TypeInfo
