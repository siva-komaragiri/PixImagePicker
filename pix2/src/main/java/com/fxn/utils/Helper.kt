package com.fxn.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author : Akshay Sharma
 * @since : 31/1/21, Sun
 * akshay2211.github.io
 **/

fun saveImageToStorage(
        bitmap: Bitmap,
        filename: String = "screenshot.jpg",
        mimeType: String = "image/jpeg",
        directory: String = Environment.DIRECTORY_PICTURES,
        mediaContentUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentResolver: ContentResolver
) {
    val imageOutStream: OutputStream
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
            put(MediaStore.Images.Media.RELATIVE_PATH, directory)
        }

        contentResolver.run {
            val uri =
                    contentResolver.insert(mediaContentUri, values)
                            ?: return
            imageOutStream = openOutputStream(uri) ?: return
        }
    } else {
        @Suppress("DEPRECATION")
        val imagePath = Environment.getExternalStoragePublicDirectory(directory).absolutePath
        val image = File(imagePath, filename)
        imageOutStream = FileOutputStream(image)
    }

    imageOutStream.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
}

fun showStatusBar(appCompatActivity: AppCompatActivity) {
    synchronized(appCompatActivity) {
        appCompatActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}

fun hideStatusBar(appCompatActivity: AppCompatActivity) {
    synchronized(appCompatActivity) {
        appCompatActivity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            appCompatActivity.window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
    }
}

fun setupStatusBarHidden(appCompatActivity: AppCompatActivity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        val w = appCompatActivity.window
        w.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            w.statusBarColor = Color.TRANSPARENT
        }
        //w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //  w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }
}

fun createFile(dir: String, extension: String): File {
    val sdf = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS", Locale.US)
    return File(Environment.getExternalStoragePublicDirectory(dir), "IMG_${sdf.format(Date())}.$extension")
}
