package com.example.dzj.myreader.view

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.View
import android.text.TextPaint
import android.util.Log
import android.view.MotionEvent

import com.example.dzj.myreader.utils.SystemUtils
import com.example.dzj.myreader.R
import com.example.dzj.myreader.activity.FictionActivity
import com.example.dzj.myreader.database.FictionChapterDao
import com.example.dzj.myreader.modle.Chapter
import com.example.dzj.myreader.modle.Fiction
import com.example.dzj.myreader.utils.TextUtil
import com.example.dzj.myreader.utils.ThreadUtil


class ReadView : View {
    private val TAG = "ReadView"
    private var textSize: Float = 50.0f//字体大小
    private var topPaint: Paint = TextPaint()
    private var mWidth: Int = 0
    private var mHeight: Int = 0
    private var textPaint = TextPaint()
    private var chapter: Chapter = Chapter()
    private var lastChapter = Chapter()
    private var nextChapter = Chapter()
    private var pagerNum = 0
    private var time: String? = null
    private var batteryLevel: Int? = null
    private var isCharging: Boolean = false
    private val bottomHeight = 40f
    private var fiction: Fiction? = null
    private var startNum = 0

    private val handler = object : Handler() {
        override fun handleMessage(message: Message) {
            when (message.what) {
                0x01 -> {
                    invalidate()
                }
            }
        }
    }

