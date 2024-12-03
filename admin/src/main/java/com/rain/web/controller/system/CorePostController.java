package com.rain.web.controller.system;

import com.rain.annotation.Log;
import com.rain.core.controller.BaseController;
import com.rain.core.domain.AjaxResult;
import com.rain.core.page.TableDataInfo;
import com.rain.domain.CorePost;
import com.rain.enums.BusinessType;
import com.rain.service.ICorePostService;
import com.rain.utils.poi.ExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 岗位信息操作处理
 * 
 * @author rain-hechang
 */
@Api(tags = "岗位信息模块")
@RestController
@RequestMapping("/system/post")
public class CorePostController extends BaseController
{
    private final ICorePostService postService;

    public CorePostController(ICorePostService postService) {
        this.postService = postService;
    }

    /**
     * @Description:  获取岗位列表
     * @param post
     * @return: TableDataInfo
     * @Author:  rain-he
     */
    @ApiOperation("获取岗位列表")
    @PreAuthorize("@ss.hasPermi('system:post:list')")
    @GetMapping("/list")
    public TableDataInfo list(CorePost post)
    {
        startPage();
        List<CorePost> list = postService.selectPostList(post);
        return getDataTable(list);
    }

    /**
     * @Description:  岗位导出
     * @param response
     * @param post
     * @return: void
     * @Author:  rain-he
     */
    @ApiOperation("岗位导出")
    @Log(title = "岗位管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:post:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, CorePost post)
    {
        List<CorePost> list = postService.selectPostList(post);
        ExcelUtil<CorePost> util = new ExcelUtil<CorePost>(CorePost.class);
        util.exportExcel(response, list, "岗位数据");
    }

    /**
     * @Description:  根据岗位编号获取详细信息
     * @param postId
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("根据岗位编号获取详细信息")
    @PreAuthorize("@ss.hasPermi('system:post:query')")
    @GetMapping(value = "/{postId}")
    public AjaxResult getInfo(@PathVariable Long postId)
    {
        return success(postService.selectPostById(postId));
    }

    /**
     * @Description:  新增岗位
     * @param post
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("新增岗位")
    @PreAuthorize("@ss.hasPermi('system:post:add')")
    @Log(title = "岗位管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody CorePost post)
    {
        if (!postService.checkPostNameUnique(post))
        {
            return error("新增岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        }
        else if (!postService.checkPostCodeUnique(post))
        {
            return error("新增岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        post.setCreateBy(getUsername());
        return toAjax(postService.insertPost(post));
    }

    /**
     * @Description:  修改岗位
     * @param post
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("修改岗位")
    @PreAuthorize("@ss.hasPermi('system:post:edit')")
    @Log(title = "岗位管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody CorePost post)
    {
        if (!postService.checkPostNameUnique(post))
        {
            return error("修改岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        }
        else if (!postService.checkPostCodeUnique(post))
        {
            return error("修改岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        post.setUpdateBy(getUsername());
        return toAjax(postService.updatePost(post));
    }

    /**
     * @Description:  删除岗位
     * @param postIds
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("删除岗位")
    @PreAuthorize("@ss.hasPermi('system:post:remove')")
    @Log(title = "岗位管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{postIds}")
    public AjaxResult remove(@PathVariable Long[] postIds)
    {
        return toAjax(postService.deletePostByIds(postIds));
    }

    /**
     * @Description:  获取岗位选择框列表
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @ApiOperation("获取岗位选择框列表")
    @GetMapping("/optionselect")
    public AjaxResult optionselect()
    {
        List<CorePost> posts = postService.selectPostAll();
        return success(posts);
    }
}
