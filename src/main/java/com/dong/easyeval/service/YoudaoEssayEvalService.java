package com.dong.easyeval.service;

import com.dong.easyeval.utils.AuthV3Util;
import com.dong.easyeval.utils.HttpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Service
public class YoudaoEssayEvalService {

    @Value("${youdao.api.app-key}")
    private String appKey;

    @Value("${youdao.api.app-secret}")
    private String appSecret;

    @Value("${youdao.api.url}")
    private String apiUrl;

    /**
     * 英文作文批改接口
     *
     * @param text  需要批改的英文文本
     * @param grade 年级，例如 "high" (高中), "middle" (初中), "primary" (小学)等
     * @param title 作文标题 (可选)
     * @return 批改结果的 JSON 字符串
     */
    public String evaluateEnglishEssay(String text, String grade, String title) {
        try {
            Map<String, String[]> params = createRequestParams(text, grade, title);
            AuthV3Util.addAuthParams(appKey, appSecret, params);
            
            byte[] result = HttpUtil.doPost(apiUrl, null, params, "application/json");
            
            if (result != null) {
                return new String(result, StandardCharsets.UTF_8);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "{\"error\": \"Authentication algorithm error\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Request failed\"}";
        }
        return "{\"error\": \"Unknown error\"}";
    }

    private Map<String, String[]> createRequestParams(String text, String grade, String title) {
        String modelContent = "";
        String isNeedSynonyms = "false";
        String correctVersion = "basic";
        String isNeedEssayReport = "false";

        Map<String, String[]> params = new HashMap<>();
        params.put("q", new String[]{text});
        params.put("grade", new String[]{grade != null ? grade : "high"});
        if (title != null && !title.isEmpty()) {
            params.put("title", new String[]{title});
        }
        params.put("modelContent", new String[]{modelContent});
        params.put("isNeedSynonyms", new String[]{isNeedSynonyms});
        params.put("correctVersion", new String[]{correctVersion});
        params.put("isNeedEssayReport", new String[]{isNeedEssayReport});
        return params;
    }
}