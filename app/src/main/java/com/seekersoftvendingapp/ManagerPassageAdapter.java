package com.seekersoftvendingapp;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.seekersoftvendingapp.database.table.Passage;

import java.util.ArrayList;
import java.util.List;

public class ManagerPassageAdapter extends RecyclerView.Adapter<ManagerPassageAdapter.ManagerPassageViewHolder> {

    private List<Passage> dataset;
    private Context mContext;

    class ManagerPassageViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_passageid;
        public TextView tv_productname;
        public TextView tv_modify_num;
        public TextView tv_modify_down;

        public ManagerPassageViewHolder(View itemView) {
            super(itemView);
            tv_passageid = (TextView) itemView.findViewById(R.id.tv_passageid);
            tv_productname = (TextView) itemView.findViewById(R.id.tv_productname);
            tv_modify_num = (TextView) itemView.findViewById(R.id.tv_modify_num);
            tv_modify_down = (TextView) itemView.findViewById(R.id.tv_modify_down);
        }
    }

    public ManagerPassageAdapter(Context mContext) {
        this.mContext = mContext;
        this.dataset = new ArrayList<>();
    }

    public void setPassageList(@NonNull List<Passage> managerPassage) {
        dataset = managerPassage;
        notifyDataSetChanged();
    }

    public List<Passage> getPassageList() {
        return dataset;
    }

    public Passage getPassage(int position) {
        return dataset.get(position);
    }

    @Override
    public ManagerPassageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_modify_stock, parent, false);
        return new ManagerPassageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ManagerPassageViewHolder holder, final int position) {
        Passage passage = dataset.get(position);
        holder.tv_passageid.setText(String.valueOf(passage.getSeqNo()));
        holder.tv_productname.setText(String.valueOf(passage.getKeepone()));
        holder.tv_modify_num.setText(String.valueOf(passage.getCapacity() - passage.getStock()));
        holder.tv_modify_down.setText(passage.getKeeptwo());
        holder.tv_modify_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertRadioListDialog(holder.tv_modify_down, getPassage(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    // 单货道补货数量
    int selecteStock = 0;

    // 当前库存
    public void alertRadioListDialog(final TextView tv_modify_down, final Passage passage) {
        if (passage == null) {
            Toast.makeText(mContext, "货道信息为null.", Toast.LENGTH_SHORT).show();
            return;
        }
        // 最大库存
        int capacity = passage.getCapacity();
        // 当前库存
        int currentStock = passage.getStock();
        // 可以补货的数量
        int canSupply = capacity - currentStock;

        // 数据做校验
        if (canSupply <= 0) {
            Toast.makeText(mContext, "已经是最大库存数量", Toast.LENGTH_SHORT).show();
            return;
        }
        final String[] intlist = new String[canSupply];
        for (int i = 0; i < canSupply; i++) {
            intlist[i] = String.valueOf(i + 1);
        }


        new AlertDialog.Builder(mContext)
                .setTitle("货道补货")
                .setSingleChoiceItems(intlist, currentStock - 1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selecteStock = which + 1;
                        passage.setKeeptwo(String.valueOf(selecteStock));
                        tv_modify_down.setText("-" + String.valueOf(selecteStock));
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (selecteStock == 0) {
                            Toast.makeText(mContext, "请选择补货数量.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // 重置
                        selecteStock = 0;
                    }
                }).setNegativeButton("取消", null).show();
    }
}
