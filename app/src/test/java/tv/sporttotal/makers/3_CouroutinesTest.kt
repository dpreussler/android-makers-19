package tv.sporttotal.makers

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyBlocking
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineContext
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

/*
 mocking and timing coroutines

 some of these tests provide real value when just calling mocks, they are for demo purposes only

 */
class CouroutinesTest {

    @Nested
    inner class `Given something suspended` {

        val tested = mock<LiveGame>()

        @Test
        fun `call suspended`() {

            runBlocking {
                whenever(tested.broadcast()).thenReturn( true)
            }
        }

        @Test
        fun `call suspended better`() = blockingTest {

            whenever(tested.broadcast()).thenReturn( true)

            tested.broadcast()

            verify(tested).broadcast()
        }

        @Test
        fun `call suspended much better`() = runBlocking<Unit>{

            whenever(tested.broadcast()).thenReturn( true)

            tested.broadcast()

            verify(tested).broadcast()
        }

        @Test
        fun `call suspended mockito`() {
            val tested = mock<LiveGame> {
                onBlocking{ broadcast() } doReturn true
            }

            runBlocking { tested.broadcast() }

            verifyBlocking(tested) { broadcast() }
        }
    }

    @Nested
    inner class `Given something deferred` {

        @Test
        fun test() {
            val tested = mock<LiveGame> {
                onBlocking { startBroadcasting() } doReturn CompletableDeferred(true)
            }

            runBlocking {
                tested.startBroadcasting().await() `should be equal to` true
            }
        }
    }

    @Nested
    inner class `Given something cancellable` {

        val testCoroutineContext = TestCoroutineContext()

        @Test
        fun `should cancel when triggering action`() {
            val game = spy(SimpleGame(Match("Germany", "France"), testCoroutineContext))

            val job = game.startBroadcasting()

            job.cancel()

            testCoroutineContext.triggerActions()

            job.isCompleted `should be equal to` true
            verifyBlocking(game, never()) { broadcast() }
        }

        @Test
        fun `should cancel when forwarding time`() {

            val game = spy(NinetyMinuteGame(Match("Germany", "France")))
            game.coroutineContext = testCoroutineContext

            val job = game.startBroadcasting()

            job.cancel()

            testCoroutineContext.advanceTimeBy(90, TimeUnit.MINUTES)

            job.isCompleted `should be equal to` true
            verifyBlocking(game, never()) { broadcast() }
        }

    }

    @Nested
    inner class `Given structured concurrency` : CoroutineScope {

        override val coroutineContext = TestCoroutineContext()

        val tested = ImageFilter()

        @Test
        fun `test via launch`() {

            val job = launch {
                tested.loadAndCombine("1", "2").name `should be equal to` "1:2"
            }

            coroutineContext.advanceTimeBy(3, TimeUnit.SECONDS)
            job.isCompleted `should be equal to` true

            coroutineContext.exceptions.forEach { throw it }
        }


        @Test
        fun `test via async`() {

            val job = async {
                tested.loadAndCombine("1", "2").name `should be equal to` "1:2"
            }

            coroutineContext.advanceTimeBy(3, TimeUnit.SECONDS)
            job.getCompletionExceptionOrNull()?.let { throw it }

            coroutineContext.assertAllUnhandledExceptions { false }
        }



        @Test
        fun `test with switching dispatchers`() {

            val dispatchers = TestCoroutineDispatchers()
            val filter = ImageFilter(dispatchers = dispatchers)

            val job = async {
                filter.loadAndSwitchAndCombine("1", "2").name shouldEqual "1:2"
            }

            coroutineContext.triggerActions()

            dispatchers.io.advanceTimeBy(3, TimeUnit.SECONDS)

            coroutineContext.triggerActions()

            job.getCompletionExceptionOrNull()?.let { throw it }
        }
    }

    private fun blockingTest(block: suspend () -> Unit) {
        runBlocking { block() }
    }
}