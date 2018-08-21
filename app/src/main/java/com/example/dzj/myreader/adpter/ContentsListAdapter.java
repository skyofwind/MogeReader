package com.example.dzj.myreader.adpter;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.dzj.myreader.R;
import com.example.dzj.myreader.modle.LineData;
import com.example.dzj.myreader.utils.ParseTxt;

import java.io.IOException;
import java.util.List;

public class ContentsListAdapter extends BaseAdapter {

    private static final String TAG = "ContentsListAdapter";
    private Context context;
    private List<LineData> lineDatas;
    private int sequence;


    public ContentsListAdapter(Context context, final List<LineData> lineDatas, int sequence){
        this.context = context;
        this.lineDatas = lineDatas;
        this.sequence = sequence;
    }

    @Override
    public int getCount() {
        return lineDatas.size()-1;
    }

    @Override
    public Object getItem(int position) {
        if (sequence == 0){
            return lineDatas.get(position);
        }else {
            return lineDatas.get(getCount()-position-1);
        }
    }

    @Override
    public long getItemId(int position) {
        if (sequence == 0){
            return position;
        }else {
            return getCount()-position-1;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.contents_item_layout, null);
            viewHolder.title = (TextView)convertView.findViewById(R.id.title);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        int target = position;
        int f = 0;
        if(sequence == 1){
            target = getCount()-position-1;
            f = getCount()-1;
        }

        if(target == 0 && lineDatas.get(target).getChapterTitle() == null){
            viewHolder.title.setText("前言");
        }else {
            viewHolder.title.setText(lineDatas.get(target).getChapterTitle());
        }

        if(lineDatas.get(target).getIsRead() == 1){
            viewHolder.title.setTextColor(getColor(R.color.gray));
        }else {
            viewHolder.title.setTextColor(getColor(R.color.black));
        }

        return convertView;
    }

    class ViewHolder{
        TextView title;
    }

    private void log(String s){
        Log.d(TAG, s);
    }

    private int getColor(int id){
        if (Build.VERSION.SDK_INT >= 23){
            return context.getColor(id);
        }else {
            return context.getResources().getColor(id);
        }
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}
