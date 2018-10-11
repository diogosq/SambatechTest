package br.com.dsqz.chatnoir.sambatechtest

import android.support.test.runner.AndroidJUnit4
import br.com.dsqz.chatnoir.sambatechtest.backend.api.TheMovieWebAPI
import br.com.dsqz.chatnoir.sambatechtest.backend.api.bean.UpComingListRequest
import br.com.dsqz.chatnoir.sambatechtest.frontend.activity.MainActivity
import com.schibsted.spain.barista.assertion.BaristaRecyclerViewAssertions
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions
import com.schibsted.spain.barista.interaction.BaristaClickInteractions
import com.schibsted.spain.barista.interaction.BaristaListInteractions
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.runBlocking
import org.junit.*
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MovieScreenBaristaTest : BaseBarista() {


    private val tagName = this.javaClass.simpleName!!

    @get:Rule
    private var personalBaristaRule: PersonalBaristaRule<MainActivity> = PersonalBaristaRule.create(MainActivity::class.java)
    private lateinit var api: TheMovieWebAPI

    @Before
    @Throws(Exception::class)
    fun setUp() {
        baseSetup(tagName)
        api = TheMovieWebAPI(BuildConfig.API_KEY)
        personalBaristaRule.launchActivity()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        baseTearDown(tagName)
    }

    @Test
    @Throws(Exception::class)
    fun loadAllMovieScreenVisibleTest() {

        var upcomming: UpComingListRequest? = null
        runBlocking(UI) {
            upcomming = api.getUpcomingParser(personalBaristaRule.getContext()).await()
        }
        Assert.assertTrue(upcomming?.results?.size ?: 0 > 0)

        upcomming!!.results!!.forEach {
            val itemIndex = upcomming!!.results!!.indexOf(it)

            BaristaSleepInteractions.sleep(4000)

            BaristaRecyclerViewAssertions.itemAtPositionAssertAnyDrawable(R.id.upcomingGrid, itemIndex)

            BaristaListInteractions.clickListItem(R.id.upcomingGrid, itemIndex)

            BaristaSleepInteractions.sleep(4000)

            BaristaVisibilityAssertions.assertDisplayed(R.id.movieName)
            BaristaVisibilityAssertions.assertDisplayed(R.id.movieYear)
            BaristaVisibilityAssertions.assertDisplayed(R.id.movieTime)
            BaristaVisibilityAssertions.assertDisplayed(R.id.movieVoteAverage)
            BaristaVisibilityAssertions.assertDisplayed(R.id.movieOverview)


            BaristaClickInteractions.clickOn(R.id.tag_barista_key, TheMovieWebAPI.TAG_BARISTA_VALUE)
            BaristaSleepInteractions.sleep(4000)
            BaristaClickInteractions.clickOn(R.id.tag_barista_key, TheMovieWebAPI.TAG_BARISTA_VALUE)

            BaristaClickInteractions.clickBack()
        }

    }


}

