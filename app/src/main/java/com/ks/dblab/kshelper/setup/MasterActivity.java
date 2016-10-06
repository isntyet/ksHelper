package com.ks.dblab.kshelper.setup;

import android.os.Bundle;
import android.view.View;

import com.ks.dblab.kshelper.R;
import com.ks.dblab.kshelper.activity.BaseActivity;

/**
 * Created by jojo on 2016-07-22.
 */
public class MasterActivity extends BaseActivity {
    @Override
    protected void createActivity(Bundle savedInstanceState) {
        View view = this.setContainerView(R.layout.activity_master);
        getSupportActionBar().setTitle("관리자 비밀의 방");
    }

    @Override
    protected void destroyActivity() {

    }

    @Override
    protected void viewClick(View view) {
        if (view.getId() == R.id.btn_back) {
            this.finish();
        }
    }
}
