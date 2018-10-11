package br.com.dsqz.chatnoir.sambatechtest.frontend.activity

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import br.com.dsqz.chatnoir.sambatechtest.R
import br.com.dsqz.chatnoir.sambatechtest.backend.api.bean.Movie
import br.com.dsqz.chatnoir.sambatechtest.frontend.fragment.GridFragment
import br.com.dsqz.chatnoir.sambatechtest.frontend.fragment.MovieFragment
import br.com.dsqz.chatnoir.sambatechtest.frontend.util.EssentialFragmentUtil
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class MainActivity : AppCompatActivity(), GridFragment.OnFragmentInteractionListener,
        MovieFragment.OnFragmentInteractionListener {

    private val gson: Gson = GsonBuilder().create()

    private var gridFragment = GridFragment()
    private var movieFragment = MovieFragment()

    private lateinit var gridBundle: Bundle
    private lateinit var movieBundle: Bundle


    companion object {
        const val GRID_FRAGMENT_TAG = "GRID_FRAGMENT_TAG"
        const val MOVIE_FRAGMENT_TAG = "MOVIE_FRAGMENT_TAG"
        const val MOVIE_BUNDLE_TAG = "MOVIE_BUNDLE_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        gridBundle = Bundle()
        movieBundle = Bundle()

        val bundle = intent.extras
        if (savedInstanceState == null) {
            EssentialFragmentUtil.add(this, R.id.mainContainer, gridFragment, bundle, GRID_FRAGMENT_TAG)
        }
    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putBundle(GRID_FRAGMENT_TAG, gridBundle)
        savedInstanceState.putBundle(MOVIE_FRAGMENT_TAG, movieBundle)
    }

    public override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        gridBundle = savedInstanceState.getBundle(GRID_FRAGMENT_TAG)
        movieBundle = savedInstanceState.getBundle(MOVIE_FRAGMENT_TAG)
    }

    override fun loadMovieFragment(movie: Movie) {

        movieBundle.putString(MOVIE_BUNDLE_TAG, gson.toJson(movie))

        EssentialFragmentUtil.replace(this, R.id.mainContainer, movieFragment, movieBundle, MainActivity.MOVIE_FRAGMENT_TAG)
    }


    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onBackPressed() {
        when {
            EssentialFragmentUtil.getFragmentByTag(this, MainActivity.GRID_FRAGMENT_TAG) != null -> finish()
            EssentialFragmentUtil.getFragmentByTag(this, MainActivity.MOVIE_FRAGMENT_TAG) != null -> EssentialFragmentUtil.replace(this, R.id
                    .mainContainer, gridFragment, gridBundle, MainActivity.GRID_FRAGMENT_TAG)
            else -> super.onBackPressed()
        }
    }


}
