package com.dong.easyeval.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dong.easyeval.common.ApiResponse;
import com.dong.easyeval.common.WxOAuthCodeException;
import com.dong.easyeval.entity.TCorrectionCount;
import com.dong.easyeval.entity.TCoupon;
import com.dong.easyeval.entity.TCouponType;
import com.dong.easyeval.entity.TUser;
import com.dong.easyeval.request.ExchangeRequest;
import com.dong.easyeval.response.CouponResp;
import com.dong.easyeval.service.ITCorrectionCountService;
import com.dong.easyeval.service.ITCouponService;
import com.dong.easyeval.service.ITCouponTypeService;
import com.dong.easyeval.service.ITUserService;
import com.dong.easyeval.service.WxOAuthCodeService;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class CouponController {

    @Autowired
    private ITCouponService couponService;
    @Autowired
    private ITCouponTypeService couponTypeService;
    @Autowired
    private ITUserService userService;
    @Autowired
    private ITCorrectionCountService countService;
    @Autowired
    private WxOAuthCodeService wxOAuthCodeService;

    @GetMapping("/mycoupon")
    public ApiResponse myCoupon(@RequestParam String code) {
        if (!StringUtils.hasText(code)) {
            return ApiResponse.error("璇蜂笌寰俊鍏紬鍙锋煡鐪嬫壒鏀硅褰?");
        }
        String openId;
        try {
            openId = wxOAuthCodeService.resolveOpenId(code);
        } catch (WxOAuthCodeException exception) {
            return ApiResponse.error(exception.getMessage());
        } catch (WxErrorException exception) {
            return ApiResponse.error("微信授权校验失败，请稍后再试");
        }
        TUser tUser = userService.getOne(new QueryWrapper<TUser>()
                .eq("wechat_user_id", openId));
        if (Objects.isNull(tUser)) {
            return ApiResponse.error("鐢ㄦ埛涓嶅瓨鍦?");
        }
        List<TCoupon> tCoupons = couponService.list(new QueryWrapper<>(TCoupon.class).eq("user_id", tUser.getUserId()));

        List<CouponResp> couponResps = new ArrayList<>();
        tCoupons.forEach(tCoupon -> {
            TCouponType tCouponType = couponTypeService.getOne(new QueryWrapper<>(TCouponType.class).eq("type_id", tCoupon.getCouponType()));
            String typeName = tCouponType != null ? tCouponType.getTypeName() : null;
            String typeId = tCouponType != null ? String.valueOf(tCouponType.getTypeId()) : null;
            String typeDescription = tCouponType != null ? tCouponType.getTypeDescription() : null;
            CouponResp couponResp = CouponResp.builder().couponId(String.valueOf(tCoupon.getCouponId()))
                    .couponCode(tCoupon.getCouponCode())
                    .couponStatus(tCoupon.getCouponStatus())
                    .couponValue(tCoupon.getCouponValue())
                    .expirationDate(tCoupon.getExpirationDate())
                    .userId(String.valueOf(tCoupon.getUserId()))
                    .typeName(typeName)
                    .typeId(typeId)
                    .typeDescription(typeDescription)
                    .build();
            couponResps.add(couponResp);
        });
        return ApiResponse.success(couponResps);
    }

    @PostMapping("exchange")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse exchange(@RequestBody ExchangeRequest request) {
        if (Objects.isNull(request) || !StringUtils.hasText(request.getUserId()) || !StringUtils.hasText(request.getCouponCode())) {
            return ApiResponse.error("淇℃伅涓嶅畬鏁达紝鏃犳硶鍏戞崲");
        }

        Long userId;
        try {
            userId = Long.valueOf(request.getUserId());
        } catch (NumberFormatException exception) {
            return ApiResponse.error("鐢ㄦ埛ID鏍煎紡閿欒");
        }

        TCoupon tCoupon = couponService.getOne(new QueryWrapper<TCoupon>()
                .eq("user_id", userId)
                .eq("coupon_code", request.getCouponCode())
                .eq("coupon_status", "UNUSED"));
        if (Objects.isNull(tCoupon)) {
            return ApiResponse.error("鏃犳硶鏌ヨ鍒版湁鏁堝崱鍒?");
        }

        tCoupon.setCouponStatus("USED");
        if (!couponService.updateById(tCoupon)) {
            return rollbackWithError("鍗″埜鐘舵€佹洿鏂板け璐ワ紝璇峰皬绋嬬墖鍚庨噸璇?");
        }

        TCorrectionCount tCorrectionCount = countService.getOne(new QueryWrapper<TCorrectionCount>()
                .eq("user_id", userId));
        boolean saved;
        if (Objects.isNull(tCorrectionCount)) {
            tCorrectionCount = TCorrectionCount.builder()
                    .userId(userId)
                    .totalCount(20)
                    .usedCount(0)
                    .expirationDate(LocalDate.now().plusMonths(6))
                    .build();
            saved = countService.save(tCorrectionCount);
        } else {
            int currentTotalCount = Objects.requireNonNullElse(tCorrectionCount.getTotalCount(), 0);
            int currentUsedCount = Objects.requireNonNullElse(tCorrectionCount.getUsedCount(), 0);
            tCorrectionCount.setTotalCount(currentTotalCount + 20);
            tCorrectionCount.setUsedCount(currentUsedCount);
            if (Objects.isNull(tCorrectionCount.getExpirationDate())
                    || tCorrectionCount.getExpirationDate().isBefore(LocalDate.now())) {
                tCorrectionCount.setExpirationDate(LocalDate.now().plusMonths(6));
            }
            saved = countService.updateById(tCorrectionCount);
        }
        if (!saved) {
            return rollbackWithError("鏉冪泭鍙戦€佸け璐ワ紝璇峰皬绋嬬墖鍚庨噸璇?");
        }

        return ApiResponse.success("鍏戞崲鎴愬姛");
    }

    private ApiResponse rollbackWithError(String message) {
        try {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (NoTransactionException ignored) {
        }
        return ApiResponse.error(message);
    }
}
