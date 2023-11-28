# Gesture zoom

Translations: [简体中文](zoom_zh.md)

> [!CAUTION]
> This component is no longer updated, please use the SketchZoomImageView component of
> the https://github.com/panpf/zoomimage library instead

`Need to import sketch-zoom module`

Sketch's [SketchZoomImageView] provides gesture zooming and super large image sampling functions.
Gesture zoom function reference [PhotoView] implementation

### Compare PhotoView

* Double-click zoom level: reduced from three levels to two levels in [PhotoView], making the
  operation easier
* Double-click scale factor: The double-click scale factor of [PhotoView] is fixed,
  while [SketchZoomImageView] is based on the size of the image and View
  The width, height and ScaleType are dynamically calculated for a better experience.
* Boundary damping: When manual scaling exceeds the minimum or maximum ratio, [PhotoView] cannot be
  pulled directly, and [SketchZoomImageView]
  It will feel like pulling a rubber band and the experience will be better
* Boundary judgment: [SketchZoomImageView] optimizes the judgment of scrollEdge and fixes the
  problem that the edge cannot be recognized under a scale factor that is not divisible bug
* Slider: [SketchZoomImageView] adds a slider to easily see the current sliding position
* Positioning: [SketchZoomImageView] adds a positioning function, which allows you to specify a
  point on the image and then move to this point in an animated manner
* Reading mode: [SketchZoomImageView] adds read mode, which provides a better read experience
  for long images

### Use

Simply replace ImageView with [SketchZoomImageView]

```kotlin
sketchZoomImageView.displayImage("https://www.sample.com/image.jpg")
```

> Note:
> * The zoom function supports Drawable from any source
> * The super large image sampling function only supports Drawable from Sketch

### Scale

```kotlin
// Zoom in 3x (without animation)
sketchZoomImageView.scale(3f)

// Zoom in 3x and use animation
sketchZoomImageView.scale(3f, true)

// Zoom in 3x with 100x200 as center point and use animation
sketchZoomImageView.scale(3f, 100f, 200f, true)
```

> Note:
> * The set scaling ratio cannot be less than the minimum scaling ratio nor greater than the maximum
    scaling ratio.
> * The scaling ratio set by the scale method is only temporary and will not be maintained forever.
    Any other scaling behavior and update behavior will overwrite this scaling ratio.

## Rotate

```kotlin
// Rotate to 180°
sketchZoomImageView.rotateTo(180)

// Rotate another 90° clockwise
sketchZoomImageView.rotateBy(90)
```

> Note:
> * Only supports rotation angles such as 90, 180, 270, 360, etc. that can be evenly divided by 90
> * The rotation angle will always exist

### Location

```kotlin
// Position to 100x200 (regardless of rotation angle)
sketchZoomImageView.location(100f, 200f)

// Position to 100x200 and use animation (regardless of rotation angle)
sketchZoomImageView.location(100f, 200f, true)
```

> Note: The position set by the location method is only temporary and will not be maintained
> forever. Any other displacement behavior and update behavior will overwrite this position.

### Scroll Bar

The scroll bar allows you to clearly know the current position when viewing a long
image. [SketchZoomImageView] turns on the scroll bar function by default. You can also turn it off,
as follows:

```kotlin
sketchZoomImageView.scrollBarEnabled = false
```

> Displayed when scrolling, automatically hidden after 800 milliseconds of no operation

### Read Mode

For long images with a particularly large difference in width and height, if the entire image is
displayed at the beginning, nothing can be seen clearly, and the user must double-click to enlarge
before starting to read.

For such images [SketchZoomImageView] provides a read mode to fill the screen from the beginning, so
that users can directly start read the content of long images.

Turn on read mode:

```kotlin
sketchZoomImageView.readModeEnabled = true
```

[SketchZoomImageView] uses [ReadModeDecider] to determine whether read mode needs to be used. The
default implementation is [LongImageReadModeDecider], which only uses read mode for long images.

> The default implementation of long image rules is [DefaultLongImageDecider], you can also
> create [LongImageReadModeDecider]
> Use custom long image determination rules when

If you want to modify the read mode determination rules, you can implement the [ReadModeDecider]
interface and then apply it through the `readModeDecider` attribute of [SketchZoomImageView], as
follows:

```kotlin
class MyReadModeDecider : ReadModeDecider {

    override fun should(
        imageWidth: Int,
        imageHeight: Int,
        viewWidth: Int,
        viewHeight: Int
    ): Boolean {
        // Implement your decision rules
    }
}

sketchZoomImageView.readModeDecider = MyReadModeDecider()
```

### Custom scaling

