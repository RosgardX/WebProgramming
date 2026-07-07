package back.hits;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hits")
public class HitController {

    private final HitService hitService;

    public HitController(HitService hitService) {
        this.hitService = hitService;
    }

    @GetMapping
    public List<HitEntity> list(Authentication auth) {
        String username = auth.getName();
        return hitService.findUserHits(username);
    }

    @PostMapping
    public ResponseEntity<HitEntity> add(Authentication auth, @RequestBody HitRequest req) {
        String username = auth.getName();
        HitEntity saved = hitService.addHit(username, req.x(), req.y(), req.r());
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping
    public ResponseEntity<Void> clear(Authentication auth) {
        String username = auth.getName();
        hitService.clearUserHits(username);
        return ResponseEntity.noContent().build();
    }

    public record HitRequest(double x, double y, double r) {}
}