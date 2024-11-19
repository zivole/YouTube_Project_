package com.example.youtube_android.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.youtube_android.R;
import com.example.youtube_android.entities.Comment;
import com.example.youtube_android.entities.VideoItem;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private  List<Comment> comments;

    public CommentAdapter(List<Comment> comments) {
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.usernameTextView.setText(comment.getUserName());
        holder.commentTextView.setText(comment.getContent());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView commentTextView;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
        }
    }
    public void updateCommentList(List<Comment> newCommentsList) {
        comments = newCommentsList;
        Log.d("VideoAdapter", "Video list updated with " + comments.size() + " items");
        notifyDataSetChanged();
    }
    public void addComment(Comment newComment) {
        comments.add(newComment);
        notifyItemInserted(comments.size() - 1);
}
    public void removeComment(int index) {
        if (index >= 0 && index < comments.size()) {
            comments.remove(index);
            notifyItemRemoved(index);
        } else {
            Log.e("CommentAdapter", "Invalid index: " + index);
        }
    }
    public void updateComment(Comment updatedComment) {
        int index = comments.indexOf(updatedComment);
        if (index >= 0) {
            comments.set(index, updatedComment);
            notifyItemChanged(index);
        }
    }

}
