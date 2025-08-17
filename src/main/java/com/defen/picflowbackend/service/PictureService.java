package com.defen.picflowbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.defen.picflowbackend.model.dto.picture.*;
import com.defen.picflowbackend.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.defen.picflowbackend.model.entity.User;
import com.defen.picflowbackend.model.vo.PictureVo;
import javax.servlet.http.HttpServletRequest;

/**
 * @author chendefeng
 * @description 针对表【picture(图片)】的数据库操作Service
 * @createDate 2025-07-23 18:10:26
 */
public interface PictureService extends IService<Picture> {

    /**
     * 上传图片
     *
     * @param inputSource
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVo uploadPicture(Object inputSource, PictureUploadRequest pictureUploadRequest, User loginUser);

    /**
     * 查询请求
     *
     * @param pictureQueryRequest
     * @return
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 获取单个图片封装
     *
     * @param picture
     * @param request
     * @return
     */
    PictureVo getPictureVo(Picture picture, HttpServletRequest request);

    /**
     * 分页获取图片封装
     *
     * @param pictureQueryRequest
     * @param request
     * @return
     */
    Page<PictureVo> getPictureVoPage(PictureQueryRequest pictureQueryRequest, HttpServletRequest request);

    /**
     * 分页获取图片封装（缓存）
     *
     * @param pictureQueryRequest
     * @param request
     * @return
     */
    Page<PictureVo> getPictureVoCachePage(PictureQueryRequest pictureQueryRequest, HttpServletRequest request);

    /**
     * 校验图片
     *
     * @param picture
     */
    void validPicture(Picture picture);

    /**
     * 图片审核
     *
     * @param pictureReviewRequest
     * @param loginUser
     */
    void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser);

    /**
     * 补充审核参数
     *
     * @param picture
     * @param loginUser
     */
    void fillReviewParams(Picture picture, User loginUser);

    /**
     * 批量抓取和创建图片
     *
     * @param pictureUploadByBatchRequest
     * @param loginUser
     * @return 成功创建图片数
     */
    Integer uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser);

    /**
     * 清理图片文件
     *
     * @param picture
     */
    void clearPictureFile(Picture picture);

    /**
     * 删除图片
     *
     * @param pictureId
     * @param loginUser
     */
    void deletePicture(long pictureId, User loginUser);

    /**
     * 编辑图片
     *
     * @param pictureEditRequest
     * @param loginUser
     */
    void editPicture(PictureEditRequest pictureEditRequest, User loginUser);

    /**
     * 校验空间图片权限
     *
     * @param picture
     * @param loginUser
     */
    void checkPictureAuth(Picture picture, User loginUser);

    /**
     * 批量修改图片信息
     *
     * @param pictureEditByBatchRequest
     * @param loginUser
     */
    void editPictureByBatch(PictureEditByBatchRequest pictureEditByBatchRequest, User loginUser);
}
