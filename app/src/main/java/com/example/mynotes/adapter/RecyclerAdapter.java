package com.example.mynotes.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mynotes.R;
import com.example.mynotes.helper.ItemTouchHelperAdapter;
import com.example.mynotes.helper.OnSwipeListener;
import com.example.mynotes.model.Note;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private List<Note> notes;
    private Context context;
    OnItemClickListener itemClickListener;
    OnSwipeListener swipeListener;

    public RecyclerAdapter(ArrayList<Note> notes, Context context, OnItemClickListener itemClickListener , OnSwipeListener swipeListener) {
        this.notes = notes;
        this.context = context;
        this.itemClickListener = itemClickListener;
        this.swipeListener = swipeListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.title.setText(note.getText());
        holder.date.setText(note.getDate());
        if (note.isDoing())
            holder.done.setVisibility(View.VISIBLE);
        else
            holder.done.setVisibility(View.GONE);

        if (note.isDone())
            holder.done.setImageResource(R.drawable.ic_done);
        else
            holder.done.setVisibility(View.GONE);

        if (note.isMarked())
            holder.bookMark.setImageResource(R.drawable.ic_bookmark);
        else
            holder.bookMark.setImageResource(R.drawable.ic_not_bookmark);
    }

    @Override
    public void onItemDismiss(int position) {
        swipeListener.onSwipe(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void setNotes(ArrayList<Note> notes) {
        this.notes = notes;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.done)
        ImageView done;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.bookMark)
        ImageView bookMark;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onItemClick(getAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    itemClickListener.onItemLongClick(getAdapterPosition());
                    return true;
                }
            });
            bookMark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onItemBookMarkClick(getAdapterPosition());
                }
            });
        }

    }
}
