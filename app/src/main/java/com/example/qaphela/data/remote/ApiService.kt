package com.example.qaphela.data.remote

import com.example.qaphela.data.remote.model.ApiReportRequest
import com.example.qaphela.data.remote.model.ApiScanRequest
import com.example.qaphela.data.remote.model.ApiScanResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("api/scan")
    suspend fun analyzeContent(@Body request: ApiScanRequest): Response<ApiScanResponse>

    @POST("api/reports")
    suspend fun submitReport(@Body request: ApiReportRequest): Response<Unit>

    @GET("api/blocklist")
    suspend fun getRemoteBlocklist(): Response<List<Map<String, String>>>
}
