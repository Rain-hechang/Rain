package com.rain.web.controller.system;


import com.rain.constant.Constants;
import com.rain.core.domain.AjaxResult;
import com.rain.core.domain.entity.CoreMenu;
import com.rain.core.domain.entity.CoreUser;
import com.rain.core.domain.model.LoginBody;
import com.rain.service.ICoreMenuService;
import com.rain.utils.SecurityUtils;
import com.rain.web.service.CoreLoginService;
import com.rain.web.service.CorePermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * 登录验证
 * 
 * @author rain-hechang
 */
@Api(tags = "登录验证模块")
@RestController
public class CoreLoginController
{
    private final CoreLoginService loginService;

    private final ICoreMenuService menuService;

    private final CorePermissionService permissionService;

    public CoreLoginController(CoreLoginService loginService, ICoreMenuService menuService, CorePermissionService permissionService) {
        this.loginService = loginService;
        this.menuService = menuService;
        this.permissionService = permissionService;
    }

    /**
     * @Description:  登录方法
     * @param loginBody
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("登录方法")
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody)
    {
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * @Description:  路由信息
     * @return: com.rain.core.domain.AjaxResult 用户信息
     * @Author:  rain-he
     */
    @ApiOperation("路由信息")
    @GetMapping("getInfo")
    public AjaxResult getInfo()
    {
        CoreUser user = SecurityUtils.getLoginUser().getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return ajax;
    }

    /**
     * @Description:  获取路由信息
     * @return: com.rain.core.domain.AjaxResult 路由信息
     * @Author:  rain-he
     */
    @ApiOperation("获取路由信息")
    @GetMapping("getRouters")
    public AjaxResult getRouters()
    {
        Long userId = SecurityUtils.getUserId();
        List<CoreMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return AjaxResult.success(menuService.buildMenus(menus));
    }
}
