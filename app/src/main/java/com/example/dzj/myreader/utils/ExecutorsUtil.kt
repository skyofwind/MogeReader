package com.example.dzj.myreader.utils

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @author: ${User}
 * @date: ${Date}
 *
 */
class ExecutorsUtil private constructor() {

    private var fixedThreadPool: ExecutorService

    init {
        fixedThreadPool = Executors.newFixedThreadPool(4)
    }

    companion object {
        @Volatile
        internal var instance: ExecutorsUtil? = null

        fun getInstance() : ExecutorsUtil? {
            instance ?: synchronized(this) {
                instance ?: ExecutorsUtil().also { instance = it }
            }
            return instance
        }
    }

    fun execute(runnable: Runnable) {
        fixedThreadPool.execute(runnable)
    }
}