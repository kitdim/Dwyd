package kit.corp.repository;

import kit.corp.model.task.TaskExecution;
import kit.corp.model.task.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskExecutionRepository extends JpaRepository<TaskExecution, Long> {
    @Query("SELECT COUNT(te) > 0 FROM TaskExecution te "
           + "WHERE te.taskName = :taskName AND te.taskStatus = :runningStatus")
    boolean isRunningTask(
            @Param("taskName") String taskName,
            @Param("runningStatus") TaskStatus runningStatus
    );
}
