package com.dong.easyeval.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dong.easyeval.common.ApiResponse;
import com.dong.easyeval.entity.TCorrectionCount;
import com.dong.easyeval.entity.TUser;
import com.dong.easyeval.service.ITCorrectionCountService;
import com.dong.easyeval.service.ITUserService;
import com.dong.easyeval.utils.SnowflakeIdGenerator;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpMenuService;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;
import me.chanjar.weixin.mp.bean.material.WxMpMaterial;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialUploadResult;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialFileBatchGetResult;
import me.chanjar.weixin.mp.bean.material.WxMediaImgUploadResult;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.session.WxSessionManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
public class WxResponse {

    @Autowired
    private WxMpService wxMpService;

    @Autowired
    private ITUserService userService;

    @Autowired
    private ITCorrectionCountService countService;

    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

    private WxMpMessageRouter router;

    @PostConstruct
    public void postConstruct() {
        WxMpMessageHandler getOpenIdHanlder = new WxMpMessageHandler() {
            @Override
            public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
                // 用户的唯一标识就是 openid
                return WxMpXmlOutMessage.TEXT().content("你的唯一标识(OpenID)：\n" + wxMessage.getFromUser())
                        .toUser(wxMessage.getFromUser())
                        .fromUser(wxMessage.getToUser())
                        .build();
            }
        };

