package br.com.dsqz.chatnoir.sambatechtest.frontend.fragment

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.dsqz.chatnoir.sambatechtest.BuildConfig
import br.com.dsqz.chatnoir.sambatechtest.R
import br.com.dsqz.chatnoir.sambatechtest.backend.api.TheMovieWebAPI
import br.com.dsqz.chatnoir.sambatechtest.backend.api.bean.Movie
import br.com.dsqz.chatnoir.sambatechtest.backend.api.bean.MovieVideoList
import br.com.dsqz.chatnoir.sambatechtest.frontend.activity.MainActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener
import kotlinx.android.synthetic.main.fragment_movie.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch


class MovieFragment : Fragment() {

    private val gson: Gson = GsonBuilder().create()

    private var listener: OnFragmentInteractionListener? = null

    private lateinit var movieApi: TheMovieWebAPI
    private lateinit var movie: Movie

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            movie = gson.fromJson<Movie>(it.getString(MainActivity.MOVIE_BUNDLE_TAG), Movie::class.java)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_movie, container, false)

        if (savedInstanceState != null) {

            // Do something with value if needed
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData()
    }

    private fun loadData() {
        movieApi = TheMovieWebAPI(BuildConfig.API_KEY)

        movieApi.loadImage(context!!, movie, movieview_image, progressBarMovieFragment, TheMovieWebAPI.SIZE_W342)

        launch(UI) {
            val movieDetail = movieApi.getMovieDetail(context!!, movie.id!!)
            val movieVideoListDeferred = movieApi.getMovieVideoTrailer(context!!, movie.id!!)

            movieName.text = movie.title ?: ""
            movieYear.text = movie.releaseDate?.substring(0, 4) ?: ""
            movieTime.text = "${movieDetail.await().runtime ?: "-"} min"
            movieVoteAverage.text = "${movie.voteAverage ?: "-"}/10"
            movieOverview.text = movie.overview ?: ""

            val movieVideoList = movieVideoListDeferred.await()
            movieVideoList.results?.forEach { movieTrailer ->
                addPlayerView(movieVideoList.results.indexOf(movieTrailer), movieTrailer,
                        movieVideoList.results.indexOf(movieTrailer) == movieVideoList.results.lastIndex)
            }

            movieName.visibility = View.VISIBLE
            movieYear.visibility = View.VISIBLE
            movieTime.visibility = View.VISIBLE
            movieVoteAverage.visibility = View.VISIBLE
            movieOverview.visibility = View.VISIBLE
            progressBarMovieBaseFragment.visibility = View.GONE
        }

    }

    private fun addPlayerView(index: Int, movieTrailer: MovieVideoList.Result?,lastIndex: Boolean = false) {
        val youtubePlayerView = YouTubePlayerView(context)
        val divisor = View(context)
        divisor.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 10)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            divisor.background = ColorDrawable(context!!.getColor(android.R.color.darker_gray))
        } else {
            divisor.background = ColorDrawable(context!!.resources.getColor(android.R.color.darker_gray))
        }

        youtubePlayerView.id = View.generateViewId()

        youtubePlayerView.setTag(R.id.tag_id_key, youtubePlayerView.id)
        youtubePlayerView.setTag(R.id.tag_trailer_key, movieTrailer!!.key)
        youtubePlayerView.setTag(R.id.tag_barista_key, TheMovieWebAPI.TAG_BARISTA_VALUE)

        youtubePlayerView.visibility = View.GONE

        divisor.id = View.generateViewId()
        divisor.visibility = View.GONE

        movieVideoLayout.addView(youtubePlayerView)
        movieVideoLayout.addView(divisor)

        youtubePlayerView.initialize({ initializedYouTubePlayer ->
            initializedYouTubePlayer.addListener(object : AbstractYouTubePlayerListener() {
                override fun onReady() {
                    val videoId: String = youtubePlayerView.getTag(R.id.tag_trailer_key) as String
                    initializedYouTubePlayer.cueVideo(videoId, 0f)
                    youtubePlayerView.visibility = View.VISIBLE
                    divisor.visibility = View.VISIBLE
                    if (lastIndex) {
                        progressBarMovieVideoList?.visibility = View.GONE
                    }
                }
            })
        }, true)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)

        fun loadMovieFragment(movie: Movie)
    }

}