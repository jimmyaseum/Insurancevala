package com.app.insurancevala.adapter

import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.activity.AddBrochureActivity
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.pojo.DocumentsModel
import com.app.insurancevala.utils.gone
import com.app.insurancevala.utils.toast
import com.app.insurancevala.utils.visible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.devs.readmoreoption.ReadMoreOption
import kotlinx.android.synthetic.main.adapter_multiple_attachment_list.view.*
import java.io.File
import java.net.URL
import java.util.Locale


class BrochureMultipleAttachmentListAdapter(
    private val mContext: Context,
    private val arrayList: ArrayList<DocumentsModel>?,
    private val recyclerItemClickListener: RecyclerClickListener,
    private val remove: Boolean
) : RecyclerView.Adapter<BrochureMultipleAttachmentListAdapter.ViewHolder>() {

    var selectedItemPosition = -1

    // Declare a DownloadCompleteReceiver
    private val downloadCompleteReceiver = DownloadCompleteReceiver(mContext)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_multiple_attachment_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(mContext, remove, arrayList!![position], recyclerItemClickListener, position)
        // Register the receiver
        mContext.registerReceiver(
            downloadCompleteReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(
            mContext: Context,
            remove: Boolean,
            model: DocumentsModel,
            recyclerItemClickListener: RecyclerClickListener,
            position: Int
        ) {
            if (!model.AttachmentURL.isNullOrEmpty()) {
                if (!model.AttachmentName.isNullOrEmpty()) {
                    itemView.txtname.text = model.AttachmentName
                }

                // PDF
                if (model.AttachmentURL.contains(".pdf")) {
                    itemView.imgFile.setImageResource(R.drawable.pdficon)
                }
                // Excel
                else if (model.AttachmentURL.contains(".xls")) {
                    itemView.imgFile.setImageResource(R.drawable.excel_icon)
                }
                // Image
                else if (model.AttachmentURL.endsWith(".jpg") || model.AttachmentURL.endsWith(".jpeg") || model.AttachmentURL.endsWith(
                        ".png"
                    ) || model.AttachmentURL.endsWith(".gif")
                ) {
                    Glide.with(mContext).load(model.AttachmentURL).apply(
                            RequestOptions().placeholder(R.drawable.ic_profile)
                                .error(R.drawable.ic_profile)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                        ).into(itemView.imgFile)
                }
                // Other file types
                else {
                    // Show document icon
                    itemView.imgFile.setImageResource(R.drawable.ic_file)
                }
            }

            if (model.ID != null && model.ID != 0) {
                itemView.img_Share.visible()
                itemView.img_Download.visible()
            } else {
                itemView.img_Share.gone()
                itemView.img_Download.gone()
            }

            itemView.txtname.setText(model.AttachmentName)

            if (remove) {
                itemView.imgRemove.visible()
            } else {
                itemView.imgRemove.gone()

                val readMoreOption = ReadMoreOption.Builder(mContext)
                    .textLength(20, ReadMoreOption.TYPE_CHARACTER)
                    .moreLabel("More")
                    .lessLabel("Less")
                    .moreLabelColor(mContext.getColor(R.color.color5))
                    .lessLabelColor(mContext.getColor(R.color.color5))
                    .labelUnderLine(true)
                    .expandAnimation(true)
                    .build()

                readMoreOption.addReadMoreTo(itemView.txtname, model.AttachmentName)
            }

            itemView.imgRemove.setOnClickListener {
                if (model.ID != null && model.ID != 0) {
                    recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 1)
                } else {
                    recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 0)
                }
            }

            itemView.img_Download.setOnClickListener {
                val fileName = model.AttachmentName
                if (isFileDownloaded(fileName!!, mContext)) {
                    Toast.makeText(mContext, "File Already Downloaded", Toast.LENGTH_SHORT).show()
                } else {
                    val downloadManager =
                        mContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    val downloadUri = Uri.parse(model.AttachmentURL)

                    val request = DownloadManager.Request(downloadUri).setAllowedNetworkTypes(
                            DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE
                        ).setAllowedOverRoaming(false).setTitle(model.AttachmentName)
                        .setDescription("Downloading ${model.AttachmentName}")
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_DOWNLOADS, fileName // Use the unique filename
                        )

                    // Enqueue the download request
                    downloadManager.enqueue(request)
                }
            }

            itemView.img_Share.setOnClickListener {
                val fileName = model.AttachmentName
                if (isFileDownloaded(fileName!!, mContext)) {
                    // Share the downloaded file
                    val file = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        fileName
                    )

                    // Create a Uri from the file
                    val uri = FileProvider.getUriForFile(
                        mContext, mContext.applicationContext.packageName + ".provider", file
                    )

                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "*/*"
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                    try {
                        mContext.startActivity(Intent.createChooser(shareIntent, "Share via"))
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(mContext, "No app available to share", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(mContext, "File not downloaded yet", Toast.LENGTH_SHORT).show()
                }
            }

            itemView.rlItemContent.setOnClickListener {
                if (model.ID != null && model.ID != 0) {
                    if (!model.AttachmentURL.isNullOrEmpty()) {
                        if (model.AttachmentURL!!.contains(".pdf")) {
                            var format = "https://docs.google.com/gview?embedded=true&url=%s"
                            val fullPath: String =
                                java.lang.String.format(Locale.ENGLISH, format, model.AttachmentURL)
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fullPath))
                            mContext.startActivity(browserIntent)
                        } else {
                            val browserIntent =
                                Intent(Intent.ACTION_VIEW, Uri.parse(model.AttachmentURL))
                            mContext.startActivity(browserIntent)
                        }

                    }
                }
            }
        }

        fun isFileDownloaded(fileName: String, context: Context): Boolean {
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                fileName
            )
            return file.exists()
        }
    }

    fun removeItem(position: Int) {
        if (!arrayList.isNullOrEmpty()) {
            arrayList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun getSelectedPosition(): Int {
        return selectedItemPosition
    }

    fun addItem(name: String, uri: Uri, attachmentType: Int) {
        val newItem = DocumentsModel(
            AttachmentName = name,
            AttachmentURL = uri.toString(),
            AttachmentType = if (attachmentType == 1) "Document" else "Image"
        )
        arrayList?.add(newItem)
        notifyDataSetChanged()
    }

    // Define a BroadcastReceiver class to handle download completion
    class DownloadCompleteReceiver(private val context: Context) : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            context!!.toast("Download completed", Toast.LENGTH_SHORT)
        }
    }
}
