package com.ks.dblab.kshelper.mcc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dd.CircularProgressButton;
import com.ks.dblab.kshelper.R;
import com.ks.dblab.kshelper.activity.BaseActivity;

/**
 * Created by Administrator on 2016-06-21.
 */
public class MccMainActivity extends BaseActivity {

    private CircularProgressButton btnNews;
    private CircularProgressButton btnTimeTable;

    @Override
    protected void createActivity(Bundle savedInstanceState) {
        View view = this.setContainerView(R.layout.activity_mcc_main);
        getSupportActionBar().setTitle("MCC (경성대 방송국)");

        btnNews = (CircularProgressButton) view.findViewById(R.id.btn_news);
        btnNews.setOnClickListener(this);

        btnTimeTable = (CircularProgressButton) view.findViewById(R.id.btn_time_table);
        btnTimeTable.setOnClickListener(this);
    }

    @Override
    protected void destroyActivity() {

    }

    @Override
    protected void viewClick(View view) {
        if (view.getId() == R.id.btn_back) {
            this.finish();
        } else if(view.getId() == btnNews.getId()){
            startActivity(new Intent(MccMainActivity.this, MccNewsActivity.class));
        } else if(view.getId() == btnTimeTable.getId()){
            startActivity(new Intent(MccMainActivity.this, MccTimeTableActivity.class));
        }
    }
}
