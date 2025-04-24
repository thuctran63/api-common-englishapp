package api.common.englishapp.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.PathContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
public class AuthenticationFilter implements WebFilter {

    private final RequestMappingHandlerMapping handlerMapping;
    private final AuthService authService;
    private final Map<PathPattern, RequiresAuth> patternToRequiresAuthMap = new HashMap<>();

    public AuthenticationFilter(RequestMappingHandlerMapping handlerMapping, AuthService authService) {
        this.handlerMapping = handlerMapping;
        this.authService = authService;
    }

    @PostConstruct
    private void initializePathPatterns() {
        // Đọc tất cả các mappings và lưu đường dẫn và annotation tương ứng
        handlerMapping.getHandlerMethods().forEach((info, method) -> {
            RequiresAuth requiresAuth = getRequiresAuthAnnotation(method);
            if (requiresAuth != null) {
                PathPatternParser parser = new PathPatternParser();
                PathPattern pattern = parser
                        .parse(info.getPatternsCondition().getPatterns().iterator().next().getPatternString());
                patternToRequiresAuthMap.put(pattern, requiresAuth);
            }
        });
    }

    private RequiresAuth getRequiresAuthAnnotation(HandlerMethod handlerMethod) {
        RequiresAuth annotation = handlerMethod.getMethodAnnotation(RequiresAuth.class);
        if (annotation == null) {
            annotation = handlerMethod.getBeanType().getAnnotation(RequiresAuth.class);
        }
        return annotation;
    }

    @SuppressWarnings("null")
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        PathContainer pathContainer = PathContainer.parsePath(path);

        System.out.println("Found matching pattern for path: " + path);

        // Find the matching RequiresAuth using a final variable
        final RequiresAuth matchingAuth = findMatchingRequiresAuth(pathContainer);

        // If no authentication is required or anonymous access is allowed, continue
        if (matchingAuth == null || matchingAuth.allowAnonymous()) {
            return chain.filter(exchange);
        }

        // Get token from header
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        token = token.substring(7);

        // Validate token using auth service
        return authService.validateToken(token)
                .flatMap(userData -> {
                    // Check roles if required
                    if (matchingAuth.roles().length > 0) {
                        boolean hasRequiredRole = false;
                        for (String requiredRole : matchingAuth.roles()) {
                            if (userData.getRole().contains(requiredRole)) {
                                hasRequiredRole = true;
                                break;
                            }
                        }

                        if (!hasRequiredRole) {
                            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                            return exchange.getResponse().setComplete();
                        }
                    }

                    exchange.getAttributes().put("USER_DATA", userData);
                    return chain.filter(exchange);
                })
                .onErrorResume(e -> {
                    exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                    return exchange.getResponse().setComplete();
                });
    }

    // Helper method to find matching RequiresAuth
    private RequiresAuth findMatchingRequiresAuth(PathContainer pathContainer) {
        for (Map.Entry<PathPattern, RequiresAuth> entry : patternToRequiresAuthMap.entrySet()) {
            if (entry.getKey().matches(pathContainer)) {
                return entry.getValue();
            }
        }
        return null;
    }
}
