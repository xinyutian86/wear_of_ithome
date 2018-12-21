package xyz.xinyutian.rss_of_ithome_waer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends ArrayAdapter {


    public MyAdapter(Context context, int resource, List<RSSItemBean> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        RSSItemBean linkeMain = (RSSItemBean)getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.card, null);
        TextView time = (TextView)view.findViewById(R.id.con);
        time.setText(linkeMain.getTitle());

        return view;
    }
}