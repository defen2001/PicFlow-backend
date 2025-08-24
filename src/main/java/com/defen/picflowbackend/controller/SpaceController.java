package com.defen.picflowbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.defen.picflowbackend.annotation.AuthCheck;
import com.defen.picflowbackend.common.ApiResponse;
import com.defen.picflowbackend.common.DeleteRequest;
import com.defen.picflowbackend.constant.UserConstant;
import com.defen.picflowbackend.exception.BusinessException;
import com.defen.picflowbackend.exception.ErrorCode;
import com.defen.picflowbackend.exception.ExceptionUtils;
import com.defen.picflowbackend.manager.auth.SpaceUserAuthManager;
import com.defen.picflowbackend.model.dto.space.*;
import com.defen.picflowbackend.model.entity.Space;
import com.defen.picflowbackend.model.entity.User;
import com.defen.picflowbackend.model.enums.SpaceLevelEnum;
import com.defen.picflowbackend.model.vo.SpaceVo;
import com.defen.picflowbackend.service.SpaceService;
import com.defen.picflowbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/space")
@Slf4j
public class SpaceController {

    @Resource
    private UserService userService;

    @Resource
    private SpaceService spaceService;

    @Resource
    private SpaceUserAuthManager spaceUserAuthManager;

    /**
     * 创建空间
     */
    @PostMapping("/add")
    public ApiResponse<Long> addSpace(@RequestBody SpaceAddRequest spaceAddRequest, HttpServletRequest request) {
        ExceptionUtils.throwIf(spaceAddRequest == null, ErrorCode.PARAM_ERROR);
        User loginUser = userService.getCurrentUser(request);
        long newId = spaceService.addSpace(spaceAddRequest, loginUser);
        return ApiResponse.success(newId);
    }

    /**
     * 删除空间
     */
    @PostMapping("/delete")
    public ApiResponse<Boolean> deleteSpace(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        User loginUser = userService.getCurrentUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Space oldSpace = spaceService.getById(id);
        ExceptionUtils.throwIf(oldSpace == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        spaceService.checkSpaceAuth(oldSpace, loginUser);
        // 操作数据库
        boolean result = spaceService.removeById(id);
        ExceptionUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ApiResponse.success(true);
    }

    /**
     * 更新空间（仅管理员可用）
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public ApiResponse<Boolean> updateSpace(@RequestBody SpaceUpdateRequest spaceUpdateRequest) {
        if (spaceUpdateRequest == null || spaceUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        // 将实体类和 DTO 进行转换
        Space space = new Space();
        BeanUtils.copyProperties(spaceUpdateRequest, space);
        // 自动填充数据
        spaceService.fillSpaceBySpaceLevel(space);
        // 数据校验
        spaceService.validSpace(space, false);
        // 判断是否存在
        long id = spaceUpdateRequest.getId();
        Space oldSpace = spaceService.getById(id);
        ExceptionUtils.throwIf(oldSpace == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = spaceService.updateById(space);
        ExceptionUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ApiResponse.success(true);
    }

    /**
     * 根据 id 获取空间（仅管理员可用）
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public ApiResponse<Space> getSpaceById(long id, HttpServletRequest request) {
        ExceptionUtils.throwIf(id <= 0, ErrorCode.PARAM_ERROR);
        // 查询数据库
        Space space = spaceService.getById(id);
        ExceptionUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ApiResponse.success(space);
    }

    /**
     * 根据 id 获取空间（封装类）
     */
    @GetMapping("/get/vo")
    public ApiResponse<SpaceVo> getSpaceVOById(long id, HttpServletRequest request) {
        ExceptionUtils.throwIf(id <= 0, ErrorCode.PARAM_ERROR);
        // 查询数据库
        Space space = spaceService.getById(id);
        ExceptionUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR);
        SpaceVo spaceVo = spaceService.getSpaceVo(space, request);
        User loginUser = userService.getCurrentUser(request);
        List<String> permissionList = spaceUserAuthManager.getPermissionList(space, loginUser);
        spaceVo.setPermissionList(permissionList);
        // 获取封装类
        return ApiResponse.success(spaceVo);
    }

    /**
     * 分页获取空间列表（仅管理员可用）
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public ApiResponse<Page<Space>> listSpaceByPage(@RequestBody SpaceQueryRequest spaceQueryRequest) {
        long current = spaceQueryRequest.getCurrent();
        long size = spaceQueryRequest.getPageSize();
        // 查询数据库
        Page<Space> spacePage = spaceService.page(new Page<>(current, size),
                spaceService.getQueryWrapper(spaceQueryRequest));
        return ApiResponse.success(spacePage);
    }

    /**
     * 分页获取空间列表（封装类）
     */
    @PostMapping("/list/page/vo")
    public ApiResponse<Page<SpaceVo>> listSpaceVOByPage(@RequestBody SpaceQueryRequest spaceQueryRequest,
                                                        HttpServletRequest request) {
        long size = spaceQueryRequest.getPageSize();
        long current = spaceQueryRequest.getCurrent();
        // 限制爬虫
        ExceptionUtils.throwIf(size > 20, ErrorCode.PARAM_ERROR);
        // 查询数据库
        Page<Space> spaceVoPage = spaceService.page(new Page<>(current, size),
                spaceService.getQueryWrapper(spaceQueryRequest));
        // 获取封装类
        return ApiResponse.success(spaceService.getSpaceVoPage(spaceVoPage, request));
    }

    /**
     * 编辑空间（给用户使用）
     */
    @PostMapping("/edit")
    public ApiResponse<Boolean> editSpace(@RequestBody SpaceEditRequest spaceEditRequest, HttpServletRequest request) {
        if (spaceEditRequest == null || spaceEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        User loginUser = userService.getCurrentUser(request);
        spaceService.editSpace(spaceEditRequest, loginUser);
        return ApiResponse.success(true);
    }

    /**
     * 获取空间级别列表，便于前端展示
     *
     * @return
     */
    @GetMapping("/list/level")
    public ApiResponse<List<SpaceLevel>> listSpaceLevel() {
        List<SpaceLevel> spaceLevelList = Arrays.stream(SpaceLevelEnum.values()) // 获取所有枚举
                .map(spaceLevelEnum -> new SpaceLevel(
                        spaceLevelEnum.getValue(),
                        spaceLevelEnum.getText(),
                        spaceLevelEnum.getMaxCount(),
                        spaceLevelEnum.getMaxSize()))
                .collect(Collectors.toList());
        return ApiResponse.success(spaceLevelList);
    }
}
