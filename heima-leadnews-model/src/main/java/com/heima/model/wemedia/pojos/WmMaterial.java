package com.heima.model.wemedia.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 自媒体图文素材信息表
 *
 * @TableName wm_material
 */
@TableName(value = "wm_material")
@Data
public class WmMaterial implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 自媒体用户ID
     */
    @TableField(value = "user_id")
    private Integer userId;
    /**
     * 图片地址
     */
    @TableField(value = "url")
    private String url;
    /**
     * 素材类型
     * 0 图片
     * 1 视频
     */
    @TableField(value = "type")
    private Integer type;
    /**
     * 是否收藏
     */
    @TableField(value = "is_collection")
    private Integer isCollection;
    /**
     * 创建时间
     */
    @TableField(value = "created_time")
    private Date createdTime;
}