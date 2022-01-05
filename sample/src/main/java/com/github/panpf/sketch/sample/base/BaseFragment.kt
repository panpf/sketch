/*
 * Copyright (C) 2021 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.sample.base

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import java.util.*

abstract class BaseFragment : Fragment() {

    private val userVisibleChangedListenerList = LinkedList<UserVisibleChangedListener>()

    val isViewCreated: Boolean
        get() = view != null

    val isVisibleToUser: Boolean
        get() = isResumed && userVisibleHint

    override fun onPause() {
        super.onPause()
        if (userVisibleHint) {
            userVisibleChangedListenerList.forEach {
                it.onUserVisibleChanged(false)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (userVisibleHint) {
            userVisibleChangedListenerList.forEach {
                it.onUserVisibleChanged(true)
            }
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isResumed) {
            userVisibleChangedListenerList.forEach {
                it.onUserVisibleChanged(isVisibleToUser)
            }
        }
    }

    fun registerUserVisibleChangedListener(
        owner: LifecycleOwner,
        userVisibleChangedListener: UserVisibleChangedListener
    ) {
        userVisibleChangedListenerList.add(userVisibleChangedListener)
        owner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event.targetState == Lifecycle.State.DESTROYED) {
                userVisibleChangedListenerList.remove(userVisibleChangedListener)
            }
        })
    }

    fun registerUserVisibleChangedListener(
        userVisibleChangedListener: UserVisibleChangedListener
    ) {
        userVisibleChangedListenerList.add(userVisibleChangedListener)
    }

    fun unregisterUserVisibleChangedListener(
        userVisibleChangedListener: UserVisibleChangedListener
    ) {
        userVisibleChangedListenerList.remove(userVisibleChangedListener)
    }
}