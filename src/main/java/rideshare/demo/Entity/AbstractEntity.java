package rideshare.demo.Entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@MappedSuperclass
public class AbstractEntity implements Serializable{


	private static final long serialVersionUID = 1L;


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	private LocalDateTime createdAt = LocalDateTime.now();

	private LocalDateTime updatedAt = LocalDateTime.now();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o==null ||getClass() !=o.getClass())
			return false;
		AbstractEntity that =(AbstractEntity)o;
		return Objects.equals(id, that.id)&&
				Objects.equals(createdAt, that.createdAt )&&
				Objects.equals(updatedAt, that.updatedAt);

	}

	@Override
	public int hashCode() {
		{return Objects.hash(id,createdAt,updatedAt);}
	}
}