package com.ks.dblab.kshelper.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.ks.dblab.kshelper.R;
import com.ks.dblab.kshelper.util.Preferences;

/**
 * Created by jo on 2016-03-19.
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener{

    public Preferences preference = null;

    protected LinearLayout llRoot = null;
    protected LinearLayout llContainer = null;
    protected Toolbar toolbar = null;
    protected ImageButton btnBack= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.i(this.getClassName(), "onCreate");
        super.onCreate(savedInstanceState);

        preference = new Preferences(this);

        this.prepareActivity(savedInstanceState);
        this.createActivity(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        //Log.i(this.getClassName(), "onDestroy");

        this.destroyActivity();

        super.onDestroy();
    }

    protected  void prepareActivity(Bundle savedInstanceState){
        setContentView(R.layout.activity_base);

        this.llRoot = (LinearLayout) findViewById(R.id.ll_root);

        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.btnBack = (ImageButton) findViewById(R.id.btn_back);
        this.btnBack.setOnClickListener(this);

        this.llContainer = (LinearLayout) findViewById(R.id.ll_container);
    }

    //클릭 리스너
    @Override
    public void onClick(View v) {
        viewClick(v);
    }

    //레이아웃 세팅
    protected View setContainerView(int resource) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        View view = inflater.inflate(resource, null);
        view.setLayoutParams(params);

        this.llContainer.removeAllViews();
        this.llContainer.addView(view);

        return view;
    }

    //인텐트 시킬 때
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        super.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    //뒤로가기 버튼 삭제
    protected void removeBackBtn(){
        this.toolbar.removeView(this.btnBack);
    }

    //패키지+클래스 이름 가져오기
    public String getClassName() {
        return this.getClass().getName();
    }

    //클래스 이름 가져오기
    public String getClassSimpleName() {
        return this.getClass().getSimpleName();
    }

    //생성자
    protected abstract void createActivity(Bundle savedInstanceState);
    //소멸자
    protected abstract void destroyActivity();
    protected abstract void viewClick(View view);
}
