package com.emartins.icmbadges.services.api

import com.emartins.icmbadges.models.ApiResponse
import com.emartins.icmbadges.models.EnrollmentData
import com.emartins.icmbadges.models.GetBadgeRequest
import com.emartins.icmbadges.models.GetEventsResponse
import com.emartins.icmbadges.models.GetEventsWrapper
import com.emartins.icmbadges.models.GetLinkResponse
import com.emartins.icmbadges.models.GetModuleResponse
import com.emartins.icmbadges.models.SignInRequest
import com.emartins.icmbadges.models.SignInResponse
import retrofit2.Response
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiClient {
    @GET("mgestao/api/eventos/inscricoes/list/{eventId}")
    suspend fun searchEnrollment(
        @Path("eventId") eventId: Int,
        @Query("kfilter") query: String,
        @Query("status") status: String = "A"
    ): List<EnrollmentData>

    @GET("mgestao/api/evento/trabalhadores/list/{eventId}")
    suspend fun searchVoluntaryEnrollment(
        @Path("eventId") eventId: Int,
        @Query("kfilter") query: String
    ): List<EnrollmentData>

    @POST("mgestao/api/eventos/inscricoes/printcrachas/{eventId}")
    suspend fun getBadgePdf(
        @Path("eventId") eventId: Int,
        @Body body: GetBadgeRequest
    ): Response<ResponseBody>

    @POST("v2/api/entrar")
    suspend fun signIn(
        @Body body: SignInRequest
    ): SignInResponse

    @GET("v2/api/obter-url-v1?ModuloId=7")
    suspend fun getLink(
        @Header("Authorization") token: String
    ): GetLinkResponse

    @GET("user/modules/7")
    suspend fun getModuleInfo(
    ): List<GetModuleResponse>


    @GET("/mgestao/api/eventos/list/{codNivel}")
    suspend fun getEvents(
        @Path("codNivel") codNivel: String,
    ): List<GetEventsResponse>
}