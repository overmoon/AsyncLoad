package my.fun.asyncload.imageloader.utils;

import android.webkit.MimeTypeMap;

import java.io.File;

/**
 * Created by admin on 2016/10/21.
 */
public class Utils {
    public static String getMimeType(File f) {
        String fileName = f.getName();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        return mimeType;
    }
}
