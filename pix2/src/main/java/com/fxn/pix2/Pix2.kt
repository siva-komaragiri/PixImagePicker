package com.fxn.pix2

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.video.OnVideoSavedCallback
import androidx.camera.view.video.OutputFileOptions
import androidx.camera.view.video.OutputFileResults
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.fxn.pix2.databinding.ActivityPix2Binding
import com.fxn.utils.*
import java.io.OutputStream
import java.util.concurrent.Executors

class Pix2 : AppCompatActivity() {
    private lateinit var binding: ActivityPix2Binding

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPix2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        var w = window
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        val executor = Executors.newSingleThreadExecutor()
        val cameraController = LifecycleCameraController(this)
        cameraController.setEnabledUseCases(CameraController.IMAGE_CAPTURE)
        cameraController.bindToLifecycle(this)
        binding.preview.controller = cameraController
        setupStatusBarHidden(this)
        hideStatusBar(this)

        cameraController.imageCaptureFlashMode = ImageCapture.FLASH_MODE_AUTO
        cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        binding.one.setOnClickListener {
            showStatusBar(this)
            cameraProviderFuture.get().unbindAll()
            cameraController.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        }
        binding.two.setOnClickListener {
            hideStatusBar(this)
            cameraProviderFuture.get().unbindAll()
            cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        }
        binding.three.setOnClickListener {
            var photoFile = createFile(Environment.DIRECTORY_PICTURES, "jpg")
            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
            cameraController.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onError(error: ImageCaptureException) {
                            Log.e("image error", "${error.localizedMessage}")
                            // insert your code here.
                        }

                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            Log.e("image saved", "${outputFileResults.savedUri}")
                            val imageOutStream: OutputStream
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                val values = ContentValues().apply {
                                    put(MediaStore.Images.Media.DISPLAY_NAME, photoFile.name)
                                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                                }

                                contentResolver.run {
                                    val uri: Uri =
                                            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
                                    imageOutStream = openOutputStream(uri)!!
                                    imageOutStream.use { it.write(photoFile.readBytes()) }
                                    photoFile.delete()
                                }
                            } else {
                                val savedUri = Uri.fromFile(photoFile)
                                val msg = "Photo capture succeeded: $savedUri"
                                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                                Log.e("TAG", msg)
                            }


                        }
                    })

        }
        binding.three.setOnLongClickListener {
            cameraController.setEnabledUseCases(CameraController.VIDEO_CAPTURE)
            var videoFile = createFile(Environment.DIRECTORY_MOVIES, "mp4")
            val outputFileOptions = OutputFileOptions.builder(videoFile).build()
            cameraController.startRecording(
                    outputFileOptions,
                    ContextCompat.getMainExecutor(this@Pix2),
                    object : OnVideoSavedCallback {
                        override fun onVideoSaved(outputFileResults: OutputFileResults) {
                            val savedUri = Uri.fromFile(videoFile)
                            val msg = "Video capture succeeded: $savedUri"
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                            Log.e("TAG", msg)
                        }


                        override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                            Log.e("onError", message + "  ->  " + cause?.localizedMessage)
                        }
                    },
            )
            true
        }
        binding.three.setOnTouchListener(OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP && cameraController.isRecording) {
                cameraController.stopRecording()
                cameraController.setEnabledUseCases(CameraController.IMAGE_CAPTURE)
            }
            false
        })


        val handler = Handler(Looper.getMainLooper())

        executor.execute {

            handler.post {

            }
        }
    }

    companion object {
        fun start(context: FragmentActivity, options: Options) {
            val i = Intent(context, Pix2::class.java)
            i.putExtra(PIX_OPTIONS, options)
            context.startActivityForResult(i, options.requestCode)
        }

        fun start(context: FragmentActivity, requestCode: Int) {
            start(context, Options(requestCode).apply { count = 1 })
        }
    }
}

private fun Window.showFullscreen() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
    }
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
        @Suppress("DEPRECATION")
        decorView.systemUiVisibility = FLAGS_FULLSCREEN
    } else {
        setDecorFitsSystemWindows(false)
    }

}

private fun Window.hideFullscreen() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
    }
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
        @Suppress("DEPRECATION")
        decorView.systemUiVisibility = FLAGS_FULLSCREEN
    } else {
        setDecorFitsSystemWindows(true)
    }

}

