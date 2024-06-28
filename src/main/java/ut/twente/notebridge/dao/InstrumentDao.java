package ut.twente.notebridge.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import ut.twente.notebridge.model.PersonInstrument;
import ut.twente.notebridge.utils.DatabaseConnection;
import ut.twente.notebridge.utils.Security;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * This class is used to interact with the database for the Instrument model.
 */
public enum InstrumentDao {
    /**
     * The instance of the InstrumentDao to achieve the singleton pattern.
     */
    INSTANCE;

    /**
     * Returns a list of all instruments in the database.
     *
     * @return all instruments in the database as a JSON string
     */
    public String getInstruments() throws SQLException, JsonProcessingException {
        String sql = """
                SELECT jsonb_agg(name) FROM instrument
                """;

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                String json = rs.getString(1);
                return json;
            } else {
                throw new SQLException("No instruments found");
            }
        }
    }

    /**
     * Returns a list of all associated instruments for which a person has an interest in the database.
     *
     * @param personId The id of the person
     * @return all instruments for a person in the database as a JSON string
     */
    public List<PersonInstrument> getPersonInstruments(int personId) throws SQLException, JsonProcessingException {
        String sql = """
                SELECT jsonb_agg(jsonb_build_object('personId',personid,'instrumentName'
                ,instrumentname,'yearsOfExperience',yearsofexperience))
                                
                 FROM personinstrument WHERE personid=?;
                """;

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setInt(1, personId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                String json = rs.getString(1);
                if (json == null) {
                    return List.of();
                }
                ObjectMapper mapper = JsonMapper.builder()
                        .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                        .build();
                return mapper.readValue(json, new TypeReference<List<PersonInstrument>>() {
                });
            } else {
                throw new SQLException("No instruments found for personId");
            }
        }
    }

    /**
     * Adds a person-instrument relationship to the database.
     *
     * @param personInstrument The person-instrument to be added
     * @return The added person-instrument
     */
    public PersonInstrument addPersonInstrument(PersonInstrument personInstrument) throws SQLException {
        String sql = """
                			INSERT INTO personinstrument(personid,instrumentname,yearsofexperience)
                			VALUES(?,?,?)
                			ON CONFLICT (personid, instrumentname)
                		    DO UPDATE SET yearsofexperience = EXCLUDED.yearsofexperience
                """;


        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setInt(1, personInstrument.getPersonId());
            statement.setString(2, Security.sanitizeInput(personInstrument.getInstrumentName()));
            statement.setDouble(3, personInstrument.getYearsOfExperience());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Failed to attach instrument to a person");
            } else {
                return personInstrument;
            }
        } catch (SQLException e) {
            throw new SQLException("Failed to attach instrument to a person", e);
        }
    }

    /**
     * Deletes a person-instrument relationship from the database.
     *
     * @param instrument The person-instrument to be deleted
     */
    public void deletePersonInstrument(PersonInstrument instrument) {
        String sql = "DELETE FROM personinstrument WHERE personid=? AND instrumentname=?";

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)) {
            statement.setInt(1, instrument.getPersonId());
            statement.setString(2, Security.sanitizeInput(instrument.getInstrumentName()));
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not delete person instrument.");
        }
    }

}
