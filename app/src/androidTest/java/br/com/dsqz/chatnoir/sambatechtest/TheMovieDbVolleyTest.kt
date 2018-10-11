package br.com.dsqz.chatnoir.sambatechtest

import android.support.test.runner.AndroidJUnit4
import br.com.dsqz.chatnoir.sambatechtest.backend.api.bean.MovieDetail
import br.com.dsqz.chatnoir.sambatechtest.backend.api.bean.MovieVideoList
import br.com.dsqz.chatnoir.sambatechtest.backend.api.TheMovieWebAPI
import br.com.dsqz.chatnoir.sambatechtest.backend.api.bean.UpComingListRequest
import br.com.dsqz.chatnoir.sambatechtest.frontend.activity.MainActivity
import com.android.volley.toolbox.RequestFuture
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.runBlocking
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class TheMovieDbVolleyTest : BaseBarista() {

    companion object {
        const val CONFIGURATION_RESPONSE = "{\"images\":{\"base_url\":\"http://image.tmdb.org/t/p/\",\"secure_base_url\":\"https://image.tmdb.org/t/p/\",\"backdrop_sizes\":[\"w300\",\"w780\",\"w1280\",\"original\"],\"logo_sizes\":[\"w45\",\"w92\",\"w154\",\"w185\",\"w300\",\"w500\",\"original\"],\"poster_sizes\":[\"w92\",\"w154\",\"w185\",\"w342\",\"w500\",\"w780\",\"original\"],\"profile_sizes\":[\"w45\",\"w185\",\"h632\",\"original\"],\"still_sizes\":[\"w92\",\"w185\",\"w300\",\"original\"]},\"change_keys\":[\"adult\",\"air_date\",\"also_known_as\",\"alternative_titles\",\"biography\",\"birthday\",\"budget\",\"cast\",\"certifications\",\"character_names\",\"created_by\",\"crew\",\"deathday\",\"episode\",\"episode_number\",\"episode_run_time\",\"freebase_id\",\"freebase_mid\",\"general\",\"genres\",\"guest_stars\",\"homepage\",\"images\",\"imdb_id\",\"languages\",\"name\",\"network\",\"origin_country\",\"original_name\",\"original_title\",\"overview\",\"parts\",\"place_of_birth\",\"plot_keywords\",\"production_code\",\"production_companies\",\"production_countries\",\"releases\",\"revenue\",\"runtime\",\"season\",\"season_number\",\"season_regular\",\"spoken_languages\",\"status\",\"tagline\",\"title\",\"translations\",\"tvdb_id\",\"tvrage_id\",\"type\",\"video\",\"videos\"]}"
        const val UPCOMMING_REQUEST_SUBSTRING = "{\"results\":[{\""
    }

    private val tagName = this.javaClass.simpleName!!
    private lateinit var api: TheMovieWebAPI

    @get:Rule
    private var personalBaristaRule: PersonalBaristaRule<MainActivity> = PersonalBaristaRule.create(MainActivity::class.java)


    @Before
    @Throws(Exception::class)
    fun setUp() {
        baseSetup(tagName)
        api = TheMovieWebAPI(BuildConfig.API_KEY)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        baseTearDown(tagName)
    }

    @Test
    @Throws(Exception::class)
    fun configurationTest() {

        val responseTextFuture = RequestFuture.newFuture<String>()
        api.getConfigurationFuture(personalBaristaRule.getContext(), responseTextFuture)
        assertTrue(CONFIGURATION_RESPONSE == responseTextFuture.get())

    }

    @Test
    @Throws(Exception::class)
    fun listUpcomingRequestTest() {

        val responseTextFuture = RequestFuture.newFuture<String>()
        api.getUpcomingFutureList(personalBaristaRule.getContext(), responseTextFuture)
        assertTrue(responseTextFuture.get().contains(UPCOMMING_REQUEST_SUBSTRING))

    }

    @Test
    @Throws(Exception::class)
    fun listUpcomingParserTest() {

        var upcomming: UpComingListRequest? = null
        runBlocking(UI) {
            upcomming = api.getUpcomingParser(personalBaristaRule.getContext()).await()
        }
        assertTrue(upcomming?.results?.size ?: 0 > 0)
    }

    @Test
    @Throws(Exception::class)
    fun getDetailTest() {

        var upcomming: UpComingListRequest?
        var movieDetail: MovieDetail? = null

        runBlocking(UI) {
            upcomming = api.getUpcomingParser(personalBaristaRule.getContext()).await()
            movieDetail = upcomming!!.results?.get(0)?.id?.let { api.getMovieDetail(personalBaristaRule.getContext(), it).await() }
        }
        assertTrue(movieDetail?.id != null)
    }

    @Test
    @Throws(Exception::class)
    fun getMovieVideoTest() {

        var upcomming: UpComingListRequest?
        var movieVideoList: MovieVideoList? = null

        runBlocking(UI) {
            upcomming = api.getUpcomingParser(personalBaristaRule.getContext()).await()
            movieVideoList = upcomming!!.results?.get(0)?.id?.let { api.getMovieVideoTrailer(personalBaristaRule.getContext(), it).await() }
        }
        assertTrue(movieVideoList?.id != null)
    }

    @Test
    @Throws(Exception::class)
    fun donwloadImagesTest() {


        var upcomming: UpComingListRequest? = null
        runBlocking(UI) {
            upcomming = api.getUpcomingParser(personalBaristaRule.getContext()).await()
        }

        assertTrue(upcomming?.results?.size ?: 0 > 0)

        val imageLoaderConfig: ImageLoaderConfiguration = ImageLoaderConfiguration.Builder(personalBaristaRule.getContext()).build()
        val imageLoader = ImageLoader.getInstance()
        imageLoader.init(imageLoaderConfig)

        upcomming?.results!!.forEach {
            val imagePath = it?.posterPath ?: ""
            val imageUri = "${TheMovieWebAPI.basePosterPathUrl}${TheMovieWebAPI.SIZE_W92}/$imagePath"
            val bmp = imageLoader.loadImageSync(imageUri)
            assertTrue(bmp.byteCount > 0)
        }

    }


}
