package com.example.dzj.myreader.activity;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dzj.myapplication.utils.SystemUtils;
import com.example.dzj.myreader.R;
import com.example.dzj.myreader.adpter.MyFragmentPagerAdapter;
import com.example.dzj.myreader.database.FictionDao;
import com.example.dzj.myreader.fragment.XiaoshuoManagerFragment;
import com.example.dzj.myreader.fragment.YuyinManagerFragment;
import com.example.dzj.myreader.utils.ThreadUtil;

import java.util.ArrayList;

import static com.example.dzj.myreader.R.id.myviewpager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private final String TAG=this.getClass().getName();
    private ViewPager viewpager;
    private TextView xiaoshuo,yuyin;
    private ImageView cursor;
    float cursorX=0;
    private int[] widthArgs;
    private TextView[] btnArgs;
    private ArrayList<android.support.v4.app.Fragment> fragments;
    private LinearLayout mTopBar, topBar;
    private TextView complete, chooseAll, number;
    private int chooseType = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SystemUtils.INSTANCE.getSystemDisplay(this);
        initView();
        permission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!ThreadUtil.isEmpty()){
            ThreadUtil.getInstance().destory();
        }
    }

    private void initView(){
        viewpager=(ViewPager)findViewById(myviewpager);
        xiaoshuo=(TextView)findViewById(R.id.btn_xiaoshuo);
        yuyin=(TextView)findViewById(R.id.btn_yuyin);

        topBar = (LinearLayout)findViewById(R.id.bottomlinear);
        mTopBar = (LinearLayout)findViewById(R.id.mTopBar);
        complete = (TextView)findViewById(R.id.complete);
        chooseAll = (TextView)findViewById(R.id.chooseAll);
        number = (TextView)findViewById(R.id.number);

        complete.setOnClickListener(completeListener);
        chooseAll.setOnClickListener(choosAllListener);

        btnArgs=new TextView[]{xiaoshuo,yuyin};
        cursor=(ImageView)findViewById(R.id.cursor_btn);
        cursor.setBackgroundColor(Color.RED);

        xiaoshuo.post(new Runnable() {
            @Override
            public void run() {
                LinearLayout.LayoutParams lp=(LinearLayout.LayoutParams)cursor.getLayoutParams();
                lp.width=xiaoshuo.getWidth()-xiaoshuo.getPaddingLeft()*2;
                cursor.setLayoutParams(lp);
                cursor.setX(xiaoshuo.getPaddingLeft());
            }
        });

        xiaoshuo.setOnClickListener(this);
        yuyin.setOnClickListener(this);
        viewpager.setOnPageChangeListener(this);

        fragments=new ArrayList<>();
        fragments.add(new XiaoshuoManagerFragment());
        fragments.add(new YuyinManagerFragment());

        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),fragments);
        viewpager.setAdapter(adapter);

    }
    private int lastValue = -1;
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        if(positionOffset!=0){
            int nowWidth=cursor.getWidth();
            if(lastValue>=positionOffsetPixels){
                float offset=nowWidth*positionOffset-nowWidth;
                cursorSlide(position+1,offset);
            }else if(lastValue<positionOffsetPixels){
                float offset=nowWidth*positionOffset;
                cursorSlide(position,offset);
            }
        }
        lastValue=positionOffsetPixels;
    }

    @Override
    public void onPageSelected(int position) {
        if(widthArgs==null){
            widthArgs=new int[]{xiaoshuo.getWidth(),yuyin.getWidth()};
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_xiaoshuo:
                viewpager.setCurrentItem(0);
                cursorAnim(0);
                break;
            case R.id.btn_yuyin:
                viewpager.setCurrentItem(1);
                cursorAnim(1);
                break;
        }
    }
    public void cursorAnim(int curItem){
        cursorX=0;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)cursor.getLayoutParams();
        lp.width=widthArgs[curItem]-btnArgs[0].getPaddingLeft()*2;
        cursor.setLayoutParams(lp);
        for(int i=0;i<curItem;i++){
            cursorX=cursorX+btnArgs[i].getWidth();
        }
        cursor.setX(cursorX+btnArgs[curItem].getPaddingLeft());
    }
    public void cursorSlide(int position,float offset){
        float mX=0;
        for(int i=0;i<position;i++){
            mX=mX+btnArgs[i].getWidth();
        }
        if(offset>0){
            cursor.setX(mX+btnArgs[position].getPaddingLeft()*3+offset);
        }else {
            cursor.setX(mX-btnArgs[position].getPaddingLeft()+offset);
        }

        print("paddindleft="+btnArgs[position].getPaddingLeft());
    }

    private void print(String msg){
        Log.i(TAG,msg);
    }
    private void permission() {

        if (Build.VERSION.SDK_INT == 23) {
            if (!(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {

                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        } else if (Build.VERSION.SDK_INT >= 24) {
            if (!(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {

                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            }
            if (!(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {

                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void topBarChange(){
        if(mTopBar != null && topBar != null){
            if(mTopBar.getVisibility() == View.GONE){
                mTopBar.setVisibility(View.VISIBLE);
                topBar.setVisibility(View.INVISIBLE);
            }else {
                mTopBar.setVisibility(View.GONE);
                topBar.setVisibility(View.VISIBLE);
            }
        }
    }

    public boolean isBarGone(){
        if(mTopBar != null){
            if(mTopBar.getVisibility() == View.GONE){
                return true;
            }else {
                return false;
            }
        }
        return false;
    }

    View.OnClickListener completeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            chooseType = 0;
            SystemUtils.INSTANCE.setGridClickType(false);
            topBarChange();
            ((XiaoshuoManagerFragment)fragments.get(0)).changeBottomBar();
            ((XiaoshuoManagerFragment)fragments.get(0)).resetIsChoose(false);
            ((XiaoshuoManagerFragment)fragments.get(0)).updateAdapterView();
        }
    };
    View.OnClickListener choosAllListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            chooseType++;
            if(chooseType % 2 == 1){
                ((XiaoshuoManagerFragment)fragments.get(0)).resetIsChoose(true);
            }else {
                ((XiaoshuoManagerFragment)fragments.get(0)).resetIsChoose(false);
            }
            ((XiaoshuoManagerFragment)fragments.get(0)).updateAdapterView();
        }
    };
}
