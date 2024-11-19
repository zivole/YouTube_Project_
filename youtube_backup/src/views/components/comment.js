import React, { useState } from 'react';
import { FaEdit, FaTrash, FaSave, FaUndo } from 'react-icons/fa';
import '../styles/comment.css'; // make sure to import your css file

const Comment = ({ comment, onDelete, onEdit, currentUser }) => {
    const [isEditing, setIsEditing] = useState(false);
    const [editedComment, setEditedComment] = useState(comment.content);

    const handleEdit = () => {
        setIsEditing(true);
    };

    const handleSave = async () => {
        await onEdit(editedComment);
        setIsEditing(false);
    };

    const handleCancel = () => {
        setEditedComment(comment.content);
        setIsEditing(false);
    };

    return (
        <div className="comment">
            <div className="comment-content">
                <strong>{comment.userId.username +":" || 'Unknown User'}</strong>
                {isEditing ? (
                    <input
                        type="text"
                        value={editedComment}
                        onChange={(e) => setEditedComment(e.target.value)}
                        className="form-control me-2"
                    />
                ) : (
                    <span>{comment.content}</span>
                )}
            </div>
            {currentUser && currentUser.username === comment.userId.username && (
                <div className="comment-actions">
                    {isEditing ? (
                        <>
                            <button className="btn btn-outline-danger me-2" onClick={handleSave}>
                                <FaSave />
                            </button>
                            <button className="btn btn-outline-danger me-2" onClick={handleCancel}>
                                <FaUndo />
                            </button>
                        </>
                    ) : (
                        <>
                            <button className="btn btn-outline-danger me-2" onClick={handleEdit}>
                                <FaEdit />
                            </button>
                            <button className="btn btn-outline-danger me-2" onClick={onDelete}>
                                <FaTrash />
                            </button>
                        </>
                    )}
                </div>
            )}
        </div>
    );
};

export default Comment;
