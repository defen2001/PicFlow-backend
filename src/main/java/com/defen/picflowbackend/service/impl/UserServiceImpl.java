package com.defen.picflowbackend.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.defen.picflowbackend.constant.UserConstant;
import com.defen.picflowbackend.exception.BusinessException;
import com.defen.picflowbackend.exception.ErrorCode;
import com.defen.picflowbackend.mapper.UserMapper;
import com.defen.picflowbackend.model.dto.user.UserLoginRequest;
import com.defen.picflowbackend.model.dto.user.UserQueryRequest;
import com.defen.picflowbackend.model.dto.user.UserRegisterRequest;
import com.defen.picflowbackend.model.entity.User;
import com.defen.picflowbackend.model.enums.UserRoleEnum;
import com.defen.picflowbackend.model.vo.LoginUserVO;
import com.defen.picflowbackend.model.vo.UserVo;
import com.defen.picflowbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author chendefeng
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2025-03-06 11:52:09
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    // 盐值
    private final String SALT = "pic_flow";

    /**
     * 用户注册
     * @param userRegisterRequest 用户
     * @return
     */
    @Override
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        // 1. 校验参数
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String confirmPassword = userRegisterRequest.getConfirmPassword();
        if (StrUtil.hasBlank(userAccount, userPassword, confirmPassword)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "账号密码不能为空");
        }
        if (!userPassword.equals(confirmPassword)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "两次密码不一致");
        }
        // 2. 检查账号唯一性（不重复）
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        Long count = this.baseMapper.selectCount(queryWrapper);
        if (count > 0){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户已存在");
        }
        // 3. 密码加密
        String encryptPassword = getEncryptPassword(userPassword);
        // 4. 保存到数据库中
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUsername("无名");
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.DB_ERROR, "注册失败，数据库错误");
        } 
        return user.getId();
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 包含用户账号和密码的登录请求对象
     * @return 返回包含用户信息的UserLoginVo对象，如果登录成功
     * @throws BusinessException 如果账号或密码为空、账号不存在或密码错误，则抛出此异常
     */
    @Override
    public UserVo userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        // 1.校验
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StrUtil.hasBlank(userAccount, userPassword)){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "账号密码不能为空");
        }
        // 2.密码加密（需与注册时的加密方式一致）
        String encryptPassword = getEncryptPassword(userPassword);

        // 3.查询数据库
        QueryWrapper <User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        queryWrapper.eq("user_password", encryptPassword);
        User user = this.getOne(queryWrapper);
        if (user == null){
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAM_ERROR, "账号不存在或密码错误");
        }
        // 4. 生成token
//        String token = JwtUtil.generateToken(user.getId().toString(), userAccount);

        // 5.封装返回请求
//        UserTokenVo userTokenVO = new UserTokenVo();
//        userTokenVO.setToken(token);
//        userTokenVO.setId(user.getId());
//        userTokenVO.setUserAccount(user.getUserAccount());
//        userTokenVO.setUserRole(user.getUserRole());

        // 6.保存用户登录态
//        HttpSession session = request.getSession();
//        session.setAttribute("userAccount", user.getUserAccount());
//        session.setAttribute("userId", user.getId());
//        session.setAttribute("userRole", user.getUserRole());
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE , user);
        return this.getUserVo(user);
    }

    /**
     * 获取登录用户信息
     * @param request
     * @return
     */
    @Override
    public User getCurrentUser(HttpServletRequest request) {
        Object object = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User user = (User) object;
        if (user == null || user.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 3. 查询数据库
        Long userId = user.getId();
        user = this.getById(userId);
        if (user == null) {
            throw  new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 4. 转换为VO对象
        return user;
    }

    /**
     * 用户注销
     * @param request
     */
    @Override
    public Boolean logout(HttpServletRequest request) {
        Object object = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (object == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登陆");
        }
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }

    /**
     * 获取加密后的密码。
     * 使用MD5算法结合预定义的盐值对用户密码进行加密处理。
     *
     * @param userPassword 用户原始密码
     * @return 加密后的密码字符串
     */
    @Override
    public String getEncryptPassword(String userPassword){
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

    /**
     * 获取脱敏的已登录用户信息
     *
     */
    @Override
    public LoginUserVO getLoginUserVo(User user) {
        if (user == null){
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    /**
     * 获取脱敏的用户信息
     *
     */
    @Override
    public UserVo getUserVo(User user) {
        if (user == null){
            return null;
        }
        UserVo userVO = new UserVo();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * 获取脱敏的用户信息列表
     *
     */
    @Override
    public List<UserVo> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVo).collect(Collectors.toList());
    }

    /**
     * 查询请求
     * @param userQueryRequest
     */
    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUsername();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), "user_role", userRole);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "user_account", userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userName), "username", userName);
        queryWrapper.like(StrUtil.isNotBlank(userProfile), "user_profile", userProfile);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

}




