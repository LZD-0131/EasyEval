package com.dong.easyeval.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 作文批改次数表
 * </p>
 *
 * @author dong
 * @since 2026-03-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_correction_count")
public class TCorrectionCount implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "record_id", type = IdType.AUTO)
    private Long recordId;

    /**
     * 用户ID，与用户表关联
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 总批改次数
     */
    @TableField("total_count")
    private Integer totalCount;

    /**
     * 已使用批改次数
     */
    @TableField("used_count")
    private Integer usedCount;

    /**
     * 到期日期，批改次数的有效期
     */
    @TableField("expiration_date")
    private LocalDate expirationDate;

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
