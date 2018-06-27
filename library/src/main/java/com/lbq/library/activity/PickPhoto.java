package com.lbq.library.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.view.Gravity;
import android.view.View;

import com.lbq.library.BuildConfig;
import com.lbq.library.R;
import com.lbq.library.base.BaseActivity;
import com.lbq.library.show.Dialog;
import java.io.File;
import java.util.List;

public abstract class PickPhoto extends BaseActivity
{
    private int max = 9; //最多可选多少个
    private String Path;
    private final int requestCodeCamera = 0x00000001; //相机编码
    private final int requestCodeAlbums = 0x00000002; //相册编码
    public  final Dialog.Pick Pick = new Dialog.Pick(this);
    private final String[] albums = new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE };
    private final String[] camera = new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA };

    public abstract void PickPath(int requestCode, String path); //相机图片回调
    public abstract void PickPath(int requestCode, List<String> paths); //多选图片列表回调

    @Override
    protected void onStart()
    {
        super.onStart();
        if (Pick.dialog == null)
        {
            Pick.create(Gravity.BOTTOM,R.style.dialog_anim_translate_bottom_to_top);
            Pick.albums.setOnClickListener(this);
            Pick.camera.setOnClickListener(this);
        }
    }
    public void setMax(int max)
    {
        this.max = max;
    }

    @Override
    public void onClick(View v)
    {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.albums)
        {
            openAlbums();
            Pick.cancel();
        }
        else if (id == R.id.camera)
        {
            openCamera();
            Pick.cancel();
        }
    }
    private String getCameraPath()
    {
        return (Path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
    }
    public void openCamera()
    {
        if (Build.VERSION.SDK_INT >= 23 && !isGrantAll(camera))
        {
            requestPermissions(camera,requestCodeCamera);
        }
        else
        {
            File file = new File(getCameraPath());
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (Build.VERSION.SDK_INT < 23)
            {
                intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(file));
            }
            else
            {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, getPackageName() + ".provider",file));
            }
            startActivityForResult(intent,requestCodeCamera);
        }
    }
    public void openAlbums()
    {
        if (Build.VERSION.SDK_INT >= 23 && !isGrantAll(albums))
        {
            requestPermissions(albums,requestCodeAlbums);
        }
        else
        {
            Intent intent = new Intent(this,AlbumsBrowser.class);
            intent.putExtra("max",max);
            intent.putExtra("button",true);
            startActivityForResult(intent,requestCodeAlbums);
        }
    }
    private boolean isGrantAll(String[] permissions)
    {
        for (String s : permissions)
        {
            if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            if (requestCode == requestCodeCamera)
            {
                PickPath(requestCode,Path);
            }
        }
        else if (resultCode == 1 && requestCode == requestCodeAlbums)
        {
            PickPath(requestCode,data.getStringArrayListExtra("list"));
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == requestCodeCamera)
        {
            if (isGrantAll(camera))
            {
                openCamera();
            }
            else
            {
                final Dialog.Query query = new Dialog.Query(this);
                query.create();
                query.content.setText("您已取消相机拍照的授权\n点击确认立即前往设置打开权限");
                query.confirm.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        query.cancel();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(Uri.fromParts("package",getApplication().getPackageName(),null));
                        startActivity(intent);
                    }
                });
                query.show();
            }
        }
        else if (requestCode == requestCodeAlbums)
        {
            if (isGrantAll(albums))
            {
                openAlbums();
            }
            else
            {
                final Dialog.Query query = new Dialog.Query(this);
                query.create();
                query.content.setText("您已取消文件读写的授权\n点击确认立即前往设置打开权限");
                query.confirm.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        query.cancel();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(Uri.fromParts("package",getApplication().getPackageName(),null));
                        startActivity(intent);
                    }
                });
                query.show();
            }
        }
    }
}
