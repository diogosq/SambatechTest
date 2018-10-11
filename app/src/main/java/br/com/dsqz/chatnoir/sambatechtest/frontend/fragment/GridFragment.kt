package br.com.dsqz.chatnoir.sambatechtest.frontend.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import br.com.dsqz.chatnoir.sambatechtest.BuildConfig
import br.com.dsqz.chatnoir.sambatechtest.R
import br.com.dsqz.chatnoir.sambatechtest.backend.api.bean.Movie
import br.com.dsqz.chatnoir.sambatechtest.backend.api.TheMovieWebAPI
import br.com.dsqz.chatnoir.sambatechtest.backend.api.bean.UpComingListRequest
import br.com.dsqz.chatnoir.sambatechtest.frontend.adapter.EssentialAdapter
import kotlinx.android.synthetic.main.fragment_grid.*
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch


class GridFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null
    private lateinit var movieApi: TheMovieWebAPI

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_grid, container, false)

        if (savedInstanceState != null) {

            // Do something with value if needed
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val numberOfColumns = 5
        upcomingGrid.layoutManager = GridLayoutManager(context, numberOfColumns)
        loadGrid()
    }


    private fun loadGrid() {
        movieApi = TheMovieWebAPI(BuildConfig.API_KEY)

        var upcomming: Deferred<UpComingListRequest>

        launch(UI) {
            upcomming = movieApi.getUpcomingParser(activity!!.applicationContext)
            setAdapter(upcomming.await())
        }


    }

    private fun setAdapter(upcomingList: UpComingListRequest?) {

        val essentialAdapter = EssentialAdapter(context, R.layout.item_grid, upcomingList?.results)

        essentialAdapter.setViewBinder { essentialViewHolder, movie, position ->

            movieApi.loadImage(context!!, movie, (essentialViewHolder.getViewById(R.id.gridview_image) as ImageView),
                    (essentialViewHolder.getViewById(R.id.progressBarGridItemFragment) as ProgressBar))

            (essentialViewHolder.getViewById(R.id.gridview_image) as ImageView).setOnClickListener {
                if (movie != null) {
                    listener!!.loadMovieFragment(movie)
                }
            }
        }

        upcomingGrid.adapter = essentialAdapter
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