package com.example.userservice.repository;

import com.example.userservice.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户资料Repository - MySQL数据访问层
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    
    /**
     * 根据Keycloak用户ID查询
     */
    Optional<UserProfile> findByUserId(String userId);
    
    /**
     * 根据用户名查询
     */
    Optional<UserProfile> findByUsername(String username);
    
    /**
     * 根据邮箱查询
     */
    Optional<UserProfile> findByEmail(String email);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查用户ID是否存在
     */
    boolean existsByUserId(String userId);
    
    /**
     * 查询活跃用户
     */
    List<UserProfile> findByIsActiveTrue();
    
    /**
     * 根据部门查询用户
     */
    List<UserProfile> findByDepartmentAndIsActiveTrue(String department);
    
    /**
     * 根据角色查询用户
     */
    @Query("SELECT up FROM UserProfile up JOIN up.roles r WHERE r = :roleName AND up.isActive = true")
    List<UserProfile> findByRoleAndActive(@Param("roleName") String roleName);
    
    /**
     * 查询最近登录的用户
     */
    @Query("SELECT up FROM UserProfile up WHERE up.lastLogin >= :since AND up.isActive = true ORDER BY up.lastLogin DESC")
    List<UserProfile> findRecentlyActiveUsers(@Param("since") LocalDateTime since);
    
    /**
     * 统计各部门用户数量
     */
    @Query("SELECT up.department, COUNT(up) FROM UserProfile up WHERE up.isActive = true GROUP BY up.department")
    List<Object[]> countUsersByDepartment();
    
    /**
     * 更新最后登录时间
     */
    @Query("UPDATE UserProfile up SET up.lastLogin = :loginTime, up.updatedAt = :updateTime WHERE up.userId = :userId")
    void updateLastLogin(@Param("userId") String userId, 
                        @Param("loginTime") LocalDateTime loginTime,
                        @Param("updateTime") LocalDateTime updateTime);
}
