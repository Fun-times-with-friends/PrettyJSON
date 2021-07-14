package src

import kotlinx.collections.immutable.persistentHashMapOf


sealed class PrettyElement{
    data class Field(val value: Expr): PrettyElement()
    data class Block(
        val bindings: List<Pair<String, PrettyElement>>,
        val properties: HashMap<String, PrettyElement>
    ) : PrettyElement()
}

sealed class Expr {
    data class Var(val name: String) : Expr()
    data class Lambda(val binder: String, val body: Expr) : Expr()
    data class Application(val func: Expr, val arg: Expr) : Expr()
    data class Number(val n: Int) : Expr()
    data class Str(val s: String) : Expr()
    data class Boolean(val b: kotlin.Boolean) : Expr()
    data class Binary(val operator: Operator, val x: Expr, val y: Expr) : Expr()
    data class If(val condition: Expr, val thenBranch: Expr, val elseBranch: Expr) : Expr()
    data class Let(val binder: String, val expr: Expr, val body: Expr) : Expr()
}

enum class Operator {
    Equals, Plus, Minus, Multiply
}