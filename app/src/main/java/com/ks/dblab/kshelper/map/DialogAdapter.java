package com.ks.dblab.kshelper.map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ks.dblab.kshelper.R;
import com.ks.dblab.kshelper.util.SysUtill;

import java.util.HashMap;
import java.util.List;

/**
 * Created by jojo on 2016-05-30.
 */
public class DialogAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<MapData> list;

    public DialogAdapter(Context context, List<MapData> list) {
        layoutInflater = LayoutInflater.from(context);
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = convertView;

        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_dialog_list, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.text_view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Context context = parent.getContext();

        //String str = SysUtill.intToStr(position+1);

        //viewHolder.textView.setText(list.get(str));
        viewHolder.textView.setText(list.get(position).getName());
        /*if(list.get(str).equals(current)){
            viewHolder.textView.setBackgroundResource(R.color.colorAccent);
        }*/

        return view;
    }

    static class ViewHolder {
        TextView textView;
    }
}
