package com.example.dzj.myreader.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.baidu.tts.client.SpeechSynthesizer
import com.baidu.tts.client.TtsMode
import com.example.bdtts_test.constant.TTS_Config
import com.example.dzj.myreader.minterface.SpeechResultListener
import com.example.dzj.myreader.voice.control.InitConfig
import com.example.dzj.myreader.voice.listener.MessageListener
import com.example.dzj.myreader.voice.util.OfflineResource

import java.io.File

/**
 * @author: ${User}
 * @date: ${Date}
 *
 */
@SuppressLint("StaticFieldLeak")
class BaiduTTSUtil private constructor(context: Context) : SpeechResultListener {

    var mContext : Context ?= context
    var mSpeechSynthesizer: SpeechSynthesizer? = null
    val ttsMode = TtsMode.ONLINE
    var fileCopySuccess = false
    val listener = MessageListener()

    companion object {
        @Volatile private var instance : BaiduTTSUtil ?= null
        fun  getInstance(context : Context) : BaiduTTSUtil? {
            instance ?: synchronized(this) {
                instance ?: BaiduTTSUtil(context).also { instance = it }
            }
            instance!!.mContext = context
            return instance
        }

    }

    fun initTTs(){
        if (fileCopySuccess) {
            val isMix = TTS_Config.TTS_Mode.equals(TtsMode.MIX)
            var isSuccess = false
            if (isMix) {
                isSuccess = checkOfflineResources()
                if (!isSuccess) {
                    return
                } else {
                    log("initTTs", "离线资源存在并且可读, 目录：" + TTS_Config.ROOT_DIR)
                }
            }

            mSpeechSynthesizer = SpeechSynthesizer.getInstance()
            mSpeechSynthesizer!!.setContext(mContext)
            mSpeechSynthesizer!!.setSpeechSynthesizerListener(listener)

            var result = mSpeechSynthesizer!!.setAppId(TTS_Config.APP_ID)
            checkResult(result, "setApiKey")
            result = mSpeechSynthesizer!!.setApiKey(TTS_Config.APP_KEY, TTS_Config.SECRET_KEY)
            if (isMix) {
                isSuccess = checkAuth()
                if (!isSuccess) {
                    return
                }
                mSpeechSynthesizer!!.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, TTS_Config.TEXT_FILENAME)
                mSpeechSynthesizer!!.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, TTS_Config.MODEL_FILENAME)
            }
            //设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
            mSpeechSynthesizer!!.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0")
            //设置合成的音量，0-9 ，默认 5
            mSpeechSynthesizer!!.setParam(SpeechSynthesizer.PARAM_VOLUME, "9")
            //设置合成的语速，0-9 ，默认 5
            mSpeechSynthesizer!!.setParam(SpeechSynthesizer.PARAM_SPEED, "5")
            //设置合成的语调，0-9 ，默认 5
            mSpeechSynthesizer!!.setParam(SpeechSynthesizer.PARAM_PITCH, "5")

            mSpeechSynthesizer!!.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI)
            // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
            // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
            // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
            // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
            // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线

            val params = HashMap<String, String>()
            if (isMix) {
                params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, TTS_Config.TEXT_FILENAME)
                params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, TTS_Config.MODEL_FILENAME)
            }
            val initConfig = InitConfig(TTS_Config.APP_ID, TTS_Config.APP_KEY, TTS_Config.SECRET_KEY, ttsMode, params, listener)
            result = mSpeechSynthesizer!!.initTts(ttsMode)
            checkResult(result, "initTts")
        }
    }

    fun checkAuth() : Boolean{
        val authInfo = mSpeechSynthesizer!!.auth(TTS_Config.TTS_Mode)
        if (!authInfo.isSuccess) {
            val errorMsg = authInfo.ttsError.detailMessage
            log("checkAuth", "errorMsg")
            return false
        } else {
            log("checkAuth", "验证通过，离线正式授权文件存在。")
            return true
        }
    }

    fun checkOfflineResources(): Boolean {
        val filenames = arrayOf<String>(TTS_Config.TEXT_FILENAME, TTS_Config.MODEL_FILENAME)
        for (path in filenames) {
            val f = File(path)
            if (!f.canRead()) {
                log("checkOfflineResources","[ERROR] 文件不存在或者不可读取，请从assets目录复制同名文件到：$path")
                log("checkOfflineResources","[ERROR] 初始化失败！！！")
                return false
            }
        }
        return true
    }

    fun speak(text : String) {
        /** 以下参数每次合成时都可以修改
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
         *  设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "5"); 设置合成的音量，0-9 ，默认 5
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5"); 设置合成的语速，0-9 ，默认 5
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5"); 设置合成的语调，0-9 ，默认 5
         *
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
         *  MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
         *  MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
         *  MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
         *  MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
         */
        if (fileCopySuccess) {
            if (mSpeechSynthesizer == null) {
                log("speak","初始化失败")
                return
            }
            val result = mSpeechSynthesizer!!.speak(text)
            checkResult(result, "speck")
        }

    }

    fun stop() {
        if (fileCopySuccess) {
            log("stop","停止语音 按钮已经点击")
            val result = mSpeechSynthesizer!!.stop()
            setSpeechResult(true)
            checkResult(result, "stop")
        }
    }

    fun pause() {
        if (fileCopySuccess) {
            log("pause","暂停语音 按钮已经点击")
            val result = mSpeechSynthesizer!!.pause()
            checkResult(result, "pause")
        }
    }

    fun resume() {
        if (fileCopySuccess) {
            log("resume","恢复语音 按钮已经点击")
            val result = mSpeechSynthesizer!!.resume()
            checkResult(result, "resume")
        }
    }

    fun checkResult(result : Int, method : String) {
        if (result != 0) {
            Toast.makeText(mContext, "error code :" + result + " method:" + method, Toast.LENGTH_SHORT).show()
            log("checkResult","error code :" + result + " method:" + method + ", 错误码文档:http://yuyin.baidu.com/docs/tts/122 ")
        } else {
            log(method, "成功")
        }
    }

    fun initFile() {
        log("initFile", "开始复制文件")
        var offlineResource = OfflineResource(mContext, OfflineResource.VOICE_DUXY)
        offlineResource.setOfflineVoiceType(OfflineResource.VOICE_DUYY)
        offlineResource.setOfflineVoiceType(OfflineResource.VOICE_FEMALE)
        offlineResource.setOfflineVoiceType(OfflineResource.VOICE_MALE)
        log("initFile", "结束复制文件")
        fileCopySuccess = true
    }

    override fun getSpeechResult(): Boolean {
        return listener.getSpeechResult()
    }

    override fun setSpeechResult(result: Boolean) {
        listener.setSpeechResult(result)
    }

    fun log(tag: String, value: String) {
        Log.e("BaiduTTSUtil", "$tag $value")
    }
}