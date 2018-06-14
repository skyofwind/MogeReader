package com.example.dzj.myapplication.utils

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import java.text.SimpleDateFormat
import java.util.*

object SystemUtils {
    var HEIGHT = 0//屏幕除去状态栏和标题栏外的高度
    var HEIGHT_MINUS_STATUS_BAR = 0//减去状态栏的高度
    var STATUS_BAR_HEIGHT = 0//状态栏高度
    var MAX_WIDTH = 0//屏幕最大宽度
    var MAX_HEIGHT = 0//屏幕最大高度

    var gridClickType = false//控制gridItem点击事件
    /**
     * 获取屏幕的宽和高
     * @param context
     * 参数为上下文对象Context
     * @return
     * 返回值为长度为2int型数组,其中
     * int[0] -- 表示屏幕的宽度
     * int[1] -- 表示屏幕的高度
     */
    fun getSystemDisplay(context: Context) {
        //创建保存屏幕信息类
        val dm = DisplayMetrics()
        //获取窗口管理类
        val wm = context.getSystemService(
                Context.WINDOW_SERVICE) as WindowManager
        //获取屏幕信息并保存到DisplayMetrics中
        wm.defaultDisplay.getMetrics(dm)
        //声明数组保存信息
        //int[] displays = new int[2];
        MAX_WIDTH = dm.widthPixels//屏幕宽度(单位:px)
        MAX_HEIGHT = dm.heightPixels//屏幕高度

        var statusBarHeight1 = -1
        //获取status_bar_height资源的ID
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = context.resources.getDimensionPixelSize(resourceId)
            STATUS_BAR_HEIGHT = statusBarHeight1
        }
        // 获取标题栏和状态栏高度
        //标题栏默认高度为56dp
        MAX_HEIGHT = dm.heightPixels
        HEIGHT_MINUS_STATUS_BAR = MAX_HEIGHT - STATUS_BAR_HEIGHT
        HEIGHT = HEIGHT_MINUS_STATUS_BAR - dip2px(context, 56f)

        Log.d("HEIGHT", "" + HEIGHT)
        Log.d("STATUS_BAR_HEIGHT", "" + STATUS_BAR_HEIGHT)
        Log.d("MAX_HEIGHT", "" + MAX_HEIGHT)
        Log.d("MAX_WIDTH", "" + MAX_WIDTH)
    }

    //dp转px
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    //px转dp
    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    fun getTime() : String{
        val calendar = Calendar.getInstance()
        val time = calendar.time
        val simpleDateFormat = SimpleDateFormat("HH:mm")
        return simpleDateFormat.format(time)
    }

    fun getColor(context: Context, id : Int) : Int{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(id);
        }else{
            return context.resources.getColor(id);
        }
    }

}
