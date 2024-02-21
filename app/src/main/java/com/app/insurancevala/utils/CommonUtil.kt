package com.app.insurancevala.utils

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import com.app.insurancevala.R
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

object CommonUtil {



    fun dialCall(context: Context?, number: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(number)))
        context?.startActivity(intent)
    }

    fun sendEmail(context: Context?, recipient: String, subject: String, message: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.data = Uri.parse("mailto:")
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, message)

        try {
            context?.startActivity(Intent.createChooser(intent, "Choose Email Client..."))
        } catch (e: Exception) {
            e.printStackTrace()
            context?.toast(e.message.toString(), AppConstant.TOAST_SHORT)
        }
    }

    fun createPartFromString(descriptionString: String): RequestBody {
        return RequestBody.create(
            MultipartBody.FORM, descriptionString
        )
    }

    fun prepareFilePart(context: Context?, mimeType: String, partName: String, fileUri: Uri): MultipartBody.Part {
        val file = File(FilePickUtils.getPath(context!!, fileUri))

        // create RequestBody instance from file
        val requestFile = RequestBody.create(MediaType.parse(mimeType), file)

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }


}