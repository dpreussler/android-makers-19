package tv.sporttotal.makers

class Match(val home: Team, val guest: Team) {

    constructor(home: String , guest: String): this(Team(home), Team(guest))

    fun isHightlight(): Boolean {
        return false
    }

    fun isPlaying(team: Team) = team in listOf(home, guest)

    var isToday = true
    var isFootball = true
}