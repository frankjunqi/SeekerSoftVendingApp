package com.seekersoftvendingapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seekersoftvendingapp.database.table.Note;
import com.seekersoftvendingapp.database.table.Passage;

import java.util.ArrayList;
import java.util.List;

public class ManagerPassageAdapter extends RecyclerView.Adapter<ManagerPassageAdapter.ManagerPassageViewHolder> {

    private ManagerPassageClickListener clickListener;
    private List<Passage> dataset;

    public interface ManagerPassageClickListener {
        void onNoteClick(int position);
    }

    static class ManagerPassageViewHolder extends RecyclerView.ViewHolder {

        public TextView text;
        public TextView comment;

        public ManagerPassageViewHolder(View itemView, final ManagerPassageClickListener clickListener) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.textViewNoteText);
            comment = (TextView) itemView.findViewById(R.id.textViewNoteComment);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        clickListener.onNoteClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    public ManagerPassageAdapter(ManagerPassageClickListener clickListener) {
        this.clickListener = clickListener;
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
                .inflate(R.layout.test_item_note, parent, false);
        return new ManagerPassageViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(ManagerPassageViewHolder holder, int position) {
        Passage passage = dataset.get(position);
        holder.text.setText("货道序号：" + String.valueOf(passage.getSeqNo()));
        holder.comment.setText("最大库存: " + String.valueOf(passage.getCapacity()) + "; 实际库存: " + String.valueOf(passage.getStock()));
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
