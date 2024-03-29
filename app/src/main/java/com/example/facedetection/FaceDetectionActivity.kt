package com.example.facedetection

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import com.example.facedetection.databinding.ActivityFaceDetectionBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.util.concurrent.Executors
import androidx.camera.view.PreviewView


class FaceDetectionActivity : AppCompatActivity() {
    private lateinit var binding:ActivityFaceDetectionBinding

    private lateinit var cameraSelector:CameraSelector
    private lateinit var processCameraProvider: ProcessCameraProvider
    private lateinit var cameraPreview: Preview
    private lateinit var imageAnalysis: ImageAnalysis

    private val cameraXViewModel = viewModels<CameraXViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaceDetectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()

        cameraXViewModel.value.processCameraProvider.observe(this){ provider ->
            processCameraProvider = provider
            bindCameraPreview()
            bindInputAnalyser()
        }
    }

     private fun bindCameraPreview(){
        cameraPreview = Preview.Builder()
            .setTargetRotation(binding.previewView.display.rotation)
            .build()
         cameraPreview.setSurfaceProvider(binding.previewView.surfaceProvider)
         try {
             processCameraProvider.bindToLifecycle(this,cameraSelector,cameraPreview)
         }catch (illegalStatException:IllegalStateException){
             Log.e(TAG,illegalStatException.message?:"IllegalStatException")
         }catch (illegalArgumentException:IllegalArgumentException){
             Log.e(TAG,illegalArgumentException.message?:"IllegalArgumentException")
         }
    }

    private fun bindInputAnalyser(){
        val detector = FaceDetection.getClient(
            FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
                .build()
        )

        imageAnalysis = ImageAnalysis.Builder()
            .setTargetRotation(binding.previewView.display.rotation)
            .build()

        val  cameraExecutor = Executors.newSingleThreadExecutor()
        imageAnalysis.setAnalyzer(cameraExecutor){ imageProxy ->
            processImageProxy(detector,imageProxy)
        }
        try {
            processCameraProvider.bindToLifecycle(this,cameraSelector,imageAnalysis)
        }catch (illegalStatException:IllegalStateException){
            Log.e(TAG,illegalStatException.message?:"IllegalStatException")
        }catch (illegalArgumentException:IllegalArgumentException){
            Log.e(TAG,illegalArgumentException.message?:"IllegalArgumentException")
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun processImageProxy(
        detector: com.google.mlkit.vision.face.FaceDetector,
        imageProxy: ImageProxy
    ){
        val inputImage  = InputImage.fromMediaImage(imageProxy.image!!,imageProxy.imageInfo.rotationDegrees)
        detector.process(inputImage)
            .addOnSuccessListener { faces ->
               binding.faceBoxOverlay.clear()

                faces.forEach{face ->
                   val box = FaceBox(binding.faceBoxOverlay,face,imageProxy.cropRect)
                  binding.faceBoxOverlay.add(box)
                }
            }
            .addOnFailureListener{
                Log.e(TAG,it.message ?:it.toString())
            }.addOnCompleteListener{
                imageProxy.close()
            }

    }
    companion object   {
        private val TAG = FaceDetectionActivity::class.simpleName
        fun start(context: Context){
            Intent(context,FaceDetectionActivity::class.java).also {
                context.startActivity(it)
            }
        }
    }
}