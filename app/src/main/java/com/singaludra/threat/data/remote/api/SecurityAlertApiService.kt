package com.singaludra.threat.data.remote.api

import com.singaludra.threat.data.remote.dto.SecurityAlertDto
import retrofit2.Response
import retrofit2.http.GET

interface SecurityAlertApiService {

    @GET("status")
    suspend fun getSecurityAlerts(): Response<List<SecurityAlertDto>>

    companion object {
        const val BASE_URL = "https://ochi-mockup.itsecasia.dev/"
    }
}