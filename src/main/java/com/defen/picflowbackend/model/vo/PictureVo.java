package com.defen.picflowbackend.model.vo;

import cn.hutool.json.JSONUtil;
import com.defen.picflowbackend.model.entity.Picture;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class PictureVo implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 图片 url
     */
    private String url;

    /**
     * 图片名称
     */
    private String name;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签（JSON 数组）
     */
    private List<String> tags;

    /**
     * 图片体积
     */
    private Long picSize;

    /**
     * 图片宽度
     */
    private Integer picWidth;

    /**
     * 图片高度
     */
    private Integer picHeight;

    /**
     * 图片宽高比例
     */
    private Double picScale;

    /**
     * 图片类型
     */
    private String picFormat;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建用户信息
     */
    private UserVo user;

    /**
     * 封装类转对象
     */
    public static Picture voToObj(PictureVo pictureVo) {
        if (pictureVo == null) return null;
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureVo, picture);
        // 类型不同，需要转换
        picture.setTags(JSONUtil.toJsonStr(pictureVo.getTags()));
        return picture;
    }

    /**
     * 对象转封装类
     */
    public static PictureVo objToVo(Picture picture) {
        if (picture == null) return null;
        PictureVo pictureVo = new PictureVo();
        BeanUtils.copyProperties(picture, pictureVo);
        List<Integer> res = new ArrayList<>();
        res.stream().mapToInt(Integer::intValue).toArray();
        // 类型不同，需要转换
        pictureVo.setTags(JSONUtil.toList(picture.getTags(), String.class));
        return pictureVo;
    }
}
