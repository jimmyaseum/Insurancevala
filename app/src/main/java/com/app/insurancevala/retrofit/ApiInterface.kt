package com.app.insurancevala.retrofit

import com.app.insurancevala.model.api.CommonResponse
import com.app.insurancevala.model.pojo.DocumentsModel
import com.app.insurancevala.model.pojo.DocumentsResponse
import com.app.insurancevala.model.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * Created by Jimmy
 */
interface ApiInterface {

    // AI005 Login
    @POST("Master/ManageUserLogin")
    fun login(@Body body: RequestBody?): retrofit2.Call<LoginResponse>

    // Dropdown

    @POST("Master/ManageLeadType")
    fun ManageLeadType(@Body body: RequestBody?): retrofit2.Call<LeadTypeResponse>

    @POST("Master/ManageUserType")
    fun ManageUserType(@Body body: RequestBody?): retrofit2.Call<UserTypeResponse>

    @POST("Master/ManageLeadSource")
    fun ManageLeadSource(@Body body: RequestBody?): retrofit2.Call<LeadSourceResponse>

    @POST("Master/ManageInquiryType")
    fun ManageInquiryType(@Body body: RequestBody?): retrofit2.Call<InquiryTypeResponse>

    @POST("Master/ManageInquirySubType")
    fun ManageInquirySubType(@Body body: RequestBody?): retrofit2.Call<InquirySubTypeResponse>

    @POST("Master/ManageLeadStatus")
    fun ManageLeadStatus(@Body body: RequestBody?): retrofit2.Call<LeadStatusResponse>

    @POST("Master/ManageCountries")
    fun ManageCountries(@Body body: RequestBody?): retrofit2.Call<CountriesResponse>

    @POST("Master/ManageStates")
    fun ManageStates(@Body body: RequestBody?): retrofit2.Call<StatesResponse>

    @POST("Master/ManageCities")
    fun ManageCities(@Body body: RequestBody?): retrofit2.Call<CitiesResponse>

    @POST("Master/ManageInitial")
    fun ManageInitial(@Body body: RequestBody?): retrofit2.Call<InitialResponse>

    @POST("Master/ManageUsers")
    fun ManageUsers(@Body body: RequestBody?): retrofit2.Call<UserResponse>

    @Multipart
    @POST("MasterSub/UserImageUpdate") // done
    fun ManageUserImage(
        @Part("UserGUID") ReferenceGUID: RequestBody?,
        @Part attachment: ArrayList<MultipartBody.Part>?
    ): retrofit2.Call<UserImageResponse>

    @POST("Master/ManageCallPurpose")
    fun ManageCallPurpose(@Body body: RequestBody?): retrofit2.Call<CallPurposeResponse>

    @POST("Master/ManageCallType")
    fun ManageCallType(@Body body: RequestBody?): retrofit2.Call<CallTypeResponse>

    @POST("Master/ManageCallResult")
    fun ManageCallResult(@Body body: RequestBody?): retrofit2.Call<CallResultResponse>

    @POST("Master/ManageMeetingType")
    fun ManageMeetingType(@Body body: RequestBody?): retrofit2.Call<MeetingTypeResponse>

    @POST("Master/ManageMeetingStatus")
    fun ManageMeetingStatus(@Body body: RequestBody?): retrofit2.Call<MeetingStatusResponse>

    @POST("Master/ManageMeetingOutcome")
    fun ManageMeetingOutcome(@Body body: RequestBody?): retrofit2.Call<MeetingOutcomeResponse>

    @POST("Master/ManageLead")
    fun ManageLead(@Body body: RequestBody?): retrofit2.Call<LeadResponse>

    @POST("Master/ManageNote")
    fun ManageNotes(@Body body: RequestBody?): retrofit2.Call<NotesResponse>

    @POST("Master/ManageCall")
    fun ManageCalls(@Body body: RequestBody?): retrofit2.Call<CallsResponse>

    @POST("Master/ManageTask")
    fun ManageTask(@Body body: RequestBody?): retrofit2.Call<TasksResponse>

    @POST("Master/ManageMeeting_New")
    fun ManageMeetings(@Body body: RequestBody?): retrofit2.Call<MeetingsResponse>

    @Multipart
    @POST("MasterSub/InsertAttachment") // done
    fun ManageAttachment(
        @Part("LeadID") LeadID: Int?,
        @Part("ReferenceGUID") ReferenceGUID: RequestBody?,
        @Part("AttachmentType") AttachmentType: RequestBody?,
        @Part("AttachmentName") AttachmentName: RequestBody?,
        @Part attachment: ArrayList<MultipartBody.Part>?

    ): retrofit2.Call<CommonResponse>

    @POST("MasterSub/AttachmentFilter")
    fun ManageAttachmentList(@Body body: RequestBody?): retrofit2.Call<DocumentsResponse>

    @POST("MasterSub/AttachmentDelete")
    fun ManageAttachmentDelete(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @Multipart
    @POST("MasterSub/UpdateAttachment")
    fun ManageAttachmentUpdate(
        @Part("ID")ID: Int?,
        @Part("AttachmentName") AttachmentName: RequestBody?,
        @Part attachment: ArrayList<MultipartBody.Part>?
    ): retrofit2.Call<CommonResponse>

    @POST("Master/ManageNBInquiry")
    fun ManageNBInquiry(@Body body: RequestBody?): retrofit2.Call<NBResponse>

    @POST("MasterSub/DashBoardCountView")
    fun ManageDashboard(@Body body: RequestBody?): retrofit2.Call<DashboardResponse>

    @POST("MasterSub/DashboardInnerView")
    fun ManageDashboardInnerList(@Body body: RequestBody?): retrofit2.Call<DashboardInnerResponse>
}