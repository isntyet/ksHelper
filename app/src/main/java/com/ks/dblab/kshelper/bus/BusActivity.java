package com.ks.dblab.kshelper.bus;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.DigitalClock;
import android.widget.TextView;

import com.ks.dblab.kshelper.R;
import com.ks.dblab.kshelper.activity.BaseActivity;
import com.ks.dblab.kshelper.util.SysUtill;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;

import info.hoang8f.widget.FButton;

/**
 * Created by jo on 2016-03-19.
 */
public class BusActivity extends BaseActivity {

    private TextView upStop;
    private TextView downStop;
    private TextView next;
    private HashMap<String, String> upBusList = null;
    private HashMap<String, String> downBusList = null;
    private DigitalClock currentTime;

    private String oldUpBusTime = "00:00";
    private String oldDownBusTime = "00:00";

    private FButton btnDownAll;
    private FButton btnUpAll;

    private Calendar calendar;

    @Override
    protected void createActivity(Bundle savedInstanceState) {
        View view = this.setContainerView(R.layout.activity_bus);
        getSupportActionBar().setTitle("버스 시간");

        upStop = (TextView) view.findViewById(R.id.up_stop);
        downStop = (TextView) view.findViewById(R.id.down_stop);
        next = (TextView) view.findViewById(R.id.tv_next);

        calendar = new GregorianCalendar(Locale.KOREA);
        int nMonth = calendar.get(Calendar.MONTH) + 1;

        //Log.d("month", ""+nMonth);

        if((nMonth == 7) || (nMonth == 8) || (nMonth == 1) || (nMonth == 2)){
            //방학시간표
            upBusList = setVacationUpBusTime();
            downBusList = setVacationDownBusTime();
            next.setText("다음 버스(방학기준)");
        } else {
            //개학 후 시간표
            upBusList = setUpBusTime();
            downBusList = setDownBusTime();
            next.setText("다음 버스");
        }


        currentTime = (DigitalClock) view.findViewById(R.id.current_time);
        currentTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!s.toString().equals(oldUpBusTime)){
                    upStop.setText(upBusTime(getTime()));
                }
                if(!s.toString().equals(oldDownBusTime)){
                    downStop.setText(downBusTime(getTime()));
                }

