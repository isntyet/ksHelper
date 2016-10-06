package com.ks.dblab.kshelper.schedule;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ks.dblab.kshelper.R;
import com.ks.dblab.kshelper.activity.BaseActivity;

import at.markushi.ui.CircleButton;

/**
 * Created by jojo on 2016-06-05.
 */
public class ScheduleActivity extends BaseActivity implements View.OnClickListener{

    private Button[][] btnTime = null;
    private CircleButton[] btnColor = null;
    private int flagColor = 0;
    private int colorAry[] = {R.color.color_btn_1, R.color.color_btn_2, R.color.color_btn_3, R.color.color_btn_4, R.color.color_btn_5, R.color.color_btn_6, R.color.color_btn_7, R.color.color_btn_8};

    private Dialog dialog = null;

    @Override
    protected void createActivity(Bundle savedInstanceState) {
        View view = this.setContainerView(R.layout.activity_schedule);
        getSupportActionBar().setTitle("수강 시간표");

        btnTime = new Button[5][11];
        int[][] btnId = {
                {R.id.btn_mon_1, R.id.btn_mon_2, R.id.btn_mon_3, R.id.btn_mon_4, R.id.btn_mon_5, R.id.btn_mon_6, R.id.btn_mon_7, R.id.btn_mon_8, R.id.btn_mon_9, R.id.btn_mon_10, R.id.btn_mon_11},
                {R.id.btn_tue_1, R.id.btn_tue_2, R.id.btn_tue_3, R.id.btn_tue_4, R.id.btn_tue_5, R.id.btn_tue_6, R.id.btn_tue_7, R.id.btn_tue_8, R.id.btn_tue_9, R.id.btn_tue_10, R.id.btn_tue_11},
                {R.id.btn_wed_1, R.id.btn_wed_2, R.id.btn_wed_3, R.id.btn_wed_4, R.id.btn_wed_5, R.id.btn_wed_6, R.id.btn_wed_7, R.id.btn_wed_8, R.id.btn_wed_9, R.id.btn_wed_10, R.id.btn_wed_11},
                {R.id.btn_thu_1, R.id.btn_thu_2, R.id.btn_thu_3, R.id.btn_thu_4, R.id.btn_thu_5, R.id.btn_thu_6, R.id.btn_thu_7, R.id.btn_thu_8, R.id.btn_thu_9, R.id.btn_thu_10, R.id.btn_thu_11},
                {R.id.btn_fri_1, R.id.btn_fri_2, R.id.btn_fri_3, R.id.btn_fri_4, R.id.btn_fri_5, R.id.btn_fri_6, R.id.btn_fri_7, R.id.btn_fri_8, R.id.btn_fri_9, R.id.btn_fri_10, R.id.btn_fri_11}
        };

        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 11; j++){
                this.btnTime[i][j] = (Button) view.findViewById(btnId[i][j]);
            }
        }

        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 11; j++){
                this.btnTime[i][j].setOnClickListener(btnListener);
            }
        }

        onLoadSchedule();
    }

    private View.OnClickListener btnListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            for(int i = 0; i < 5; i++){
                for(int j = 0; j < 11; j++){
                    if(v.getId() == btnTime[i][j].getId()) {
                        loadDialog(i, j);
                    }
                }
            }
        }
    };

    private View.OnClickListener btnColorListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            for(int k = 0; k < 8; k++){
                btnColor[k].setImageResource(0);
            }

            for(int k = 0; k < 8; k++){
                if(v.getId() == btnColor[k].getId()) {
                    btnColor[k].setImageResource(R.drawable.ic_action_accept);
                    flagColor = k;
                }
            }
        }
    };

    private void loadDialog(final int i, final int j) {

        dialog = new Dialog(ScheduleActivity.this);
        dialog.setContentView(R.layout.item_dialog_input);
        dialog.setTitle("시간표 등록");

        String savedData = preference.getSchedule(i, j);

        btnColor = new CircleButton[8];
        int[] btnColorId = {R.id.color_1, R.id.color_2, R.id.color_3, R.id.color_4, R.id.color_5, R.id.color_6, R.id.color_7, R.id.color_8};

        for(int k = 0; k < 8; k++){
            btnColor[k] = (CircleButton) dialog.findViewById(btnColorId[k]);
        }
        for(int k = 0; k < 8; k++){
            btnColor[k].setOnClickListener(btnColorListener);
        }

        for(int k = 0; k < 8; k++){
            btnColor[k].setImageResource(0);
        }
        btnColor[preference.getScheduleColor(i, j)].setImageResource(R.drawable.ic_action_accept);


        final EditText name = (EditText) dialog.findViewById(R.id.txt_name);
        final EditText classroom = (EditText) dialog.findViewById(R.id.txt_classroom);
        if((!"".equals(savedData)) && (savedData != null)){
            if(savedData.split("\n").length >= 2){
                name.setText(savedData.split("\n")[0]);
                classroom.setText(savedData.split("\n")[1]);
            }else if(savedData.split("\n").length == 1){
                name.setText(savedData.split("\n")[0]);
            } else {
                name.setText("");
                classroom.setText("");
            }
        }
        dialog.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTime[i][j].setText(name.getText() + "\n" + classroom.getText());
                preference.setSchedule(name.getText() + "\n" + classroom.getText(), i, j);
                btnTime[i][j].setBackgroundResource(colorAry[flagColor]);
                preference.setScheduleColor(flagColor, i, j);

                Toast.makeText(ScheduleActivity.this, "저장 완료", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void onLoadSchedule(){
        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 11; j++){
                this.btnTime[i][j].setText(preference.getSchedule(i, j));
                this.btnTime[i][j].setBackgroundResource((colorAry[preference.getScheduleColor(i, j)]));
            }
        }
    }

    private void deleteData(){

        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 11; j++){
                preference.setSchedule("", i, j);
                preference.setScheduleColor(0, i, j);
            }
        }

        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 11; j++){
                this.btnTime[i][j].setText(preference.getSchedule(i, j));
                this.btnTime[i][j].setBackgroundResource((colorAry[preference.getScheduleColor(i, j)]));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "시간표 초기화");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == 1){
            deleteData();
        }

        return super.onOptionsItemSelected(item);
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
