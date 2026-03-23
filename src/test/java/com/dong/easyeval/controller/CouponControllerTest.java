package com.dong.easyeval.controller;

import com.dong.easyeval.common.ApiResponse;
import com.dong.easyeval.entity.TCorrectionCount;
import com.dong.easyeval.entity.TCoupon;
import com.dong.easyeval.request.ExchangeRequest;
import com.dong.easyeval.service.ITCorrectionCountService;
import com.dong.easyeval.service.ITCouponService;
import com.dong.easyeval.service.ITCouponTypeService;
import com.dong.easyeval.service.ITUserService;
import com.dong.easyeval.service.WxOAuthCodeService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CouponControllerTest {

    @Test
    void exchangeShouldInitializeCorrectionCountWhenUserHasNoQuotaRecord() {
        CouponController controller = new CouponController();
        ITCouponService couponService = mock(ITCouponService.class);
        ITCorrectionCountService correctionCountService = mock(ITCorrectionCountService.class);
        ReflectionTestUtils.setField(controller, "couponService", couponService);
        ReflectionTestUtils.setField(controller, "countService", correctionCountService);
        ReflectionTestUtils.setField(controller, "couponTypeService", mock(ITCouponTypeService.class));
        ReflectionTestUtils.setField(controller, "userService", mock(ITUserService.class));
        ReflectionTestUtils.setField(controller, "wxOAuthCodeService", mock(WxOAuthCodeService.class));

        when(couponService.getOne(any())).thenReturn(TCoupon.builder()
                .couponId(1L)
                .userId(1L)
                .couponCode("PAY-COUPON")
                .couponStatus("UNUSED")
                .build());
        when(couponService.updateById(any(TCoupon.class))).thenReturn(true);
        when(correctionCountService.getOne(any())).thenReturn(null);
        when(correctionCountService.save(any(TCorrectionCount.class))).thenReturn(true);

        ExchangeRequest request = new ExchangeRequest();
        request.setUserId("1");
        request.setCouponCode("PAY-COUPON");

        ApiResponse response = controller.exchange(request);

        assertEquals(200, response.getStatus());
        verify(couponService).updateById(any(TCoupon.class));
        verify(correctionCountService).save(any(TCorrectionCount.class));
    }

    @Test
    void exchangeShouldRefreshExpiredQuotaRecord() {
        CouponController controller = new CouponController();
        ITCouponService couponService = mock(ITCouponService.class);
        ITCorrectionCountService correctionCountService = mock(ITCorrectionCountService.class);
        ReflectionTestUtils.setField(controller, "couponService", couponService);
        ReflectionTestUtils.setField(controller, "countService", correctionCountService);
        ReflectionTestUtils.setField(controller, "couponTypeService", mock(ITCouponTypeService.class));
        ReflectionTestUtils.setField(controller, "userService", mock(ITUserService.class));
        ReflectionTestUtils.setField(controller, "wxOAuthCodeService", mock(WxOAuthCodeService.class));

        TCorrectionCount existingCount = TCorrectionCount.builder()
                .recordId(1L)
                .userId(1L)
                .totalCount(2)
                .usedCount(1)
                .expirationDate(LocalDate.now().minusDays(1))
                .build();
        when(couponService.getOne(any())).thenReturn(TCoupon.builder()
                .couponId(1L)
                .userId(1L)
                .couponCode("PAY-COUPON")
                .couponStatus("UNUSED")
                .build());
        when(couponService.updateById(any(TCoupon.class))).thenReturn(true);
        when(correctionCountService.getOne(any())).thenReturn(existingCount);
        when(correctionCountService.updateById(any(TCorrectionCount.class))).thenAnswer(invocation -> {
            TCorrectionCount updated = invocation.getArgument(0);
            assertEquals(22, updated.getTotalCount());
            assertEquals(1, updated.getUsedCount());
            assertNotNull(updated.getExpirationDate());
            return true;
        });

        ExchangeRequest request = new ExchangeRequest();
        request.setUserId("1");
        request.setCouponCode("PAY-COUPON");

        ApiResponse response = controller.exchange(request);

        assertEquals(200, response.getStatus());
        verify(correctionCountService).updateById(any(TCorrectionCount.class));
    }
}
