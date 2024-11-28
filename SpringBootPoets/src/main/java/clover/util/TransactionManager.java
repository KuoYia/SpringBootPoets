package clover.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

/**
 * 事务管理器类，提供对数据库连接的线程安全管理，包括设置、获取连接，提交、回滚事务，
 * 设置、释放保存点以及关闭连接等方法。
 */
/**JDBC（Java Database Connectivity）是Java语言中用于连接和操作数据库的标准API。
 * JDBC事务管理机制是指在使用JDBC进行数据库操作时，如何控制和管理事务的开始、提交和回滚
 * 事务的基本概念
        事务（Transaction）：一组一起执行或都不执行的数据库操作序列，这些操作要么全部成功，要么全部失败。
        ACID特性：事务必须满足四个特性，即原子性（Atomicity）、一致性（Consistency）、
                                   隔离性（Isolation）和持久性（Durability）。*/

/**TransactionManager类使用ThreadLocal来存储当前线程的数据库连接。
这意味着在同一个线程中，无论调用多少次getConnection()方法，都会返回同一个Connection对象。
这样就可以保证在一个服务类（service）中的方法调用的多个数据访问对象（dao）方法使用的是同一个Connection对象。
当服务类需要获取数据库连接时，它可以调用TransactionManager.getConnection()方法。由于ThreadLocal的特性，
这个方法将返回当前线程所持有的那个Connection对象，从而确保了多个dao方法使用的是同一个连接。
此外，当服务类完成数据库操作后，它应该调用TransactionManager.closeConnection()来关闭这个连接，
并清除ThreadLocal中的引用，以避免内存泄漏。*/
public class TransactionManager {
    // 使用ThreadLocal存储当前线程的数据库连接
    private static final ThreadLocal<Connection> threadLocalConnection = new ThreadLocal<>();
    // 使用ThreadLocal存储当前线程的保存点
    private static final ThreadLocal<Savepoint> threadLocalSavepoint = new ThreadLocal<>();

    /**
     * 设置当前线程的数据库连接。
     *
     * @param conn 要设置的数据库连接
     */
    public static void setConnection(Connection conn) {
        threadLocalConnection.set(conn);
    }

    /**
     * 获取当前线程的数据库连接。
     *
     * @return 当前线程的数据库连接
     */
    public static Connection getConnection() {
        return threadLocalConnection.get();
    }

    /**
     * 提交当前线程的事务。
     *
     * @throws SQLException 如果提交事务时发生错误
     */
    public static void commit() throws SQLException {
        Connection conn = getConnection();
        if (conn != null) {
            conn.commit();
        }
    }

    /**
     * 回滚当前线程的事务。
     *
     * @throws SQLException 如果回滚事务时发生错误
     */
    public static void rollback() throws SQLException {
        Connection conn = getConnection();
        if (conn != null) {
            conn.rollback();
        }
    }

    /**
     * 回滚到指定的保存点。
     *
     * @param savepoint 要回滚到的保存点
     * @throws SQLException 如果回滚到保存点时发生错误
     */
    public static void rollback(Savepoint savepoint) throws SQLException {
        Connection conn = getConnection();
        if (conn != null && savepoint != null) {
            conn.rollback(savepoint);
        }
    }

    /**
     * 设置当前事务的保存点。
     *
     * @return 设置的保存点
     * @throws SQLException 如果设置保存点时发生错误
     */
    public static Savepoint setSavepoint() throws SQLException {
        Connection conn = getConnection();
        Savepoint savepoint = conn.setSavepoint();
        threadLocalSavepoint.set(savepoint);
        return savepoint;
    }

    /**
     * 释放当前线程的保存点。
     *
     * @param savepoint 要释放的保存点
     * @throws SQLException 如果释放保存点时发生错误
     */
    public static void releaseSavepoint(Savepoint savepoint) throws SQLException {
        Connection conn = getConnection();
        if (conn != null && savepoint != null) {
            conn.releaseSavepoint(savepoint);
        }
        threadLocalSavepoint.remove();
    }

    /**
     * 关闭当前线程的数据库连接。
     *
     * @throws SQLException 如果关闭连接时发生错误
     */
    public static void closeConnection() throws SQLException {
        Connection conn = getConnection();
        if (conn != null) {
            conn.close();
        }
        threadLocalConnection.remove();
    }
}
