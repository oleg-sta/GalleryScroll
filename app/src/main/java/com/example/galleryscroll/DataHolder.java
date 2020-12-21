package com.example.galleryscroll;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.LruCache;

import com.example.galleryscroll.util.BitmapUtils;
import com.example.galleryscroll.util.DiskLruImageCache;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DataHolder {
    private static final String TAG = DataHolder.class.getSimpleName();
    public static final int THUMBNAIL_SIZE = 150;
    public static LruCache<String, Bitmap> mMemoryCache;
    private static DiskLruImageCache diskLruCacheImage;

    private static final int DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 50; // 10MB

    private static final DataHolder holder = new DataHolder();

    public static DiskLruImageCache getDiskLruImageCache(Context context) {
        if (diskLruCacheImage == null) {
            diskLruCacheImage = new DiskLruImageCache(context, "img", DEFAULT_DISK_CACHE_SIZE, Bitmap.CompressFormat.JPEG, 100);
        }
        return diskLruCacheImage;
    }

    public static DataHolder getInstance() {
        if (mMemoryCache == null) {
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

            final int cacheSize = maxMemory / 4;
            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getByteCount() / 1024;
                }
            };
        }
        return holder;
    }

    /**
     * Blocking mechanism of getting thumbnail photo. If photo exists in memory or disk cache it returns it, otherwise it doing creates thumbnails and put to cache either.
     * @param photo path to photo
     * @param context conext of the application
     * @return thumbnail photo
     */
    public Bitmap getLittleThumbnail(String photo, Context context) {
        return getLittleThumbnail(photo, context, THUMBNAIL_SIZE);
    }

    private Bitmap getLittleThumbnail(String photo, Context context, int size) {
        String key = photo + "_" + size;
        Bitmap bitmap = mMemoryCache.get(key);
        String diskCacheKey = md5(new File(key).getName() + key.hashCode());
        if (bitmap == null) {
            DiskLruImageCache diskLruImageCache = getDiskLruImageCache(context);
            if (diskLruImageCache.containsKey(diskCacheKey)) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                bitmap = diskLruImageCache.getBitmap(diskCacheKey);
            } else {
                final BitmapFactory.Options options = new BitmapFactory.Options();
                bitmap = BitmapUtils.decodeSampledBitmapFromResource(photo, size, size, options, true);
                if (bitmap == null) {
                    return null;
                }
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, size, size);
                if (bitmap == null) {
                    return null;
                }
                diskLruImageCache.put(diskCacheKey, bitmap);
            }
            mMemoryCache.put(key, bitmap);
        }
        return bitmap;
    }

    private String md5(String key) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(key.getBytes("UTF-8"));
            byte[] digest = messageDigest.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            return bigInt.toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError();
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError();
        }
    }
}
