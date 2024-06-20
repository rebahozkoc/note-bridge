package ut.twente.notebridge.dto;

import java.util.List;

public class CommentDtoList {
    private List<CommentDto> comments;

    public CommentDtoList() {
    }

    public List<CommentDto> getComments() {
        return comments;
    }

    public void setComments(List<CommentDto> comments) {
        this.comments = comments;
    }
}
