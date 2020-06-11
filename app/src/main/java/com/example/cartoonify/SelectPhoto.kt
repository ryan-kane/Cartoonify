package com.example.cartoonify

import android.Manifest
import android.R.attr.bitmap
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.icu.text.SimpleDateFormat
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.PathUtils
import androidx.fragment.app.Fragment
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.URI
import java.util.*


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */

private const val TAG: String = "SelectPhoto"

private const val SELECT_PHOTO_FROM_DEVICE = 1
private const val TAKE_PHOTO = 2


class SelectPhoto : Fragment() {

    internal lateinit var callback: OnPhotoSelectedListener

    lateinit var photoPath: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_photo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_select_photo).setOnClickListener {
            Intent(Intent.ACTION_PICK).also {
                    selectPhotoIntent ->
                selectPhotoIntent.type = "image/*"
                selectPhotoIntent.resolveActivity(requireActivity().packageManager)?.also {

                    startActivityForResult(selectPhotoIntent,
                        SELECT_PHOTO_FROM_DEVICE
                    )
                }
            }
        }

        view.findViewById<Button>(R.id.button_take_photo).setOnClickListener {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                    startActivityForResult(takePictureIntent, TAKE_PHOTO)
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            var bitmap: Bitmap? = null
            var fileBitmap: Bitmap? = null
            var orientation: Int? = null

            when (requestCode) {
                TAKE_PHOTO -> {
                    fileBitmap = data!!.extras!!.get("data") as Bitmap
                    orientation = 90
                }
                SELECT_PHOTO_FROM_DEVICE -> {
                    fileBitmap = MediaStore.Images.Media.getBitmap(
                        requireActivity().contentResolver,
                        data?.data
                    )
                    val orientationPathCol = arrayOf(MediaStore.Images.Media.ORIENTATION)
                    val cur = requireContext().contentResolver.query(
                        data?.data!!,
                        orientationPathCol,
                        null,
                        null
                    )
                    cur?.moveToFirst()
                    val imOrientation = cur?.getString(cur.getColumnIndex(orientationPathCol[0]))
                    cur?.close()
                    if(imOrientation != null) {
                        orientation = imOrientation.toInt()
                    }
                    Log.d(TAG, "$imOrientation")
                }
            }
            val matrix = Matrix()
            when(orientation) {
                90 -> {
                    matrix.setRotate(90F)
                    bitmap = Bitmap.createBitmap(
                        fileBitmap!!,
                        0,
                        0,
                        fileBitmap.width,
                        fileBitmap.height,
                        matrix,
                        true
                    )
                }
                else -> {
                    bitmap = fileBitmap
                }
            }
            if(bitmap != null) {
                // properly rotate image

                callback.onPhotoSelected(bitmap)
            }
        }
    }

    fun setOnPhotoSelectedListener(callback: OnPhotoSelectedListener) {
        this.callback = callback
    }

    interface OnPhotoSelectedListener {
        fun onPhotoSelected(imBitmap: Bitmap)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timestamp: String = SimpleDateFormat("yyyMMdd_HHmmss", Locale.CANADA).format(Date())
        val storageDir: File? = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        val file =  File.createTempFile(
            "JPEG_${timestamp}_",
            ".jpg",
            storageDir).apply {
            photoPath = absolutePath
        }
        return file
    }

    companion object {
        @JvmStatic
        fun newInstance() = SelectPhoto()
    }

}
