package com.example.dzj.myreader.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.example.dzj.myreader.R
import com.example.dzj.myreader.adpter.MyFragmentPagerAdapter
import com.example.dzj.myreader.broadcastreceiver.FictionUpdateReceiver
import com.example.dzj.myreader.database.FictionDao
import com.example.dzj.myreader.ui.fragment.FileManagerFragment
import com.example.dzj.myreader.ui.fragment.SacnManagerFragment
import com.example.dzj.myreader.modle.TxtFile
import com.example.dzj.myreader.utils.ThreadUtil

import java.util.ArrayList

/**
 * Created by dzj on 2017/8/21.
 */

class AddXiaoshuoActivity : BaseActivty(), View.OnClickListener, ViewPager.OnPageChangeListener {

    private var viewpager: ViewPager? = null

    private var scan: TextView? = null
    private var mchoose: TextView? = null

    private var cursor: ImageView? = null

    internal var cursorX = 0f

    private var widthArgs: IntArray? = null

    private var btnArgs: Array<TextView>? = null

    private var fragments: ArrayList<Fragment>? = null

    var backListener: FragmentBackListener? = null

    var isInterception = false

    private var chooseAll: TextView? = null
    private var addBook: TextView? = null

    private var isPutScan: Boolean? = false
    private var isPutFile: Boolean? = false
    private val isClick = false

    private var isAddScan: Boolean? = false
    private var isAddFile: Boolean? = false

    private var adapter: MyFragmentPagerAdapter? = null

