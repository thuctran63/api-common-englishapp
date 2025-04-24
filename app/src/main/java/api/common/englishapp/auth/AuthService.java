package api.common.englishapp.auth;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import api.common.englishapp.requests.CommonResponse;
import reactor.core.publisher.Mono;

@Service
public class AuthService {

    private final WebClient webClient;

    public AuthService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://localhost:8080") // Điều chỉnh URL cho phù hợp
                .build();
    }

    public Mono<UserData> validateToken(String token) {
        return webClient.post()
                .uri("/auth/checkToken")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<CommonResponse<UserData>>() {
                })
                .map(CommonResponse::getData); // Lấy ra phần data (UserData)
    }

}
