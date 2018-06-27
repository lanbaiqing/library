package com.lbq.library.tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

public class Img
{
    public static Bitmap inSampleSize(String path, int w1, int h1)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);
        int w2 = options.outWidth;
        int h2 = options.outHeight;
        int inSampleSize = 1;
        if (w1 < w2 || h1 < h2)
        {
            int w3 = w2 / w1;
            int h3 = h2 / h1;
            inSampleSize = w3 < h3 ? w3 : h3;
        }
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path,options);
    }
    public static Bitmap compress(Bitmap bitmap,Bitmap.CompressFormat format, int quality, int maxKB)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(format,quality,bytes);
        while (bytes.toByteArray().length / 1024 > maxKB)
        {
            quality -= 10;
            bytes.reset();
            bitmap.compress(format, quality, bytes);
        }
        if (!bitmap.isRecycled())
        {
            bitmap.recycle();
        }
        return BitmapFactory.decodeByteArray(bytes.toByteArray(),0,bytes.size());
    }
    public static boolean save(Bitmap.CompressFormat format ,String path, Bitmap bitmap ,int quality)
    {
        try
        {
            FileOutputStream output = new FileOutputStream(path);
            if (bitmap.compress(format, quality, output))
            {
                output.close();
                return true;
            }
            output.close();
        }
        catch (Exception e)
        {
            Log.e("Save Bitmap",e.toString());
        }
        return false;
    }
    public static Bitmap thumbnail(Bitmap bitmap , int width ,int height)
    {
        return ThumbnailUtils.extractThumbnail(bitmap,width,height);
    }
    public static Bitmap circular(Context context, Bitmap bitmap)
    {
        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(context.getResources(),bitmap);
        drawable.setCircular(true);
        return drawable.getBitmap();
    }
    public static Bitmap cornerRadius(Context context, Bitmap bitmap, int dpRadius)
    {
        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(context.getResources(),bitmap);
        drawable.setCornerRadius(Utils.dpToPx(context,dpRadius));
        return drawable.getBitmap();
    }
}
