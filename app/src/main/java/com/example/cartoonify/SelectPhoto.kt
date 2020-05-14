package com.example.cartoonify

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.core.content.FileProvider
import androidx.loader.content.CursorLoader
import androidx.navigation.fragment.findNavController
import java.io.*
import java.nio.channels.FileChannel
import java.util.*
import kotlin.math.log

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
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
                    takePictureIntent ->
                takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (e: IOException) {
                        null
                    }

                    if(photoFile == null) {
                        Log.d(TAG, "null photo file")
                    } else {
                        Log.d(TAG, "Not null photo file")
                    }
                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                            requireActivity(), "com.example.cartoonify.fileprovider", it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, TAKE_PHOTO)
                    }
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode){
                TAKE_PHOTO -> {
                    val photoUri = Uri.parse(photoPath)
                    callback.onPhotoSelected(photoUri)
                }
                SELECT_PHOTO_FROM_DEVICE -> {
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (e: IOException) {
                        null
                    }
                    photoFile?.also {
                        val photoUri: Uri = FileProvider.getUriForFile(
                            requireActivity(),
                            BuildConfig.APPLICATION_ID + ".fileprovider",
                            it
                        )
                        val selectedPhotoUri = data?.data

                        try {
                            val selectedContent: InputStream? =
                                requireActivity().contentResolver.openInputStream(selectedPhotoUri!!)
                            selectedContent.use {
                                selected_content ->
                                val content = FileOutputStream(photoFile)
                                content.use {
                                    out ->
                                    val buffer = ByteArray(4 * 1024)
                                    while (true) {
                                        val byteCount = selected_content?.read(buffer)
                                        if (byteCount == null || byteCount < 0) break
                                        out.write(buffer, 0, byteCount)
                                    }
                                }
                            }



                        } catch (e: IOException) {
                            Log.e(TAG, "Could not get selected photo" + e)
                        }
                    }
                    val photoUri = Uri.parse(photoPath)
                    callback.onPhotoSelected(photoUri)
                }
            }
        }
    }

    fun setOnPhotoSelectedListener(callback: OnPhotoSelectedListener) {
        this.callback = callback
    }

    interface OnPhotoSelectedListener {
        fun onPhotoSelected(photo_uri: Uri)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timestamp: String = SimpleDateFormat("yyyMMdd_HHmmss").format(Date())
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
