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
package com.github.panpf.sketch.sample.ui.notify

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.RemoteViews
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.enqueue
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.databinding.FragmentTestRemoteViewsBinding
import com.github.panpf.sketch.sample.ui.base.BaseToolbarBindingFragment
import com.github.panpf.sketch.target.RemoteViewsTarget
import com.github.panpf.tools4a.dimen.ktx.dp2px

class RemoteViewsTestFragment : BaseToolbarBindingFragment<FragmentTestRemoteViewsBinding>() {

    private var notificationHelper: NotificationHelper? = null

    override fun onViewCreated(
        toolbar: Toolbar,
        binding: FragmentTestRemoteViewsBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "RemoteViews"

        notificationHelper = NotificationHelper(requireContext())
        binding.showButton.setOnClickListener {
            notificationHelper?.show()
        }
    }

    override fun onDestroyView() {
        notificationHelper?.cancel()
        super.onDestroyView()
    }

    class NotificationHelper(val context: Context) {

        companion object {
            private const val NOTIFICATION_CHANNEL_ID = "NotificationTest"
            private const val NOTIFICATION_ID = 101
            private const val BROADCAST_ACTION_NOTIFICATION =
                "REMOTE_VIEWS_FRAGMENT_BROADCAST_ACTION_NOTIFICATION"
        }

        private val notificationManager = NotificationManagerCompat.from(context)

        private val imageUris = arrayOf(
            AssetImages.statics[0].uri,
            AssetImages.statics[2].uri,
            AssetImages.statics[3].uri,
        )
        private var imageUriIndex = 0

        private val clickBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    BROADCAST_ACTION_NOTIFICATION -> {
                        updateImage()
                    }
                }
            }
        }

        init {
            val notificationChannel = NotificationChannelCompat.Builder(
                NOTIFICATION_CHANNEL_ID,
                NotificationManagerCompat.IMPORTANCE_DEFAULT
            ).setName("测试加载图片并显示到 RemoteViews 上").build()
            notificationManager.createNotificationChannel(notificationChannel)
        }

        fun show() {
            updateImage()

            try {
                context.registerReceiver(
                    clickBroadcastReceiver,
                    IntentFilter(BROADCAST_ACTION_NOTIFICATION)
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun updateImage() {
            val remoteViews =
                RemoteViews(context.packageName, R.layout.notification_remote_views).apply {
                    setImageViewResource(
                        R.id.image,
                        R.drawable.im_placeholder
                    )
                    setOnClickPendingIntent(
                        R.id.image,
                        PendingIntent.getBroadcast(
                            context,
                            0,
                            Intent(BROADCAST_ACTION_NOTIFICATION),
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )
                    )
                }
            val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID).apply {
                setSmallIcon(R.mipmap.ic_launcher)
                setContent(remoteViews)
            }.build()
            val nextImageUri = imageUris[imageUriIndex++ % imageUris.size]
            ImageRequest(context, nextImageUri) {
                resize(100.dp2px, 100.dp2px, scale = START_CROP)
                target(
                    RemoteViewsTarget(
                        remoteViews = remoteViews,
                        imageViewId = R.id.image,
                        ignoreNullDrawable = true,
                        onUpdated = {
                            val notificationManager =
                                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            notificationManager.notify(NOTIFICATION_ID, notification)
                        }
                    )
                )
            }.enqueue()
        }

        fun cancel() {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(NOTIFICATION_ID)

            try {
                context.unregisterReceiver(clickBroadcastReceiver)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}