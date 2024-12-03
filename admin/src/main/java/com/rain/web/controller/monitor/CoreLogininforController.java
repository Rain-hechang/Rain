package com.rain.web.controller.monitor;

import com.rain.annotation.Log;
import com.rain.core.controller.BaseController;
import com.rain.core.domain.AjaxResult;
import com.rain.core.page.TableDataInfo;
import com.rain.domain.CoreLogininfor;
import com.rain.enums.BusinessType;
import com.rain.service.ICoreLogininforService;
import com.rain.utils.poi.ExcelUtil;
import com.rain.web.service.CorePasswordService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 系统访问记录
 * 
 * @author rain-hechang
 */
@RestController
@RequestMapping("/monitor/logininfor")
public class CoreLogininforController extends BaseController
{
    private final ICoreLogininforService logininforService;

    private final CorePasswordService passwordService;

    public CoreLogininforController(ICoreLogininforService logininforService, CorePasswordService passwordService) {
        this.logininforService = logininforService;
        this.passwordService = passwordService;
    }

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:list')")
    @GetMapping("/list")
    public TableDataInfo list(CoreLogininfor logininfor)
    {
        startPage();
        List<CoreLogininfor> list = logininforService.selectLogininforList(logininfor);
        return getDataTable(list);
    }

    @Log(title = "登录日志", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, CoreLogininfor logininfor)
    {
        List<CoreLogininfor> list = logininforService.selectLogininforList(logininfor);
        ExcelUtil<CoreLogininfor> util = new ExcelUtil<CoreLogininfor>(CoreLogininfor.class);
        util.exportExcel(response, list, "登录日志");
    }

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:remove')")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{infoIds}")
    public AjaxResult remove(@PathVariable Long[] infoIds)
    {
        return toAjax(logininforService.deleteLogininforByIds(infoIds));
    }

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:remove')")
    @Log(title = "登录日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public AjaxResult clean()
    {
        logininforService.cleanLogininfor();
        return success();
    }

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:unlock')")
    @Log(title = "账户解锁", businessType = BusinessType.OTHER)
    @GetMapping("/unlock/{userName}")
    public AjaxResult unlock(@PathVariable("userName") String userName)
    {
        passwordService.clearLoginRecordCache(userName);
        return success();
    }
}
