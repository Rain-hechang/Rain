package com.rain.web.service;

import com.rain.service.ICoreConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.rain.constant.CacheConstants;
import com.rain.constant.Constants;
import com.rain.constant.UserConstants;
import com.rain.core.domain.entity.CoreUser;
import com.rain.core.domain.model.RegisterBody;
import com.rain.core.redis.RedisCache;
import com.rain.exception.user.CaptchaException;
import com.rain.exception.user.CaptchaExpireException;
import com.rain.utils.MessageUtils;
import com.rain.utils.SecurityUtils;
import com.rain.utils.StringUtils;
import com.rain.manager.AsyncManager;
import com.rain.manager.factory.AsyncFactory;
import com.rain.service.ICoreUserService;

/**
 * 注册校验方法
 * 
 * @author rain-hechang
 */
@Component
public class CoreRegisterService
{
    @Autowired
    private ICoreUserService userService;

    @Autowired
    private ICoreConfigService configService;

    @Autowired
    private RedisCache redisCache;

    /**
     * 注册
     */
    public String register(RegisterBody registerBody)
    {
        String msg = "", username = registerBody.getUsername(), password = registerBody.getPassword();
        CoreUser CoreUser = new CoreUser();
        CoreUser.setUserName(username);

        // 验证码开关
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        if (captchaEnabled)
        {
            validateCaptcha(username, registerBody.getCode(), registerBody.getUuid());
        }

        if (StringUtils.isEmpty(username))
        {
            msg = "用户名不能为空";
        }
        else if (StringUtils.isEmpty(password))
        {
            msg = "用户密码不能为空";
        }
        else if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH)
        {
            msg = "账户长度必须在2到20个字符之间";
        }
        else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH)
        {
            msg = "密码长度必须在5到20个字符之间";
        }
        else if (!userService.checkUserNameUnique(CoreUser))
        {
            msg = "保存用户'" + username + "'失败，注册账号已存在";
        }
        else
        {
            CoreUser.setNickName(username);
            CoreUser.setPassword(SecurityUtils.encryptPassword(password));
            boolean regFlag = userService.registerUser(CoreUser);
            if (!regFlag)
            {
                msg = "注册失败,请联系系统管理人员";
            }
            else
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.REGISTER, MessageUtils.message("user.register.success")));
            }
        }
        return msg;
    }

    /**
     * 校验验证码
     * 
     * @param username 用户名
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public void validateCaptcha(String username, String code, String uuid)
    {
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);
        if (captcha == null)
        {
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha))
        {
            throw new CaptchaException();
        }
    }
}
