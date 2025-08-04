package com.defen.picflowbackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PictureTagCategory implements Serializable {

    private static final long serialVersionUID = 2898923186223813582L;

    /**
     * 标签数组
     */
    private List<String> tagList;

    /**
     * 分类数组
     */
    private List<String> categoryList;
}
