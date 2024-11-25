package clover.service;


import clover.model.OperationLog;

import clover.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    @Autowired
    private LogRepository logRepository; // 注入LogRepository

    public void saveOperationLog(OperationLog operationLog) {
        logRepository.save(operationLog);
    }
}