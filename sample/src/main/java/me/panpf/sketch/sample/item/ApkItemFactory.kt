package me.panpf.sketch.sample.item

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.panpf.assemblyadapter.BindingItemFactory
import me.panpf.sketch.sample.ImageOptions
import me.panpf.sketch.sample.bean.AppInfo
import me.panpf.sketch.sample.databinding.ListItemAppBinding
import me.panpf.sketch.sample.widget.SampleImageView
import me.panpf.sketch.uri.ApkIconUriModel

class ApkItemFactory : BindingItemFactory<AppInfo, ListItemAppBinding>(AppInfo::class) {

    override fun createItemViewBinding(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup
    ) = ListItemAppBinding.inflate(inflater, parent, false)

    override fun initItem(
        context: Context,
        binding: ListItemAppBinding,
        item: BindingItem<AppInfo, ListItemAppBinding>
    ) {
        binding.imageInstalledAppIcon.apply {
            setOptions(ImageOptions.ROUND_RECT)
            page = SampleImageView.Page.APP_LIST
        }
    }

    override fun bindItemData(
        context: Context,
        binding: ListItemAppBinding,
        item: BindingItem<AppInfo, ListItemAppBinding>,
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: AppInfo
    ) {
        binding.imageInstalledAppIcon.displayImage(
            ApkIconUriModel.makeUri(data.apkFilePath)
        )
        binding.textInstalledAppName.text = data.name
        binding.textInstalledAppInfo.text =
            String.format("v%s  |  %s", data.versionName, data.formattedAppSize)
    }
}
