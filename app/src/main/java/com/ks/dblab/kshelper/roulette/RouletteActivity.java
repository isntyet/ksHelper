package com.ks.dblab.kshelper.roulette;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.dd.CircularProgressButton;
import com.ks.dblab.kshelper.R;
import com.ks.dblab.kshelper.activity.BaseActivity;
import com.szugyi.circlemenu.view.CircleImageView;
import com.szugyi.circlemenu.view.CircleLayout;

/**
 * Created by jojo on 2016-07-07.
 */
public class RouletteActivity extends BaseActivity implements CircleLayout.OnItemClickListener, CircleLayout.OnRotationFinishedListener,CircleLayout.OnItemSelectedListener , CircleLayout.OnCenterClickListener{

    private CircleLayout circleLayout;
    private CircularProgressButton btnMenuDelete;
    private CircularProgressButton btnAuto;
    private Dialog dialog;
    private Toast toast = null;
    private Display display;

    @Override
    protected void createActivity(Bundle savedInstanceState) {
        View view = this.setContainerView(R.layout.activity_roulette);
        getSupportActionBar().setTitle("돌려돌려 돌림판");

        display = getWindowManager().getDefaultDisplay();

        circleLayout = (CircleLayout) view.findViewById(R.id.circle_layout);
        circleLayout.setOnItemSelectedListener(this);
        circleLayout.setOnItemClickListener(this);
        circleLayout.setOnRotationFinishedListener(this);
        circleLayout.setOnCenterClickListener(this);
        circleLayout.setRotating(false);

        btnMenuDelete = (CircularProgressButton) view.findViewById(R.id.btn_menu_delete);
        btnMenuDelete.setOnClickListener(this);

        btnAuto = (CircularProgressButton) view.findViewById(R.id.btn_auto);
        btnAuto.setOnClickListener(this);


        //onAddClick();
    }

    public void onAddClick(String content) {
        int width = display.getWidth();
        int size = width/4;
        CircleImageView newMenu = new CircleImageView(this);

        ShapeDrawable drawable = makeCircle(size, size, Color.argb(255, 0,0,0));
        TextDrawable td = makeTextDrawable(Color.WHITE, Color.argb(255, 0,0,0), size/6, content);

        newMenu.setBackgroundDrawable(drawable);
        newMenu.setImageDrawable(td);
        newMenu.setName(content);

        circleLayout.addView(newMenu);
    }

    public void onRemoveClick() {
        if (circleLayout.getChildCount() > 0) {
            circleLayout.removeViewAt(circleLayout.getChildCount() - 1);
        }
    }

    private TextDrawable makeTextDrawable(int textColor, int backgroundColor, int textSize, String text) {
        TextDrawable td = TextDrawable.builder().beginConfig()
                .textColor(textColor)
                .useFont(Typeface.DEFAULT)
                .fontSize(textSize) /* size in px */
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRound(text, backgroundColor); // radius in px

        return td;
    }


    private ShapeDrawable makeCircle(int width, int height, int color) {
        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
        drawable.setIntrinsicWidth(width);
        drawable.setIntrinsicHeight(height);
        drawable.getPaint().setColor(color);

        return drawable;
    }

    private void loadDialog() {
        dialog = new Dialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.item_dialog_roulette);
        dialog.setTitle("메뉴 작성");

        final EditText etContent = (EditText) dialog.findViewById(R.id.et_content);

        dialog.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etContent.length() == 0){
                    Toast.makeText(RouletteActivity.this, "내용을 제대로 작성해 주세요", Toast.LENGTH_SHORT).show();
                } else {
                    onAddClick(etContent.getText().toString());
                    circleLayout.setRotating(true);
                    Toast.makeText(RouletteActivity.this, "저장 완료", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
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

    @Override
    protected void destroyActivity() {

    }

    @Override
    protected void viewClick(View view) {
        if (view.getId() == R.id.btn_back) {
            this.finish();
        } else if(view.getId() == btnMenuDelete.getId()){
            onRemoveClick();
            if(circleLayout.getChildCount() == 0){
                circleLayout.setRotating(false);
            } else {
                circleLayout.setRotating(true);
            }
        } else if(view.getId() == btnAuto.getId()){
            //Log.d("getWidth", display.getWidth() + "");
            //Log.d("getHeight", display.getHeight() + "");
            int height = display.getHeight();
            int start = height/4;
            int end = height - start;

            long downTime = SystemClock.uptimeMillis();
            long eventTime = SystemClock.uptimeMillis();
            MotionEvent event = MotionEvent.obtain(downTime, eventTime+1000, MotionEvent.ACTION_DOWN, 0, start , 0);
            MotionEvent event1 = MotionEvent.obtain(downTime+1000, eventTime+1500, MotionEvent.ACTION_MOVE, 0, end , 0);
            MotionEvent event2 = MotionEvent.obtain(downTime+1500, eventTime+1500, MotionEvent.ACTION_UP, 0, 0 , 0);
            //MotionEvent event3 = MotionEvent.obtain
            this.dispatchTouchEvent(event);
            this.dispatchTouchEvent(event1);
            this.dispatchTouchEvent(event2);


        }
    }

    @Override
    public void onCenterClick() {
        //가운데 터치
        loadDialog();
    }

    @Override
    public void onItemClick(View view) {

    }

    @Override
    public void onItemSelected(View view) {
        final String name;
        if (view instanceof CircleImageView) {
            name = ((CircleImageView) view).getName();
        } else {
            name = null;
        }


        if (toast == null) {
            toast = Toast.makeText(this, name, Toast.LENGTH_SHORT);
        } else {
            toast.cancel();
            toast = Toast.makeText(this, name, Toast.LENGTH_SHORT);
        }
        toast.show();


    }

    @Override
    public void onRotationFinished(View view) {
        Animation animation = new RotateAnimation(0, 360, view.getWidth() / 2, view.getHeight() / 2);
        animation.setDuration(300);
        view.startAnimation(animation);
    }
}
