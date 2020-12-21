package com.example.galleryscroll;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class BitmapWorkerThumbnailPhotoTask extends AsyncTask<String, Void, BitmapDrawable> {
    private final WeakReference<ViewHolder> imageViewReference;
    private final int position;
    private final Context context;
    private String data = "";

    public BitmapWorkerThumbnailPhotoTask(ViewHolder holder, Activity activity, int position) {
        this.imageViewReference = new WeakReference(holder);
        this.context = activity;
        this.position = position;
    }

    @Override
    protected BitmapDrawable doInBackground(String... params) {
        data = params[0];
        Bitmap myBitmap = DataHolder.getInstance().getLittleThumbnail(data, context);
        BitmapDrawable drawable = new BitmapDrawable(context.getResources(), myBitmap);
        return drawable;
    }

    @Override
    protected void onPostExecute(BitmapDrawable bitmap) {
        // View could be recycled because of out of the View, in this situation we don't try to put image in View
        if (imageViewReference != null && bitmap != null) {
            final ViewHolder imageView = imageViewReference.get();
            // We check that the image belongs to the same View, because it could be reused
            if (imageView != null && imageView.position == position && imageView.image != null) {
                imageView.image.setImageDrawable(bitmap);
            }
        }
    }

    public static void setImageAsync(String photo, Activity context, ViewHolder holder, int position) {
        BitmapDrawable value = null;
        Bitmap bitmap = DataHolder.getInstance().mMemoryCache.get(photo + "_" + DataHolder.THUMBNAIL_SIZE);
        if (bitmap != null) {
            value = new BitmapDrawable(context.getResources(), bitmap);
        }
        if (value != null) {
            holder.image.setImageDrawable(value);
        } else if (cancelPotentialWork(photo, holder)) {
            holder.image.setImageBitmap(null);
            holder.image.setBackgroundColor(Color.GRAY);
            final BitmapWorkerThumbnailPhotoTask task = new BitmapWorkerThumbnailPhotoTask(holder, context, position);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(context.getResources(), null, task);
            holder.image.setImageDrawable(asyncDrawable);
            task.execute(photo);
        }
    }

    // If View is reused then it could have task already assigned
    // we need to check the task, if it works with different picture then we try to cancel it and create new one
    public static boolean cancelPotentialWork(String photo, ViewHolder holder) {
        ImageView imageView = holder.image;
        final BitmapWorkerThumbnailPhotoTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
        if (bitmapWorkerTask != null) {
            final String bitmapData = bitmapWorkerTask.data;
            if (bitmapData == null || !bitmapData.equals(photo)) {
                bitmapWorkerTask.cancel(true);
            } else {
                // we proceed with the same asynctask
                return false;
            }
        }
        return true;
    }

    private static BitmapWorkerThumbnailPhotoTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    private static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerThumbnailPhotoTask> bitmapWorkerTaskReference;
        @SuppressLint("NewApi")
        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerThumbnailPhotoTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference(bitmapWorkerTask);
        }
        public BitmapWorkerThumbnailPhotoTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }
}