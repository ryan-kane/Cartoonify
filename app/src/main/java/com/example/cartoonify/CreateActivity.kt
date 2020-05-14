package com.example.cartoonify

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.cartoonify.ShowPhoto
import kotlinx.android.synthetic.main.activity_create.*
import java.io.File
import java.io.IOException
import java.util.*

private const val TAG = "CreateActivity"

class CreateActivity : AppCompatActivity(), SelectPhoto.OnPhotoSelectedListener {

    private var state: Int = NO_PHOTO_SELECTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)
        val fragMan = supportFragmentManager
        val fragTrans = fragMan.beginTransaction()

        val selectPhotoFragment = SelectPhoto.newInstance()

        fragTrans.add(R.id.fragment_container, selectPhotoFragment)
        fragTrans.commit()
    }


    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        if (fragment is SelectPhoto) {
            fragment.setOnPhotoSelectedListener(this)
        }
    }

    override fun onPhotoConfirmed(photo_uri: Uri) {
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

    override fun onPhotoSelected(photo_uri: Uri) {
        Log.d(TAG, "Photo Selected")

        var showPhotoFragment: ShowPhoto? = supportFragmentManager.findFragmentById(R.id.ShowPhoto) as ShowPhoto?

        if (showPhotoFragment != null) {
            showPhotoFragment.showPhoto(photo_uri)
        } else {
            val fragMan = supportFragmentManager
            val fragTrans = fragMan.beginTransaction()

            showPhotoFragment = ShowPhoto.newInstance(photo_uri.path)
            fragTrans.replace(R.id.fragment_container, showPhotoFragment)
            fragTrans.addToBackStack(null)
            fragTrans.commit()
        }

        state = PHOTO_SELECTED
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
