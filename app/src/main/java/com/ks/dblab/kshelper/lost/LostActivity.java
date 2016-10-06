package com.ks.dblab.kshelper.lost;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ks.dblab.kshelper.R;
import com.ks.dblab.kshelper.activity.BaseActivity;
import com.ks.dblab.kshelper.request.LostListRequest;

import java.util.List;

/**
 * Created by Administrator on 2016-06-30.
 */
public class LostActivity extends BaseActivity {

    private List<LostItem> lostList;
    private RecyclerView rvLostList;
    private FloatingActionButton btnWrite;

    @Override
    protected void createActivity(Bundle savedInstanceState) {
        View view = this.setContainerView(R.layout.activity_lost);
        getSupportActionBar().setTitle("분실물 찾기/찾아주기");

        rvLostList = (RecyclerView) view.findViewById(R.id.rv_lost_list);

        btnWrite = (FloatingActionButton) view.findViewById(R.id.btn_write);
        btnWrite.setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvLostList.setHasFixedSize(true);
        rvLostList.setLayoutManager(layoutManager);

        lostList = listRequest();

        //Log.d("size : " , lostList.size() + "");

        rvLostList.setAdapter(new LostAdapter(getApplicationContext(),lostList, R.layout.activity_lost));

    }

    private List<LostItem> listRequest(){
        LostListRequest request = new LostListRequest(this);
        request.setParams();
        return request.startRequest();
    }

    @Override
    protected void onResume() {
        lostList = listRequest();
        rvLostList.setAdapter(new LostAdapter(getApplicationContext(),lostList, R.layout.activity_lost));
        super.onResume();
    }

    @Override
    protected void onPause() {
        lostList = null;
        rvLostList.removeAllViewsInLayout();
        super.onPause();
    }

    @Override
    protected void destroyActivity() {

    }

    @Override
    protected void viewClick(View view) {
        if (view.getId() == R.id.btn_back) {
            this.finish();
        } else if(view.getId() == btnWrite.getId()){
            startActivity(new Intent(LostActivity.this, LostWriteActivity.class));
        }
    }
}
