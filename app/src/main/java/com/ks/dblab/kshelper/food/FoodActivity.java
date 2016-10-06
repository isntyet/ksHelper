package com.ks.dblab.kshelper.food;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dd.CircularProgressButton;
import com.ks.dblab.kshelper.R;
import com.ks.dblab.kshelper.activity.BaseActivity;
import com.ks.dblab.kshelper.activity.WebviewActivity;

/**
 * Created by Administrator on 2016-05-31.
 */
public class FoodActivity extends BaseActivity{

    private CircularProgressButton btnScience;
    private CircularProgressButton btnStudent;
    private CircularProgressButton btnNuri;

    @Override
    protected void createActivity(Bundle savedInstanceState) {
        View view = this.setContainerView(R.layout.activity_food);
        getSupportActionBar().setTitle("교내 식당");

        btnScience = (CircularProgressButton) view.findViewById(R.id.btn_science);
        btnScience.setOnClickListener(this);

        btnStudent = (CircularProgressButton) view.findViewById(R.id.btn_student);
        btnStudent.setOnClickListener(this);

        btnNuri = (CircularProgressButton) view.findViewById(R.id.btn_nuri);
        btnNuri.setOnClickListener(this);


    }

    @Override
    protected void destroyActivity() {

    }

    @Override
    protected void viewClick(View view) {
        if (view.getId() == R.id.btn_back) {
            this.finish();
        } else if(view.getId() == btnScience.getId()){
            Intent it = new Intent(FoodActivity.this, WebviewActivity.class);
            //it.putExtra("url", "http://cms1.ks.ac.kr/mweb/FoodMenu/fmList.do?mCode=MN0056&ft=3");
            it.putExtra("url", "http://apps.ks.ac.kr/ksun/servlet/ksun1.Manager?cmd=food3");
            it.putExtra("name", "과학관 식단");
            startActivity(it);
        } else if(view.getId() == btnStudent.getId()){
            Intent it = new Intent(FoodActivity.this, WebviewActivity.class);
            //it.putExtra("url", "http://cms1.ks.ac.kr/mweb/FoodMenu/fmList.do?mCode=MN0055&ft=2");
            it.putExtra("url", "http://apps.ks.ac.kr/ksun/servlet/ksun1.Manager?cmd=food2");
            it.putExtra("name", "학생회관 식단");
            startActivity(it);
        } else if(view.getId() == btnNuri.getId()){
            Intent it = new Intent(FoodActivity.this, WebviewActivity.class);
            //it.putExtra("url", "http://cms1.ks.ac.kr/mweb/FoodMenu/fmList.do?mCode=MN0059&ft=1");
            it.putExtra("url", "http://apps.ks.ac.kr/ksun/servlet/ksun1.Manager?cmd=food");
            it.putExtra("name", "누리생활관 식단");
            startActivity(it);
        }
    }
}
