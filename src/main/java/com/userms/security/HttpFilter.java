package com.userms.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.userms.DTO.ResponseBO;
import com.userms.DTO.StatusConstant;
import com.userms.entity.CustomUserDetails;
import com.userms.security.jwt.JwtUtil;
import com.userms.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class HttpFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(HttpFilter.class);

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final PermissionService permissionService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public HttpFilter(JwtUtil jwtUtil, UserService userService, PermissionService permissionService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.permissionService = permissionService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        if (requestUri.startsWith("/userms/v1/api/user/register") || requestUri.startsWith("/userms/v1/api/subscription/getAllActive") ||
                requestUri.startsWith("/userms/v1/api/user/login") || requestUri.startsWith("/userms/v1/api/user/profile/") ||
                requestUri.startsWith("/userms/v1/api/user/logout/")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwtToken);
            } catch (Exception e) {
                logger.error("Error extracting username from token: {}", e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            CustomUserDetails userDetails = userService.loadUserByUsername(username);
            if (jwtUtil.validateToken(jwtToken, userDetails.getUsername())) {
                if (permissionService.hasPermission(userDetails, request)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    logger.info("User {} authenticated and authorized for URI: {}", username, request.getRequestURI());
                } else {
                    logger.warn("Access denied for user: {} to URI: {}", username, request.getRequestURI());

                    ResponseBO errorResponse = new ResponseBO(
                            org.apache.http.HttpStatus.SC_FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase(), null,  StatusConstant.FORBIDDEN);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
                    return;
                }
            } else {
                logger.warn("Invalid token for user: {}", username);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}

