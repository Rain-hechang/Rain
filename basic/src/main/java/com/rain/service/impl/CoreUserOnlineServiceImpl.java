package com.rain.service.impl;

import com.rain.core.domain.model.LoginUser;
import com.rain.domain.CoreUserOnline;
import com.rain.service.ICoreUserOnlineService;
import com.rain.utils.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 在线用户 服务层处理
 * 
 * @author rain-hechang
 */
@Service
public class CoreUserOnlineServiceImpl implements ICoreUserOnlineService
{
    /**
     * 通过登录地址查询信息
     * 
     * @param ipaddr 登录地址
     * @param user 用户信息
     * @return 在线用户信息
     */
    @Override
    public CoreUserOnline selectOnlineByIpaddr(String ipaddr, LoginUser user)
    {
        if (StringUtils.equals(ipaddr, user.getIpaddr()))
        {
            return loginUserToUserOnline(user);
        }
        return null;
    }

    /**
     * 通过用户名称查询信息
     * 
     * @param userName 用户名称
     * @param user 用户信息
     * @return 在线用户信息
     */
    @Override
    public CoreUserOnline selectOnlineByUserName(String userName, LoginUser user)
    {
        if (StringUtils.equals(userName, user.getUsername()))
        {
            return loginUserToUserOnline(user);
        }
        return null;
    }

    /**
     * 通过登录地址/用户名称查询信息
     * 
     * @param ipaddr 登录地址
     * @param userName 用户名称
     * @param user 用户信息
     * @return 在线用户信息
     */
    @Override
    public CoreUserOnline selectOnlineByInfo(String ipaddr, String userName, LoginUser user)
    {
        if (StringUtils.equals(ipaddr, user.getIpaddr()) && StringUtils.equals(userName, user.getUsername()))
        {
            return loginUserToUserOnline(user);
        }
        return null;
    }

    /**
     * 设置在线用户信息
     * 
     * @param user 用户信息
     * @return 在线用户
     */
    @Override
    public CoreUserOnline loginUserToUserOnline(LoginUser user)
    {
        if (StringUtils.isNull(user) || StringUtils.isNull(user.getUser()))
        {
            return null;
        }
        CoreUserOnline CoreUserOnline = new CoreUserOnline();
        CoreUserOnline.setTokenId(user.getToken());
        CoreUserOnline.setUserName(user.getUsername());
        CoreUserOnline.setIpaddr(user.getIpaddr());
        CoreUserOnline.setLoginLocation(user.getLoginLocation());
        CoreUserOnline.setBrowser(user.getBrowser());
        CoreUserOnline.setOs(user.getOs());
        CoreUserOnline.setLoginTime(user.getLoginTime());
        if (StringUtils.isNotNull(user.getUser().getDept()))
        {
            CoreUserOnline.setDeptName(user.getUser().getDept().getDeptName());
        }
        return CoreUserOnline;
    }
}
