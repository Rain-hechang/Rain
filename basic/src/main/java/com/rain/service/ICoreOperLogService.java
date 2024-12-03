package com.rain.service;


import com.rain.domain.CoreOperLog;

import java.util.List;

/**
 * 操作日志 服务层
 * 
 * @author rain-hechang
 */
public interface ICoreOperLogService
{
    /**
     * 新增操作日志
     * 
     * @param operLog 操作日志对象
     */
    public void insertOperlog(CoreOperLog operLog);

    /**
     * 查询系统操作日志集合
     * 
     * @param operLog 操作日志对象
     * @return 操作日志集合
     */
    public List<CoreOperLog> selectOperLogList(CoreOperLog operLog);

    /**
     * 批量删除系统操作日志
     * 
     * @param operIds 需要删除的操作日志ID
     * @return 结果
     */
    public int deleteOperLogByIds(Long[] operIds);

    /**
     * 查询操作日志详细
     * 
     * @param operId 操作ID
     * @return 操作日志对象
     */
    public CoreOperLog selectOperLogById(Long operId);

    /**
     * 清空操作日志
     */
    public void cleanOperLog();
}
