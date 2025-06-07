package uni.fmi.masters.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class SearchHistoryEntry {
    private int id;
    private int userId;
    private Map<String, Object> searchParams;
    private LocalDateTime timestamp;

    public SearchHistoryEntry(int id, int userId, Map<String, Object> searchParams, LocalDateTime timestamp) {
        this.id = id;
        this.userId = userId;
        this.searchParams = searchParams;
        this.timestamp = timestamp;
    }

    public SearchHistoryEntry(int userId, Map<String, Object> searchParams) {
        this(-1, userId, searchParams, LocalDateTime.now());
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public Map<String, Object> getSearchParams() {
        return searchParams;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setSearchParams(Map<String, Object> searchParams) {
        this.searchParams = searchParams;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("Search at ").append(timestamp.format(formatter)).append(":\n");
        searchParams.forEach((key, value) -> {
            if ("alreadyHaveFish".equals(key) && value instanceof List) {
                sb.append("  Already Have Fish: ").append(String.join(", ", (List<String>) value)).append("\n");
            } else if (!"numFishKinds".equals(key)) {
                sb.append("  ").append(key).append(": ").append(value).append("\n");
            }
        });
        return sb.toString();
    }
}