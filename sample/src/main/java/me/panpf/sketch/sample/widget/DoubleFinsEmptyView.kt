package me.panpf.sketch.sample.widget

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.LinearLayout
import java.util.*

class DoubleFinsEmptyView : LinearLayout {
    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    private fun init(context: Context) {
        val boundsRect = Rect(0, 0, 100, 100)

        val fullRectList = ArrayList<Rect>()

        fullRectList.add(Rect(0, 0, 25, 25))
        fullRectList.add(Rect(25, 0, 50, 25))
        fullRectList.add(Rect(50, 0, 75, 25))
        fullRectList.add(Rect(75, 0, 100, 25))

        fullRectList.add(Rect(0, 25, 25, 50))
        fullRectList.add(Rect(25, 25, 50, 50))
        fullRectList.add(Rect(50, 25, 75, 50))
        fullRectList.add(Rect(75, 25, 100, 50))

        fullRectList.add(Rect(0, 50, 25, 75))
        fullRectList.add(Rect(25, 50, 50, 75))
        fullRectList.add(Rect(50, 50, 75, 75))
        fullRectList.add(Rect(75, 50, 100, 75))

        fullRectList.add(Rect(0, 75, 25, 100))
        fullRectList.add(Rect(25, 75, 50, 100))
        fullRectList.add(Rect(50, 75, 75, 100))
        fullRectList.add(Rect(75, 75, 100, 100))

        orientation = LinearLayout.VERTICAL

        val originView = FindEmptyView(context)
        originView.setBoundsRect(boundsRect)
        originView.setFullRectList(fullRectList)

        val findEmptyView = FindEmptyView(context)
        findEmptyView.setBoundsRect(boundsRect)
        val newFullRectList = mutableListOf<Rect>()
        newFullRectList += fullRectList
        val emptyRectList = findEmptyRect(boundsRect, newFullRectList)
        findEmptyView.setFullRectList(newFullRectList)
        findEmptyView.setEmptyRectList(emptyRectList)

        addView(originView, LinearLayout.LayoutParams(500, 500))
        addView(findEmptyView, LinearLayout.LayoutParams(500, 500))
    }

    companion object {

        fun findEmptyRect(rect: Rect, childRectList: MutableList<Rect>?): List<Rect>? {
            if (rect.isEmpty) {
                return null
            }

            var emptyRectList: MutableList<Rect>? = null
            if (childRectList == null || childRectList.isEmpty()) {
                emptyRectList = LinkedList<Rect>()
                emptyRectList.add(rect)
                return emptyRectList
            }

            // 按离左上角的距离排序
            childRectList.sortWith(Comparator { o1, o2 ->
                if (o1.top >= o2.bottom || o2.top >= o1.bottom) {
                    o1.top - o2.top
                } else {
                    o1.left - o2.left
                }
            })

            val left = rect.left
            var top = rect.top
            var right = 0
            var bottom = -1
            var lastRect: Rect? = null
            var childRect: Rect
            val rectIterator: MutableIterator<Rect> = childRectList.iterator()
            while (rectIterator.hasNext()) {
                childRect = rectIterator.next()

                val newLine = lastRect == null || childRect.top >= bottom
                if (newLine) {
                    // 首先要处理上一行的最后一个
                    if (lastRect != null) {
                        if (lastRect.right < rect.right) {
                            val rightEmptyRect = Rect(lastRect.right, top, rect.right, bottom)
                            if (emptyRectList == null) {
                                emptyRectList = LinkedList<Rect>()
                            }
                            emptyRectList.add(rightEmptyRect)
                        }
                    }

                    // 然后要更新top和bottom
                    top = if (bottom != -1) bottom else top
                    bottom = childRect.bottom

                    // 左边有空隙
                    if (childRect.left > left) {
                        val leftEmptyRect = Rect(left, childRect.top, childRect.left, childRect.bottom)
                        if (emptyRectList == null) {
                            emptyRectList = LinkedList<Rect>()
                        }
                        emptyRectList.add(leftEmptyRect)
                    }

                    // 顶部有空隙
                    if (childRect.top > top) {
                        val topEmptyRect = Rect(left, top, childRect.right, childRect.top)
                        if (emptyRectList == null) {
                            emptyRectList = LinkedList<Rect>()
                        }
                        emptyRectList.add(topEmptyRect)
                    }

                    right = childRect.right
                    lastRect = childRect
                } else {
                    val available = childRect.bottom == lastRect!!.bottom
                    if (available) {
                        // 左边有空隙
                        if (childRect.left > right) {
                            val leftEmptyRect = Rect(right, top, childRect.left, bottom)
                            if (emptyRectList == null) {
                                emptyRectList = LinkedList<Rect>()
                            }
                            emptyRectList.add(leftEmptyRect)
                        }

                        // 顶部有空隙
                        if (childRect.top > top) {
                            val topEmptyRect = Rect(childRect.left, top, childRect.right, childRect.top)
                            if (emptyRectList == null) {
                                emptyRectList = LinkedList<Rect>()
                            }
                            emptyRectList.add(topEmptyRect)
                        }

                        right = childRect.right
                        lastRect = childRect
                    } else {
                        rectIterator.remove()
                    }
                }
            }

            // 最后的结尾处理
            if (right < rect.right) {
                val rightEmptyRect = Rect(right, top, rect.right, bottom)
                if (emptyRectList == null) {
                    emptyRectList = LinkedList<Rect>()
                }
                emptyRectList.add(rightEmptyRect)
            }

            if (bottom < rect.bottom) {
                val bottomEmptyRect = Rect(rect.left, bottom, rect.right, rect.bottom)
                if (emptyRectList == null) {
                    emptyRectList = LinkedList<Rect>()
                }
                emptyRectList.add(bottomEmptyRect)
            }

            return emptyRectList
        }
    }
}
