package com.app.insurancevala.retrofit

import com.app.insurancevala.model.api.AppVersion
import com.app.insurancevala.model.api.CommonResponse
import com.app.insurancevala.model.api.LeadCountResponse
import com.app.insurancevala.model.pojo.DocumentsResponse
import com.app.insurancevala.model.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * Created by Jimmy
 */
interface ApiInterface {

    @POST("Setting/VersionApi")
    fun getAppVersion(@Body body: RequestBody?): retrofit2.Call<AppVersion>

    // AI005 Login
    @POST("Master/ManageUserLogin")
    fun login(@Body body: RequestBody?): retrofit2.Call<LoginResponse>

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

    @POST("Leads/ClientListById")
    fun ManageFamilyList(@Body body: RequestBody?): retrofit2.Call<FamilyResponse>

    @POST("Master/ManageInitial")
    fun ManageInitial(@Body body: RequestBody?): retrofit2.Call<InitialResponse>

    @POST("Leads/LeadFamilyDetailsDelete")
    fun deleteFamilyDetail(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("Master/ManageUsers")
    fun ManageUsers(@Body body: RequestBody?): retrofit2.Call<UserResponse>

    @Multipart
    @POST("MasterSub/UserImageUpdate") // done
    fun ManageUserImage(
        @Part("UserGUID") ReferenceGUID: RequestBody?,
        @Part attachment: ArrayList<MultipartBody.Part>?
    ): retrofit2.Call<UserImageResponse>

    @Multipart
    @POST("Leads/LeadImageUpdate") // done
    fun ManageLeadImage(
        @Part("LeadGUID") ReferenceGUID: RequestBody?,
        @Part attachment: ArrayList<MultipartBody.Part>?
    ): retrofit2.Call<LeadImageResponse>

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

    @GET("MasterSub/TotalLeadCount")
    fun ManageLeadCount(): retrofit2.Call<LeadCountResponse>

    @POST("Master/ManageLead")
    fun ManageLead(@Body body: RequestBody?): retrofit2.Call<LeadResponse>

    @POST("Master/ManageMeeting_New")
    fun ManageMeetings(@Body body: RequestBody?): retrofit2.Call<MeetingsResponse>

    @Multipart
    @POST("MasterSub/InsertAttachment") // done
    fun ManageAttachment(
        @Part("LeadID") LeadID: Int?,
        @Part("ReferenceGUID") ReferenceGUID: RequestBody?,
        @Part("NBInquiryTypeID") NBInquiryTypeID: RequestBody?,
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
        @Part("NBInquiryTypeID")NBInquiryTypeID: Int?,
        @Part("AttachmentName") AttachmentName: RequestBody?,
        @Part attachment: ArrayList<MultipartBody.Part>?
    ): retrofit2.Call<CommonResponse>

    @POST("NBInquiry/NBInquiryFindAllActive")
    fun ManageNBInquiryFindAllActive(@Body body: RequestBody?): retrofit2.Call<NBResponse>

    @POST("NBInquiry/NBInquiryInsert")
    fun ManageNBInquiryInsert(@Body body: RequestBody?): retrofit2.Call<NBInquiryTypeAddUpdateResponse>

    @POST("NBInquiry/NBInquiryUpdate")
    fun ManageNBInquiryUpdate(@Body body: RequestBody?): retrofit2.Call<NBInquiryTypeAddUpdateResponse>

    @POST("NBInquiry/NBInquiryByGUID")
    fun ManageNBInquiryByGUID(@Body body: RequestBody?): retrofit2.Call<NBInquiryByGUIDResponse>

    @POST("NBInquiry/NBInquiryTypeUpdate")
    fun ManageNBInquiryTypeUpdate(@Body body: RequestBody?): retrofit2.Call<NBInquiryTypeAddUpdateResponse>

    @POST("NBInquiry/NBInquiryTypeByGUID")
    fun ManageNBInquiryTypeByGUID(@Body body: RequestBody?): retrofit2.Call<NBInquiryTypeByGUIDResponse>

    @POST("NBInquiry/NBInquiryFindByID")
    fun ManageNBInquiryFindByID(@Body body: RequestBody?): retrofit2.Call<NBInquiryByGUIDResponse>

    @POST("MasterSub/DashBoardCountView")
    fun ManageDashboard(@Body body: RequestBody?): retrofit2.Call<DashboardResponse>

    @POST("MasterSub/DashboardInnerView")
    fun ManageDashboardInnerList(@Body body: RequestBody?): retrofit2.Call<DashboardInnerResponse>

    @POST("Category/CategoryFindAll")
    fun ManageCategoryFindAll(@Body body: RequestBody?): retrofit2.Call<CategoryTypeResponse>

    @POST("Category/CategoryDelete")
    fun ManageCategoryDelete(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("Category/CategoryInsert")
    fun ManageCategoryInsert(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("Category/CategoryUpdate")
    fun ManageCategoryUpdate(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("Category/CategoryFindByID")
    fun ManageCategoryFindByID(@Body body: RequestBody?): retrofit2.Call<CategoryTypeByGUIDResponse>

    @POST("Company/CompanyFindAll")
    fun ManageCompanyFindAll(@Body body: RequestBody?): retrofit2.Call<CompanyResponse>

    @POST("Company/CompanyDelete")
    fun ManageCompanyDelete(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("Company/CompanyInsert")
    fun ManageCompanyInsert(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("Company/CompanyUpdate")
    fun ManageCompanyUpdate(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @GET("Company/CompanyFindAllActive")
    fun getCompanyAllActive(): Call<CompanyResponse>

    @POST("Company/CompanyFindByID")
    fun ManageCompanyFindByID(@Body body: RequestBody?): retrofit2.Call<CompanyByGUIDResponse>

    @POST("PlanBrochure/FindAllCompanyIdByPlan")
    fun ManagePlanBrochure(@Body body: RequestBody?): retrofit2.Call<PlanResponse>

    @POST("BrochureAttachments/BrochureAttachmentsDelete")
    fun ManageBrochureDelete(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @Multipart
    @POST("BrochureAttachments/InsertBrochureAttachments")
    fun ManageBrochureAttachments(
        @Part("PlanBrochureID")PlanBrochureID: Int?,
        @Part("AttachmentName") AttachmentName: RequestBody?,
        @Part attachment: ArrayList<MultipartBody.Part>?
    ): retrofit2.Call<CommonResponse>

    @POST("Occupation/OccupationFindAll")
    fun ManageOccupationFindAll(@Body body: RequestBody?): retrofit2.Call<OccupationTypeResponse>

    @GET("Occupation/OccupationFindAllActive")
    fun getOccupationAllActive(): Call<OccupationResponse>

    @POST("Occupation/OccupationDelete")
    fun ManageOccupationDelete(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("Occupation/OccupationInsert")
    fun ManageOccupationInsert(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("Occupation/OccupationUpdate")
    fun ManageOccupationUpdate(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("Occupation/OccupationFindByID")
    fun ManageOccupationFindByID(@Body body: RequestBody?): retrofit2.Call<OccupationTypeByGUIDResponse>

    @POST("Relation/RelationFindAll")
    fun ManageRelationFindAll(@Body body: RequestBody?): retrofit2.Call<RelationTypeResponse>

    @GET("Relation/RelationFindAllActive")
    fun getRelationAllActive(): Call<RelationResponse>

    @POST("Relation/RelationDelete")
    fun ManageRelationDelete(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("Relation/RelationInsert")
    fun ManageRelationInsert(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("Relation/RelationUpdate")
    fun ManageRelationUpdate(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("Relation/RelationFindByID")
    fun ManageRelationFindByID(@Body body: RequestBody?): retrofit2.Call<RelationTypeByGUIDResponse>

    @POST("Country/CountryFindAll")
    fun ManageCountryFindAll(@Body body: RequestBody?): retrofit2.Call<CountryResponse>

    @GET("Country/CountryFindAllActive")
    fun getCountryAllActive(): Call<CountryResponse>

    @POST("Country/CountryDelete")
    fun ManageCountryDelete(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("Country/CountryInsert")
    fun ManageCountryInsert(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("Country/CountryUpdate")
    fun ManageCountryUpdate(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("Country/CountryFindByID")
    fun ManageCountryFindByID(@Body body: RequestBody?): retrofit2.Call<CountryByGUIDResponse>

    @POST("State/StateFindAll")
    fun ManageStateFindAll(@Body body: RequestBody?): retrofit2.Call<StateResponse>

    @GET("State/StateFindAllActive")
    fun getStateAllActive(): Call<StateResponse>

    @POST("State/StateDelete")
    fun ManageStateDelete(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("State/StateInsert")
    fun ManageStateInsert(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("State/StateUpdate")
    fun ManageStateUpdate(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("State/StateFindByID")
    fun ManageStateFindByID(@Body body: RequestBody?): retrofit2.Call<StateByGUIDResponse>

    @POST("City/CityFindAll")
    fun ManageCityFindAll(@Body body: RequestBody?): retrofit2.Call<CityResponse>

    @GET("City/CityFindAllActive")
    fun getCityAllActive(): Call<CityResponse>

    @POST("City/CityDelete")
    fun ManageCityDelete(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("City/CityInsert")
    fun ManageCityInsert(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("City/CityUpdate")
    fun ManageCityUpdate(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("City/CityFindByID")
    fun ManageCityFindByID(@Body body: RequestBody?): retrofit2.Call<CityByGUIDResponse>

    @POST("LeadType/LeadTypeFindAll")
    fun ManageLeadTypeFindAll(@Body body: RequestBody?): retrofit2.Call<LeadTypeResponse>

    @GET("LeadType/LeadTypeFindAllActive")
    fun getLeadTypeAllActive(): Call<LeadTypeResponse>

    @POST("LeadType/LeadTypeDelete")
    fun ManageLeadTypeDelete(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("LeadType/LeadTypeInsert")
    fun ManageLeadTypeInsert(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("LeadType/LeadTypeUpdate")
    fun ManageLeadTypeUpdate(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("LeadType/LeadTypeFindByID")
    fun ManageLeadTypeFindByID(@Body body: RequestBody?): retrofit2.Call<LeadTypeByGUIDResponse>

    @POST("Note/NoteFindAll")
    fun ManageNoteFindAll(@Body body: RequestBody?): retrofit2.Call<NoteResponse>

    @GET("Note/NoteFindAllActive")
    fun getNoteAllActive(): Call<NoteResponse>

    @POST("Note/NoteDelete")
    fun ManageNoteDelete(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("Note/NoteInsert")
    fun ManageNoteInsert(@Body body: RequestBody?): retrofit2.Call<RefGUIDResponse>

    @POST("Note/NoteUpdate")
    fun ManageNoteUpdate(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("Note/NoteFindByID")
    fun ManageNoteFindByID(@Body body: RequestBody?): retrofit2.Call<NoteByGUIDResponse>

    @POST("Calls/CallsFindAll")
    fun ManageCallsFindAll(@Body body: RequestBody?): retrofit2.Call<CallsResponse>

    @GET("Calls/CallsFindAllActive")
    fun getCallsAllActive(): Call<CallsResponse>

    @POST("Calls/CallsDelete")
    fun ManageCallsDelete(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("Calls/CallsInsert")
    fun ManageCallsInsert(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("Calls/CallsUpdate")
    fun ManageCallsUpdate(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("Calls/CallsFindByID")
    fun ManageCallsFindByID(@Body body: RequestBody?): retrofit2.Call<CallsByGUIDResponse>

    @POST("Tasks/TasksFindAll")
    fun ManageTasksFindAll(@Body body: RequestBody?): retrofit2.Call<TasksResponse>

    @GET("Tasks/TasksFindAllActive")
    fun getTasksAllActive(): Call<TasksResponse>

    @POST("Tasks/TasksDelete")
    fun ManageTasksDelete(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("Tasks/TasksInsert")
    fun ManageTasksInsert(@Body body: RequestBody?): retrofit2.Call<RefGUIDResponse>

    @POST("Tasks/TasksUpdate")
    fun ManageTasksUpdate(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("MasterSub/TaskStatusUpdate")
    fun ManageTaskStatusUpdate(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("Tasks/TasksFindByID")
    fun ManageTasksFindByID(@Body body: RequestBody?): retrofit2.Call<TasksByGUIDResponse>

    @POST("Meetings/MeetingsFindAll")
    fun ManageMeetingFindAll(@Body body: RequestBody?): retrofit2.Call<MeetingsResponse>

    @POST("Meetings/MeetingsDelete")
    fun ManageMeetingDelete(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("Meetings/MeetingsInsert")
    fun ManageMeetingInsert(@Body body: RequestBody?): retrofit2.Call<RefGUIDResponse>

    @POST("Meetings/MeetingsUpdate")
    fun ManageMeetingUpdate(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("Meetings/MeetingsFindByID")
    fun ManageMeetingFindByID(@Body body: RequestBody?): retrofit2.Call<MeetingsByGUIDResponse>

    @POST("Leads/LeadsFindAll")
    fun ManageLeadsFindAll(@Body body: RequestBody?): retrofit2.Call<LeadResponse>

    @POST("Leads/LeadsFindAllActive")
    fun LeadsFindAllActive(@Body body: RequestBody?): retrofit2.Call<LeadResponse>

    @POST("Leads/LeadsDelete")
    fun ManageLeadsDelete(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("Leads/LeadsInsert")
    fun ManageLeadsInsert(@Body body: RequestBody?): retrofit2.Call<RefGUIDResponse>

    @POST("Leads/LeadsUpdate")
    fun ManageLeadsUpdate(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("Leads/LeadsFindByID")
    fun ManageLeadsFindByID(@Body body: RequestBody?): retrofit2.Call<LeadByGUIDResponse>

    @GET("PlanBrochure/PlanBrochureFindAllActive")
    fun getPlanBrochureAllActive(): Call<PlanBrochuresResponse>

    @POST("PlanBrochure/PlanBrochureDelete")
    fun ManagePlanBrochureDelete(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("PlanBrochure/PlanBrochureInsert")
    fun ManagePlanBrochureInsert(@Body body: RequestBody?): retrofit2.Call<RefIDResponse>

    @POST("PlanBrochure/PlanBrochureFindByID")
    fun ManagePlanBrochureFindByID(@Body body: RequestBody?): retrofit2.Call<PlanBrochureByIDResponse>

    @POST("PlanBrochure/PlanBrochureUpdate")
    fun ManagePlanBrochureUpdate(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("Master/ManageUserChangePassword")
    fun ManageUserChangePassword(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("Master/ManageUserResetPassword")
    fun ManageUserResetPassword(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @POST("Master/ManageUserLogout")
    fun ManageUserLogout(@Body body: RequestBody?): retrofit2.Call<CommonResponse>

    @Multipart
    @POST("RecodingFiles/RecodingFilesInsert")
    fun ManageRecordingInsert(
        @Part("NBInquiryTypeID") NBInquiryTypeID: Int?,
        @Part("Title") Title: String?,
        @Part attachment: ArrayList<MultipartBody.Part>?

    ): retrofit2.Call<CommonResponse>

    @Multipart
    @POST("RecodingFiles/RecodingFilesImageUpdate")
    fun ManageRecordingUpdate(
        @Part("ID")ID: Int?,
        @Part("NBInquiryTypeID")NBInquiryTypeID: Int?,
        @Part("Title") Title: String?,
        @Part attachment: ArrayList<MultipartBody.Part>?
    ): retrofit2.Call<CommonResponse>

    @POST("RecodingFiles/RecodingFilesFindAll")
    fun ManageRecordingFindAll(@Body body: RequestBody?): retrofit2.Call<RecordingsResponse>

    @POST("RecodingFiles/RecodingFilesDelete")
    fun ManageRecordingDelete(@Body body: RequestBody?): retrofit2.Call<CommonResponse>
}