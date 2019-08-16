package com.example.dzj.myreader.modle

class TxtLine : Cloneable {
    var position: Int = 0//文章段数
    var start: Int = 0//段字符串中的开始位置
    var end: Int = 0//结束位置

    constructor() {}

    constructor(start: Int, end: Int) {
        this.start = start
        this.end = end
    }

    public override fun clone(): Any {
        var txtLine: TxtLine? = null
        try {
            txtLine = super.clone() as TxtLine
        } catch (e: CloneNotSupportedException) {
            e.printStackTrace()
        }

        return txtLine!!
    }
}
