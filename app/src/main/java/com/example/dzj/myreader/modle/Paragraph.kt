package com.example.dzj.myreader.modle

class Paragraph(var strParagraph: String?) : Cloneable {
    var startPage: Int = 0
    var startLine: Int = 0
    var endPage: Int = 0
    var endLine: Int = 0
    override fun toString(): String {
        return strParagraph.toString()
    }

    public override fun clone(): Any {
        var paragraph: Paragraph? = null
        try {
            paragraph = super.clone() as Paragraph
        } catch (e: CloneNotSupportedException) {
            e.printStackTrace()
        }

        return paragraph!!
    }
}
