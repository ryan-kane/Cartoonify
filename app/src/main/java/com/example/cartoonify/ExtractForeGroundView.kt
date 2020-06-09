package com.example.cartoonify

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.util.AttributeSet
import android.view.View

class ExtractForeGroundView(context: Context, attributeSet: AttributeSet):
    View(context, attributeSet)
{
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val foregroundColor = Color.BLUE
    private val backgroundColor = Color.RED

    private lateinit var photoUri: Uri

    fun setPhotoUri(photoUri: Uri) {
        this.photoUri = photoUri
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }
}