package com.github.panpf.sketch.view.core.test.request

class ImageViewRequestTest {

    // TODO test

//    @Test
//    fun testListener() {
//        val context1 = getTestContext()
//        val uriString1 = MyImages.jpeg.uri
//        ImageRequest.Builder(context1, uriString1).apply {
//            build().apply {
//                Assert.assertNull(listener)
//                Assert.assertNull(target)
//            }
//
//            listener(onStart = {}, onCancel = {}, onError = { _, _ -> }, onSuccess = { _, _ -> })
//            build().apply {
//                Assert.assertNotNull(listener)
//                Assert.assertTrue(listener !is CombinedListener)
//            }
//            build().newRequest().apply {
//                Assert.assertNotNull(listener)
//                Assert.assertTrue(listener !is CombinedListener)
//            }
//
//            listener(onStart = {})
//            build().apply {
//                Assert.assertNotNull(listener)
//                Assert.assertTrue(listener !is CombinedListener)
//            }
//
//            listener(onCancel = {})
//            build().apply {
//                Assert.assertNotNull(listener)
//                Assert.assertTrue(listener !is CombinedListener)
//            }
//
//            listener(onError = { _, _ -> })
//            build().apply {
//                Assert.assertNotNull(listener)
//                Assert.assertTrue(listener !is CombinedListener)
//            }
//
//            listener(onSuccess = { _, _ -> })
//            build().apply {
//                Assert.assertNotNull(listener)
//                Assert.assertTrue(listener !is CombinedListener)
//            }
//
//            listener(null)
//            target(null)
//            build().apply {
//                Assert.assertNull(listener)
//            }
//
//            target(ImageView(context1))
//            build().apply {
//                Assert.assertNull(listener)
//            }
//
//            listener(onSuccess = { _, _ -> })
//            build().apply {
//                Assert.assertNotNull(listener)
//                Assert.assertTrue(listener !is CombinedListener)
//            }
//
//            listener(null)
//            target(null)
//            build().apply {
//                Assert.assertNull(listener)
//            }
//
//            target(TestListenerImageView(context1))
//            build().listener!!.asOrThrow<CombinedListener>().apply {
//                Assert.assertNull(fromBuilderListener)
//                Assert.assertNull(fromBuilderListeners)
//                Assert.assertNotNull(fromTargetListener)
//                Assert.assertTrue(fromTargetListener !is CombinedListener)
//            }
//            build().newRequest().listener!!.asOrThrow<CombinedListener>().apply {
//                Assert.assertNull(fromBuilderListener)
//                Assert.assertNull(fromBuilderListeners)
//                Assert.assertNotNull(fromTargetListener)
//                Assert.assertTrue(fromTargetListener !is CombinedListener)
//            }
//
//            listener(onSuccess = { _, _ -> })
//            build().listener!!.asOrThrow<CombinedListener>().apply {
//                Assert.assertNotNull(fromBuilderListener)
//                Assert.assertNull(fromBuilderListeners)
//                Assert.assertTrue(fromBuilderListener !is CombinedListener)
//                Assert.assertNotNull(fromTargetListener)
//                Assert.assertTrue(fromTargetListener !is CombinedListener)
//            }
//            build().newRequest().listener!!.asOrThrow<CombinedListener>().apply {
//                Assert.assertNotNull(fromBuilderListener)
//                Assert.assertNull(fromBuilderListeners)
//                Assert.assertTrue(fromBuilderListener !is CombinedListener)
//                Assert.assertNotNull(fromTargetListener)
//                Assert.assertTrue(fromTargetListener !is CombinedListener)
//            }
//
//            val listener2 = object : Listener {}
//            val listener3 = object : Listener {}
//            addListener(listener2)
//            addListener(listener3)
//            addListener(listener2)
//            build().listener!!.asOrThrow<CombinedListener>().apply {
//                Assert.assertNotNull(fromBuilderListener)
//                Assert.assertNotNull(fromBuilderListeners)
//                Assert.assertTrue(fromBuilderListeners!!.size == 2)
//                Assert.assertTrue(fromBuilderListener !is CombinedListener)
//                Assert.assertNotNull(fromTargetListener)
//                Assert.assertTrue(fromTargetListener !is CombinedListener)
//            }
//            build().newRequest().listener!!.asOrThrow<CombinedListener>().apply {
//                Assert.assertNotNull(fromBuilderListener)
//                Assert.assertNotNull(fromBuilderListeners)
//                Assert.assertTrue(fromBuilderListeners!!.size == 2)
//                Assert.assertTrue(fromBuilderListener !is CombinedListener)
//                Assert.assertNotNull(fromTargetListener)
//                Assert.assertTrue(fromTargetListener !is CombinedListener)
//            }
//
//            removeListener(listener2)
//            build().listener!!.asOrThrow<CombinedListener>().apply {
//                Assert.assertNotNull(fromBuilderListener)
//                Assert.assertNotNull(fromBuilderListeners)
//                Assert.assertTrue(fromBuilderListeners!!.size == 1)
//                Assert.assertTrue(fromBuilderListener !is CombinedListener)
//                Assert.assertNotNull(fromTargetListener)
//                Assert.assertTrue(fromTargetListener !is CombinedListener)
//            }
//            build().newRequest().listener!!.asOrThrow<CombinedListener>().apply {
//                Assert.assertNotNull(fromBuilderListener)
//                Assert.assertNotNull(fromBuilderListeners)
//                Assert.assertTrue(fromBuilderListeners!!.size == 1)
//                Assert.assertTrue(fromBuilderListener !is CombinedListener)
//                Assert.assertNotNull(fromTargetListener)
//                Assert.assertTrue(fromTargetListener !is CombinedListener)
//            }
//        }
//    }
//
//    @Test
//    fun testProgressListener() {
//        val context1 = getTestContext()
//        val uriString1 = MyImages.jpeg.uri
//        ImageRequest.Builder(context1, uriString1).apply {
//            build().apply {
//                Assert.assertNull(progressListener)
//                Assert.assertNull(target)
//            }
//
//            progressListener { _, _ -> }
//            build().apply {
//                Assert.assertNotNull(progressListener)
//                Assert.assertTrue(progressListener !is CombinedProgressListener)
//            }
//            build().newRequest().apply {
//                Assert.assertNotNull(progressListener)
//                Assert.assertTrue(progressListener !is CombinedProgressListener)
//            }
//
//            progressListener(null)
//            target(null)
//            build().apply {
//                Assert.assertNull(progressListener)
//            }
//
//            target(ImageView(context1))
//            build().apply {
//                Assert.assertNull(progressListener)
//            }
//
//            progressListener { _, _ -> }
//            build().apply {
//                Assert.assertNotNull(progressListener)
//                Assert.assertTrue(progressListener !is CombinedProgressListener)
//            }
//
//            progressListener(null)
//            target(null)
//            build().apply {
//                Assert.assertNull(progressListener)
//            }
//
//            target(TestListenerImageView(context1))
//            build().progressListener!!.asOrThrow<CombinedProgressListener>().apply {
//                Assert.assertNull(fromBuilderProgressListener)
//                Assert.assertNull(fromBuilderProgressListeners)
//                Assert.assertNotNull(fromTargetProgressListener)
//                Assert.assertTrue(fromTargetProgressListener !is CombinedProgressListener)
//            }
//            build().newRequest().progressListener!!.asOrThrow<CombinedProgressListener>()
//                .apply {
//                    Assert.assertNull(fromBuilderProgressListener)
//                    Assert.assertNull(fromBuilderProgressListeners)
//                    Assert.assertNotNull(fromTargetProgressListener)
//                    Assert.assertTrue(fromTargetProgressListener !is CombinedProgressListener)
//                }
//
//            progressListener { _, _ -> }
//            build().progressListener!!.asOrThrow<CombinedProgressListener>().apply {
//                Assert.assertNotNull(fromBuilderProgressListener)
//                Assert.assertNull(fromBuilderProgressListeners)
//                Assert.assertTrue(fromBuilderProgressListener !is CombinedProgressListener)
//                Assert.assertNotNull(fromTargetProgressListener)
//                Assert.assertTrue(fromTargetProgressListener !is CombinedProgressListener)
//            }
//            build().newRequest().progressListener!!.asOrThrow<CombinedProgressListener>()
//                .apply {
//                    Assert.assertNotNull(fromBuilderProgressListener)
//                    Assert.assertNull(fromBuilderProgressListeners)
//                    Assert.assertTrue(fromBuilderProgressListener !is CombinedProgressListener)
//                    Assert.assertNotNull(fromTargetProgressListener)
//                    Assert.assertTrue(fromTargetProgressListener !is CombinedProgressListener)
//                }
//
//            val progressListener2 = ProgressListener { _, _ -> }
//            val progressListener3 = ProgressListener { _, _ -> }
//            addProgressListener(progressListener2)
//            addProgressListener(progressListener3)
//            addProgressListener(progressListener2)
//            build().progressListener!!.asOrThrow<CombinedProgressListener>().apply {
//                Assert.assertNotNull(fromBuilderProgressListener)
//                Assert.assertNotNull(fromBuilderProgressListeners)
//                Assert.assertTrue(fromBuilderProgressListeners!!.size == 2)
//                Assert.assertTrue(fromBuilderProgressListener !is CombinedProgressListener)
//                Assert.assertNotNull(fromTargetProgressListener)
//                Assert.assertTrue(fromTargetProgressListener !is CombinedProgressListener)
//            }
//            build().newRequest().progressListener!!.asOrThrow<CombinedProgressListener>()
//                .apply {
//                    Assert.assertNotNull(fromBuilderProgressListener)
//                    Assert.assertNotNull(fromBuilderProgressListeners)
//                    Assert.assertTrue(fromBuilderProgressListeners!!.size == 2)
//                    Assert.assertTrue(fromBuilderProgressListener !is CombinedProgressListener)
//                    Assert.assertNotNull(fromTargetProgressListener)
//                    Assert.assertTrue(fromTargetProgressListener !is CombinedProgressListener)
//                }
//
//            removeProgressListener(progressListener2)
//            build().progressListener!!.asOrThrow<CombinedProgressListener>().apply {
//                Assert.assertNotNull(fromBuilderProgressListener)
//                Assert.assertNotNull(fromBuilderProgressListeners)
//                Assert.assertTrue(fromBuilderProgressListeners!!.size == 1)
//                Assert.assertTrue(fromBuilderProgressListener !is CombinedProgressListener)
//                Assert.assertNotNull(fromTargetProgressListener)
//                Assert.assertTrue(fromTargetProgressListener !is CombinedProgressListener)
//            }
//            build().newRequest().progressListener!!.asOrThrow<CombinedProgressListener>()
//                .apply {
//                    Assert.assertNotNull(fromBuilderProgressListener)
//                    Assert.assertNotNull(fromBuilderProgressListeners)
//                    Assert.assertTrue(fromBuilderProgressListeners!!.size == 1)
//                    Assert.assertTrue(fromBuilderProgressListener !is CombinedProgressListener)
//                    Assert.assertNotNull(fromTargetProgressListener)
//                    Assert.assertTrue(fromTargetProgressListener !is CombinedProgressListener)
//                }
//        }
//    }

}