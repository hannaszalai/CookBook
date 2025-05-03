package cookbook.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import cookbook.model.Comment;
import cookbook.model.User;

/*
 * Controller class for Comment Editing.
 */
public class CommentEditController {
    private Comment comment;
    private User user;
    private CommentDisplayController commentDisplayController;

    @FXML
    private TextField commentField;
    @FXML
    private Button btnCancel, btnSaveCommentConfirm;

    public void setUser(User user) {
        this.user = user;
    }

    public void setCommentDisplayController(CommentDisplayController commentDisplayController) {
        this.commentDisplayController = commentDisplayController;
    }
}