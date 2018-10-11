package br.com.dsqz.chatnoir.sambatechtest.frontend.util

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction

@Suppress("unused")
object EssentialFragmentUtil {

    private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
        beginTransaction().func().commit()
    }

    private fun prepareManager(context: Context): FragmentManager {
        val fragmentActivity = context as FragmentActivity
        return fragmentActivity.supportFragmentManager
    }

    fun remove(context: Context, fragment: Fragment) {
        val supportFragmentManager = prepareManager(context)
        supportFragmentManager.inTransaction { remove(fragment) }
    }

    fun add(context: Context, layoutContainerId: Int, fragment: Fragment, extras: Bundle?, tag: String) {
        val supportFragmentManager = prepareManager(context)
        if (extras != null) {
            fragment.arguments = extras
        }
        supportFragmentManager.inTransaction { add(layoutContainerId, fragment, tag) }
    }

    fun replace(context: Context, layoutContainerId: Int, fragment: Fragment, extras: Bundle?, tag: String) {
        val supportFragmentManager = prepareManager(context)
        if (extras != null) {
            fragment.arguments = extras
        }
        supportFragmentManager.inTransaction { replace(layoutContainerId, fragment, tag) }
    }

    fun getFragmentByTag(context: Context, fragmentTag: String): Fragment? {
        val supportFragmentManager = prepareManager(context)
        return supportFragmentManager.findFragmentByTag(fragmentTag)
    }
}
