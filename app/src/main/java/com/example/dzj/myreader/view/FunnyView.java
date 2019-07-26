package com.example.dzj.myreader.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.dzj.myreader.R;

import java.math.BigDecimal;

/**
 * Created by dzj on 2017/8/24.
 */

public class FunnyView extends View {

    private float mWidth;
    private float mHeight;
    private float circle_r;
    private float mcircle_r;
    private BigDecimal round = new BigDecimal(0).setScale(2, BigDecimal.ROUND_HALF_UP);
    private Paint mPaint = new Paint();
    private int vwidth = 0;
    private int bitmapWidth = 0,bitmapHeight = 0;
    private float arotate = 0;
    private float brotate = 0;
    private float trotate = 0;
    private int x1, y1, x2, y2;
    private boolean isBig = false;
    int textType = 0;

    public FunnyView(Context context){
        super(context);
        initPaint();
    }
    public FunnyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    private void initPaint(){
        mPaint.setColor(Color.BLACK);       //设置画笔颜色
        mPaint.setStyle(Paint.Style.STROKE);  //设置画笔模式为填充
        mPaint.setStrokeWidth(4f);         //设置画笔宽度为10px
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        circle_r = (float) (0.8*w/2);
        mcircle_r = 0;
        vwidth= (int) (circle_r/3);
        Bitmap funny = getBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.funny),vwidth);
        bitmapWidth = funny.getWidth();
        bitmapHeight = funny.getHeight();
        x1 = bitmapWidth/2;
        y1 = bitmapHeight/2;
        x2 = bitmapWidth/2;
        y2 = bitmapHeight/2;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawARGB(85,0,0,0);
        canvas.translate(mWidth/2,mHeight/2);
        canvas.save();
        drawText(canvas);
        canvas.rotate(trotate);

        drawTaiji(canvas,mPaint);

        if(isBig){
            //canvas.restore();
            Bitmap funny = getBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.funny),vwidth);
            if(y1 <= -circle_r/2 + bitmapHeight/2){
                canvas.save();
                canvas.rotate(arotate,0,circle_r/2);
            }
            canvas.drawBitmap(funny,-x1,-y1,mPaint);
            if(y2>=circle_r/2+bitmapHeight/2){
                canvas.restore();
                canvas.rotate(brotate,0,-circle_r/2);
            }
            canvas.drawBitmap(funny,-x2,-y2,mPaint);
        }

    }
    private void drawTaiji(Canvas canvas,Paint paint){
        float cr=round.floatValue();
        RectF rectFb = new RectF(-cr,-cr,cr,cr);
        canvas.drawArc(rectFb,90,-180,true,paint);
        paint.setColor(Color.WHITE);
        canvas.drawArc(rectFb,-90,-180,true,paint);

        RectF rectF = new RectF(-cr/2,-cr,cr/2,0);
        canvas.drawArc(rectF,0,360,true,paint);
        paint.setColor(Color.BLACK);
        RectF rectF2 = new RectF(-cr/2,0,cr/2,cr);
        canvas.drawArc(rectF2,0,360,true,paint);


    }
    private DrawThread drawThread;
    private void drawText(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);       //设置画笔颜色
        paint.setTextSize(60);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fontMetrics=paint.getFontMetrics();
        //float fontHeight=fontMetrics.descent- fontMetrics.ascent;

        float baseY = round.floatValue()  - fontMetrics.top;
        canvas.drawText("扫描中",0,baseY,paint);
    }
    public void start(){
        drawThread=new DrawThread();
        drawThread.setRunning(true);
        drawThread.start();
    }
    public void stop(){
        if(drawThread!=null){
            drawThread.setRunning(false);
            drawThread.interrupt();
        }
    }
    private Bitmap getBitmap(Bitmap bitmap, int newwidth){
        int height=bitmap.getHeight();
        int width=bitmap.getWidth();
        float scale = ((float)newwidth)/width;
        Matrix matrix = new Matrix();
        matrix.postScale(scale,scale);
        Bitmap nb=Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
        return nb;
    }

    private String getText(int type){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("加载中");
        for (int i = 0; i < type; i++){
            stringBuilder.append("。");
        }
        return stringBuilder.toString();
    }

    private class DrawThread extends Thread{
        // 用来停止线程的标记
        private boolean isRunning = false;

        public void setRunning(boolean running) {
            isRunning = running;
        }
        public boolean getRunning(){
            return this.isRunning;
        }
        @Override
        public void run(){
            while (isRunning){
                if(mcircle_r<circle_r&&!isBig){
                    mcircle_r+=circle_r/180;
                    round=new BigDecimal(mcircle_r).setScale(2, BigDecimal.ROUND_HALF_UP);
                    trotate+=2;
                    if(trotate>=360){
                        isBig=true;
                    }
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //Log.i("trotate","trotate="+trotate+" circle_r="+circle_r+" circle_r/180="+circle_r/180+" mcircle_r="+new BigDecimal(mcircle_r).setScale(2, BigDecimal.ROUND_HALF_UP));
                }else{

                    if(arotate>=360){
                        arotate=0;
                    }
                    if(brotate<=-360){
                        brotate=0;
                    }
                    if(trotate>=360){
                        trotate=0;
                    }
                    arotate+=10;
                    trotate+=1;
                    brotate-=10;
                    if(y1>-circle_r/2+bitmapHeight/2){
                        y1-=5;
                    }
                    if(y2<circle_r/2+bitmapHeight/2){
                        y2+=5;
                    }
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                textType++;
                if(textType > 3){
                    textType = 0;
                }
                postInvalidate();
            }
        }
    }
}
