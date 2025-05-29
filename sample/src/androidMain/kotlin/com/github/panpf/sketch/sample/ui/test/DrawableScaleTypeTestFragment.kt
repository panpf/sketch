package com.github.panpf.sketch.sample.ui.test

import android.app.Application
import android.content.Context
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView.ScaleType
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import com.github.panpf.assemblyadapter.recycler.AssemblyRecyclerAdapter
import com.github.panpf.assemblyadapter.recycler.divider.Divider
import com.github.panpf.assemblyadapter.recycler.divider.GridDividerItemDecoration
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.asDrawable
import com.github.panpf.sketch.decode.ImageDecoderGifDecoder
import com.github.panpf.sketch.decode.KoralGifDecoder
import com.github.panpf.sketch.decode.MovieGifDecoder
import com.github.panpf.sketch.drawable.CrossfadeDrawable
import com.github.panpf.sketch.drawable.ResizeDrawable
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.execute
import com.github.panpf.sketch.sample.databinding.FragmentTestDrawableScaletypeBinding
import com.github.panpf.sketch.sample.databinding.GridItemDrawableScaletypeBinding
import com.github.panpf.sketch.sample.image.decode
import com.github.panpf.sketch.sample.ui.base.BaseBindingItemFactory
import com.github.panpf.sketch.sample.ui.base.BaseToolbarBindingFragment
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.fitScale
import com.github.panpf.sketch.util.toScale
import com.github.panpf.tools4a.dimen.ktx.dp2px
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class DrawableScaleTypeTestFragment :
    BaseToolbarBindingFragment<FragmentTestDrawableScaletypeBinding>() {

    private val viewModel by viewModels<DrawableScaleTypeViewModel>()

    override fun getNavigationBarInsetsView(binding: FragmentTestDrawableScaletypeBinding): View {
        return binding.root
    }

    override fun onViewCreated(
        toolbar: androidx.appcompat.widget.Toolbar,
        binding: FragmentTestDrawableScaletypeBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "Drawable ScaleType"

        binding.fitXYButton.setOnClickListener {
            viewModel.setScaleType(ScaleType.FIT_XY)
        }
        binding.fitStartButton.setOnClickListener {
            viewModel.setScaleType(ScaleType.FIT_START)
        }
        binding.fitCenterButton.setOnClickListener {
            viewModel.setScaleType(ScaleType.FIT_CENTER)
        }
        binding.fitEndButton.setOnClickListener {
            viewModel.setScaleType(ScaleType.FIT_END)
        }
        binding.centerButton.setOnClickListener {
            viewModel.setScaleType(ScaleType.CENTER)
        }
        binding.centerCropButton.setOnClickListener {
            viewModel.setScaleType(ScaleType.CENTER_CROP)
        }
        binding.centerInsideButton.setOnClickListener {
            viewModel.setScaleType(ScaleType.CENTER_INSIDE)
        }
        binding.matrixButton.setOnClickListener {
            viewModel.setScaleType(ScaleType.MATRIX)
        }

        viewModel.scaleTypeState.repeatCollectWithLifecycle(
            owner = this.viewLifecycleOwner,
            state = Lifecycle.State.STARTED
        ) {
            binding.fitXYButton.isEnabled = it != ScaleType.FIT_XY
            binding.fitStartButton.isEnabled = it != ScaleType.FIT_START
            binding.fitCenterButton.isEnabled = it != ScaleType.FIT_CENTER
            binding.fitEndButton.isEnabled = it != ScaleType.FIT_END
            binding.centerButton.isEnabled = it != ScaleType.CENTER
            binding.centerCropButton.isEnabled = it != ScaleType.CENTER_CROP
            binding.centerInsideButton.isEnabled = it != ScaleType.CENTER_INSIDE
            binding.matrixButton.isEnabled = it != ScaleType.MATRIX
        }

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(
                /* context = */ requireContext(),
                /* spanCount = */ 3,
                /* orientation = */ GridLayoutManager.VERTICAL,
                /* reverseLayout = */ false
            )
            addItemDecoration(
                GridDividerItemDecoration.Builder(requireContext())
                    .divider(Divider.space(4.dp2px))
                    .headerAndFooterDivider(Divider.space(4.dp2px))
                    .sideDivider(Divider.space(4.dp2px))
                    .sideHeaderAndFooterDivider(Divider.space(4.dp2px))
                    .build()
            )

            val recyclerAdapter = AssemblyRecyclerAdapter<DrawableScaleType>(
                itemFactoryList = listOf(element = DrawableScaleTypeItemFactory(viewModel.scaleTypeState)),
            )
            viewModel.drawablesState.repeatCollectWithLifecycle(
                owner = this@DrawableScaleTypeTestFragment.viewLifecycleOwner,
                state = Lifecycle.State.STARTED
            ) {
                recyclerAdapter.submitList(it)
            }
            adapter = recyclerAdapter
        }
    }

    class DrawableScaleTypeItemFactory(val scaleTypeState: StateFlow<ScaleType>) :
        BaseBindingItemFactory<DrawableScaleType, GridItemDrawableScaletypeBinding>(
            DrawableScaleType::class
        ) {

        override fun initItem(
            context: Context,
            binding: GridItemDrawableScaletypeBinding,
            item: BindingItem<DrawableScaleType, GridItemDrawableScaletypeBinding>
        ) {
            binding.image.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                var scope: CoroutineScope? = null

                override fun onViewAttachedToWindow(v: View) {
                    val coroutineScope = CoroutineScope(Dispatchers.Main)
                    scope = coroutineScope
                    coroutineScope.launch {
                        scaleTypeState.collect {
                            binding.image.scaleType = it
                        }
                    }
                }

                override fun onViewDetachedFromWindow(v: View) {
                    scope?.cancel()
                    scope = null
                }
            })

            binding.imageLayout.updateLayoutParams<ViewGroup.LayoutParams> {
                width =
                    ((context.resources.displayMetrics.widthPixels - (8.dp2px * 4)) / 3f).roundToInt()
                height = (width / 0.75f).roundToInt()
            }
        }

        override fun bindItemData(
            context: Context,
            binding: GridItemDrawableScaletypeBinding,
            item: BindingItem<DrawableScaleType, GridItemDrawableScaletypeBinding>,
            bindingAdapterPosition: Int,
            absoluteAdapterPosition: Int,
            data: DrawableScaleType
        ) {
            binding.titleText.text = data.title
            binding.image.setImageDrawable(data.drawable)
            if (data.drawable is Animatable) {
                data.drawable.start()
            }
        }
    }

    data class DrawableScaleType(val title: String, val drawable: Drawable)

    class DrawableScaleTypeViewModel(application: Application) : AndroidViewModel(application) {

        private val _scaleTypeState = MutableStateFlow(ScaleType.FIT_CENTER)
        val scaleTypeState: StateFlow<ScaleType> = _scaleTypeState

        private val _drawablesState = MutableStateFlow<List<DrawableScaleType>>(emptyList())
        val drawablesState: StateFlow<List<DrawableScaleType>> = _drawablesState

        init {
            loadPainters()
        }

        fun setScaleType(scaleType: ScaleType) {
            _scaleTypeState.value = scaleType
            loadPainters()
        }

        private fun loadPainters() {
            viewModelScope.launch {
                _drawablesState.value =
                    buildPainterContentScaleTestPainters(getApplication(), scaleTypeState.value)
            }
        }

        private suspend fun buildPainterContentScaleTestPainters(
            context: PlatformContext,
            scaleType: ScaleType
        ): List<DrawableScaleType> {
            val list = mutableListOf<DrawableScaleType>()

            ImageRequest(context, ResourceImages.clockHor.uri).execute()
                .let { it as ImageResult.Success }
                .image.asDrawable().apply {
                    list.add(DrawableScaleType(title = "BitmapDrawable", drawable = this))
                }

            ImageRequest(context, ResourceImages.clockHor.uri).execute()
                .let { it as ImageResult.Success }
                .image.asDrawable().apply {
                    val resizeDrawable = ResizeDrawable(this, Size(300, 300), scaleType.toScale())
                    list.add(
                        DrawableScaleType(
                            title = "ResizeDrawable\nBitmapDrawable",
                            drawable = resizeDrawable
                        )
                    )
                }

            ImageRequest(context, ResourceImages.clockHor.uri).execute()
                .let { it as ImageResult.Success }
                .image.asDrawable().apply {
                    val crossfadeDrawable =
                        CrossfadeDrawable(null, this, fitScale = scaleType.fitScale)
                    list.add(
                        DrawableScaleType(
                            title = "CrossfadeDrawable\nBitmapDrawable",
                            drawable = crossfadeDrawable
                        )
                    )
                }

            ImageRequest(context, ResourceImages.animGif.uri)
                .decode(MovieGifDecoder.Factory())?.image?.asDrawable()?.apply {
                    println("$this")
                    list.add(
                        DrawableScaleType(
                            title = "AnimatableDrawable\nMovieDrawable",
                            drawable = this
                        )
                    )
                }

            ImageRequest(context, ResourceImages.animGif.uri)
                .decode(KoralGifDecoder.Factory())?.image?.asDrawable()?.apply {
                    println("$this")
                    list.add(
                        DrawableScaleType(
                            title = "AnimatableDrawable\nGifDrawableWrapperDrawable",
                            drawable = this
                        )
                    )
                }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageRequest(context, ResourceImages.animGif.uri)
                    .decode(ImageDecoderGifDecoder.Factory())?.image?.asDrawable()?.apply {
                        println("$this")
                        list.add(
                            DrawableScaleType(
                                title = "ScaledAnimatableDrawable\nAnimatedImageDrawable",
                                drawable = this
                            )
                        )
                    }
            }

            return list
        }
    }
}