package com.rain.mapper;

import com.rain.core.domain.entity.CoreRole;

import java.util.List;

/**
 * 角色表 数据层
 * 
 * @author rain-hechang
 */
public interface CoreRoleMapper
{
    /**
     * 根据条件分页查询角色数据
     * 
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    public List<CoreRole> selectRoleList(CoreRole role);

    /**
     * 根据用户ID查询角色
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    public List<CoreRole> selectRolePermissionByUserId(Long userId);

    /**
     * 查询所有角色
     * 
     * @return 角色列表
     */
    public List<CoreRole> selectRoleAll();

    /**
     * 根据用户ID获取角色选择框列表
     * 
     * @param userId 用户ID
     * @return 选中角色ID列表
     */
    public List<Long> selectRoleListByUserId(Long userId);

    /**
     * 通过角色ID查询角色
     * 
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    public CoreRole selectRoleById(Long roleId);

    /**
     * 根据用户ID查询角色
     * 
     * @param userName 用户名
     * @return 角色列表
     */
    public List<CoreRole> selectRolesByUserName(String userName);

    /**
     * 校验角色名称是否唯一
     * 
     * @param roleName 角色名称
     * @return 角色信息
     */
    public CoreRole checkRoleNameUnique(String roleName);

    /**
     * 校验角色权限是否唯一
     * 
     * @param roleKey 角色权限
     * @return 角色信息
     */
    public CoreRole checkRoleKeyUnique(String roleKey);

    /**
     * 修改角色信息
     * 
     * @param role 角色信息
     * @return 结果
     */
    public int updateRole(CoreRole role);

    /**
     * 新增角色信息
     * 
     * @param role 角色信息
     * @return 结果
     */
    public int insertRole(CoreRole role);

    /**
     * 通过角色ID删除角色
     * 
     * @param roleId 角色ID
     * @return 结果
     */
    public int deleteRoleById(Long roleId);

    /**
     * 批量删除角色信息
     * 
     * @param roleIds 需要删除的角色ID
     * @return 结果
     */
    public int deleteRoleByIds(Long[] roleIds);
}
