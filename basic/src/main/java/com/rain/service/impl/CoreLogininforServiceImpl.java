package com.rain.service.impl;

import com.rain.domain.CoreLogininfor;
import com.rain.mapper.CoreLogininforMapper;
import com.rain.service.ICoreLogininforService;
import com.rain.utils.time.MaxAndMinUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 系统访问日志情况信息 服务层处理
 * 
 * @author rain-hechang
 */
@Service
public class CoreLogininforServiceImpl implements ICoreLogininforService
{

    @Autowired
    private CoreLogininforMapper logininforMapper;

    /**
     * 新增系统登录日志
     * 
     * @param logininfor 访问日志对象
     */
    @Override
    public void insertLogininfor(CoreLogininfor logininfor)
    {
        logininforMapper.insertLogininfor(logininfor);
    }

    /**
     * 查询系统登录日志集合
     * 
     * @param logininfor 访问日志对象
     * @return 登录记录集合
     */
    @Override
    public List<CoreLogininfor> selectLogininforList(CoreLogininfor logininfor)
    {
        Map<String, Object> params = logininfor.getParams();
        String beginTime = String.valueOf(params.get("beginTime"));
        String endTime = String.valueOf(params.get("endTime"));
        String begin = MaxAndMinUtil.getStartTime(beginTime);
        String end = MaxAndMinUtil.getEndTime(endTime);
        params.put("beginTime" , begin);
        params.put("endTime" , end);
        return logininforMapper.selectLogininforList(logininfor);
    }

    /**
     * 批量删除系统登录日志
     * 
     * @param infoIds 需要删除的登录日志ID
     * @return 结果
     */
    @Override
    public int deleteLogininforByIds(Long[] infoIds)
    {
        return logininforMapper.deleteLogininforByIds(infoIds);
    }

    /**
     * 清空系统登录日志
     */
    @Override
    public void cleanLogininfor()
    {
        logininforMapper.cleanLogininfor();
    }
}
