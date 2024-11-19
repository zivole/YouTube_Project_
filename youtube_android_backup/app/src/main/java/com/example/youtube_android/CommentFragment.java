package com.example.youtube_android;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.youtube_android.adapters.CommentAdapter;
import com.example.youtube_android.entities.Comment;
import com.example.youtube_android.entities.User;
import com.example.youtube_android.viewModels.CommentsViewModel;

import java.util.ArrayList;
import java.util.List;

public class CommentFragment extends Fragment {

    private EditText editTextComment;
    private RecyclerView recyclerView;
    private CommentAdapter adapter;
    private List<Comment> comments;
    private ImageButton backButton;
    private ImageButton addButton;
    private ImageButton editButton;
    private ImageButton deleteButton;
    private String videoId;
    private int selectedIndex = -1;
    private User currentUser;
    private CommentsViewModel commentsViewModel;
    private String token;

    public CommentFragment() {
    }

    public CommentFragment(String videoId, User currentUser, String token) {
        this.videoId = videoId;
        this.currentUser = currentUser;
        this.token = token;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.comments_fragment, container, false);

        editTextComment = view.findViewById(R.id.editTextComment);
        recyclerView = view.findViewById(R.id.recyclerView);
        backButton = view.findViewById(R.id.backButton);
        addButton = view.findViewById(R.id.addButton);
        editButton = view.findViewById(R.id.editButton);
        deleteButton = view.findViewById(R.id.deleteButton);


        comments = new ArrayList<>();

        commentsViewModel = new ViewModelProvider(this).get(CommentsViewModel.class);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        adapter = new CommentAdapter(comments);
        recyclerView.setAdapter(adapter);

        // Fetch comments
        commentsViewModel.getCommentsByVideoId(videoId).observe(getViewLifecycleOwner(), fetchedComments -> {
            if (fetchedComments != null) {
                comments.clear();
                comments.addAll(fetchedComments);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "No comments found for this video.", Toast.LENGTH_SHORT).show();
            }
        });

        backButton.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().beginTransaction().remove(CommentFragment.this).commit();
        });

        addButton.setOnClickListener(v -> {
            if (currentUser != null) {
                String commentText = editTextComment.getText().toString().trim();
                if (!commentText.isEmpty()) {
                    Comment newComment = Comment.createNewComment(commentText, currentUser.getUsername(), videoId);

                    // Create comment and observe for success or failure
                    commentsViewModel.createComment(token, newComment).observe(getViewLifecycleOwner(), createdComment -> {
                        if (createdComment != null) {
                            adapter.updateCommentList(createdComment);
                            editTextComment.setText("");  // Clear the text field
                            Toast.makeText(getContext(), "Comment added", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to add comment.", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(getContext(), "Comment cannot be empty", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "You need to be logged in to comment.", Toast.LENGTH_SHORT).show();
            }
        });

        deleteButton.setOnClickListener(v -> {
            if (selectedIndex != -1) {
                Comment selectedComment = comments.get(selectedIndex);
                if (currentUser != null && selectedComment.getUserName().equals(currentUser.getUsername())) {
                    commentsViewModel.deleteComment(token, selectedComment.get_id()).observe(getViewLifecycleOwner(), isDeleted -> {
                        if (Boolean.TRUE.equals(isDeleted)) {
                            getActivity().runOnUiThread(() -> {
                                adapter.removeComment(selectedIndex);
                                editTextComment.setText("");
                                selectedIndex = -1;
                                Toast.makeText(getContext(), "Comment deleted", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            Toast.makeText(getContext(), "Failed to delete comment.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "You can only delete your own comments", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Please select a comment to delete", Toast.LENGTH_SHORT).show();
            }
        });

        editButton.setOnClickListener(v -> {
            if (currentUser != null) {
                String updatedCommentText = editTextComment.getText().toString().trim();

                if (!updatedCommentText.isEmpty() && selectedIndex != -1 && selectedIndex < comments.size()) {
                    Comment selectedComment = comments.get(selectedIndex);

                    if (selectedComment.getUserName().equals(currentUser.getUsername())) {
                        selectedComment.setContent(updatedCommentText);

                        // Update comment and observe for success or failure
                        commentsViewModel.updateComment(token, selectedComment).observe(getViewLifecycleOwner(), success -> {
                            if (Boolean.TRUE.equals(success)) {
                                adapter.updateComment(selectedComment); // Update the comment in the adapter
                                editTextComment.setText("");  // Clear the text field
                                selectedIndex = -1;  // Reset the selected index
                                Toast.makeText(getContext(), "Comment updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Failed to update comment.", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        Toast.makeText(getContext(), "You can only edit your own comments.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Please enter a comment and select a valid comment to edit.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "You need to be logged in to edit a comment.", Toast.LENGTH_SHORT).show();
            }
        });



        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (position >= 0 && position < comments.size()) {
                            selectedIndex = position;
                            editTextComment.setText(comments.get(position).getContent());
                        } else {
                            Toast.makeText(getContext(), "Selected comment does not exist.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // Do nothing
                    }
                })
        );

        return view;
    }
}