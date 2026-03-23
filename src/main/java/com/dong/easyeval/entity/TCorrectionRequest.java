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
 * 作文批改请求表
 * </p>
 *
 * @author dong
 * @since 2026-03-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_correction_request")
public class TCorrectionRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 请求ID
     */
    @TableId(value = "request_id", type = IdType.AUTO)
    private Long requestId;

    /**
     * 用户ID，与用户表关联
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 作文内容
     */
    @TableField("essay_content")
    private String essayContent;

    /**
     * 提交时间
     */
    @TableField("submission_time")
    private LocalDateTime submissionTime;

    /**
     * 批改状态，如未批改、已批改
     */
    @TableField("correction_status")
    private String correctionStatus;

    /**
     * 批改结果，批改后的作文内容
     */
    @TableField("correction_result")
    private String correctionResult;

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
