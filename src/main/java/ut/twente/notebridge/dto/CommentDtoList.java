package ut.twente.notebridge.dto;

import java.util.List;

/**
 * This class is used at retrieving all comments of a given posts with tailored information about the sender of the comments.
 */
public class CommentDtoList {

    /**
     * The list of comments.
     */
    private List<CommentDto> comments;

    /**
     * Default constructor for CommentDtoList.
     */
    public CommentDtoList() {
    }

    /**
     * getComments method returns the list of comments.
     */
    public List<CommentDto> getComments() {
        return comments;
    }

    /**
     * setComments method sets the list of comments.
     *
     * @param comments The list of comments
     */
    public void setComments(List<CommentDto> comments) {
        this.comments = comments;
    }
}
