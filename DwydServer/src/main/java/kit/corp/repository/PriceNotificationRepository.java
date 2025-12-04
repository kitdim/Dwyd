package kit.corp.repository;

import kit.corp.model.notification.PriceNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PriceNotificationRepository extends JpaRepository<PriceNotification, Long> {
    @Query("SELECT pn FROM PriceNotification pn "
            + "WHERE pn.userId = :userId")
    Optional<List<PriceNotification>> findAllByUserId(@Param("userId") Long userId);
}
