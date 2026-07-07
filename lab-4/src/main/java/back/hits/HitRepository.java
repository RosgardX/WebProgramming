package back.hits;

import back.hits.HitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HitRepository extends JpaRepository<HitEntity, Long> {
    List<HitEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
    void deleteByUserId(Long userId);
}