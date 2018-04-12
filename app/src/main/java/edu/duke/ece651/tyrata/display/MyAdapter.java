package edu.duke.ece651.tyrata.display;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

/**
 * Created by yangm on 2018/4/9.
 */

public class MyAdapter extends SimpleAdapter {
    public MyAdapter(Context context, List<? extends Map<String, Object>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (position % 2 == 0) {
            convertView.setBackgroundColor(Color.parseColor("#b3FFFFFF"));
        } else {
            convertView.setBackgroundColor(Color.parseColor("#b3FAFAFA"));
        }
        return convertView;
    }
}
