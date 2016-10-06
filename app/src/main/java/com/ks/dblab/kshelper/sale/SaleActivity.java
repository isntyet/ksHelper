package com.ks.dblab.kshelper.sale;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ks.dblab.kshelper.R;
import com.ks.dblab.kshelper.activity.BaseActivity;
import com.ks.dblab.kshelper.request.SaleListRequest;

import java.util.List;

/**
 * Created by Administrator on 2016-07-06.
 */
public class SaleActivity extends BaseActivity {

    private List<SaleItem> saleList;
    private RecyclerView rvSaleList;
    private FloatingActionButton btnWrite;

    @Override
    protected void createActivity(Bundle savedInstanceState) {
        View view = this.setContainerView(R.layout.activity_sale);
        getSupportActionBar().setTitle("중고 장터");

        rvSaleList = (RecyclerView) view.findViewById(R.id.rv_sale_list);

        btnWrite = (FloatingActionButton) view.findViewById(R.id.btn_write);
        btnWrite.setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvSaleList.setHasFixedSize(true);
        rvSaleList.setLayoutManager(layoutManager);

        saleList = listRequest();

        //Log.d("size : " , saleList.size() + "");

        rvSaleList.setAdapter(new SaleAdapter(getApplicationContext(),saleList, R.layout.activity_sale));
    }

    private List<SaleItem> listRequest(){
        SaleListRequest request = new SaleListRequest(this);
        request.setParams();
        return request.startRequest();
    }

    @Override
    protected void onPause() {
        saleList = null;
        rvSaleList.removeAllViewsInLayout();
        super.onPause();
    }

    @Override
    protected void onResume() {
        saleList = listRequest();
        rvSaleList.setAdapter(new SaleAdapter(getApplicationContext(),saleList, R.layout.activity_sale));
        super.onResume();
    }

    @Override
    protected void destroyActivity() {

    }

    @Override
    protected void viewClick(View view) {
        if (view.getId() == R.id.btn_back) {
            this.finish();
        } else if(view.getId() == btnWrite.getId()){
            startActivity(new Intent(SaleActivity.this, SaleWriteActivity.class));
        }
    }
}
