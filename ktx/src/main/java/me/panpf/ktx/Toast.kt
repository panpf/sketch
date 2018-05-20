@file:Suppress("RedundantVisibilityModifier", "unused")

package me.panpf.ktx

import android.content.Context
import android.support.annotation.StringRes
import android.widget.Toast

public fun Context.shortToast(@StringRes messageId: Int) =
        Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show()

public fun Context.shortToast(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

public fun Context.shortToast(@StringRes messageId: Int, vararg params: Any) =
        Toast.makeText(this, getString(messageId, params), Toast.LENGTH_SHORT).show()

public fun Context.shortToast(message: String, vararg params: Any) =
        Toast.makeText(this, message.format(params), Toast.LENGTH_SHORT).show()


public fun Context.longToast(@StringRes messageId: Int) =
        Toast.makeText(this, getString(messageId), Toast.LENGTH_LONG).show()

public fun Context.longToast(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

public fun Context.longToast(@StringRes messageId: Int, vararg params: Any) =
        Toast.makeText(this, getString(messageId, params), Toast.LENGTH_LONG).show()

public fun Context.longToast(message: String, vararg params: Any) =
        Toast.makeText(this, message.format(params), Toast.LENGTH_LONG).show()


public fun android.support.v4.app.Fragment.shortToast(@StringRes messageId: Int) =
        this.context?.let { Toast.makeText(it, getString(messageId), Toast.LENGTH_SHORT).show() }

public fun android.support.v4.app.Fragment.shortToast(message: String) =
        this.context?.let { Toast.makeText(it, message, Toast.LENGTH_SHORT).show() }

public fun android.support.v4.app.Fragment.shortToast(@StringRes messageId: Int, vararg params: Any) =
        this.context?.let { Toast.makeText(it, getString(messageId, params), Toast.LENGTH_SHORT).show() }

public fun android.support.v4.app.Fragment.shortToast(message: String, vararg params: Any) =
        this.context?.let { Toast.makeText(it, message.format(params), Toast.LENGTH_SHORT).show() }


public fun android.support.v4.app.Fragment.longToast(@StringRes messageId: Int) =
        this.context?.let { Toast.makeText(it, getString(messageId), Toast.LENGTH_LONG).show() }

public fun android.support.v4.app.Fragment.longToast(message: String) =
        this.context?.let { Toast.makeText(it, message, Toast.LENGTH_LONG).show() }

public fun android.support.v4.app.Fragment.longToast(@StringRes messageId: Int, vararg params: Any) =
        this.context?.let { Toast.makeText(it, getString(messageId, params), Toast.LENGTH_LONG).show() }

public fun android.support.v4.app.Fragment.longToast(message: String, vararg params: Any) =
        this.context?.let { Toast.makeText(it, message.format(params), Toast.LENGTH_LONG).show() }


public fun android.app.Fragment.shortToast(@StringRes messageId: Int) =
        this.activity?.let { Toast.makeText(it, getString(messageId), Toast.LENGTH_SHORT).show() }

public fun android.app.Fragment.shortToast(message: String) =
        this.activity?.let { Toast.makeText(it, message, Toast.LENGTH_SHORT).show() }

public fun android.app.Fragment.shortToast(@StringRes messageId: Int, vararg params: Any) =
        this.activity?.let { Toast.makeText(it, getString(messageId, params), Toast.LENGTH_SHORT).show() }

public fun android.app.Fragment.shortToast(message: String, vararg params: Any) =
        this.activity?.let { Toast.makeText(it, message.format(params), Toast.LENGTH_SHORT).show() }


public fun android.app.Fragment.longToast(@StringRes messageId: Int) =
        this.activity?.let { Toast.makeText(it, getString(messageId), Toast.LENGTH_LONG).show() }

public fun android.app.Fragment.longToast(message: String) =
        this.activity?.let { Toast.makeText(it, message, Toast.LENGTH_LONG).show() }

public fun android.app.Fragment.longToast(@StringRes messageId: Int, vararg params: Any) =
        this.activity?.let { Toast.makeText(it, getString(messageId, params), Toast.LENGTH_LONG).show() }

public fun android.app.Fragment.longToast(message: String, vararg params: Any) =
        this.activity?.let { Toast.makeText(it, message.format(params), Toast.LENGTH_LONG).show() }