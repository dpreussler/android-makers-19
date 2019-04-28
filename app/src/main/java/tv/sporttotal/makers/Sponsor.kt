package tv.sporttotal.makers

@Suppress("unused")
interface Sponsor {

    var isPremium: Boolean

    var hasBanners: Boolean

    var hasVideoAds: Boolean

    fun addImpressions(count: Int)

    fun getMoney() : Money
}