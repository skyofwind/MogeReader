package com.example.dzj.myreader.adpter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dzj.myreader.utils.SystemUtils;
import com.example.dzj.myreader.R;
import com.example.dzj.myreader.activity.FictionActivity;
import com.example.dzj.myreader.broadcastreceiver.FictionUpdateReceiver;
import com.example.dzj.myreader.database.FictionChapterDao;
import com.example.dzj.myreader.database.FictionDao;
import com.example.dzj.myreader.modle.TxtFile;
import com.example.dzj.myreader.utils.BookUtil;
import com.example.dzj.myreader.utils.ThreadUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dzj on 2017/8/19.
 */

public class XiaoshuoManagerAdapter extends BaseAdapter {
    public static final int TYPE_ONE = 0;
    public static final int TYPE_TWO = 1;
    private List<TxtFile> files;
    private Context context;
    private LayoutInflater inflater;
    private BookUtil bookUtil;
    private AddBookClickListener addBookClickListener;
    private List<Boolean> isChoose;

    public XiaoshuoManagerAdapter(Context context,List<TxtFile> files,int width){
        this.context=context;
        this.files=files;
        this.bookUtil=new BookUtil((width-80)/3);
        inflater=LayoutInflater.from(context);
        Log.d("files.size", files.size()+"");
        this.isChoose = new ArrayList<>(files.size());
        for(int i = 0; i < files.size(); i++){
            isChoose.add(false);
            Log.d("XiaoshuoManagerAdapter",files.get(i).getName()+" "+files.get(i).getId());
        }
    }
    @Override
    public int getCount() {
        return files.size()+1;
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
    public int getItemViewType(int position){
        if(position<files.size()){
            return TYPE_ONE;
        }else{
            return TYPE_TWO;
        }
    }
    @Override
    public int getViewTypeCount(){
        return 2;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        ViewHolder2 viewHolder2=null;
        View view = null;
        switch (getItemViewType(position)){
            case TYPE_ONE:
                if(convertView==null){
                    viewHolder=new ViewHolder();
                    view = inflater.inflate(R.layout.book_cover,null);
                    viewHolder.background = (LinearLayout)view.findViewById(R.id.book_cover);
                    viewHolder.title = (TextView)view.findViewById(R.id.book_title);
                    viewHolder.title2 = (TextView)view.findViewById(R.id.book_title2);
                    viewHolder.icon = (ImageView)view.findViewById(R.id.choose_type);
                    view.setTag(viewHolder);
                }else{
                    view = convertView;
                    viewHolder=(ViewHolder)view.getTag();
                }
                RelativeLayout.LayoutParams lp=(RelativeLayout.LayoutParams)viewHolder.background.getLayoutParams();
                lp.width=bookUtil.getWidth();
                lp.height=bookUtil.getHeight();
                viewHolder.background.setPadding(bookUtil.gethPadding(),bookUtil.getvPadding(),bookUtil.gethPadding(),bookUtil.getvPadding());
                String name = files.get(position).getName();
                name = name.substring(0, name.length()-4);
                viewHolder.title.setText(name);
                viewHolder.title2.setText(name);
                viewHolder.background.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(SystemUtils.INSTANCE.getGridClickType()){
                            isChoose.set(position, !isChoose.get(position));
                            updateView();
                        }else {
                            Intent intent = new Intent(context, FictionActivity.class);
                            intent.putExtra("Id", files.get(position).getId());
                            context.startActivity(intent);
                        }
                    }
                });
                viewHolder.background.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(!SystemUtils.INSTANCE.getGridClickType()){
                            SystemUtils.INSTANCE.setGridClickType(true);
                            isChoose.set(position, true);
                            Intent intent = new Intent();
                            intent.setAction(FictionUpdateReceiver.GRID_LONG_PRESS);
                            context.sendBroadcast(intent);
                            updateView();
                        }
                        return true;
                    }
                });
                if(SystemUtils.INSTANCE.getGridClickType()){
                    if(isChoose.get(position)){
                        viewHolder.icon.setImageDrawable(getDrawable(R.drawable.circle_choose));
                    }else {
                        viewHolder.icon.setImageDrawable(getDrawable(R.drawable.circle_bg));
                    }
                }else {
                    viewHolder.icon.setImageDrawable(null);
                }
                break;
            case TYPE_TWO:
                if(convertView==null){
                    viewHolder2=new ViewHolder2();
                    view = inflater.inflate(R.layout.addbook,null);
                    viewHolder2.background=(LinearLayout)view.findViewById(R.id.book_add);
                    viewHolder2.plus=(ImageView)view.findViewById(R.id.book_plus);
                    view.setTag(viewHolder2);
                }else{
                    view = convertView;
                    viewHolder2=(ViewHolder2)view.getTag();
                }
                RelativeLayout.LayoutParams lp2=(RelativeLayout.LayoutParams)viewHolder2.background.getLayoutParams();
                lp2.width=bookUtil.getWidth();
                lp2.height=bookUtil.getHeight();
                viewHolder2.background.setPadding(bookUtil.gethPadding(),bookUtil.getvPadding(),bookUtil.gethPadding(),bookUtil.getvPadding());
                viewHolder2.plus.setImageResource(R.drawable.plus);
                if(!viewHolder2.background.hasOnClickListeners()){
                    if(addBookClickListener!=null){
                        viewHolder2.background.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!SystemUtils.INSTANCE.getGridClickType()){
                                    addBookClickListener.onClick();
                                }
                            }
                        });
                    }
                }
                break;
        }

        return view;
    }
    static class ViewHolder{
        TextView title;
        TextView title2;
        LinearLayout background;
        ImageView icon;
    }
    static class ViewHolder2{
        LinearLayout background;
        ImageView plus;
    }
    public interface AddBookClickListener{
        void onClick();
    }
    public void setAddBookClickListener(AddBookClickListener listener){
        this.addBookClickListener=listener;
    }

    public void resetIsChoose(boolean a){
        for(int i = 0; i < isChoose.size(); i++){
            isChoose.set(i, a);
        }
    }

    public void updateView(){
        this.notifyDataSetChanged();
    }

    public void deleteFiction(){
        List<Integer> isDelete = new ArrayList<>();
        for(int i = 0; i < files.size(); i++){
            if(isChoose.get(i)){
                isDelete.add(i);
            }
        }
        for(int i = isDelete.size() - 1 ; i >= 0; i-- ){
            final int id = files.get(i).getId();
            ThreadUtil.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    FictionDao.getInstance(context).deleteFiction(id);
                    FictionChapterDao.getInstance(context).deleteChpaters(id);
                }
            });
            files.remove(i);
            isChoose.remove(i);
        }
        updateView();
    }

    private Drawable getDrawable(int id){
        if(Build.VERSION.SDK_INT >= 23){
            return context.getDrawable(id);
        }else {
            return context.getResources().getDrawable(id, null);
        }
    }
}
