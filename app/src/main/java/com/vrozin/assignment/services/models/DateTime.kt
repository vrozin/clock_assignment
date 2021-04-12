package com.vrozin.assignment.services.models

import com.google.gson.annotations.SerializedName

data class DateTime(
    @SerializedName("datetime")
    var dateTime: String?
)
