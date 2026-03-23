package com.dong.easyeval.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 卡券类型表
 * </p>
 *
 * @author dong
 * @since 2026-03-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_coupon_type")
public class TCouponType implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 类型ID
     */
    @TableId(value = "type_id", type = IdType.AUTO)
    private Long typeId;

    /**
     * 类型名称，描述卡券类型
     */
    @TableField("type_name")
    private String typeName;

    /**
     * 类型描述，详细说明卡券类型的信息
     */
    @TableField("type_description")
    private String typeDescription;

    /**
     * 类型属性，可包括卡券类型的相关属性
     */
    @TableField("type_properties")
    private String typeProperties;

    /**
     * 数据创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 数据最后修改时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    /**
     * 创建人ID
     */
    @TableField("created_by")
    private Long createdBy;

    /**
     * 最后修改人ID
     */
    @TableField("updated_by")
    private Long updatedBy;
}
