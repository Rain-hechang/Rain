package com.rain.web.controller.system;

import com.rain.annotation.Log;
import com.rain.core.controller.BaseController;
import com.rain.core.domain.AjaxResult;
import com.rain.core.page.TableDataInfo;
import com.rain.domain.CoreNotice;
import com.rain.enums.BusinessType;
import com.rain.service.ICoreNoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公告 信息操作处理
 * 
 * @author rain-hechang
 */
@Api(tags = "公告、信息操作模块")
@RestController
@RequestMapping("/system/notice")
public class CoreNoticeController extends BaseController
{
    private final ICoreNoticeService noticeService;

    public CoreNoticeController(ICoreNoticeService noticeService) {
        this.noticeService = noticeService;
    }

    /**
     * @Description:  获取通知公告列表
     * @param notice
     * @return: com.rain.core.page.TableDataInfo
     * @Author:  rain-he
     */
    @ApiOperation("获取通知公告列表")
    @PreAuthorize("@ss.hasPermi('system:notice:list')")
    @GetMapping("/list")
    public TableDataInfo list(CoreNotice notice)
    {
        startPage();
        List<CoreNotice> list = noticeService.selectNoticeList(notice);
        return getDataTable(list);
    }

    /**
     * @Description:  根据通知公告编号获取详细信息
     * @param noticeId
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("根据通知公告编号获取详细信息")
    @PreAuthorize("@ss.hasPermi('system:notice:query')")
    @GetMapping(value = "/{noticeId}")
    public AjaxResult getInfo(@PathVariable Long noticeId)
    {
        return success(noticeService.selectNoticeById(noticeId));
    }

    /**
     * @Description:  新增通知公告
     * @param notice
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("新增通知公告")
    @PreAuthorize("@ss.hasPermi('system:notice:add')")
    @Log(title = "通知公告", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody CoreNotice notice)
    {
        notice.setCreateBy(getUsername());
        return toAjax(noticeService.insertNotice(notice));
    }

    /**
     * @Description:  删除通知公告
     * @param notice
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("删除通知公告")
    @PreAuthorize("@ss.hasPermi('system:notice:edit')")
    @Log(title = "通知公告", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody CoreNotice notice)
    {
        notice.setUpdateBy(getUsername());
        return toAjax(noticeService.updateNotice(notice));
    }

    /**
     * @Description:  删除通知公告
     * @param noticeIds
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("删除通知公告")
    @PreAuthorize("@ss.hasPermi('system:notice:remove')")
    @Log(title = "通知公告", businessType = BusinessType.DELETE)
    @DeleteMapping("/{noticeIds}")
    public AjaxResult remove(@PathVariable Long[] noticeIds)
    {
        return toAjax(noticeService.deleteNoticeByIds(noticeIds));
    }
}
