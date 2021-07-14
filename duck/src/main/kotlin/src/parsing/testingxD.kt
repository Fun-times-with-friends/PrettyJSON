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

fun testExistingBlock(){
    val b = PrettyElement.Block(
        listOf(
            "a" to PrettyElement.Field( Expr.Number(5)),
        ),
        hashMapOf(
            "foo" to PrettyElement.Block(
                listOf(),
                hashMapOf("bar" to PrettyElement.Field(Expr.Var("a")))
            ),
        )
    )
    val result = evalBlock(persistentHashMapOf(), b)
    println(result)

}

// todo LEXER, check for fieldname
//
fun main(){
    val simpleTest = """
         {  
            foo: {
                thisShouldWork: (\x => x + 2) 10
                thisShouldNotWork: let a = 10 in
                                   let a = 9 in
                                   if a == 10 then 10 else 0
                x: let a = "AAAHHHHHHHHHH" in a
            } 
         }""".trimIndent()


    // let bindings
    // todo our letbindings bites with the normal lets
    // todo check lamda in evaluator?
    val funTest = """
        {  
        # This is a comment
        #* mutliline???
        yeet *#
            let fib = \n => 
                if n == 0 then 1 
                else if n == 1 then 1 
                else fib (n - 1) + fib (n - 2) 
            lol: (fib) 5
        }
    """.trimIndent()
    // testBlock(simpleTest)
    // testExistingBlock()
    testBlock(funTest)
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