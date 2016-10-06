package com.ks.dblab.kshelper.mcc;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.ks.dblab.kshelper.R;
import com.ks.dblab.kshelper.activity.WebviewActivity;
import com.ks.dblab.kshelper.app.MyApplication;

import java.util.ArrayList;

/**
 * Created by jojo on 2016-06-24.
 */
public class NewsAdapter extends BaseAdapter {

    private Context mContext = null;
    private int layout = 0;
    private ArrayList<News> data = null;
    private LayoutInflater inflater = null;
    public ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();

    public NewsAdapter(Context context, int linear, ArrayList<News> data){
        this.mContext = context;
        this.layout = linear;
        this.data = data;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = inflater.inflate(this.layout, parent, false);
        }
        if (imageLoader == null) {
            imageLoader = MyApplication.getInstance().getImageLoader();
        }

        TextView subjcet = (TextView) convertView.findViewById(R.id.tv_subject);
        TextView context = (TextView) convertView.findViewById(R.id.tv_context);
        TextView date = (TextView) convertView.findViewById(R.id.tv_date);
        NetworkImageView photo = (NetworkImageView) convertView.findViewById(R.id.iv_photo);
        Button origin = (Button) convertView.findViewById(R.id.btn_origin);

        subjcet.setText(data.get(position).subject);
        context.setText(data.get(position).context);
        date.setText(data.get(position).date);
        photo.setImageUrl(data.get(position).image_url, imageLoader );

        final int mPosition = position;

        origin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(mContext, WebviewActivity.class);
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                it.putExtra("url", "https://cms1.ks.ac.kr/mcc/Board.do"+data.get(mPosition).href);
                it.putExtra("name", "뉴스 원문 보기");
                mContext.startActivity(it);
            }
        });

        return convertView;

    }
}
