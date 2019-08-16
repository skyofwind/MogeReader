package com.example.dzj.myreader.utils

/**
 * Created by dzj on 2017/8/20.
 */

class BookUtil(width: Double) {
    //封面宽度
    var width: Int = 0
    //高度
    var height: Int = 0
    //左右边距
    private var hPadding: Int = 0
    //上下边距
    private var vPadding: Int = 0

    init {
        this.width = width.toInt()
        setValue()
    }

    private fun setValue() {
        val mheight = width / 0.8
        height = mheight.toInt()
        val mhPadding = width * 0.16
        hPadding = mhPadding.toInt()
        val mvPadding = mheight * 0.10
        vPadding = mvPadding.toInt()
    }

    fun gethPadding(): Int {
        return hPadding
    }

    fun getvPadding(): Int {
        return vPadding
    }

    fun sethPadding(hPadding: Int) {
        this.hPadding = hPadding
    }

    fun setvPadding(vPadding: Int) {
        this.vPadding = vPadding
    }

    override fun toString(): String {
        return "width=$width\nheight=$height\nleftPadding=$hPadding\ntopPadding=$vPadding"
    }
}
