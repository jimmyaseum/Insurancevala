package com.app.insurancevala.activity.ActivityLog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.ActivityLogModel
import com.app.insurancevala.utils.AppConstant
import com.app.insurancevala.utils.convertDateStringToString
import com.app.insurancevala.utils.gone
import com.app.insurancevala.utils.visible
import kotlinx.android.synthetic.main.adapter_inquiry_type_attachment.view.*
import kotlinx.android.synthetic.main.adapter_inquiry_type_call.view.*
import kotlinx.android.synthetic.main.adapter_inquiry_type_meeting.view.*
import kotlinx.android.synthetic.main.adapter_inquiry_type_note.view.*
import kotlinx.android.synthetic.main.adapter_inquiry_type_recording.view.*
import kotlinx.android.synthetic.main.adapter_inquiry_type_task.view.*

class ParticularInquiryAdapter(
    private val arrayList: ArrayList<ActivityLogModel>,
    private val recyclerItemClickListener: RecyclerClickListener,
    private val tabPosition: Int
) : RecyclerView.Adapter<ParticularInquiryAdapter.BaseViewHolder<*>>() {

    var context: Context? = null

    companion object {
        private const val TYPE_CALL = 1
        private const val TYPE_MEETING = 2
        private const val TYPE_NOTE = 3
        private const val TYPE_TASK = 4
        private const val TYPE_RECORDING = 5
        private const val TYPE_ATTACHMENT = 6
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        context = parent.context
        return when (viewType) {
            TYPE_CALL -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.adapter_inquiry_type_call, parent, false)
                CallViewHolder(view)
            }

            TYPE_MEETING -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.adapter_inquiry_type_meeting, parent, false)
                MeetingViewHolder(view)
            }

            TYPE_NOTE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.adapter_inquiry_type_note, parent, false)
                NoteViewHolder(view)
            }

            TYPE_TASK -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.adapter_inquiry_type_task, parent, false)
                TaskViewHolder(view)
            }

            TYPE_RECORDING -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.adapter_inquiry_type_recording, parent, false)
                RecordingViewHolder(view)
            }

            TYPE_ATTACHMENT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.adapter_inquiry_type_attachment, parent, false)
                AttachmentViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val item = arrayList!![position]

        var colors = arrayOf(R.drawable.bg_shadow_orange, R.drawable.bg_shadow_purple, R.drawable.bg_shadow_blue)
        val reminder = position % 3

        when (holder) {
            is CallViewHolder -> holder.bind(item, recyclerItemClickListener, tabPosition)
            is MeetingViewHolder -> holder.bind(item, recyclerItemClickListener, tabPosition)
            is NoteViewHolder -> holder.bind(item, recyclerItemClickListener, tabPosition)
            is TaskViewHolder -> holder.bind(item, recyclerItemClickListener, tabPosition)
            is RecordingViewHolder -> holder.bind(item, recyclerItemClickListener, tabPosition)
            is AttachmentViewHolder -> holder.bind(item, recyclerItemClickListener, tabPosition)
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T, recyclerItemClickListener: RecyclerClickListener, tabPosition: Int)
    }

    //----------------CallViewHolder | CallData------------
    inner class CallViewHolder(itemView: View) : BaseViewHolder<ActivityLogModel>(itemView) {

        override fun bind(item: ActivityLogModel, recyclerItemClickListener: RecyclerClickListener, tabPosition: Int) {
            // Use recyclerItemClickListener here
            itemView.txtCallTitle.text = "Title - " + item.ActivityTitle
            itemView.txtCallPurpose.text = "Purpose - " + item.ActivityPurpose
            itemView.txtCallResult.text = "Result - " + item.ActivityResult
            itemView.txtCallAgenda.text = "Agenda - " + item.ActivityAgenda
            itemView.txtCallType.text = "Type - " + item.ActivityType
            itemView.txtCallDescription.text = "Desc - " + item.ActivityDescription

            if (tabPosition == 0) {
                itemView.txtCallDate.text = item.FollowupDate
                itemView.txtCallLog.text = "Call - Follow Up"
                itemView.txtCallTime.text = "Time - " + convertDateStringToString(
                    item.FollowupTime!!,
                    AppConstant.HH_MM_SS_FORMAT,
                    AppConstant.HH_MM_AA_FORMAT
                )
                itemView.txtCallTime.visible()
            } else {
                itemView.txtCallDate.text = item.ActivityDate
                itemView.txtCallLog.text = "Call"
                itemView.txtCallTime.gone()
            }
        }
    }

    //----------------MeetingViewHolder | EmailData-------------
    inner class MeetingViewHolder(itemView: View) : BaseViewHolder<ActivityLogModel>(itemView) {

        override fun bind(item: ActivityLogModel, recyclerItemClickListener: RecyclerClickListener, tabPosition: Int) {
            // Use recyclerItemClickListener here
            itemView.txtMeetingPurpose.text = "Purpose - " + item.ActivityPurpose
            if (tabPosition == 0) {
                itemView.txtMeetingTime.text = "Time - " + convertDateStringToString(
                    item.FollowupTime!!,
                    AppConstant.HH_MM_SS_FORMAT,
                    AppConstant.HH_MM_AA_FORMAT
                )
            } else {
                itemView.txtMeetingTime.text = "Time - " + convertDateStringToString(
                    item.ActivityStartTime!!,
                    AppConstant.HH_MM_SS_FORMAT,
                    AppConstant.HH_MM_AA_FORMAT
                ) + " To " + convertDateStringToString(
                    item.ActivityEndTime!!,
                    AppConstant.HH_MM_SS_FORMAT,
                    AppConstant.HH_MM_AA_FORMAT
                )
            }
            itemView.txtMeetingLocation.text = "Location - " + item.ActivityLocation
            itemView.txtMeetingType.text = "Type - " + item.ActivityType
            itemView.txtMeetingOutcome.text = "OutCome - " + item.ActivityOutCome
            itemView.txtMeetingDescription.text = "Desc - " + item.ActivityDescription
            itemView.txtMeetingStutas.text = "Status - " + item.ActivityStutas
            itemView.txtMeetingAttendee.text = "Attendee - " + item.ActivityAttendee

            if (tabPosition == 0) {
                itemView.txtMeetingDate.text = item.FollowupDate
                itemView.txtMeetingLog.text = "Meeting - Follow Up"
            } else {
                itemView.txtMeetingDate.text = item.ActivityDate
                itemView.txtMeetingLog.text = "Meeting"
            }
            // Set click listener
            itemView.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 101)
            }
        }
    }

    //----------------NoteViewHolder | NoteData-------------
    inner class NoteViewHolder(itemView: View) : BaseViewHolder<ActivityLogModel>(itemView) {

        override fun bind(item: ActivityLogModel, recyclerItemClickListener: RecyclerClickListener, tabPosition: Int) {
            // Use recyclerItemClickListener here
            itemView.txtNoteDate.text = convertDateStringToString(item.CreatedOn!! , AppConstant.ddMMyyyy_HHmmss, AppConstant.HH_MM_AA_FORMAT)
            itemView.txtNoteTitle.text = "Title - " + item.ActivityTitle
            itemView.txtNoteDescription.text = "Desc - " + item.ActivityDescription

            // Set click listener
            itemView.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 102)
            }
        }
    }

    //----------------TaskViewHolder | TaskData-------------
    inner class TaskViewHolder(itemView: View) : BaseViewHolder<ActivityLogModel>(itemView) {

        override fun bind(item: ActivityLogModel, recyclerItemClickListener: RecyclerClickListener, tabPosition: Int) {
            // Use recyclerItemClickListener here
            itemView.txtTaskTitle.text = "Title - " + item.ActivityTitle
            itemView.txtTaskOwner.text = "Owner Name - " + item.ActivityOwnName
            itemView.txtTaskPriority.text = "Priority - " + item.Activitypriority
            itemView.txtTaskStutas.text = "Status - " + item.ActivityStutas
            itemView.txtTaskDescription.text = "Desc - " + item.ActivityDescription

            if (tabPosition == 0) {
                itemView.txtTaskDate.text = item.FollowupDate
                itemView.txtTaskLog.text = "Task - Reminder"
            } else {
                itemView.txtTaskDate.text = item.ActivityDate
                itemView.txtTaskLog.text = "Task"
            }

            // Set click listener
            itemView.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 103)
            }
        }
    }

    //----------------AttachmentViewHolder | AttachmentData-------------
    inner class AttachmentViewHolder(itemView: View) : BaseViewHolder<ActivityLogModel>(itemView) {

        override fun bind(item: ActivityLogModel, recyclerItemClickListener: RecyclerClickListener, tabPosition: Int) {
            // Use recyclerItemClickListener here
            itemView.txtAttachmentTitle.text = "Title - " + item.ActivityTitle
            itemView.txtAttachmentDate.text = item.ActivityDate!!

            itemView.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 104)
            }
        }
    }

    //----------------RecordingViewHolder | RecordingData-------------
    inner class RecordingViewHolder(itemView: View) : BaseViewHolder<ActivityLogModel>(itemView) {

        override fun bind(item: ActivityLogModel, recyclerItemClickListener: RecyclerClickListener, tabPosition: Int) {
            // Use recyclerItemClickListener here
            itemView.txtRecordingTitle.text = "Title - " + item.ActivityTitle
            itemView.txtRecordingDate.text = item.ActivityDate

            itemView.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 105)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val comparable = arrayList[position].ActivityLog

        return when (comparable) {
            "CallLog" -> TYPE_CALL
            "MeetingLog" -> TYPE_MEETING
            "NoteLog" -> TYPE_NOTE
            "TaskLog" -> TYPE_TASK
            "RecodingLog" -> TYPE_RECORDING
            "AttachmentLog" -> TYPE_ATTACHMENT
            else -> throw IllegalArgumentException("Invalid type of data $position")
        }
    }

}
