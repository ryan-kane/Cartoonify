package com.example.cartoonify

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.cartoonify.ExtractForeground.ExtractForegroundFragment
import com.example.cartoonify.Pixelate.PixelateFragment
import com.example.cartoonify.Vectorize.VectorizeImageFragment
import kotlinx.android.synthetic.main.activity_create.*
import org.opencv.android.OpenCVLoader


private const val TAG = "CreateActivity"

class CreateActivity :
    AppCompatActivity(),
    SelectPhoto.OnPhotoSelectedListener,
    ImageReadyListener{

    private var state = STATE.NO_PHOTO_SELECTED
    private var imBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        if(OpenCVLoader.initDebug()){
            Toast.makeText(applicationContext, "Success", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(applicationContext, "Failure", Toast.LENGTH_SHORT).show()
        }

        button_create_confirm.setOnClickListener(onConfirm)
        create_bottom_app_bar.setNavigationOnClickListener {
            onBackPressed()
        }

        hideConfirmButton()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SelectPhoto())
            .commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // decrement state
        if(state != STATE.values()[0]) {
            val nextStateIndex = STATE.values().indexOf(state) - 1
            state = STATE.values()[nextStateIndex]
            if (state == STATE.NO_PHOTO_SELECTED) {
                hideConfirmButton()
            }
        }
        // Update title
        title_pipeline.text = getString(state.title)
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        if (fragment is SelectPhoto) {
            fragment.setOnPhotoSelectedListener(this)
        }
    }

    val onConfirm = View.OnClickListener {
        // assuming this button was only enabled when the image pro
        Log.d(TAG, "Confirm Button Pressed")
        var nextFragment: Fragment? = null
        hideConfirmButton()

        // Update state
        incrementState()

        // flow of image manipulation
        nextFragment = when(state) {
            STATE.EXTRACT_FOREGROUND -> {
                // continue to extract foreground fragment
                ExtractForegroundFragment.newInstance(this, imBitmap!!)
            }
            STATE.PIXELATE_IMAGE -> {
                PixelateFragment.newInstance(this, imBitmap!!)
            }
            STATE.VECTORIZE_IMAGE -> {
                VectorizeImageFragment.newInstance(this, imBitmap!!)
            }
            else ->
                // default
                null
        }
        if(nextFragment != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, nextFragment)
                .addToBackStack(null)
                .commit()
        }

    }

    override fun onPhotoSelected(imBitmap: Bitmap) {
        Log.d(TAG, "Photo Selected")
        val displayImageFragment = DisplayImageFragment.newInstance(this, imBitmap)
        state = STATE.SELECT_PHOTO
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, displayImageFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun incrementState() {
        Log.d(TAG, "${STATE.values().indexOf(state)}")
        val nextStateIndex = STATE.values().indexOf(state) + 1
        state = STATE.values()[nextStateIndex]

        // Update title
        title_pipeline.text = getString(state.title)
    }

    override fun imageReady(imBitmap: Bitmap) {
        this.imBitmap = imBitmap
        showConfirmButton()
    }

    override fun imageNotReady() {
        hideConfirmButton()
    }

    private fun showConfirmButton() {
        button_create_confirm.show()
    }

    private fun hideConfirmButton() {
        button_create_confirm.hide()
    }


    enum class STATE(@StringRes val title: Int){
        NO_PHOTO_SELECTED(R.string.pipeline_title_display_image),
        SELECT_PHOTO(R.string.pipeline_title_display_image),
        EXTRACT_FOREGROUND(R.string.pipeline_title_extract_foreground),
        PIXELATE_IMAGE(R.string.pipeline_title_pixelate),
        VECTORIZE_IMAGE(R.string.pipeline_title_vectorize),
        IMAGE_FINISHED(R.string.pipeline_title_display_image),
    }
}
