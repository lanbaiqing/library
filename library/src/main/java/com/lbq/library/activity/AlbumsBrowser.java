package com.lbq.library.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.lbq.library.R;
import com.lbq.library.base.BaseActivity;
import com.lbq.library.show.Dialog;
import com.lbq.library.tool.Img;
import com.lbq.library.tool.Utils;
import com.lbq.library.view.PopupWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AlbumsBrowser extends BaseActivity
{
    private int max; //最多可选多少个
    private String defaultKey = "全部图片";
    private String cachePath = "/Albums/";     //缓存目录
    private String diskPath = Environment.getExternalStorageDirectory().getAbsolutePath();

    private PopupWindow window;
    private GridView mGridView;
    private ListView mListView;
    private GridAdapter mAdapter;
    private TextView tv_finish,tv_preview,tv_type;

    private ArrayList<String> sList = new ArrayList<>();

    private LinkedHashMap<String, String> lruCache = new LinkedHashMap<>();

    private LinkedHashMap<String,ArrayList<File>> maps = new LinkedHashMap<>();
    {
        maps.put("全部图片",new ArrayList<File>());
    }
    private Handler handler = new Handler(new Handler.Callback()
    {

        @Override
        public boolean handleMessage(Message msg)
        {
            if (msg.what == 1)
            {

            }
            else if (msg.what == 2)
            {
                mAdapter.notifyDataSetChanged();
            }
            else if (msg.what == 3)
            {

            }
            return false;
        }
    });
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums_browser);
        mGridView = findViewById(R.id.albums_grid);
        tv_type = findViewById(R.id.type);
        tv_finish = findViewById(R.id.albums_finish);
        tv_preview = findViewById(R.id.albums_preview);
        findViewById(R.id.albums_back).setOnClickListener(this);
        findViewById(R.id.albums_finish).setOnClickListener(this);
        findViewById(R.id.albums_popup).setOnClickListener(this);
        findViewById(R.id.albums_preview).setOnClickListener(this);
        mGridView.setAdapter((mAdapter = new GridAdapter()));
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (sList.contains(maps.get(defaultKey).get(position).getAbsolutePath()))
                    sList.remove(maps.get(defaultKey).get(position).getAbsolutePath());
                else if (max == 1)
                {
                    sList.clear();
                    sList.add(maps.get(defaultKey).get(position).getAbsolutePath());
                }
                else if (sList.size() >= max)
                    showToast(String.format("最多只能选择%s个",max));
                else
                    sList.add(maps.get(defaultKey).get(position).getAbsolutePath());
                mAdapter.notifyDataSetChanged();
            }
        });
        if (Build.VERSION.SDK_INT < 23)
            new scanFile().start();
        else
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        max = getIntent().getIntExtra("max",1);
    }
    private void popupWindow()
    {
        if (window == null)
        {
            final View view = this.getLayoutInflater().inflate(R.layout.popup_window,null);
            mListView = view.findViewById(R.id.list);
            mListView.setAdapter(new ListAdapter());
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View v, int position, long l)
                {
                    defaultKey = maps.keySet().toArray()[position].toString();
                    mAdapter.notifyDataSetChanged();
                    window.dismiss();
                }
            });
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            float density = getResources().getDisplayMetrics().density;
            window = new PopupWindow(view,width,height - (int)(155 * density + 0.5f));
            window.setFocusable(true);
            window.setOutsideTouchable(true);
            window.setBackgroundDrawable(new ColorDrawable(0x00000000));
            window.update();
        }
        if (window.isShowing())
        {
            window.dismiss();
        }
        else
        {
            window.showAsDropDown(findViewById(R.id.albums_navigation));
        }
    }
    @Override
    public void onClick(View v)
    {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.albums_finish && tv_finish.isSelected())
        {
            Intent intent = new Intent();
            intent.putStringArrayListExtra("list",sList);
            setResult(1,intent);
            finish();
        }
        else if (id == R.id.albums_preview && tv_preview.isSelected())
        {
            Intent intent = getIntent();
            intent.setClass(this,AlbumsPreview.class);
            intent.putStringArrayListExtra("list",sList);
            startActivityForResult(intent,1);
        }
        else if (id == R.id.albums_popup)
        {
            popupWindow();
        }
    }
    private int getWidthPx()
    {
        return Math.round((Utils.getWidthPx(this) - Utils.dpToPx(this,9)) / 4);
    }
    private class scanFile extends Thread
    {
        private File getCachePath(File file)
        {
            File newFile = new File(diskPath + cachePath + file.getParent().replace(diskPath,""));
            if (!newFile.exists())
            {
                newFile.mkdirs();
            }
            return newFile;
        }
        private void readBitmap(final File file)
        {
            final File newFile = new File(getCachePath(file).getAbsolutePath() + "/." + file.getName());
            if (newFile.exists())
            {
                lruCache.put(file.getAbsolutePath(), newFile.getAbsolutePath());
            }
            else
            {
                Bitmap bitmap1 = Img.inSampleSize(file.getAbsolutePath(), getWidthPx(), getWidthPx());

                Bitmap bitmap2 = Img.thumbnail(bitmap1, getWidthPx(), getWidthPx());

                lruCache.put(file.getAbsolutePath(), newFile.getAbsolutePath());

                Img.save(Bitmap.CompressFormat.JPEG, newFile.getAbsolutePath(), bitmap2,100);

                if (!bitmap1.isRecycled())
                {
                    bitmap1.recycle();
                }
                if (!bitmap2.isRecycled())
                {
                    bitmap2.recycle();
                }
            }
        }
        public void run()
        {
            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null,null,null,null);
            if (cursor != null)
            {
                if (cursor.moveToFirst())
                {
                    while (cursor.moveToNext())
                    {
                        File file = new File(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                        if (file.getParent().startsWith(diskPath + cachePath.substring(0,cachePath.length()-1)) || !file.exists())
                        {
                            continue;
                        }
                        if (!maps.containsKey(file.getParent()))
                        {
                            maps.put(file.getParent(),new ArrayList<File>());
                        }
                        if (!maps.get("全部图片").contains(file))
                        {
                            maps.get("全部图片").add(file);
                        }
                        if (!maps.get(file.getParent()).contains(file))
                        {
                            maps.get(file.getParent()).add(file);
                        }
                    }
                }
                cursor.close();
                Collections.sort(maps.get("全部图片"), new Comparator<File>()
                {
                    @Override
                    public int compare(File o1,File o2)
                    {
                        if (o1.lastModified() > o2.lastModified())
                            return -1;
                        else if (o1.lastModified() < o2.lastModified())
                            return 1;
                        else
                            return 0;
                    }
                });
                for (String key : maps.keySet())
                {
                    Collections.sort(maps.get(key), new Comparator<File>()
                    {
                        @Override
                        public int compare(File o1, File o2)
                        {
                            if (o1.lastModified() > o2.lastModified())
                                return -1;
                            else if (o1.lastModified() < o2.lastModified())
                                return 1;
                            else
                                return 0;
                        }
                    });
                }
                handler.obtainMessage(1).sendToTarget();
                for (File file : maps.get("全部图片"))
                {
                    try
                    {
                        readBitmap(file);
                        handler.obtainMessage(2).sendToTarget();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                handler.obtainMessage(3).sendToTarget();
            }
        }
    }

    private ExecutorService cachedThread = Executors.newCachedThreadPool();

    private class GridAdapter extends BaseAdapter
    {

        private LayoutInflater inflater;
        {
            inflater = LayoutInflater.from(AlbumsBrowser.this);
        }
        @Override
        public int getCount()
        {
            if (sList.size() == 0)
            {
                tv_finish.setText("完成");
                tv_preview.setText("预览");
                tv_finish.setSelected(false);
                tv_preview.setSelected(false);
            }
            else
            {
                tv_finish.setSelected(true);
                tv_preview.setSelected(true);
                tv_preview.setText(String.format("预览(%s)",sList.size()));
                tv_finish.setText(String.format("完成(%s/%s)",sList.size(),max));
            }
            tv_type.setText(defaultKey.split("/")[defaultKey.split("/").length -1]);
            return maps.get(defaultKey).size();
        }
        @Override
        public Object getItem(int position)
        {
            return maps.get(defaultKey).get(position).getAbsolutePath();
        }
        @Override
        public long getItemId(int position)
        {
            return position;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            final ViewHolder holder;
            if (convertView == null)
            {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_albums,null);
                holder.img = convertView.findViewById(R.id.albums_img);
                holder.gif = convertView.findViewById(R.id.albums_gif);
                holder.img.setMinimumWidth(getWidthPx());
                holder.img.setMinimumHeight(getWidthPx());
                holder.img.setMaxWidth(getWidthPx());
                holder.img.setMaxHeight(getWidthPx());
                holder.view = convertView.findViewById(R.id.albums_view);
                holder.checkBox = convertView.findViewById(R.id.albums_checkBox);
                convertView.setTag(holder);
            }
            else holder = (ViewHolder) convertView.getTag();
            holder.gif.setVisibility(isGif(position) ? View.VISIBLE : View.GONE);

            if (lruCache.get(getItem(position).toString()) != null)
            {
                cachedThread.execute(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        final Bitmap bitmap = BitmapFactory.decodeFile(lruCache.get(getItem(position).toString()));
                        handler.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                holder.img.setImageBitmap(bitmap);
                            }
                        });
                    }
                });
            }
            holder.checkBox.setChecked(sList.contains(getItem(position).toString()));
            if (max == 1)
            {
                if (sList.contains(getItem(position).toString()))
                    holder.checkBox.setVisibility(View.VISIBLE);
                else
                    holder.checkBox.setVisibility(View.GONE);
            }
            holder.view.setVisibility(sList.contains(getItem(position).toString()) ? View.VISIBLE : View.GONE);
            return convertView;

        }
        private class ViewHolder
        {
            View view;
            ImageView img,gif;
            CheckBox checkBox;
        }
        private boolean isGif(int position)
        {
            return maps.get(defaultKey).get(position).getName().toLowerCase().endsWith(".gif");
        }
    }
    private class ListAdapter extends BaseAdapter
    {
        private LayoutInflater inflater;
        {
            this.inflater = LayoutInflater.from(AlbumsBrowser.this);
        }
        @Override
        public int getCount()
        {
            return maps.size();
        }
        @Override
        public Object getItem(int position)
        {
            return maps.keySet().toArray()[position];
        }
        @Override
        public long getItemId(int position)
        {
            return position;
        }
        @Override
        public View getView(int position, View view, ViewGroup viewGroup)
        {
            final ViewHolder holder;
            final String key = getItem(position).toString();
            if (view == null)
            {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.item_popup,null);
                holder.img = view.findViewById(R.id.img);
                holder.status = view.findViewById(R.id.status);
                holder.count = view.findViewById(R.id.count);
                holder.name = view.findViewById(R.id.name);
                view.setTag(holder);
            }
            else holder = (ViewHolder) view.getTag();
            holder.count.setText((maps.get(key).size() + "张"));
            holder.status.setVisibility(defaultKey.equals(key) ? View.VISIBLE : View.GONE);
            holder.name.setText(key.split("/")[key.split("/").length -1]);

            if (maps.get(key).size() > 0)
            {
                Bitmap bitmap = BitmapFactory.decodeFile(lruCache.get(maps.get(key).get(0).getAbsolutePath()));
                holder.img.setImageBitmap(bitmap);

                //####
            }
            return view;
        }
        private class ViewHolder
        {
            TextView name,count;
            ImageView img,status;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == 1 && requestCode == 1)
        {
            setResult(1,data);
            finish();
        }
        else if (resultCode == 2 && requestCode == 1)
        {
            sList = data.getStringArrayListExtra("list");
            mAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1)
        {
            if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                new scanFile().start();
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
