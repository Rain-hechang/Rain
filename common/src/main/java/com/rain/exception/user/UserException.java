package com.rain.exception.user;

import com.rain.exception.base.BaseException;

/**
 * 用户信息异常类
 * 
 * @author rain-hechang
 */
public class UserException extends BaseException
{
    private static final long serialVersionUID = 1L;

    public UserException(String code, Object[] args)
    {
        super("user", code, args, null);
    }
}
