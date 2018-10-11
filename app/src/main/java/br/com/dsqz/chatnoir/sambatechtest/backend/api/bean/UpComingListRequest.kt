package br.com.dsqz.chatnoir.sambatechtest.backend.api.bean

import com.google.gson.annotations.SerializedName

/**
 * Created by diogosq on 10/8/18.
 */
data class UpComingListRequest(
        @SerializedName("page") val page: Int?,
        @SerializedName("results") val results: List<Movie?>?,
        @SerializedName("dates") val dates: Dates?,
        @SerializedName("total_pages") val totalPages: Int?,
        @SerializedName("total_results") val totalResults: Int?
)