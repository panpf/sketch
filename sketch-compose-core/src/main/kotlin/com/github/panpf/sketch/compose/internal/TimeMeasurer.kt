//package com.github.panpf.sketch.compose.state
//
//import android.util.Log
//
//class TimeMeasurer {
//
//    private val recorders = mutableListOf<Recorder>()
//    private var totalNewToLoad: Long = 0
//    private var totalLoadToSetSize: Long = 0
//    private var totalSetSizeToOnStart: Long = 0
//    private var totalOnStartToDrawLoading: Long = 0
//    private var closedCount = 0
//
//    fun newRecorder(): Recorder {
//        return Recorder(this).also { recorders.add(it) }
//    }
//
//    fun onClose(
//        newToLoad: Long,
//        loadToSetSize: Long,
//        setSizeToOnStart: Long,
//        onStartToDrawLoading: Long,
//        actions: String
//    ) {
//        closedCount++
//        totalNewToLoad += newToLoad
//        totalLoadToSetSize += loadToSetSize
//        totalSetSizeToOnStart += setSizeToOnStart
//        totalOnStartToDrawLoading += onStartToDrawLoading
//        val avgNewToLoad = totalNewToLoad / closedCount
//        val avgLoadToSetSize = totalLoadToSetSize / closedCount
//        val avgSetSizeToOnStart = totalSetSizeToOnStart / closedCount
//        val avgOnStartToDrawLoading = totalOnStartToDrawLoading / closedCount
//        Log.d(
//            "TimeMeasurer", "onClose. $actions. " +
//                    "timeConsuming=($newToLoad, $loadToSetSize, $setSizeToOnStart, $onStartToDrawLoading), " +
//                    "avgTimeConsuming=($avgNewToLoad, $avgLoadToSetSize, $avgSetSizeToOnStart, $avgOnStartToDrawLoading)"
//        )
//    }
//
//    class Recorder(val timeMeasurer: TimeMeasurer) {
//        var newTime: Long? = null
//        var loadTime: Long? = null
//        var setSizeTime: Long? = null
//        var onStartTime: Long? = null
//        var drawLoadingTime: Long? = null
//        var actions = ""
//
//        private var close = false
//
//        fun recordNew() {
//            if (close) return
//            newTime = System.currentTimeMillis()
//            actions += "New"
//        }
//
//        fun recordSetSize() {
//            if (close) return
//            setSizeTime = System.currentTimeMillis()
//            actions += ", SetSize"
//        }
//
//        fun recordLoad() {
//            if (close) return
//            loadTime = System.currentTimeMillis()
//            actions += ", Load"
//        }
//
//        fun recordOnStart() {
//            if (close) return
//            onStartTime = System.currentTimeMillis()
//            actions += ", OnStart"
//        }
//
//        fun recordDrawLoading() {
//            if (close) return
//            drawLoadingTime = System.currentTimeMillis()
//            actions += ", DrawLoading"
//        }
//
//        fun close() {
//            if (close) return
//            close = true
//            val newTime = newTime!!
//            val loadTime = loadTime!!
//            val setSizeTime = setSizeTime!!
//            val onStartTime = onStartTime!!
//            val drawLoadingTime = drawLoadingTime!!
//
//            val newToLoad = loadTime - newTime
//            val loadToSetSize = setSizeTime - loadTime
//            val setSizeToOnStart = onStartTime - setSizeTime
//            val onStartToDrawLoading = drawLoadingTime - onStartTime
//            timeMeasurer.onClose(
//                newToLoad = newToLoad,
//                loadToSetSize = loadToSetSize,
//                setSizeToOnStart = setSizeToOnStart,
//                onStartToDrawLoading = onStartToDrawLoading,
//                actions = actions
//            )
//        }
//    }
//}