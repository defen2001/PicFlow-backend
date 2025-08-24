package com.defen.picflowbackend.controller;

import cn.hutool.core.util.ObjectUtil;
import com.defen.picflowbackend.common.ApiResponse;
import com.defen.picflowbackend.common.DeleteRequest;
import com.defen.picflowbackend.exception.BusinessException;
import com.defen.picflowbackend.exception.ErrorCode;
import com.defen.picflowbackend.exception.ExceptionUtils;
import com.defen.picflowbackend.manager.auth.annotation.SaSpaceCheckPermission;
import com.defen.picflowbackend.manager.auth.model.SpaceUserPermissionConstant;
import com.defen.picflowbackend.model.dto.spaceuser.SpaceUserAddRequest;
import com.defen.picflowbackend.model.dto.spaceuser.SpaceUserEditRequest;
import com.defen.picflowbackend.model.dto.spaceuser.SpaceUserQueryRequest;
import com.defen.picflowbackend.model.entity.SpaceUser;
import com.defen.picflowbackend.model.entity.User;
import com.defen.picflowbackend.model.vo.SpaceUserVo;
import com.defen.picflowbackend.service.SpaceUserService;
import com.defen.picflowbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/spaceUser")
@Slf4j
public class SpaceUserController {

    @Resource
    private SpaceUserService spaceUserService;

    @Resource
    private UserService userService;

    /**
     * 添加成员到空间
     */
    @PostMapping("/add")
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    public ApiResponse<Long> addSpaceUser(@RequestBody SpaceUserAddRequest spaceUserAddRequest, HttpServletRequest request) {
        ExceptionUtils.throwIf(spaceUserAddRequest == null, ErrorCode.PARAM_ERROR);
        long id = spaceUserService.addSpaceUser(spaceUserAddRequest);
        return ApiResponse.success(id);
    }

    /**
     * 从空间移除成员
     */
    @PostMapping("/delete")
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    public ApiResponse<Boolean> deleteSpaceUser(@RequestBody DeleteRequest deleteRequest,
                                                HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        long id = deleteRequest.getId();
        // 判断是否存在
        SpaceUser oldSpaceUser = spaceUserService.getById(id);
        ExceptionUtils.throwIf(oldSpaceUser == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = spaceUserService.removeById(id);
        ExceptionUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ApiResponse.success(true);
    }

    /**
     * 查询某个成员在某个空间的信息
     */
    @PostMapping("/get")
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    public ApiResponse<SpaceUser> getSpaceUser(@RequestBody SpaceUserQueryRequest spaceUserQueryRequest) {
        // 参数校验
        ExceptionUtils.throwIf(spaceUserQueryRequest == null, ErrorCode.PARAM_ERROR);
        Long spaceId = spaceUserQueryRequest.getSpaceId();
        Long userId = spaceUserQueryRequest.getUserId();
        ExceptionUtils.throwIf(ObjectUtil.hasEmpty(spaceId, userId), ErrorCode.PARAM_ERROR);
        // 查询数据库
        SpaceUser spaceUser = spaceUserService.getOne(spaceUserService.getQueryWrapper(spaceUserQueryRequest));
        ExceptionUtils.throwIf(spaceUser == null, ErrorCode.NOT_FOUND_ERROR);
        return ApiResponse.success(spaceUser);
    }

    /**
     * 查询成员信息列表
     */
    @PostMapping("/list")
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    public ApiResponse<List<SpaceUserVo>> listSpaceUser(@RequestBody SpaceUserQueryRequest spaceUserQueryRequest,
                                                        HttpServletRequest request) {
        ExceptionUtils.throwIf(spaceUserQueryRequest == null, ErrorCode.PARAM_ERROR);
        List<SpaceUser> spaceUserList = spaceUserService.list(
                spaceUserService.getQueryWrapper(spaceUserQueryRequest)
        );
        return ApiResponse.success(spaceUserService.getSpaceUserVoList(spaceUserList));
    }

    /**
     * 编辑成员信息（设置权限）
     */
    @PostMapping("/edit")
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    public ApiResponse<Boolean> editSpaceUser(@RequestBody SpaceUserEditRequest spaceUserEditRequest,
                                              HttpServletRequest request) {
        if (spaceUserEditRequest == null || spaceUserEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        // 将实体类和 DTO 进行转换
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(spaceUserEditRequest, spaceUser);
        // 数据校验
        spaceUserService.validSpaceUser(spaceUser, false);
        // 判断是否存在
        long id = spaceUserEditRequest.getId();
        SpaceUser oldSpaceUser = spaceUserService.getById(id);
        ExceptionUtils.throwIf(oldSpaceUser == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = spaceUserService.updateById(spaceUser);
        ExceptionUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ApiResponse.success(true);
    }

    /**
     * 查询我加入的团队空间列表
     */
    @PostMapping("/list/my")
    public ApiResponse<List<SpaceUserVo>> listMyTeamSpace(HttpServletRequest request) {
        User loginUser = userService.getCurrentUser(request);
        SpaceUserQueryRequest spaceUserQueryRequest = new SpaceUserQueryRequest();
        spaceUserQueryRequest.setUserId(loginUser.getId());
        List<SpaceUser> spaceUserList = spaceUserService.list(
                spaceUserService.getQueryWrapper(spaceUserQueryRequest)
        );
        return ApiResponse.success(spaceUserService.getSpaceUserVoList(spaceUserList));
    }
}
