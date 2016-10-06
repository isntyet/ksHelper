package com.ks.dblab.kshelper.setup;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.util.helper.log.Logger;
import com.ks.dblab.kshelper.R;
import com.ks.dblab.kshelper.activity.BaseActivity;
import com.ks.dblab.kshelper.app.MyApplication;
import com.ks.dblab.kshelper.main.MainActivity;
import com.ks.dblab.kshelper.request.UserDeleteRequest;

/**
 * Created by jojo on 2016-07-09.
 */
public class SetupActivity extends BaseActivity {

    int masterEsterEgg = 1;

    private TextView tvNickname;
    private NetworkImageView ivUserImage;
    private TextView tvMaster;
    private TextView tvPush;

    private LinearLayout llUnlink;
    private LinearLayout llNotice;
    private LinearLayout llPush;

    public ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();

    @Override
    protected void createActivity(Bundle savedInstanceState) {
        View view = this.setContainerView(R.layout.activity_setup);
        getSupportActionBar().setTitle("설정");

        tvNickname = (TextView) view.findViewById(R.id.tv_nickname);
        ivUserImage = (NetworkImageView) view.findViewById(R.id.niv_user_image);

        llUnlink = (LinearLayout) view.findViewById(R.id.ll_unlink);
        llUnlink.setOnClickListener(this);

        llNotice = (LinearLayout) view.findViewById(R.id.ll_notice);
        llNotice.setOnClickListener(this);

        tvMaster = (TextView) view.findViewById(R.id.tv_master);
        tvMaster.setOnClickListener(this);

        llPush = (LinearLayout) view.findViewById(R.id.ll_push);
        llPush.setOnClickListener(this);

        tvPush = (TextView) view.findViewById(R.id.tv_push);

        initViewData();

        setPushUI();

    }

    private void setPushUI() {
        //true = on = 안받는다
        //false = off = 받는다
        if (preference.isPushAlarm() == true) {
            tvPush.setText("ON");
        } else {
            tvPush.setText("OFF");
        }
    }

    private void initViewData() {

        if (imageLoader == null) {
            imageLoader = MyApplication.getInstance().getImageLoader();
        }

        tvNickname.setText(preference.getKakaoNickName());
        ivUserImage.setErrorImageResId(R.drawable.kakao_default_profile_image);
        String image = preference.getKakaoImage();

        if ((!"".equals(image)) && (!"default".equals(image)) && (image != null)) {
            ivUserImage.setImageUrl(image, imageLoader);
        } else {
            ivUserImage.setDefaultImageResId(R.drawable.kakao_default_profile_image);
        }
    }

    private void onClickUnlink() {
        final String appendMessage = getString(R.string.com_kakao_confirm_unlink);
        new AlertDialog.Builder(this)
                .setMessage(appendMessage)
                .setPositiveButton(getString(R.string.com_kakao_ok_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UserManagement.requestUnlink(new UnLinkResponseCallback() {
                                    @Override
                                    public void onFailure(ErrorResult errorResult) {
                                        Logger.e(errorResult.toString());
                                    }

                                    @Override
                                    public void onSessionClosed(ErrorResult errorResult) {
                                        redirectSignupActivity();
                                    }

                                    @Override
                                    public void onNotSignedUp() {
                                        redirectSignupActivity();
                                    }

                                    @Override
                                    public void onSuccess(Long userId) {
                                        MyApplication.getInstance().logout();
                                        preference.setPushIdInsert(false);
                                        redirectSignupActivity();
                                    }
                                });
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(getString(R.string.com_kakao_cancel_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

    }

    private String userDeleteRequest(){
        UserDeleteRequest request = new UserDeleteRequest(this);
        request.setParams(preference.getKakaoNickName(), preference.getKakaoId());
        return request.startRequest();
    }

    protected void redirectSignupActivity() {
        final Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("state", "kill");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.finish();
    }

    @Override
    protected void destroyActivity() {

    }

    @Override
    protected void viewClick(View view) {
        if (view.getId() == R.id.btn_back) {
            this.finish();
        } else if (view.getId() == llUnlink.getId()) {
            preference.setPushIdInsert(false);
            String result = userDeleteRequest();
            //Log.d("unlink_result", result);
            onClickUnlink();
        } else if (view.getId() == llPush.getId()) {
            if (preference.isPushAlarm() == true) {
                preference.setPushAlarm(false);
            } else {
                preference.setPushAlarm(true);
            }
            setPushUI();
        } else if (view.getId() == llNotice.getId()) {
            startActivity(new Intent(SetupActivity.this, NoticeActivity.class));
        } else if (view.getId() == tvMaster.getId()) {
            masterEsterEgg++;
            if (masterEsterEgg == 7) {
                Toast.makeText(this, "!!!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SetupActivity.this, MasterActivity.class));
                masterEsterEgg = 1;
            }
        }
    }
}
