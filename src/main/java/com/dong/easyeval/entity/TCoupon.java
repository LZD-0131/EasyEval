package com.dong.easyeval.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 卡券表
 * </p>
 *
 * @author dong
 * @since 2026-03-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_coupon")
public class TCoupon implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 卡券ID
     */
    @TableId(value = "coupon_id", type = IdType.AUTO)
    private Long couponId;

    /**
     * 卡券代码，唯一标识卡券的代码
     */
    @TableField("coupon_code")
    private String couponCode;

    /**
     * 卡券类型，与卡券类型表相关联
     */
    @TableField("coupon_type")
    private Long couponType;

    /**
     * 卡券面值，可能是折扣金额或批改次数
     */
    @TableField("coupon_value")
    private BigDecimal couponValue;

    /**
     * 卡券有效期
     */
    @TableField("expiration_date")
    private LocalDate expirationDate;

    /**
     * 卡券状态，如未使用、已使用、过期
     */
    @TableField("coupon_status")
    private String couponStatus;

    /**
     * 用户ID，与用户表关联
     */
    @TableField("user_id")
    private Long userId;

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
