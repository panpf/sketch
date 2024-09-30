package com.github.panpf.sketch.view.core.test.request.internal

import android.widget.RemoteViews
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.RemoteViewsDelegate
import com.github.panpf.sketch.target.RemoteViewsTarget
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.TestLifecycle
import com.github.panpf.sketch.test.utils.block
import kotlinx.coroutines.Job
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class RemoteViewsDelegateTest {

    @Test
    fun test() {
        val (context, sketch) = getTestContextAndSketch()
        val remoteViews = RemoteViews(context.packageName, android.R.layout.list_content)
        val target = RemoteViewsTarget(remoteViews, android.R.id.title) {

        }
        val delegate = RemoteViewsDelegate(
            sketch = sketch,
            initialRequest = ImageRequest(context, "http://sample.com/sample.jpeg"),
            target = target,
            job = Job()
        )

        // assertActive
        delegate.assertActive()

        // lifecycle
        val lifecycle = TestLifecycle().apply {
            currentState = Lifecycle.State.RESUMED
        }
        assertNull(lifecycle.observers.find { it === delegate })

        delegate.start(lifecycle)
        block(100)
        assertNotNull(lifecycle.observers.find { it === delegate })

        delegate.finish()
        block(100)
        assertNull(lifecycle.observers.find { it === delegate })
    }
}