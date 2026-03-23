package com.dong.easyeval.wx;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Disabled("Manual integration test against WeChat API")
public class WxMenuPublisherTest {

    @Autowired
    private WxMpService wxMpService;

    @Test
    public void publishMenuFromJson() throws WxErrorException {
        String menuJson = "{\n" +
                "  \"button\":[\n" +
                "    {\n" +
                "      \"type\":\"view\",\n" +
                "      \"name\":\"作文批改\",\n" +
                "      \"url\":\"https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx243a54bb564b2f31&redirect_uri=https%3A%2F%2F11ir9803988vm.vicp.fun%2Feaasyeval.html&response_type=code&scope=snsapi_base&state=state&connect_redirect=1#wechat_redirect\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\":\"自助充值\",\n" +
                "      \"sub_button\":[\n" +
                "        {\n" +
                "          \"type\":\"click\",\n" +
                "          \"name\":\"充值方法\",\n" +
                "          \"key\":\"REDEEM_VOUCHER\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"type\":\"click\",\n" +
                "          \"name\":\"唯一标识\",\n" +
                "          \"key\":\"MY_OPENID\"\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\":\"我的\",\n" +
                "      \"sub_button\":[\n" +
                "        {\n" +
                "          \"type\":\"view\",\n" +
                "          \"name\":\"我的卡券\",\n" +
                "          \"url\":\"https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx243a54bb564b2f31&redirect_uri=https%3A%2F%2F11ir9803988vm.vicp.fun%2Fcoupon.html&response_type=code&scope=snsapi_base&state=state&connect_redirect=1#wechat_redirect\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"type\":\"click\",\n" +
                "          \"name\":\"剩余额度\",\n" +
                "          \"key\":\"REMAINING_AMOUNT\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"type\":\"view\",\n" +
                "          \"name\":\"批改记录\",\n" +
                "          \"url\":\"https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx243a54bb564b2f31&redirect_uri=https%3A%2F%2F11ir9803988vm.vicp.fun%2Fevalrecode.html&response_type=code&scope=snsapi_base&state=state&connect_redirect=1#wechat_redirect\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        wxMpService.getMenuService().menuCreate(menuJson);
    }
}
