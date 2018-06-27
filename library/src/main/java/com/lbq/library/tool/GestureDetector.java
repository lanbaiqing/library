package com.lbq.library.tool;

import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;

public abstract class GestureDetector <T extends GestureDetector>
{
    private int[] values;
    private int width = 0;
    private int height = 0;
    private int paddingTop = 0;
    private int paddingLeft = 0;
    private int paddingRight = 0;
    private int paddingBottom = 0;

    private final int click = 0;
    private final int move = 3;
    private final int long_click = 1;
    private final int double_click = 2;
    private final long short_timeout = 300;
    private final long long_timeout = 500;
    private float dx1,dx2,dy1,dy2;
    private float mx1,mx2,my1,my2;
    private float ux1,ux2,uy1,uy2;

    public void setUsefulRange(int... values)
    {
        this.values = values;
        regainUsefulRange();
    }
    public void regainUsefulRange()
    {
        this.width = values[0];
        this.height =  values[1];

        this.paddingTop =  values[2];
        this.paddingBottom = this.height -  values[3];

        this.paddingLeft =  values[4];
        this.paddingRight = this.width -  values[5];
    }
    public void resetUsefulRange()
    {
        this.width = 0;
        this.height = 0;
        this.paddingTop = 0;
        this.paddingBottom = 0;
        this.paddingLeft = 0;
        this.paddingRight = 0;
    }
    public void onMove(float x1, float x2, float y1, float y2)
    {

    }
    public void onClick()
    {}
    public void onLongClick()
    {}
    public void onDoubleClick()
    {}
    private Handler handler = new Handler(new Handler.Callback()
    {

        @Override
        public boolean handleMessage(Message msg)
        {
            if (msg.what == move)
            {
                onMove(dx1,mx1,dy1,my1);
            }
            else if (msg.what == click)
            {
                onClick();
                handler.removeMessages(msg.what);
            }
            else if (msg.what == long_click)
            {
                onLongClick();
                handler.removeMessages(msg.what);
            }
            else if (msg.what == double_click)
            {
                onDoubleClick();
                handler.removeMessages(msg.what);
            }
            return false;
        }
    });
    public void onTouchEvent(MotionEvent ev)
    {
        final int count = ev.getPointerCount();
        if (count == 1)
        {
            final float x = ev.getX(0);
            final float y = ev.getY(0);
            if (paddingTop > 0 && paddingTop > y)
                return;
            else if (paddingBottom > 0 && y > paddingBottom)
                return;
            else if (paddingLeft > 0 && paddingLeft > x)
                return;
            else if (paddingRight > 0 && x > paddingRight)
                return;
            else
            {
                switch (ev.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        dx1 = ev.getX(0);
                        dy1 = ev.getY(0);
                        if (handler.hasMessages(click))
                        {
                            handler.removeMessages(click);
                            handler.sendEmptyMessage(double_click);
                        }
                        else
                            handler.sendEmptyMessageDelayed(long_click,long_timeout);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mx1 = ev.getX(0);
                        my1 = ev.getY(0);
                        if (dx1 != mx1 || dy1 != my1)
                        {
                            if (handler.hasMessages(long_click))
                            {
                                handler.removeMessages(long_click);
                                handler.sendEmptyMessage(move);
                            }
                            else if (handler.hasMessages(move))
                            {
                                handler.removeMessages(move);
                                handler.sendEmptyMessage(move);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        ux1 = ev.getX(0);
                        uy1 = ev.getY(0);
                        if (handler.hasMessages(long_click))
                        {
                            handler.removeMessages(long_click);
                            if (dx1 == ux1 && dy1 == uy1)
                            {
                                handler.sendEmptyMessageDelayed(click,short_timeout);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        else
        {
            if (handler.hasMessages(move))
                handler.removeMessages(move);
            if (handler.hasMessages(click))
                handler.removeMessages(click);
            if (handler.hasMessages(long_click))
                handler.removeMessages(long_click);
            if (handler.hasMessages(double_click))
                handler.removeMessages(double_click);
        }
    }
}
