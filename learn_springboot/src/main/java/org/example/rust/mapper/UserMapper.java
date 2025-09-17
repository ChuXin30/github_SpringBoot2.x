package org.example.rust.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.rust.model.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户数据访问层接口
 * 使用MyBatis进行数据库操作
 */
@Mapper
public interface UserMapper {
    
    /**
     * 插入用户
     * @param user 用户信息
     * @return 影响行数
     */
    int insert(User user);
    
    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户信息
     */
    User selectById(@Param("id") Long id);
    
    /**
     * 根据邮箱查询用户
     * @param email 邮箱
     * @return 用户信息
     */
    User selectByEmail(@Param("email") String email);
    
    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 影响行数
     */
    int updateById(User user);
    
    /**
     * 根据ID删除用户
     * @param id 用户ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 分页查询用户列表
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 用户列表
     */
    List<User> selectPage(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 搜索用户
     * @param name 姓名（模糊查询）
     * @param email 邮箱（模糊查询）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 用户列表
     */
    List<User> searchUsers(@Param("name") String name, 
                          @Param("email") String email, 
                          @Param("offset") int offset, 
                          @Param("limit") int limit);
    
    /**
     * 统计用户总数
     * @return 用户总数
     */
    Long countUsers();
    
    /**
     * 统计搜索结果总数
     * @param name 姓名（模糊查询）
     * @param email 邮箱（模糊查询）
     * @return 搜索结果总数
     */
    Long countSearchUsers(@Param("name") String name, @Param("email") String email);
    
    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    Boolean existsByEmail(@Param("email") String email);
    
    /**
     * 检查邮箱是否被其他用户使用
     * @param email 邮箱
     * @param id 用户ID（排除此用户）
     * @return 是否被使用
     */
    Boolean existsByEmailAndNotId(@Param("email") String email, @Param("id") Long id);
    
    /**
     * 批量插入用户
     * @param users 用户列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<User> users);
    
    /**
     * 根据ID列表查询用户
     * @param ids 用户ID列表
     * @return 用户列表
     */
    List<User> selectByIds(@Param("list") List<Long> ids);
    
    /**
     * 根据年龄范围查询用户
     * @param minAge 最小年龄
     * @param maxAge 最大年龄
     * @return 用户列表
     */
    List<User> selectByAgeRange(@Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge);
    
    /**
     * 根据创建时间范围查询用户
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 用户列表
     */
    List<User> selectByCreatedTimeRange(@Param("startTime") LocalDateTime startTime, 
                                       @Param("endTime") LocalDateTime endTime);
}
