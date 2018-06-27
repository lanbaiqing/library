package com.lbq.demo;

import android.os.Bundle;
import android.view.View;

import com.lbq.library.activity.PickPhoto;

import java.util.List;

public class MainActivity extends PickPhoto
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    @Override
    public void PickPath(int requestCode, String path)
    {

    }
    @Override
    public void PickPath(int requestCode, List<String> paths)
    {

    }
    public void onClick(View view)
    {
        super.onClick(view);
        switch (view.getId())
        {
            case R.id.click:
                Pick.show();
                break;
        }
    }
}
