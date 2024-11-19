package com.example.youtube_android.utils;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;


public class FileUtil {

    public static File compressFile(File originalFile, String compressedFileName) throws IOException {
        File compressedFile = new File(originalFile.getParent(), compressedFileName);

        try (FileInputStream fis = new FileInputStream(originalFile);
             FileOutputStream fos = new FileOutputStream(compressedFile);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             GZIPOutputStream gzipOS = new GZIPOutputStream(bos)) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                gzipOS.write(buffer, 0, len);
            }
        }

        return compressedFile;
    }
}