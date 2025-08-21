package com.defen.picflowbackend.controller;

import com.defen.picflowbackend.common.ApiResponse;
import com.defen.picflowbackend.exception.ErrorCode;
import com.defen.picflowbackend.exception.ExceptionUtils;
import com.defen.picflowbackend.model.dto.space.analyze.*;
import com.defen.picflowbackend.model.entity.Space;
import com.defen.picflowbackend.model.entity.User;
import com.defen.picflowbackend.model.vo.space.analyze.*;
import com.defen.picflowbackend.service.SpaceAnalyzeService;
import com.defen.picflowbackend.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/space/analyze")
public class SpaceAnalyzeController {

    @Resource
    private SpaceAnalyzeService spaceAnalyzeService;

    @Resource
    private UserService userService;

    /**
     * 获取空间使用状态
     */
    @PostMapping("/usage")
    public ApiResponse<SpaceUsageAnalyzeResponse> getSpaceUsageAnalyze(
            @RequestBody SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest,
            HttpServletRequest request
    ) {
        ExceptionUtils.throwIf(spaceUsageAnalyzeRequest == null, ErrorCode.PARAM_ERROR);
        User loginUser = userService.getCurrentUser(request);
        SpaceUsageAnalyzeResponse spaceUsageAnalyze = spaceAnalyzeService.getSpaceUsageAnalyze(spaceUsageAnalyzeRequest, loginUser);
        return ApiResponse.success(spaceUsageAnalyze);
    }

    /**
     * 获取空间分类使用数量
     */
    @PostMapping("/category")
    public ApiResponse<List<SpaceCategoryAnalyzeResponse>> getSpaceCategoryAnalyze(@RequestBody SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest, HttpServletRequest request) {
        ExceptionUtils.throwIf(spaceCategoryAnalyzeRequest == null, ErrorCode.PARAM_ERROR);
        User loginUser = userService.getCurrentUser(request);
        List<SpaceCategoryAnalyzeResponse> resultList = spaceAnalyzeService.getSpaceCategoryAnalyze(spaceCategoryAnalyzeRequest, loginUser);
        return ApiResponse.success(resultList);
    }

    /**
     * 获取空间标签使用分析
     */
    @PostMapping("/tag")
    public ApiResponse<List<SpaceTagAnalyzeResponse>> getSpaceTagAnalyze(@RequestBody SpaceTagAnalyzeRequest spaceTagAnalyzeRequest, HttpServletRequest request) {
        ExceptionUtils.throwIf(spaceTagAnalyzeRequest == null, ErrorCode.PARAM_ERROR);
        User loginUser = userService.getCurrentUser(request);
        List<SpaceTagAnalyzeResponse> resultList = spaceAnalyzeService.getSpaceTagsAnalyze(spaceTagAnalyzeRequest, loginUser);
        return ApiResponse.success(resultList);
    }

    /**
     * 获取空间大小使用分析
     */
    @PostMapping("/size")
    public ApiResponse<List<SpaceSizeAnalyzeResponse>> getSpaceSizeAnalyze(@RequestBody SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest, HttpServletRequest request) {
        ExceptionUtils.throwIf(spaceSizeAnalyzeRequest == null, ErrorCode.PARAM_ERROR);
        User loginUser = userService.getCurrentUser(request);
        List<SpaceSizeAnalyzeResponse> resultList = spaceAnalyzeService.getSpaceSizeAnalyze(spaceSizeAnalyzeRequest, loginUser);
        return ApiResponse.success(resultList);
    }

    /**
     * 获取空间用户上传情况
     */
    @PostMapping("/user")
    public ApiResponse<List<SpaceUserAnalyzeResponse>> getSpaceUserAnalyze(@RequestBody SpaceUserAnalyzeRequest spaceUserAnalyzeRequest, HttpServletRequest request) {
        ExceptionUtils.throwIf(spaceUserAnalyzeRequest == null, ErrorCode.PARAM_ERROR);
        User loginUser = userService.getCurrentUser(request);
        List<SpaceUserAnalyzeResponse> resultList = spaceAnalyzeService.getSpaceUserAnalyze(spaceUserAnalyzeRequest, loginUser);
        return ApiResponse.success(resultList);
    }

    /**
     * 获取空间使用排行
     */
    @PostMapping("/rank")
    public ApiResponse<List<Space>> getSpaceRankAnalyze(@RequestBody SpaceRankAnalyzeRequest spaceRankAnalyzeRequest, HttpServletRequest request) {
        ExceptionUtils.throwIf(spaceRankAnalyzeRequest == null, ErrorCode.PARAM_ERROR);
        User loginUser = userService.getCurrentUser(request);
        List<Space> resultList = spaceAnalyzeService.getSpaceRankAnalyze(spaceRankAnalyzeRequest, loginUser);
        return ApiResponse.success(resultList);
    }

}
