package com.example.dzj.myreader.modle


class LineData {
    var liteNum: Int = 0//行数
    var chapterNum: Int = 0//段落字数
    var size: Long = 0//段落长度
    var length: Int = 0
    var chapterTitle: String? = null//标题名
    var isRead: Int = 0//是否阅读
    var id: Int = 0

    constructor(lineNum: Int, size: Long, length: Int, chapterNum: Int) {
        this.liteNum = lineNum
        this.size = size
        this.length = length
        this.chapterNum = chapterNum
    }

    constructor() {

    }

    override fun toString(): String {
        return ("lineNum = " + liteNum
                + "\nsize = " + size
                + "\nlength = " + length
                + "\nchapterNum = " + chapterNum
                + "\nchapterTitle = " + chapterTitle
                + "\nisRead = " + isRead
                + "\nid = " + id)
    }
}
