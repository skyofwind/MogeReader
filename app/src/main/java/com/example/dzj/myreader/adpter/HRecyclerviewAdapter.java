package com.example.dzj.myreader.adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dzj.myreader.R;

import java.util.List;

/**
 * Created by dzj on 2017/8/21.
 */

public class HRecyclerviewAdapter extends RecyclerView.Adapter {
    private Context context;

    private List<String> mDatas;
    private LayoutInflater m_layoutInflater;
    private OnItemClickListener mOnItemClickListener;

    public HRecyclerviewAdapter(Context context, List<String> mDatas) {
        this.mDatas = mDatas;
        this.context = context;
        m_layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemViewHolder holder = new ItemViewHolder(m_layoutInflater.inflate(R.layout.recyclerview_item, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder mholder = (ItemViewHolder) holder;
        mholder.m_text.setText(mDatas.get(position));
        if (position == mDatas.size() - 1) {
            mholder.m_imageView.setVisibility(View.INVISIBLE);
        } else {
            mholder.m_imageView.setVisibility(View.VISIBLE);
        }
        if (mOnItemClickListener != null) {
            /**
             * 这里加了判断，itemViewHolder.itemView.hasOnClickListeners()
             * 目的是减少对象的创建，如果已经为view设置了click监听事件,就不用重复设置了
             * 不然每次调用onBindViewHolder方法，都会创建两个监听事件对象，增加了内存的开销
             */
            if (!holder.itemView.hasOnClickListeners()) {
                Log.d("ListAdapter", "setOnClickListener");
                if (position < mDatas.size() - 1) {
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int pos = holder.getPosition();
                            mOnItemClickListener.onItemClick(v, pos);
                        }
                    });
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView m_text;
        private ImageView m_imageView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            m_imageView = (ImageView) itemView.findViewById(R.id.img);
            m_text = (TextView) itemView.findViewById(R.id.txt);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void refresh() {
        notifyDataSetChanged();
    }
}
