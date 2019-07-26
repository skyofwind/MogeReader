package com.example.dzj.myreader.minterface

/**
 * @author: ${User}
 * @date: ${Date}
 *
 */
interface SpeechResultListener {
    abstract fun getSpeechResult(): Boolean
    abstract fun setSpeechResult(result : Boolean)
}