package com.lbq.library.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lbq.library.R;
import com.lbq.library.base.BaseActivity;
import com.lbq.library.tool.GestureDetector;
import com.lbq.library.view.PhotoViewPager;
import com.github.chrisbanes.photoviews.PhotoView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class AlbumsPreview extends BaseActivity
{
    private int maxKB = 1024 * 2; //图像大于多少 KB 进行压缩
    private GestureDetector detector;
    private CheckBox checkBox;

    private PageAdapter mAdapter;
    private PhotoViewPager mViewPager;
    private TextView tv_count,tv_index,tv_finish;

    private boolean NAVIGATION = true;
    private LinearLayout TOP_NAVIGATION;
    private RelativeLayout BOTTOM_NAVIGATION;

    private List<String> sList = new ArrayList<>();
    private List<Boolean> bList = new ArrayList<>();
    private Handler handler = new Handler();
    private Executor mThread = Executors.newFixedThreadPool(1);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums_preview);
        findViewById(R.id.albums_back).setOnClickListener(this);
        findViewById(R.id.albums_finish).setOnClickListener(this);
        tv_count = findViewById(R.id.count);
        tv_index = findViewById(R.id.index);
        tv_finish = findViewById(R.id.albums_finish);
        checkBox = findViewById(R.id.checkbox);
        mViewPager = findViewById(R.id.albums_viewPage);
        TOP_NAVIGATION = findViewById(R.id.TOP_NAVIGATION);
        BOTTOM_NAVIGATION = findViewById(R.id.BOTTOM_NAVIGATION);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                bList.set(mViewPager.getCurrentItem(),b);
                tv_finish.setText(String.format("完成(%s)",getCheckedCount()));
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }
            @Override
            public void onPageSelected(int position)
            {
                checkBox.setChecked(bList.get(position));
                tv_index.setText(String.valueOf(position + 1));
            }
            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });

        sList = getIntent().getStringArrayListExtra("list");
        if (!getIntent().getBooleanExtra("button",false))
        {
            tv_finish.setVisibility(View.GONE);
            findViewById(R.id.albums_select).setVisibility(View.GONE);
        }
        else
        {
            tv_finish.setSelected(true);
            tv_finish.setText(String.format("完成(%s)",sList.size()));
        }
        tv_count.setText(String.valueOf(sList.size()));
        mViewPager.setAdapter((mAdapter = new PageAdapter()));

        detector = new GestureDetector()
        {
            @Override
            public void onClick()
            {
                if (NAVIGATION)
                    HideNAVIGATION();
                else
                    ShowNAVIGATION();
            }
        };
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int paddingTop = Math.round(75 * getResources().getDisplayMetrics().density + 0.5f);
        int paddingBottom = Math.round(40 * getResources().getDisplayMetrics().density + 0.5f);
        int paddingLeft = 0;
        int paddingRight = 0;
        detector.setUsefulRange(width,height,paddingTop,paddingBottom,paddingLeft,paddingRight);
    }
    private int getCheckedCount()
    {
        int count = 0;
        for (boolean b : bList)
        {
            if (b)
                count ++;
        }
        return count;
    }
    private class PageAdapter extends PagerAdapter
    {
        private List<View> vList = new ArrayList<>();
        public PageAdapter()
        {
            for (int i = 0 ; i < sList.size() ; i ++ )
            {
                bList.add(true);
                if (isGif(i))
                    loadingGif(i);
                else
                    loadingImg(i);
            }
        }
        private boolean isGif(int position)
        {
            return sList.get(position).toLowerCase().endsWith(".gif");
        }
        private void loadingGif(int position)
        {
            View view = LayoutInflater.from(AlbumsPreview.this).inflate(R.layout.layout_gif,null);
            GifImageView gifImageView = view.findViewById(R.id.gifView);
            try
            {
                GifDrawable drawable = new GifDrawable(sList.get(position));
                gifImageView.setImageDrawable(drawable);
            }
            catch (IOException e)
            {
                gifImageView.setImageResource(R.drawable.ic_image);
            }
            vList.add(view);
        }
        private void loadingImg(int position)
        {
            View view = LayoutInflater.from(AlbumsPreview.this).inflate(R.layout.layout_photo,null);
            final PhotoView photoView = view.findViewById(R.id.photoView);
            final File file = new File(sList.get(position));
            if (file.lastModified() / 1024 > maxKB)
            {
                mThread.execute(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = false;
                            options.inSampleSize = 2;
                            final Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),options);
                            int quality = 100;
                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);

                            while (bytes.toByteArray().length / 1024 > maxKB)
                            {
                                bytes.reset();
                                quality -= 10;
                                bitmap.compress(Bitmap.CompressFormat.JPEG,quality,bytes);
                            }
                            handler.post(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    photoView.setImageBitmap(bitmap);
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                        catch (Exception e)
                        {
                            handler.post(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    photoView.setImageResource(R.drawable.ic_image);
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });
            }
            else
            {
                try
                {
                    photoView.setImageURI(Uri.parse(file.getAbsolutePath()));
                }
                catch (Exception e)
                {
                    photoView.setImageResource(R.drawable.ic_image);
                }
            }
            vList.add(view);
        }
        @Override
        public int getCount()
        {
            return vList.size();
        }
        @Override
        public boolean isViewFromObject(View view, Object object)
        {
            return object == view;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            container.removeView((View)object);
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            container.addView(vList.get(position));
            return vList.get(position);
        }
    }
    private Animation getAnimation(int id)
    {
        return AnimationUtils.loadAnimation(this,id);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        detector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }
    private void HideNAVIGATION()
    {
        NAVIGATION = false;
        detector.resetUsefulRange();
        TOP_NAVIGATION.setVisibility(View.GONE);
        BOTTOM_NAVIGATION.setVisibility(View.GONE);
        TOP_NAVIGATION.startAnimation(getAnimation(R.anim.layout_anim_hide_from_bottom_to_top));
        BOTTOM_NAVIGATION.startAnimation(getAnimation(R.anim.layout_anim_hide_from_top_to_bottom));
    }
    private void ShowNAVIGATION()
    {
        NAVIGATION = true;
        detector.regainUsefulRange();
        TOP_NAVIGATION.setVisibility(View.VISIBLE);
        BOTTOM_NAVIGATION.setVisibility(View.VISIBLE);
        TOP_NAVIGATION.startAnimation(getAnimation(R.anim.layout_anim_show_from_top_to_bottom));
        BOTTOM_NAVIGATION.startAnimation(getAnimation(R.anim.layout_anim_show_from_bottom_to_top));
    }
    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.albums_back)
        {
            this.onBackPressed();
        }
        else if (v.getId() == R.id.albums_finish)
        {
            ArrayList<String> list = new ArrayList<>();
            for (int i = 0 ; i < sList.size() ; i ++ )
            {
                if (bList.get(i)) list.add(sList.get(i));
            }
            Intent intent = new Intent();
            intent.putStringArrayListExtra("list",list);
            setResult(1,intent);
            finish();
        }
    }
    @Override
    public void onBackPressed()
    {
        if (getIntent().getBooleanExtra("button",false))
        {
            ArrayList<String> list = new ArrayList<>();
            for (int i = 0 ; i < sList.size() ; i ++ )
            {
                if (bList.get(i)) list.add(sList.get(i));
            }
            Intent intent = new Intent();
            intent.putStringArrayListExtra("list",list);
            setResult(2,intent);
            finish();
        }
        else
            finish();
    }
}
