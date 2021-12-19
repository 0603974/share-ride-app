package rideshare.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rideshare.demo.Entity.Community;

@Repository
public interface CommunityRepository extends JpaRepository<Community,String> {


}
