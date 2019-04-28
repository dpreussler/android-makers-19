package tv.sporttotal.makers

import com.nhaarman.mockitokotlin2.KStubbing
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Test

class MockingTest {

    val tested = Tournament()
    val normalsponsor = mock<Sponsor>()
    val premiumSponsor =mockPremiumSponsor{
        on { hasBanners } doReturn true
    }

    @Test
    fun `should have no impressions without visitors`() {
        tested.showBannersOfSponsor(normalsponsor)

        verify(normalsponsor).addImpressions(0)
    }

    @Test
    fun `normal sponsor gets default impressions`() {
        tested.add(Match("Germany", "France"))

        tested.showBannersOfSponsor(normalsponsor)

        verify(normalsponsor).addImpressions(100)
    }

    @Test
    fun `premium sponsor gets double impressions`() {
        tested.add(Match("Germany", "France"))

        tested.showBannersOfSponsor(premiumSponsor)

        verify(premiumSponsor).addImpressions(200)
    }


    private fun mockPremiumSponsor(function: KStubbing<Sponsor>.() -> Any) =
        mock<Sponsor>() {
            function(this)
        }
}