package ut.twente.notebridge.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import ut.twente.notebridge.dto.PostDto;
import ut.twente.notebridge.model.Interest;
import ut.twente.notebridge.model.Like;
import ut.twente.notebridge.utils.DatabaseConnection;
import ut.twente.notebridge.utils.Security;
import ut.twente.notebridge.utils.Utils;
import ut.twente.notebridge.model.Post;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * This class is used to interact with the database for the Post model.
 */
public enum PostDao {
    /**
     * The instance of the PostDao to achieve the singleton pattern.
     */
    INSTANCE;

    /**
     * Deletes a post from the database.
     *
     * @param id The id of the post to be deleted
     */
    public void delete(int id) {
        String sql = "DELETE FROM post WHERE id=?"; // Assuming delete_post takes one parameter

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not delete post.");
        }
    }

    /**
     * Deletes all posts from the database.
     * Testing purposes only.
     */
    public void deleteAll() {
        String sql = "DELETE FROM post"; // Assuming delete_post takes one parameter

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            int affectedRows = statement.executeUpdate();
            System.out.println("Deleted " + affectedRows + " posts");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while deleting all posts");
        }
    }

    /**
     * Gets all posts from the database.
     * Supports the use of pagination, searching, filtering and sorting
     *
     * @return A list of all posts
     */
    public List<PostDto> getPosts(int pageSize, int pageNumber, String sortBy, Integer personId, String search, String filterBy, Integer sponsorId, Boolean isSponsored,StringBuilder returnQuery) {
        System.out.println("GET posts called");
        List<PostDto> list = new ArrayList<>();

        List<String> sortableColumns = Arrays.asList("oldest", "latest", "mostinterested", "mostlikes");

        StringBuilder sqlBuilder = new StringBuilder("SELECT json_agg(t) FROM (SELECT *FROM postdetailed\n");


        String tsquery = "";

        boolean isSearchGiven = search != null && !search.isEmpty() && !search.equals("undefined");
        boolean isPersonIdGiven = personId != null && personId > 0;
        boolean isSponsorIdGiven = sponsorId != null && sponsorId > 0;
        boolean isFilterByGiven = filterBy != null && !filterBy.isEmpty();
        boolean isSortByGiven = sortBy != null && !sortBy.isEmpty() && sortableColumns.contains(sortBy);
        boolean isSponsoredGiven= isSponsored!= null && isSponsored;
        if (isPersonIdGiven) {
            sqlBuilder.append("WHERE personId=?\n");

        }
        if (isFilterByGiven) {
            if (isPersonIdGiven) {
                sqlBuilder.append("AND\n");
            } else {
                sqlBuilder.append("WHERE ");
            }

            if (filterBy.equals("jam-session")) {
                sqlBuilder.append("eventType='Jam Session'\n");
            } else if (filterBy.equals("live-event")) {
                sqlBuilder.append("eventType='Live Event'\n");
            } else if (filterBy.equals("find-band-member")) {
                sqlBuilder.append("eventType='Find Band Member'\n");
            } else if (filterBy.equals("find-instrument")) {
                sqlBuilder.append("eventType='Find Instrument'\n");
            } else if (filterBy.equals("music-discussion")) {
                sqlBuilder.append("eventType='Music Discussion'\n");
            }

        }
        if (isSponsorIdGiven) {
            if (isPersonIdGiven || isFilterByGiven) {
                sqlBuilder.append("AND\n");
            } else {
                sqlBuilder.append("WHERE ");
            }
            sqlBuilder.append("sponsoredBy=?\n");
        }
        if(isSponsoredGiven){
            if (isPersonIdGiven || isFilterByGiven || isSponsorIdGiven) {
                sqlBuilder.append("AND\n");
            } else {
                sqlBuilder.append("WHERE ");
            }
            sqlBuilder.append("sponsoredBy IS NOT NULL\n");
        }

        if (!isSearchGiven) {

            if (!isSortByGiven) {
                sortBy = "ORDER BY createDate DESC\n";
            } else {
                //sortBy is given, search cannot exist at the same time(set in frontend)
                if (sortBy.equals("oldest")) {
                    sortBy = "ORDER BY createDate ASC\n";
                } else if (sortBy.equals("latest")) {
                    sortBy = "ORDER BY createDate DESC\n";
                } else if (sortBy.equals("mostinterested")) {
                    sortBy = "ORDER BY totalinterested DESC\n";
                } else if (sortBy.equals("mostlikes")) {
                    sortBy = "ORDER BY totallikes DESC\n";
                }
            }
            sqlBuilder.append(sortBy);


        } else {
            //Search is given
            if (isPersonIdGiven || isFilterByGiven || isSponsorIdGiven || isSponsoredGiven) {
                sqlBuilder.append("AND\n");
            } else {
                sqlBuilder.append("WHERE ");
            }
            sqlBuilder.append("to_tsvector(title || ' ' || description || ' ' || location || ' ' || eventtype) @@ to_tsquery(?)\n");
            sqlBuilder.append("ORDER BY ts_rank(to_tsvector(title || ' ' || description || ' ' || location || ' ' || eventtype), to_tsquery(?)) DESC\n");
            tsquery = String.join("&", Arrays.asList(Security.sanitizeInput(search).split(" ")));

        }
        sqlBuilder.append("LIMIT ?\nOFFSET ?) t;");

        String query = sqlBuilder.toString();
        int parameterIndex = 1;
        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(query)) {
            if (isPersonIdGiven) {
                //PersonID is provided
                statement.setInt(parameterIndex, personId);
                parameterIndex++;

            }
            if (isSponsorIdGiven) {
                statement.setInt(parameterIndex, sponsorId);
                parameterIndex++;
            }
            if (isSearchGiven) {
                statement.setString(parameterIndex, tsquery);
                parameterIndex++;
                statement.setString(parameterIndex, tsquery);
                parameterIndex++;

            }
            statement.setInt(parameterIndex, pageSize);
            parameterIndex++;
            statement.setInt(parameterIndex, (pageNumber - 1) * pageSize);

            //removing LIMIT AND OFFSET BEFORE RETURNING for COUNT
            String statementWithoutLimitOffset = statement.toString().substring(0, statement.toString().indexOf("LIMIT"));
            statementWithoutLimitOffset += ") t;";
            returnQuery.append(statementWithoutLimitOffset);


            ResultSet rs = statement.executeQuery();
            ObjectMapper mapper = JsonMapper.builder().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true).build();
            if (rs.next() && rs.getString("json_agg") != null) {
                list = Arrays.asList(mapper.readValue(rs.getString("json_agg"), PostDto[].class));
                for (PostDto post : list) {
                    post.setImage(getFirstImage(post.getId()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not get posts.");
        }
        return list;
    }

    /**
     * Returns a list of all sponsored posts that are valid within the sponsoredUntil date in the database.
     *
     * @return The list of all valid sponsored posts
     */
    public List<PostDto> getSponsoredPosts() {
        String sql = """
                SELECT json_agg(t) FROM 
                                   (SELECT *FROM postdetailed 
                                        WHERE sponsoredBy IS NOT NULL
                                    AND sponsoredUntil >= NOW()) t;
                """;
        try {
            PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            ObjectMapper mapper = JsonMapper.builder().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true).build();
            if (rs.next() && rs.getString("json_agg") != null) {
                List<PostDto> list = Arrays.asList(mapper.readValue(rs.getString("json_agg"), PostDto[].class));
                for (PostDto post : list) {
                    post.setImage(getFirstImage(post.getId()));
                }
                return list;
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not get sponsored posts.");
        }

    }

    /**
     * Returns a post from the database.
     *
     * @param id The id of the requested post
     * @return The requested post
     */
    public Post getPost(int id) {
        String sql = "SELECT row_to_json(t) post FROM(SELECT * FROM Post WHERE id=?) t";

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String json = rs.getString("post");

                ObjectMapper mapper = JsonMapper.builder().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true).build();
                return mapper.readValue(json, Post.class);

            } else {
                //no rows returned, post with that id does not exist
                throw new NotFoundException("Post '" + id + "' not found.");
            }

        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while getting post.");
        }
    }

    /**
     * If the user has liked the post, the like is removed. If the user has not liked the post, the like is added.
     *
     * @param like The like to be added or removed
     * @return The like that was added or removed
     */
    public Like toggleLike(Like like) {
        String sqlDoAction = "";
        String sqlCheck = """
                	SELECT EXISTS(SELECT personid,postid FROM personlikespost WHERE personid=? AND postid=?);
                """;
        try (PreparedStatement statementCheck = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sqlCheck)) {
            statementCheck.setInt(1, like.getPersonId());
            statementCheck.setInt(2, like.getPostId());
            ResultSet rs = statementCheck.executeQuery();
            if (rs.next()) {
                if (rs.getBoolean(1)) {
                    sqlDoAction = """
                            DELETE FROM personlikespost WHERE personid=? AND postid=?;
                            """;
                } else {
                    sqlDoAction = """
                            INSERT INTO personlikespost (personid, postid) VALUES (?, ?);
                            """;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while checking if user is a person");
        }

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sqlDoAction)) {
            statement.setInt(1, like.getPersonId());
            statement.setInt(2, like.getPostId());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 1) {
                return like;
            } else {
                throw new BadRequestException("Like failed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while liking post");
        }
    }

    /**
     * If the user has shown interest in the post, the interest is removed. If the user has not shown interest in the post, the interest is added.
     *
     * @param interest The interest to be added or removed
     * @return The interest that was added or removed
     */
    public Interest toggleInterest(Interest interest) {
        String sqlDoAction = "";
        String sqlCheck = """
                	SELECT EXISTS(SELECT personid,postid FROM personinterestedinpost WHERE personid=? AND postid=?);
                """;
        try (PreparedStatement statementCheck = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sqlCheck)) {
            statementCheck.setInt(1, interest.getPersonId());
            statementCheck.setInt(2, interest.getPostId());
            ResultSet rs = statementCheck.executeQuery();
            if (rs.next()) {
                if (rs.getBoolean(1)) {
                    sqlDoAction = """
                            DELETE FROM personinterestedinpost WHERE personid=? AND postid=?;
                            """;
                } else {
                    sqlDoAction = """
                            INSERT INTO personinterestedinpost (personid, postid) VALUES (?, ?);
                            """;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while checking if user is a person");
        }

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sqlDoAction)) {
            statement.setInt(1, interest.getPersonId());
            statement.setInt(2, interest.getPostId());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 1) {
                return interest;
            } else {
                throw new BadRequestException("Like failed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while liking post");
        }
    }

    /**
     * Creates a post in the database.
     *
     * @param newPost The post to be created
     * @return The created post
     */
    public Post create(Post newPost) {
        String sql = """
                		INSERT INTO Post (createDate,
                		lastUpdate, personId,
                		title, description,
                		sponsoredBy, sponsoredFrom,
                		sponsoredUntil, eventType, location)
                		VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
                """;


        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            Timestamp currentTime = Timestamp.from(Instant.now());
            newPost.setCreateDate(currentTime);
            newPost.setLastUpdate(currentTime);
            statement.setTimestamp(1, currentTime);
            statement.setTimestamp(2, currentTime);
            statement.setInt(3, newPost.getPersonId());
            statement.setString(4, Security.sanitizeInput(newPost.getTitle()));
            if (newPost.getDescription() == null) {
                statement.setNull(5, java.sql.Types.VARCHAR);
            } else {
                statement.setString(5, Security.sanitizeInput(newPost.getDescription()));
            }
            if (newPost.getSponsoredBy() == null) {
                statement.setNull(6, java.sql.Types.INTEGER);
            } else {
                statement.setInt(6, newPost.getSponsoredBy());
            }
            if (newPost.getSponsoredFrom() == null) {
                statement.setNull(7, java.sql.Types.TIMESTAMP);
            } else {
                statement.setTimestamp(7, newPost.getSponsoredFrom());
            }
            if (newPost.getSponsoredUntil() == null) {
                statement.setNull(8, java.sql.Types.TIMESTAMP);
            } else {
                statement.setTimestamp(8, newPost.getSponsoredUntil());
            }
            statement.setString(9, Security.sanitizeInput(newPost.getEventType()));
            if (newPost.getLocation() == null) {
                statement.setNull(10, java.sql.Types.VARCHAR);
            } else {
                statement.setString(10, Security.sanitizeInput(newPost.getLocation()));
            }

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating post failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    newPost.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating post failed, no ID obtained.");
                }
            }
            return newPost;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Creating post failed, no ID obtained.");
        }
    }

    /**
     * Returns a list of all posts from a person in the database.
     *
     * @param personId The id of the person
     * @return The list of all posts from the person
     */
    public List<Post> getPostsByPersonId(int personId) {
        String sql = """
                SELECT json_agg(post) FROM post WHERE personId=?
                """;

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setInt(1, personId);
            ResultSet rs = statement.executeQuery();
            ObjectMapper mapper = JsonMapper.builder().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true).build();
            if (rs.next()) {
                return Arrays.asList(mapper.readValue(rs.getString("json_agg"), Post[].class));
            } else {
                throw new NotFoundException("No posts found for person with id " + personId);
            }
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while getting posts");
        }
    }

    /**
     * Updates a post in the database.
     *
     * @param updatedPost The updated post
     * @return The updated post
     */
    public Post update(Post updatedPost) {
        String sql = """
                		UPDATE Post
                		SET lastUpdate = ?,
                		title = ?,
                		description = ?,
                		location = ?
                		WHERE id = ?;
                """;


        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            Timestamp currentTime = Timestamp.from(Instant.now());
            statement.setTimestamp(1, currentTime);
            if (updatedPost.getTitle() == null || updatedPost.getTitle().isEmpty()) {
                throw new BadRequestException("Title cannot be empty");
            } else {
                statement.setString(2, Security.sanitizeInput(updatedPost.getTitle()));
            }
            if (updatedPost.getDescription() == null) {
                statement.setNull(3, java.sql.Types.VARCHAR);
            } else {
                statement.setString(3, Security.sanitizeInput(updatedPost.getDescription()));
            }
            if (updatedPost.getLocation() == null) {
                statement.setNull(4, java.sql.Types.VARCHAR);
            } else {
                statement.setString(4, Security.sanitizeInput(updatedPost.getLocation()));
            }
            statement.setInt(5, updatedPost.getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating post failed, no rows affected.");
            }
            return updatedPost;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while updating post");
        }
    }

    /**
     * Puts a sponsorship on a post in the database.
     *
     * @param updatedPost The post to be sponsored
     * @return The updated post
     */
    public Post updateSponsorship(Post updatedPost) {
        String sql = """
                		UPDATE Post
                		SET lastUpdate = ?,
                  			sponsoredBy = ?,
                			sponsoredFrom = ?,
                			sponsoredUntil = ?
                		WHERE id = ?;
                """;

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            Timestamp currentTime = Timestamp.from(Instant.now());
            statement.setTimestamp(1, currentTime);
            if (updatedPost.getSponsoredBy() == null) {
                statement.setNull(2, java.sql.Types.INTEGER);
            } else {
                statement.setInt(2, updatedPost.getSponsoredBy());
            }
            if (updatedPost.getSponsoredFrom() == null) {
                statement.setNull(3, java.sql.Types.TIMESTAMP);
            } else {
                statement.setTimestamp(3, updatedPost.getSponsoredFrom());
            }
            if (updatedPost.getSponsoredUntil() == null) {
                statement.setNull(4, java.sql.Types.TIMESTAMP);
            } else {
                statement.setTimestamp(4, updatedPost.getSponsoredUntil());
            }
            statement.setInt(5, updatedPost.getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating post failed, no rows affected.");
            }
            return updatedPost;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while updating post");
        }
    }

    /**
     * Extracts the total number of posts from the database.
     *
     * @param sql The sql query to be executed, which is obtained dynamically from the getPosts method in getPosts route.
     * @return The total number of posts
     */
    public int getTotalPosts(String sql) {

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while getting total posts");
        }
    }

    /**
     * Creates images for a post in the database.
     *
     * @param post  The post to which the images belong
     * @param parts The images to be created
     */
    public void createImages(Post post, List<FormDataBodyPart> parts) {
        for (FormDataBodyPart part : parts) {
            InputStream uploadedInputStream = part.getValueAs(InputStream.class);
            String fileName = part.getFormDataContentDisposition().getFileName();
            String uuid = java.util.UUID.randomUUID().toString();
            String uploadedFileLocation = Utils.readFromProperties("PERSISTENCE_FOLDER_PATH") + uuid + fileName;
            System.out.println(uploadedFileLocation);
            // save it
            File objFile = new File(uploadedFileLocation);
            if (objFile.exists()) {
                boolean res = objFile.delete();
            }

            Utils.saveToFile(uploadedInputStream, uploadedFileLocation);

            String sql = """
                    INSERT INTO picture (postid, pictureurl) VALUES (?, ?)
                    """;
            try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
                statement.setInt(1, post.getId());
                statement.setString(2, Security.sanitizeInput(uuid + fileName));
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Could not create image.");
            }
        }
    }

    /**
     * Gets images for a post from the database.
     *
     * @param id The id of the post
     * @return The list of image URL's
     */
    public List<String> getImages(Integer id) {
        String sql = """
                SELECT pictureurl FROM picture WHERE postid=?
                """;

        List<String> pictureUrls = new ArrayList<>();

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String pictureUrl = rs.getString("pictureurl");
                pictureUrls.add(pictureUrl);
            }
            return pictureUrls;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while getting images");
        }
    }

    /**
     * Gets the first image for a post from the database.
     * It is used to display only first images of the posts if there is any in the overview page
     *
     * @param id The id of the post
     * @return The first image as a base64 encoded string
     */
    public String getFirstImage(Integer id) {
        String sql = """
                SELECT pictureurl FROM picture WHERE postid=? LIMIT 1
                """;

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                String imageFile = rs.getString("pictureurl");
                String fileLocation = Utils.readFromProperties("PERSISTENCE_FOLDER_PATH") + imageFile;
                File file = new File(fileLocation);
                if (file.exists()) {
                    try {
                        byte[] fileContent = FileUtils.readFileToByteArray(file);
                        return Base64.getEncoder().encodeToString(fileContent);
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException("Error while encoding image to base64");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while getting first image");
        }
        return null;
    }
}
