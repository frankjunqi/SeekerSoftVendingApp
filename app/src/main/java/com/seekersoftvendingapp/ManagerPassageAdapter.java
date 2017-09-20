package com.seekersoftvendingapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
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

public class ManagerPassageAdapter extends RecyclerView.Adapter<ManagerPassageAdapter.ManagerPassageViewHolder> implements View.OnClickListener {

    private List<Passage> dataset;
    private Context mContext;

    private OnItemClickListener mOnItemClickListener = null;

    //define interface
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_modify_stock, parent, false);
        view.setOnClickListener(this);
        return new ManagerPassageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ManagerPassageViewHolder holder, final int position) {
        Passage passage = dataset.get(position);
        holder.tv_passageid.setText(String.valueOf(passage.getSeqNo()));
        holder.tv_productname.setText(String.valueOf(passage.getKeepone()));
        if (passage.getCapacity() - passage.getStock() <= 0) {
            holder.tv_modify_num.setText("0");
        } else {
            holder.tv_modify_num.setText(String.valueOf(passage.getCapacity() - passage.getStock()));
        }
        holder.tv_modify_down.setText(passage.getKeeptwo());
        holder.tv_modify_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertRadioListDialog(holder.tv_modify_down, getPassage(position));
            }
        });
        holder.itemView.setTag(position);
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


        /*final AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("货道补货");
        alertDialog.setSingleChoiceItems(intlist, currentStock - 1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selecteStock = which + 1;
            }
        });
        alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (selecteStock == 0) {
                    Toast.makeText(mContext, "请选择补货数量.", Toast.LENGTH_SHORT).show();
                    return;
                }
                passage.setKeeptwo(String.valueOf(selecteStock));
                tv_modify_down.setText("-" + String.valueOf(selecteStock));
                // 重置
                selecteStock = 0;
            }
        });
        alertDialog.setNegativeButton("取消", null).show();*/
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("货道补货");
        /**
         * 1、public Builder setItems(int itemsId, final OnClickListener
         * listener) itemsId表示字符串数组的资源ID，该资源指定的数组会显示在列表中。 2、public Builder
         * setItems(CharSequence[] items, final OnClickListener listener)
         * items表示用于显示在列表中的字符串数组
         */
        builder.setItems(intlist, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selecteStock = which + 1;
                if (selecteStock == 0) {
                    Toast.makeText(mContext, "请选择补货数量.", Toast.LENGTH_SHORT).show();
                    return;
                }
                passage.setKeeptwo(String.valueOf(selecteStock));
                tv_modify_down.setText("-" + String.valueOf(selecteStock));
                // 重置
                selecteStock = 0;
            }
        });
        builder.create().show();
    }
}
