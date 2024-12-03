package com.rain.web.controller.system;

import com.rain.annotation.Log;
import com.rain.core.controller.BaseController;
import com.rain.core.domain.AjaxResult;
import com.rain.core.domain.entity.CoreDept;
import com.rain.core.domain.entity.CoreRole;
import com.rain.core.domain.entity.CoreUser;
import com.rain.core.page.TableDataInfo;
import com.rain.enums.BusinessType;
import com.rain.service.ICoreDeptService;
import com.rain.service.ICorePostService;
import com.rain.service.ICoreRoleService;
import com.rain.service.ICoreUserService;
import com.rain.utils.SecurityUtils;
import com.rain.utils.StringUtils;
import com.rain.utils.poi.ExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户信息
 * 
 * @author rain-hechang
 */
@Api(tags = "用户信息模块")
@RestController
@RequestMapping("/system/user")
public class CoreUserController extends BaseController
{
    private final ICoreUserService userService;

    private final ICoreRoleService roleService;

    private final ICoreDeptService deptService;

    private final ICorePostService postService;

    public CoreUserController(ICoreUserService userService, ICoreRoleService roleService, ICoreDeptService deptService, ICorePostService postService) {
        this.userService = userService;
        this.roleService = roleService;
        this.deptService = deptService;
        this.postService = postService;
    }

    /**
     * @Description:  获取用户列表
     * @param user
     * @return: com.rain.core.page.TableDataInfo
     * @Author:  rain-he
     */
    @ApiOperation("用户信息")
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(CoreUser user)
    {
        startPage();
        List<CoreUser> list = userService.selectUserList(user);
        return getDataTable(list);
    }

    /**
     * @Description:  用户导出
     * @param response
     * @param user
     * @return: void
     * @Author:  rain-he
     */
    @ApiOperation("用户导出")
    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:user:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, CoreUser user)
    {
        List<CoreUser> list = userService.selectUserList(user);
        ExcelUtil<CoreUser> util = new ExcelUtil<CoreUser>(CoreUser.class);
        util.exportExcel(response, list, "用户数据");
    }

    /**
     * @Description:  用户导入
     * @param file
     * @param updateSupport
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("用户导入")
    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('system:user:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<CoreUser> util = new ExcelUtil<CoreUser>(CoreUser.class);
        List<CoreUser> userList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = userService.importUser(userList, updateSupport, operName);
        return success(message);
    }

    /**
     * @Description:  用户导入模板
     * @param response
     * @return: void
     * @Author:  rain-he
     */
    @ApiOperation("用户导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<CoreUser> util = new ExcelUtil<CoreUser>(CoreUser.class);
        util.importTemplateExcel(response, "用户数据");
    }

    /**
     * @Description:  根据用户编号获取详细信息
     * @param userId
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("用户获取详细信息")
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping(value = { "/", "/{userId}" })
    public AjaxResult getInfo(@PathVariable(value = "userId", required = false) Long userId)
    {
        userService.checkUserDataScope(userId);
        AjaxResult ajax = AjaxResult.success();
        List<CoreRole> roles = roleService.selectRoleAll();
        ajax.put("roles", CoreUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        ajax.put("posts", postService.selectPostAll());
        if (StringUtils.isNotNull(userId))
        {
            CoreUser CoreUser = userService.selectUserById(userId);
            ajax.put(AjaxResult.DATA_TAG, CoreUser);
            ajax.put("postIds", postService.selectPostListByUserId(userId));
            ajax.put("roleIds", CoreUser.getRoles().stream().map(CoreRole::getRoleId).collect(Collectors.toList()));
        }
        return ajax;
    }

    /**
     * @Description:  新增用户
     * @param user
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("用户新增")
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody CoreUser user)
    {
        deptService.checkDeptDataScope(user.getDeptId());
        roleService.checkRoleDataScope(user.getRoleIds());
        if (!userService.checkUserNameUnique(user))
        {
            return error("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user))
        {
            return error("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user))
        {
            return error("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setCreateBy(getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return toAjax(userService.insertUser(user));
    }

    /**
     * @Description:  删除用户
     * @param user
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("用户修改")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody CoreUser user)
    {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        deptService.checkDeptDataScope(user.getDeptId());
        roleService.checkRoleDataScope(user.getRoleIds());
        if (!userService.checkUserNameUnique(user))
        {
            return error("修改用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user))
        {
            return error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user))
        {
            return error("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUpdateBy(getUsername());
        return toAjax(userService.updateUser(user));
    }

    /**
     * @Description:  删除用户
     * @param userIds
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("用户删除")
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds)
    {
        if (ArrayUtils.contains(userIds, getUserId()))
        {
            return error("当前用户不能删除");
        }
        return toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * @Description:  重置密码
     * @param user
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("用户密码重置")
    @PreAuthorize("@ss.hasPermi('system:user:resetPwd')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public AjaxResult resetPwd(@RequestBody CoreUser user)
    {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(getUsername());
        return toAjax(userService.resetPwd(user));
    }

    /**
     * @Description:  状态修改
     * @param user
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("用户状态修改")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody CoreUser user)
    {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        user.setUpdateBy(getUsername());
        return toAjax(userService.updateUserStatus(user));
    }

    /**
     * @Description:  获取部门树列表
     * @param userId
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("用户编号获取授权角色")
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping("/authRole/{userId}")
    public AjaxResult authRole(@PathVariable("userId") Long userId)
    {
        AjaxResult ajax = AjaxResult.success();
        CoreUser user = userService.selectUserById(userId);
        List<CoreRole> roles = roleService.selectRolesByUserId(userId);
        ajax.put("user", user);
        ajax.put("roles", CoreUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        return ajax;
    }

    /**
     * @Description:  获取部门树列表
     * @param userId
     * @param roleIds
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("用户角色授权")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.GRANT)
    @PutMapping("/authRole")
    public AjaxResult insertAuthRole(Long userId, Long[] roleIds)
    {
        userService.checkUserDataScope(userId);
        roleService.checkRoleDataScope(roleIds);
        userService.insertUserAuth(userId, roleIds);
        return success();
    }

    /**
     * @Description:  获取部门树列表
     * @param dept
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("部门树列表")
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/deptTree")
    public AjaxResult deptTree(CoreDept dept)
    {
        return success(deptService.selectDeptTreeList(dept));
    }
}
