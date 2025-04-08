package api.common.englishapp.requests;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T> {
    private int status;
    private String message;
    private T data;

    public CommonResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> CommonResponse<T> http200(T data) {
        return new CommonResponse<>(200, "Success", data);
    }

    public static <T> CommonResponse<T> http204() {
        return new CommonResponse<>(204, "No Content", null);
    }

    public static <T> CommonResponse<T> http400(T data) {
        return new CommonResponse<T>(400, "Bad request", data);
    }

    public static <T> CommonResponse<T> http403(String message) {
        return new CommonResponse<>(403, message, null);
    }

    public static <T> CommonResponse<T> http500(String message) {
        return new CommonResponse<>(500, message, null);
    }

    // Getters và Setters
    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
