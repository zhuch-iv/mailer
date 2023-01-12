package net.zhu4

import org.junit.jupiter.api.Test
import kotlin.random.Random

class TestedTest {

    val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    @Test
    fun test() {
        (0..1201)
            .map { randomString() }
            .forEach { println(it) }
    }

    fun randomString() = (1..12)
        .map { Random.nextInt(0, charPool.size).let { charPool[it] } }
        .joinToString("")
}