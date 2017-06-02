package com.zhaiugo.photopicker;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.zhaiugo.photopicker.variable.Variable;
import com.zhaiugo.photopicker.util.AndroidVersionUtil;
import com.zhaiugo.photopicker.util.FlymeUtils;
import com.zhaiugo.photopicker.util.MIUIUtils;
import com.zhaiugo.photopicker.util.ScreenUtil;
import com.zhaiugo.photopicker.util.SystemBarTintManager;

import com.meizu.flyme.reflect.StatusBarProxy;


public abstract class BaseActivity extends AppCompatActivity {
    protected String TAG = this.getClass().getSimpleName();

    protected Toolbar vToolbar;
    protected TextView vMenuText;

    protected Handler mHandler = new Handler();

    protected Context mContext;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        mContext = this;

        if (Variable.WIDTH == 0) {
            ScreenUtil.initScreenProperties(this);
        }

    }

    protected void initToolBar(String title, String menuText, int statusColor) {
        vMenuText = (TextView) findViewById(R.id.right_text);
        vToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (!TextUtils.isEmpty(title)) {
            vToolbar.setTitle(title);
        }
        if (!TextUtils.isEmpty(menuText)) {
            vMenuText.setVisibility(View.VISIBLE);
            vMenuText.setText(menuText);
        }
        // toolbar.setLogo(R.drawable.ic_launcher);
        // vToolbar.setTitle(R.string.course_detail_title);// 标题的文字需在setSupportActionBar之前，不然会无效
        // toolbar.setSubtitle("副标题");
        setSupportActionBar(vToolbar);
        /* 这些通过ActionBar来设置也是一样的，注意要在setSupportActionBar(toolbar);之后，不然就报错了 */
        // getSupportActionBar().setTitle("标题");
        // getSupportActionBar().setSubtitle("副标题");
        // getSupportActionBar().setLogo(R.drawable.ic_launcher);
        if (TextUtils.isEmpty(title)) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } else {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // 初始完头部立即设置沉浸式状态栏
        operateStatusBar(statusColor);
    }

    protected void initToolBar(int titleRes, int menuTextRes, int statusColor) {
        if (titleRes == 0) {
            initToolBar(null, mContext.getString(menuTextRes), statusColor);
        } else if (menuTextRes == 0) {
            initToolBar(mContext.getString(titleRes), null, statusColor);
        } else {
            initToolBar(mContext.getString(titleRes), mContext.getString(menuTextRes), statusColor);
        }
    }

    @SuppressLint("InlinedApi")
    protected void operateStatusBar(int color) {
        //首先要判断是否为小米/魅族系统
        if(MIUIUtils.isMIUIV6() || FlymeUtils.isFlyme()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                //状态栏透明 需要在创建SystemBarTintManager 之前调用。
                setTranslucentStatus(true);
            }
            SystemBarTintManager mTintManager = new SystemBarTintManager(this);
            StatusBarProxy.setImmersedWindow(getWindow(), true);
            mTintManager.setStatusBarTintEnabled(true);
            mTintManager.setStatusBarTintResource(color);
            if(R.color.white == color){
                mTintManager.setStatusBarDarkMode(true, this);
                StatusBarProxy.setStatusBarDarkIcon(getWindow(), true);
            }
        }else if(AndroidVersionUtil.hasM()){
            //Android6.0以上系统
            //当状态拦设置白色时，图标则设为亮灰色
            if(color == R.color.white){
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            getWindow().setStatusBarColor(getResources().getColor(color));
        }else if(AndroidVersionUtil.hasLollipop()){
            //由于5.0不支持状态栏图标的颜色修改，故白色状态栏时要设置为亮灰色
            if(color == R.color.white){
                getWindow().setStatusBarColor(getResources().getColor(R.color.grey_light));
            }else{
                getWindow().setStatusBarColor(getResources().getColor(color));
            }
        }else{
            //Android5.0以下系统,4.4虽支持沉浸式，但由于样式过于简陋，故此简单处理
            setTranslucentStatus(true);
            StatusBarProxy.setImmersedWindow(getWindow(), true);
            SystemBarTintManager mTintManager = new SystemBarTintManager(this);
            mTintManager.setStatusBarTintEnabled(true);
            if(color == R.color.white){
                mTintManager.setStatusBarTintResource(R.color.black);
            }else{
                mTintManager.setStatusBarTintResource(color);
            }

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goBack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @TargetApi(19)
    protected void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public void goBack() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    @Override
    public void startActivity(Intent it) {
        super.startActivity(it);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void startActivityNoAnim(Intent it) {
        super.startActivity(it);
        overridePendingTransition(0, 0);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void showToast(String msg) {
        if (mContext != null) {
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public void showToast(String msg, int time) {
        if (mContext != null) {
            Toast.makeText(mContext, msg, time).show();
        }
    }

    public void showToast(int resId) {
        if (mContext != null) {
            Toast.makeText(mContext, resId, Toast.LENGTH_SHORT).show();
        }
    }

    public void showToast(int resId, int time) {
        if (mContext != null) {
            Toast.makeText(mContext, resId, time).show();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

    protected abstract void initView();

    protected abstract void setListener();

}
