package com.github.panpf.sketch.sample.ui.test

import android.content.Context
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView.ScaleType
import androidx.appcompat.widget.Toolbar
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.GridLayoutManager
import com.github.panpf.assemblyadapter.recycler.AssemblyRecyclerAdapter
import com.github.panpf.assemblyadapter.recycler.divider.Divider
import com.github.panpf.assemblyadapter.recycler.divider.GridDividerItemDecoration
import com.github.panpf.sketch.sample.databinding.FragmentTestDrawableScaletypeBinding
import com.github.panpf.sketch.sample.databinding.GridItemDrawableScaletypeBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingItemFactory
import com.github.panpf.sketch.sample.ui.base.BaseToolbarBindingFragment
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.github.panpf.tools4a.dimen.ktx.dp2px
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.roundToInt

abstract class BaseDrawableTestFragment :
    BaseToolbarBindingFragment<FragmentTestDrawableScaletypeBinding>() {

    private val drawableScaleTypeViewModel by viewModel<DrawableScaleTypeViewModel>()

    override fun getNavigationBarInsetsView(binding: FragmentTestDrawableScaletypeBinding): View {
        return binding.root
    }

    abstract val title: String

    abstract suspend fun buildDrawables(
        context: Context,
        scaleType: ScaleType,
        itemWidth: Float
    ): List<DrawableScaleType>

    override fun onViewCreated(
        toolbar: Toolbar,
        binding: FragmentTestDrawableScaletypeBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = title

        binding.fitXYButton.setOnClickListener {
            drawableScaleTypeViewModel.setScaleType(ScaleType.FIT_XY)
        }
        binding.fitStartButton.setOnClickListener {
            drawableScaleTypeViewModel.setScaleType(ScaleType.FIT_START)
        }
        binding.fitCenterButton.setOnClickListener {
            drawableScaleTypeViewModel.setScaleType(ScaleType.FIT_CENTER)
        }
        binding.fitEndButton.setOnClickListener {
            drawableScaleTypeViewModel.setScaleType(ScaleType.FIT_END)
        }
        binding.centerButton.setOnClickListener {
            drawableScaleTypeViewModel.setScaleType(ScaleType.CENTER)
        }
        binding.centerCropButton.setOnClickListener {
            drawableScaleTypeViewModel.setScaleType(ScaleType.CENTER_CROP)
        }
        binding.centerInsideButton.setOnClickListener {
            drawableScaleTypeViewModel.setScaleType(ScaleType.CENTER_INSIDE)
        }
        binding.matrixButton.setOnClickListener {
            drawableScaleTypeViewModel.setScaleType(ScaleType.MATRIX)
        }

        val recyclerAdapter = AssemblyRecyclerAdapter<DrawableScaleType>(
            itemFactoryList = listOf(
                element = DrawableScaleTypeItemFactory(
                    drawableScaleTypeViewModel.scaleTypeState
                )
            ),
        )
        val gridCells = 3
        val gridDividerSizePx = 8.dp2px
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(
                /* context = */ requireContext(),
                /* spanCount = */ gridCells,
                /* orientation = */ GridLayoutManager.VERTICAL,
                /* reverseLayout = */ false
            )
            addItemDecoration(
                GridDividerItemDecoration.Builder(requireContext())
                    .divider(Divider.space(gridDividerSizePx / 2))
                    .headerAndFooterDivider(Divider.space(gridDividerSizePx / 2))
                    .sideDivider(Divider.space(gridDividerSizePx / 2))
                    .sideHeaderAndFooterDivider(Divider.space(gridDividerSizePx / 2))
                    .build()
            )

            adapter = recyclerAdapter
        }

        drawableScaleTypeViewModel.scaleTypeState.repeatCollectWithLifecycle(
            owner = this.viewLifecycleOwner,
            state = Lifecycle.State.STARTED
        ) { scaleType ->
            binding.fitXYButton.isEnabled = scaleType != ScaleType.FIT_XY
            binding.fitStartButton.isEnabled = scaleType != ScaleType.FIT_START
            binding.fitCenterButton.isEnabled = scaleType != ScaleType.FIT_CENTER
            binding.fitEndButton.isEnabled = scaleType != ScaleType.FIT_END
            binding.centerButton.isEnabled = scaleType != ScaleType.CENTER
            binding.centerCropButton.isEnabled = scaleType != ScaleType.CENTER_CROP
            binding.centerInsideButton.isEnabled = scaleType != ScaleType.CENTER_INSIDE
            binding.matrixButton.isEnabled = scaleType != ScaleType.MATRIX

            viewLifecycleOwner.lifecycle.coroutineScope.launch {
                val containerWidth = requireContext().resources.displayMetrics.widthPixels
                val gridWidth =
                    (containerWidth.toFloat() - (gridCells + 1) * gridDividerSizePx) / gridCells
                val drawables = buildDrawables(requireContext(), scaleType, gridWidth)
                recyclerAdapter.submitList(drawables)
            }
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

}