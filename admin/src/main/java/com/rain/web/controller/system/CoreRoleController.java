package com.rain.web.controller.system;

import com.rain.annotation.Log;
import com.rain.core.controller.BaseController;
import com.rain.core.domain.AjaxResult;
import com.rain.core.domain.entity.CoreDept;
import com.rain.core.domain.entity.CoreRole;
import com.rain.core.domain.entity.CoreUser;
import com.rain.core.domain.model.LoginUser;
import com.rain.core.page.TableDataInfo;
import com.rain.domain.CoreUserRole;
import com.rain.enums.BusinessType;
import com.rain.service.ICoreDeptService;
import com.rain.service.ICoreRoleService;
import com.rain.service.ICoreUserService;
import com.rain.utils.StringUtils;
import com.rain.utils.poi.ExcelUtil;
import com.rain.web.service.CorePermissionService;
import com.rain.web.service.TokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 角色信息
 * 
 * @author rain-hechang
 */
@Api(tags = "角色信息模块")
@RestController
@RequestMapping("/system/role")
public class CoreRoleController extends BaseController
{
    private final ICoreRoleService roleService;

    private final TokenService tokenService;

    private final CorePermissionService permissionService;

    private final ICoreUserService userService;

    private final ICoreDeptService deptService;

    public CoreRoleController(ICoreRoleService roleService, TokenService tokenService, CorePermissionService permissionService, ICoreUserService userService, ICoreDeptService deptService) {
        this.roleService = roleService;
        this.tokenService = tokenService;
        this.permissionService = permissionService;
        this.userService = userService;
        this.deptService = deptService;
    }

    /**
     * @Description:  获取角色信息
     * @param role
     * @return: com.rain.core.page.TableDataInfo
     * @Author:  rain-he
     */
    @ApiOperation("获取角色信息")
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/list")
    public TableDataInfo list(CoreRole role)
    {
        startPage();
        List<CoreRole> list = roleService.selectRoleList(role);
        return getDataTable(list);
    }

    /**
     * @Description:  角色信息导出
     * @param response
     * @param role
     * @return: void
     * @Author:  rain-he
     */
    @ApiOperation("角色信息导出")
    @Log(title = "角色管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:role:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, CoreRole role)
    {
        List<CoreRole> list = roleService.selectRoleList(role);
        ExcelUtil<CoreRole> util = new ExcelUtil<CoreRole>(CoreRole.class);
        util.exportExcel(response, list, "角色数据");
    }

    /**
     * @Description:  根据角色编号获取详细信息
     * @param roleId
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("根据角色编号获取详细信息")
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping(value = "/{roleId}")
    public AjaxResult getInfo(@PathVariable Long roleId)
    {
        roleService.checkRoleDataScope(roleId);
        return success(roleService.selectRoleById(roleId));
    }

    /**
     * @Description:  新增角色
     * @param role
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("新增角色")
    @PreAuthorize("@ss.hasPermi('system:role:add')")
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody CoreRole role)
    {
        if (!roleService.checkRoleNameUnique(role))
        {
            return error("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        else if (!roleService.checkRoleKeyUnique(role))
        {
            return error("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        role.setCreateBy(getUsername());
        return toAjax(roleService.insertRole(role));

    }

    /**
     * @Description:  修改保存角色
     * @param role
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("修改保存角色")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody CoreRole role)
    {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());
        if (!roleService.checkRoleNameUnique(role))
        {
            return error("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        else if (!roleService.checkRoleKeyUnique(role))
        {
            return error("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        role.setUpdateBy(getUsername());
        
        if (roleService.updateRole(role) > 0)
        {
            // 更新缓存用户权限
            LoginUser loginUser = getLoginUser();
            if (StringUtils.isNotNull(loginUser.getUser()) && !loginUser.getUser().isAdmin())
            {
                loginUser.setPermissions(permissionService.getMenuPermission(loginUser.getUser()));
                loginUser.setUser(userService.selectUserByUserName(loginUser.getUser().getUserName()));
                tokenService.setLoginUser(loginUser);
            }
            return success();
        }
        return error("修改角色'" + role.getRoleName() + "'失败，请联系管理员");
    }

    /**
     * @Description:  修改保存数据权限
     * @param role
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("修改保存数据权限")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/dataScope")
    public AjaxResult dataScope(@RequestBody CoreRole role)
    {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());
        return toAjax(roleService.authDataScope(role));
    }

    /**
     * @Description:  状态修改
     * @param role
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("状态修改")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody CoreRole role)
    {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());
        role.setUpdateBy(getUsername());
        return toAjax(roleService.updateRoleStatus(role));
    }

    /**
     * @Description: 删除角色
     * @param roleIds
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("删除角色")
    @PreAuthorize("@ss.hasPermi('system:role:remove')")
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roleIds}")
    public AjaxResult remove(@PathVariable Long[] roleIds)
    {
        return toAjax(roleService.deleteRoleByIds(roleIds));
    }

    /**
     * @Description:  获取角色选择框列表
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("获取角色选择框列表")
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping("/optionselect")
    public AjaxResult optionselect()
    {
        return success(roleService.selectRoleAll());
    }

    /**
     * @Description:  查询未分配用户角色列表
     * @param user
     * @return: com.rain.core.page.TableDataInfo
     * @Author:  rain-he
     */
    @ApiOperation("查询未分配用户角色列表")
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/authUser/allocatedList")
    public TableDataInfo allocatedList(CoreUser user)
    {
        startPage();
        List<CoreUser> list = userService.selectAllocatedList(user);
        return getDataTable(list);
    }

    /**
     * @Description:  查询未分配用户角色列表
     * @param user
     * @return: com.rain.core.page.TableDataInfo
     * @Author:  rain-he
     */
    @ApiOperation("查询未分配用户角色列表")
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/authUser/unallocatedList")
    public TableDataInfo unallocatedList(CoreUser user)
    {
        startPage();
        List<CoreUser> list = userService.selectUnallocatedList(user);
        return getDataTable(list);
    }

    /**
     * @Description:  取消授权用户
     * @param userRole
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("取消授权用户")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancel")
    public AjaxResult cancelAuthUser(@RequestBody CoreUserRole userRole)
    {
        return toAjax(roleService.deleteAuthUser(userRole));
    }

    /**
     * @Description:  批量取消授权用户
     * @param roleId
     * @param userIds
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("批量取消授权用户")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancelAll")
    public AjaxResult cancelAuthUserAll(Long roleId, Long[] userIds)
    {
        return toAjax(roleService.deleteAuthUsers(roleId, userIds));
    }

    /**
     * @Description:  批量选择用户授权
     * @param roleId
     * @param userIds
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("批量选择用户授权")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/selectAll")
    public AjaxResult selectAuthUserAll(Long roleId, Long[] userIds)
    {
        roleService.checkRoleDataScope(roleId);
        return toAjax(roleService.insertAuthUsers(roleId, userIds));
    }

    /**
     * @Description:  获取对应角色部门树列表
     * @param roleId
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("获取对应角色部门树列表")
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping(value = "/deptTree/{roleId}")
    public AjaxResult deptTree(@PathVariable("roleId") Long roleId)
    {
        AjaxResult ajax = AjaxResult.success();
        ajax.put("checkedKeys", deptService.selectDeptListByRoleId(roleId));
        ajax.put("depts", deptService.selectDeptTreeList(new CoreDept()));
        return ajax;
    }
}
