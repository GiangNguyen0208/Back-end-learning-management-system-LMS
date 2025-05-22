package com.lms_backend.lms_project.serviceimpl;

import com.lms_backend.lms_project.Utility.Constant;
import com.lms_backend.lms_project.dao.UserDAO;
import com.lms_backend.lms_project.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@Component
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    @Autowired
    private UserDAO userDAO;



    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String provider = userRequest.getClientRegistration().getRegistrationId(); // "google", "facebook"
        String email = null, firstName = null, lastName = null, avatarUrl = null, oauth2Id = null, username = null;
        boolean isVerified = false;

        // Extract info based on provider
        if ("google".equalsIgnoreCase(provider)) {
            email = (String) attributes.get("email");
            firstName = (String) attributes.get("given_name");
            lastName = (String) attributes.get("family_name");
            avatarUrl = (String) attributes.get("picture");
            oauth2Id = (String) attributes.get("sub");
            username = firstName + lastName;

            Object verifiedObj = attributes.get("email_verified");
            isVerified = verifiedObj instanceof Boolean && (Boolean) verifiedObj;

        } else if ("facebook".equalsIgnoreCase(provider)) {
            email = (String) attributes.get("email");
            firstName = (String) attributes.get("first_name");
            lastName = (String) attributes.get("last_name");
            oauth2Id = (String) attributes.get("id");
            username = firstName + lastName;

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

            isVerified = email != null;
        } else {
            throw new OAuth2AuthenticationException("Unsupported OAuth2 provider: " + provider);
        }

        if (email == null) {
            throw new OAuth2AuthenticationException("Email is missing from OAuth2 provider response");
        }

        // Lookup existing user
        User user = userDAO.findByEmailId(email);

        if (user == null) {
            // New user
            user = new User();
            user.setEmailId(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setUsername(username);
            user.setAvatar(avatarUrl);
            user.setOauth2_provider(provider);
            user.setOauth2_id(oauth2Id);
            user.setEmail_verified(isVerified);
            user.setRole(Constant.UserRole.ROLE_STUDENT.value());
            user.setStatus(Constant.ActiveStatus.ACTIVE.value());
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdateAt(LocalDateTime.now());

            userDAO.save(user);
            LOG.info("Created new user: email={}, provider={}", email, provider);

        } else {
            // Existing user, check if provider is consistent
            if (user.getOauth2_provider() == null || user.getOauth2_provider().equalsIgnoreCase(provider)) {
                boolean updated = false;

                if (!oauth2Id.equals(user.getOauth2_id())) {
                    user.setOauth2_id(oauth2Id);
                    updated = true;
                }
                if (!provider.equalsIgnoreCase(user.getOauth2_provider())) {
                    user.setOauth2_provider(provider);
                    updated = true;
                }
                if (!user.isEmail_verified() && isVerified) {
                    user.setEmail_verified(true);
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
                    user.setUpdateAt(LocalDateTime.now());
                    userDAO.save(user);
                    LOG.info("Updated user info: email={}, provider={}", email, provider);
                }
            } else {
                // Email đã đăng ký bằng provider khác
                LOG.warn("User exists with different provider: email={}, existingProvider={}, currentProvider={}",
                        email, user.getOauth2_provider(), provider);
                throw new OAuth2AuthenticationException("Account already registered with provider: " + user.getOauth2_provider());
            }
        }

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole())),
                attributes,
                "email"
        );
    }
}
