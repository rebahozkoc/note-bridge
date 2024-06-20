package ut.twente.notebridge.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.ws.rs.NotFoundException;
import ut.twente.notebridge.dto.CommentDtoList;
import ut.twente.notebridge.model.Instrument;
import ut.twente.notebridge.model.PersonInstrument;
import ut.twente.notebridge.utils.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public enum InstrumentDao {
    INSTANCE;

    public String getInstruments() throws SQLException, JsonProcessingException {
        String sql= """
                SELECT jsonb_agg(name) FROM instrument
                """;

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)){
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                String json = rs.getString(1);
                return json;
            } else {
                throw new SQLException("No instruments found");
            }
        }
    }

    public List<PersonInstrument> getPersonInstruments(int personId) throws SQLException, JsonProcessingException {
        String sql= """
                SELECT jsonb_agg(jsonb_build_object('personId',personid,'instrumentName'
                ,instrumentname,'yearsOfExperience',yearsofexperience))
                
                 FROM personinstrument WHERE personid=?;
                """;

        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)){
            statement.setInt(1, personId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                String json = rs.getString(1);
                if(json==null){
                    throw new NotFoundException("No instruments found for personId");
                }
                ObjectMapper mapper = JsonMapper.builder()
                        .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                        .build();
                return mapper.readValue(json,new TypeReference<List<PersonInstrument>>() {});
            } else {
                throw new SQLException("No instruments found for personId");
            }
        }
    }


    public PersonInstrument addPersonInstrument(PersonInstrument personInstrument) throws SQLException {
        String sql = """
                    INSERT INTO personinstrument(personid,instrumentname,yearsofexperience)
                        VALUES(?,?,?)
                        """;


        try (PreparedStatement statement = DatabaseConnection.INSTANCE.getConnection().prepareStatement(sql)){
            statement.setInt(1, personInstrument.getPersonId());
            statement.setString(2, personInstrument.getInstrumentName());
            statement.setDouble(3, personInstrument.getYearsOfExperience());
            int affectedRows=statement.executeUpdate();
            if(affectedRows==0){
                throw new SQLException("Failed to attach instrument to a person");
            }else{
                return personInstrument;
            }
        }catch(SQLException e){
            throw new SQLException("Failed to attach instrument to a person",e);
        }
    }
}
