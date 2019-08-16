package com.example.dzj.myreader.modle

import com.google.common.collect.Lists

class TxtPager : Cloneable {

    var lines : MutableList<TxtLine> = mutableListOf()

    fun addTxtLine(line : TxtLine) {
        lines.add(line)
    }

    fun getLine(position : Int) : TxtLine {
        return lines.get(position)
    }

    public override fun clone(): Any {
        var txtPager: TxtPager? = null
        try {
            txtPager = super.clone() as TxtPager
            txtPager.lines = Lists.newArrayList(lines!!)
        } catch (e: CloneNotSupportedException) {
            e.printStackTrace()
        }

        return txtPager!!
    }
}
