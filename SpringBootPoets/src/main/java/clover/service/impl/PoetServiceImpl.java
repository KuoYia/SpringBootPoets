package clover.service.impl;

import clover.dao.PoetDao;
import clover.pojo.Poem;
import clover.pojo.Poet;
import clover.service.PoetService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 服务层实现类，标记为Spring的服务组件
@Service
// 标记为事务性操作，确保方法中的数据库操作在一个事务内完成
@Transactional
public class PoetServiceImpl implements PoetService {

    @Autowired
    private PoetDao poetDao;

    @Override
    public Poet findById(int id) {
        return poetDao.findById(id);
    }

    //插入诗人
    @Override
    public int insert(Poet poet) {
        // 该方法用于将一个新的Poet对象插入到数据库中，并返回插入的Poet对象
        // 检查数据库中是否已存在同名的诗人
        List<Poet> existingPoets = poetDao.findByMultipleConditions(poet.getName(), null, null);
        // findByMultipleConditions方法接受诗人的姓名、朝代和传记作为参数，这里只搜索姓名

        // 如果搜索结果不为空，说明存在至少一个同名的诗人
        if (!existingPoets.isEmpty()) {
            // 如果存在同名诗人，则抛出IllegalArgumentException异常，表示不能插入同名诗人
            throw new IllegalArgumentException("A poet with the same name already exists.");
        }
        // 如果没有同名诗人，调用poetDao的insert方法将诗人对象插入到数据库中
        poetDao.insert(poet);

        // 插入操作完成后，返回传入的poet对象
        return 1;
    }
    //更改诗人信息
    @Override
    public int update(Poet poet) {
        int affectedRows = poetDao.update(poet);
        return affectedRows > 0 ? 1 : 0;
    }
    //删除诗人
    @Override
    @Transactional
    public boolean delete(int id) {
        // 检查诗人是否存在
        Poet poet = poetDao.findById(id);
        if (poet == null) {
            return false; // 诗人不存在，删除操作不允许，返回false
        }
        // 查询与给定诗人ID相关联的所有诗
        List<Poem> poems = poetDao.selectPoemsByPoetId(id);
        // 计算与给定诗人ID相关联的收藏数量
        int collectionsCount = poetDao.countCollectionsByAuthorId(id);

        // 如果存在与诗人关联的诗或收藏，则不允许删除
        if (!poems.isEmpty() || collectionsCount > 0) {
            return false; // 由于存在关联数据，删除操作不允许，返回false
        }
        // 如果没有关联的诗或收藏，执行删除诗人的操作
        poetDao.delete(id);

        // 删除操作成功，返回true
        return true;
    }

    //批量插入诗人
    @Override
    public void insertPoetsInBatch(List<Poet> poets) {
        for (Poet poet : poets) {
            insert(poet);
        }
    }
    //批量删除诗人
    @Override
    public void deletePoetsInBatch(List<Integer> ids) {
        for (Integer id : ids) {
            if (!delete(id)) {
                throw new IllegalStateException("Deletion of poet with ID " + id + " failed due to associated poems or collections.");
            }
        }
    }

    @Override
    public List<Poet> findAllPoets(int page, int size) {
        PageHelper.startPage(page, size);
        return poetDao.findAllPoets();
    }

    @Override
    public List<Poet> findPoetsByPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return poetDao.findAllPoets();
    }
    @Override
    public List<Poet> findPoetWithPoemsById(int id) {
        return poetDao.findPoetWithPoemsById(id);
    }

    @Override
    public List<Poem> selectPoemsByPoetId(int poetId) {
        return poetDao.selectPoemsByPoetId(poetId);
    }

}