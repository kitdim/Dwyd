package kit.corp.repository;

import kit.corp.model.notification.PriceNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceNotificationRepository extends JpaRepository<PriceNotification, Long> {
}
