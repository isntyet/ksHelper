package com.ks.dblab.kshelper.calculator;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.ks.dblab.kshelper.R;
import com.ks.dblab.kshelper.activity.BaseActivity;

import at.markushi.ui.CircleButton;

/**
 * Created by jojo on 2016-07-09.
 */
public class CalculatorActivity extends BaseActivity {
    private CircleButton[] btnCredit;
    private CircleButton[] btnGrade;
    private Dialog dialog = null;
    private CircleButton[] btnSelectCredit;
    private CircleButton[] btnSelectGrade;

    private TextView tvResult;

    private PointItem[] point;

    double flagCredit = 0;
    double flagGrade = 0;

    int currentCredit = 0;
    int currentGrade = 0;

    @Override
    protected void createActivity(Bundle savedInstanceState) {
        View view = this.setContainerView(R.layout.activity_calculator);
        getSupportActionBar().setTitle("학점 계산기");

        tvResult = (TextView) view.findViewById(R.id.tv_result);

        point = new PointItem[10];

        btnCredit = new CircleButton[10];
        int[] btnCreditId = {R.id.credit_1, R.id.credit_2, R.id.credit_3, R.id.credit_4, R.id.credit_5, R.id.credit_6, R.id.credit_7, R.id.credit_8, R.id.credit_9, R.id.credit_10};

        btnGrade = new CircleButton[10];
        int[] btnGradeId = {R.id.grade_1, R.id.grade_2, R.id.grade_3, R.id.grade_4, R.id.grade_5, R.id.grade_6, R.id.grade_7, R.id.grade_8, R.id.grade_9, R.id.grade_10};


        for(int i = 0; i < 10; i++){
            this.btnCredit[i] = (CircleButton) view.findViewById(btnCreditId[i]);
            this.btnCredit[i].setOnClickListener(this);
        }

        for(int i = 0; i < 10; i++){
            this.btnGrade[i] = (CircleButton) view.findViewById(btnGradeId[i]);
            this.btnGrade[i].setOnClickListener(this);
        }

        init();

    }

