package com.example.theexplorer.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class that provides helper methods for handling images and files.
 */
public class Utils {
    // Holds a reference to the image file.
    public static File image;

    /**
     * Retrieves the gallery path of the given URI and applies necessary modifications
     * such as rotation based on the Exif information of the image.
     *
     * @param context The context used to access the content resolver.
     * @param uri     The URI of the image to be processed.
     * @return The modified URI with the correct gallery path.
     */
    public static Uri getPathGallery(Context context, Uri uri) {

        String fileName = getFileName(context, uri);
        File file = new File(context.getExternalCacheDir(), fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (OutputStream outputStream = new FileOutputStream(file);
             InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            copyStream(inputStream, outputStream); //Simply reads input to output stream
            String timeStamp =
                    new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            } else {
                storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            }
            image = File.createTempFile(imageFileName, ".jpg", storageDir);

            InputStream is = context.getContentResolver().openInputStream(uri);
            Bitmap bitmapOrg1 = BitmapFactory.decodeStream(is);

            ExifInterface ei = new ExifInterface(image);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            Bitmap rotatedBitmap = null;
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmapOrg1, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmapOrg1, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmapOrg1, 270);
                    break;

                default:
                    rotatedBitmap = bitmapOrg1;
            }

            Bitmap bitmapOrg = Bitmap.createScaledBitmap(rotatedBitmap, rotatedBitmap.getWidth(), rotatedBitmap.getHeight(), false);
            FileOutputStream out = new FileOutputStream(image);
            bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 100, out);
            is.close();

            outputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Uri.fromFile(file);

    }

    /**
     * Copies the content of the source InputStream to the target OutputStream.
     *
     * @param source The input stream to be copied.
     * @param target The output stream where the content will be copied.
     * @throws IOException If an I/O error occurs.
     */
    public static void copyStream(InputStream source, OutputStream target) throws IOException {
        byte[] buf = new byte[8192];
        int length;
        while ((length = source.read(buf)) != -1) {
            target.write(buf, 0, length);
        }
    }

    /**
     * Retrieves the file name for the given URI.
     *
     * @param context The context used to access the content resolver.
     * @param uri     The URI of the file.
     * @return The file name with extension if available, otherwise a temporary file name with extension.
     */
    public static String getFileName(Context context, Uri uri) {
        String fileName = getFileNameFromCursor(context, uri);
        if (fileName == null) {
            String fileExtension = getFileExtension(context, uri);
            fileName = "temp_file" + (fileExtension != null ? "." + fileExtension : "");
        } else if (!fileName.contains(".")) {
            String fileExtension = getFileExtension(context, uri);
            fileName = fileName + "." + fileExtension;
        }
        return fileName;
    }

    /**
     * Retrieves the file extension for the given URI.
     *
     * @param context The context used to access the content resolver.
     * @param uri     The URI of the file.
     * @return The file extension, or null if not found.
     */

    public static String getFileExtension(Context context, Uri uri) {
        String fileType = context.getContentResolver().getType(uri);
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType);
    }

    /**
     * Retrieves the file name from the cursor for the given URI.
     *
     * @param context The context used to access the content resolver.
     * @param uri     The URI of the file.
     * @return The file name, or null if not found.
     */
    public static String getFileNameFromCursor(Context context, Uri uri) {
        Cursor fileCursor = context.getContentResolver().query(uri, new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null);
        String fileName = null;
        if (fileCursor != null && fileCursor.moveToFirst()) {
            int cIndex = fileCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (cIndex != -1) {
                fileName = fileCursor.getString(cIndex);
            }
        }
        return fileName;
    }

    /**
     * Rotates the given bitmap image by the specified angle.
     *
     * @param source The bitmap image to be rotated.
     * @param angle  The rotation angle in degrees.
     * @return The rotated bitmap image.
     */
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

}
