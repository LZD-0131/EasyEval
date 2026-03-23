package com.dong.easyeval.common;

import lombok.Data;

/**
 * 通用响应包装类
 */
@Data
public class ApiResponse<T> {
    /**
     * 响应状态码
     * 200: 成功
     * 500: 失败
     */
    private int status;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据（泛型）
     */
    private T data;
    
    /**
     * 响应时间戳
     */
    private long timestamp;

    /**
     * 设置响应信息
     * @param status 状态码
     * @param message 消息
     * @param data 数据
     * @return 当前对象
     */
    public ApiResponse apiResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
        return this;
    }

    /**
     * 返回错误响应
     * @param message 错误消息
     * @param <T> 泛型类型
     * @return 错误响应对象
     */
    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setStatus(500);
        apiResponse.setMessage(message);
        apiResponse.setTimestamp(System.currentTimeMillis());
        apiResponse.setData(null);
        return apiResponse;
    }

    /**
     * 返回带数据的错误响应
     * @param message 错误消息
     * @param data 错误数据
     * @param <T> 泛型类型
     * @return 错误响应对象
     */
    public static <T> ApiResponse<T> error(String message, T data) {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setStatus(500);
        apiResponse.setMessage(message);
        apiResponse.setTimestamp(System.currentTimeMillis());
        apiResponse.setData(data);
        return apiResponse;
    }

    /**
     * 返回成功响应
     * @param message 成功消息
     * @param data 响应数据
     * @param <T> 泛型类型
     * @return 成功响应对象
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setStatus(200);
        apiResponse.setMessage(message);
        apiResponse.setTimestamp(System.currentTimeMillis());
        apiResponse.setData(data);
        return apiResponse;
    }

    /**
     * 返回带数据的成功响应（默认消息为"success"）
     * @param data 响应数据
     * @param <T> 泛型类型
     * @return 成功响应对象
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setStatus(200);
        apiResponse.setMessage("success");
        apiResponse.setTimestamp(System.currentTimeMillis());
        apiResponse.setData(data);
        return apiResponse;
    }
}