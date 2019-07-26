package com.example.bdtts_test.constant

import com.baidu.tts.client.TtsMode

/**
 * @author: ${User}
 * @date: ${Date}
 */
object TTS_Config {
    internal var APP_ID = "16858689"
    internal var APP_KEY = "SeT3GM2wjL2RGUfDqHcyYvG1"
    internal var SECRET_KEY = "tC1rlunrSIXRbd3HXAH7Fu4cEF89bc4F"

    internal var TTS_Mode = TtsMode.MIX
    internal val ROOT_DIR = "/sdcard/baiduTTS"
    internal val TEXT_FILENAME = ROOT_DIR + "/" + "bd_etts_text.dat"
    internal val MODEL_FILENAME = ROOT_DIR + "/" + "bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat"
}
