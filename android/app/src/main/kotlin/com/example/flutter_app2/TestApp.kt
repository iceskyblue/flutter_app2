package com.example.flutter_app2

import android.util.Log
import io.flutter.app.FlutterApplication
import java.io.File
import java.io.FileOutputStream

/**
 * @author yangfeng
 * @date 2020/8/6
 */
class TestApp: FlutterApplication() {
    override fun onCreate() {
        if (!File(filesDir.parentFile.absolutePath + "/lib/libapp.so").exists()) {
            FileUtil.copySo(applicationInfo.sourceDir, "lib/" + FileUtil.getArc(applicationInfo.nativeLibraryDir) + "/libapp.so",
                    filesDir.parentFile.absolutePath + "/lib/libapp.so")
            val sof = File(filesDir.parentFile.absolutePath + "/lib/libapp.so")
            sof.setExecutable(true)
            sof.setReadable(true)
        }

        var lib1 = File(filesDir.parentFile.absolutePath + "/lib/libapp1.so")
        if (lib1.exists()) {
            lib1.renameTo(File(filesDir.parentFile.absolutePath + "/lib/libapp.so"))
        }
        super.onCreate()
    }
}