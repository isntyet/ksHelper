package com.ks.dblab.kshelper.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.ks.dblab.kshelper.R;
import com.ks.dblab.kshelper.activity.BaseActivity;
import com.ks.dblab.kshelper.activity.WebviewActivity;
import com.ks.dblab.kshelper.app.MyApplication;
import com.ks.dblab.kshelper.bus.BusActivity;
import com.ks.dblab.kshelper.calculator.CalculatorActivity;
import com.ks.dblab.kshelper.call.CallActivity;
import com.ks.dblab.kshelper.chat.LoginActivity;
import com.ks.dblab.kshelper.chat.gcm.GcmIntentService;
import com.ks.dblab.kshelper.chat.model.User;
import com.ks.dblab.kshelper.desk.DeskActivity;
import com.ks.dblab.kshelper.food.FoodActivity;
import com.ks.dblab.kshelper.login.SignupActivity;
import com.ks.dblab.kshelper.lost.LostActivity;
import com.ks.dblab.kshelper.map.MapActivity;
import com.ks.dblab.kshelper.mcc.MccMainActivity;
import com.ks.dblab.kshelper.request.ChatIdRequest;
import com.ks.dblab.kshelper.roulette.RouletteActivity;
import com.ks.dblab.kshelper.sale.SaleActivity;
import com.ks.dblab.kshelper.schedule.ScheduleActivity;
import com.ks.dblab.kshelper.setup.SetupActivity;
import com.ks.dblab.kshelper.util.VolleyRequest;
import com.mingle.entity.MenuEntity;
import com.mingle.sweetpick.DimEffect;
import com.mingle.sweetpick.SweetSheet;
import com.mingle.sweetpick.ViewPagerDelegate;
import com.mxn.soul.flowingdrawer_core.FlowingView;
import com.mxn.soul.flowingdrawer_core.LeftDrawerLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    public final static int REQUEST_CODE = 3333;

    private String TAG = MainActivity.class.getSimpleName();
    private Context context;

    private LeftDrawerLayout leftDrawerLayout;
    private WebView wvMain;
    private SweetSheet mSweetSheet;
    private RelativeLayout rlMain;
    private FloatingActionButton btnMenu;
    private BackPressCloseHandler backPressCloseHandler;


    @Override
    protected void createActivity(Bundle savedInstanceState) {
        View view = this.setContainerView(R.layout.activity_main);
        getSupportActionBar().setTitle("경성대 헬퍼");

        if (getIntent().getExtras() != null && getIntent().getExtras().getString("state").equals("kill")) {
            final Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
            this.finish();
            return;
        }

        backPressCloseHandler = new BackPressCloseHandler(this);

        context = this.getApplicationContext();

        leftDrawerLayout = (LeftDrawerLayout) view.findViewById(R.id.left_drawer_layout);

        wvMain = (WebView) view.findViewById(R.id.wv_main);
        rlMain = (RelativeLayout) view.findViewById(R.id.rl_main);

        btnMenu = (FloatingActionButton) view.findViewById(R.id.btn_menu);
        btnMenu.setOnClickListener(this);

        removeBackBtn();

        setupToolbar();

        setupViewpager();

        setupWebview(preference.getMainWebUrl());

        //kakao 닉네임을 이용해 chatting 아이디 생성, 아이디 이미 존재 시 작동X
        checkChatId();

        //gcm id 등록
        if ((checkPlayServices()) && (!preference.isPushIdInsert())) {
            registerGCM();
            preference.setPushIdInsert(true);
        }


        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                startOverlayWindowService(context);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "권한이 없습니다.\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                startOverlayWindowService(context);
            }
        };

        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("만약 권한을 거부하시게 되면 해당 서비스를 사용하실 수 없습니다.\n(해당 권한은 푸쉬알림, 경성톡(실시간 채팅)을 위해 필요한 권한입니다.)\n권한을 허용 하시려면 [설정] > [권한] 에서 설정해 주세요.")
                .setPermissions(Manifest.permission.GET_ACCOUNTS, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    public void startOverlayWindowService(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    Toast.makeText(MainActivity.this, "오버레이 권한 확인 완료", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "오버레이 권한이 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void registerGCM() {
        Intent intent = new Intent(this, GcmIntentService.class);
        intent.putExtra("key", "register");
        startService(intent);
    }

    private void checkChatId() {
        if (MyApplication.getInstance().getPrefManager().getUser() == null) {

            ChatIdRequest request = new ChatIdRequest(this);
            request.setParams(preference.getKakaoNickName(), preference.getKakaoId());

            request.startRequest(new VolleyRequest.VolleyCallback() {
                @Override
                public void onSuccess(String result) {

                    try {
                        JSONObject obj = new JSONObject(result);

                        // check for error flag
                        if (obj.getBoolean("error") == false) {
                            // user successfully logged in

                            JSONObject userObj = obj.getJSONObject("user");
                            User user = new User(userObj.getString("user_id"), userObj.getString("name"), userObj.getString("email"));

                            // storing user in shared preferences
                            MyApplication.getInstance().getPrefManager().storeUser(user);

                        } else {
                            // login error - simply toast the message
                            Toast.makeText(getApplicationContext(), "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "json parsing error: " + e.getMessage());
                        Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            User user = MyApplication.getInstance().getPrefManager().getUser();
            Log.d("chat_user_id", user.getId());
            Log.d("chat_user_name", user.getName());
            Log.d("chat_user_email", user.getEmail());
        }
    }

    //메인 웹뷰 설정
    private void setupWebview(String url) {
        wvMain.clearHistory();
        wvMain.clearCache(true);
        wvMain.clearView();


        this.deleteDatabase("webview.db");
        this.deleteDatabase("webviewCache.db");

        wvMain.getSettings().setJavaScriptEnabled(true);
        wvMain.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wvMain.getSettings().setAppCacheEnabled(true);
        wvMain.getSettings().setSupportMultipleWindows(true);
        wvMain.getSettings().supportZoom();
        wvMain.loadUrl(url);

        wvMain.setWebViewClient(new WebViewClient() {

        });
    }

    //툴바 설정
    protected void setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.btn_left_menu);

        FragmentManager fm = getSupportFragmentManager();
        LeftMenuFragment mMenuFragment = (LeftMenuFragment) fm.findFragmentById(R.id.container_menu);
        FlowingView mFlowingView = (FlowingView) findViewById(R.id.sv);
        if (mMenuFragment == null) {
            fm.beginTransaction().add(R.id.container_menu, mMenuFragment = new LeftMenuFragment()).commit();
        }
        leftDrawerLayout.setFluidView(mFlowingView);
        leftDrawerLayout.setMenuFragment(mMenuFragment);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftDrawerLayout.toggle();
            }
        });
    }

    //아래 메뉴(서랍) 리스트 설정
    private void setupViewpager() {
        mSweetSheet = new SweetSheet(rlMain);

        mSweetSheet.setMenuList(R.menu.menu_sweet);
        mSweetSheet.setDelegate(new ViewPagerDelegate());
        mSweetSheet.setBackgroundEffect(new DimEffect(0.9f));
        mSweetSheet.setBackgroundClickEnable(true);

        mSweetSheet.setOnMenuItemClickListener(new SweetSheet.OnMenuItemClickListener() {
            @Override
            public boolean onItemClick(int position, MenuEntity menuEntity) {

                btnMenu.show();
                if (position == 0) {
                    //버스
                    startActivity(new Intent(MainActivity.this, BusActivity.class));
                } else if (position == 1) {
                    //채팅
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                } else if (position == 2) {
                    //지도
                    startActivity(new Intent(MainActivity.this, MapActivity.class));
                } else if (position == 3) {
                    //지도
                    startActivity(new Intent(MainActivity.this, FoodActivity.class));
                } else if (position == 4) {
                    //열람실 실시간 정보
                    //startWebviewActivity("http://library.ks.ac.kr/main/readingroom/main.jsp", "열람실 실시간 정보");
                    startActivity(new Intent(MainActivity.this, DeskActivity.class));
                } else if (position == 5) {
                    //교내 전화
                    startActivity(new Intent(MainActivity.this, CallActivity.class));
                } else if (position == 6) {
                    //학사일정
                    startWebviewActivity("http://cms1.ks.ac.kr/mweb/Contents.do?mCode=MN0052", "학사 일정");
                } else if (position == 7) {
                    //수강 시간표
                    startActivity(new Intent(MainActivity.this, ScheduleActivity.class));
                } else if (position == 8) {
                    //MCC 방송국
                    startActivity(new Intent(MainActivity.this, MccMainActivity.class));
                } else if (position == 9) {
                    //분실물 찾기
                    startActivity(new Intent(MainActivity.this, LostActivity.class));
                } else if (position == 10) {
                    //중고장터
                    startActivity(new Intent(MainActivity.this, SaleActivity.class));
                } else if (position == 11) {
                    //돌림판
                    startActivity(new Intent(MainActivity.this, RouletteActivity.class));
                } else if (position == 12) {
                    //학점계산기
                    startActivity(new Intent(MainActivity.this, CalculatorActivity.class));
                } else if (position == 13) {
                    //도서자료검색
                    startWebviewActivity("http://library.ks.ac.kr/mobile_web/m_search.jsp", "도서 검색");
                } else if (position == 14) {
                    //설정
                    startActivity(new Intent(MainActivity.this, SetupActivity.class));
                }

                Toast.makeText(MainActivity.this, menuEntity.title, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }

    private void startWebviewActivity(String url, String name) {
        Intent it = new Intent(MainActivity.this, WebviewActivity.class);
        it.putExtra("url", url);
        it.putExtra("name", name);
        startActivity(it);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_MENU) {
            mSweetSheet.toggle();
            menuBtnState();
            return true;
        } else if (leftDrawerLayout.isShownMenu()) {
            leftDrawerLayout.closeDrawer();
            return true;
        } else if ((keyCode == KeyEvent.KEYCODE_BACK) && (mSweetSheet.isShow())) {
            mSweetSheet.dismiss();
            menuBtnState();
            return true;
        } else if ((keyCode == KeyEvent.KEYCODE_BACK) && wvMain.canGoBack()) {
            wvMain.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    @Override
    protected void destroyActivity() {

    }

    @Override
    protected void viewClick(View view) {

        if (view.getId() == btnMenu.getId()) {
            mSweetSheet.toggle();
            menuBtnState();
        }
    }

    private void menuBtnState() {
        int x = this.rlMain.getLeft(); // 기준 설정 (가로)
        int y = this.rlMain.getTop(); // 기준 설정 (높이)
        int durationTime = 300; // 애니메이션 실행 시간 1000 = 1초

        TranslateAnimation toUpMove = new TranslateAnimation(x, x, y, -600);
        toUpMove.setDuration(durationTime);
        toUpMove.setFillAfter(true);

        TranslateAnimation toDownMove = new TranslateAnimation(x, x, -600, y);
        toDownMove.setDuration(durationTime);
        toDownMove.setFillAfter(true);

        if (mSweetSheet.isShow()) {
            btnMenu.hide();
            //btnMenu.setAnimation(toUpMove);
        } else {
            btnMenu.show();
            //btnMenu.setAnimation(toDownMove);
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Rect menu = new Rect();
        getWindow().getDecorView().getHitRect(menu);

        if (mSweetSheet.isShow()) {
            //btnMenu.show();
            btnMenu.hide();
        } else if (!mSweetSheet.isShow()) {
            btnMenu.show();
        }

        return super.dispatchTouchEvent(ev);
    }


    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 9000)
                        .show();
            } else {
                //Log.i(TAG, "This device is not supported. Google Play Services not installed!");
                Toast.makeText(getApplicationContext(), "This device is not supported. Google Play Services not installed!", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
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
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            //Log.e("name not found", e.toString());
        }
    }
}
