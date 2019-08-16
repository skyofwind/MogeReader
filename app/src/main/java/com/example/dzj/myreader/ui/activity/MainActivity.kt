package com.example.dzj.myreader.ui.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message

import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.example.dzj.myreader.utils.PermissionUtil
import com.example.dzj.myreader.utils.SystemUtils
import com.example.dzj.myreader.R
import com.example.dzj.myreader.adpter.MyFragmentPagerAdapter
import com.example.dzj.myreader.ui.fragment.XiaoshuoManagerFragment
import com.example.dzj.myreader.ui.fragment.YuyinManagerFragment
import com.example.dzj.myreader.utils.ThreadUtil

import java.util.ArrayList

import com.example.dzj.myreader.R.id.myviewpager

class MainActivity : AppCompatActivity(), View.OnClickListener, ViewPager.OnPageChangeListener {

    private val TAG = this.javaClass.name
    private var viewpager: ViewPager? = null
    private var xiaoshuo: TextView? = null
    private var yuyin: TextView? = null
    private var cursor: ImageView? = null
    internal var cursorX = 0f
    private var widthArgs: IntArray? = null
    private var btnArgs: Array<TextView>? = null
    private var fragments: ArrayList<Fragment>? = null
    private var mTopBar: LinearLayout? = null
    private var topBar: LinearLayout? = null
    private var complete: TextView? = null
    private var chooseAll: TextView? = null
    private var number: TextView? = null
    private var chooseType = 0

    @SuppressLint("HandlerLeak")
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0x110 -> {
                }
            }
        }
    }

    private var lastValue = -1

    private var mPosition = 0

    val isBarGone: Boolean
        get() = if (mTopBar != null) {
            mTopBar!!.visibility == View.GONE
        } else true

    internal var completeListener: View.OnClickListener = View.OnClickListener { onComplete() }

    internal var choosAllListener: View.OnClickListener = View.OnClickListener {
        chooseType++
        if (chooseType % 2 == 1) {
            (fragments!![0] as XiaoshuoManagerFragment).resetIsChoose(true)
        } else {
            (fragments!![0] as XiaoshuoManagerFragment).resetIsChoose(false)
        }
        (fragments!![0] as XiaoshuoManagerFragment).updateAdapterView()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SystemUtils.getSystemDisplay(this)
        initView()
        permission()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mPosition == 0 && !isBarGone) {
                onComplete()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!ThreadUtil.isEmpty) {
            ThreadUtil.getInstance()!!.destory()
        }
    }

    private fun initView() {
        viewpager = findViewById<View>(myviewpager) as ViewPager
        xiaoshuo = findViewById<View>(R.id.btn_xiaoshuo) as TextView
        yuyin = findViewById<View>(R.id.btn_yuyin) as TextView

        topBar = findViewById<View>(R.id.bottomlinear) as LinearLayout
        mTopBar = findViewById<View>(R.id.mTopBar) as LinearLayout
        complete = findViewById<View>(R.id.complete) as TextView
        chooseAll = findViewById<View>(R.id.chooseAll) as TextView
        number = findViewById<View>(R.id.number) as TextView

        complete!!.setOnClickListener(completeListener)
        chooseAll!!.setOnClickListener(choosAllListener)

        btnArgs = arrayOf<TextView>(xiaoshuo!!, yuyin!!)
        cursor = findViewById<View>(R.id.cursor_btn) as ImageView
        cursor!!.setBackgroundColor(Color.RED)

        xiaoshuo!!.post {
            val lp = cursor!!.layoutParams as LinearLayout.LayoutParams
            lp.width = xiaoshuo!!.width - xiaoshuo!!.paddingLeft * 2
            cursor!!.layoutParams = lp
            cursor!!.x = xiaoshuo!!.paddingLeft.toFloat()
        }

        xiaoshuo!!.setOnClickListener(this)
        yuyin!!.setOnClickListener(this)
        viewpager!!.setOnPageChangeListener(this)

        fragments = ArrayList()
        fragments!!.add(XiaoshuoManagerFragment())
        fragments!!.add(YuyinManagerFragment())

        val adapter = MyFragmentPagerAdapter(supportFragmentManager, fragments)
        viewpager!!.adapter = adapter

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

        if (positionOffset != 0f) {
            val nowWidth = cursor!!.width
            if (lastValue >= positionOffsetPixels) {
                val offset = nowWidth * positionOffset - nowWidth
                cursorSlide(position + 1, offset)
            } else if (lastValue < positionOffsetPixels) {
                val offset = nowWidth * positionOffset
                cursorSlide(position, offset)
            }
        }
        lastValue = positionOffsetPixels
    }

    override fun onPageSelected(position: Int) {
        mPosition = position
        if (widthArgs == null) {
            widthArgs = intArrayOf(xiaoshuo!!.width, yuyin!!.width)
        }
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_xiaoshuo -> {
                viewpager!!.currentItem = 0
                cursorAnim(0)
            }
            R.id.btn_yuyin -> {
                viewpager!!.currentItem = 1
                cursorAnim(1)
            }
        }
    }

    fun cursorAnim(curItem: Int) {
        cursorX = 0f
        val lp = cursor!!.layoutParams as LinearLayout.LayoutParams
        lp.width = widthArgs!![curItem] - btnArgs!![0].paddingLeft * 2
        cursor!!.layoutParams = lp
        for (i in 0 until curItem) {
            cursorX = cursorX + btnArgs!![i].width
        }
        cursor!!.x = cursorX + btnArgs!![curItem].paddingLeft
    }

    fun cursorSlide(position: Int, offset: Float) {
        var mX = 0f
        for (i in 0 until position) {
            mX = mX + btnArgs!![i].width
        }
        if (offset > 0) {
            cursor!!.x = mX + (btnArgs!![position].paddingLeft * 3).toFloat() + offset
        } else {
            cursor!!.x = mX - btnArgs!![position].paddingLeft + offset
        }

        print("paddindleft=" + btnArgs!![position].paddingLeft)
    }

    private fun print(msg: String) {
        Log.i(TAG, msg)
    }

    private fun permission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            PermissionUtil.initPermission(this)
        }
        /*if (Build.VERSION.SDK_INT == 23) {
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
        }*/
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun topBarChange() {
        if (mTopBar != null && topBar != null) {
            if (mTopBar!!.visibility == View.GONE) {
                mTopBar!!.visibility = View.VISIBLE
                topBar!!.visibility = View.INVISIBLE
            } else {
                mTopBar!!.visibility = View.GONE
                topBar!!.visibility = View.VISIBLE
            }
        }
    }

    private fun onComplete() {
        chooseType = 0
        SystemUtils.gridClickType = false
        topBarChange()
        (fragments!![0] as XiaoshuoManagerFragment).changeBottomBar()
        (fragments!![0] as XiaoshuoManagerFragment).resetIsChoose(false)
        (fragments!![0] as XiaoshuoManagerFragment).updateAdapterView()
    }

    private fun log(tag: String, value: String) {
        Log.e(tag, value)
    }
}
