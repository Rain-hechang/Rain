package com.rain.web.controller.system;

import com.rain.annotation.Log;
import com.rain.core.controller.BaseController;
import com.rain.core.domain.AjaxResult;
import com.rain.core.domain.entity.CoreDictType;
import com.rain.core.page.TableDataInfo;
import com.rain.enums.BusinessType;
import com.rain.service.ICoreDictTypeService;
import com.rain.utils.poi.ExcelUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 数据字典信息
 * 
 * @author rain-hechang
 */
@RestController
@RequestMapping("/system/dict/type")
public class CoreDictTypeController extends BaseController
{
    private final ICoreDictTypeService dictTypeService;

    public CoreDictTypeController(ICoreDictTypeService dictTypeService) {
        this.dictTypeService = dictTypeService;
    }

    /**
     * @Description:  列表
     * @param dictType
     * @return: com.rain.core.page.TableDataInfo
     * @Author:  rain-he
     */
    @PreAuthorize("@ss.hasPermi('system:dict:list')")
    @GetMapping("/list")
    public TableDataInfo list(CoreDictType dictType)
    {
        startPage();
        List<CoreDictType> list = dictTypeService.selectDictTypeList(dictType);
        return getDataTable(list);
    }

    /**
     * @Description:  导出
     * @param response
     * @param dictType
     * @return: void
     * @Author:  rain-he
     */
    @Log(title = "字典类型", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:dict:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, CoreDictType dictType)
    {
        List<CoreDictType> list = dictTypeService.selectDictTypeList(dictType);
        ExcelUtil<CoreDictType> util = new ExcelUtil<CoreDictType>(CoreDictType.class);
        util.exportExcel(response, list, "字典类型");
    }

    /**
     * @Description:  查询字典类型详细
     * @param dictId
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @PreAuthorize("@ss.hasPermi('system:dict:query')")
    @GetMapping(value = "/{dictId}")
    public AjaxResult getInfo(@PathVariable Long dictId)
    {
        return success(dictTypeService.selectDictTypeById(dictId));
    }

    /**
     * @Description:  新增字典类型
     * @param dict
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @PreAuthorize("@ss.hasPermi('system:dict:add')")
    @Log(title = "字典类型", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody CoreDictType dict)
    {
        if (!dictTypeService.checkDictTypeUnique(dict))
        {
            return error("新增字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        dict.setCreateBy(getUsername());
        return toAjax(dictTypeService.insertDictType(dict));
    }

    /**
     * @Description:  修改字典类型
     * @param dict
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
    @Log(title = "字典类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody CoreDictType dict)
    {
        if (!dictTypeService.checkDictTypeUnique(dict))
        {
            return error("修改字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        dict.setUpdateBy(getUsername());
        return toAjax(dictTypeService.updateDictType(dict));
    }

    /**
     * @Description:  删除字典类型
     * @param dictIds
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictIds}")
    public AjaxResult remove(@PathVariable Long[] dictIds)
    {
        dictTypeService.deleteDictTypeByIds(dictIds);
        return success();
    }

    /**
     * @Description:  刷新字典缓存
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    @Log(title = "字典类型", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public AjaxResult refreshCache()
    {
        dictTypeService.resetDictCache();
        return success();
    }

    /**
     * @Description:  获取字典选择框列表
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @GetMapping("/optionselect")
    public AjaxResult optionselect()
    {
        List<CoreDictType> dictTypes = dictTypeService.selectDictTypeAll();
        return success(dictTypes);
    }
}
