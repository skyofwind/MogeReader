package com.example.dzj.myreader.modle

import android.util.Log

import com.example.dzj.myreader.utils.ParseTxt
import com.google.common.collect.Lists

import java.util.ArrayList

class Chapter : Cloneable {
    //private String strChapter;
    //	public String getStrChapter() {
    //		return strChapter;
    //	}

    //	public void setStrChapter(String strChapter) {
    //		this.strChapter = strChapter;
    //	}

    var paragraphs: MutableList<Paragraph>? = null
    var pagers: MutableList<TxtPager>? = null
    var chapterNum: Int = 0
    var title: String? = null
    var isRead: Int = 0
    var id: Int = 0


    constructor(str: String, num: Int) {
        //this.strChapter = str;
        this.chapterNum = num
        paragraphs = ParseTxt.getParagraph(str)
        pagers = ArrayList()
    }

    constructor(str: String) {
        //this.strChapter = str;
        paragraphs = ParseTxt.getParagraph(str)
        pagers = mutableListOf()
    }

    constructor() {}

    //	public String toString() {
    //		return strChapter;
    //	}

    fun log() {

        for (i in pagers!!.indices) {
            val pager = pagers!![i]
            val lines = pager.lines
            for (j in lines.indices) {
                val line = lines[j]
                Log.d("pager", "第" + i + "页" + j + "行=" + getString(line.position, line.start, line.end))
            }
        }
    }

    fun getString(position: Int, start: Int, end: Int): String {
        val p = paragraphs!![position].strParagraph
        return p!!.substring(start, end)
    }

    public override fun clone(): Any {
        var chapter: Chapter? = null
        try {
            chapter = super.clone() as Chapter
            chapter.paragraphs = Lists.newArrayList(paragraphs!!)
            chapter.pagers = Lists.newArrayList(pagers!!)
        } catch (e: CloneNotSupportedException) {
            e.printStackTrace()
        }

        return chapter!!
    }
}
