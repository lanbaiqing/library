package com.lbq.library.base;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.lbq.library.R;


public class BaseActivity extends AppCompatActivity implements View.OnClickListener
{
    private Toast toast;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            //透明顶部状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明底部导航栏
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        //隐藏底部导航栏
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        //全屏
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        if (id == R.id.albums_back)
        {
            super.onBackPressed();
        }
    }
    public void showToast(String str)
    {
        if (toast != null) toast.cancel();
        (toast = Toast.makeText(this,str,Toast.LENGTH_SHORT)).show();
    }
}
