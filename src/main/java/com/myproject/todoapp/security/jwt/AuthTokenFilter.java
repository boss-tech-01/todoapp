package com.myproject.todoapp.security.jwt;

import com.myproject.todoapp.security.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {

    //Simple Logging Facade 4 Java
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthTokenFilter.class);

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (request.getServletPath().startsWith("/auth")
                || request.getServletPath().startsWith("/swagger")
                || request.getServletPath().startsWith("/v3/api")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractJwtFromRequest(request); //Bearer adfasdfa.asdfasf.asdfasdf

        try {
            if (token != null && jwtUtils.validateAccessToken(token)){
                String username = jwtUtils.extractUsernameFromToken(token);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                request.setAttribute("username", username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (UsernameNotFoundException e) {
            LOGGER.warn("User not found while authenticating: {}", e.getMessage());
        } catch (Exception e) {
            LOGGER.warn("JWT Filter Error: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request){
        return request.getHeader(HttpHeaders.AUTHORIZATION);
    }
}
