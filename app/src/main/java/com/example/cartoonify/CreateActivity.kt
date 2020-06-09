package com.example.cartoonify

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.cartoonify.Pixelate.PixelateFragment
import kotlinx.android.synthetic.main.activity_create.*
import org.opencv.android.OpenCVLoader


private const val TAG = "CreateActivity"

class CreateActivity :
    AppCompatActivity(),
    SelectPhoto.OnPhotoSelectedListener,
    ImageReadyListener{

    private lateinit var viewPager: ViewPager2

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
            if(supportFragmentManager.backStackEntryCount > 0) {
                onBackPressed()
            }
        }

        hideConfirmButton()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SelectPhoto())
            .commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
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
        // flow of image manipulation
        when(state) {
            STATE.PHOTO_SELECTED ->
                // continue to extract foreground fragment
                nextFragment = PixelateFragment.newInstance(this, imBitmap!!)
            STATE.IMAGE_PIXELATED ->
                nextFragment = null
            else ->
                // default
                nextFragment = null
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
        state = STATE.PHOTO_SELECTED
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, displayImageFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun imageReady(imBitmap: Bitmap) {
        this.imBitmap = imBitmap
        showConfirmButton()
    }

    fun showConfirmButton() {
        button_create_confirm.show()
    }

    fun hideConfirmButton() {
        button_create_confirm.hide()
    }


    private enum class STATE(){
        NO_PHOTO_SELECTED,
        PHOTO_SELECTED,
        FOREGROUND_EXTRACTED,
        IMAGE_PIXELATED,
        IMAGE_VECTORIZED,
        IMAGE_FINISHED,
    }
}