    private void loadCreditDialog(final CircleButton circle) {

        dialog = new Dialog(CalculatorActivity.this);
        dialog.setContentView(R.layout.item_dialog_credit);
        dialog.setTitle("학점 입력");

        btnSelectCredit = new CircleButton[4];
        int[] btnSelectCreditId = {R.id.credit_select_1, R.id.credit_select_2, R.id.credit_select_3, R.id.credit_select_4};

        flagCredit = 0;

        for(int k = 0; k < 4; k++){
            btnSelectCredit[k] = (CircleButton) dialog.findViewById(btnSelectCreditId[k]);
            btnSelectCredit[k].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(v.getId() == R.id.credit_select_1){
                        circle.setImageDrawable(makeCreditButton("1"));
                        flagCredit = 1;
                    } else if(v.getId() == R.id.credit_select_2){
                        circle.setImageDrawable(makeCreditButton("2"));
                        flagCredit = 2;
                    } else if(v.getId() == R.id.credit_select_3){
                        circle.setImageDrawable(makeCreditButton("3"));
                        flagCredit = 3;
                    } else if(v.getId() == R.id.credit_select_4){
                        circle.setImageDrawable(makeCreditButton("4"));
                        flagCredit = 4;
                    }

                    setDataItem("credit");

                    Toast.makeText(CalculatorActivity.this, "학점 등록 완료", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        }

        btnSelectCredit[0].setImageDrawable(makeCreditButton("1"));
        btnSelectCredit[1].setImageDrawable(makeCreditButton("2"));
        btnSelectCredit[2].setImageDrawable(makeCreditButton("3"));
        btnSelectCredit[3].setImageDrawable(makeCreditButton("4"));

        dialog.show();
    }

    private void loadGradeDialog(final CircleButton circle) {

        dialog = new Dialog(CalculatorActivity.this);
        dialog.setContentView(R.layout.item_dialog_grade);
        dialog.setTitle("점수 입력");

        btnSelectGrade = new CircleButton[9];
        int[] btnSelectGradeId = {R.id.grade_select_1, R.id.grade_select_2, R.id.grade_select_3, R.id.grade_select_4, R.id.grade_select_5, R.id.grade_select_6, R.id.grade_select_7, R.id.grade_select_8, R.id.grade_select_9};

        flagGrade = 0;

        for(int k = 0; k < 9; k++){
            btnSelectGrade[k] = (CircleButton) dialog.findViewById(btnSelectGradeId[k]);
            btnSelectGrade[k].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(v.getId() == R.id.grade_select_1){
                        circle.setImageDrawable(makeCreditButton("A+"));
                        flagGrade = 4.5;
                    } else if(v.getId() == R.id.grade_select_2){
                        circle.setImageDrawable(makeCreditButton("A"));
                        flagGrade = 4;
                    } else if(v.getId() == R.id.grade_select_3){
                        circle.setImageDrawable(makeCreditButton("B+"));
                        flagGrade = 3.5;
                    } else if(v.getId() == R.id.grade_select_4){
                        circle.setImageDrawable(makeCreditButton("B"));
                        flagGrade = 3;
                    } else if(v.getId() == R.id.grade_select_5){
                        circle.setImageDrawable(makeCreditButton("C+"));
                        flagGrade = 2.5;
                    } else if(v.getId() == R.id.grade_select_6){
                        circle.setImageDrawable(makeCreditButton("C"));
                        flagGrade = 2;
                    } else if(v.getId() == R.id.grade_select_7){
                        circle.setImageDrawable(makeCreditButton("D+"));
                        flagGrade = 1.5;
                    } else if(v.getId() == R.id.grade_select_8){
                        circle.setImageDrawable(makeCreditButton("D"));
                        flagGrade = 1;
                    } else if(v.getId() == R.id.grade_select_9){
                        circle.setImageDrawable(makeCreditButton("F"));
                        //입력안한것과 구분하기 위해 0.5로 임시로 저장
                        //0.5는 계산할때 제외됨
                        flagGrade = 0.5;
                    }

                    setDataItem("grade");

                    Toast.makeText(CalculatorActivity.this, "점수 등록 완료", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        }

        btnSelectGrade[0].setImageDrawable(makeCreditButton("A+"));
        btnSelectGrade[1].setImageDrawable(makeCreditButton("A"));
        btnSelectGrade[2].setImageDrawable(makeCreditButton("B+"));
        btnSelectGrade[3].setImageDrawable(makeCreditButton("B"));
        btnSelectGrade[4].setImageDrawable(makeCreditButton("C+"));
        btnSelectGrade[5].setImageDrawable(makeCreditButton("C"));
        btnSelectGrade[6].setImageDrawable(makeCreditButton("D+"));
        btnSelectGrade[7].setImageDrawable(makeCreditButton("D"));
        btnSelectGrade[8].setImageDrawable(makeCreditButton("F"));

        dialog.show();
    }

    private void init(){

        for(int i = 0; i < 10; i++){
            point[i] = new PointItem(0, 0, 0);
        }

        for(int i = 0; i < 10; i++){
            this.btnCredit[i].setImageDrawable(makeCreditButton("학점"));
        }

        for(int i = 0; i < 10; i++){
            this.btnGrade[i].setImageDrawable(makeCreditButton("점수"));
        }
    }

    private TextDrawable makeCreditButton(String text){
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .textColor(Color.WHITE)
                .fontSize(40)
                .bold()
                .endConfig()
                .buildRound(text, Color.argb(0, 0, 0, 0));

        return  drawable;
    }

    private void setDataItem(String flag){
        if("credit".equals(flag)){
            point[currentCredit].credit = flagCredit;
        } else if("grade".equals(flag)){
            point[currentGrade].grade = flagGrade;
        }

        for(int i = 0; i < 10; i++){
            if((point[i].credit != 0) && (point[i].grade != 0)){
                point[i].state = 1;
            } else if((point[i].credit == 0) && (point[i].grade == 0)){
                point[i].state = 0;
            } else {
                point[i].state = -1;
            }
        }

        resultUpdate();
    }

    private void resultUpdate() {
        double totalCredit = 0;
        double sumPoint = 0;

        for(int i = 0; i < 10; i++){
            if(point[i].state == 1){
                if(point[i].grade != 0.5){
                    sumPoint = sumPoint + (point[i].credit * point[i].grade);
                }
                totalCredit = totalCredit + point[i].credit;
            } else if(point[i].state == -1){
                sumPoint = -1;
                break;
            }
        }
        if(sumPoint == -1){
            tvResult.setText("작성 중");
        } else if(sumPoint == 0){
            tvResult.setText("입력 중");
        } else {
            tvResult.setText(String.format("%.2f", sumPoint/totalCredit));
        }

    }



    @Override
    protected void viewClick(View view) {
        if (view.getId() == R.id.btn_back) {
            this.finish();
        } else if((view.getId() == btnCredit[0].getId()) || (view.getId() == btnCredit[1].getId()) || (view.getId() == btnCredit[2].getId()) || (view.getId() == btnCredit[3].getId()) || (view.getId() == btnCredit[4].getId()) || (view.getId() == btnCredit[5].getId()) || (view.getId() == btnCredit[6].getId()) || (view.getId() == btnCredit[7].getId()) || (view.getId() == btnCredit[8].getId()) || (view.getId() == btnCredit[9].getId())){
            for(int i=0; i < 10; i++){
                if(view.getId() == btnCredit[i].getId()){
                    currentCredit = i;
                    break;
                }
            }
            loadCreditDialog((CircleButton) view);
        } else if((view.getId() == btnGrade[0].getId()) || (view.getId() == btnGrade[1].getId()) || (view.getId() == btnGrade[2].getId()) || (view.getId() == btnGrade[3].getId()) || (view.getId() == btnGrade[4].getId()) || (view.getId() == btnGrade[5].getId()) || (view.getId() == btnGrade[6].getId()) || (view.getId() == btnGrade[7].getId()) || (view.getId() == btnGrade[8].getId()) || (view.getId() == btnGrade[9].getId())){
            for(int i=0; i < 10; i++){
                if(view.getId() == btnGrade[i].getId()){
                    currentGrade = i;
                    break;
                }
            }
            loadGradeDialog((CircleButton) view);
        }
    }

    @Override
    protected void destroyActivity() {

    }

    public class PointItem {
        public int state;
        public double credit;
        public double grade;

        public PointItem(int state, double credit, double grade){
            this.state = state;
            this.credit = credit;
            this.grade = grade;
        }
    }
}
