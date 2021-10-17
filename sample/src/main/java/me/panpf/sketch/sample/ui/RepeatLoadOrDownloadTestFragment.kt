package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.panpf.sketch.Sketch
import me.panpf.sketch.sample.base.ToolbarBindingFragment
import me.panpf.sketch.sample.databinding.FragmentRepeatLoadOrDownloadTestBinding
import me.panpf.sketch.uri.ApkIconUriModel

class RepeatLoadOrDownloadTestFragment :
    ToolbarBindingFragment<FragmentRepeatLoadOrDownloadTestBinding>() {

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentRepeatLoadOrDownloadTestBinding.inflate(inflater, parent, false)

    override fun onInitData(
        toolbar: Toolbar,
        binding: FragmentRepeatLoadOrDownloadTestBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "Repeat Load Or Download Test"

        val uri1 = ApkIconUriModel.makeUri(requireContext().applicationInfo.publicSourceDir)
        val uri2 = "http://b.zol-img.com.cn/desk/bizhi/image/4/1366x768/1387347695254.jpg"
        val uri3 =
            "https://images.unsplash.com/photo-1431440869543-efaf3388c585?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&s=8b00971a3e4a84fb43403797126d1991%22"

        lifecycleScope.launch {
            val sketch = Sketch.with(requireContext())
            withContext(Dispatchers.IO) {
                sketch.configuration.apply {
                    memoryCache.remove(uri1)
                    memoryCache.remove(uri2)
                    memoryCache.remove(uri3)

                    diskCache.get(uri1)?.delete()
                    diskCache.get(uri2)?.delete()
                    diskCache.get(uri3)?.delete()
                }
            }

            arrayOf(
                binding.imageRepeatLoadOrDownloadTest1,
                binding.imageRepeatLoadOrDownloadTest2,
                binding.imageRepeatLoadOrDownloadTest3,
                binding.imageRepeatLoadOrDownloadTest4,
                binding.imageRepeatLoadOrDownloadTest5,
                binding.imageRepeatLoadOrDownloadTest6,
                binding.imageRepeatLoadOrDownloadTest7,
                binding.imageRepeatLoadOrDownloadTest8
            ).forEach {
                it.isShowImageFromEnabled = true
                it.isShowDownloadProgressEnabled = true
                it.displayImage(uri1)
            }

            arrayOf(
                binding.imageRepeatLoadOrDownloadTest9,
                binding.imageRepeatLoadOrDownloadTest10,
                binding.imageRepeatLoadOrDownloadTest11,
                binding.imageRepeatLoadOrDownloadTest12,
                binding.imageRepeatLoadOrDownloadTest13,
                binding.imageRepeatLoadOrDownloadTest14,
                binding.imageRepeatLoadOrDownloadTest15,
                binding.imageRepeatLoadOrDownloadTest16
            ).forEach {
                it.isShowImageFromEnabled = true
                it.isShowDownloadProgressEnabled = true
                it.displayImage(uri2)
            }

            arrayOf(
                binding.imageRepeatLoadOrDownloadTest31,
                binding.imageRepeatLoadOrDownloadTest32,
                binding.imageRepeatLoadOrDownloadTest33,
                binding.imageRepeatLoadOrDownloadTest34,
                binding.imageRepeatLoadOrDownloadTest35,
                binding.imageRepeatLoadOrDownloadTest36,
                binding.imageRepeatLoadOrDownloadTest37,
                binding.imageRepeatLoadOrDownloadTest38
            ).forEach {
                it.isShowImageFromEnabled = true
                it.isShowDownloadProgressEnabled = true
                it.displayImage(uri3)
            }
        }
    }
}
