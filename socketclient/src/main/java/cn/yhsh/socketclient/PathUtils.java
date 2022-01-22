package cn.yhsh.socketclient;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * @author xiayiye5
 * @date 2022/1/18 17:38
 */
public class PathUtils {
    private PathUtils() {
    }

    private static final PathUtils PATH_UTILS = new PathUtils();

    public static PathUtils getInstance() {
        return PATH_UTILS;
    }

    public void requestPath(Context context) {
        String cachePath = context.getExternalCacheDir() + "xiayiye.pcm";
        String fileCache = context.getFilesDir().getAbsolutePath();
        File codeCacheDir = context.getCodeCacheDir();
        File cacheDir = context.getCacheDir();
        File obbDir = context.getObbDir();
        File xiayiye5 = context.getDir("xiayiye5", Context.MODE_PRIVATE);
        Log.e("打印文件夹：", cachePath);
        Log.e("打印文件夹：", fileCache);
        Log.e("打印文件夹：", codeCacheDir.getAbsolutePath());
        Log.e("打印文件夹：", cacheDir.getAbsolutePath());
        Log.e("打印文件夹：", obbDir.getAbsolutePath());
        Log.e("打印文件夹：", xiayiye5.getAbsolutePath());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            File dataDir = context.getDataDir();
            Log.e("打印文件夹：", dataDir.getAbsolutePath());
        }
        File download = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        Log.e("打印文件夹", "路径！" + download.getAbsolutePath());
    }
}
