package br.com.dsqz.chatnoir.sambatechtest

import android.support.test.runner.AndroidJUnit4
import br.com.dsqz.chatnoir.sambatechtest.backend.api.TheMovieWebAPI
import br.com.dsqz.chatnoir.sambatechtest.backend.api.bean.UpComingListRequest
import br.com.dsqz.chatnoir.sambatechtest.frontend.activity.MainActivity
import br.com.dsqz.chatnoir.sambatechtest.test.BuildConfig
import com.schibsted.spain.barista.assertion.BaristaRecyclerViewAssertions
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.runBlocking
import org.junit.*
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class GridScreenBaristaTest : BaseBarista() {


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
    fun loadAllImagesTest() {

        var upcomming: UpComingListRequest? = null
        runBlocking(UI) {
            upcomming = api.getUpcomingParser(personalBaristaRule.getContext()).await()
        }
        Assert.assertTrue(upcomming?.results?.size ?: 0 > 0)

        upcomming!!.results!!.forEach {
            BaristaRecyclerViewAssertions.itemAtPositionAssertAnyDrawable(R.id.upcomingGrid, upcomming!!.results!!.indexOf(it))
        }

    }


}

