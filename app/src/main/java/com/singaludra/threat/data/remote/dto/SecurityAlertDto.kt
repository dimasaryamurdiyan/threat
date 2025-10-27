package com.singaludra.threat.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SecurityAlertDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("severity")
    val severity: String,
    @SerializedName("source")
    val source: String,
    @SerializedName("threat_type")
    val threatType: String,
    @SerializedName("timestamp")
    val timestamp: String
)