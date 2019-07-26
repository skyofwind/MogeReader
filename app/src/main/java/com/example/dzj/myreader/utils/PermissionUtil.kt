package com.example.dzj.myreader.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * @author: ${User}
 * @date: ${Date}
 *
 */
class PermissionUtil{
    companion object {
        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        fun initPermission(context : Context) {
            val permissions = arrayOf<String>(
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.MODIFY_AUDIO_SETTINGS,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_SETTINGS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE
            )

            val toApplyList = ArrayList<String>()

            for (perm in permissions) {
                if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(context, perm)) {
                    toApplyList.add(perm)
                    // 进入到这里代表没有权限.
                }
            }
            val tmpList = arrayOfNulls<String>(toApplyList.size)
            if (!toApplyList.isEmpty()) {
                ActivityCompat.requestPermissions(context as Activity, toApplyList.toArray(tmpList), 123)
            }
        }
    }
}