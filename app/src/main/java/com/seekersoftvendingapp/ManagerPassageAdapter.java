package com.seekersoftvendingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seekersoftvendingapp.network.entity.seekwork.MRoad;

import java.util.ArrayList;
import java.util.List;

public class ManagerPassageAdapter extends RecyclerView.Adapter<ManagerPassageAdapter.ManagerPassageViewHolder> {

    private List<MRoad> dataset;
    private Context mContext;

    class ManagerPassageViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_name;
        public TextView tv_huodao;
        public TextView tv_bu_num;
        public TextView tv_diff_num;
        public TextView tv_add, tv_cut;

        public ManagerPassageViewHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_huodao = (TextView) itemView.findViewById(R.id.tv_huodao);
            tv_diff_num = (TextView) itemView.findViewById(R.id.tv_diff_num);
            tv_bu_num = (TextView) itemView.findViewById(R.id.tv_bu_num);
            tv_add = (TextView) itemView.findViewById(R.id.tv_add);
            tv_cut = (TextView) itemView.findViewById(R.id.tv_cut);
        }
    }

    public ManagerPassageAdapter(Context mContext) {
        this.mContext = mContext;
        this.dataset = new ArrayList<>();
    }

    public void setPassageList(@NonNull List<MRoad> managerPassage) {
        dataset = managerPassage;
        notifyDataSetChanged();
    }

    public List<MRoad> getDataset() {
        return dataset;
    }

    public MRoad getPassage(int position) {
        return dataset.get(position);
    }

    @Override
    public ManagerPassageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stu_view, parent, false);
        return new ManagerPassageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ManagerPassageViewHolder holder, final int position) {
        final MRoad mRoad = dataset.get(position);

        holder.tv_huodao.setText(String.valueOf(mRoad.getRoadCode()));
        holder.tv_name.setText(mRoad.getProductName());
        holder.tv_bu_num.setText(String.valueOf(mRoad.getLackNum()));
        holder.tv_diff_num.setText(String.valueOf(mRoad.getChaLackNum()));

        holder.tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRoad.getChaLackNum() >= mRoad.getLackNum()) {
                    // 不可以在加

                } else {
                    mRoad.setChaLackNum(mRoad.getChaLackNum() + 1);
                }
                holder.tv_diff_num.setText(String.valueOf(mRoad.getChaLackNum()));
            }
        });

        holder.tv_cut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRoad.getChaLackNum() == 0) {
                    // 不可以在减少
                } else {
                    mRoad.setChaLackNum(mRoad.getChaLackNum() - 1);
                }
                holder.tv_diff_num.setText(String.valueOf(mRoad.getChaLackNum()));
            }
        });

        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

}
