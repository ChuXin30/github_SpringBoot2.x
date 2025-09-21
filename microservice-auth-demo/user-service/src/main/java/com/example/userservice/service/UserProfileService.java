package com.example.userservice.service;

import com.example.userservice.entity.UserProfile;
import com.example.userservice.repository.UserProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 用户资料服务 - 集成MySQL持久化和Redis缓存
 */
@Service
@Transactional
public class UserProfileService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserProfileService.class);
    
    @Autowired
    private UserProfileRepository userProfileRepository;
    
    /**
     * 根据用户ID获取用户资料 (带Redis缓存)
     */
    @Cacheable(value = "user-profiles", key = "#userId", unless = "#result == null")
    public UserProfile getUserProfile(String userId) {
        logger.debug("从数据库查询用户资料: {}", userId);
        Optional<UserProfile> profile = userProfileRepository.findByUserId(userId);
        
        if (profile.isEmpty()) {
            // 如果用户资料不存在，创建默认资料
            logger.info("用户资料不存在，创建默认资料: {}", userId);
            return createDefaultProfile(userId);
        }
        
        return profile.get();
    }
    
    /**
     * 创建或更新用户资料 (更新缓存)
     */
    @CachePut(value = "user-profiles", key = "#userProfile.userId")
    public UserProfile saveOrUpdateProfile(UserProfile userProfile) {
        logger.debug("保存用户资料: {}", userProfile.getUsername());
        
        Optional<UserProfile> existing = userProfileRepository.findByUserId(userProfile.getUserId());
        if (existing.isPresent()) {
            // 更新现有资料
            UserProfile existingProfile = existing.get();
            updateProfileFields(existingProfile, userProfile);
            return userProfileRepository.save(existingProfile);
        } else {
            // 创建新资料
            return userProfileRepository.save(userProfile);
        }
    }
    
    /**
     * 删除用户资料 (清除缓存)
     */
    @CacheEvict(value = "user-profiles", key = "#userId")
    public boolean deleteUserProfile(String userId) {
        logger.debug("删除用户资料: {}", userId);
        Optional<UserProfile> profile = userProfileRepository.findByUserId(userId);
        if (profile.isPresent()) {
            userProfileRepository.delete(profile.get());
            return true;
        }
        return false;
    }
    
    /**
     * 更新最后登录时间
     */
    @CacheEvict(value = "user-profiles", key = "#userId")
    public void updateLastLogin(String userId) {
        logger.debug("更新最后登录时间: {}", userId);
        LocalDateTime now = LocalDateTime.now();
        userProfileRepository.updateLastLogin(userId, now, now);
    }
    
    /**
     * 获取部门用户列表 (带缓存)
     */
    @Cacheable(value = "department-users", key = "#department")
    public List<UserProfile> getUsersByDepartment(String department) {
        logger.debug("查询部门用户: {}", department);
        return userProfileRepository.findByDepartmentAndIsActiveTrue(department);
    }
    
    /**
     * 获取角色用户列表 (带缓存)
     */
    @Cacheable(value = "role-users", key = "#roleName")
    public List<UserProfile> getUsersByRole(String roleName) {
        logger.debug("查询角色用户: {}", roleName);
        return userProfileRepository.findByRoleAndActive(roleName);
    }
    
    /**
     * 获取最近活跃用户
     */
    @Cacheable(value = "recent-users", key = "#hours")
    public List<UserProfile> getRecentlyActiveUsers(int hours) {
        logger.debug("查询最近{}小时活跃用户", hours);
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return userProfileRepository.findRecentlyActiveUsers(since);
    }
    
    /**
     * 获取部门统计 (带缓存)
     */
    @Cacheable(value = "department-stats")
    public Map<String, Long> getDepartmentStatistics() {
        logger.debug("查询部门统计信息");
        List<Object[]> results = userProfileRepository.countUsersByDepartment();
        Map<String, Long> stats = new HashMap<>();
        for (Object[] result : results) {
            String department = (String) result[0];
            Long count = (Long) result[1];
            stats.put(department == null ? "未分配" : department, count);
        }
        return stats;
    }
    
    /**
     * 获取所有活跃用户
     */
    public List<UserProfile> getAllActiveUsers() {
        logger.debug("查询所有活跃用户");
        return userProfileRepository.findByIsActiveTrue();
    }
    
    /**
     * 根据用户名查询用户资料
     */
    @Cacheable(value = "user-profiles-by-username", key = "#username")
    public UserProfile getUserProfileByUsername(String username) {
        logger.debug("根据用户名查询用户资料: {}", username);
        return userProfileRepository.findByUsername(username).orElse(null);
    }
    
    /**
     * 创建默认用户资料
     */
    private UserProfile createDefaultProfile(String userId) {
        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        profile.setUsername("用户" + userId.substring(0, 8)); // 使用部分UUID作为默认用户名
        profile.setDisplayName("新用户");
        profile.setDepartment("未分配");
        profile.setPosition("员工");
        profile.setRoles(Set.of("user"));
        profile.setIsActive(true);
        
        return userProfileRepository.save(profile);
    }
    
    /**
     * 更新用户资料字段
     */
    private void updateProfileFields(UserProfile existing, UserProfile updated) {
        if (updated.getDisplayName() != null) existing.setDisplayName(updated.getDisplayName());
        if (updated.getEmail() != null) existing.setEmail(updated.getEmail());
        if (updated.getPhone() != null) existing.setPhone(updated.getPhone());
        if (updated.getDepartment() != null) existing.setDepartment(updated.getDepartment());
        if (updated.getPosition() != null) existing.setPosition(updated.getPosition());
        if (updated.getAvatarUrl() != null) existing.setAvatarUrl(updated.getAvatarUrl());
        if (updated.getRoles() != null) existing.setRoles(updated.getRoles());
        if (updated.getIsActive() != null) existing.setIsActive(updated.getIsActive());
        existing.setUpdatedAt(LocalDateTime.now());
    }
}
