package api.common.englishapp.auth;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class SecurityContextUtils {

    private SecurityContextUtils() {
        // Utility class
    }

    public static Mono<UserData> getCurrentUser(ServerWebExchange exchange) {
        UserData userData = exchange.getAttribute("USER_DATA");
        if (userData != null) {
            return Mono.just(userData);
        }
        return Mono.empty();
    }
}
