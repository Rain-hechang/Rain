package com.rain.web.controller.monitor;

import com.rain.constant.CacheConstants;
import com.rain.core.domain.AjaxResult;
import com.rain.domain.CoreCache;
import com.rain.utils.StringUtils;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 缓存监控
 * 
 * @author rain-hechang
 */
@RestController
@RequestMapping("/monitor/cache")
public class CacheController
{
    private final RedisTemplate<String, String> redisTemplate;

    private final static List<CoreCache> caches = new ArrayList<CoreCache>();
    {
        caches.add(new CoreCache(CacheConstants.LOGIN_TOKEN_KEY, "用户信息"));
        caches.add(new CoreCache(CacheConstants.CORE_CONFIG_KEY, "配置信息"));
        caches.add(new CoreCache(CacheConstants.CORE_DICT_KEY, "数据字典"));
        caches.add(new CoreCache(CacheConstants.CAPTCHA_CODE_KEY, "验证码"));
        caches.add(new CoreCache(CacheConstants.REPEAT_SUBMIT_KEY, "防重提交"));
        caches.add(new CoreCache(CacheConstants.RATE_LIMIT_KEY, "限流处理"));
        caches.add(new CoreCache(CacheConstants.PWD_ERR_CNT_KEY, "密码错误次数"));
    }

    public CacheController(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PreAuthorize("@ss.hasPermi('monitor:cache:list')")
    @GetMapping()
    public AjaxResult getInfo() throws Exception
    {
        Properties info = (Properties) redisTemplate.execute((RedisCallback<Object>) connection -> connection.info());
        Properties commandStats = (Properties) redisTemplate.execute((RedisCallback<Object>) connection -> connection.info("commandstats"));
        Object dbSize = redisTemplate.execute((RedisCallback<Object>) connection -> connection.dbSize());

        Map<String, Object> result = new HashMap<>(3);
        result.put("info", info);
        result.put("dbSize", dbSize);

        List<Map<String, String>> pieList = new ArrayList<>();
        commandStats.stringPropertyNames().forEach(key -> {
            Map<String, String> data = new HashMap<>(2);
            String property = commandStats.getProperty(key);
            data.put("name", StringUtils.removeStart(key, "cmdstat_"));
            data.put("value", StringUtils.substringBetween(property, "calls=", ",usec"));
            pieList.add(data);
        });
        result.put("commandStats", pieList);
        return AjaxResult.success(result);
    }

    @PreAuthorize("@ss.hasPermi('monitor:cache:list')")
    @GetMapping("/getNames")
    public AjaxResult cache()
    {
        return AjaxResult.success(caches);
    }

    @PreAuthorize("@ss.hasPermi('monitor:cache:list')")
    @GetMapping("/getKeys/{cacheName}")
    public AjaxResult getCacheKeys(@PathVariable String cacheName)
    {
        Set<String> cacheKeys = redisTemplate.keys(cacheName + "*");
        return AjaxResult.success(new TreeSet<>(cacheKeys));
    }

    @PreAuthorize("@ss.hasPermi('monitor:cache:list')")
    @GetMapping("/getValue/{cacheName}/{cacheKey}")
    public AjaxResult getCacheValue(@PathVariable String cacheName, @PathVariable String cacheKey)
    {
        String cacheValue = redisTemplate.opsForValue().get(cacheKey);
        CoreCache CoreCache = new CoreCache(cacheName, cacheKey, cacheValue);
        return AjaxResult.success(CoreCache);
    }

    @PreAuthorize("@ss.hasPermi('monitor:cache:list')")
    @DeleteMapping("/clearCacheName/{cacheName}")
    public AjaxResult clearCacheName(@PathVariable String cacheName)
    {
        Collection<String> cacheKeys = redisTemplate.keys(cacheName + "*");
        redisTemplate.delete(cacheKeys);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('monitor:cache:list')")
    @DeleteMapping("/clearCacheKey/{cacheKey}")
    public AjaxResult clearCacheKey(@PathVariable String cacheKey)
    {
        redisTemplate.delete(cacheKey);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('monitor:cache:list')")
    @DeleteMapping("/clearCacheAll")
    public AjaxResult clearCacheAll()
    {
        Collection<String> cacheKeys = redisTemplate.keys("*");
        redisTemplate.delete(cacheKeys);
        return AjaxResult.success();
    }
}
