package com.defen.picflowbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.defen.picflowbackend.model.dto.spaceuser.SpaceUserAddRequest;
import com.defen.picflowbackend.model.dto.spaceuser.SpaceUserQueryRequest;
import com.defen.picflowbackend.model.entity.SpaceUser;
import com.defen.picflowbackend.model.vo.SpaceUserVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author chendefeng
 * @description 针对表【space_user(空间用户关联)】的数据库操作Service
 * @createDate 2025-08-23 11:00:12
 */
public interface SpaceUserService extends IService<SpaceUser> {

    /**
     * 校验空间成员
     *
     * @param spaceUser
     * @param add       判断是否为空间创建操作
     */
    void validSpaceUser(SpaceUser spaceUser, boolean add);

    /**
     * 获取空间成员包装类
     *
     * @param spaceUser
     * @param request
     */
    SpaceUserVo getSpaceUserVo(SpaceUser spaceUser, HttpServletRequest request);

    /**
     * 获取空间成员封装类
     *
     * @param spaceUserList
     */
    List<SpaceUserVo> getSpaceUserVoList(List<SpaceUser> spaceUserList);

    /**
     * 获取查询对象
     *
     * @param spaceUserQueryRequest
     * @return
     */
    QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);

    /**
     * 添加空间成员
     *
     * @param spaceUserAddRequest
     */
    long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest);

}
