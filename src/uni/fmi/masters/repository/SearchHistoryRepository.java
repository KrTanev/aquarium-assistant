package uni.fmi.masters.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.type.TypeReference;
import uni.fmi.masters.DBManager;
import uni.fmi.masters.model.SearchHistoryEntry;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchHistoryRepository {

    private final ObjectMapper objectMapper;

    public SearchHistoryRepository() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public boolean saveSearchHistory(SearchHistoryEntry entry) {
        String sql = "INSERT INTO search_history(user_id, search_params_json) VALUES(?,?)";
        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchParamsJson = objectMapper.writeValueAsString(entry.getSearchParams());
            pstmt.setInt(1, entry.getUserId());
            pstmt.setString(2, searchParamsJson);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Database error saving search history: " + e.getMessage());
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            System.err.println("JSON processing error saving search history: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<SearchHistoryEntry> getSearchHistoryByUserId(int userId) {
        List<SearchHistoryEntry> history = new ArrayList<>();
        String sql = "SELECT id, user_id, search_params_json, timestamp FROM search_history WHERE user_id = ? ORDER BY timestamp DESC";
        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String searchParamsJson = rs.getString("search_params_json");
                
                Timestamp sqlTimestamp = rs.getTimestamp("timestamp");
                LocalDateTime timestamp = (sqlTimestamp != null) ? sqlTimestamp.toLocalDateTime() : null;

                Map<String, Object> searchParams = null;
                try {
                    searchParams = objectMapper.readValue(searchParamsJson,
                            new TypeReference<Map<String, Object>>() {
                            });
                } catch (IOException e) {
                    System.err.println("JSON processing error retrieving search history: " + e.getMessage());
                    e.printStackTrace();
                    continue;
                }

                history.add(new SearchHistoryEntry(id, userId, searchParams, timestamp));
            }
        } catch (SQLException e) {
            System.err.println("Database error retrieving search history: " + e.getMessage());
            e.printStackTrace();
        }
        return history;
    }
}