package com.example.galleryscroll.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.IOException;

public class BitmapUtils {
    public static Bitmap decodeSampledBitmapFromResource(String photo, int reqWidth, int reqHeight, BitmapFactory.Options options, boolean orientFlag) {

        // First decode with inJustDecodeBounds=true to check dimensions
        // final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photo, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap res = BitmapFactory.decodeFile(photo, options);
        if (orientFlag && res != null) {

            ExifInterface exif = null;
            try {
                exif = new ExifInterface(photo);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int orient = getOrient(orientation);

            Matrix matrix = new Matrix();
            matrix.postRotate(orient * 90);
            res = Bitmap.createBitmap(res, 0, 0, res.getWidth(),
                    res.getHeight(), matrix, true);
        }
        return res;
    }

    public static int getOrient(int orientation) {
        int orient = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            orient = 1;
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            orient = 2;
        } if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            orient = 3;
        }
        return orient;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        return calculateInSampleSize(options.outWidth, options.outHeight, reqWidth, reqHeight);
    }

    public static int calculateInSampleSize(int width, int height, int reqWidth, int reqHeight) {
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
