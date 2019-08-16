package com.example.dzj.myreader.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.*
import android.util.Log
import android.view.*
import android.widget.*
import com.example.dzj.myreader.utils.SystemUtils
import com.example.dzj.myreader.R
import com.example.dzj.myreader.adpter.ContentsListAdapter
import com.example.dzj.myreader.database.FictionChapterDao
import com.example.dzj.myreader.database.FictionDao
import com.example.dzj.myreader.modle.Chapter
import com.example.dzj.myreader.modle.Fiction
import com.example.dzj.myreader.modle.LineData
import com.example.dzj.myreader.modle.TxtFile
import com.example.dzj.myreader.utils.ParseTxt
import com.example.dzj.myreader.utils.TextUtil
import com.example.dzj.myreader.utils.ThreadUtil
import kotlinx.android.synthetic.main.fiction_layout.*

class FictionActivity : BaseActivty() {
    private val TAG = "FictionActivity "

    private var batterry: BatteryChangedReceiver? = null
    private var timeBroadcastReceiver: TimeBroadcastReceiver? = null

    private var file: TxtFile? = null
    private var lineDatas: List<LineData>? = null
    private var contentsWindow: PopupWindow? = null
    private var contentsAdapter: ContentsListAdapter? = null;
    private var contentsList: ListView? = null
    private var sequenceText: TextView? = null
    var fiction: Fiction? = null

    companion object {
        val FICTION_CHANGE_BAR = "change_bar"
    }

