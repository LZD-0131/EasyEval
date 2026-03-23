package com.dong.easyeval.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 付款历史记录表
 * </p>
 *
 * @author dong
 * @since 2026-03-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_payment_history")
public class TPaymentHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 付款ID
     */
    @TableId(value = "payment_id", type = IdType.AUTO)
    private Long paymentId;

    /**
     * 用户ID，与用户表关联
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 付款日期
     */
    @TableField("payment_date")
    private LocalDateTime paymentDate;

    /**
     * 付款金额
     */
    @TableField("payment_amount")
    private BigDecimal paymentAmount;

    /**
     * 付款状态，如成功、失败等
     */
    @TableField("payment_status")
    private String paymentStatus;

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