    @SuppressLint("HandlerLeak")
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0x10 -> statrProgressDialog("")
                0x11 -> cancel()
                0x12 -> Toast.makeText(this@AddXiaoshuoActivity, "书籍添加成功", Toast.LENGTH_LONG).show()
                else -> {
                }
            }
        }
    }
    private var lastValue = -1
    internal var chooseAllListener: View.OnClickListener = View.OnClickListener {
        val fragment = adapter!!.instantiateItem(viewpager!!, viewpager!!.currentItem) as Fragment
        if (fragment === fragments!![0]) {
            (fragment as SacnManagerFragment).chooseAll()
        } else {
            (fragment as FileManagerFragment).chooseAll()
        }
    }

    internal var putBookListener: View.OnClickListener = View.OnClickListener {
        val fragment = adapter!!.instantiateItem(viewpager!!, viewpager!!.currentItem) as Fragment
        if (fragment === fragments!![0]) {
            if (isAddScan!!) {
                Log.d("sss", "扫描")
                mHandler.sendEmptyMessage(0x10)
                ThreadUtil.getInstance()!!.execute(Runnable {
                    val files = (fragment as SacnManagerFragment).add
                    val num = FictionDao.getInstance(this@AddXiaoshuoActivity).insertFictions(files)
                    if (num > 0) {
                        mHandler.sendEmptyMessage(0x12)
                    }
                    mHandler.sendEmptyMessage(0x11)
                    sendUpdateBroadcast()
                })
            }
        } else {
            if (isAddFile!!) {
                Log.d("sss", "选择")
                mHandler.sendEmptyMessage(0x10)
                ThreadUtil.getInstance()!!.execute(Runnable {
                    val files = (fragment as FileManagerFragment).add
                    val num = FictionDao.getInstance(this@AddXiaoshuoActivity).insertFictions(files)
                    if (num > 0) {
                        mHandler.sendEmptyMessage(0x12)
                    }
                    mHandler.sendEmptyMessage(0x11)
                    sendUpdateBroadcast()
                })
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_fiction)

        initToolbar("导入本地书籍")
        initView()
    }

    private fun initToolbar(title: String) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            var toolbar: Toolbar? = null
            toolbar = findViewById<View>(R.id.toolbar) as Toolbar
            setSupportActionBar(toolbar)
            val actionBar = supportActionBar
            toolbar.setNavigationIcon(R.drawable.back)
            actionBar!!.title = title
        }
    }

    private fun initView() {
        viewpager = findViewById<View>(R.id.viewpager_choose_xiaoshuo) as ViewPager
        scan = findViewById<View>(R.id.automatic_scan) as TextView
        mchoose = findViewById<View>(R.id.manual_choose) as TextView

        chooseAll = findViewById<View>(R.id.choose_all) as TextView
        addBook = findViewById<View>(R.id.add_book) as TextView
        btnArgs = arrayOf<TextView>(scan!!, mchoose!!)
        cursor = findViewById<View>(R.id.cursor_btn) as ImageView
        cursor!!.setBackgroundColor(Color.RED)

        scan!!.post {
            val lp = cursor!!.layoutParams as LinearLayout.LayoutParams
            lp.width = scan!!.width - scan!!.paddingLeft * 2
            cursor!!.layoutParams = lp
            cursor!!.x = scan!!.paddingLeft.toFloat()
        }

        scan!!.setOnClickListener(this)
        mchoose!!.setOnClickListener(this)
        viewpager!!.setOnPageChangeListener(this)

        fragments = ArrayList()
        fragments!!.add(SacnManagerFragment())
        fragments!!.add(FileManagerFragment())

        adapter = MyFragmentPagerAdapter(supportFragmentManager, fragments)
        viewpager!!.adapter = adapter

        chooseAll!!.setOnClickListener(chooseAllListener)
        addBook!!.setOnClickListener(putBookListener)

        chooseView(false)
        setAddBookView(false)
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
        if (widthArgs == null) {
            widthArgs = intArrayOf(scan!!.width, mchoose!!.width)
        }
        Log.d("page", position.toString() + "")
        if (position == 0) {
            chooseView(isPutScan)
            setAddBookView(isAddScan!!)
        } else {
            chooseView(isPutFile)
            setAddBookView(isAddFile!!)
        }
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.automatic_scan -> {
                viewpager!!.currentItem = 0
                cursorAnim(0)
            }
            R.id.manual_choose -> {
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
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        when (id) {
            android.R.id.home -> finish()
            R.id.action_seach -> {
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (isInterception) {
            if (backListener != null) {
                backListener!!.onbackForword()
            }
        }
    }

    interface FragmentBackListener {
        fun onbackForword()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
    }

    fun setPutScan(putScan: Boolean?) {
        isPutScan = putScan
    }

    fun setPutFile(putFile: Boolean?) {
        isPutFile = putFile
    }

    fun setAddScan(addScan: Boolean?) {
        isAddScan = addScan
    }

    fun setAddFile(addFile: Boolean?) {
        isAddFile = addFile
    }

    fun chooseView(b: Boolean?) {
        if (b!!) {
            chooseAll!!.visibility = View.VISIBLE
            chooseAll!!.isClickable = true
        } else {
            chooseAll!!.visibility = View.GONE
            chooseAll!!.isClickable = false
        }
    }

    private fun sendUpdateBroadcast() {
        val intent = Intent()
        intent.action = FictionUpdateReceiver.FICTION_UPDATE
        sendBroadcast(intent)
    }

    fun setCouldPutBook(positon: Int, b: Boolean?) {
        if (positon == 0) {
            isAddScan = b
        } else {
            isAddFile = b
        }
        addBookView()
    }

    private fun addBookView() {
        val fragment = adapter!!.instantiateItem(viewpager!!, viewpager!!.currentItem) as Fragment
        if (fragment === fragments!![0]) {
            setAddBookView(isAddScan!!)
        } else {
            setAddBookView(isAddFile!!)
        }
    }

    private fun setAddBookView(b: Boolean) {
        if (b) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                addBook!!.setTextColor(getColor(R.color.nomal_text))
            } else {
                addBook!!.setTextColor(resources.getColor(R.color.nomal_text))
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                addBook!!.setTextColor(getColor(R.color.swhite))
            } else {
                addBook!!.setTextColor(resources.getColor(R.color.swhite))
            }
        }
    }
}
