package com.defen.picflowbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.defen.picflowbackend.model.dto.space.SpaceAddRequest;
import com.defen.picflowbackend.model.dto.space.SpaceEditRequest;
import com.defen.picflowbackend.model.dto.space.SpaceQueryRequest;
import com.defen.picflowbackend.model.entity.Space;
import com.defen.picflowbackend.model.entity.User;
import com.defen.picflowbackend.model.vo.SpaceVo;

import javax.servlet.http.HttpServletRequest;

/**
 * @author chendefeng
 * @description 针对表【space(空间)】的数据库操作Service
 * @createDate 2025-08-14 10:54:22
 */
public interface SpaceService extends IService<Space> {

    /**
     * 校验空间
     *
     * @param space
     * @param add   判断是否为空间创建操作
     */
    void validSpace(Space space, boolean add);

    /**
     * 获取空间包装类
     *
     * @param space
     * @return
     */
    SpaceVo getSpaceVo(Space space, HttpServletRequest request);

    /**
     * 获取空间封装类（分页）
     *
     * @param spacePage
     * @param request
     * @return
     */
    Page<SpaceVo> getSpaceVoPage(Page<Space> spacePage, HttpServletRequest request);

    /**
     * 获取查询对象
     *
     * @param spaceQueryRequest
     * @return
     */
    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);

    /**
     * 编辑空间
     *
     * @param spaceEditRequest
     * @param loginUser
     */
    void editSpace(SpaceEditRequest spaceEditRequest, User loginUser);

    /**
     * 创建空间
     *
     * @param spaceAddRequest
     * @param loginUser
     * @return
     */
    long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);

    /**
     * 根据空间级别填充空间对象
     *
     * @param space
     */
    void fillSpaceBySpaceLevel(Space space);

    /**
     * 校验空间权限
     *
     * @param loginUser
     * @param space
     */
    void checkSpaceAuth(Space space, User loginUser);

}
