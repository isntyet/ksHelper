package com.ks.dblab.kshelper.etc;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.ks.dblab.kshelper.R;
import com.ks.dblab.kshelper.login.LoginActivity;

/**
 * Created by Administrator on 2016-07-21.
 */
public class DialogViewService extends Service implements View.OnClickListener {
    private WindowManager.LayoutParams mParams; // layout params 객체. 뷰의 위치 및 크기
    private WindowManager mWindowManager;
    private TextView txtMsg;
    private Button btnNo;
    private Button btnYes;
    private View popupView;
    private String msg = "test";

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // create보다 먼저 실행
        msg = intent.getExtras().getString("msg");

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.dialog_popup, null);

        txtMsg = (TextView) popupView.findViewById(R.id.txt_msg);
        txtMsg.setText(msg);
        btnNo = (Button) popupView.findViewById(R.id.btn_no);
        btnNo.setOnClickListener(this);
        btnYes = (Button) popupView.findViewById(R.id.btn_yes);
        btnYes.setOnClickListener(this);

        // 최상위 윈도우에 넣기 위한 설정
        mParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_PHONE,// 항상
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON, PixelFormat.TRANSLUCENT); // 투명=TRANSLUCENT
        mParams.gravity = Gravity.CENTER | Gravity.CENTER_VERTICAL; //

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE); // 윈도우 매니져
        mWindowManager.addView(popupView, mParams); // 윈도우에 뷰 넣기. permission 필요
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Log.d("push_message", msg + " ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWindowManager.removeView(popupView);
        // service 종료시 꼭 뷰를 지워주어야 함
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_yes) {
            // mWindowManager.removeView(popupView);
            Intent it = new Intent(getApplicationContext(), LoginActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 1, it, PendingIntent.FLAG_UPDATE_CURRENT);
            try {
                pi.send();
            } catch (Exception e) {
                //Log.d("error", e.toString());
            }
            stopSelf();
        } else if (v.getId() == R.id.btn_no) {
            stopSelf();
        }

    }

}
