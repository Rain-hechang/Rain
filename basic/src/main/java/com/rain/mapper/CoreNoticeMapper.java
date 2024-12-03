package com.rain.mapper;

import com.rain.domain.CoreNotice;

import java.util.List;

/**
 * 通知公告表 数据层
 * 
 * @author rain-hechang
 */
public interface CoreNoticeMapper
{
    /**
     * 查询公告信息
     * 
     * @param noticeId 公告ID
     * @return 公告信息
     */
    public CoreNotice selectNoticeById(Long noticeId);

    /**
     * 查询公告列表
     * 
     * @param notice 公告信息
     * @return 公告集合
     */
    public List<CoreNotice> selectNoticeList(CoreNotice notice);

    /**
     * 新增公告
     * 
     * @param notice 公告信息
     * @return 结果
     */
    public int insertNotice(CoreNotice notice);

    /**
     * 修改公告
     * 
     * @param notice 公告信息
     * @return 结果
     */
    public int updateNotice(CoreNotice notice);

    /**
     * 批量删除公告
     * 
     * @param noticeId 公告ID
     * @return 结果
     */
    public int deleteNoticeById(Long noticeId);

    /**
     * 批量删除公告信息
     * 
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    public int deleteNoticeByIds(Long[] noticeIds);
}
