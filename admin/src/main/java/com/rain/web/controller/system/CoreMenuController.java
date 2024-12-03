package com.rain.web.controller.system;

import com.rain.annotation.Log;
import com.rain.constant.UserConstants;
import com.rain.core.controller.BaseController;
import com.rain.core.domain.AjaxResult;
import com.rain.core.domain.entity.CoreMenu;
import com.rain.enums.BusinessType;
import com.rain.service.ICoreMenuService;
import com.rain.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单信息
 * 
 * @author rain-hechang
 */
@Api(tags = "通知公告模块")
@RestController
@RequestMapping("/system/menu")
public class CoreMenuController extends BaseController
{
    private final ICoreMenuService menuService;

    public CoreMenuController(ICoreMenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * @Description:  获取菜单列表
     * @param menu
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("获取菜单列表")
    @PreAuthorize("@ss.hasPermi('system:menu:list')")
    @GetMapping("/list")
    public AjaxResult list(CoreMenu menu)
    {
        List<CoreMenu> menus = menuService.selectMenuList(menu, getUserId());
        return success(menus);
    }

    /**
     * @Description:  根据菜单编号获取详细信息
     * @param menuId
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("根据菜单编号获取详细信息")
    @PreAuthorize("@ss.hasPermi('system:menu:query')")
    @GetMapping(value = "/{menuId}")
    public AjaxResult getInfo(@PathVariable Long menuId)
    {
        return success(menuService.selectMenuById(menuId));
    }

    /**
     * @Description:  加载对应角色菜单列表树
     * @param menu
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("加载对应角色菜单列表树")
    @GetMapping("/treeselect")
    public AjaxResult treeselect(CoreMenu menu)
    {
        List<CoreMenu> menus = menuService.selectMenuList(menu, getUserId());
        return success(menuService.buildMenuTreeSelect(menus));
    }

    /**
     * @Description:  加载对应角色菜单列表树
     * @param roleId
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("加载对应角色菜单列表树")
    @GetMapping(value = "/roleMenuTreeselect/{roleId}")
    public AjaxResult roleMenuTreeselect(@PathVariable("roleId") Long roleId)
    {
        List<CoreMenu> menus = menuService.selectMenuList(getUserId());
        AjaxResult ajax = AjaxResult.success();
        ajax.put("checkedKeys", menuService.selectMenuListByRoleId(roleId));
        ajax.put("menus", menuService.buildMenuTreeSelect(menus));
        return ajax;
    }

    /**
     * @Description:  新增菜单
     * @param menu
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("新增菜单")
    @PreAuthorize("@ss.hasPermi('system:menu:add')")
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody CoreMenu menu)
    {
        if (!menuService.checkMenuNameUnique(menu))
        {
            return error("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath()))
        {
            return error("新增菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        menu.setCreateBy(getUsername());
        return toAjax(menuService.insertMenu(menu));
    }

    /**
     * @Description:  修改菜单
     * @param menu
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("修改菜单")
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody CoreMenu menu)
    {
        if (!menuService.checkMenuNameUnique(menu))
        {
            return error("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath()))
        {
            return error("修改菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        else if (menu.getMenuId().equals(menu.getParentId()))
        {
            return error("修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
        }
        menu.setUpdateBy(getUsername());
        return toAjax(menuService.updateMenu(menu));
    }

    /**
     * @Description:  删除菜单
     * @param menuId
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("删除菜单")
    @PreAuthorize("@ss.hasPermi('system:menu:remove')")
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{menuId}")
    public AjaxResult remove(@PathVariable("menuId") Long menuId)
    {
        if (menuService.hasChildByMenuId(menuId))
        {
            return warn("存在子菜单,不允许删除");
        }
        if (menuService.checkMenuExistRole(menuId))
        {
            return warn("菜单已分配,不允许删除");
        }
        return toAjax(menuService.deleteMenuById(menuId));
    }
}