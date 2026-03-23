package com.dong.easyeval.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dong.easyeval.common.ApiResponse;
import com.dong.easyeval.common.WxOAuthCodeException;
import com.dong.easyeval.dto.YoudaoResponse;
import com.dong.easyeval.entity.TCorrectionCount;
import com.dong.easyeval.entity.TCorrectionRequest;
import com.dong.easyeval.entity.TUser;
import com.dong.easyeval.request.EccSubmitRequest;
import com.dong.easyeval.request.EvalRecordRequest;
import com.dong.easyeval.service.ITCorrectionCountService;
import com.dong.easyeval.service.ITCorrectionRequestService;
import com.dong.easyeval.service.ITUserService;
import com.dong.easyeval.service.WxOAuthCodeService;
import com.dong.easyeval.service.YoudaoEssayEvalService;
import com.dong.easyeval.utils.RedisTool;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.JedisPool;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/ecc")
public class EccController {

    @Autowired
    private YoudaoEssayEvalService essayEvalService;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ITCorrectionCountService countService;
    @Autowired
    private ITCorrectionRequestService correctionRequestService;
    @Autowired
    private ITUserService userService;
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private WxOAuthCodeService wxOAuthCodeService;



    @PostMapping("/submit")
    @Transactional
    public ApiResponse<Object> submit(@RequestBody EccSubmitRequest eccSubmitRequest) {
        // 1. 参数非空校验 (增强版：使用 SpringUtils 工具类处理空白字符)
        if (eccSubmitRequest == null || 
            !StringUtils.hasText(eccSubmitRequest.getContent()) || 
            !StringUtils.hasText(eccSubmitRequest.getGrade())||
            !StringUtils.hasText(eccSubmitRequest.getTitle())||
            !StringUtils.hasText(eccSubmitRequest.getCode())) {
            return ApiResponse.error("作文内容、年级和标题不能为空，请检查请求参数");
        }
        // 分布式锁校验
        boolean flag = RedisTool.tryGetDistributedLock(jedisPool.getResource(), "user_code_" + eccSubmitRequest.getCode(), "1", 5);
        if (!flag) {
            return ApiResponse.error("操作频繁，请稍后再试");
        }



        String openId;
        try {
            openId = wxOAuthCodeService.resolveOpenId(eccSubmitRequest.getCode());
        } catch (WxOAuthCodeException exception) {
            return ApiResponse.error(exception.getMessage());
        } catch (WxErrorException exception) {
            log.error("微信授权校验失败, code={}", eccSubmitRequest.getCode(), exception);
            return ApiResponse.error("微信授权校验失败，请稍后再试");
        }

        // 通过openId查询用户并且判断用户是否有批改权益“
        TUser tUser = userService.getOne(new QueryWrapper<TUser>()
                .eq("wechat_user_id", openId));
        if (Objects.isNull(tUser)) {
            return ApiResponse.error("未查找到有效用户");
        }
        TCorrectionCount tCorrectionCount = countService.getOne(new QueryWrapper<TCorrectionCount>().eq("user_id", tUser.getUserId()));
        if (Objects.isNull(tCorrectionCount)) {
            return ApiResponse.error("未查找到用户的批改次数记录");
        }
        if (tCorrectionCount.getTotalCount() - tCorrectionCount.getUsedCount() <= 0) {
            return ApiResponse.error("您暂无批改次数，请先通过自助充值进行购买");
        }

        // 2. 调用有道云作文批改服务（核心业务层调用）
        String result = essayEvalService.evaluateEnglishEssay(
                eccSubmitRequest.getContent(),
                eccSubmitRequest.getGrade(),
                eccSubmitRequest.getTitle()
        );

        // 3. 解析有道云返回的 JSON 结果并返回给前端
        try {
            if (StringUtils.hasText(result) && result.contains("\"errorCode\":\"0\"")) {
                // 将 JSON 字符串反序列化为实体类对象
                YoudaoResponse response = objectMapper.readValue(result, YoudaoResponse.class);
                
                // 批改完成后，批改记录+1
                correctionRequestService.save(TCorrectionRequest.builder()
                        .userId(tUser.getUserId())
                        .essayContent(objectMapper.writeValueAsString(eccSubmitRequest))
                        .submissionTime(LocalDateTime.now())
                        .correctionStatus("1")
                        .correctionResult(result)
                        .build());
                        
                // 已使用次数+1
                boolean updated = countService.lambdaUpdate()
                        .eq(TCorrectionCount::getUserId, tUser.getUserId())
                        .setSql("used_count = used_count + 1")
                        .update();
                if (!updated) {
                    return ApiResponse.error("批改次数更新失败");
                }
                
                return ApiResponse.success("批改成功", response);
            } else if (StringUtils.hasText(result)) {
                // 业务失败，但也成功收到了有道的 JSON 响应
                YoudaoResponse response = objectMapper.readValue(result, YoudaoResponse.class);
                log.warn("调用有道云批改服务失败, 响应: {}", result);
                return ApiResponse.error("批改失败", response);
            } else {
                log.error("调用有道云批改服务失败，返回结果为空");
                return ApiResponse.error("批改失败，第三方接口无响应");
            }
        } catch (Exception e) {
            log.error("解析有道云返回结果异常, 原始结果: {}", result, e);
            return ApiResponse.error("结果解析异常，请联系管理员", null);
        }
    }
    @PostMapping("/eval_record")
    public ApiResponse getEvalRecord(@RequestBody EvalRecordRequest evalRecordRequest) {
        /*
        1. 向服务端发送code
        2. code->openId->userId
        3. userId->evalrecord
        4. return evalrecord
        */
        if (!StringUtils.hasText(evalRecordRequest.getCode())) {
            return ApiResponse.error("请与微信公众号中访问");
        }
        String openId;
        try {
            openId = wxOAuthCodeService.resolveOpenId(evalRecordRequest.getCode());
        } catch (WxOAuthCodeException exception) {
            return ApiResponse.error(exception.getMessage());
        } catch (WxErrorException exception) {
            log.error("查询批改记录时微信授权校验失败, code={}", evalRecordRequest.getCode(), exception);
            return ApiResponse.error("微信授权校验失败，请稍后再试");
        }

        // 通过openId查询用户并且判断用户是否有批改权益“
        TUser tUser = userService.getOne(new QueryWrapper<TUser>()
                .eq("wechat_user_id", openId));
        if (Objects.isNull(tUser)) {
            return ApiResponse.error("未查找到有效用户");
        }
        return ApiResponse.success("批改记录查询成功",
                correctionRequestService.list(new QueryWrapper<TCorrectionRequest>()
                        .eq("user_id", tUser.getUserId())
                        .orderByDesc("updated_at")));
    }
}
