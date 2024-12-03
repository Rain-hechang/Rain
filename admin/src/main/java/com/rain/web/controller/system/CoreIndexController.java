package com.rain.web.controller.system;

import com.rain.config.RainConfig;
import com.rain.utils.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页
 *
 * @author rain-hechang
 */
@RestController
public class CoreIndexController
{
    /** 系统基础配置 */
    private final RainConfig rainConfig;

    public CoreIndexController(RainConfig rainConfig) {
        this.rainConfig = rainConfig;
    }

    /**
     * 访问首页，提示语
     */
    @RequestMapping("/")
    public String index()
    {
        return StringUtils.format("欢迎使用{}脚手架，当前版本：v{}。", rainConfig.getName(), rainConfig.getVersion());
    }
}
