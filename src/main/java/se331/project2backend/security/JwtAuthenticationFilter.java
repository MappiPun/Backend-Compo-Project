package se331.project2backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import se331.project2backend.entity.AppUser;
import se331.project2backend.repository.UserRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Reads the "Authorization: Bearer ..." header and authenticates the request.
 * The user is loaded from the database on every request, so a user who has just been deactivated
 * (or deleted) cannot use the API any more even if the token has not expired yet.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = header.substring(7);
            Optional<String> username = jwtService.extractUsername(token);
            if (username.isPresent()) {
                Optional<AppUser> user = userRepository.findByUsername(username.get());
                if (user.isPresent() && user.get().isActive()) {
                    var auth = new UsernamePasswordAuthenticationToken(
                            user.get().getUsername(),
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + user.get().getRole().name())));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
