package com.rain.web.controller.system;

import com.rain.core.controller.BaseController;
import com.rain.core.domain.AjaxResult;
import com.rain.core.domain.model.RegisterBody;
import com.rain.service.ICoreConfigService;
import com.rain.utils.StringUtils;
import com.rain.web.service.CoreRegisterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 注册验证
 * 
 * @author rain-hechang
 */
@Api(tags = "注册校验模块")
@RestController
public class CoreRegisterController extends BaseController
{
    private final CoreRegisterService registerService;

    private final ICoreConfigService configService;

    public CoreRegisterController(CoreRegisterService registerService, ICoreConfigService configService) {
        this.registerService = registerService;
        this.configService = configService;
    }

    /**
     * @Description:
     * @param user
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("注册校验")
    @PostMapping("/register")
    public AjaxResult register(@RequestBody RegisterBody user)
    {
        if (!("true".equals(configService.selectConfigByKey("Core.account.registerUser"))))
        {
            return error("当前系统没有开启注册功能！");
        }
        String msg = registerService.register(user);
        return StringUtils.isEmpty(msg) ? success() : error(msg);
    }
}
