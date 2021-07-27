package src

import kotlinx.collections.immutable.persistentHashMapOf
import src.parsing.Lexer
import src.parsing.Parser
import src.parsing.Token

fun test(input: String) {
    println("Lexing: $input")
    val lexer = Lexer(input)
    while (lexer.peek() != Token.EOF) {
        println(lexer.next())
    }
    println(lexer.next())
}

fun testParser(input: String) {
    println("Parsing: $input")
    val lexer = Lexer(input)
    val parser = Parser(lexer.lexTokens())
    println(parser.parseExpr())
}
fun testBlock(input: String) {
    println("Parsing: $input")
    val lexer = Lexer(input)
    val parser = Parser(lexer.lexTokens())
    val parsedBlock = parser.parseBlock()
    val evaledBlock = evalBlock(persistentHashMapOf(),parsedBlock )
    println(evaledBlock)
}


//
fun main(){
    val simpleTest = """
         {  
            block: {
                let x = "inner variable"
                inner : x
            }
            outer: "y"
         }""".trimIndent()


    // let bindings
    val funTest = """
        {  
            let value = 5
            let fib = \n => 
                if n == 0 then 1 
                else if n == 1 then 1 
                else fib (n - 1) + fib (n - 2) 
            lol: (fib) value
        }
    """.trimIndent()
    // testBlock(simpleTest)
    // testExistingBlock()
    testBlock(simpleTest)
    /*
    let a = { foo: "bar" }
    let a = 10 | "bar" | true
    keyword binder equals, Block | Expr
    testEval(
        """

        """.trimIndent()
    )


     */

}