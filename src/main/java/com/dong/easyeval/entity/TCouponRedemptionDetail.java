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
 * 
 * </p>
 *
 * @author dong
 * @since 2026-03-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_coupon_redemption_detail")
public class TCouponRedemptionDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 领取明细ID
     */
    @TableId(value = "redemption_id", type = IdType.AUTO)
    private Integer redemptionId;

    /**
     * 卡券ID，与卡券表关联
     */
    @TableField("coupon_id")
    private Integer couponId;

    /**
     * 用户ID，与用户表关联
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 领取时间，记录用户领取卡券的日期和时间
     */
    @TableField("redemption_date")
    private LocalDateTime redemptionDate;

    /**
     * 使用时间，记录用户使用卡券的日期和时间
     */
    @TableField("usage_date")
    private LocalDateTime usageDate;

    /**
     * 使用订单ID，如果用户使用卡券进行购买
     */
    @TableField("redemption_order_id")
    private Integer redemptionOrderId;

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
