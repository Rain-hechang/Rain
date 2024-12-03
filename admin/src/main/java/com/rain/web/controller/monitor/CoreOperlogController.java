package com.rain.web.controller.monitor;

import com.rain.annotation.Log;
import com.rain.core.controller.BaseController;
import com.rain.core.domain.AjaxResult;
import com.rain.core.page.TableDataInfo;
import com.rain.domain.CoreOperLog;
import com.rain.enums.BusinessType;
import com.rain.service.ICoreOperLogService;
import com.rain.utils.poi.ExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 操作日志记录
 * 
 * @author rain-hechang
 */
@Api(tags = "日志记录模块")
@RestController
@RequestMapping("/monitor/operlog")
public class CoreOperlogController extends BaseController
{
    private final ICoreOperLogService operLogService;

    public CoreOperlogController(ICoreOperLogService operLogService) {
        this.operLogService = operLogService;
    }

    /**
     * @Description:  获取日志列表
     * @param operLog
     * @return: com.rain.core.page.TableDataInfo
     * @Author:  rain-he
     */
    @ApiOperation("获取日志列表")
    @PreAuthorize("@ss.hasPermi('monitor:operlog:list')")
    @GetMapping("/list")
    public TableDataInfo list(CoreOperLog operLog)
    {
        startPage();
        List<CoreOperLog> list = operLogService.selectOperLogList(operLog);
        return getDataTable(list);
    }

    /**
     * @Description:导出日志
     * @param response
     * @param operLog
     * @return: void
     * @Author:  rain-he
     */

    @ApiOperation("导出日志")
    @Log(title = "操作日志", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('monitor:operlog:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, CoreOperLog operLog)
    {
        List<CoreOperLog> list = operLogService.selectOperLogList(operLog);
        ExcelUtil<CoreOperLog> util = new ExcelUtil<CoreOperLog>(CoreOperLog.class);
        util.exportExcel(response, list, "操作日志");
    }

    /**
     * @Description:  删除日志
     * @param operIds
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("删除日志")
    @Log(title = "操作日志", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('monitor:operlog:remove')")
    @DeleteMapping("/{operIds}")
    public AjaxResult remove(@PathVariable Long[] operIds)
    {
        return toAjax(operLogService.deleteOperLogByIds(operIds));
    }

    /**
     * @Description:  清空日志
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("清空日志")
    @Log(title = "操作日志", businessType = BusinessType.CLEAN)
    @PreAuthorize("@ss.hasPermi('monitor:operlog:remove')")
    @DeleteMapping("/clean")
    public AjaxResult clean()
    {
        operLogService.cleanOperLog();
        return success();
    }
}
