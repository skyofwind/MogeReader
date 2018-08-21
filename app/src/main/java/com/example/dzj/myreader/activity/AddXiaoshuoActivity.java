package com.example.dzj.myreader.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dzj.myreader.R;
import com.example.dzj.myreader.adpter.MyFragmentPagerAdapter;
import com.example.dzj.myreader.broadcastreceiver.FictionUpdateReceiver;
import com.example.dzj.myreader.database.FictionDao;
import com.example.dzj.myreader.fragment.FileManagerFragment;
import com.example.dzj.myreader.fragment.SacnManagerFragment;
import com.example.dzj.myreader.modle.TxtFile;
import com.example.dzj.myreader.utils.ExecutorsUtil;
import com.example.dzj.myreader.utils.ThreadUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dzj on 2017/8/21.
 */

public class AddXiaoshuoActivity extends BaseActivty implements View.OnClickListener, ViewPager.OnPageChangeListener{

    private ViewPager viewpager;

    private TextView scan, mchoose;

    private ImageView cursor;

    float cursorX = 0;

    private int[] widthArgs;

    private TextView[] btnArgs;

    private ArrayList<Fragment> fragments;

    private FragmentBackListener backListener;

    private boolean isInterception = false;

    private TextView chooseAll, addBook;

    private Boolean isPutScan = false, isPutFile = false, isClick = false;

    private Boolean isAddScan = false, isAddFile = false;

    private MyFragmentPagerAdapter adapter;

    @SuppressLint("HandlerLeak")
    private final Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x10:
                    statrProgressDialog("");
                    break;
                case 0x11:
                    cancel();
                    break;
                case 0x12:
                    Toast.makeText(AddXiaoshuoActivity.this, "书籍添加成功", Toast.LENGTH_LONG).show();
                    break;
                default:

