package com.defen.picflowbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.defen.picflowbackend.model.dto.picture.PictureQueryRequest;
import com.defen.picflowbackend.model.dto.picture.PictureReviewRequest;
import com.defen.picflowbackend.model.dto.picture.PictureUploadRequest;
import com.defen.picflowbackend.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.defen.picflowbackend.model.entity.User;
import com.defen.picflowbackend.model.vo.PictureVo;
import org.springframework.web.multipart.MultipartFile;

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
     * @param multipartFile
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVo uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, User loginUser);

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
     * @param picturePage
     * @param request
     * @return
     */
    Page<PictureVo> getPictureVoPage(Page<Picture> picturePage, HttpServletRequest request);

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
}
