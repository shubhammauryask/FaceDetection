package com.example.facedetection

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.example.facedetection.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val cameraPermission = android.Manifest.permission.CAMERA
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
       if (isGranted){
           //start face detection app
       }
    }
    private lateinit var binding: ActivityMainBinding
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            requestCameraAndStartFaceDetection()
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestCameraAndStartFaceDetection(){
        if(isPermissionGranted(cameraPermission)){
            //start face detection
            val intent = Intent(this,FaceDetectionActivity::class.java)
            startActivity(intent)
        }else{
            requestCameraPermission()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestCameraPermission() {
        when{
            shouldShowRequestPermissionRationale(cameraPermission) ->{
                cameraPermissionRequest {
                    openPermissionSetting()
                }
            } else-> {
                requestPermissionLauncher.launch(cameraPermission)
            }
        }
    }
}