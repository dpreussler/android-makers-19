package tv.sporttotal.makers

import org.amshove.kluent.`should be`
import org.amshove.kluent.`should throw`
import org.amshove.kluent.shouldContainSome
import org.junit.jupiter.api.Test

class FirstTest {

    val tested = Tournament()

    @Test
    fun `tournament should have no teams initially`() {
        val tested = Tournament()
        tested.hasMatches `should be` false
    }

    @Test
    fun `tournamend should have teams`() {
        tested.add(Team("Germany"), Team("France"))
        tested.getTeams() shouldContainSome  listOf(Team("Germany"))
    }

    @Test
    fun `should throw if no matches drawn`() {

//        assertFailsWith<IllegalArgumentException> { tested.getMatches() }
//        assertThrows<IllegalArgumentException> { tested.getMatches() }

        { tested.getMatches() } `should throw` IllegalArgumentException::class
    }
}