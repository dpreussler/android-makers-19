package tv.sporttotal.makers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

interface LiveGame {
    suspend fun broadcast(): Boolean
    fun startBroadcasting(): Deferred<Boolean>
}

class TestGame(
    val liveGame: LiveGame,
    override val coroutineContext: CoroutineContext = Dispatchers.Default): CoroutineScope {

    fun testSignal() =
        launch {
            liveGame.broadcast()
        }
}

class SimpleGame(
    val match: Match,
    override var coroutineContext: CoroutineContext = Dispatchers.Default)
    : LiveGame, CoroutineScope {

    override fun startBroadcasting(): Deferred<Boolean> {
        return async {
            broadcast()
        }
    }
    override suspend fun broadcast(): Boolean {
        return true
    }
}

class NinetyMinuteGame(val match: Match): LiveGame, CoroutineScope {

    override var coroutineContext: CoroutineContext = Dispatchers.Default

    override fun startBroadcasting(): Deferred<Boolean> {
        return async {
            broadcast()
        }
    }
    override suspend fun broadcast(): Boolean {
        delay(90 * 60 * 1000)
        return true
    }
}