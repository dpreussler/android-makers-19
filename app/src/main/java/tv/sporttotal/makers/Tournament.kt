package tv.sporttotal.makers


class Tournament() {
    private val games = mutableListOf<Match>()
    private val teams = mutableListOf<Team>()

    fun getMatches() : List<Match> {
        if (games.isEmpty()) {
            throw IllegalArgumentException()
        }
        else return games
    }

    fun getTeams() : List<Team> = teams

    fun add(vararg matches: Match) {
        this.games.addAll(matches)
        matches.forEach {
            add(it.guest)
            add(it.home)
        }
    }

    fun add(vararg teams: Team) {
        this.teams.addAll(teams)
    }

    fun showBannersOfSponsor(sponsor: Sponsor) {
        sponsor.addImpressions(if (sponsor.isPremium) 2 * visitors else visitors)
    }

    fun drawMatches() {
        games.addAll(teams.zipWithNext().map { Match(it.first, it.second) })
    }

    var hasMatches: Boolean = games.isEmpty().not()

    val visitors: Int
        get() = games.calculateVisitors()

    var isRunning: Boolean = false

    operator fun plus(match: Match): Tournament = this.also { add(match) }
    operator fun plusAssign(match: Match) { add(match) }
    operator fun plus(team: Team): Tournament = this.also { add(team) }
    operator fun plusAssign(team: Team) { add(team) }
}

private fun MutableList<Match>.calculateVisitors(): Int = size * 100


