package com.ks.dblab.kshelper.sale;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ks.dblab.kshelper.R;

import java.util.List;

/**
 * Created by Administrator on 2016-07-06.
 */
public class SaleAdapter extends RecyclerView.Adapter<SaleAdapter.ViewHolder> {
    Context context;
    List<SaleItem> items;
    int item_layout;

    public SaleAdapter(Context context, List<SaleItem> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sale, parent, false);
        return new ViewHolder(v);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final SaleItem item = items.get(position);
        holder.title.setText(item.title);
        holder.content.setText(item.content);
        holder.cnt.setText("(+"+ item.comment_cnt + ")");
        holder.user.setText(item.user);
        holder.reg_date.setText(item.reg_date);
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, item.title, Toast.LENGTH_SHORT).show();

                Intent it = new Intent(context, SaleDetailActivity.class);
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                it.putExtra("no", item.no);
                it.putExtra("title", item.title);
                it.putExtra("content", item.content);
                it.putExtra("user", item.user);
                it.putExtra("pwd", item.pwd);
                it.putExtra("image", item.img);
                it.putExtra("reg_date", item.reg_date);

                context.startActivity(it);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView content;
        TextView cnt;
        TextView user;
        TextView reg_date;
        CardView cardview;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            content = (TextView) itemView.findViewById(R.id.tv_content);
            cnt = (TextView) itemView.findViewById(R.id.tv_cnt);
            user = (TextView) itemView.findViewById(R.id.tv_user);
            reg_date = (TextView) itemView.findViewById(R.id.tv_regdate);
            cardview = (CardView) itemView.findViewById(R.id.cardview);
        }
    }
}
