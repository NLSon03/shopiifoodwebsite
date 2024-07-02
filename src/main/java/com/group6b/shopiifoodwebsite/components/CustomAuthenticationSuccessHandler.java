package com.group6b.shopiifoodwebsite.components;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        // Kiểm tra nếu người dùng đã xác thực có vai trò là "ROLE_RESTAURANT"
        String redirectURL = request.getContextPath();
        boolean isRestaurant = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("SELLER"));
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"));
        if (isRestaurant) {
            // Chuyển hướng đến trang dashboard của nhà hàng
            redirectURL="/restaurants/dashboard";

        } else if(isAdmin) {
            // Chuyển hướng đến URL mặc định cho các người dùng khác
            redirectURL="/admin";
        }
        else {
            redirectURL="/";
        }

        response.sendRedirect(redirectURL);
    }
}
