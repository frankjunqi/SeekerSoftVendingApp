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

        public TextView tv_passageid;
        public TextView tv_productname;
        public TextView tv_modify_num;

        public TextView tv_cut, tv_add;
        public TextView tv_diff_num;

        public ManagerPassageViewHolder(View itemView) {
            super(itemView);
            tv_passageid = (TextView) itemView.findViewById(R.id.tv_passageid);
            tv_productname = (TextView) itemView.findViewById(R.id.tv_productname);
            tv_modify_num = (TextView) itemView.findViewById(R.id.tv_modify_num);
            tv_cut = (TextView) itemView.findViewById(R.id.tv_cut);
            tv_add = (TextView) itemView.findViewById(R.id.tv_add);
            tv_diff_num = (TextView) itemView.findViewById(R.id.tv_diff_num);

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

    public MRoad getPassage(int position) {
        return dataset.get(position);
    }

    @Override
    public ManagerPassageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_modify_stock, parent, false);
        return new ManagerPassageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ManagerPassageViewHolder holder, final int position) {
        final MRoad passage = dataset.get(position);
        holder.tv_passageid.setText(String.valueOf(passage.getNo()));
        holder.tv_productname.setText(String.valueOf(passage.getProductName()));
        holder.tv_modify_num.setText(String.valueOf(passage.getLackNum()));
        holder.tv_diff_num.setText(String.valueOf(passage.getChaLackNum()));
        holder.tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passage.getChaLackNum() >= passage.getLackNum()) {
                    // 不可以在加

                } else {
                    passage.setChaLackNum(passage.getChaLackNum() + 1);
                }
                holder.tv_diff_num.setText(String.valueOf(passage.getChaLackNum()));
            }
        });
        holder.tv_cut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passage.getChaLackNum() == 0) {
                    // 不可以在减少
                } else {
                    passage.setChaLackNum(passage.getChaLackNum() - 1);
                }
                holder.tv_diff_num.setText(String.valueOf(passage.getChaLackNum()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

}
