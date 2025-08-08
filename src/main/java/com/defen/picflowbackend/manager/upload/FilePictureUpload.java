package com.defen.picflowbackend.manager.upload;

import cn.hutool.core.io.FileUtil;
import com.defen.picflowbackend.exception.ErrorCode;
import com.defen.picflowbackend.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * 本地图片上传
 */
@Service
public class FilePictureUpload extends PictureUploadTemplate {

        /**
         * 校验文件
         *
         * @param inputSource
         */
    @Override
    protected void validPicture(Object inputSource) {
        MultipartFile multipartFile = (MultipartFile) inputSource;
        ExceptionUtils.throwIf(multipartFile == null, ErrorCode.PARAM_ERROR, "文件不能为空");
        // 1. 校验文件大小
        long fileSize = multipartFile.getSize();
        final long ONE_M = 1024 * 1024;
        ExceptionUtils.throwIf(fileSize > 5 * ONE_M, ErrorCode.PARAM_ERROR, "文件大小不能超过 5M");
        // 2. 校验文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        // 允许上传的文件后缀
        final List<String> ALLOW_FORMAT_LIST = Arrays.asList("jpeg", "jpg", "png", "webp");
        ExceptionUtils.throwIf(!ALLOW_FORMAT_LIST.contains(fileSuffix), ErrorCode.PARAM_ERROR, "文件类型错误");

    }

    /**
     * 获取输入源原始文件名
     *
     * @param inputSource
     * @return
     */
    @Override
    protected String getOriginFilename(Object inputSource) {
        MultipartFile multipartFile = (MultipartFile) inputSource;
        return multipartFile.getOriginalFilename();
    }

    /**
     * 处理输入源并处理本地临时文件
     *
     * @param inputSource
     * @param file
     * @throws Exception
     */
    @Override
    protected void processFile(Object inputSource, File file) throws Exception {
        MultipartFile multipartFile = (MultipartFile) inputSource;
        multipartFile.transferTo(file);
    }
}
