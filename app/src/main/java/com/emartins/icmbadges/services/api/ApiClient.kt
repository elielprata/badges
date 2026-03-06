package com.emartins.icmbadges.services.api

import com.emartins.icmbadges.models.EnrollmentData
import com.emartins.icmbadges.models.GetBadgeRequest
import com.emartins.icmbadges.models.GetEventsResponse
import com.emartins.icmbadges.models.GetLinkResponse
import com.emartins.icmbadges.models.GetModuleResponse
import com.emartins.icmbadges.models.SignInRequest
import com.emartins.icmbadges.models.SignInResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

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


    // crachá seminaristas
    @POST("mgestao/api/eventos/inscricoes/printcrachas/{eventId}")
    suspend fun getBadgePdf(
        @Path("eventId") eventId: Int,
        @Body body: GetBadgeRequest
    ): Response<ResponseBody>


    // crachá trabalhadores
    @POST("mgestao/api/evento/trabalhadores/printcrachas/{eventId}")
    suspend fun getWorkerBadgePdf(
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
    suspend fun getModuleInfo(): List<GetModuleResponse>


    @GET("/mgestao/api/eventos/list/{codNivel}")
    suspend fun getEvents(
        @Path("codNivel") codNivel: String
    ): List<GetEventsResponse>
}