                //시스템 시간 포멧이 설정마다 달라서 아래와 같이 사용하면 안됨
                /*if(!s.toString().equals(oldUpBusTime)){
                    upStop.setText(upBusTime(s.toString()));
                }
                if(!s.toString().equals(oldDownBusTime)){
                    downStop.setText(downBusTime(s.toString()));
                }*/

            }
        });

        this.btnDownAll = (FButton) view.findViewById(R.id.btn_down_all);
        this.btnDownAll.setOnClickListener(this);

        this.btnUpAll = (FButton) view.findViewById(R.id.btn_up_all);
        this.btnUpAll.setOnClickListener(this);

    }

    private String getTime(){
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String formattedDate = df.format(new Date());

        return formattedDate;
    }

    private String downBusTime(String current){
        String result = "버스 없음";

        String day = SysUtill.getDayKor();
        if(day.equals("토") || day.equals("일")){
            result = "주말 휴무";
        } else {
            for( String key : downBusList.keySet() ){
                if(SysUtill.strToInt(current.replaceAll(":", "")) <= SysUtill.strToInt(downBusList.get(key).replaceAll(":",""))){
                    result = downBusList.get(key);
                    break;
                }
            }
        }

        oldDownBusTime = current;

        return result;
    }

    private String upBusTime(String current){
        String result = "버스 없음";

        String day = SysUtill.getDayKor();
        if(day.equals("토") || day.equals("일")){
            result = "주말 휴무";
        } else {
            for( String key : upBusList.keySet() ){
                if(SysUtill.strToInt(current.replaceAll(":", "")) <= SysUtill.strToInt(upBusList.get(key).replaceAll(":",""))){
                    result = upBusList.get(key);
                    break;
                }
            }
        }

        oldUpBusTime = current;

        return result;
    }

    @Override
    protected void destroyActivity() {

    }

    @Override
    protected void viewClick(View view) {
        if (view.getId() == R.id.btn_back) {
            this.finish();
        } else if(view.getId() == btnDownAll.getId()) {
            loadDialog(downBusList, downStop.getText().toString());
        } else if(view.getId() == btnUpAll.getId()) {
            loadDialog(upBusList, upStop.getText().toString());
        }
    }

    private void loadDialog(HashMap<String, String> list, String currentTime){

        //Toast.makeText(this, SysUtill.getDayKor(), Toast.LENGTH_SHORT).show();
        DialogAdapter adapter = new DialogAdapter(this, list);

        DialogPlus dialog = DialogPlus.newDialog(this)
                .setAdapter(adapter)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                    }
                })
                .setGravity(Gravity.CENTER)
                .setMargin(150, 400, 150, 20)
                .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                .create();
        dialog.show();
    }

    //자연관 시간표
    public HashMap<String, String> setDownBusTime(){

        HashMap<String, String> busData = new LinkedHashMap<String, String>();

        busData.put("1", "08:30");
        busData.put("2", "08:40");
        busData.put("3", "08:50");
        busData.put("4", "09:00");
        busData.put("5", "09:10");
        busData.put("6", "09:20");
        busData.put("7", "09:30");
        busData.put("8", "09:40");
        busData.put("9", "09:50");
        busData.put("10", "10:00");
        busData.put("11", "10:10");
        busData.put("12", "10:20");
        busData.put("13", "10:30");
        busData.put("14", "10:40");
        busData.put("15", "10:50");
        busData.put("16", "11:00");
        busData.put("17", "11:10");
        busData.put("18", "11:20");
        busData.put("19", "11:30");
        busData.put("20", "11:40");
        busData.put("21", "11:50");

        busData.put("22", "12:40");
        busData.put("23", "12:50");
        busData.put("24", "13:00");
        busData.put("25", "13:10");
        busData.put("26", "13:20");
        busData.put("27", "13:30");
        busData.put("28", "13:40");
        busData.put("29", "13:50");
        busData.put("30", "14:00");
        busData.put("31", "14:30");
        busData.put("32", "14:40");
        busData.put("33", "14:50");
        busData.put("34", "15:00");
        busData.put("35", "15:40");
        busData.put("36", "15:50");
        busData.put("37", "16:00");
        busData.put("38", "16:30");
        busData.put("39", "16:40");
        busData.put("40", "16:50");
        busData.put("41", "17:00");
        busData.put("42", "17:30");
        busData.put("43", "17:45");
        busData.put("44", "18:00");

        return busData;
    }

    //공학관 시간표
    public HashMap<String, String> setUpBusTime(){

        HashMap<String, String> busData = new LinkedHashMap<String, String>();

        busData.put("1", "08:33");
        busData.put("2", "08:43");
        busData.put("3", "08:53");
        busData.put("4", "09:03");
        busData.put("5", "09:13");
        busData.put("6", "09:23");
        busData.put("7", "09:33");
        busData.put("8", "09:43");
        busData.put("9", "09:53");
        busData.put("10", "10:03");
        busData.put("11", "10:13");
        busData.put("12", "10:23");
        busData.put("13", "10:33");
        busData.put("14", "10:43");
        busData.put("15", "10:53");
        busData.put("16", "11:03");
        busData.put("17", "11:13");
        busData.put("18", "11:23");
        busData.put("19", "11:33");
        busData.put("20", "11:43");
        busData.put("21", "11:53");

        busData.put("22", "12:43");
        busData.put("23", "12:53");
        busData.put("24", "13:03");
        busData.put("25", "13:13");
        busData.put("26", "13:23");
        busData.put("27", "13:33");
        busData.put("28", "13:43");
        busData.put("29", "13:53");
        busData.put("30", "14:03");
        busData.put("31", "14:33");
        busData.put("32", "14:43");
        busData.put("33", "14:53");
        busData.put("34", "15:03");
        busData.put("35", "15:43");
        busData.put("36", "15:53");
        busData.put("37", "16:03");
        busData.put("38", "16:33");
        busData.put("39", "16:43");
        busData.put("40", "16:53");
        busData.put("41", "17:03");
        busData.put("42", "17:33");
        busData.put("43", "17:48");
        busData.put("44", "18:03");

        return busData;
    }

    //방학 시간표 (자연관)
    public HashMap<String, String> setVacationDownBusTime(){

        HashMap<String, String> busData = new LinkedHashMap<String, String>();

        busData.put("1", "08:50");
        busData.put("2", "09:00");
        busData.put("3", "09:20");
        busData.put("4", "09:40");
        busData.put("5", "10:00");
        busData.put("6", "10:20");
        busData.put("7", "10:40");
        busData.put("8", "11:00");
        busData.put("9", "11:20");
        busData.put("10", "11:40");
        busData.put("11", "12:00");

        busData.put("12", "12:40");
        busData.put("13", "12:50");
        busData.put("14", "13:00");
        busData.put("15", "13:20");
        busData.put("16", "13:40");
        busData.put("17", "14:00");
        busData.put("18", "14:20");
        busData.put("19", "14:40");
        busData.put("20", "15:00");
        busData.put("21", "15:20");
        busData.put("22", "15:40");
        busData.put("23", "16:00");
        busData.put("24", "16:20");
        busData.put("25", "16:40");
        busData.put("26", "17:00");

        return busData;
    }

    //방학 시간표 (공학관)
    public HashMap<String, String> setVacationUpBusTime(){

        HashMap<String, String> busData = new LinkedHashMap<String, String>();

        busData.put("1", "08:55");
        busData.put("2", "09:05");
        busData.put("3", "09:25");
        busData.put("4", "09:45");
        busData.put("5", "10:05");
        busData.put("6", "10:25");
        busData.put("7", "10:45");
        busData.put("8", "11:05");
        busData.put("9", "11:25");
        busData.put("10", "11:45");
        busData.put("11", "12:05");

        busData.put("12", "12:45");
        busData.put("13", "12:55");
        busData.put("14", "13:05");
        busData.put("15", "13:25");
        busData.put("16", "13:45");
        busData.put("17", "14:05");
        busData.put("18", "14:25");
        busData.put("19", "14:45");
        busData.put("20", "15:05");
        busData.put("21", "15:25");
        busData.put("22", "15:45");
        busData.put("23", "16:05");
        busData.put("24", "16:25");
        busData.put("25", "16:45");
        busData.put("26", "17:05");

        return busData;
    }

}
