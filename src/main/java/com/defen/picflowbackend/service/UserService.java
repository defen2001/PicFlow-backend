package com.defen.picflowbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.defen.picflowbackend.model.dto.user.UserLoginRequest;
import com.defen.picflowbackend.model.dto.user.UserQueryRequest;
import com.defen.picflowbackend.model.dto.user.UserRegisterRequest;
import com.defen.picflowbackend.model.entity.User;
import com.defen.picflowbackend.model.vo.LoginUserVO;
import com.defen.picflowbackend.model.vo.UserVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author chendefeng
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-03-06 11:52:09
*/
public interface UserService extends IService<User> {

    /**
     *  用户注册
     * @param userRegisterRequest 用户
     * @return
     */
    long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登录
     *
     * @param userLoginRequest 包含用户账号和密码的登录请求实体
     * @return 返回脱敏后的用户信息
     */
    UserVo userLogin(UserLoginRequest userLoginRequest, HttpServletRequest httpServletRequest);

    /**
     * 获取登录用户信息
     */
    User getCurrentUser(HttpServletRequest httpServletRequest);

    /**
     * 用户注销
     * @param httpServletRequest
     */
    Boolean logout(HttpServletRequest httpServletRequest);

    /**
     * 获取加密后的密码。
     * 使用MD5算法结合预定义的盐值对用户密码进行加密处理。
     *
     * @param userPassword 用户原始密码
     * @return 加密后的密码字符串
     */
    String getEncryptPassword(String userPassword);

    /**
     * 获取脱敏的已登录用户信息
     *
     * @return LoginUserVO
     */
    LoginUserVO getLoginUserVo(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @return UserVo
     */
    UserVo getUserVo(User user);

    /**
     * 获取脱敏的用户信息列表
     *
     * @return List<UserVo>
     */
    List<UserVo> getUserVOList(List<User> userList);

    /**
     * 查询请求
     * @param userQueryRequest 查询请求参数
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

}
