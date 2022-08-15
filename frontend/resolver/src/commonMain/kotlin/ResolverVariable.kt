package org.plank.resolver

import org.plank.syntax.element.Identifier

data class ResolverVariable(val name: Identifier, val declaredIn: ResolverScope)

data class ResolverTy(val name: Identifier, val declaredIn: ResolverScope)
