package mg.haja.federationagricole.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "x-api-key";
    private final String expectedApiKey;

    public ApiKeyFilter() {
        this.expectedApiKey = Dotenv.load().get("API_KEY");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (!path.equals("/error") && !path.equals("/favicon.ico")) {
            String providedKey = request.getHeader(API_KEY_HEADER);
            
            if (providedKey == null || !providedKey.equals(expectedApiKey)) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"message\": \"Bad credentials\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
