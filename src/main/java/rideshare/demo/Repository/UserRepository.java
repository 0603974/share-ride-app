package rideshare.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rideshare.demo.Entity.User;

import java.util.List;

/**
 * This interface serves as the communicator to the database, extending the JpaRepository eliminates the need of writing sql queries expect we have complex queries to write
 **/
@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmailAddress(String email);
    User findByUsername(String username);
    User getOneById(Long id);
    Long countAllByRole(String role);
    List<User> getAllByRole(String role);

}