    private val handler = object : Handler() {
        override fun handleMessage(message: Message) {
            when (message.what) {
                0x01 -> {
                    val chapter: Chapter = message.obj as Chapter
                    readView.setChapter(chapter)
                    log("已经设置好了")
                }
                0x02 -> {
                    statrProgressDialog("生成目录中，请稍等")
                }
                0x03 -> {
                    cancel()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideTitleBar()
        setContentView(R.layout.fiction_layout)
        init()
        initListener()
    }

    override fun onPause() {
        super.onPause()
        saveProcess()
        unRegister()
    }

    override fun onResume() {
        super.onResume()
        register()
    }

    override fun onDestroy() {
        super.onDestroy()
        fiction = null
    }


    override fun onBackPressed() {
        if (top.visibility == View.VISIBLE) {
            changeBar()
        } else {
            super.onBackPressed()
        }
    }

    private fun init() {
        handler.sendEmptyMessage(0x02)
        ThreadUtil.getInstance()!!.execute(Runnable {
            val id = intent.getIntExtra("Id", 0)
            file = FictionDao.getInstance(this).getTxtFileByID(id)
            initFiction()
            //log(fiction!!.lineDatas.get(1).toString())
            var chapterNum = file!!.chapter
            if (chapterNum == 0 && fiction!!.hasForeword == 1) {
                chapterNum = 1
            }
            if (fiction!!.name != null) {
                val title: TextView = findViewById(R.id.title) as TextView
                title.setText(fiction!!.name!!.substring(0, fiction!!.name!!.length - 4))
            }
            val chapter = fiction!!.getChapter(chapterNum)
            readView.setFiction(fiction!!)
            readView.setPageNum(file!!.page)
            dealTxt(chapter!!)
        })
    }

    fun dealTxt(chapter: Chapter) {
        val textUtil = TextUtil.getInstance()
        textUtil.setWidthAndHeight(SystemUtils.MAX_WIDTH - readView.paddingLeft - readView.paddingRight, SystemUtils.MAX_HEIGHT - readView.paddingTop - readView.paddingBottom - readView.getBottomHeight().toInt())
        textUtil.init(readView.getPaint())
        //log(textUtil.toString())
        //Thread(Runnable {
        textUtil.dealChpter(chapter)
        var message = Message()
        message.obj = chapter
        message.what = 0x01
        handler.sendMessage(message)
        handler.sendEmptyMessage(0x03)
    }

    fun initListener() {
        top.setOnClickListener(clickListener)
        bottom.setOnClickListener(clickListener)
        contents.setOnClickListener(clickListener)
        title_back.setOnClickListener(clickListener)
        night.setOnClickListener(clickListener)
        font_set.setOnClickListener(clickListener)
    }

    val clickListener = View.OnClickListener {
        when (it.id) {
            R.id.top -> {
                log("top is click")
            }
            R.id.title_back -> {
                finish()
            }
            R.id.bottom -> {
                log("bottom is click")
            }
            R.id.night -> {

            }
            R.id.font_set -> {

            }
            R.id.contents -> {
                initContentsPopupWindow(it)
            }
            R.id.back -> {
                if (contentsWindow != null) {
                    contentsWindow!!.dismiss()
                    log("dismiss")
                }
            }
            R.id.sequence -> {
                if (file!!.sequence == 0) {
                    sequenceText!!.setText(R.string.positive_sequence)
                    file!!.sequence = 1
                    contentsAdapter!!.sequence = 1
                } else {
                    sequenceText!!.setText(R.string.reverse_order)
                    file!!.sequence = 0
                    contentsAdapter!!.sequence = 0
                }
                contentsAdapter!!.notifyDataSetChanged()
                contentsList!!.setSelection(0)
                FictionDao.getInstance(this).updateFiction(file)
            }
        }
    }

    fun log(msg: String) {
        Log.d(TAG, msg)
    }

    fun hideTitleBar() {
        val window = window
        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //隐藏状态栏
        //定义全屏参数
        val flag = WindowManager.LayoutParams.FLAG_FULLSCREEN
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag)
    }

    private fun register() {
        if (batterry == null) {
            batterry = BatteryChangedReceiver()
            val filter = getFilter()
            registerReceiver(batterry, filter)
        }
        if (timeBroadcastReceiver == null) {
            timeBroadcastReceiver = TimeBroadcastReceiver()
            val filter = IntentFilter()
            filter.addAction(Intent.ACTION_TIME_TICK)
            filter.addAction(FICTION_CHANGE_BAR)
            registerReceiver(timeBroadcastReceiver, filter)
        }
    }

    private fun unRegister() {
        if (batterry != null) {
            unregisterReceiver(batterry)
            batterry = null
        }
        if (timeBroadcastReceiver != null) {
            unregisterReceiver(timeBroadcastReceiver)
            timeBroadcastReceiver = null
        }
    }

    private fun getFilter(): IntentFilter {
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_BATTERY_CHANGED)
        filter.addAction(Intent.ACTION_BATTERY_LOW)
        filter.addAction(Intent.ACTION_BATTERY_OKAY)
        return filter
    }

    inner class BatteryChangedReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // TODO Auto-generated method stub
            val action = intent.action
            if (action!!.equals(Intent.ACTION_BATTERY_CHANGED, ignoreCase = true)) {
                // 当前电池的电压
                //val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
                // 电池的健康状态
//                val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
//                when (health) {
//                    BatteryManager.BATTERY_HEALTH_GOOD -> {
//                        log("很好")
//                        log("BATTERY_HEALTH_COLD")
//                    }
//                    BatteryManager.BATTERY_HEALTH_COLD -> log("BATTERY_HEALTH_COLD")
//                    BatteryManager.BATTERY_HEALTH_DEAD -> log("BATTERY_HEALTH_DEAD")
//                    BatteryManager.BATTERY_HEALTH_OVERHEAT -> log("BATTERY_HEALTH_OVERHEAT")
//                    BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> log("BATTERY_HEALTH_OVER_VOLTAGE")
//                    BatteryManager.BATTERY_HEALTH_UNKNOWN -> log("BATTERY_HEALTH_UNKNOWN")
//                    BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> log("BATTERY_HEALTH_UNSPECIFIED_FAILURE")
//                    else -> {
//                    }
//                }
                // 电池当前的电量, 它介于0和 EXTRA_SCALE之间
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                log(level.toString())
                // 电池电量的最大值
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                log(scale.toString())
                // 当前手机使用的是哪里的电源
//                val pluged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
//                when (pluged) {
//                    BatteryManager.BATTERY_PLUGGED_AC ->
//                        // 电源是AC charger.[应该是指充电器]
//                        log("电源是AC charger.")
//                    BatteryManager.BATTERY_PLUGGED_USB ->
//                        // 电源是USB port
//                        log("电源是USB port")
//                    else -> {
//                    }
//                }
                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                when (status) {
                    BatteryManager.BATTERY_STATUS_CHARGING -> {
                        // 正在充电
                        log("正在充电")
                        readView.setBattery(level, true)
                    }
                    BatteryManager.BATTERY_STATUS_DISCHARGING -> {
                        log("BATTERY_STATUS_DISCHARGING")
                        readView.setBattery(level, false)
                    }
                    BatteryManager.BATTERY_STATUS_FULL -> {
                        // 充满
                        log("充满")
                        readView.setBattery(level, true)
                    }
                    BatteryManager.BATTERY_STATUS_NOT_CHARGING -> {
                        // 没有充电
                        log("没有充电")
                        readView.setBattery(level, false)
                    }

                    BatteryManager.BATTERY_STATUS_UNKNOWN -> {
                        // 未知状态
                        log("未知状态")
                        readView.setBattery(level, false)
                    }
                    else -> {
                        readView.setBattery(level, false)
                    }
                }
                // 电池使用的技术。比如，对于锂电池是Li-ion
                //val technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)
                // 当前电池的温度
                //val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
                //val str = ("voltage = " + voltage + " technology = "
                //        + technology + " temperature = " + temperature)
                //log(str)
            } else if (action.equals(Intent.ACTION_BATTERY_LOW, ignoreCase = true)) {
                // 表示当前电池电量低
            } else if (action.equals(Intent.ACTION_BATTERY_OKAY, ignoreCase = true)) {
                // 表示当前电池已经从电量低恢复为正常
                println("BatteryChangedReceiver ACTION_BATTERY_OKAY---")
            }
        }

    }

    private fun saveProcess() {
        file!!.chapter = readView.getChapterNum()
        file!!.page = readView.getPageNum()
        log("saveProcess = " + file!!.chapter + " " + file!!.page + " " + readView.getChapterNum() + " " + readView.getPageNum())
        ThreadUtil.getInstance()!!.execute(Runnable {
            FictionDao.getInstance(this).updateFiction(file)
        })
    }

    private fun changeBar() {
        if (top.visibility == View.GONE) {
            top.visibility = View.VISIBLE
            bottom.visibility = View.VISIBLE
        } else {
            top.visibility = View.GONE
            bottom.visibility = View.GONE
        }
    }

    private var m = 0

    inner class TimeBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.equals(Intent.ACTION_TIME_TICK)) {
                readView.setTime(SystemUtils.getTime())
                if (m >= 2) {
                    m = 0
                    saveProcess()
                }
                m++
            }
            if (intent.action.equals(FICTION_CHANGE_BAR)) {
                changeBar()
            }
        }
    }

    private fun initFiction() {
        fiction = Fiction()
        if (file!!.charset == null || file!!.charset.equals("") || file!!.chapterNum == 0) {
            val parseTxt = ParseTxt(file!!.path)
            //lineDatas = ParseTxt.getChapterListByReadLine(file!!.path, parseTxt.charset)
            lineDatas = ParseTxt.getChapterList(file!!.path, parseTxt.charset)
            file!!.charset = parseTxt.charset
            val num = FictionChapterDao.getInstance(this).insertChapters(file!!.id, lineDatas)
            log("num=" + num + " lineDatas.size=" + lineDatas!!.size)
            if (num == lineDatas!!.size) {
                file!!.chapterNum = num
            } else {
                FictionChapterDao.getInstance(this).deleteChpaters(file!!.id)
            }
            fiction!!.name = file!!.name
            fiction!!.filePath = file!!.path
            fiction!!.lineDatas = lineDatas as MutableList<LineData>
            fiction!!.charset = file!!.charset
            fiction!!.maxChapter = lineDatas!!.size - 1
            fiction!!.sequence = file!!.sequence
            val chapter = fiction!!.getChapter(0)
            if (chapter!!.paragraphs!!.size == 0) {//无前言内容hasForeword设置为1
                file!!.hasForeword = 1
                fiction!!.hasForeword = 1
            } else {//有前言内容hasForeword设置为0
                fiction!!.hasForeword = 0
            }
            FictionDao.getInstance(this).updateFiction(file)
        } else {
            lineDatas = FictionChapterDao.getInstance(this).getLinedatas(file!!.id)
            fiction!!.name = file!!.name
            fiction!!.filePath = file!!.path
            fiction!!.lineDatas = lineDatas as MutableList<LineData>?
            fiction!!.charset = file!!.charset
            fiction!!.maxChapter = lineDatas!!.size - 1
            fiction!!.sequence = file!!.sequence
            fiction!!.hasForeword = file!!.hasForeword
        }
        log(fiction!!.toString())
    }

    //初始化章节菜单
    private fun initContentsPopupWindow(view: View) {
        if (contentsWindow == null) {
            val contentsView = LayoutInflater.from(this).inflate(R.layout.contents_layout, null)
            contentsList = contentsView.findViewById(R.id.contents_list) as ListView
            initContentsLayout(contentsView)
            contentsWindow = PopupWindow(contentsView, SystemUtils.MAX_WIDTH, SystemUtils.MAX_HEIGHT)
            contentsWindow!!.animationStyle = R.style.popup_window_anim
            contentsWindow!!.isFocusable = true
            contentsWindow!!.setBackgroundDrawable(null);
            contentsWindow!!.isOutsideTouchable = true
            contentsWindow!!.update()
            contentsList!!.post(Runnable {
                moveToLastContentsLine()
            })
        }
        contentsWindow!!.showAtLocation(view, Gravity.CENTER, 0, 0)
        //moveToLastContentsLine()
    }

    //初始化章节菜单adpater
    private fun initContentsLayout(view: View) {
        log("initContentsAdpater")
        val back = view.findViewById(R.id.back) as ImageView
        val sequence = view.findViewById(R.id.sequence) as LinearLayout
        sequenceText = view.findViewById(R.id.sequence_text) as TextView
        back.setOnClickListener(clickListener)
        sequence.setOnClickListener(clickListener)

        contentsAdapter = ContentsListAdapter(this, fiction!!.lineDatas, fiction!!.sequence, fiction!!.hasForeword)
        contentsList!!.adapter = contentsAdapter
        contentsList!!.setOnItemClickListener { parent, view, position, id ->
            var index = position
            if (fiction!!.hasForeword == 1 && contentsAdapter!!.sequence == 0) {//无前言同时正序
                index += 1
            }
            contentsWindow!!.dismiss()
            val l = contentsList!!.getItemAtPosition(index) as LineData
            dealTxt(fiction!!.getChapter(l.chapterNum)!!)
            log("chapterNum = " + l.chapterNum)
            readView.setPageNum(0)
            changeBar()
        }
        if (file!!.sequence == 0) {
            sequenceText!!.setText(R.string.positive_sequence)
        } else {
            sequenceText!!.setText(R.string.reverse_order)
        }
    }

    private fun moveToLastContentsLine() {
        if (file!!.sequence == 0) {
            contentsList!!.setSelection(file!!.chapter)
            log(file!!.chapter.toString())
        } else {
            contentsList!!.setSelection(contentsAdapter!!.count - file!!.chapter - 1)
            log((contentsAdapter!!.count - file!!.chapter - 1).toString())
        }
    }
}