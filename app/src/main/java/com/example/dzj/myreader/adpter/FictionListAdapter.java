package com.example.dzj.myreader.adpter;

import android.content.Context;
import androidx.appcompat.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dzj.myreader.R;
import com.example.dzj.myreader.activity.AddXiaoshuoActivity;
import com.example.dzj.myreader.modle.TxtFile;

import java.util.ArrayList;
import java.util.List;

public class FictionListAdapter extends BaseExpandableListAdapter {
    private List<String> titles;
    private List<List<TxtFile>> files;
    private List<List<Boolean>> checks;
    private Context context;
    private int checkNum = 0;

    public FictionListAdapter(Context context, List<String> titles, List<List<TxtFile>> files){
        this.titles = titles;
        this.files = files;
        this.context = context;

        checks = new ArrayList<>();
        for(int i = 0; i < files.size(); i++){
            List<TxtFile> file = files.get(i);
            List<Boolean> check = new ArrayList<>();
            for(int j = 0; j < file.size(); j++){
                check.add(false);
            }
            checks.add(check);
        }
        if(files.size() > 0){
            ((AddXiaoshuoActivity)context).setPutScan(true);
            ((AddXiaoshuoActivity)context).chooseView(true);
        }
        Log.i("page","扫描页面就绪");
    }

    //获取组元素数目
    @Override
    public int getGroupCount() {
        return titles.size();
    }

    //获取子元素数目
    @Override
    public int getChildrenCount(int groupPosition) {
        return files.get(groupPosition).size();
    }

    //获取组元素对象
    @Override
    public Object getGroup(int groupPosition) {
        return titles.get(groupPosition);
    }

    //获取子元素对象
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return files.get(groupPosition).get(childPosition);
    }

    //获取组元素id
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    //获取子元素id
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    @Override
    public boolean hasStableIds() {
        return true;
    }

    //加载组元素并显示
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = null;
        GroupHolder groupHolder = null;
        if (convertView != null){
            view = convertView;
            groupHolder = (GroupHolder)view.getTag();
        }else {
            view = View.inflate(context, R.layout.group_view_item, null);
            groupHolder = new GroupHolder();
            groupHolder.group_title = (TextView) view.findViewById(R.id.group_view_title);
            view.setTag(groupHolder);
        }
        groupHolder.group_title.setText(titles.get(groupPosition));
        return view;
    }

    //加载子元素并显示
    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view = null;
        ChildHodler childHodler = null;
        if(convertView == null){
            view = View.inflate(context, R.layout.fileitem_choose, null);
            childHodler = new ChildHodler();
            childHodler.file_name = (TextView) view.findViewById(R.id.file_name);
            childHodler.file_size = (TextView) view.findViewById(R.id.file_size);
            childHodler.checkBox = (AppCompatCheckBox) view.findViewById(R.id.checkbox);
            childHodler.item = (LinearLayout) view.findViewById(R.id.item_background);
            view.setTag(childHodler);
        }else {
            view = convertView;
            childHodler = (ChildHodler) view.getTag();
        }
        childHodler.file_name.setText(files.get(groupPosition).get(childPosition).getName());
        childHodler.file_size.setText(files.get(groupPosition).get(childPosition).getSize());
        childHodler.checkBox.setChecked(checks.get(groupPosition).get(childPosition));

        childHodler.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checks.get(groupPosition).set(childPosition, isChecked);
                if(isChecked){
                    checkNum++;
                }else {
                    checkNum--;
                }
                if(checkNum > 0){
                    ((AddXiaoshuoActivity)context).setCouldPutBook(0,true);
                }else {
                    ((AddXiaoshuoActivity)context).setCouldPutBook(0,false);
                }
            }
        });
        final ChildHodler finalChildHodler = childHodler;
        childHodler.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalChildHodler.checkBox.callOnClick();
            }
        });
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    static class GroupHolder{
        TextView group_title;
    }

    static class ChildHodler{
        TextView file_name;
        TextView file_size;
        AppCompatCheckBox checkBox;
        LinearLayout item;
    }
    private void log(String s){
        Log.d("fictionAdapter", s);
    }
    public void chooseAll(){
        for(int i = 0; i < checks.size(); i++){
            List<Boolean> check = checks.get(i);
            for(int j = 0; j < check.size(); j++){
                check.set(j, !check.get(j));
            }
        }
        notifyDataSetChanged();
    }
    public List<TxtFile> getAdd(){
        List<TxtFile> txtFiles = new ArrayList<>();
        for(int i = 0; i < checks.size(); i++){
            List<Boolean> booleans = checks.get(i);
            for(int j = 0; j < booleans.size(); j++){
                if (booleans.get(j)){
                    txtFiles.add(files.get(i).get(j));
                }
            }
        }
        return txtFiles;
    }
}
