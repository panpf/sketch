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
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.sample.AssetImages
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.databinding.RemoteViewsTestFragmentBinding
import com.github.panpf.sketch.sample.ui.base.ToolbarBindingFragment
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.target.RemoteViewsDisplayTarget
import com.github.panpf.tools4a.dimen.ktx.dp2px

class RemoteViewsTestFragment : ToolbarBindingFragment<RemoteViewsTestFragmentBinding>() {

    private var notificationHelper: NotificationHelper? = null

    override fun onViewCreated(
        toolbar: Toolbar,
        binding: RemoteViewsTestFragmentBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "RemoteViews"

        notificationHelper = NotificationHelper(requireContext())
        binding.remoteViewsTestButton1.setOnClickListener {
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
            AssetImages.STATICS[0],
            AssetImages.STATICS[2],
            AssetImages.STATICS[3]
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
                NotificationManager.IMPORTANCE_DEFAULT
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
                RemoteViews(context.packageName, R.layout.remote_views_notification).apply {
                    setImageViewResource(
                        R.id.remoteViewsNotificationImage,
                        R.drawable.im_placeholder
                    )
                    setOnClickPendingIntent(
                        R.id.remoteViewsNotificationImage,
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
            context.sketch.enqueue(
                DisplayRequest(context, nextImageUri) {
                    resize(100.dp2px, 100.dp2px, scale = START_CROP)
                    target(
                        RemoteViewsDisplayTarget(
                            remoteViews = remoteViews,
                            imageViewId = R.id.remoteViewsNotificationImage,
                            ignoreNullDrawable = true,
                            onUpdated = {
                                val notificationManager =
                                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                                notificationManager.notify(NOTIFICATION_ID, notification)
                            }
                        )
                    )
                }
            )
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