                    break;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_xiaoshuo);

        initToolbar("导入本地书籍");
        initView();
    }
    private void initToolbar(String title){

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Toolbar toolbar =  null;
            toolbar = (Toolbar)findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            toolbar.setNavigationIcon(R.drawable.back);
            actionBar.setTitle(title);
        }
    }
    private void initView(){
        viewpager=(ViewPager)findViewById(R.id.viewpager_choose_xiaoshuo);
        scan=(TextView)findViewById(R.id.automatic_scan);
        mchoose=(TextView)findViewById(R.id.manual_choose);

        chooseAll = (TextView)findViewById(R.id.choose_all);
        addBook = (TextView)findViewById(R.id.add_book);
        btnArgs=new TextView[]{scan,mchoose};
        cursor=(ImageView)findViewById(R.id.cursor_btn);
        cursor.setBackgroundColor(Color.RED);

        scan.post(new Runnable() {
            @Override
            public void run() {
                LinearLayout.LayoutParams lp=(LinearLayout.LayoutParams)cursor.getLayoutParams();
                lp.width=scan.getWidth()-scan.getPaddingLeft()*2;
                cursor.setLayoutParams(lp);
                cursor.setX(scan.getPaddingLeft());
            }
        });

        scan.setOnClickListener(this);
        mchoose.setOnClickListener(this);
        viewpager.setOnPageChangeListener(this);

        fragments=new ArrayList<>();
        fragments.add(new SacnManagerFragment());
        fragments.add(new FileManagerFragment());

        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),fragments);
        viewpager.setAdapter(adapter);

        chooseAll.setOnClickListener(chooseAllListener);
        addBook.setOnClickListener(putBookListener);

        chooseView(false);
        setAddBookView(false);
    }
    private int lastValue = -1;
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        if(positionOffset != 0){
            int nowWidth = cursor.getWidth();
            if(lastValue >= positionOffsetPixels){
                float offset = nowWidth*positionOffset-nowWidth;
                cursorSlide(position+1,offset);
            }else if(lastValue < positionOffsetPixels){
                float offset = nowWidth*positionOffset;
                cursorSlide(position,offset);
            }
        }
        lastValue = positionOffsetPixels;
    }

    @Override
    public void onPageSelected(int position) {
        if(widthArgs == null){
            widthArgs=new int[]{scan.getWidth(),mchoose.getWidth()};
        }
        Log.d("page",position+"");
        if (position == 0){
            chooseView(isPutScan);
            setAddBookView(isAddScan);
        }else {
            chooseView(isPutFile);
            setAddBookView(isAddFile);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.automatic_scan:
                viewpager.setCurrentItem(0);
                cursorAnim(0);
                break;
            case R.id.manual_choose:
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
        for(int i = 0; i < curItem; i++){
            cursorX = cursorX+btnArgs[i].getWidth();
        }
        cursor.setX(cursorX+btnArgs[curItem].getPaddingLeft());
    }
    public void cursorSlide(int position,float offset){
        float mX = 0;
        for(int i = 0; i < position; i++){
            mX = mX+btnArgs[i].getWidth();
        }
        if(offset > 0){
            cursor.setX(mX+btnArgs[position].getPaddingLeft()*3+offset);
        }else {
            cursor.setX(mX-btnArgs[position].getPaddingLeft()+offset);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_seach:

                break;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        if(isInterception()){
            if(backListener != null){
                backListener.onbackForword();
            }
        }
    }
    public void setBackListener(FragmentBackListener backListener) {
        this.backListener = backListener;
    }
    public FragmentBackListener getBackListener() {
        return this.backListener;
    }
    public boolean isInterception() {
        return this.isInterception;
    }
    public void setInterception(boolean interception){
        this.isInterception = interception;
    }
    public interface FragmentBackListener{
        void onbackForword();
    }
    @Override
    protected void onPause(){
        super.onPause();
    }
    @Override
    protected void onResume(){
        super.onResume();
    }
    @Override
    protected void onStart(){
        super.onStart();
    }

    public void setPutScan(Boolean putScan) {
        isPutScan = putScan;
    }

    public void setPutFile(Boolean putFile) {
        isPutFile = putFile;
    }

    public void setAddScan(Boolean addScan) {
        isAddScan = addScan;
    }

    public void setAddFile(Boolean addFile) {
        isAddFile = addFile;
    }

    public void chooseView(Boolean b){
        if(b){
            chooseAll.setVisibility(View.VISIBLE);
            chooseAll.setClickable(true);
        }else {
            chooseAll.setVisibility(View.GONE);
            chooseAll.setClickable(false);
        }
    }
    View.OnClickListener chooseAllListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Fragment fragment = (Fragment) adapter.instantiateItem(viewpager, viewpager.getCurrentItem());
            if(fragment == fragments.get(0)){
                ((SacnManagerFragment)fragment).chooseAll();
            }else {
                ((FileManagerFragment)fragment).chooseAll();
            }
        }
    };

    View.OnClickListener putBookListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final Fragment fragment = (Fragment) adapter.instantiateItem(viewpager, viewpager.getCurrentItem());
            if(fragment == fragments.get(0)){
                if(isAddScan){
                    Log.d("sss","扫描");
                    mHandler.sendEmptyMessage(0x10);
                    ThreadUtil.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            List<TxtFile> files = ((SacnManagerFragment)fragment).getAdd();
                            int num = FictionDao.getInstance(AddXiaoshuoActivity.this).insertFictions(files);
                            if(num > 0){
                                mHandler.sendEmptyMessage(0x12);
                            }
                            mHandler.sendEmptyMessage(0x11);
                            sendUpdateBroadcast();
                        }
                    });
                }
            }else {
                if (isAddFile){
                    Log.d("sss","选择");
                    mHandler.sendEmptyMessage(0x10);
                    ThreadUtil.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            List<TxtFile> files = ((FileManagerFragment)fragment).getAdd();
                            int num = FictionDao.getInstance(AddXiaoshuoActivity.this).insertFictions(files);
                            if(num > 0){
                                mHandler.sendEmptyMessage(0x12);
                            }
                            mHandler.sendEmptyMessage(0x11);
                            sendUpdateBroadcast();
                        }
                    });
                }
            }

        }
    };
    private void sendUpdateBroadcast(){
        Intent intent = new Intent();
        intent.setAction(FictionUpdateReceiver.FICTION_UPDATE);
        sendBroadcast(intent);
    }
    public void setCouldPutBook(int positon, Boolean b){
        if(positon == 0){
            isAddScan = b;
        }else {
            isAddFile = b;
        }
        addBookView();
    }
    private void addBookView(){
        Fragment fragment = (Fragment) adapter.instantiateItem(viewpager, viewpager.getCurrentItem());
        if(fragment == fragments.get(0)){
            setAddBookView(isAddScan);
        }else {
            setAddBookView(isAddFile);
        }
    }
    private void setAddBookView(Boolean b){
        if (b){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                addBook.setTextColor(getColor(R.color.nomal_text));
            }else {
                addBook.setTextColor(getResources().getColor(R.color.nomal_text));
            }
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                addBook.setTextColor(getColor(R.color.swhite));
            }else {
                addBook.setTextColor(getResources().getColor(R.color.swhite));
            }
        }
    }
}
