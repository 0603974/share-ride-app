package rideshare.demo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rideshare.demo.Entity.Community;
import rideshare.demo.Repository.CommunityRepository;

import java.util.List;

@Service
public class CommunityService {

    @Autowired
    CommunityRepository communityRepository;

    public void saveComment(Community community){
        communityRepository.save(community);
    }

    public List<Community> getallComment(){
        return communityRepository.findAll();
    }
}
