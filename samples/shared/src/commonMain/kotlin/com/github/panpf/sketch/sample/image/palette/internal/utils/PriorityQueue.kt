package com.kmpalette.palette.internal.utils

internal class PriorityQueue<T>(
    private val comparator: Comparator<in T>,
) : Collection<T> {

    private var array: Array<T?>? = null
    private var _size: Int = 0

    fun peek(): T? =
        array?.takeUnless { isEmpty() }?.get(0)

    fun offer(item: T) {
        var arr: Array<T?>? = array
        if (arr == null) {
            arr = newArray()
        } else if (_size == arr.size) {
            arr = arr.copyOf(_size * 2)
        }
        array = arr

        val lastIndex = _size++
        arr[lastIndex] = item
        @Suppress("UNCHECKED_CAST")
        (arr as Array<T>).heapifyUp(lastIndex, comparator)
    }

    fun poll(): T? {
        val arr = array
        if ((arr == null) || isEmpty()) {
            return null
        }

        val lastIndex = --_size
        val item = arr[0]
        arr[0] = arr[lastIndex]
        arr[lastIndex] = null
        @Suppress("UNCHECKED_CAST")
        (arr as Array<T>).heapifyDown(0, _size, comparator)

        return item
    }

    fun clear() {
        array = null
        _size = 0
    }

    override val size: Int
        get() = _size

    override fun isEmpty(): Boolean {
        return _size == 0
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        return elements.all { array?.contains(it) ?: false }
    }

    override fun contains(element: T): Boolean {
        return array?.contains(element) ?: false
    }

    /**
     * The doc is derived from Java PriorityQueue.
     *
     * Returns an iterator over the elements in this queue. The
     * iterator does not return the elements in any particular order.
     *
     * @return an iterator over the elements in this queue
     */
    override fun iterator(): Iterator<T> =
        object : Iterator<T> {
            private var index = 0

            override fun hasNext(): Boolean = index < _size

            @Suppress("UNCHECKED_CAST")
            override fun next(): T {
                val arr = array?.takeIf { index < _size } ?: throw NoSuchElementException()

                return arr[index++] as T
            }
        }

    private companion object {

        private const val INITIAL_CAPACITY = 8

        @Suppress("UNCHECKED_CAST")
        private fun <T> newArray(): Array<T?> = arrayOfNulls<Any?>(INITIAL_CAPACITY) as Array<T?>

        private fun <T> Array<T>.heapifyDown(
            index: Int,
            actualSize: Int,
            comparator: Comparator<in T>
        ) {
            val leftChildIndex = index * 2 + 1
            if (leftChildIndex >= actualSize) {
                return
            }

            val rightChildIndex = leftChildIndex + 1

            val childIndex =
                if (rightChildIndex >= actualSize) {
                    leftChildIndex
                } else {
                    val leftChildValue = get(leftChildIndex)
                    val rightChildValue = get(rightChildIndex)
                    if (comparator.compare(
                            leftChildValue,
                            rightChildValue
                        ) < 0
                    ) leftChildIndex else rightChildIndex
                }

            if (comparator.compare(get(childIndex), get(index)) < 0) {
                swap(index, childIndex)
                heapifyDown(childIndex, actualSize, comparator)
            }
        }

        private fun <T> Array<T>.heapifyUp(index: Int, comparator: Comparator<in T>) {
            val parentIndex = if (index % 2 == 0) index / 2 - 1 else index / 2
            if (parentIndex < 0) {
                return
            }

            if (comparator.compare(get(parentIndex), get(index)) > 0) {
                swap(index, parentIndex)
                heapifyUp(parentIndex, comparator)
            }
        }

        private fun <T> Array<T>.swap(first: Int, second: Int) {
            val temp = get(first)
            set(first, get(second))
            set(second, temp)
        }
    }
}
