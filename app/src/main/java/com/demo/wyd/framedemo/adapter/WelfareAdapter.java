package com.demo.wyd.framedemo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.demo.wyd.framedemo.R;
import com.demo.wyd.framedemo.bean.Welfare;

import java.util.List;

/**
 * Description:
 * Created by wyd on 2017/2/21.
 */

public class WelfareAdapter extends RecyclerView.Adapter<WelfareAdapter.ViewHolder> {
    private Context mContext;
    private List<Welfare> welfareList;

    public WelfareAdapter(Context mContext, List<Welfare> welfareList) {
        this.mContext = mContext;
        this.welfareList = welfareList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_welfare, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Welfare welfare = welfareList.get(position);
        Glide.with(mContext).load(welfare.getUrl()).into(holder.imvWelfare);
        holder.tvName.setText(welfare.getWho());
    }

    @Override
    public int getItemCount() {
        return welfareList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imvWelfare;
        private TextView tvName;

        ViewHolder(View itemView) {
            super(itemView);
            imvWelfare = itemView.findViewById(R.id.imageView);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }
}
