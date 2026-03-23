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
 * 用户表
 * </p>
 *
 * @author dong
 * @since 2026-03-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_user")
public class TUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /**
     * 微信用户唯一标识
     */
    @TableField("wechat_user_id")
    private String wechatUserId;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 电子邮件
     */
    @TableField("email")
    private String email;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * 付款信息，如信用卡信息
     */
    @TableField("payment_info")
    private String paymentInfo;

    /**
     * 微信昵称
     */
    @TableField("wechat_nickname")
    private String wechatNickname;

    /**
     * 微信头像图片URL
     */
    @TableField("wechat_avatar")
    private String wechatAvatar;

    /**
     * 微信性别信息（男、女、未知）
     */
    @TableField("wechat_gender")
    private String wechatGender;

    /**
     * 微信城市信息
     */
    @TableField("wechat_city")
    private String wechatCity;

    /**
     * 微信省份信息
     */
    @TableField("wechat_province")
    private String wechatProvince;

    /**
     * 微信国家信息
     */
    @TableField("wechat_country")
    private String wechatCountry;

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
