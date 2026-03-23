package com.dong.easyeval.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 有道云作文批改 API 响应实体类
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YoudaoResponse {
    
    /**
     * 请求 ID，用于标识每次 API 请求的唯一标识符
     */
    @JsonProperty("RequestId")
    private String requestId;
    
    /**
     * 错误代码，当 API 调用失败时返回的错误标识
     */
    private String errorCode;
    
    /**
     * 作文批改的结果信息
     */
    @JsonProperty("Result")
    private EvaluationResult result;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EvaluationResult {
        /**
         * 原始作文内容
         */
        private String rawEssay;
        
        /**
         * 整体作文建议
         */
        private String essayAdvice;
        
        /**
         * 作文标题
         */
        private String title;
        
        /**
         * 作文总得分
         */
        private Double totalScore;
        
        /**
         * 满分分值
         */
        private Double fullScore;
        
        /**
         * 整体评价
         */
        private String totalEvaluation;
        
        /**
         * 主要评分维度
         */
        private MajorScore majorScore;
        
        /**
         * 作文详细反馈
         */
        private EssayFeedback essayFeedback;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MajorScore {
        /**
         * 语法方面的建议
         */
        private String grammarAdvice;
        
        /**
         * 词汇得分
         */
        private Double wordScore;
        
        /**
         * 语法得分
         */
        private Double grammarScore;
        
        /**
         * 主题得分
         */
        private Double topicScore;
        
        /**
         * 词汇方面的建议
         */
        private String wordAdvice;
        
        /**
         * 结构得分
         */
        private Double structureScore;
        
        /**
         * 结构方面的建议
         */
        private String structureAdvice;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EssayFeedback {
        /**
         * 句子级别的反馈列表
         */
        private List<SentFeedback> sentsFeedback;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SentFeedback {
        /**
         * 原始句子
         */
        private String rawSent;
        
        /**
         * 修正后的句子
         */
        private String correctedSent;
        
        /**
         * 句子级别的反馈
         */
        private String sentFeedback;
        
        /**
         * 错误位置信息列表
         */
        private List<ErrorPosInfo> errorPosInfos;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ErrorPosInfo {
        /**
         * 错误原因
         */
        private String reason;
        
        /**
         * 原始错误片段
         */
        private String orgChunk;
        
        /**
         * 正确的修正片段
         */
        private String correctChunk;
        
        /**
         * 错误类型标题
         */
        private String errorTypeTitle;
        
        /**
         * 相关知识点解释
         */
        private String knowledgeExp;
        
        /**
         * 错误基础信息
         */
        private String errBaseInfo;
    }
}