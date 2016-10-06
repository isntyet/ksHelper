package com.ks.dblab.kshelper.login;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import com.ks.dblab.kshelper.R;
import com.ks.dblab.kshelper.util.Preferences;

import java.security.MessageDigest;


/**
 * Created by Administrator on 2016-03-07.
 */


public class LoginActivity extends Activity {

    private SessionCallback callback;
    private Preferences preference;
    //private TextView tvTest;

    /**
     * 로그인 버튼을 클릭 했을시 access token을 요청하도록 설정한다.
     *
     * @param savedInstanceState 기존 session 정보가 저장된 객체
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preference = new Preferences(this);

        //tvTest = (TextView) findViewById(R.id.tv_test);

        getAppKeyHash();

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().checkAndImplicitOpen();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            redirectSignupActivity();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Logger.e(exception);
            }
        }
    }

    protected void redirectSignupActivity() {
        final Intent intent = new Intent(this, SignupActivity.class);
        requestMe();
        startActivity(intent);
        finish();
    }

    private void requestMe() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                preference.setPushIdInsert(false);
                String message = "failed to get user info. msg=" + errorResult;

            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                preference.setPushIdInsert(false);
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                //Log.d("myLog", "userProfileID" + userProfile.getId());
                //Log.d("myLog", "userProfileNAME" + userProfile.getNickname());
                //Log.d("myLog", "userProfileIMAGE" + userProfile.getThumbnailImagePath());

                preference.setKakaoId(Long.toString(userProfile.getId()));
                if(("".equals(userProfile.getNickname())) || (userProfile.getNickname() == null)){
                    preference.setKakaoNickName("anonymous");
                } else {
                    preference.setKakaoNickName(userProfile.getNickname());
                }

                if(("".equals(userProfile.getThumbnailImagePath())) || (userProfile.getThumbnailImagePath() == null)){
                    preference.setKakaoImage("");
                } else {
                    preference.setKakaoImage(userProfile.getThumbnailImagePath());
                }
            }

            @Override
            public void onNotSignedUp() {
                preference.setPushIdInsert(false);
            }
        });
    }

    //키해시 알아보기
    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //Log.d("Hash key", something);
                //tvTest.setText(something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }

}