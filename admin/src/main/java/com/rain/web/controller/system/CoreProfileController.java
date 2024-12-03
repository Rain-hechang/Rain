package com.rain.web.controller.system;

import com.rain.annotation.Log;
import com.rain.config.RainConfig;
import com.rain.core.controller.BaseController;
import com.rain.core.domain.AjaxResult;
import com.rain.core.domain.entity.CoreUser;
import com.rain.core.domain.model.LoginUser;
import com.rain.enums.BusinessType;
import com.rain.service.ICoreUserService;
import com.rain.utils.SecurityUtils;
import com.rain.utils.StringUtils;
import com.rain.utils.file.FileUploadUtils;
import com.rain.utils.file.MimeTypeUtils;
import com.rain.web.service.TokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 个人信息 业务处理
 * 
 * @author rain-hechang
 */
@Api(tags = "个人信息模块")
@RestController
@RequestMapping("/system/user/profile")
public class CoreProfileController extends BaseController
{
    private final ICoreUserService userService;

    private final TokenService tokenService;

    public CoreProfileController(ICoreUserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    /**
     * @Description: 个人信息
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("个人信息")
    @GetMapping
    public AjaxResult profile()
    {
        LoginUser loginUser = getLoginUser();
        CoreUser user = loginUser.getUser();
        AjaxResult ajax = AjaxResult.success(user);
        ajax.put("roleGroup", userService.selectUserRoleGroup(loginUser.getUsername()));
        ajax.put("postGroup", userService.selectUserPostGroup(loginUser.getUsername()));
        return ajax;
    }

    /**
     * @Description:  修改用户
     * @param user
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("修改用户")
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult updateProfile(@RequestBody CoreUser user)
    {
        LoginUser loginUser = getLoginUser();
        CoreUser currentUser = loginUser.getUser();
        currentUser.setNickName(user.getNickName());
        currentUser.setEmail(user.getEmail());
        currentUser.setPhonenumber(user.getPhonenumber());
        currentUser.setSex(user.getSex());
        if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(currentUser))
        {
            return error("修改用户'" + loginUser.getUsername() + "'失败，手机号码已存在");
        }
        if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(currentUser))
        {
            return error("修改用户'" + loginUser.getUsername() + "'失败，邮箱账号已存在");
        }
        if (userService.updateUserProfile(currentUser) > 0)
        {
            // 更新缓存用户信息
            tokenService.setLoginUser(loginUser);
            return success();
        }
        return error("修改个人信息异常，请联系管理员");
    }

    /**
     * @Description:  重置密码
     * @param oldPassword
     * @param newPassword
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("重置密码")
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updatePwd")
    public AjaxResult updatePwd(String oldPassword, String newPassword)
    {
        LoginUser loginUser = getLoginUser();
        String userName = loginUser.getUsername();
        String password = loginUser.getPassword();
        if (!SecurityUtils.matchesPassword(oldPassword, password))
        {
            return error("修改密码失败，旧密码错误");
        }
        if (SecurityUtils.matchesPassword(newPassword, password))
        {
            return error("新密码不能与旧密码相同");
        }
        newPassword = SecurityUtils.encryptPassword(newPassword);
        if (userService.resetUserPwd(userName, newPassword) > 0)
        {
            // 更新缓存用户密码
            loginUser.getUser().setPassword(newPassword);
            tokenService.setLoginUser(loginUser);
            return success();
        }
        return error("修改密码异常，请联系管理员");
    }

    /**
     * @Description:  头像上传
     * @param file
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("头像上传")
    @Log(title = "用户头像", businessType = BusinessType.UPDATE)
    @PostMapping("/avatar")
    public AjaxResult avatar(@RequestParam("avatarfile") MultipartFile file) throws Exception
    {
        if (!file.isEmpty())
        {
            LoginUser loginUser = getLoginUser();
            String avatar = FileUploadUtils.upload(RainConfig.getAvatarPath(), file, MimeTypeUtils.IMAGE_EXTENSION);
            if (userService.updateUserAvatar(loginUser.getUsername(), avatar))
            {
                AjaxResult ajax = AjaxResult.success();
                ajax.put("imgUrl", avatar);
                // 更新缓存用户头像
                loginUser.getUser().setAvatar(avatar);
                tokenService.setLoginUser(loginUser);
                return ajax;
            }
        }
        return error("上传图片异常，请联系管理员");
    }
}