        WxMpMessageHandler payMethodHandler = new WxMpMessageHandler() {
            @Override
            public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
                // 这里填入你通过 uploadMedia 接口上传后获得的 mediaId
                String mediaId = "41zwZDS6Ji_d9vkUCs6Iay_haRXKKv79muerza2GBdQesuJNcPxqhXhvfFSxZUQC"; 
                return WxMpXmlOutMessage.IMAGE()
                        .mediaId(mediaId)
                        .toUser(wxMessage.getFromUser())
                        .fromUser(wxMessage.getToUser())
                        .build();
            }
        };

        WxMpMessageHandler amountHandler = new WxMpMessageHandler() {
            @Override
            public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
                TUser tUser = userService.getOne(new QueryWrapper<TUser>()
                        .eq("wechat_user_id", wxMessage.getFromUser()));
                if (Objects.isNull(tUser)) {
                    return WxMpXmlOutMessage.TEXT().content("未找到有效用户，请先关注公众号")
                            .toUser(wxMessage.getFromUser())
                            .fromUser(wxMessage.getToUser())
                            .build();
                }
                TCorrectionCount tCorrectionCount = countService.getOne(new QueryWrapper<TCorrectionCount>().eq("user_id", tUser.getUserId()));
                if (Objects.isNull(tCorrectionCount)) {
                    return WxMpXmlOutMessage.TEXT().content("暂无额度信息")
                            .toUser(wxMessage.getFromUser())
                            .fromUser(wxMessage.getToUser())
                            .build();
                }
                int count = tCorrectionCount.getTotalCount() - tCorrectionCount.getUsedCount();
                return WxMpXmlOutMessage.TEXT().content("您当前可用额度：" + count + "次")
                        .toUser(wxMessage.getFromUser())
                        .fromUser(wxMessage.getToUser())
                        .build();
            }
        };

        WxMpMessageHandler subscribeHandler = new WxMpMessageHandler() {
            @Override
            public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) {
                String openId = wxMessage.getFromUser();
                TUser user = userService.getOne(new QueryWrapper<TUser>().eq("wechat_user_id", openId));
                if (user != null) {
                    return WxMpXmlOutMessage.TEXT().fromUser(wxMessage.getToUser()).toUser(wxMessage.getFromUser()).content("感谢回来，老朋友！").build();
                }
                
                long userId = snowflakeIdGenerator.generateId();
                userService.save(TUser.builder().wechatUserId(openId).userId(userId).build());
                countService.save(TCorrectionCount.builder()
                        .expirationDate(LocalDate.now().plusMonths(6))
                        .userId(userId)
                        .totalCount(2)
                        .usedCount(0)
                        .build());
                return WxMpXmlOutMessage.TEXT().fromUser(wxMessage.getToUser()).toUser(wxMessage.getFromUser())
                        .content("欢迎您关注小E作文批改网！我们将为您提供两次作文批改的免费权益，您可以立即享用。此外，如果您有更多的作文批改需求，您可以通过自助充值获得更多权益。")
                        .build();
            }
        };

        router = new WxMpMessageRouter(wxMpService);
        router
                .rule()
                .async(false)
                .event(WxConsts.EventType.CLICK)
                .eventKey("MY_OPENID")
                .handler(getOpenIdHanlder)
                .end()
                .rule()
                .async(false)
                .event(WxConsts.EventType.CLICK)
                .eventKey("REDEEM_VOUCHER")
                .handler(payMethodHandler)
                .end()
                .rule()
                .async(false)
                .event(WxConsts.EventType.CLICK)
                .eventKey("REMAINING_AMOUNT")
                .handler(amountHandler)
                .end()
                .rule()
                .async(false)
                .event(WxConsts.EventType.SUBSCRIBE)
                .handler(subscribeHandler)
                .end();
    }

    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
    public void wxMessage(
            @RequestParam(name = "signature", required = false) String signature,
            @RequestParam(name = "timestamp", required = false) String timestamp,
            @RequestParam(name = "nonce", required = false) String nonce,
            @RequestParam(name = "echostr", required = false) String echostr,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        if (signature == null || timestamp == null || nonce == null) {
            response.getWriter().write("非法请求");
            return;
        }

        if (!wxMpService.checkSignature(timestamp, nonce, signature)) {
            response.getWriter().write("非法请求");
            return;
        }

        if (StringUtils.isNotBlank(echostr)) {
            response.getWriter().write(echostr);
            return;
        }

        WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(request.getInputStream());
        log.info("收到微信消息: Type={}, Event={}, EventKey={}", inMessage.getMsgType(), inMessage.getEvent(), inMessage.getEventKey());
        
        WxMpXmlOutMessage outMessage = router.route(inMessage);
        if (outMessage != null) {
            response.setCharacterEncoding("UTF-8");
            String xml = outMessage.toXml();
            log.info("回复微信消息: {}", xml);
            response.getWriter().write(xml);
        } else {
            response.getWriter().write("");
        }
    }

    @PostMapping("/uploadMedia")
    public ApiResponse upload() throws WxErrorException {
        // 请确保文件路径正确
        File file = new File("E:\\SpingbootStudy\\easyeval\\src\\main\\resources\\static\\shoukuanma.jpg");
        if (!file.exists()) {
            return ApiResponse.error("素材文件不存在，请检查路径");
        }

        // 使用 materialFileUpload 上传永久素材 (针对 4.5.0 版本)
        WxMpMaterial material = new WxMpMaterial();
        material.setFile(file);
        material.setName("shoukuanma");
        WxMpMaterialUploadResult resSingle = wxMpService.getMaterialService().materialFileUpload(WxConsts.MaterialType.IMAGE, material);
        return ApiResponse.success("上传永久素材成功", resSingle);
    }

    @PostMapping("/getMediaList")
    public ApiResponse getMediaList() throws WxErrorException {
        WxMpMaterialFileBatchGetResult resSingle = wxMpService.getMaterialService().materialFileBatchGet(WxConsts.MaterialType.IMAGE, 0, 20);
        return ApiResponse.success(resSingle);
    }

    @PostMapping("/createmenu")
    public ApiResponse createMenu(@RequestBody String menuStr) throws WxErrorException {
        try {
            wxMpService.getMenuService().menuCreate(menuStr);
            return ApiResponse.success("创建成功");
        } catch (WxErrorException e) {
            log.error("创建菜单失败", e);
            return ApiResponse.error("创建失败：" + e.getError().getErrorMsg());
        }
    }
}
