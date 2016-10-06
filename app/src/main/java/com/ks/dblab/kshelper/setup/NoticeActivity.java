package com.ks.dblab.kshelper.setup;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ks.dblab.kshelper.R;
import com.ks.dblab.kshelper.activity.BaseActivity;
import com.ks.dblab.kshelper.request.NoticeRequest;

import java.util.List;

/**
 * Created by Administrator on 2016-07-20.
 */
public class NoticeActivity extends BaseActivity {

    private List<NoticeItem> noticeList;
    private TextView tvTitle;
    private TextView tvContent;

    @Override
    protected void createActivity(Bundle savedInstanceState) {
        View view = this.setContainerView(R.layout.activity_notice);
        getSupportActionBar().setTitle("공지사항");

        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvContent = (TextView) view.findViewById(R.id.tv_content);

        noticeList = listRequest();

        tvTitle.setText(noticeList.get(0).title);
        tvContent.setText(noticeList.get(0).content);

    }

    private List<NoticeItem> listRequest(){
        NoticeRequest request = new NoticeRequest(this);
        request.setParams();
        return request.startRequest();
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
