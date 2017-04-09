package com.seekersoftvendingapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.seekersoftvendingapp.database.table.Passage;

import java.util.ArrayList;
import java.util.List;

public class ManagerCheckPassageAdapter extends RecyclerView.Adapter<ManagerCheckPassageAdapter.ManagerPassageViewHolder> {

    private List<Passage> dataset;
    private Context mContext;

    class ManagerPassageViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_passageid;
        public TextView tv_productname;
        public TextView tv_modify_num;

        public ManagerPassageViewHolder(View itemView) {
            super(itemView);
            tv_passageid = (TextView) itemView.findViewById(R.id.tv_passageid);
            tv_productname = (TextView) itemView.findViewById(R.id.tv_productname);
            tv_modify_num = (TextView) itemView.findViewById(R.id.tv_modify_num);
        }
    }

    public ManagerCheckPassageAdapter(Context mContext) {
        this.mContext = mContext;
        this.dataset = new ArrayList<>();
    }

    public void setPassageList(@NonNull List<Passage> managerPassage) {
        dataset = managerPassage;
        notifyDataSetChanged();
    }

    public Passage getPassage(int position) {
        return dataset.get(position);
    }

    @Override
    public ManagerPassageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_check_stock, parent, false);
        return new ManagerPassageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ManagerPassageViewHolder holder, final int position) {
        Passage passage = dataset.get(position);
        holder.tv_passageid.setText(String.valueOf(passage.getSeqNo()));
        holder.tv_productname.setText(String.valueOf(passage.getKeepone()));
        holder.tv_modify_num.setText(String.valueOf(passage.getStock()));
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

}
