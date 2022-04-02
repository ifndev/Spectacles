/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ---
 *
 * THIS FILE HAS BEEN MODIFIED FOR USE IN THIS PROJECT
 */
package com.google.ar.core.examples.java.augmentedimage

import com.ifndev.spectacles.viewModel.ActiveDatabaseViewModel.Companion.getPaintingIdFromIndexFromJson
import androidx.appcompat.app.AppCompatActivity
import android.opengl.GLSurfaceView
import com.google.ar.core.examples.java.common.rendering.BackgroundRenderer
import com.bumptech.glide.RequestManager
import com.google.ar.core.examples.java.common.helpers.DisplayRotationHelper
import android.os.Bundle
import com.ifndev.spectacles.R
import com.google.ar.core.examples.java.common.helpers.CameraPermissionHelper
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException
import com.google.ar.core.exceptions.UnavailableApkTooOldException
import com.google.ar.core.exceptions.UnavailableSdkTooOldException
import android.widget.Toast
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.google.ar.core.examples.java.common.helpers.FullScreenHelper
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLES20
import com.google.ar.core.examples.java.augmentedimage.AugmentedImageActivity
import android.content.Intent
import com.ifndev.spectacles.viewModel.ActiveDatabaseViewModel
import android.app.Activity
import android.util.Log
import com.google.ar.core.*
import java.io.IOException
import java.lang.Exception
import javax.microedition.khronos.egl.EGLConfig

class AugmentedImageActivity : AppCompatActivity(), GLSurfaceView.Renderer {
    private val backgroundRenderer = BackgroundRenderer()

    // Rendering. The Renderers are created here, and initialized when the GL surface is created.
    private var surfaceView: GLSurfaceView? = null
    private val glideRequestManager: RequestManager? = null
    private var installRequested = false
    private var session: Session? = null
    private var displayRotationHelper: DisplayRotationHelper? = null
    private var shouldConfigureSession = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)
        surfaceView = findViewById(R.id.surfaceview)
        displayRotationHelper = DisplayRotationHelper( /*context=*/this)

        // Set up renderer.
        surfaceView!!.setPreserveEGLContextOnPause(true)
        surfaceView!!.setEGLContextClientVersion(2)
        surfaceView!!.setEGLConfigChooser(8, 8, 8, 8, 16, 0) // Alpha used for plane blending.
        surfaceView!!.setRenderer(this)
        surfaceView!!.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY)
        surfaceView!!.setWillNotDraw(false)
        installRequested = false
    }

    override fun onDestroy() {
        if (session != null) {
            session!!.close()
            session = null
        }
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        if (session == null) {
            var exception: Exception? = null
            var message: String? = null
            try {
                when (ArCoreApk.getInstance().requestInstall(this, !installRequested)) {
                    ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                        installRequested = true
                        return
                    }
                    ArCoreApk.InstallStatus.INSTALLED -> {}
                }
                if (!CameraPermissionHelper.hasCameraPermission(this)) {
                    CameraPermissionHelper.requestCameraPermission(this)
                    return
                }
                session = Session(this)
            } catch (e: UnavailableArcoreNotInstalledException) {
                message = "Please install ARCore"
                exception = e
            } catch (e: UnavailableUserDeclinedInstallationException) {
                message = "Please install ARCore"
                exception = e
            } catch (e: UnavailableApkTooOldException) {
                message = "Please update ARCore"
                exception = e
            } catch (e: UnavailableSdkTooOldException) {
                message = "Please update this app"
                exception = e
            } catch (e: Exception) {
                message = "This device does not support AR"
                exception = e
            }
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                finish()
                return
            }
            shouldConfigureSession = true
        }
        if (shouldConfigureSession) {
            configureSession()
            shouldConfigureSession = false
        }
        try {
            session!!.resume()
        } catch (e: CameraNotAvailableException) {
            Toast.makeText(
                this,
                "Camera not available. Try restarting the app.",
                Toast.LENGTH_SHORT
            ).show()
            session = null
            finish()
            return
        }
        surfaceView!!.onResume()
        displayRotationHelper!!.onResume()
    }

    public override fun onPause() {
        super.onPause()
        if (session != null) {
            displayRotationHelper!!.onPause()
            surfaceView!!.onPause()
            session!!.pause()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        results: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, results)
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            Toast.makeText(
                this, "Camera permissions are needed to run this application", Toast.LENGTH_LONG
            )
                .show()
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                CameraPermissionHelper.launchPermissionSettings(this)
            }
            finish()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus)
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
        try {
            backgroundRenderer.createOnGlThread(this)
        } catch (e: IOException) {
            Log.e(TAG, "Failed to read an asset file", e)
        }
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        displayRotationHelper!!.onSurfaceChanged(width, height)
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        if (session == null) {
            return
        }
        displayRotationHelper!!.updateSessionIfNeeded(session!!)
        try {
            session!!.setCameraTextureName(backgroundRenderer.textureId)
            val frame = session!!.update()
            val camera = frame.camera
            backgroundRenderer.draw(frame)
            drawAugmentedImages(frame)
        } catch (t: Throwable) {
            Log.e(TAG, "Exception on the OpenGL thread", t)
        }
    }

    private fun configureSession() {
        val config = Config(session)
        config.focusMode = Config.FocusMode.FIXED
        if (!setupAugmentedImageDatabase(config)) {
            finish()
        }
        session!!.configure(config)
    }

    private fun drawAugmentedImages(frame: Frame) {
        val updatedAugmentedImages = frame.getUpdatedTrackables(
            AugmentedImage::class.java
        )
        for (augmentedImage in updatedAugmentedImages) {
            if (augmentedImage.trackingState == TrackingState.PAUSED) {
                val extras = intent.extras
                val intent = Intent()
                Log.e("TAG", "augmentedImage.getIndex() = " + augmentedImage.index)
                intent.putExtra(
                    "paintingid", getPaintingIdFromIndexFromJson(
                        extras!!.getString("activedbvm")!!, augmentedImage.index
                    )
                )
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun setupAugmentedImageDatabase(config: Config): Boolean {
        var augmentedImageDatabase: AugmentedImageDatabase?
        val extras = intent.extras
        try {
            assets.open("image_databases/" + extras!!.getString("activedbfile")).use { `is` ->
                augmentedImageDatabase = AugmentedImageDatabase.deserialize(session, `is`)
            }
        } catch (e: IOException) {
            Log.e(TAG, "IO exception loading augmented image database.", e)
            return false
        }
        config.augmentedImageDatabase = augmentedImageDatabase
        return true
    }

    companion object {
        private val TAG = AugmentedImageActivity::class.java.simpleName
    }
}