    constructor(context: Context) : super(context) {
        initPaint()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initPaint()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initPaint()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initPaint()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec)
        val WIDTH_DEFAULT = SystemUtils.MAX_WIDTH
        val HEIGHT_DEFAULT = SystemUtils.MAX_HEIGHT
        if (widthMeasureSpec === View.MeasureSpec.AT_MOST && heightSpecSize == View.MeasureSpec.AT_MOST) {
            setMeasuredDimension(WIDTH_DEFAULT, HEIGHT_DEFAULT)
        } else if (widthSpecMode == View.MeasureSpec.AT_MOST) {
            setMeasuredDimension(WIDTH_DEFAULT, heightSpecSize)
        } else if (heightSpecMode == View.MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, HEIGHT_DEFAULT)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (chapter.pagers != null) {
            if (canvas != null) {
                writeText(canvas)
            }
        }
        if (canvas != null) {
            writeBottom(canvas)
        }
    }

    fun initPaint() {
        textPaint.textSize = textSize
        textPaint.color = Color.BLACK
        textPaint.typeface = Typeface.SANS_SERIF
        textPaint.isAntiAlias = true

        topPaint.textSize = 45f
        topPaint.color = SystemUtils.getColor(context, R.color.title_text)

        topPaint.typeface = Typeface.SANS_SERIF
        topPaint.isAntiAlias = true

    }

    public fun getPaint(): TextPaint {
        return textPaint
    }

    fun writeBottom(canvas: Canvas) {
        //电池
        val maxHeight = SystemUtils.MAX_HEIGHT
        val paint = Paint()
        val bWidth = 80;
        val bHeight = bottomHeight;
        val sWidth = 5f
        paint.color = SystemUtils.getColor(context, R.color.title_text)
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = sWidth
        var rect = RectF(paddingLeft.toFloat(), (maxHeight - paddingBottom - bHeight).toFloat(), (paddingLeft + bWidth).toFloat(), (maxHeight - paddingBottom).toFloat())
        canvas.drawRect(rect, paint)

        paint.style = Paint.Style.FILL_AND_STROKE
        val cWidth = bWidth / 10
        val cSpec = sWidth
        rect = RectF((paddingLeft.toFloat() + bWidth + cSpec).toFloat(), (maxHeight - paddingBottom - (bHeight * 2 / 3)).toFloat(), (paddingLeft.toFloat() + bWidth + cWidth).toFloat(), (maxHeight - paddingBottom - (bHeight * 1 / 3)).toFloat())
        canvas.drawRect(rect, paint)

        paint.style = Paint.Style.FILL
        val bSpac = sWidth + 2
        if (batteryLevel != null) {
            if (batteryLevel!! >= 0 && batteryLevel!! <= 100) {
                val levelWidth: Int = batteryLevel!! * bWidth / 100
                rect = RectF(paddingLeft.toFloat() + bSpac, (maxHeight - paddingBottom - bHeight + bSpac).toFloat(), (paddingLeft + levelWidth - bSpac).toFloat(), (maxHeight - paddingBottom - bSpac).toFloat())
                canvas.drawRect(rect, paint)
            }
        }

        //闪电形状
        val lSpec = 20
        val lWidth = (paddingLeft + bWidth + cWidth + lSpec) / 5
        if (isCharging) {
            val path = Path()
            path.moveTo((paddingLeft + bWidth + cWidth + lSpec + lWidth - 6).toFloat(), (maxHeight - paddingBottom - bHeight - 5).toFloat())
            path.lineTo((paddingLeft + bWidth + cWidth + lSpec).toFloat(), (maxHeight - paddingBottom - bHeight * 0.4).toFloat())
            path.lineTo((paddingLeft + bWidth + cWidth + lSpec + lWidth * 0.3).toFloat(), (maxHeight - paddingBottom - bHeight * 0.4).toFloat())
            path.lineTo((paddingLeft + bWidth + cWidth + lSpec + 6).toFloat(), (maxHeight - paddingBottom + 5).toFloat())
            path.lineTo((paddingLeft + bWidth + cWidth + lSpec + lWidth).toFloat(), (maxHeight - paddingBottom - bHeight * 0.6).toFloat())
            path.lineTo((paddingLeft + bWidth + cWidth + lSpec + lWidth * 0.7).toFloat(), (maxHeight - paddingBottom - bHeight * 0.6).toFloat())
            path.close()
            canvas.drawPath(path, paint)
        }

        //电量文字
        if (batteryLevel != null) {
            topPaint.textSize = 50f
            if (batteryLevel!! >= 0 && batteryLevel!! <= 100) {
                canvas.drawText(batteryLevel.toString() + "%", (paddingLeft + bWidth + cWidth + lSpec * 2 + lWidth).toFloat(), (maxHeight - paddingBottom).toFloat(), topPaint)
            } else {
                canvas.drawText("--", (paddingLeft + bWidth + cWidth + lSpec * 2 + lWidth).toFloat(), (maxHeight - paddingBottom).toFloat(), topPaint)
            }
        }
        //时间
        if (time != null) {
            canvas.drawText(time, (paddingLeft + bWidth + cWidth + lSpec * 2 + lWidth + 150).toFloat(), (maxHeight - paddingBottom).toFloat(), topPaint)
        } else {
            canvas.drawText(SystemUtils.getTime(), (paddingLeft + bWidth + cWidth + lSpec * 2 + lWidth + 165).toFloat(), (maxHeight - paddingBottom).toFloat(), topPaint)
        }

        if (chapter.pagers != null) {
            val str: String = (pagerNum + 1).toString() + "/" + chapter.pagers.size
            val pWidth = topPaint.measureText(str)
            val pX = SystemUtils.MAX_WIDTH - paddingRight - pWidth
            canvas.drawText(str, pX, (maxHeight - paddingBottom).toFloat(), topPaint)
        }


        topPaint.textSize = 45f
    }

    fun writeText(canvas: Canvas) {
        if (pagerNum > chapter.pagers.size) {
            pagerNum = 1
        }
        val txtPager = chapter.pagers!!.get(pagerNum)
        val lines = txtPager!!.lines

        var i = 0
        while (i < lines!!.size) {
            val line = lines.get(i)
            val str = chapter.getString(line.position, line.start, line.end)
            val height = TextUtil.getInstance().rowHeight
            val lineSpec = TextUtil.getInstance().lineSpacing
            if (i == 0) {
                if (chapter.chapterNum == 0) {
                    canvas.drawText("前言", this.paddingLeft.toFloat(), height * (i + 1) + lineSpec * i + paddingTop, topPaint)
                } else {
                    canvas.drawText(chapter.paragraphs.get(0).strParagraph, this.paddingLeft.toFloat(), height * (i + 1) + lineSpec * i + paddingTop, topPaint)
                }
            }
            canvas.drawText(str, this.paddingLeft.toFloat(), height * (i + 2) + lineSpec * (i + 1) + paddingTop, textPaint)
            i++
        }

    }

    fun setChapter(chapter: Chapter) {
        this.chapter = chapter
        val i = chapter.chapterNum
        log("chapter.chapterNum = " + chapter.chapterNum)
        ThreadUtil.getInstance().execute(Runnable {
            if (i > 0) {
                lastChapter = fiction!!.getChapter(i - 1)
                TextUtil.getInstance().dealChpter(lastChapter)
            }
            if (i < fiction!!.maxChapter - 1) {
                nextChapter = fiction!!.getChapter(i + 1)
                TextUtil.getInstance().dealChpter(nextChapter)
            }
            handler.sendEmptyMessage(0x01)
        })
        setRead(chapter)
    }

    var lastX: Int = 0
    var lastY: Int = 0
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            var nowX: Int = 0
            var nowY: Int = 0
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastX = event.rawX.toInt()
                    lastY = event.rawY.toInt()
                }
                MotionEvent.ACTION_UP -> {
                    nowX = event.rawX.toInt()
                    nowY = event.rawY.toInt()
                    val offset = lastX - nowX
                    log("up offset=" + (lastX - nowX))
                    if (offset == 0) {
                        if (nowX > SystemUtils.MAX_WIDTH / 3 && nowX < SystemUtils.MAX_WIDTH / 3 * 2 && nowY > SystemUtils.MAX_HEIGHT / 3 && nowY < SystemUtils.MAX_HEIGHT / 3 * 2) {
                            val intent = Intent()
                            intent.setAction(FictionActivity.FICTION_CHANGE_BAR)
                            context.sendBroadcast(intent)
                        } else {
                            if (lastX >= SystemUtils.MAX_WIDTH / 3 * 2) {
                                //前进
                                goNext()
                            } else {
                                //后退
                                goLast()
                            }
                        }

                    }
                    //左滑
                    if (offset >= 100) {
                        goLast()
                    }
                    //右滑
                    if (offset <= -100) {
                        goNext()
                    }

                }
            }
        }
        return true
    }

    private fun goNext() {
        val max = chapter.pagers.size
        if (pagerNum < max - 1) {
            pagerNum++
            invalidate()
        } else {
            if (chapter.chapterNum < fiction!!.maxChapter - 1) {
                lastChapter = chapter.clone()
                chapter = nextChapter.clone()
                if (chapter.chapterNum < fiction!!.maxChapter - 1) {
                    ThreadUtil.getInstance().execute(Runnable {
                        nextChapter = fiction!!.getChapter(chapter.chapterNum + 1)
                        TextUtil.getInstance().dealChpter(nextChapter)
                    })
                }
                pagerNum = 0
                invalidate()
                setRead(chapter)
            }
        }
    }

    private fun goLast() {
        if (pagerNum > 0) {
            pagerNum--
            invalidate()
        } else {
            if (chapter.chapterNum > startNum) {
                nextChapter = chapter.clone()
                chapter = lastChapter.clone()
                if (chapter.chapterNum > startNum) {
                    ThreadUtil.getInstance().execute(Runnable {
                        lastChapter = fiction!!.getChapter(chapter.chapterNum - 1)
                        TextUtil.getInstance().dealChpter(lastChapter)
                    })
                }
                pagerNum = chapter.pagers.size - 1
                invalidate()
                setRead(chapter)
            }
        }
    }

    private fun setRead(chapter: Chapter) {
        log("updateChapter = nmu:" + chapter.chapterNum + " id:" + chapter.id + " isRead:" + chapter.isRead)
        if (chapter != null && chapter.isRead == 0) {
            chapter.isRead = 1
            ThreadUtil.getInstance().execute(Runnable {
                fiction!!.lineDatas.get(chapter.chapterNum).isRead = 1
                FictionChapterDao.getInstance(context).updateChapter(fiction!!.lineDatas.get(chapter.chapterNum))
            })

        }
    }

    public fun setTime(time: String?) {
        if (time != null) {
            this.time = time
            invalidate()
        }
    }

    public fun setBattery(level: Int, isCharging: Boolean) {
        this.batteryLevel = level
        this.isCharging = isCharging
        invalidate()
    }

    public fun getBottomHeight(): Float {
        return bottomHeight
    }

    public fun getChapterNum(): Int {
        log("getChapterNum " + chapter.title + " " + chapter.chapterNum + " " + chapter.id)
        return chapter.chapterNum
    }

    public fun getPageNum(): Int {
        return pagerNum
    }

    public fun setPageNum(num: Int) {
        this.pagerNum = num
    }

    public fun setFiction(fiction: Fiction) {
        this.fiction = fiction
        if (fiction!!.hasForeword == 1) {
            startNum = 1
        } else {
            startNum = 0
        }
    }

    fun log(msg: String) {
        Log.d(TAG, msg)
    }
}
