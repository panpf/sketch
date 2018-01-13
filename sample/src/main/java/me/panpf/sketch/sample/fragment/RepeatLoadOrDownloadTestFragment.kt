package me.panpf.sketch.sample.fragment

import android.os.Bundle
import android.view.View
import me.panpf.sketch.sample.BaseFragment
import me.panpf.sketch.sample.BindContentView
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.bindView
import me.panpf.sketch.sample.widget.SampleImageView
import me.panpf.sketch.uri.ApkIconUriModel

@BindContentView(R.layout.fragment_repeat_load_or_download_test)
class RepeatLoadOrDownloadTestFragment : BaseFragment() {

    val imageView1: SampleImageView by bindView(R.id.image_repeatLoadOrDownloadTest_1)
    val imageView2: SampleImageView by bindView(R.id.image_repeatLoadOrDownloadTest_2)
    val imageView3: SampleImageView by bindView(R.id.image_repeatLoadOrDownloadTest_3)
    val imageView4: SampleImageView by bindView(R.id.image_repeatLoadOrDownloadTest_4)
    val imageView5: SampleImageView by bindView(R.id.image_repeatLoadOrDownloadTest_5)
    val imageView6: SampleImageView by bindView(R.id.image_repeatLoadOrDownloadTest_6)
    val imageView7: SampleImageView by bindView(R.id.image_repeatLoadOrDownloadTest_7)
    val imageView8: SampleImageView by bindView(R.id.image_repeatLoadOrDownloadTest_8)

    val imageView9: SampleImageView by bindView(R.id.image_repeatLoadOrDownloadTest_9)
    val imageView10: SampleImageView by bindView(R.id.image_repeatLoadOrDownloadTest_10)
    val imageView11: SampleImageView by bindView(R.id.image_repeatLoadOrDownloadTest_11)
    val imageView12: SampleImageView by bindView(R.id.image_repeatLoadOrDownloadTest_12)
    val imageView13: SampleImageView by bindView(R.id.image_repeatLoadOrDownloadTest_13)
    val imageView14: SampleImageView by bindView(R.id.image_repeatLoadOrDownloadTest_14)
    val imageView15: SampleImageView by bindView(R.id.image_repeatLoadOrDownloadTest_15)
    val imageView16: SampleImageView by bindView(R.id.image_repeatLoadOrDownloadTest_16)

    val imageView31: SampleImageView by bindView(R.id.image_repeatLoadOrDownloadTest_31)
    val imageView32: SampleImageView by bindView(R.id.image_repeatLoadOrDownloadTest_32)
    val imageView33: SampleImageView by bindView(R.id.image_repeatLoadOrDownloadTest_33)
    val imageView34: SampleImageView by bindView(R.id.image_repeatLoadOrDownloadTest_34)
    val imageView35: SampleImageView by bindView(R.id.image_repeatLoadOrDownloadTest_35)
    val imageView36: SampleImageView by bindView(R.id.image_repeatLoadOrDownloadTest_36)
    val imageView37: SampleImageView by bindView(R.id.image_repeatLoadOrDownloadTest_37)
    val imageView38: SampleImageView by bindView(R.id.image_repeatLoadOrDownloadTest_38)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selfApkFile = context.applicationInfo.publicSourceDir
        imageView1.displayImage(ApkIconUriModel.makeUri(selfApkFile))
        imageView2.displayImage(ApkIconUriModel.makeUri(selfApkFile))
        imageView3.displayImage(ApkIconUriModel.makeUri(selfApkFile))
        imageView4.displayImage(ApkIconUriModel.makeUri(selfApkFile))
        imageView5.displayImage(ApkIconUriModel.makeUri(selfApkFile))
        imageView6.displayImage(ApkIconUriModel.makeUri(selfApkFile))
        imageView7.displayImage(ApkIconUriModel.makeUri(selfApkFile))
        imageView8.displayImage(ApkIconUriModel.makeUri(selfApkFile))

        imageView9.displayImage("http://img3.imgtn.bdimg.com/it/u=1671737159,3601566602&fm=21&gp=0.jpg")
        imageView10.displayImage("http://img3.imgtn.bdimg.com/it/u=1671737159,3601566602&fm=21&gp=0.jpg")
        imageView11.displayImage("http://img3.imgtn.bdimg.com/it/u=1671737159,3601566602&fm=21&gp=0.jpg")
        imageView12.displayImage("http://img3.imgtn.bdimg.com/it/u=1671737159,3601566602&fm=21&gp=0.jpg")
        imageView13.displayImage("http://img3.imgtn.bdimg.com/it/u=1671737159,3601566602&fm=21&gp=0.jpg")
        imageView14.displayImage("http://img3.imgtn.bdimg.com/it/u=1671737159,3601566602&fm=21&gp=0.jpg")
        imageView15.displayImage("http://img3.imgtn.bdimg.com/it/u=1671737159,3601566602&fm=21&gp=0.jpg")
        imageView16.displayImage("http://img3.imgtn.bdimg.com/it/u=1671737159,3601566602&fm=21&gp=0.jpg")

        imageView31.displayImage("http://img3.duitang.com/uploads/item/201604/26/20160426001415_teGBZ.jpeg")
        imageView32.displayImage("http://img3.duitang.com/uploads/item/201604/26/20160426001415_teGBZ.jpeg")
        imageView33.displayImage("http://img3.duitang.com/uploads/item/201604/26/20160426001415_teGBZ.jpeg")
        imageView34.displayImage("http://img3.duitang.com/uploads/item/201604/26/20160426001415_teGBZ.jpeg")
        imageView35.displayImage("http://img3.duitang.com/uploads/item/201604/26/20160426001415_teGBZ.jpeg")
        imageView36.displayImage("http://img3.duitang.com/uploads/item/201604/26/20160426001415_teGBZ.jpeg")
        imageView37.displayImage("http://img3.duitang.com/uploads/item/201604/26/20160426001415_teGBZ.jpeg")
        imageView38.displayImage("http://img3.duitang.com/uploads/item/201604/26/20160426001415_teGBZ.jpeg")
    }
}
