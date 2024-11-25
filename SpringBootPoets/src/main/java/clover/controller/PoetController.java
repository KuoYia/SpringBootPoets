package clover.controller;

import clover.pojo.Poem;
import clover.pojo.Poet;
import clover.service.PoetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*@RestController：这是一个组合注解，
它包含了@Controller和@ResponseBody两个注解的功能。
@Controller注解用于标记该类是一个控制器，负责处理客户端的请求。
@ResponseBody注解表示该控制器方法的返回值将直接写入HTTP响应体中，
而不是解析为视图。通常用于返回JSON或XML格式的数据。*/
/**
 * 诗人控制器，处理与诗人相关的API请求。
 * 使用@RestController注解标记该类为一个RESTful风格的控制器。
 * 使用@RequestMapping注解指定基础路径为"/api/poets"。
 */
@RestController
@RequestMapping("/api/poets")
public class PoetController {

    @Autowired
    private PoetService poetService;

    private static final Logger logger = LoggerFactory.getLogger(PoetController.class);
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePoet(@PathVariable int id) {
        try {
            boolean result = poetService.delete(id);
            if (result) {
                return ResponseEntity.ok(1); // 删除成功
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("存在级联关系的诗人无法删除"); // 存在级联关系
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("删除诗人时发生错误：" + e.getMessage()); // 其他错误
        }
    }

    //查找诗人

    @GetMapping("/{id}")
    public ResponseEntity<Poet> getPoetById(@PathVariable int id) {
        Poet poet = poetService.findById(id);
        return ResponseEntity.ok(poet);
    }


    //分页查询
    ////http://localhost:8080/api/poets/getPage
    @GetMapping("/getPage")
    public ResponseEntity<List<Poet>> getIssues(@RequestParam(defaultValue = "2") Integer pageNum,
                                                @RequestParam(defaultValue = "5") Integer pageSize) {
        List<Poet> poets = poetService.findPoetsByPage(pageNum, pageSize);
        return ResponseEntity.ok(poets); // 使用 ResponseEntity.ok() 来返回成功的响应
    }



    @PostMapping
    public ResponseEntity<Integer> addPoet(@RequestBody Poet poet) {
        try {
            int result = poetService.insert(poet);
            if (result == 1) {
                return ResponseEntity.ok(1); // 成功返回1
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0); // 失败返回0
            }
        } catch (Exception e) {
            logger.error("Error inserting poet", e); // 打印异常信息和堆栈跟踪
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0); // 异常返回0
        }
    }

     // 更新诗人信息

/**
           * 更新诗人信息。
           *
           * @param poet 包含更新信息的诗人对象
           * @return ResponseEntity 包含更新后的诗人对象或错误信息
           */
          @PutMapping
          public ResponseEntity<Poet> updatePoet(@RequestBody Poet poet) {
              try {
                  // 调用服务层方法更新诗人信息
                  int result = poetService.update(poet);
                  if (result > 0) {
                      // 如果更新成功，返回更新后的诗人对象
                      return ResponseEntity.ok(poet);
                  } else {
                      // 如果更新失败，返回400 Bad Request状态码
                      return ResponseEntity.badRequest().body(null);
                  }
              } catch (Exception e) {
                  // 打印异常信息和堆栈跟踪
                  logger.error("Error updating poet", e);
                  // 如果发生异常，返回500 Internal Server Error状态码
                  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
              }
          }

/*     @PutMapping("/{id}")
     public ResponseEntity<Integer> updatePoet(@PathVariable int id, @RequestBody Poet poet) {
        try {
            poet.setId(id); // 确保更新的是正确的诗人信息
            int result = poetService.update(poet);
            if (result > 0) {
                return ResponseEntity.ok(1); // 成功返回1
            } else {
                return ResponseEntity.badRequest().body(0); // 失败返回0
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0); // 异常返回0
        }
    }*/

        // 批量插入诗人
        // 批量插入诗人
        @PostMapping("/batch")
        public ResponseEntity<List<Poet>> addPoets(@RequestBody List<Poet> poets) {
            try {
                poetService.insertPoetsInBatch(poets);
                return ResponseEntity.ok(poets);
            } catch (Exception e) {
                logger.error("Error inserting poets in batch", e); // 打印异常信息和堆栈跟踪
                return ResponseEntity.badRequest().body(null);
            }
        }

        // 批量删除诗人
        @DeleteMapping("/batch")
        public ResponseEntity<Integer> deletePoets(@RequestBody List<Integer> ids) {
            try {
                poetService.deletePoetsInBatch(ids);
                return ResponseEntity.ok(1);
            } catch (Exception e) {
                return ResponseEntity.ok(0);
            }
        }

        //多表连接查询，查询诗人和诗句
    // clover.controller.PoetController

    // 多表连接查询，查询特定ID的诗人返回诗人及其诗句
    @GetMapping("/poetAndPoems/{poetId}")
    public ResponseEntity<?> findPoetAndPoems(@PathVariable int poetId) {
        try {
            // 调用服务层的方法查询特定ID的诗人及其诗句
            List<Poet> poetsWithPoems = poetService.findPoetWithPoemsById(poetId);
            if (!poetsWithPoems.isEmpty()) {
                // 由于只查询了一个诗人，所以直接获取第一个元素
                Poet poetWithPoems = poetsWithPoems.get(0);
                return ResponseEntity.ok(poetWithPoems); // 如果查询成功且结果不为空，返回查询结果
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Poet with ID " + poetId + " not found."); // 如果诗人不存在，返回404 Not Found状态码
            }
        } catch (Exception e) {
            logger.error("Error finding poet and poems with ID {}", poetId, e); // 打印异常信息和堆栈跟踪
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while finding poet and poems: " + e.getMessage()); // 如果发生异常，返回错误状态和消息
        }
    }

    // 多表连接查询，查询特定ID的诗人和诗句
    @GetMapping("/poetSPoems/{poetId}")
    public ResponseEntity<?> findPoetSPoems(@PathVariable int poetId) {
        try {
            // 调用服务层的方法查询特定ID的诗人及其诗句
            List<Poem> poems = poetService.selectPoemsByPoetId(poetId);
            if (poems != null && !poems.isEmpty()) {
                return ResponseEntity.ok(poems); // 如果查询成功且结果不为空，返回查询结果
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No poems found for poet ID " + poetId); // 如果没有找到诗句，返回404 Not Found状态码
            }
        } catch (Exception e) {
            logger.error("Error finding poems for poet ID {}", poetId, e); // 打印异常信息和堆栈跟踪
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while finding poems: " + e.getMessage()); // 如果发生异常，返回错误状态和消息
        }
    }
}