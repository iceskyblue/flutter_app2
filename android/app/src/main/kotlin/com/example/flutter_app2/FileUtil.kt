package com.example.flutter_app2

import android.content.res.AssetManager
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

/**
 * @author yangfeng
 * @date 2020/8/10
 */
class FileUtil {
    companion object {
        fun copySo(apkPath: String, libName: String, toPath: String) {
            Log.e("FileUtil", "copy from= " + apkPath + "\nlibName=" + libName + "\n toPath=" + toPath)
            val zipFile = ZipFile(apkPath)
            val zipInput = ZipInputStream(FileInputStream(apkPath))
            var entry: ZipEntry? = null
            val outFile = File(toPath)
            if (!outFile.parentFile.exists()) {
                outFile.parentFile.mkdirs()
            }
            val soOutput = FileOutputStream(toPath)

            while ({ entry = zipInput.nextEntry; entry }() != null) {
                if (!entry!!.isDirectory && entry!!.name.endsWith(libName)) {
                    Log.e("FileUtil", "copy entry=" + entry!!.name)
                    val buffer = ByteArray(entry!!.size.toInt())
                    zipFile.getInputStream(entry).use {
                        var len = -1
                        soOutput.use { out ->
                            while ({ len = it.read(buffer); len }() != -1) {
                                out.write(buffer, 0, len)
                            }
                        }
                    }

                    return
                }
            }
        }

        fun getArc(nativePath: String): String {
            if (nativePath.endsWith("arm64")) {
                return "arm64-v8a"
            }

            return "armeabi-v7a"
        }

        fun copyFile(assets: AssetManager, name: String, toPath: String) {
            Log.e("FileUtil", "toPath= " + toPath)
            val input = assets.open(name)
            val file = File(toPath)
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
            val output = FileOutputStream(file)
            var buff = ByteArray(1024 * 1000)
            var len = -1
            input.use { reader ->
                output.use {
                    while ({ len = reader.read(buff); len }() != -1) {
                        it.write(buff, 0, len)
                    }
                }
            }

        }
    }
}