package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_repeat_load_or_download_test.*
import me.panpf.sketch.sample.base.BaseFragment
import me.panpf.sketch.sample.base.BindContentView
import me.panpf.sketch.sample.R
import me.panpf.sketch.uri.ApkIconUriModel

@BindContentView(R.layout.fragment_repeat_load_or_download_test)
class RepeatLoadOrDownloadTestFragment : BaseFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = context ?: return

        val selfApkFile = context.applicationInfo.publicSourceDir
        arrayOf(image_repeatLoadOrDownloadTest_1
                , image_repeatLoadOrDownloadTest_2
                , image_repeatLoadOrDownloadTest_3
                , image_repeatLoadOrDownloadTest_4
                , image_repeatLoadOrDownloadTest_5
                , image_repeatLoadOrDownloadTest_6
                , image_repeatLoadOrDownloadTest_7
                , image_repeatLoadOrDownloadTest_8
        ).forEach { it.displayImage(ApkIconUriModel.makeUri(selfApkFile)) }

        arrayOf(image_repeatLoadOrDownloadTest_9
                , image_repeatLoadOrDownloadTest_10
                , image_repeatLoadOrDownloadTest_11
                , image_repeatLoadOrDownloadTest_12
                , image_repeatLoadOrDownloadTest_13
                , image_repeatLoadOrDownloadTest_14
                , image_repeatLoadOrDownloadTest_15
                , image_repeatLoadOrDownloadTest_16
        ).forEach { it.displayImage("http://img3.imgtn.bdimg.com/it/u=1671737159,3601566602&fm=21&gp=0.jpg") }

        arrayOf(image_repeatLoadOrDownloadTest_31
                , image_repeatLoadOrDownloadTest_32
                , image_repeatLoadOrDownloadTest_33
                , image_repeatLoadOrDownloadTest_34
                , image_repeatLoadOrDownloadTest_35
                , image_repeatLoadOrDownloadTest_36
                , image_repeatLoadOrDownloadTest_37
                , image_repeatLoadOrDownloadTest_38
        ).forEach { it.displayImage("http://img3.duitang.com/uploads/item/201604/26/20160426001415_teGBZ.jpeg") }
    }
}
