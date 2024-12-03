package com.rain.web.controller.system;

import com.rain.annotation.Log;
import com.rain.core.controller.BaseController;
import com.rain.core.domain.AjaxResult;
import com.rain.core.page.TableDataInfo;
import com.rain.domain.CoreConfig;
import com.rain.enums.BusinessType;
import com.rain.service.ICoreConfigService;
import com.rain.utils.poi.ExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 参数配置 信息操作处理
 * 
 * @author rain-hechang
 */
@Api(tags = "系统参数模块")
@RestController
@RequestMapping("/system/config")
public class CoreConfigController extends BaseController
{
    private final ICoreConfigService configService;

    public CoreConfigController(ICoreConfigService configService) {
        this.configService = configService;
    }

    /**
     * @Description:  获取参数配置列表
     * @param config
     * @return: com.rain.core.page.TableDataInfo
     * @Author:  rain-he
     */
    @ApiOperation("获取参数配置列表")
    @PreAuthorize("@ss.hasPermi('system:config:list')")
    @GetMapping("/list")
    public TableDataInfo list(CoreConfig config)
    {
        startPage();
        List<CoreConfig> list = configService.selectConfigList(config);
        return getDataTable(list);
    }

    /**
     * @Description:  导出
     * @param response
     * @param config
     * @return: void
     * @Author:  rain-he
     */
    @ApiOperation("导出")
    @Log(title = "参数管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:config:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, CoreConfig config)
    {
        List<CoreConfig> list = configService.selectConfigList(config);
        ExcelUtil<CoreConfig> util = new ExcelUtil<CoreConfig>(CoreConfig.class);
        util.exportExcel(response, list, "参数数据");
    }

    /**
     * @Description:  根据参数编号获取详细信息
     * @param configId
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("根据参数编号获取详细信息")
    @PreAuthorize("@ss.hasPermi('system:config:query')")
    @GetMapping(value = "/{configId}")
    public AjaxResult getInfo(@PathVariable Long configId)
    {
        return success(configService.selectConfigById(configId));
    }

    /**
     * @Description:  根据参数键名查询参数值
     * @param configKey
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("根据参数键名查询参数值")
    @GetMapping(value = "/configKey/{configKey}")
    public AjaxResult getConfigKey(@PathVariable String configKey)
    {
        return success(configService.selectConfigByKey(configKey));
    }

    /**
     * @Description:  新增参数配置
     * @param config
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("新增参数配置")
    @PreAuthorize("@ss.hasPermi('system:config:add')")
    @Log(title = "参数管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody CoreConfig config)
    {
        if (!configService.checkConfigKeyUnique(config))
        {
            return error("新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        config.setCreateBy(getUsername());
        return toAjax(configService.insertConfig(config));
    }

    /**
     * @Description:  修改参数配置
     * @param config
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("修改参数配置")
    @PreAuthorize("@ss.hasPermi('system:config:edit')")
    @Log(title = "参数管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody CoreConfig config)
    {
        if (!configService.checkConfigKeyUnique(config))
        {
            return error("修改参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        config.setUpdateBy(getUsername());
        return toAjax(configService.updateConfig(config));
    }

    /**
     * @Description:  删除参数配置
     * @param configIds
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("删除参数配置")
    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @Log(title = "参数管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    public AjaxResult remove(@PathVariable Long[] configIds)
    {
        configService.deleteConfigByIds(configIds);
        return success();
    }

    /**
     * @Description:  刷新参数缓存
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("刷新参数缓存")
    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @Log(title = "参数管理", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public AjaxResult refreshCache()
    {
        configService.resetConfigCache();
        return success();
    }
}
