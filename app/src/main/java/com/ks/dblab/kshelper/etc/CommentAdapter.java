package com.ks.dblab.kshelper.etc;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.ks.dblab.kshelper.R;
import com.ks.dblab.kshelper.app.MyApplication;

import java.util.List;

/**
 * Created by jojo on 2016-07-07.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    Context context;
    List<CommentItem> items;
    int item_layout;
    public ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();

    public CommentAdapter(Context context, List<CommentItem> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(v);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final CommentItem item = items.get(position);
        holder.content.setText(item.content);
        holder.user.setText(item.user);
        item.reg_date = item.reg_date.substring(0, 10);
        holder.reg_date.setText(item.reg_date);

        holder.userImage.setDefaultImageResId(R.drawable.kakao_default_profile_image);
        holder.userImage.setErrorImageResId(R.drawable.kakao_default_profile_image);
        if((!"".equals(item.user_image)) && (!"default".equals(item.user_image)) && (item.user_image != null)){
            holder.userImage.setImageUrl(item.user_image, imageLoader );
        }

        holder.llLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, item.content, Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView user;
        TextView content;
        TextView reg_date;
        NetworkImageView userImage;
        LinearLayout llLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            user = (TextView) itemView.findViewById(R.id.tv_user);
            content = (TextView) itemView.findViewById(R.id.tv_content);
            reg_date = (TextView) itemView.findViewById(R.id.tv_date);
            userImage = (NetworkImageView) itemView.findViewById(R.id.niv_user_image);
            llLayout = (LinearLayout) itemView.findViewById(R.id.ll_layout);
        }
    }
}
