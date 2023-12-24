/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.decode.internal

import android.annotation.TargetApi
import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.GLES10
import android.opengl.GLES20
import android.os.Build
import android.os.Build.VERSION
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLContext

object OpenGLTextureHelper {

    /**
     * The maximum size of an image allowed by OpenGL (single side length)
     */
    val maxSize: Int? = try {
        if (VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            openGLTextureMaxSizeJB1()
        } else {
            // TODO The result read on api 16 is 32766, but in fact the Bitmap cannot be drawn during runtime and an error is reported in the log 'Bitmap too large to be uploaded into a texture (3750x116, max=2048x2048)'
            openGLTextureMaxSizeBase()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        0
    }.takeIf { it > 0 }


// TROUBLE! No config found.

// To make a context current, which we will need later,
// you need a rendering surface, even if you don't actually plan to render.
// To satisfy this requirement, create a small offscreen (Pbuffer) surface:

// Next, create the context:

// Ready to make the context current now:

// If all of the above succeeded (error checking was omitted), you can make your OpenGL calls now:

    // Once you're all done, you can tear down everything:
// Then get a hold of the default display, and initialize.
// This could get more complex if you have to deal with devices that could have multiple displays,
// but will be sufficient for a typical phone/tablet:
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun openGLTextureMaxSizeJB1(): Int {
        // Next, we need to find a config. Since we won't use this context for rendering,
        // the exact attributes aren't very critical:
        // Then get a hold of the default display, and initialize.
        // This could get more complex if you have to deal with devices that could have multiple displays,
        // but will be sufficient for a typical phone/tablet:
        val dpy = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
        val vers = IntArray(2)
        EGL14.eglInitialize(dpy, vers, 0, vers, 1)

        // Next, we need to find a config. Since we won't use this context for rendering,
        // the exact attributes aren't very critical:
        val configAttr = intArrayOf(
            EGL14.EGL_COLOR_BUFFER_TYPE, EGL14.EGL_RGB_BUFFER,
            EGL14.EGL_LEVEL, 0,
            EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
            EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT,
            EGL14.EGL_NONE
        )
        val configs = arrayOfNulls<EGLConfig>(1)
        val numConfig = IntArray(1)
        EGL14.eglChooseConfig(
            dpy, configAttr, 0,
            configs, 0, 1, numConfig, 0
        )
        @Suppress("ControlFlowWithEmptyBody")
        if (numConfig[0] == 0) {
            // TROUBLE! No config found.
        }
        val config = configs[0]

        // To make a context current, which we will need later,
        // you need a rendering surface, even if you don't actually plan to render.
        // To satisfy this requirement, create a small offscreen (Pbuffer) surface:
        val surfAttr = intArrayOf(
            EGL14.EGL_WIDTH, 64,
            EGL14.EGL_HEIGHT, 64,
            EGL14.EGL_NONE
        )
        val surf = EGL14.eglCreatePbufferSurface(dpy, config, surfAttr, 0)

        // Next, create the context:
        val ctxAttrib = intArrayOf(
            EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
            EGL14.EGL_NONE
        )
        val ctx = EGL14.eglCreateContext(dpy, config, EGL14.EGL_NO_CONTEXT, ctxAttrib, 0)

        // Ready to make the context current now:
        EGL14.eglMakeCurrent(dpy, surf, surf, ctx)

        // If all of the above succeeded (error checking was omitted), you can make your OpenGL calls now:
        val maxSize = IntArray(1)
        GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxSize, 0)

        // Once you're all done, you can tear down everything:
        EGL14.eglMakeCurrent(
            dpy, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
            EGL14.EGL_NO_CONTEXT
        )
        EGL14.eglDestroySurface(dpy, surf)
        EGL14.eglDestroyContext(dpy, ctx)
        EGL14.eglTerminate(dpy)
        return maxSize[0]
    }

    // missing in EGL10// TROUBLE! No config found.
    // In JELLY_BEAN will collapse
    private fun openGLTextureMaxSizeBase(): Int {
        // In JELLY_BEAN will collapse
        if (VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN) {
            return 0
        }
        val egl = EGLContext.getEGL() as EGL10
        val dpy = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)
        val vers = IntArray(2)
        egl.eglInitialize(dpy, vers)
        val configAttr = intArrayOf(
            EGL10.EGL_COLOR_BUFFER_TYPE, EGL10.EGL_RGB_BUFFER,
            EGL10.EGL_LEVEL, 0,
            EGL10.EGL_SURFACE_TYPE, EGL10.EGL_PBUFFER_BIT,
            EGL10.EGL_NONE
        )
        val configs = arrayOfNulls<javax.microedition.khronos.egl.EGLConfig>(1)
        val numConfig = IntArray(1)
        egl.eglChooseConfig(dpy, configAttr, configs, 1, numConfig)
        @Suppress("ControlFlowWithEmptyBody")
        if (numConfig[0] == 0) {
            // TROUBLE! No config found.
        }
        val config = configs[0]
        val surfAttr = intArrayOf(
            EGL10.EGL_WIDTH, 64,
            EGL10.EGL_HEIGHT, 64,
            EGL10.EGL_NONE
        )
        val surf = egl.eglCreatePbufferSurface(dpy, config, surfAttr)
        @Suppress("LocalVariableName") val EGL_CONTEXT_CLIENT_VERSION = 0x3098 // missing in EGL10
        val ctxAttrib = intArrayOf(
            EGL_CONTEXT_CLIENT_VERSION, 1,
            EGL10.EGL_NONE
        )
        val ctx = egl.eglCreateContext(dpy, config, EGL10.EGL_NO_CONTEXT, ctxAttrib)
        egl.eglMakeCurrent(dpy, surf, surf, ctx)
        val maxSize = IntArray(1)
        GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxSize, 0)
        egl.eglMakeCurrent(
            dpy,
            EGL10.EGL_NO_SURFACE,
            EGL10.EGL_NO_SURFACE,
            EGL10.EGL_NO_CONTEXT
        )
        egl.eglDestroySurface(dpy, surf)
        egl.eglDestroyContext(dpy, ctx)
        egl.eglTerminate(dpy)
        return maxSize[0]
    }
}