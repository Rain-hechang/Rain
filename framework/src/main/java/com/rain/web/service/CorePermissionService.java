package com.rain.web.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.rain.service.ICoreMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import com.rain.core.domain.entity.CoreRole;
import com.rain.core.domain.entity.CoreUser;
import com.rain.service.ICoreRoleService;

/**
 * 用户权限处理
 * 
 * @author rain-hechang
 */
@Component
public class CorePermissionService
{
    @Autowired
    private ICoreRoleService roleService;

    @Autowired
    private ICoreMenuService menuService;

    /**
     * 获取角色数据权限
     * 
     * @param user 用户信息
     * @return 角色权限信息
     */
    public Set<String> getRolePermission(CoreUser user)
    {
        Set<String> roles = new HashSet<String>();
        // 管理员拥有所有权限
        if (user.isAdmin())
        {
            roles.add("admin");
        }
        else
        {
            roles.addAll(roleService.selectRolePermissionByUserId(user.getUserId()));
        }
        return roles;
    }

    /**
     * 获取菜单数据权限
     * 
     * @param user 用户信息
     * @return 菜单权限信息
     */
    public Set<String> getMenuPermission(CoreUser user)
    {
        Set<String> perms = new HashSet<String>();
        // 管理员拥有所有权限
        if (user.isAdmin())
        {
            perms.add("*:*:*");
        }
        else
        {
            List<CoreRole> roles = user.getRoles();
            if (!CollectionUtils.isEmpty(roles))
            {
                // 多角色设置permissions属性，以便数据权限匹配权限
                for (CoreRole role : roles)
                {
                    Set<String> rolePerms = menuService.selectMenuPermsByRoleId(role.getRoleId());
                    role.setPermissions(rolePerms);
                    perms.addAll(rolePerms);
                }
            }
            else
            {
                perms.addAll(menuService.selectMenuPermsByUserId(user.getUserId()));
            }
        }
        return perms;
    }
}
