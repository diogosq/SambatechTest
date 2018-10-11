package br.com.dsqz.chatnoir.sambatechtest.backend.api

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import br.com.dsqz.chatnoir.sambatechtest.backend.api.bean.Movie
import br.com.dsqz.chatnoir.sambatechtest.backend.api.bean.MovieDetail
import br.com.dsqz.chatnoir.sambatechtest.backend.api.bean.MovieVideoList
import br.com.dsqz.chatnoir.sambatechtest.backend.api.bean.UpComingListRequest
import com.android.volley.Request
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async


class TheMovieWebAPI(private val token: String) {

    @Suppress("unused")
    companion object {

        private const val basePath = "https://api.themoviedb.org/3"
        const val basePosterPathUrl = "https://image.tmdb.org/t/p/"

        const val SIZE_W92 = "w92"
        const val SIZE_W342 = "w342"

        const val TYPE_VIDEO_TRAILER: String = "Trailer"
        const val TYPE_VIDEO_TEASER: String = "Teaser"
        const val TYPE_VIDEO_CLIP: String = "Clip"
        const val TYPE_VIDEO_FEATURETTE: String = "Featurette"
        const val TAG_BARISTA_VALUE: String = "BARISTA_TAG"

        fun getMovieVideoList(token: String, movieId: Int, type: String = TYPE_VIDEO_TRAILER): String =
                "$basePath/movie/$movieId/videos?api_key=$token&language=en-US&type=$type"
        // ="Trailer"

        fun getConfigurationUrl(token: String): String = "$basePath/configuration?api_key=$token"

        fun getMovieDetailUrl(token: String, movieId: Int): String = "$basePath/movie/$movieId?api_key=$token&language=en-US"

        fun getUpcomingUrl(token: String): String = "$basePath/movie/upcoming?api_key=$token&language=en-US&page=1"

        fun getImageUrl(size: String, posterPath: String): String = "${"$basePosterPathUrl$size/"}$posterPath"
    }

    private val gson: Gson = GsonBuilder().create()


    private inline fun <reified T : Any> getData(context: Context, url: String): Deferred<T> = async(CommonPool) {
        val responseTextFuture = RequestFuture.newFuture<String>()
        val queue = Volley.newRequestQueue(context)

        val stringRequest = StringRequest(Request.Method.GET, url, responseTextFuture, responseTextFuture)
        queue.add(stringRequest)

        val jsonValue = responseTextFuture?.get() ?: ""
        gson.fromJson<T>(jsonValue, T::class.java)
    }

    fun getConfigurationFuture(context: Context, responseTextFuture: RequestFuture<String>?) {
        val queue = Volley.newRequestQueue(context)
        val stringRequest = StringRequest(Request.Method.GET, getConfigurationUrl(token), responseTextFuture, responseTextFuture)
        queue.add(stringRequest)
    }

    fun getUpcomingFutureList(context: Context, responseTextFuture: RequestFuture<String>?) {
        val queue = Volley.newRequestQueue(context)
        val stringRequest = StringRequest(Request.Method.GET, getUpcomingUrl(token), responseTextFuture, responseTextFuture)
        queue.add(stringRequest)
    }

    fun getUpcomingParser(context: Context): Deferred<UpComingListRequest> = async(CommonPool) {
        getData<UpComingListRequest>(context, getUpcomingUrl(token)).await()
    }

    fun getMovieDetail(context: Context, movieId: Int): Deferred<MovieDetail> = async(CommonPool) {
        getData<MovieDetail>(context, getMovieDetailUrl(token, movieId)).await()
    }

    fun getMovieVideoTrailer(context: Context, movieId: Int): Deferred<MovieVideoList> = async(CommonPool) {
        getData<MovieVideoList>(context, getMovieVideoList(token, movieId, TYPE_VIDEO_TRAILER)).await()
    }

    fun loadImage(context: Context, movie: Movie?, imageView: ImageView, progressBar: ProgressBar, size: String = SIZE_W92) {

        imageView.visibility = View.GONE

        val imageLoaderConfig: ImageLoaderConfiguration = ImageLoaderConfiguration.Builder(context).build()
        val imageLoader = ImageLoader.getInstance()
        imageLoader.init(imageLoaderConfig)

        val posterPath = movie?.posterPath ?: ""
        val imageUri = getImageUrl(size, posterPath)
        imageLoader.displayImage(imageUri, imageView, object : SimpleImageLoadingListener() {

            override fun onLoadingComplete(imageUri: String, view: View, loadedImage: Bitmap) {
                view.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }
        })
    }


}

