package br.com.dsqz.chatnoir.sambatechtest

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.test.InstrumentationRegistry
import android.util.Log
import org.junit.Assume

open class BaseBarista {

    private var getPermissions = false

    private fun hasNeededPermission(context: Context, permissionNeeded: String): Boolean {
        val permissionStatus = context.checkPermission(permissionNeeded, android.os.Process.myPid(), android.os.Process.myUid())
        return permissionStatus == PackageManager.PERMISSION_GRANTED
    }

    /**
     *
     *
     * Assume.assumeTrue("This test expects you to not have the permission granted. Remember to clear data.",
     *       hasNeededPermission(InstrumentationRegistry.getTargetContext(), Manifest.permission.ACCESS_FINE_LOCATION))
     */
    fun baseSetup(tagName: String, permissionNeededArr: List<String> = arrayListOf()) {
        Log.i(tagName, "[START] - Setup $tagName")
        Assume.assumeTrue("This test needs to run on a device with Android 23 or above",
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)

        try {
            permissionNeededArr.forEach {
                Assume.assumeTrue("This test expects you to not have the permission granted. Remember to clear data.",
                        hasNeededPermission(InstrumentationRegistry.getTargetContext(), it))
            }
        } catch (e: Exception) {
            Log.i(tagName, "Try get permissions...")
            getPermissions = true
        }
    }


    fun baseTearDown(tagName: String) {
        Log.i(tagName, "[FINISH] - Setup $tagName")
    }

}

