package com.cosmetics.cosmetics_store.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        System.out.println("--- DEBUG LOGIN ---");
        System.out.println("User: " + authentication.getName());
        System.out.println("Roles: " + roles);
        
        // Ép kiểu chuyển hướng tuyệt đối
        if (roles.contains("ROLE_ADMIN")) {
            System.out.println("Redirecting to: /admin");
            response.sendRedirect(request.getContextPath() + "/admin");
        } else {
            System.out.println("Redirecting to: /");
            response.sendRedirect(request.getContextPath() + "/");
        }
    }
}