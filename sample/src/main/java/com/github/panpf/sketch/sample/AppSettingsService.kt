package com.github.panpf.sketch.sample

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.lifecycle.MutableLiveData
import com.github.panpf.sketch.sample.bean.LayoutMode

class AppSettingsService(val context: Context) {

    val photoListLayoutMode by lazy {
        EnumPrefsData(
            context,
            "photoListLayoutMode",
            LayoutMode.STAGGERED_GRID,
            { LayoutMode.valueOf(it) },
            { it.name }
        )
    }

    val showMimeTypeLogoInLIst by lazy {
        BooleanPrefsData(context, "showMimeTypeLogoInLIst", true)
    }

    val showProgressIndicatorInList by lazy {
        BooleanPrefsData(context, "showProgressIndicatorInList", true)
    }

    val showDataFrom by lazy {
        BooleanPrefsData(context, "showDataFrom", true)
    }

    val disabledAnimatableDrawableInList by lazy {
        BooleanPrefsData(context, "disabledAnimatableDrawableInList", false)
    }

    val saveCellularTrafficInList by lazy {
        BooleanPrefsData(context, "saveCellularTrafficInList", false)
    }

    val pauseLoadWhenScrollInList by lazy {
        BooleanPrefsData(context, "pauseLoadWhenScrollInList", false)
    }

    val memoryCacheDisabled by lazy {
        BooleanPrefsData(context, "memoryCacheDisabled", false)
    }
    val bitmapPoolDisabled by lazy {
        BooleanPrefsData(context, "bitmapPoolDisabled", false)
    }
    val diskCacheDisabled by lazy {
        BooleanPrefsData(context, "diskCacheDisabled", false)
    }

    val inPreferQualityOverSpeedEnabled by lazy {
        BooleanPrefsData(context, "inPreferQualityOverSpeedEnabled", false)
    }
    val lowQualityImageEnabled by lazy {
        BooleanPrefsData(context, "lowQualityImageEnabled", false)
    }
    val cacheProcessedImageEnabled by lazy {
        BooleanPrefsData(context, "cacheProcessedImageEnabled", true)
    }
    val correctImageOrientationEnabled by lazy {
        BooleanPrefsData(context, "correctImageOrientationEnabled", true)
    }

    val showImageFromFlagEnabled by lazy {
        BooleanPrefsData(context, "showImageFromFlagEnabled", false)
    }
    val scrollingPauseLoadEnabled by lazy {
        BooleanPrefsData(context, "scrollingPauseLoadEnabled", false)
    }

    //    val logLevel by lazy {
//        IntPrefsData(context, "logLevel", Logger.Level.INFO)
//    }
    val outLog2SdcardLevel by lazy {
        BooleanPrefsData(context, "outLog2SdcardLevel", false)
    }

    val readModeEnabled by lazy {
        BooleanPrefsData(context, "readModeEnabled", true)
    }
    val pauseBlockDisplayWhenPageNoVisibleEnabled by lazy {
        BooleanPrefsData(context, "pauseBlockDisplayWhenPageNoVisibleEnabled", true)
    }
    val threeLevelZoomModeEnabled by lazy {
        BooleanPrefsData(context, "threeLevelZoomModeEnabled", false)
    }
    val smallMapLocationAnimateEnabled by lazy {
        BooleanPrefsData(context, "smallMapLocationAnimateEnabled", true)
    }

    val playGifInListEnabled by lazy {
        BooleanPrefsData(context, "playGifInListEnabled", true)
    }
    val clickPlayGifEnabled by lazy {
        BooleanPrefsData(context, "clickPlayGifEnabled", false)
    }

    val showRoundedInPhotoListEnabled by lazy {
        BooleanPrefsData(context, "showRoundedInPhotoListEnabled", false)
    }
    val showRawImageInDetailEnabled by lazy {
        BooleanPrefsData(context, "showRawImageInDetailEnabled", false)
    }
    val showPressedStatusInListEnabled by lazy {
        BooleanPrefsData(context, "showPressedStatusInListEnabled", true)
    }
    val showImageDownloadProgressEnabled by lazy {
        BooleanPrefsData(context, "showImageDownloadProgressEnabled", false)
    }
    val mobileNetworkPauseDownloadEnabled by lazy {
        BooleanPrefsData(context, "mobileNetworkPauseDownloadEnabled", false)
    }
    val thumbnailModeEnabled by lazy {
        BooleanPrefsData(context, "thumbnailModeEnabled", true)
    }
}

//class PrefsData<T: Any>(
//    context: Context,
//    val key: String,
//    prefsName: String? = null,
//) {
//    private val preference: SharedPreferences = if (prefsName != null) {
//        context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
//    } else {
//        PreferenceManager.getDefaultSharedPreferences(context)
//    }
//    private val liveData = MutableLiveData<T>()
//
//    private val mObservers: SafeIterableMap<androidx.lifecycle.Observer<in T?>, ObserverWrapper> =
//        SafeIterableMap<androidx.lifecycle.Observer<in T?>, ObserverWrapper>()
//
//    val value: T
//        get() = liveData.value as T
//
//    fun setValue(value: T?){
//        liveData.postValue(value)
//    }
//
//    fun observe(owner: LifecycleOwner, observer: Observer<T>) {
//        liveData.observe(owner, {
//            observer.onChanged(it)
//        })
//    }
//}
//
//fun interface Observer<T> {
//    /**
//     * Called when the data is changed.
//     * @param t  The new data
//     */
//    fun onChanged(t: T)
//}

