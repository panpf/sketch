package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import me.panpf.sketch.sample.base.BaseBindingFragment
import me.panpf.sketch.sample.databinding.FragmentRepeatLoadOrDownloadTestBinding
import me.panpf.sketch.uri.ApkIconUriModel

class RepeatLoadOrDownloadTestFragment :
    BaseBindingFragment<FragmentRepeatLoadOrDownloadTestBinding>() {

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentRepeatLoadOrDownloadTestBinding.inflate(inflater, parent, false)

    override fun onInitData(
        binding: FragmentRepeatLoadOrDownloadTestBinding,
        savedInstanceState: Bundle?
    ) {
        val context = context ?: return

        val selfApkFile = context.applicationInfo.publicSourceDir
        arrayOf(
            binding.imageRepeatLoadOrDownloadTest1,
            binding.imageRepeatLoadOrDownloadTest2,
            binding.imageRepeatLoadOrDownloadTest3,
            binding.imageRepeatLoadOrDownloadTest4,
            binding.imageRepeatLoadOrDownloadTest5,
            binding.imageRepeatLoadOrDownloadTest6,
            binding.imageRepeatLoadOrDownloadTest7,
            binding.imageRepeatLoadOrDownloadTest8
        ).forEach { it.displayImage(ApkIconUriModel.makeUri(selfApkFile)) }

        arrayOf(
            binding.imageRepeatLoadOrDownloadTest9,
            binding.imageRepeatLoadOrDownloadTest10,
            binding.imageRepeatLoadOrDownloadTest11,
            binding.imageRepeatLoadOrDownloadTest12,
            binding.imageRepeatLoadOrDownloadTest13,
            binding.imageRepeatLoadOrDownloadTest14,
            binding.imageRepeatLoadOrDownloadTest15,
            binding.imageRepeatLoadOrDownloadTest16
        ).forEach { it.displayImage("http://e.hiphotos.baidu.com/image/pic/item/4610b912c8fcc3cef70d70409845d688d53f20f7.jpg") }

        arrayOf(
            binding.imageRepeatLoadOrDownloadTest31,
            binding.imageRepeatLoadOrDownloadTest32,
            binding.imageRepeatLoadOrDownloadTest33,
            binding.imageRepeatLoadOrDownloadTest34,
            binding.imageRepeatLoadOrDownloadTest35,
            binding.imageRepeatLoadOrDownloadTest36,
            binding.imageRepeatLoadOrDownloadTest37,
            binding.imageRepeatLoadOrDownloadTest38
        ).forEach { it.displayImage("http://img3.duitang.com/uploads/item/201604/26/20160426001415_teGBZ.jpeg") }
    }
}
