package com.rain.web.controller.monitor;

import com.rain.annotation.Log;
import com.rain.constant.CacheConstants;
import com.rain.core.controller.BaseController;
import com.rain.core.domain.AjaxResult;
import com.rain.core.domain.model.LoginUser;
import com.rain.core.page.TableDataInfo;
import com.rain.core.redis.RedisCache;
import com.rain.domain.CoreUserOnline;
import com.rain.enums.BusinessType;
import com.rain.service.ICoreUserOnlineService;
import com.rain.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 在线用户监控
 *
 * @author rain-hechang
 */
@Api(tags = "在线用户模块")
@RestController
@RequestMapping("/monitor/online")
public class CoreUserOnlineController extends BaseController {
    private final ICoreUserOnlineService userOnlineService;

    private final RedisCache redisCache;

    public CoreUserOnlineController(ICoreUserOnlineService userOnlineService, RedisCache redisCache) {
        this.userOnlineService = userOnlineService;
        this.redisCache = redisCache;
    }

    /**
     * @Description:  列表
     * @param ipaddr
     * @param userName
     * @return: com.rain.core.page.TableDataInfo
     * @Author:  rain-he
     */
    @PreAuthorize("@ss.hasPermi('monitor:online:list')")
    @GetMapping("/list")
    public TableDataInfo list(String ipaddr, String userName) {
        Collection<String> keys = redisCache.keys(CacheConstants.LOGIN_TOKEN_KEY + "*");
        List<CoreUserOnline> userOnlineList = new ArrayList<CoreUserOnline>();
        for (String key : keys) {
            LoginUser user = redisCache.getCacheObject(key);
            if (StringUtils.isNotEmpty(ipaddr) && StringUtils.isNotEmpty(userName)) {
                userOnlineList.add(userOnlineService.selectOnlineByInfo(ipaddr, userName, user));
            } else if (StringUtils.isNotEmpty(ipaddr)) {
                userOnlineList.add(userOnlineService.selectOnlineByIpaddr(ipaddr, user));
            } else if (StringUtils.isNotEmpty(userName) && StringUtils.isNotNull(user.getUser())) {
                userOnlineList.add(userOnlineService.selectOnlineByUserName(userName, user));
            } else {
                userOnlineList.add(userOnlineService.loginUserToUserOnline(user));
            }
        }
        Collections.reverse(userOnlineList);
        userOnlineList.removeAll(Collections.singleton(null));
        return getDataTable(userOnlineList);
    }

    /**
     * @Description:  强退用户
     * @param tokenId
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("强退用户")
    @PreAuthorize("@ss.hasPermi('monitor:online:forceLogout')")
    @Log(title = "在线用户", businessType = BusinessType.FORCE)
    @DeleteMapping("/{tokenId}")
    public AjaxResult forceLogout(@PathVariable String tokenId) {
        redisCache.deleteObject(CacheConstants.LOGIN_TOKEN_KEY + tokenId);
        return success();
    }
}
