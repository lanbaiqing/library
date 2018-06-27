package com.lbq.library.view;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.lbq.library.R;

public class PopupWindow extends android.widget.PopupWindow
{
    public PopupWindow(View view,int width,int height)
    {
        super(view,width,height);
    }
    @Override
    public void showAsDropDown(View anchor)
    {
        super.showAsDropDown(anchor);
        getContentView().startAnimation(AnimationUtils.loadAnimation(anchor.getContext(),R.anim.anim_popup_in));
    }
    @Override
    public void dismiss()
    {
        Animation animation = AnimationUtils.loadAnimation(getContentView().getContext(), R.anim.anim_popup_out);
        getContentView().startAnimation(animation);
        getContentView().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                PopupWindow.super.dismiss();
            }
        },animation.getDuration());
    }
}