class BooleanPrefsData(
    context: Context,
    private val key: String,
    private val defaultValue: Boolean = false,
    prefsName: String? = null,
) : MutableLiveData<Boolean>() {

    private val preference: SharedPreferences = if (prefsName != null) {
        context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    } else {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    init {
        value = preference.getBoolean(key, defaultValue)
    }

    override fun postValue(value: Boolean?) {
        preference.edit().apply {
            if (value != null) {
                putBoolean(key, value)
            } else {
                remove(key)
            }
        }.apply()
        super.postValue(value ?: defaultValue)
    }

    override fun setValue(value: Boolean?) {
        preference.edit().apply {
            if (value != null) {
                putBoolean(key, value)
            } else {
                remove(key)
            }
        }.apply()
        super.setValue(value ?: defaultValue)
    }
}

class BooleanNullablePrefsData(
    context: Context,
    private val key: String,
    prefsName: String? = null,
) : MutableLiveData<Boolean>() {

    private val preference: SharedPreferences = if (prefsName != null) {
        context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    } else {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    init {
        val result = preference.getBoolean(key, false)
        val result1 = if (!result && preference.getBoolean(key, true)) {
            null
        } else {
            result
        }
        setValue(result1)
    }

    override fun postValue(value: Boolean?) {
        preference.edit().apply {
            if (value != null) {
                putBoolean(key, value)
            } else {
                remove(key)
            }
        }.apply()
        super.postValue(value)
    }

    override fun setValue(value: Boolean?) {
        preference.edit().apply {
            if (value != null) {
                putBoolean(key, value)
            } else {
                remove(key)
            }
        }.apply()
        super.setValue(value)
    }
}

class StringPrefsData(
    context: Context,
    private val key: String,
    private val defaultValue: String,
    prefsName: String? = null,
) : MutableLiveData<String>() {

    private val preference: SharedPreferences = if (prefsName != null) {
        context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    } else {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    init {
        setValue(preference.getString(key, defaultValue))
    }

    override fun postValue(value: String?) {
        preference.edit().apply {
            if (value != null) {
                putString(key, value)
            } else {
                remove(key)
            }
        }.apply()
        super.postValue(value ?: defaultValue)
    }

    override fun setValue(value: String?) {
        preference.edit().apply {
            if (value != null) {
                putString(key, value)
            } else {
                remove(key)
            }
        }.apply()
        super.setValue(value ?: defaultValue)
    }
}

class StringNullablePrefsData(
    context: Context,
    private val key: String,
    prefsName: String? = null,
) : MutableLiveData<String>() {

    private val preference: SharedPreferences = if (prefsName != null) {
        context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    } else {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    init {
        setValue(preference.getString(key, null))
    }

    override fun postValue(value: String?) {
        preference.edit().apply {
            if (value != null) {
                putString(key, value)
            } else {
                remove(key)
            }
        }.apply()
        super.postValue(value)
    }

    override fun setValue(value: String?) {
        preference.edit().apply {
            if (value != null) {
                putString(key, value)
            } else {
                remove(key)
            }
        }.apply()
        super.setValue(value)
    }
}

class IntPrefsData(
    context: Context,
    private val key: String,
    private val defaultValue: Int,
    prefsName: String? = null,
) : MutableLiveData<Int>() {

    private val preference: SharedPreferences = if (prefsName != null) {
        context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    } else {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    init {
        setValue(preference.getInt(key, defaultValue))
    }

    override fun postValue(value: Int?) {
        preference.edit().apply {
            if (value != null) {
                putInt(key, value)
            } else {
                remove(key)
            }
        }.apply()
        super.postValue(value ?: defaultValue)
    }

    override fun setValue(value: Int?) {
        preference.edit().apply {
            if (value != null) {
                putInt(key, value)
            } else {
                remove(key)
            }
        }.apply()
        super.setValue(value ?: defaultValue)
    }
}

class IntNullablePrefsData(
    context: Context,
    private val key: String,
    prefsName: String? = null,
) : MutableLiveData<Int>() {

    private val preference: SharedPreferences = if (prefsName != null) {
        context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    } else {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    init {
        val result = preference.getInt(key, Integer.MIN_VALUE)
        val result1 = if (
            result == Integer.MIN_VALUE
            && preference.getInt(key, Integer.MAX_VALUE) == Integer.MAX_VALUE
        ) {
            null
        } else {
            result
        }
        setValue(result1)
    }

    override fun postValue(value: Int?) {
        preference.edit().apply {
            if (value != null) {
                putInt(key, value)
            } else {
                remove(key)
            }
        }.apply()
        super.postValue(value)
    }

    override fun setValue(value: Int?) {
        preference.edit().apply {
            if (value != null) {
                putInt(key, value)
            } else {
                remove(key)
            }
        }.apply()
        super.setValue(value)
    }
}

class EnumPrefsData<T : Enum<T>>(
    context: Context,
    private val key: String,
    private val defaultValue: T,
    parse: (String) -> T,
    private val toStringValue: (T) -> String,
    prefsName: String? = null,
) : MutableLiveData<T>() {

    private val preference: SharedPreferences = if (prefsName != null) {
        context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    } else {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    init {
        value = parse(preference.getString(key, null) ?: defaultValue.name)
    }

    override fun postValue(value: T?) {
        preference.edit().apply {
            if (value != null) {
                putString(key, toStringValue(value))
            } else {
                remove(key)
            }
        }.apply()
        super.postValue(value ?: defaultValue)
    }

    override fun setValue(value: T?) {
        preference.edit().apply {
            if (value != null) {
                putString(key, toStringValue(value))
            } else {
                remove(key)
            }
        }.apply()
        super.setValue(value ?: defaultValue)
    }
}