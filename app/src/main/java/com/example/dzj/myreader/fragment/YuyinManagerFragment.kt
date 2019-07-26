package com.example.dzj.myreader.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.dzj.myreader.R
import com.example.dzj.myreader.utils.BaiduTTSUtil
import kotlinx.android.synthetic.main.yuyin_manager.*

/**
 * Created by dzj on 2017/8/19.
 */

class YuyinManagerFragment : Fragment() {

    private var rootView: View? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        rootView = inflater.inflate(R.layout.yuyin_manager, null)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setOnclickListener()
    }

    val clickListener = View.OnClickListener {
        when(it!!.id) {
            R.id.start -> {
                if (BaiduTTSUtil.getInstance(this.context!!)!!.getSpeechResult()) {
                    BaiduTTSUtil.getInstance(this.context!!)!!.speak(editText.text.toString())
                } else {
                    BaiduTTSUtil.getInstance(this.context!!)!!.resume()
                }
            }
            R.id.pause -> BaiduTTSUtil.getInstance(this.context!!)!!.pause()
            R.id.stop -> BaiduTTSUtil.getInstance(this.context!!)!!.stop()
        }
    }

    private fun setOnclickListener() {
        start.setOnClickListener(clickListener)
        pause.setOnClickListener(clickListener)
        stop.setOnClickListener(clickListener)
    }
}
