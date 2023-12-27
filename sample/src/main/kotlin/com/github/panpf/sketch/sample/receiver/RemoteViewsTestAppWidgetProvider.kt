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
package com.github.panpf.sketch.sample.receiver

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.RemoteViews
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.enqueue
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.sample.BuildConfig
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.target.RemoteViewsDisplayTarget
import com.github.panpf.sketch.transform.RoundedCornersTransformation
import com.github.panpf.tools4a.dimen.ktx.dp2px
import com.github.panpf.tools4a.dimen.ktx.dp2pxF

class RemoteViewsTestAppWidgetProvider : AppWidgetProvider() {

    companion object {
        private const val BROADCAST_ACTION =
            "${BuildConfig.APPLICATION_ID}.BROADCAST_ACTION_REMOTE_VIEWS_TEST_APP_WIDGET_PROVIDER"
    }

    private val imageUris = arrayOf(
        AssetImages.statics[0].uri,
        AssetImages.statics[2].uri,
        AssetImages.statics[3].uri,
    )
    private var imageUriIndex = 0

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        try {
            context.applicationContext.registerReceiver(this, IntentFilter(BROADCAST_ACTION))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == BROADCAST_ACTION) {
            intent.getIntExtra("appWidgetId", -1).takeIf { it != -1 }?.let {
                update(context, it)
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        appWidgetIds?.forEach {
            update(context, it)
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun update(context: Context, appWidgetId: Int) {
        val nextImageUri = imageUris[imageUriIndex++ % imageUris.size]
        val remoteViews = RemoteViews(context.packageName, R.layout.appwidget_remote_views).apply {
            setOnClickPendingIntent(
                R.id.image2,
                PendingIntent.getBroadcast(
                    context,
                    0,
                    Intent(BROADCAST_ACTION).apply {
                        putExtra("appWidgetId", appWidgetId)
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
        }
        DisplayRequest(context, nextImageUri) {
            resize(200.dp2px, 200.dp2px, scale = CENTER_CROP)
            transformations(RoundedCornersTransformation(20.dp2pxF))
            target(
                RemoteViewsDisplayTarget(
                    remoteViews = remoteViews,
                    imageViewId = R.id.image1,
                    ignoreNullDrawable = true,
                    onUpdated = {
                        AppWidgetManager.getInstance(context)!!
                            .updateAppWidget(appWidgetId, remoteViews)
                    }
                )
            )
        }.enqueue()
    }
}