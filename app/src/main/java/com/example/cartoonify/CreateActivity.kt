package com.example.cartoonify

import android.R.attr.data
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.cartoonify.Pixelate.PixelateFragment
import org.opencv.android.OpenCVLoader


private const val TAG = "CreateActivity"

class CreateActivity : AppCompatActivity(), SelectPhoto.OnPhotoSelectedListener {

    private var state: Int = NO_PHOTO_SELECTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        if(OpenCVLoader.initDebug()){
            Toast.makeText(applicationContext, "Success", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(applicationContext, "Failure", Toast.LENGTH_SHORT).show()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SelectPhoto())
            .addToBackStack(null)
            .commit()
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        if (fragment is SelectPhoto) {
            fragment.setOnPhotoSelectedListener(this)
        }
    }

    fun onPhotoConfirmed(photo_uri: Uri) {
        Log.d(TAG, "Photo Confirmed")
        val fragMan = supportFragmentManager
        val fragTrans = fragMan.beginTransaction()
        var nextFragment: Fragment? = null
        when(state) {
            PHOTO_SELECTED ->
                // continue to extract foreground fragment
                nextFragment = null
            else ->
                // default
                nextFragment = null
        }
        if(nextFragment != null) {
            fragTrans.replace(R.id.fragment_container, nextFragment)
        }

    }

    override fun onPhotoSelected(imBitmap: Bitmap) {
        Log.d(TAG, "Photo Selected")

        // get bitmap from photo_uri
//        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        val pixelateFragment = PixelateFragment.newInstance(imBitmap)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, pixelateFragment)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        @JvmStatic val NO_PHOTO_SELECTED = 0
        @JvmStatic val PHOTO_SELECTED = 1
        @JvmStatic val FOREGROUND_EXTRACTED = 2
        @JvmStatic val PHOTO_PIXELATED = 3
        @JvmStatic val PHOTO_VECTORIZED = 4
        @JvmStatic val PHOTO_FINISHED = 5
    }
}
