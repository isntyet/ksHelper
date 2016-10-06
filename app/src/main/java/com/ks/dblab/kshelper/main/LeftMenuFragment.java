package com.ks.dblab.kshelper.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ks.dblab.kshelper.R;
import com.ks.dblab.kshelper.schedule.ScheduleActivity;
import com.ks.dblab.kshelper.util.Preferences;
import com.mxn.soul.flowingdrawer_core.MenuFragment;

public class LeftMenuFragment extends MenuFragment implements View.OnClickListener{
    private Button[][] btnTime = null;
    private int colorAry[] = {R.color.color_btn_1, R.color.color_btn_2, R.color.color_btn_3, R.color.color_btn_4, R.color.color_btn_5, R.color.color_btn_6, R.color.color_btn_7, R.color.color_btn_8};
    public Preferences preference = null;
    private Context context;
    private Toast toast = null;
    private Button btnUpdateTime = null;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        getActivity().getApplicationContext();

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        preference = new Preferences(context);

        btnUpdateTime = (Button) view.findViewById(R.id.btn_update);
        btnUpdateTime.setOnClickListener(this);

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

        return  setupReveal(view) ;
    }

    private void onLoadSchedule(){
        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 11; j++){
                this.btnTime[i][j].setText(preference.getSchedule(i, j));
                this.btnTime[i][j].setBackgroundResource((colorAry[preference.getScheduleColor(i, j)]));
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        onLoadSchedule();
    }

    private View.OnClickListener btnListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            for(int i = 0; i < 5; i++){
                for(int j = 0; j < 11; j++){
                    if(v.getId() == btnTime[i][j].getId()) {
                        if((!"".equals(preference.getSchedule(i, j))) && (preference.getSchedule(i, j) != null)){
                            if (toast == null) {
                                toast = Toast.makeText(context, preference.getSchedule(i, j), Toast.LENGTH_SHORT);
                            } else {
                                toast.cancel();
                                toast = Toast.makeText(context, preference.getSchedule(i, j), Toast.LENGTH_SHORT);
                            }
                            toast.show();
                        }
                    }
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        if(v.getId() == btnUpdateTime.getId()){
            startActivity(new Intent(context, ScheduleActivity.class));
        }
    }
}
