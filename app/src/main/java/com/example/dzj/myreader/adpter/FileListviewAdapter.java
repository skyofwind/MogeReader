package com.example.dzj.myreader.adpter;

import android.content.Context;

import androidx.appcompat.widget.AppCompatCheckBox;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dzj.myreader.R;
import com.example.dzj.myreader.ui.activity.AddXiaoshuoActivity;
import com.example.dzj.myreader.modle.TxtFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dzj on 2017/8/21.
 */

public class FileListviewAdapter extends BaseAdapter{
    public static final int TYPE_ONE = 0;
    public static final int TYPE_TWO = 1;
    private List<TxtFile> fileDatases;
    private LayoutInflater inflater;
    private FileItemClickListener fileItemClickListener;
    private Context context;
    private SparseArray<Boolean> checks;
    private List<Integer> cPositions;
    private int checkNum = 0;

    public FileListviewAdapter(Context context,List<TxtFile> fileDatases){
        inflater=LayoutInflater.from(context);
        this.fileDatases=fileDatases;
        this.context=context;
        checks = new SparseArray<>();
        cPositions = new ArrayList<>();
        for(int i = 0; i < fileDatases.size(); i++){
            TxtFile fileDatas = fileDatases.get(i);
            if(!fileDatas.isDirectory()){
                checks.append(i, false);
                cPositions.add(i);
            }
        }
        ((AddXiaoshuoActivity)context).setPutFile(false);
        ((AddXiaoshuoActivity)context).chooseView(false);
        if(checks.size() > 0){
            ((AddXiaoshuoActivity)context).setPutFile(true);
            ((AddXiaoshuoActivity)context).chooseView(true);
        }
        ((AddXiaoshuoActivity)context).setCouldPutBook(1,false);
       Log.i("page","选择页面准备就绪");
    }

    @Override
    public int getCount() {
        return fileDatases.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //final int index=position;
        final int mPosition=position;
        FolderItemHolder folder_holder = null;
        FileItemHolder file_holder = null;
        View view = null;
        //Log.i("mposition",position+"");
        switch (getItemViewType(position)) {
            case TYPE_ONE:
                if(convertView==null){
                    folder_holder=new FolderItemHolder();
                    view = inflater.inflate(R.layout.fileitem,null);
                    folder_holder.name=(TextView)view.findViewById(R.id.fileitem_name);
                    folder_holder.content=(TextView)view.findViewById(R.id.fileitem_content);
                    folder_holder.background=(LinearLayout)view.findViewById(R.id.item_background);
                    view.setTag(folder_holder);
                }else{
                    view = convertView;
                    folder_holder=(FolderItemHolder)view.getTag();
                }
                folder_holder.name.setText(fileDatases.get(position).getName());
                folder_holder.content.setText(fileDatases.get(position).getSize());
                if(fileItemClickListener!=null){
                    folder_holder.background.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("myd", mPosition+"");
                            fileItemClickListener.onClick(mPosition);
                        }
                    });
                }

                break;
            case TYPE_TWO:
                if(convertView==null){
                    file_holder=new FileItemHolder();
                    view = inflater.inflate(R.layout.fileitem_choose,null);
                    file_holder.name=(TextView)view.findViewById(R.id.file_name);
                    file_holder.content=(TextView)view.findViewById(R.id.file_size);
                    file_holder.checkBox=(AppCompatCheckBox)view.findViewById(R.id.checkbox);
                    file_holder.background=(LinearLayout)view.findViewById(R.id.item_background);
                    view.setTag(file_holder);
                }else{
                    view = convertView;
                    file_holder=(FileItemHolder)view.getTag();
                }
                file_holder.name.setText(fileDatases.get(position).getName());
                file_holder.content.setText(fileDatases.get(position).getSize());
                file_holder.checkBox.setChecked(checks.get(position));
                file_holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        checks.setValueAt(mPosition, isChecked);
                        if(isChecked){
                            checkNum++;
                        }else {
                            checkNum--;
                        }
                        if(checkNum > 0){
                            ((AddXiaoshuoActivity)context).setCouldPutBook(1,true);
                        }else {
                            ((AddXiaoshuoActivity)context).setCouldPutBook(1,false);
                        }
                    }
                });

                break;
        }
        return view;
    }
    @Override
    public int getItemViewType(int position){

        if(fileDatases.get(position).isDirectory()){
            return TYPE_ONE;
        }else {
            return TYPE_TWO;
        }
    }
    @Override
    public int getViewTypeCount(){
        return 2;
    }


    static class FileItemHolder{
        TextView name;
        TextView content;
        AppCompatCheckBox checkBox;
        LinearLayout background;
    }
    static class FolderItemHolder{
        TextView name;
        TextView content;
        LinearLayout background;
    }
    public interface FileItemClickListener{
        void onClick(int position);
    }
    public void setFileItemClickListener(FileItemClickListener fileItemClickListener){
        this.fileItemClickListener=fileItemClickListener;
    }

    public void chooseAll(){
        for(int i = 0; i < cPositions.size(); i++){
            Boolean b = checks.get(cPositions.get(i));
            checks.setValueAt(cPositions.get(i), !b);
        }
        notifyDataSetChanged();
    }
    public List<TxtFile> getAdd(){
        List<TxtFile> files = new ArrayList<>();
        for(int i = 0; i < cPositions.size(); i++){
            Boolean b = checks.get(cPositions.get(i));
            if(b){
                files.add(fileDatases.get(cPositions.get(i)));
            }
        }
        return files;
    }
}
