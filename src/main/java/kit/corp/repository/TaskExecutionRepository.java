package kit.corp.repository;

import kit.corp.model.task.TaskExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskExecutionRepository extends JpaRepository<TaskExecution, Long> {
}
