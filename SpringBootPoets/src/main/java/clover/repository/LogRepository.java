package clover.repository;

import clover.model.OperationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<OperationLog, Long> {


}