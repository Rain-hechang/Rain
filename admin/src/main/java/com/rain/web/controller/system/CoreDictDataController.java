package com.rain.web.controller.system;

import com.rain.annotation.Log;
import com.rain.core.controller.BaseController;
import com.rain.core.domain.AjaxResult;
import com.rain.core.domain.entity.CoreDictData;
import com.rain.core.page.TableDataInfo;
import com.rain.enums.BusinessType;
import com.rain.service.ICoreDictDataService;
import com.rain.service.ICoreDictTypeService;
import com.rain.utils.StringUtils;
import com.rain.utils.poi.ExcelUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据字典信息
 * 
 * @author rain-hechang
 */
@RestController
@RequestMapping("/system/dict/data")
public class CoreDictDataController extends BaseController
{
    private final ICoreDictDataService dictDataService;

    private final ICoreDictTypeService dictTypeService;

    public CoreDictDataController(ICoreDictDataService dictDataService, ICoreDictTypeService dictTypeService) {
        this.dictDataService = dictDataService;
        this.dictTypeService = dictTypeService;
    }

    /**
     * @Description:  列表
     * @param dictData
     * @return: com.rain.core.page.TableDataInfo
     * @Author:  rain-he
     */
    @PreAuthorize("@ss.hasPermi('system:dict:list')")
    @GetMapping("/list")
    public TableDataInfo list(CoreDictData dictData)
    {
        startPage();
        List<CoreDictData> list = dictDataService.selectDictDataList(dictData);
        return getDataTable(list);
    }

    /**
     * @Description:  导出
     * @param response
     * @param dictData
     * @return: void
     * @Author:  rain-he
     */
    @Log(title = "字典数据", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:dict:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, CoreDictData dictData)
    {
        List<CoreDictData> list = dictDataService.selectDictDataList(dictData);
        ExcelUtil<CoreDictData> util = new ExcelUtil<CoreDictData>(CoreDictData.class);
        util.exportExcel(response, list, "字典数据");
    }

    /**
     * @Description:  查询字典数据详细
     * @param dictCode
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @PreAuthorize("@ss.hasPermi('system:dict:query')")
    @GetMapping(value = "/{dictCode}")
    public AjaxResult getInfo(@PathVariable Long dictCode)
    {
        return success(dictDataService.selectDictDataById(dictCode));
    }

    /**
     * @Description:  查询字典数据详细
     * @param dictType
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @GetMapping(value = "/type/{dictType}")
    public AjaxResult dictType(@PathVariable String dictType)
    {
        List<CoreDictData> data = dictTypeService.selectDictDataByType(dictType);
        if (StringUtils.isNull(data))
        {
            data = new ArrayList<CoreDictData>();
        }
        return success(data);
    }

    /**
     * @Description:  新增字典类型
     * @param dict
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @PreAuthorize("@ss.hasPermi('system:dict:add')")
    @Log(title = "字典数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody CoreDictData dict)
    {
        dict.setCreateBy(getUsername());
        return toAjax(dictDataService.insertDictData(dict));
    }

    /**
     * @Description:  新增字典类型
     * @param dict
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
    @Log(title = "字典数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody CoreDictData dict)
    {
        dict.setUpdateBy(getUsername());
        return toAjax(dictDataService.updateDictData(dict));
    }

    /**
     * @Description:  删除字典类型
     * @param dictCodes
     * @return: com.rain.core.domain.AjaxResult
     * @Author:  rain-he
     */
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictCodes}")
    public AjaxResult remove(@PathVariable Long[] dictCodes)
    {
        dictDataService.deleteDictDataByIds(dictCodes);
        return success();
    }
}
