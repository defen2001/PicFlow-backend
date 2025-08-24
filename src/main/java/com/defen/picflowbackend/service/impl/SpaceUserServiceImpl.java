package com.defen.picflowbackend.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.defen.picflowbackend.exception.BusinessException;
import com.defen.picflowbackend.exception.ErrorCode;
import com.defen.picflowbackend.exception.ExceptionUtils;
import com.defen.picflowbackend.mapper.SpaceUserMapper;
import com.defen.picflowbackend.model.dto.spaceuser.SpaceUserAddRequest;
import com.defen.picflowbackend.model.dto.spaceuser.SpaceUserQueryRequest;
import com.defen.picflowbackend.model.entity.Space;
import com.defen.picflowbackend.model.entity.SpaceUser;
import com.defen.picflowbackend.model.entity.User;
import com.defen.picflowbackend.model.enums.SpaceRoleEnum;
import com.defen.picflowbackend.model.vo.SpaceUserVo;
import com.defen.picflowbackend.model.vo.SpaceVo;
import com.defen.picflowbackend.model.vo.UserVo;
import com.defen.picflowbackend.service.SpaceService;
import com.defen.picflowbackend.service.SpaceUserService;
import com.defen.picflowbackend.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author chendefeng
 * @description 针对表【space_user(空间用户关联)】的数据库操作Service实现
 * @createDate 2025-08-23 11:00:12
 */
@Service
public class SpaceUserServiceImpl extends ServiceImpl<SpaceUserMapper, SpaceUser>
        implements SpaceUserService {

    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private SpaceService spaceService;

    /**
     * 校验空间成员
     *
     * @param spaceUser
     * @param add       判断是否为空间成员添加操作
     */
    @Override
    public void validSpaceUser(SpaceUser spaceUser, boolean add) {
        ExceptionUtils.throwIf(spaceUser == null, ErrorCode.PARAM_ERROR);
        // 创建时，空间 id 和用户 id 必填
        Long userId = spaceUser.getUserId();
        Long spaceId = spaceUser.getSpaceId();
        if (add) {
            ExceptionUtils.throwIf(ObjectUtil.hasEmpty(userId, spaceId), ErrorCode.PARAM_ERROR);
            User user = userService.getById(userId);
            ExceptionUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
            Space space = spaceService.getById(spaceId);
            ExceptionUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
            boolean exists = this.lambdaQuery()
                    .eq(SpaceUser::getUserId, userId)
                    .eq(SpaceUser::getSpaceId, spaceId)
                    .exists();
            ExceptionUtils.throwIf(exists, ErrorCode.OPERATION_ERROR, "已添加该成员");

        }
        // 校验空间角色
        String spaceRole = spaceUser.getSpaceRole();
        SpaceRoleEnum spaceRoleEnum = SpaceRoleEnum.getEnumByValue(spaceRole);
        if (spaceRole != null && spaceRoleEnum == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "空间角色不存在");
        }
    }

    /**
     * 获取空间成员包装类
     *
     * @param spaceUser
     * @param request
     * @return
     */
    @Override
    public SpaceUserVo getSpaceUserVo(SpaceUser spaceUser, HttpServletRequest request) {
        // 对象转封装类
        SpaceUserVo spaceUserVo = SpaceUserVo.objToVo(spaceUser);
        // 关联查询用户信息
        Long userId = spaceUser.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVo userVo = userService.getUserVo(user);
            spaceUserVo.setUser(userVo);
        }
        // 关联查询空间信息
        Long spaceId = spaceUser.getSpaceId();
        if (spaceId != null && spaceId > 0) {
            Space space = spaceService.getById(spaceId);
            SpaceVo spaceVo = spaceService.getSpaceVo(space, request);
            spaceUserVo.setSpace(spaceVo);
        }
        return spaceUserVo;
    }

    /**
     * 获取空间成员封装类
     *
     * @param spaceUserList
     */
    @Override
    public List<SpaceUserVo> getSpaceUserVoList(List<SpaceUser> spaceUserList) {
        // 判断输入列表是否为空
        if (CollUtil.isEmpty(spaceUserList)) {
            return Collections.emptyList();
        }
        // 对象类标 =》 封装对象列表
        List<SpaceUserVo> spaceUserVoList = spaceUserList.stream().map(SpaceUserVo::objToVo).collect(Collectors.toList());
        // 1.收集需要关联查询的 用户 id 和空间 id
        Set<Long> userIdSet = spaceUserList.stream().map(SpaceUser::getUserId).collect(Collectors.toSet());
        Set<Long> spaceIdSet = spaceUserList.stream().map(SpaceUser::getSpaceId).collect(Collectors.toSet());
        // 2.批零查询用户和空间
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        Map<Long, List<Space>> sapceIdSpaceListMap = spaceService.listByIds(spaceIdSet).stream()
                .collect(Collectors.groupingBy(Space::getId));
        // 3.填充SpaceVoList 的用户和空间信息
        spaceUserVoList.forEach(spaceUserVo -> {
            Long userId = spaceUserVo.getUserId();
            Long spaceId = spaceUserVo.getSpaceId();
            // 填充用户信息
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            spaceUserVo.setUser(userService.getUserVo(user));
            // 填充空间信息
            Space space = null;
            if (sapceIdSpaceListMap.containsKey(spaceId)) {
                space = sapceIdSpaceListMap.get(spaceId).get(0);
            }
            spaceUserVo.setSpace(SpaceVo.objToVo(space));
        });
        return spaceUserVoList;
    }

    /**
     * 获取查询对象
     *
     * @param spaceUserQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest) {
        QueryWrapper<SpaceUser> queryWrapper = new QueryWrapper<>();
        if (spaceUserQueryRequest == null) {
            return queryWrapper;
        }
        // 从·对象中取值
        Long id = spaceUserQueryRequest.getId();
        Long spaceId = spaceUserQueryRequest.getSpaceId();
        Long userId = spaceUserQueryRequest.getUserId();
        String spaceRole = spaceUserQueryRequest.getSpaceRole();
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceId), "space_id", spaceId);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "user_id", userId);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceRole), "space_role", spaceRole);
        return queryWrapper;
    }

    /**
     * 添加空间成员
     *
     * @param spaceUserAddRequest
     */
    @Override
    public long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest) {
        // 参数校验
        ExceptionUtils.throwIf(spaceUserAddRequest == null, ErrorCode.PARAM_ERROR);
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(spaceUserAddRequest, spaceUser);
        validSpaceUser(spaceUser, true);
        // 数据库操作
        boolean result = this.save(spaceUser);
        ExceptionUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return spaceUser.getId();
    }
}




