inline infix fun Boolean.implies(expression: () -> Boolean) = !this || expression()
