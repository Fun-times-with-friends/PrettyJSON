package src.parsing

import kotlinx.collections.immutable.persistentHashMapOf
import src.evalBlock

fun main() {
    simpleBlock()
    array()
    letBindings()
    lamdaTest()
    alreadyDefined()
    localVariable()
}

fun simpleBlock() {
    testBlock(
        """
{
    #* This is a very simple
    block *#
    prop1: "red"
    prop2: 10 + 10
    prop3: { 
        foo: "bar" 
    }
}
    """.trimIndent()
    )
}

fun array() {
    testBlock(
        """
{
    prob1: [
      { foo: 1 bar: 2 },
      { foo: 2 bar: 3 }, # Trailing comma
    ]
    prob2: [
      10,
      10 + 10,
      [ 1,2,3 ],
    ]
}
    """.trimIndent()
    )
}

fun letBindings() {
    testBlock(
        """
{
    let color = "red"
    let block = {
        prop1: "Im a string in a block"
        prop2: color
    }
    
    prop1: color
    prop2: block
    
}
    """.trimIndent()
    )
}

fun alreadyDefined() {
    testInvalid(
        """
{
    prop1: "Im a string in a block"
    prop1: "I should already be defined"
}
    """.trimIndent())
}

fun localVariable(){
    testInvalid(
        """
{
    prop1: { let a = "a" }
    prob2: a
}
    """.trimIndent())
}

fun testInvalid(str: String) {
    try {
        testBlock(str)
    } catch (e: Exception) {
        println("Failed Exception (this is a good thing):")
        println(e.message)
    }
}

fun lamdaTest() {
    testBlock(
        """
{  
    let value = 5
    let fib = \n => 
        if n == 0 then 1 
        else if n == 1 then 1 
        else fib (n - 1) + fib (n - 2) 
        
    prob1: (fib) value
}
    """.trimIndent()
    )
}


fun testBlock(input: String) {
    println("\n===============================================")
    println("PrettyJSON:")
    println(input)
    println("-----------------------------------------------")
    val lexer = Lexer(input)
    val parser = Parser(lexer.lexTokens())
    val parsedBlock = parser.parse()
    val evaledBlock = evalBlock(persistentHashMapOf(), parsedBlock)
    println("JSON")
    println(evaledBlock)
}
