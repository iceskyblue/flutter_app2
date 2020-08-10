package com.example.flutter_app2

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.os.Bundle
import android.os.Process
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.io.File


class MainActivity : FlutterActivity() {
    var asset: AssetManager? = null
    var channel: MethodChannel? = null
    lateinit var amOrigin: AssetManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (null == channel) {
            channel = MethodChannel(flutterEngine?.dartExecutor, "com.test")
            channel!!.setMethodCallHandler(object : MethodChannel.MethodCallHandler {
                override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
                    releaseSoAndResource()
                    AlertDialog.Builder(this@MainActivity)
                            .setMessage("更新完成，重启生效")
                            .setCancelable(false)
                            .setPositiveButton("确定", object: DialogInterface.OnClickListener{
                                override fun onClick(dialog: DialogInterface?, which: Int) {
                                    restartApp()
                                }

                            })
                            .show()
                }
            })
        }
    }

    private fun releaseSoAndResource() {
        FileUtil.copyFile(amOrigin,"app-release-1.apk", filesDir.absolutePath + "/app-release-1.apk")
        FileUtil.copySo(filesDir.absolutePath + "/app-release-1.apk",
                "lib/" + FileUtil.getArc(applicationInfo.nativeLibraryDir) + "/libapp.so",
                filesDir.parentFile.absolutePath + "/lib/libapp1.so")
        val sof = File(filesDir.parentFile.absolutePath + "/lib/libapp1.so")
        sof.setExecutable(true)
        sof.setReadable(true)
    }

    @SuppressLint("SoonBlockedPrivateApi")
    override fun getAssets(): AssetManager {
        if (null == asset && File(this.filesDir.absolutePath + "/app-release-1.apk").exists()) {

            val info = packageManager.getPackageArchiveInfo(this.filesDir.absolutePath + "/app-release-1.apk", PackageManager.MATCH_UNINSTALLED_PACKAGES or PackageManager.GET_ACTIVITIES).applicationInfo
            info.publicSourceDir = this.filesDir.absolutePath + "/app-release-1.apk"
            asset = packageManager.getResourcesForApplication(info).assets
        }

        if (!::amOrigin.isInitialized) {
            amOrigin = super.getAssets()
        }

        return asset ?: amOrigin
    }

    private fun restartApp() {
        val intent = baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)
        intent.putExtra("REBOOT", "reboot")
        val restartIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val mgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr[AlarmManager.RTC, System.currentTimeMillis() + 1000] = restartIntent
        Process.killProcess(Process.myPid())
    }


}
