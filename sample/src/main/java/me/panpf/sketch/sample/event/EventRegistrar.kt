package me.panpf.sketch.sample.event

import android.annotation.TargetApi
import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.FragmentActivity
import android.view.View
import org.greenrobot.eventbus.EventBus
import java.lang.annotation.Inherited
import java.util.*
import android.app.Fragment as OriginFragment
import android.app.FragmentManager as OriginFragmentManager
import android.support.v4.app.Fragment as SupportFragment
import android.support.v4.app.FragmentManager as SupportFragmentManager

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
annotation class RegisterEvent

@RequiresApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
@TargetApi(Build.VERSION_CODES.O)
class ActivityEventRegistrar : Application.ActivityLifecycleCallbacks {

    private val supportFragmentEventRegister = SupportFragmentEventRegistrar()
    @RequiresApi(Build.VERSION_CODES.O)
    private var originFragmentEventRegistrar: Any? = null
    private val activityCodes = LinkedList<String>()

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        activity ?: return

        // onActivityCreated 会先于子类的 onCreate() 执行，这时候子类尚未初始化完成，如果子类的事件方法需要依赖 onCreate() 初始化，比如 view，
        // 那么在这里注册事件的话，如果子类立马就收到事件就会因依赖未初始化而崩溃

        if (activity is FragmentActivity) {
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(supportFragmentEventRegister, true)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val registrar = if (originFragmentEventRegistrar == null) {
                val newRegistrar = OriginFragmentEventRegistrar()
                originFragmentEventRegistrar = newRegistrar
                newRegistrar
            } else {
                originFragmentEventRegistrar as OriginFragmentEventRegistrar
            }
            activity.fragmentManager.registerFragmentLifecycleCallbacks(registrar, true)
        }
    }

    override fun onActivityStarted(activity: Activity?) {
        activity?.let {
            val hashCode = it.hashCode().toString()
            if (!activityCodes.contains(hashCode)) {
                activityCodes.add(hashCode)

                if (it.javaClass.isAnnotationPresent(RegisterEvent::class.java)) {
                    EventBus.getDefault().register(activity)
                }
            }
        }
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
        activity ?: return

        activityCodes.remove(activity.hashCode().toString())

        if (activity.javaClass.isAnnotationPresent(RegisterEvent::class.java)) {
            EventBus.getDefault().unregister(activity)
        }

        if (activity is FragmentActivity) {
            activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(supportFragmentEventRegister)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (originFragmentEventRegistrar != null) {
                activity.fragmentManager.unregisterFragmentLifecycleCallbacks(originFragmentEventRegistrar as OriginFragmentEventRegistrar)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
@TargetApi(Build.VERSION_CODES.O)
class SupportFragmentEventRegistrar : SupportFragmentManager.FragmentLifecycleCallbacks() {
    override fun onFragmentViewCreated(fm: SupportFragmentManager?, f: SupportFragment?, v: View?, savedInstanceState: Bundle?) {
        super.onFragmentViewCreated(fm, f, v, savedInstanceState)

        if (f?.javaClass?.isAnnotationPresent(RegisterEvent::class.java) == true) {
            EventBus.getDefault().register(f)
        }
    }

    override fun onFragmentViewDestroyed(fm: SupportFragmentManager?, f: SupportFragment?) {
        super.onFragmentViewDestroyed(fm, f)

        if (f?.javaClass?.isAnnotationPresent(RegisterEvent::class.java) == true) {
            EventBus.getDefault().unregister(f)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
class OriginFragmentEventRegistrar : OriginFragmentManager.FragmentLifecycleCallbacks() {
    override fun onFragmentViewCreated(fm: OriginFragmentManager?, f: OriginFragment?, v: View?, savedInstanceState: Bundle?) {
        super.onFragmentViewCreated(fm, f, v, savedInstanceState)

        if (f?.javaClass?.isAnnotationPresent(RegisterEvent::class.java) == true) {
            EventBus.getDefault().register(f)
        }
    }

    override fun onFragmentViewDestroyed(fm: OriginFragmentManager?, f: OriginFragment?) {
        super.onFragmentViewDestroyed(fm, f)

        if (f?.javaClass?.isAnnotationPresent(RegisterEvent::class.java) == true) {
            EventBus.getDefault().unregister(f)
        }
    }
}