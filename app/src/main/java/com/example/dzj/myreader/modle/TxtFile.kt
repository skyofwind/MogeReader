package com.example.dzj.myreader.modle

import android.os.Parcel
import android.os.Parcelable

class TxtFile : Parcelable {
    var name: String? = null
    var path: String? = null
    var size: String? = null
    var lastModified: Long = 0
    var isDirectory: Boolean = false

    var id: Int = 0
    var charset: String? = null
    var chapter: Int = 0
    var page: Int = 0
    var chapterNum: Int = 0
    var sequence: Int = 0
    var hasForeword: Int = 0

    constructor() {

    }

    constructor(name: String, path: String, lastModified: Long, size: String, isDirectory: Boolean) {
        this.name = name
        this.path = path
        this.lastModified = lastModified
        this.size = size
        this.isDirectory = isDirectory
    }

    protected constructor(`in`: Parcel) {
        name = `in`.readString()
        path = `in`.readString()
        size = `in`.readString()
        lastModified = `in`.readLong()
        isDirectory = `in`.readByte().toInt() != 0
        id = `in`.readInt()
        charset = `in`.readString()
        chapter = `in`.readInt()
        page = `in`.readInt()
        chapterNum = `in`.readInt()
        sequence = `in`.readInt()
        hasForeword = `in`.readInt()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(path)
        dest.writeString(size)
        dest.writeLong(lastModified)
        dest.writeByte((if (isDirectory) 1 else 0).toByte())
        dest.writeInt(id)
        dest.writeString(charset)
        dest.writeInt(chapter)
        dest.writeInt(page)
        dest.writeInt(chapterNum)
        dest.writeInt(sequence)
        dest.writeInt(hasForeword)
    }

    override fun toString(): String {
        return ("name=" + name
                + "\npath=" + path
                + "\ncharset=" + charset
                + "\nchapter=" + chapter
                + "\npage=" + page
                + "\nchapterNum=" + chapterNum
                + "\nsequence=" + sequence
                + "\nhasForeword=" + hasForeword)
    }

    companion object CREATOR : Parcelable.Creator<TxtFile> {
        override fun createFromParcel(parcel: Parcel): TxtFile {
            return TxtFile(parcel)
        }

        override fun newArray(size: Int): Array<TxtFile?> {
            return arrayOfNulls(size)
        }
    }
}
