package me.panpf.sketch.sample.event

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.view.View
import org.greenrobot.eventbus.EventBus
import java.lang.annotation.Inherited

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
annotation class RegisterEvent

class ActivityEventRegister : Application.ActivityLifecycleCallbacks {
    private val fragmentEventRegister = FragmentEventRegister()

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        if (activity?.javaClass?.isAnnotationPresent(RegisterEvent::class.java) == true) {
            EventBus.getDefault().register(activity)
        }

        if (activity != null && activity is FragmentActivity) {
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentEventRegister, true)
        }
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityResumed(activity: Activity?) {
    }

    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
        if (activity?.javaClass?.isAnnotationPresent(RegisterEvent::class.java) == true) {
            EventBus.getDefault().unregister(activity)
        }

        if (activity != null && activity is FragmentActivity) {
            activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentEventRegister)
        }
    }
}

class FragmentEventRegister : FragmentManager.FragmentLifecycleCallbacks() {
    override fun onFragmentViewCreated(fm: FragmentManager?, f: Fragment?, v: View?, savedInstanceState: Bundle?) {
        super.onFragmentViewCreated(fm, f, v, savedInstanceState)

        if (f?.javaClass?.isAnnotationPresent(RegisterEvent::class.java) == true) {
            EventBus.getDefault().register(f)
        }
    }

    override fun onFragmentViewDestroyed(fm: FragmentManager?, f: Fragment?) {
        super.onFragmentViewDestroyed(fm, f)

        if (f?.javaClass?.isAnnotationPresent(RegisterEvent::class.java) == true) {
            EventBus.getDefault().unregister(f)
        }
    }
}