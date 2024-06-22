package ut.twente.notebridge.dao;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import ut.twente.notebridge.utils.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public enum InterestDao {
    INSTANCE;

    public Boolean isInterested(int postId, int personId) {
        String sql = """
            SELECT EXISTS(SELECT* FROM personinterestedinpost WHERE postid=? AND personid=?);
        """;

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setInt(1, postId);
            statement.setInt(2, personId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getBoolean(1);
            }else{
                throw new SQLException();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while checking if post is liked");
        }

    }

    public int getTotalInterest(int postId) {
        String sql = """
            SELECT COUNT(*) FROM personinterestedinpost WHERE postid=? GROUP BY postid;
        """;

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setInt(1, postId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }else{
                return 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while getting total likes");
        }

    }

    public String getInterestedUsernames(int postId) {
        String sql = """
            SELECT jsonb_agg(jsonb_build_object('id',b.id,'username',b.username))
                        FROM personinterestedinpost i, baseuser b
                        WHERE i.personid=b.id AND i.postid=?
        """;

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setInt(1, postId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                String json = rs.getString(1);
                ObjectMapper mapper = JsonMapper.builder()
                        .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                        .build();
                return json;
            }else{
                throw new SQLException();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error while getting total likes");
        }

    }
}
