package rideshare.demo.Entity;

import javax.persistence.Entity;

@Entity
public class Review extends AbstractEntity {
    private Long userId;
    private String username;
    private Long driverId;
    private String rating;
    private String description;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Review{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", driverId=" + driverId +
                ", rating='" + rating + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
