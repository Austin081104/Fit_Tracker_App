package com.example.fit_tracker;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoryAdapter extends BaseAdapter {

    private Context context;
    private final String[] titles;
    private final int[] images;

    public CategoryAdapter(Context context, String[] titles, int[] images) {
        this.context = context;
        this.titles = titles;
        this.images = images;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Object getItem(int i) {
        return titles[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.category_item, viewGroup, false);
        }

        TextView title = convertView.findViewById(R.id.categoryTitle);
        ImageView image = convertView.findViewById(R.id.categoryImage);

        title.setText(titles[i]);
        image.setImageResource(images[i]);

        return convertView;
    }
}