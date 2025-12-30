package com.ydsw.utils;

public class ResultTemplate<T> {

    public static <T> ResultTemplate<T> success() {
        return success(null);
    }

    public static <T> ResultTemplate<T> success(T data) {
        return new ResultTemplate<>(true, "操作成功", data);
    }

    public static <T> ResultTemplate<T> fail(String message) {
        return new ResultTemplate<>(false, message, null);
    }

    // 构造函数和字段
    private boolean success;
    private String message;
    private T data;

    public ResultTemplate(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // getters and setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}