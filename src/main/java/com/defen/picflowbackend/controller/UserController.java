package com.defen.picflowbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.defen.picflowbackend.annotation.AuthCheck;
import com.defen.picflowbackend.common.ApiResponse;
import com.defen.picflowbackend.common.DeleteRequest;
import com.defen.picflowbackend.constant.UserConstant;
import com.defen.picflowbackend.exception.BusinessException;
import com.defen.picflowbackend.exception.ErrorCode;
import com.defen.picflowbackend.exception.ExceptionUtils;
import com.defen.picflowbackend.model.dto.user.*;
import com.defen.picflowbackend.model.entity.User;
import com.defen.picflowbackend.model.vo.LoginUserVO;
import com.defen.picflowbackend.model.vo.UserVo;
import com.defen.picflowbackend.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户相关接口
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册接口
     *
     * @param userRegisterRequest 用户注册请求实体，包含用户账号、密码和确认密码
     * @return ApiResponse<Long> 返回一个ApiResponse对象，其中包含新注册用户的ID。如果注册成功，返回状态码200000；如果参数为空，抛出BusinessException异常，错误码为400001
     */
    @PostMapping("/register")
    public ApiResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "参数为空");
        }
        long result = userService.userRegister(userRegisterRequest);
        return ApiResponse.success(result);
    }

    /**
     * 用户登录接口
     *
     * @param userLoginRequest 包含用户账号和密码的登录请求实体
     * @return ApiResponse<UserLoginVo> 返回一个ApiResponse对象，其中包含登录成功后的用户信息。如果登录成功，返回状态码200000；如果参数为空，抛出BusinessException异常，错误码为400001
     */
    @PostMapping("/login")
    public ApiResponse<UserVo> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest httpServletRequest) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "参数为空");
        }
        UserVo userLogin = userService.userLogin(userLoginRequest, httpServletRequest);
        return ApiResponse.success(userLogin);
    }

    /**
     * 获取登录用户信息
     *
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public ApiResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User currentUser = userService.getCurrentUser(request);
        return ApiResponse.success(userService.getLoginUserVo(currentUser));
    }

    /**
     * 用户注销
     */
    @PostMapping("/logout")
    public ApiResponse<Boolean> logout(HttpServletRequest request) {
        Boolean result = userService.logout(request);
        return ApiResponse.success(result);
    }

    /**
     * 创建用户
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public ApiResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        ExceptionUtils.throwIf(userAddRequest == null, ErrorCode.PARAM_ERROR);
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        // 默认密码 12345678
        final String DEFAULT_PASSWORD = "12345678";
        String encryptPassword = userService.getEncryptPassword(DEFAULT_PASSWORD);
        user.setUserPassword(encryptPassword);
        boolean result = userService.save(user);
        ExceptionUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ApiResponse.success(user.getId());
    }

    /**
     * 根据 id 获取用户（仅管理员）
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public ApiResponse<User> getUserById(long id) {
        ExceptionUtils.throwIf(id <= 0, ErrorCode.PARAM_ERROR);
        User user = userService.getById(id);
        ExceptionUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ApiResponse.success(user);
    }

    /**
     * 根据 id 获取包装类
     */
    @GetMapping("/get/vo")
    public ApiResponse<UserVo> getUserVoById(long id) {
        ApiResponse<User> response = getUserById(id);
        User user = response.getData();
        return ApiResponse.success(userService.getUserVo(user));
    }

    /**
     * 删除用户
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public ApiResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return ApiResponse.success(b);
    }

    /**
     * 更新用户
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public ApiResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ExceptionUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ApiResponse.success(true);
    }

    /**
     * 分页获取用户封装列表（仅管理员）
     *
     * @param userQueryRequest 查询请求参数
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public ApiResponse<Page<UserVo>> listUserVoByPage(@RequestBody UserQueryRequest userQueryRequest) {
        ExceptionUtils.throwIf(userQueryRequest == null, ErrorCode.PARAM_ERROR);
        long current = userQueryRequest.getCurrent();
        long pageSize = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, pageSize),
                userService.getQueryWrapper(userQueryRequest));
        Page<UserVo> userVOPage = new Page<>(current, pageSize, userPage.getTotal());
        List<UserVo> userVOList = userService.getUserVOList(userPage.getRecords());
        userVOPage.setRecords(userVOList);
        return ApiResponse.success(userVOPage);
    }

}
