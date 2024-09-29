package com.github.panpf.sketch.extensions.core.common.test.state

import com.github.panpf.sketch.request.Depth.LOCAL
import com.github.panpf.sketch.request.Depth.MEMORY
import com.github.panpf.sketch.request.Depth.NETWORK
import com.github.panpf.sketch.request.DepthException
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.SAVE_CELLULAR_TRAFFIC_KEY
import com.github.panpf.sketch.state.ErrorStateImage
import com.github.panpf.sketch.state.SaveCellularTrafficCondition
import com.github.panpf.sketch.state.saveCellularTrafficError
import com.github.panpf.sketch.test.utils.FakeStateImage
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SaveCellularTrafficExtensionsTest {

    @Test
    fun testSaveCellularTrafficError() {
        ErrorStateImage(null).apply {
            assertEquals(expected = 0, actual = stateList.size)
            assertNull(stateList.find { it.first is SaveCellularTrafficCondition })
        }

        ErrorStateImage(null) {
            saveCellularTrafficError()
        }.apply {
            assertEquals(expected = 1, actual = stateList.size)
            assertNull(stateList.find { it.first == SaveCellularTrafficCondition }!!.second)
        }

        ErrorStateImage(null) {
            saveCellularTrafficError(FakeStateImage())
        }.apply {
            assertEquals(expected = 1, actual = stateList.size)
            assertNotNull(stateList.find { it.first == SaveCellularTrafficCondition }!!.second)
        }
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