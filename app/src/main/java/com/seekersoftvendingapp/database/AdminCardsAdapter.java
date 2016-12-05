package com.seekersoftvendingapp.database;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seekersoftvendingapp.R;
import com.seekersoftvendingapp.database.table.AdminCard;

import java.util.ArrayList;
import java.util.List;

public class AdminCardsAdapter extends RecyclerView.Adapter<AdminCardsAdapter.AdminCardViewHolder> {

    private AdminCardClickListener clickListener;
    private List<AdminCard> dataset;

    public interface AdminCardClickListener {
        void onNoteClick(int position);
    }

    static class AdminCardViewHolder extends RecyclerView.ViewHolder {

        public TextView text;
        public TextView comment;

        public AdminCardViewHolder(View itemView, final AdminCardClickListener clickListener) {
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

    public AdminCardsAdapter(AdminCardClickListener clickListener) {
        this.clickListener = clickListener;
        this.dataset = new ArrayList<AdminCard>();
    }

    public void setNotes(@NonNull List<AdminCard> notes) {
        dataset = notes;
        notifyDataSetChanged();
    }

    public AdminCard getNote(int position) {
        return dataset.get(position);
    }

    @Override
    public AdminCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.test_item_note, parent, false);
        return new AdminCardViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(AdminCardViewHolder holder, int position) {
        AdminCard note = dataset.get(position);
        holder.text.setText(note.getCard());
        holder.comment.setText(note.getCreatedAt().toString());
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
