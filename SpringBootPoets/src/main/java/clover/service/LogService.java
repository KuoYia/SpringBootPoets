package clover.service;


import clover.model.OperationLog;

import clover.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// LogService.java

/**
 * 日志服务类，用于处理操作日志的保存。
 */
@Service
public class LogService {

    /**
     * 注入日志仓库，用于持久化操作日志。
     */
    @Autowired
    private LogRepository logRepository; // 注入LogRepository

    /**
     * 保存操作日志到数据库。
     *
     * @param operationLog 要保存的操作日志对象
     */
    public void saveOperationLog(OperationLog operationLog) {
        logRepository.save(operationLog); // 调用仓库的save方法保存日志
    }
}
