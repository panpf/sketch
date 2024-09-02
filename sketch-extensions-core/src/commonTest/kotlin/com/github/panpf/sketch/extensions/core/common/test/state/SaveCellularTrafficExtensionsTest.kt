package com.github.panpf.sketch.extensions.core.common.test.state

import com.github.panpf.sketch.request.Depth.LOCAL
import com.github.panpf.sketch.request.Depth.MEMORY
import com.github.panpf.sketch.request.Depth.NETWORK
import com.github.panpf.sketch.request.DepthException
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.SAVE_CELLULAR_TRAFFIC_KEY
import com.github.panpf.sketch.state.SaveCellularTrafficCondition
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SaveCellularTrafficExtensionsTest {

    @Test
    fun testSaveCellularTrafficError() {
        // TODO test
    }

    @Test
    fun testSaveCellularTrafficCondition() {
        val context = getTestContext()
        val request = ImageRequest(context, "http://sample.com/sample.jpeg") {
            depth(NETWORK, SAVE_CELLULAR_TRAFFIC_KEY)
        }

        SaveCellularTrafficCondition.apply {
            assertTrue(
                accept(
                    request.newRequest {
                        depth(LOCAL, SAVE_CELLULAR_TRAFFIC_KEY)
                    },
                    DepthException("")
                )
            )
            assertFalse(
                accept(
                    request.newRequest {
                        depth(MEMORY, SAVE_CELLULAR_TRAFFIC_KEY)
                    },
                    DepthException("")
                )
            )
            assertFalse(accept(request, null))

            assertEquals(
                "SaveCellularTrafficCondition",
                toString()
            )
        }
    }
}