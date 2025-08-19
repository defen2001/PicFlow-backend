package com.defen.picflowbackend.model.dto.picture;

import com.defen.picflowbackend.api.aliyunai.model.CreateOutPaintingTaskRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * AI 扩图请求类
 */
@Data
public class CreatePictureOutPaintingTaskRequest implements Serializable {

    /**
     * 图片 id
     */
    private Long pictureId;

    /**
     * 扩图参数
     */
    private CreateOutPaintingTaskRequest.Parameters parameters;

    private static final long serialVersionUID = 1L;
}
