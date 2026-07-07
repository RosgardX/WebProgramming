package back.hits;

import back.hits.HitChecker;
import back.user.UserEntity;
import back.user.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HitService {

    private final HitRepository hits;
    private final UserService users;
    private final HitChecker hitChecker;

    public HitService(HitRepository hits, UserService users, HitChecker hitChecker) {
        this.hits = hits;
        this.users = users;
        this.hitChecker = hitChecker;
    }

    public HitEntity addHit(String username, double x, double y, double r) {
        UserEntity user = users.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        HitEntity h = new HitEntity();
        h.setX(x);
        h.setY(y);
        h.setR(r);
        h.setHit(hitChecker.isHit(x, y, r)); // <- используем HitChecker
        h.setUserId(user.getId());

        return hits.save(h);
    }

    public List<HitEntity> findUserHits(String username) {
        UserEntity user = users.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return hits.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public void clearUserHits(String username) {
        UserEntity user = users.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        hits.deleteByUserId(user.getId());
    }
}