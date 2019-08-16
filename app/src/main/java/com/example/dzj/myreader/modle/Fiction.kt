package com.example.dzj.myreader.modle

import com.example.dzj.myreader.utils.ParseTxt
import java.io.IOException

class Fiction {
    var name: String? = null
    var filePath: String? = null
    var charset: String? = null
    var lineDatas: MutableList<LineData>? = null
    var maxChapter: Int = 0
    var sequence: Int = 0
    var hasForeword: Int = 0

    @Throws(IOException::class)
    fun getChapter(position: Int): Chapter? {
        var position = position
        var target = 0
        if (lineDatas!!.size == 2) {
            target = 1
            position = 0
        } else {
            target = position + 1
        }
        if (filePath != null && lineDatas != null && charset != null) {
            val chapter = ParseTxt.getChapter(lineDatas!![target].size, lineDatas!![position], filePath, charset)
            chapter.chapterNum = position

            return chapter
        }
        return null
    }

    override fun toString(): String {
        return ("name = " + name
                + "\n" + "charset = " + charset
                + "\n" + "maxChapter = " + maxChapter
                + "\n" + "lineDatas.size = " + lineDatas!!.size)
    }
}
