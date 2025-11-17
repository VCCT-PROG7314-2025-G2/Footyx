package com.example.footyxapp.data.model

import com.google.gson.annotations.SerializedName

data class PlayerSearchResponse(
    @SerializedName("get")
    val get: String,
    @SerializedName("parameters")
    val parameters: SearchParameters,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("results")
    val results: Int,
    @SerializedName("paging")
    val paging: Paging,
    @SerializedName("response")
    val response: List<PlayerProfile>
)

data class SearchParameters(
    @SerializedName("search")
    val search: String
)

data class PlayerProfile(
    @SerializedName("player")
    val player: PlayerBasicInfo
)

data class PlayerBasicInfo(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("firstname")
    val firstname: String?,
    @SerializedName("lastname")
    val lastname: String?,
    @SerializedName("age")
    val age: Int?,
    @SerializedName("birth")
    val birth: Birth?,
    @SerializedName("nationality")
    val nationality: String?,
    @SerializedName("height")
    val height: String?,
    @SerializedName("weight")
    val weight: String?,
    @SerializedName("number")
    val number: Int?,
    @SerializedName("position")
    val position: String?,
    @SerializedName("photo")
    val photo: String?
)
