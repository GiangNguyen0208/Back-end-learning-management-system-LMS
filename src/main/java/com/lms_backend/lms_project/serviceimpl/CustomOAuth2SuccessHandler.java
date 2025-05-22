package com.lms_backend.lms_project.serviceimpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms_backend.lms_project.Utility.Constant;
import com.lms_backend.lms_project.Utility.JwtUtils;
import com.lms_backend.lms_project.dao.UserDAO;
import com.lms_backend.lms_project.dto.UserDTO;
import com.lms_backend.lms_project.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger LOG = LoggerFactory.getLogger(CustomOAuth2SuccessHandler.class);

    private static final String DEFAULT_TARGET_URL = "http://localhost:5173/";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String redirectURL = DEFAULT_TARGET_URL;
        try {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            String provider = oauthToken.getAuthorizedClientRegistrationId(); // "google" hoặc "facebook"
            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes = oauthUser.getAttributes();

            String email = null;
            String firstName = null;
            String lastName = null;
            String avatarUrl = null;
            String oauth2Id = null;
            String username = null;
            boolean isVerified = false;

            if ("google".equalsIgnoreCase(provider)) {
                email = (String) attributes.get("email");
                firstName = (String) attributes.get("given_name");
                lastName = (String) attributes.get("family_name");
                avatarUrl = (String) attributes.get("picture");
                oauth2Id = (String) attributes.get("sub");
                username = email.substring(0, email.indexOf("@"));

                Object verifiedObj = attributes.get("email_verified");
                isVerified = verifiedObj instanceof Boolean && (Boolean) verifiedObj;

            } else if ("facebook".equalsIgnoreCase(provider)) {
                oauth2Id = (String) attributes.get("id");
                firstName = (String) attributes.get("first_name");
                lastName = (String) attributes.get("last_name");
                email = (String) attributes.get("email"); // Có thể null
                username = email.substring(0, email.indexOf("@"));

                try {
                    Map<String, Object> picture = (Map<String, Object>) attributes.get("picture");
                    if (picture != null) {
                        Map<String, Object> data = (Map<String, Object>) picture.get("data");
                        if (data != null) {
                            avatarUrl = (String) data.get("url");
                        }
                    }
                } catch (Exception e) {
                    LOG.warn("Cannot extract Facebook avatar", e);
                }

                // Facebook: email optional, coi như verified nếu tồn tại
                isVerified = (email != null);
            } else {
                LOG.warn("Unknown OAuth2 provider: {}", provider);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unsupported OAuth2 provider");
                return;
            }

            if (oauth2Id == null) {
                LOG.error("OAuth2 ID is missing from provider: {}", provider);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "OAuth2 ID missing.");
                return;
            }

            User user = null;
            if (email != null) {
                user = userDAO.findByEmailId(email);
            }

            // Nếu không có email hoặc không tìm thấy theo email => tìm theo oauth2Id
            if (user == null) {
                user = userDAO.findByOauth2Id(oauth2Id);
            }

            if (user == null) {
                user = new User();
                user.setEmailId(email); // Có thể null
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setAvatar(avatarUrl);
                user.setRole(Constant.UserRole.ROLE_STUDENT.value());
                user.setStatus(Constant.ActiveStatus.ACTIVE.value());
                user.setOauth2_provider(provider);
                user.setOauth2_id(oauth2Id);
                user.setEmail_verified(isVerified);
                user.setCreatedAt(LocalDateTime.now());
                user.setUpdateAt(LocalDateTime.now());
                user.setUsername(username);

                user = userDAO.save(user);
                LOG.info("Created new user via OAuth2: email={}, provider={}", email, provider);
            } else {
                boolean updated = false;

                if (!provider.equalsIgnoreCase(user.getOauth2_provider())) {
                    user.setOauth2_provider(provider);
                    updated = true;
                }
                if (!oauth2Id.equals(user.getOauth2_id())) {
                    user.setOauth2_id(oauth2Id);
                    updated = true;
                }
                if (isVerified != user.isEmail_verified()) {
                    user.setEmail_verified(isVerified);
                    updated = true;
                }
                if (avatarUrl != null && !avatarUrl.equals(user.getAvatar())) {
                    user.setAvatar(avatarUrl);
                    updated = true;
                }
                if (firstName != null && !firstName.equals(user.getFirstName())) {
                    user.setFirstName(firstName);
                    updated = true;
                }
                if (lastName != null && !lastName.equals(user.getLastName())) {
                    user.setLastName(lastName);
                    updated = true;
                }

                if (updated) {
                    user = userDAO.save(user);
                    LOG.info("Updated existing user info via OAuth2: id={}, provider={}", oauth2Id, provider);
                }
            }

            String jwt = jwtUtils.generateToken(user.getUsername());

            UserDTO userDTO = UserDTO.toUserDtoEntity(user);
            String userJson = objectMapper.writeValueAsString(userDTO);
            String userBase64 = Base64.getEncoder().encodeToString(userJson.getBytes(StandardCharsets.UTF_8));

            redirectURL = UriComponentsBuilder.fromUriString("http://localhost:5173/oauth2/redirect")
                    .queryParam("token", jwt)
                    .queryParam("user", userBase64)
                    .queryParam("ts", System.currentTimeMillis())
                    .build()
                    .toUriString();


            LOG.info("OAuth2 login successful: {}", authentication.getName());
        } catch (Exception e) {
            LOG.error("Error during OAuth2 login success handling", e);
        }
        response.sendRedirect(redirectURL);
    }

}
