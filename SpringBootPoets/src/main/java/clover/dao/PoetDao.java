package clover.dao;


import clover.pojo.Poem;
import clover.pojo.Poet;
import org.apache.ibatis.annotations.*;
import org.springframework.context.annotation.Primary;

import java.util.List;


@Mapper
@Primary
public interface PoetDao {
    //查询功能
    @Select("SELECT * FROM poets WHERE id = #{id}")
    Poet findById(int id);

    //插入，新增诗人
    @Insert("INSERT INTO poets(name, birthDate, deathDate, dynasty, biography) " +
            "VALUES(#{name}, #{birthDate}, #{deathDate}, #{dynasty}, #{biography})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Poet poet);


    //更改
 /*   @Update("UPDATE poets SET name = #{name}, birthDate = #{birthDate}, " +
            "deathDate = #{deathDate}, dynasty = #{dynasty}, biography = #{biography} WHERE id = #{id}")*/
    @Update({
            "<script>",
            "UPDATE poets",
            "<set>",
            "<if test='name != null'>name = #{name},</if>",
            "<if test='birthDate != null'>birthDate = #{birthDate},</if>",
            "<if test='deathDate != null'>deathDate = #{deathDate},</if>",
            "<if test='dynasty != null'>dynasty = #{dynasty},</if>",
            "<if test='biography != null'>biography = #{biography},</if>",
            "</set>",
            "WHERE id = #{id}",
            "</script>"
    })
    int update(Poet poet);


    //删除诗人
    @Delete("DELETE FROM poets WHERE id = #{id}")
    void delete(int id);

    // 分页查询所有诗人
    /*@Select("SELECT * FROM poets")
    List<Poet> findAllPoets(@Param("page") PageHelper.Page<?> page);*/
    @Select("SELECT * FROM poets")
    List<Poet> findAllPoets();

    // 批量插入
    @Insert("<script>" +
            "INSERT INTO poets (name, birthDate, deathDate, dynasty, biography) VALUES " +
            "<foreach collection='list' item='poet' separator=','>" +
            "(#{poet.name}, #{poet.birthDate}, #{poet.deathDate}, #{poet.dynasty}, #{poet.biography})" +
            "</foreach>" +
            "</script>")
    void insertPoetsInBatch(@Param("list") List<Poet> poets);

    // 批量删除
    @Delete("<script>" +
            "DELETE FROM poets " +
            "WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    void deletePoetsInBatch(@Param("ids") List<Integer> ids);

    // 模糊查询三个条件三选一
    @Select("<script>" +
            "SELECT * FROM poets " +
            "WHERE 1=1 " +
            "<if test='name != null and name.trim() != \"\"'>" +
            "AND name LIKE CONCAT('%', #{name}, '%') " +
            "</if>" +
            "<if test='dynasty != null and dynasty.trim() != \"\"'>" +
            "AND dynasty LIKE CONCAT('%', #{dynasty}, '%') " +
            "</if>" +
            "<if test='biography != null and biography.trim() != \"\"'>" +
            "AND biography LIKE CONCAT('%', #{biography}, '%') " +
            "</if>" +
            "</script>")

    List<Poet> findByMultipleConditions(@Param("name") String name,
                                        @Param("dynasty") String dynasty,
                                        @Param("biography") String biography);

/*    @Select("<script>" +
            "SELECT * FROM poets " +
            "<where>"+
            "<if test='name != null and name.trim() != \"\"'>" +
            "AND name LIKE CONCAT('%', #{name}, '%') " +
            "</if>" +
            "<if test='dynasty != null and dynasty.trim() != \"\"'>" +
            "AND dynasty LIKE CONCAT('%', #{dynasty}, '%') " +
            "</if>" +
            "<if test='biography != null and biography.trim() != \"\"'>" +
            "AND biography LIKE CONCAT('%', #{biography}, '%') " +
            "</if>" +
            "</where>"+
            "</script>")*/
    /*// 根据诗人ID查询其所有诗句
    @Select("SELECT id, title, content, translation, authorId, poemTypeId FROM poems WHERE authorId = #{poetId}")
    List<Poem> selectPoemsByPoetId(@Param("poetId") int poetId);*/

    // 查询作者被收藏的次数
    @Select("SELECT COUNT(*) " +
            "FROM collections " +
            "WHERE resource_id IN " +
            "(SELECT id FROM poems WHERE authorId = #{id})")
    int countCollectionsByAuthorId(int id);


    // 多表连接查询，查询诗人和诗句
    // 返回包含诗人主体和诗句列表的单个Poet对象
  /*  @Select("SELECT " +
            "p.id AS poet_id, " +
            "p.name AS poet_name, " +
            "p.birthDate AS poet_birthDate, " +
            "p.deathDate AS poet_deathDate, " +
            "p.dynasty AS poet_dynasty, " +
            "p.biography AS poet_biography, " +

            "po.id AS poem_id, " +
            "po.title AS poem_title, " +
            "po.content AS poem_content, " +
            "po.translation AS poem_translation, " +
            "po.authorId AS poem_authorId, " +
            "po.poemTypeId AS poem_poemTypeId " +

            "FROM poets p " +
            "LEFT JOIN poems po ON p.id = po.authorId " +
            "WHERE p.id = #{id}")
    @Results(value = {
            @Result(property = "id", column = "poet_id"),
            @Result(property = "name", column = "poet_name"),
            @Result(property = "birthDate", column = "poet_birthDate"),
            @Result(property = "deathDate", column = "poet_deathDate"),
            @Result(property = "dynasty", column = "poet_dynasty"),
            @Result(property = "biography", column = "poet_biography"),
            @Result(property = "poems", many = @Many(select = "selectPoemsByPoetId"))
    })
    Poet findPoetWithPoemsById(@Param("id") int id);
*/
    // 根据诗人ID查询其所有诗句
    @Select("SELECT id, title, content, translation, authorId, poemTypeId FROM poems WHERE authorId = #{poetId}")
    List<Poem> selectPoemsByPoetId(@Param("poetId") int poetId);


    @Select("SELECT " +
            "p.id AS poet_id, " +
            "p.name AS poet_name, " +
            "p.birthDate AS poet_birthDate, " +
            "p.deathDate AS poet_deathDate, " +
            "p.dynasty AS poet_dynasty, " +
            "p.biography AS poet_biography, " +

            "po.id AS poem_id, " +
            "po.title AS poem_title, " +
            "po.content AS poem_content, " +
            "po.translation AS poem_translation, " +

            "po.authorId AS poem_authorId, " +
            "po.poemTypeId AS poem_poemTypeId " +

            "FROM poets p " +
            "LEFT JOIN poems po ON p.id = po.authorId " +
            "WHERE p.id = #{id}")

    // 使用@Results注解来指定查询结果如何映射到Java对象的属性上
    @Results(value = {
            // 映射单个属性
            @Result(property = "id", column = "poet_id"),
            @Result(property = "name", column = "poet_name"),
            @Result(property = "birthDate", column = "poet_birthDate"),
            @Result(property = "deathDate", column = "poet_deathDate"),
            @Result(property = "dynasty", column = "poet_dynasty"),
            @Result(property = "biography", column = "poet_biography"),

            // 映射一个集合属性，表示一对多的关系
            // 这里映射的是Poet对象中的poems属性，它是一个包含多个Poem对象的列表
            @Result(property = "poems", column = "poet_id",
                    many = @Many(select = "clover.dao.PoetDao.selectPoemsByPoetId"))
        })
       List<Poet> findPoetWithPoemsById(@Param("id") int id);

}



