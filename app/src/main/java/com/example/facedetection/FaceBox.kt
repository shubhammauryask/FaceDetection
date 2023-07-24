package com.example.facedetection

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.google.mlkit.vision.face.Face

class FaceBox (
    overlay: FaceBoxOverlay,
    private var face:Face,
    private var imageRect: Rect,

    ):FaceBoxOverlay.FaceBox(overlay){

    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 6.0f
    }

    override fun draw(canvas: Canvas?) {
       val rect = getBoxRect(
           imageRectWidth = imageRect.width().toFloat(),
           imageRectHeight = imageRect.height().toFloat(),
           faceBoundingBox = face.boundingBox
       )
        canvas?.drawRect(rect,paint)
    }

}