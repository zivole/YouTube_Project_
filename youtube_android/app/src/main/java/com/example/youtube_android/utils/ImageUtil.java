package com.example.youtube_android.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtil {
    private static final String preBase64 = "data:image/jpeg;base64,";
    private static final String IMAGE_DIRECTORY = "images";
    public static boolean isBase64Image(String imageData) {
        if (imageData.startsWith("data:image/jpeg;base64,") || imageData.startsWith("data:image/png;base64,")) {
            return true;
        }
        try {
            byte[] decodedBytes = Base64.decode(imageData, Base64.DEFAULT);
            return decodedBytes.length > 0;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // Method to resize, compress, and encode image to Base64
    public static String resizeAndCompressAndEncodeImage(Bitmap bitmap, int maxSize, int quality) {
        Bitmap resizedBitmap = resizeBitmap(bitmap, maxSize);
        return compressAndEncodeBase64Image(resizedBitmap, quality);
    }

    // Method to resize Bitmap
    private static Bitmap resizeBitmap(Bitmap bitmap, int maxSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width > height) {
            height = (height * maxSize) / width;
            width = maxSize;
        } else {
            width = (width * maxSize) / height;
            height = maxSize;
        }
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    // Method to compress and encode Bitmap to Base64
    public static String compressAndEncodeBase64Image(Bitmap bitmap, int quality) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return preBase64 + Base64.encodeToString(byteArray, Base64.DEFAULT).replace("\n", "");
    }

    // Method to decode Base64 to Bitmap with resizing options
    public static Bitmap decodeBase64ToBitmap(String base64Image, int reqWidth, int reqHeight) {
        String base64s;

        String[] base64Parts = base64Image.split(",");
        if (base64Parts.length > 1) {
            base64s = base64Parts[1];  // Use the actual base64 part
        } else {
            base64s = base64Parts[0];  // Fallback to use the entire string if no prefix is found
        }

        byte[] decodedBytes = Base64.decode(base64s, Base64.DEFAULT);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length, options);
    }

    // Method to calculate the optimal inSampleSize
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    // Method to convert URI to Base64
    public static String uriToBase64(Uri uri, Context context) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        byte[] bytes = getBytes(inputStream);
        return preBase64 + (Base64.encodeToString(bytes, Base64.DEFAULT)).replace("\n", "");
    }

    // Helper method to convert InputStream to byte array
    private static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    // Method to convert Base64 to URI
    public static Uri base64ToUri(String base64String, Context context) throws IOException {
        String base64s = base64String.split(",")[1];
        byte[] decodedBytes = Base64.decode(base64s, Base64.DEFAULT);
        File file = File.createTempFile("temp_image", null, context.getCacheDir());
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(decodedBytes);
        fos.flush();
        fos.close();
        return Uri.fromFile(file);
    }

    // Method to convert Bitmap to URI and save to external storage
    public static Uri bitmapToUri(Context context, Bitmap bitmap) {
        File imagesDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(imagesDirectory, "image_" + System.currentTimeMillis() + ".jpg");

        try (FileOutputStream outputStream = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Uri.fromFile(imageFile);
    }

    // Method to convert URI to Bitmap
    public static Bitmap uriToBitmap(Context context, Uri uri) {
        Bitmap bitmap = null;
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    // Method to save Base64 image as file and return the file path
    public static String saveBase64AsFile(Context context, String base64Image, String fileName) {
        File directory = new File(context.getFilesDir(), IMAGE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            String[] base64Parts = base64Image.split(",");
            if (base64Parts.length > 1) {
                byte[] decodedImage = Base64.decode(base64Parts[1], Base64.DEFAULT);
                fos.write(decodedImage);
            } else {
                throw new IllegalArgumentException("Invalid Base64 string format");
            }
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Save a bitmap to a file and return the file path
    public static String saveBitmapToFile(Context context, Bitmap bitmap, String fileName) {
        File directory = new File(context.getFilesDir(), IMAGE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(directory, fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Load a bitmap from a file path
    public static Bitmap loadBitmapFromFile(String filePath) {
        return BitmapFactory.decodeFile(filePath);
    }
}
