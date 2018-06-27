package com.lbq.library.show;

import android.content.Context;
import android.support.v7.app.AppCompatDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lbq.library.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Dialog extends AppCompatDialog
{
    public Dialog(Context context,int theme)
    {
        super(context,theme);
    }
    public static class Query
    {
        private Dialog dialog;
        private Context context;
        public TextView title,content,confirm,cancel;
        public Query(Context context)
        {
            this.context = context;
        }
        public Dialog create()
        {
            dialog = new Dialog(context, R.style.dialog_style);
            dialog.setContentView(R.layout.dialog_query);
            title = dialog.findViewById(R.id.title);
            content = dialog.findViewById(R.id.content);
            confirm = dialog.findViewById(R.id.confirm);
            cancel = dialog.findViewById(R.id.cancel);
            return dialog;
        }
        public Dialog create(int gravity)
        {
            dialog = new Dialog(context, R.style.dialog_style);
            dialog.getWindow().setGravity(gravity);
            dialog.setContentView(R.layout.dialog_query);
            title = dialog.findViewById(R.id.title);
            content = dialog.findViewById(R.id.content);
            confirm = dialog.findViewById(R.id.confirm);
            cancel = dialog.findViewById(R.id.cancel);
            return dialog;
        }
        public Dialog create(int gravity,int anim)
        {
            dialog = new Dialog(context, R.style.dialog_style);
            dialog.getWindow().setGravity(gravity);
            dialog.getWindow().setWindowAnimations(anim);
            dialog.setContentView(R.layout.dialog_query);
            title = dialog.findViewById(R.id.title);
            content = dialog.findViewById(R.id.content);
            confirm = dialog.findViewById(R.id.confirm);
            cancel = dialog.findViewById(R.id.cancel);
            return dialog;
        }
        public void show()
        {
            dialog.show();
        }
        public void cancel()
        {
            dialog.cancel();
        }
        public void setCancelable(boolean flag)
        {
            dialog.setCancelable(flag);
        }
    }
    public static class Item
    {
        private Dialog dialog;
        private Context context;
        public ListView listView;
        public Item(Context context)
        {
            this.context = context;
        }
        public Dialog create()
        {
            dialog = new Dialog(context,R.style.dialog_style);
            dialog.setContentView(R.layout.dialog_item);
            listView = dialog.findViewById(R.id.list_item);
            return dialog;
        }
        public Dialog create(int gravity)
        {
            dialog = new Dialog(context, R.style.dialog_style);
            dialog.getWindow().setGravity(gravity);
            dialog.setContentView(R.layout.dialog_item);
            listView = dialog.findViewById(R.id.list_item);
            return dialog;
        }
        public Dialog create(int gravity,int anim)
        {
            dialog = new Dialog(context, R.style.dialog_style);
            dialog.getWindow().setGravity(gravity);
            dialog.getWindow().setWindowAnimations(anim);
            dialog.setContentView(R.layout.dialog_item);
            listView = dialog.findViewById(R.id.list_item);
            return dialog;
        }
        public void show()
        {
            dialog.show();
        }
        public void cancel()
        {
            dialog.cancel();
        }
        public void setCancelable(boolean flag)
        {
            dialog.setCancelable(flag);
        }
    }
    public static class Pick
    {
        public Dialog dialog;
        private Context context;
        public TextView albums,camera,cancel;
        public Pick(Context context)
        {
            this.context = context;
        }
        public Dialog create()
        {
            dialog = new Dialog(context,R.style.dialog_style);
            dialog.setContentView(R.layout.dialog_pick);
            albums = dialog.findViewById(R.id.albums);
            camera = dialog.findViewById(R.id.camera);
            cancel = dialog.findViewById(R.id.cancel);
            cancel.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    dialog.cancel();
                }
            });
            return dialog;
        }
        public Dialog create(int gravity)
        {
            dialog = new Dialog(context, R.style.dialog_style);
            dialog.getWindow().setGravity(gravity);
            dialog.setContentView(R.layout.dialog_pick);
            albums = dialog.findViewById(R.id.albums);
            camera = dialog.findViewById(R.id.camera);
            cancel = dialog.findViewById(R.id.cancel);
            cancel.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    dialog.cancel();
                }
            });
            return dialog;
        }
        public Dialog create(int gravity,int anim)
        {
            dialog = new Dialog(context, R.style.dialog_style);
            dialog.getWindow().setGravity(gravity);
            dialog.getWindow().setWindowAnimations(anim);
            dialog.setContentView(R.layout.dialog_pick);
            albums = dialog.findViewById(R.id.albums);
            camera = dialog.findViewById(R.id.camera);
            cancel = dialog.findViewById(R.id.cancel);
            cancel.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    dialog.cancel();
                }
            });
            return dialog;
        }
        public void show()
        {
            dialog.show();
        }
        public void cancel()
        {
            dialog.cancel();
        }
        public void setCancelable(boolean flag)
        {
            dialog.setCancelable(flag);
        }
    }
    public static class Loading
    {
        private Dialog dialog;
        private Context context;
        public TextView content;
        public Loading(Context context)
        {
            this.context = context;
        }
        public Dialog create()
        {
            dialog = new Dialog(context, R.style.dialog_style);
            dialog.setContentView(R.layout.dialog_loading);
            content = dialog.findViewById(R.id.content);
            return dialog;
        }
        public Dialog create(int gravity)
        {
            dialog = new Dialog(context, R.style.dialog_style);
            dialog.getWindow().setGravity(gravity);
            dialog.setContentView(R.layout.dialog_loading);
            content = dialog.findViewById(R.id.content);
            return dialog;
        }
        public Dialog create(int gravity,int anim)
        {
            dialog = new Dialog(context, R.style.dialog_style);
            dialog.getWindow().setGravity(gravity);
            dialog.getWindow().setWindowAnimations(anim);
            dialog.setContentView(R.layout.dialog_loading);
            content = dialog.findViewById(R.id.content);
            return dialog;
        }
        public void show()
        {
            dialog.show();
        }
        public void cancel()
        {
            dialog.cancel();
        }
        public void setCancelable(boolean flag)
        {
            dialog.setCancelable(flag);
        }
    }
    public static class SubmitProgressBar
    {
        private Dialog dialog;
        private Context context;
        public ProgressBar progress;
        public TextView title,count,content;
        public SubmitProgressBar(Context context)
        {
            this.context = context;
        }
        public Dialog create()
        {
            dialog = new Dialog(context,R.style.dialog_style);
            dialog.setContentView(R.layout.dialog_submit_progress_bar);
            title = dialog.findViewById(R.id.title);
            progress = dialog.findViewById(R.id.progress);
            content = dialog.findViewById(R.id.content);
            count = dialog.findViewById(R.id.count);
            return dialog;
        }
        public Dialog create(int gravity)
        {
            dialog = new Dialog(context, R.style.dialog_style);
            dialog.getWindow().setGravity(gravity);
            dialog.setContentView(R.layout.dialog_submit_progress_bar);
            title = dialog.findViewById(R.id.title);
            progress = dialog.findViewById(R.id.progress);
            content = dialog.findViewById(R.id.content);
            count = dialog.findViewById(R.id.count);
            return dialog;
        }
        public Dialog create(int gravity,int anim)
        {
            dialog = new Dialog(context, R.style.dialog_style);
            dialog.getWindow().setGravity(gravity);
            dialog.getWindow().setWindowAnimations(anim);
            dialog.setContentView(R.layout.dialog_submit_progress_bar);
            title = dialog.findViewById(R.id.title);
            progress = dialog.findViewById(R.id.progress);
            content = dialog.findViewById(R.id.content);
            count = dialog.findViewById(R.id.count);
            return dialog;
        }
        public void show()
        {
            dialog.show();
        }
        public void cancel()
        {
            dialog.cancel();
        }
        public void setCancelable(boolean flag)
        {
            dialog.setCancelable(flag);
        }
    }
    public static class ListAdapter extends BaseAdapter
    {
        private List<String> list;
        private LayoutInflater inflater;
        public ListAdapter(Context context, List<String> list)
        {
            this.list = list;
            this.inflater = LayoutInflater.from(context);
        }
        public ListAdapter(Context context,String... value)
        {
            this.inflater = LayoutInflater.from(context);
            Collections.addAll((this.list = new ArrayList<>()),value);
        }
        @Override
        public int getCount()
        {
            return list.size();
        }
        @Override
        public Object getItem(int position)
        {
            return list.get(position);
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
            if (view == null)
            {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.dialog_item_text,null);
                holder.content = view.findViewById(R.id.content);
                if (position == 0)
                    holder.content.setBackgroundResource(R.drawable.bg_white_selector_top_corners_5);
                else if (position == list.size() -1)
                    holder.content.setBackgroundResource(R.drawable.bg_white_selector_bottom_corners_5);
                else
                    holder.content.setBackgroundResource(R.drawable.bg_white_selector);
                view.setTag(holder);
            }
            else holder = (ViewHolder) view.getTag();
            holder.content.setText(getItem(position).toString());
            return view;
        }
        private class ViewHolder
        {
            TextView content;
        }
    }
}