Through ScaleState, you can modify the minimum, maximum, double-click, initial and other scaling
ratios. Just customize your ScaleState and apply it as follows:

```kotlin
class MyScaleStateFactory : ScaleState.Factory {

    override fun create(
        viewSize: Size,
        imageSize: Size,
        drawableSize: Size,
        rotateDegrees: Int,
        scaleType: ScaleType,
        readModeDecider: ReadModeDecider?,
    ): ScaleState {
        val minScale = ...
        val maxScale = ...
        val fullScale = ...
        val fillScale = ...
        val originScale = ...
        val initial = ...
        val steps = ...
        return ScaleState(
            min = minScale,
            max = maxScale,
            full = fullScale,
            fill = fillScale,
            origin = originScale,
            initial = initial,
            doubleClickSteps = steps,
        )
    }
}

sketchZoomImageView.scaleStateFactory = MyScaleStateFactory()
```

### Zoom information

```kotlin
// Get the current scale factor
sketchZoomImageView.scale

// Get the current rotation angle (clockwise)
sketchZoomImageView.rotateDegrees

// Get the minimum scale factor
sketchZoomImageView.minScale

// Get the maximum scale factor
sketchZoomImageView.maxScale

// Get the area visible to the user on the current preview image (not affected by rotation)
sketchZoomImageView.getVisibleRect(Rect())

// more ...
```

### Gesture zoom listener

```kotlin
// Listen for scale changes
sketchZoomImageView.addOnScaleChangeListener { scaleFactor: Float, focusX: Float, focusY: Float ->

}

// Listen click
sketchZoomImageView.onViewTapListener = { view: View, x: Float, y: Float ->

}

// Monitor long press
sketchZoomImageView.onViewLongPressListener = { view: View, x: Float, y: Float ->

}

// more ...
```

> Note: When OnViewTapListener or OnViewLongPressListener is not registered, [SketchZoomImageView]
> will try to call back ImageView's OnClickListener or OnLongClickListener

### ArrayIndexOutOfBoundsException 与 IllegalArgumentException：pointerIndex out of range

Since it is implemented with reference to [PhotoView], this exception is inevitable. It is
recommended to intercept it in Activity, as follows:

```kotlin
class ImageDetailActivity : AppCompatActivity() {

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return try {
            super.dispatchTouchEvent(ev)
        } catch (e: RuntimeException) {
            e.printStackTrace()
            true
        }
    }
}
```

### Very large image sampling

Very large images are usually huge in size, and trying to read them completely into the memory will
definitely cause the app to crash due to insufficient memory.

[SketchZoomImageView] supports the function of super large image sampling through
BitmapRegionDecoder to avoid app crashes

Under what circumstances will the super large image sampling function be turned on?

1. The image is a type supported by BitmapRegionDecoder
2. Bitmap size is smaller than the original image
3. Images are loaded via Sketch

### Lifecycle

[SketchZoomImageView] can monitor the status of Lifecycle, pause the super large image sampling and
release the Bitmap of all fragments in the pause state, and resume the super large image sampling
and reload the fragments in the resume state, so that it can switch to the background or not display
when the Fragment or Activity Actively release memory

[SketchZoomImageView] will obtain the Lifecycle from [DisplayRequest], and [DisplayRequest] will
first obtain the Lifecycle from [SketchZoomImageView].context. In this way, what is usually obtained
is the Lifecycle of the Activity, which is generally sufficient.

If [SketchZoomImageView] is used in a combination of ViewPager + Fragment, then you need to actively
set the Fragment's viewLifecycleOwner.lifecycle to [DisplayRequest], as follows:

```kotlin
class MyFragment : Fragment() {

    override fun onViewCreated(view: View) {
        // ...
        sketchZoomImageView.displayImage("https://www.sample.com/image.jpg") {
            lifecycle(viewLifecycleOwner.lifecycle)
        }
    }
}
```

### Super large image sampling monitoring

```kotlin
// Listen tile changes
sketchZoomImageView.addOnTileChangedListener {

}
```

[Sketch]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/Sketch.kt

[BitmapRegionDecoder]: https://developer.android.google.cn/reference/android/graphics/BitmapRegionDecoder.html

[PhotoView]: https://github.com/chrisbanes/PhotoView

[SketchZoomImageView]: ../../sketch-zoom/src/main/kotlin/com/github/panpf/sketch/zoom/SketchZoomImageView.kt

[ReadModeDecider]: ../../sketch-zoom/src/main/kotlin/com/github/panpf/sketch/zoom/ReadModeDecider.kt

[LongImageReadModeDecider]: ../../sketch-zoom/src/main/kotlin/com/github/panpf/sketch/zoom/ReadModeDecider.kt

[DisplayRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/DisplayRequest.kt