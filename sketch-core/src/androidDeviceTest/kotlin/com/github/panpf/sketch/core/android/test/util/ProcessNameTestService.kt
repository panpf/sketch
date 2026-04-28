package com.github.panpf.sketch.core.android.test.util

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.github.panpf.sketch.util.fileNameCompatibilityMultiProcess
import com.github.panpf.sketch.util.getProcessNameCompat
import com.github.panpf.sketch.util.getProcessNameSuffix
import okio.FileSystem
import java.io.File

class ProcessNameTestService : Service() {

    companion object {
        val resultFile = File(
            File(FileSystem.SYSTEM_TEMPORARY_DIRECTORY.toString()),
            "ProcessNameTest.txt"
        )
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        val processNameCompat = getProcessNameCompat(this).orEmpty()
        val processNameSuffix = getProcessNameSuffix(this).orEmpty()
        val fileNameCompatibilityMultiProcess = fileNameCompatibilityMultiProcess(
            context = this,
            file = File("/test/file")
        ).toString()

        resultFile.createNewFile()
        val text = listOf(
            processNameCompat,
            processNameSuffix,
            fileNameCompatibilityMultiProcess
        ).toString()
        resultFile.writeText(text)
    }
}