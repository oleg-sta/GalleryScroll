package com.example.galleryscroll;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public class GridAdapter extends BaseAdapter {

    private final List<String> photos;
    private final Activity context;

    public GridAdapter(Activity applicationContext, List<String> photos) {
        context = applicationContext;
        this.photos = photos;
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater inflater = context.getLayoutInflater();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.grid_view, null, true);
            holder = new ViewHolder();
            holder.image = convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final String photo = photos.get(position);
        holder.position = position;
        BitmapWorkerThumbnailPhotoTask.setImageAsync(photo, context, holder, position);
        return convertView;
    }

}
