package api.common.englishapp.requests;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {

    public static ResponseEntity<CommonResponse<?>> ok(Object data) {
        return ResponseEntity.ok(CommonResponse.http200(data));
    }

    public static ResponseEntity<CommonResponse<?>> noContent() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(CommonResponse.http204());
    }

    public static ResponseEntity<CommonResponse<?>> badRequest(String message) {
        return ResponseEntity.badRequest().body(CommonResponse.http400(message));
    }

    public static ResponseEntity<CommonResponse<?>> unAuthorized(String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(CommonResponse.http401(message));
    }

    public static ResponseEntity<CommonResponse<?>> unAuthenticated(String message) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CommonResponse.http403(message));
    }

    public static ResponseEntity<CommonResponse<?>> serverError(String message) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CommonResponse.http500(message));
    }
}