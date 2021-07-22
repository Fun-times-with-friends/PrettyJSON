package src

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentHashMapOf
import src.parsing.Lexer
import src.parsing.Parser

typealias Env = PersistentMap<String, Value>

sealed class Value {
    data class JsonBlock(val properties: ArrayList<Pair<String, Value>> = arrayListOf()): Value(){
        override fun toString(): String {
            var s = "{ "
            var setColon = false
            for (p in properties) {
                // { "foo": 5, "bar": 6 }
                if (setColon) s += ", "
                else setColon = true
                s += "\"${p.first}\": ${p.second}"
            }
            s += " }"
            return s
        }
    }
    data class Array(val a: List<Value>): Value(){
        override fun toString(): String = "[ ${a.joinToString(", ")} ]"
    }
    data class Number(val n: Int) : Value(){
        override fun toString(): String = n.toString()
    }
    data class Str(val s: String) : Value(){
        override fun toString(): String = "\"$s\""
    }
    data class Closure(val env: Env, val binder: String, val body: Expr) : Value(){
        override fun toString(): String = "$binder => $body"
    }
    data class Boolean(val b: kotlin.Boolean) : Value(){
        override fun toString(): String = b.toString()
    }
}

fun evalElement(env: Env, element: PrettyElement): Value{
    return when(element){
        is PrettyElement.Block -> evalBlock(env,element)
        is PrettyElement.Field -> evalField(env,element)
        is PrettyElement.Array -> evalArray(env,element)
        else -> throw Exception("Nope")
    }
}

fun evalBlock(pEnv: Env, block: PrettyElement.Block): Value.JsonBlock {
    var env = pEnv
    val b = Value.JsonBlock(arrayListOf())
    for (p in block.bindings) {
        env = env.put(p.first, evalElement(env, p.second))
    }

    for (p in block.properties) {
        b.properties.add(p.first to evalElement(env, p.second))
    }
    return b
}

fun evalField(env: Env, field: PrettyElement.Field): Value =
     eval(env, field.value)

fun evalArray(env: Env, array: PrettyElement.Array): Value {
    val elements = array.values.map { evalElement(env, it) }
    return Value.Array(elements)
}

fun eval(env: Env, expr: Expr): Value {
    return when (expr) {
        is Expr.Str -> Value.Str(expr.s)
        is Expr.Number -> Value.Number(expr.n)
        is Expr.Boolean -> Value.Boolean(expr.b)
        is Expr.Var -> env[expr.name] ?: throw Exception("${expr.name} is not defined.")
        is Expr.Lambda -> Value.Closure(env, expr.binder, expr.body)
        is Expr.Application -> {
            val evaledFunc = eval(env, expr.func)
            val evaledArg = eval(env, expr.arg)
            when (evaledFunc) {
                is Value.Closure -> {
                    val newEnv = env.put(evaledFunc.binder, evaledArg)
                    eval(newEnv, evaledFunc.body)
                }
                else -> throw Exception("$evaledFunc is not a function")
            }
        }
        is Expr.If -> {
            val cond = eval(env, expr.condition) as? Value.Boolean ?: throw Exception("Not a boolean")
            if (cond.b) {
                eval(env, expr.thenBranch)
            } else {
                eval(env, expr.elseBranch)
            }
        }
        is Expr.Binary -> {
            when (expr.operator) {
                Operator.Equals -> equalsValue(eval(env, expr.x), eval(env, expr.y))
                Operator.Multiply ->
                    evalBinaryNumber(eval(env, expr.x), eval(env, expr.y)) { x, y -> x * y }
                Operator.Plus ->
                    evalBinaryNumber(eval(env, expr.x), eval(env, expr.y)) { x, y -> x + y }
                Operator.Minus ->
                    evalBinaryNumber(eval(env, expr.x), eval(env, expr.y)) { x, y -> x - y }
            }
        }
    }
}

fun equalsValue(x: Value, y: Value): Value {
    val v1n = x as? Value.Number ?: throw Exception("Can't compare $x, it's not a number")
    val v2n = y as? Value.Number ?: throw Exception("Can't compare $y, it's not a number")
    return Value.Boolean(v1n.n == v2n.n)
}

fun evalBinaryNumber(v1: Value, v2: Value, f: (Int, Int) -> Int): Value {
    val v1n = v1 as? Value.Number ?: throw Exception("Can't use a binary operation on $v1, it's not a number")
    val v2n = v2 as? Value.Number ?: throw Exception("Can't use a binary operation on $v2, it's not a number")
    return Value.Number(f(v1n.n, v2n.n))
}

fun testEval(expr: String) {
    try {
        println(
            eval(
                persistentHashMapOf()
                ,
                Parser(
                    Lexer(
                        expr
                    ).lexTokens()
                )
                    .
                    parseExpr(
                    )
            )
        )

    }
    catch
        (
        ex
                     :
                     Exception) {
        println("Failed to eval with: ${ex.message}")
    }

}

fun main() {
"""
    fun myFunction(input: String): JSONObject{
        return input+=" ist cool"
    }
    let x = "Basti"
    {
        foo: "bar"
        yeet: myFunction(x)
    }
""".trimIndent()
}
