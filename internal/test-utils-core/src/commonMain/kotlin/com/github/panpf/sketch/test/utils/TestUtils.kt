package com.github.panpf.sketch.test.utils

import kotlin.math.pow


fun Long.pow(n: Int): Long = this.toDouble().pow(n).toLong()