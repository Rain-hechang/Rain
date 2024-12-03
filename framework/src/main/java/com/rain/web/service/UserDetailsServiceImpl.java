package com.rain.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.rain.core.domain.entity.CoreUser;
import com.rain.core.domain.model.LoginUser;
import com.rain.enums.UserStatus;
import com.rain.exception.ServiceException;
import com.rain.utils.MessageUtils;
import com.rain.utils.StringUtils;
import com.rain.service.ICoreUserService;

/**
 * 用户验证处理
 *
 * @author rain-hechang
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService
{
    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private ICoreUserService userService;
    
    @Autowired
    private CorePasswordService passwordService;

    @Autowired
    private CorePermissionService permissionService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        CoreUser user = userService.selectUserByUserName(username);
        if (StringUtils.isNull(user))
        {
            log.info("登录用户：{} 不存在.", username);
            throw new ServiceException(MessageUtils.message("user.not.exists"));
        }
        else if (UserStatus.DELETED.getCode().equals(user.getDelFlag()))
        {
            log.info("登录用户：{} 已被删除.", username);
            throw new ServiceException(MessageUtils.message("user.password.delete"));
        }
        else if (UserStatus.DISABLE.getCode().equals(user.getStatus()))
        {
            log.info("登录用户：{} 已被停用.", username);
            throw new ServiceException(MessageUtils.message("user.blocked"));
        }

        passwordService.validate(user);

        return createLoginUser(user);
    }

    public UserDetails createLoginUser(CoreUser user)
    {
        return new LoginUser(user.getUserId(), user.getDeptId(), user, permissionService.getMenuPermission(user));
    }
}
