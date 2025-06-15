package com.allomed.app.service;

import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KeycloakAdminService {

    private static final Logger LOG = LoggerFactory.getLogger(KeycloakAdminService.class);

    @Value("${keycloak.auth-server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak-admin.username}")
    private String adminUsername;

    @Value("${keycloak-admin.password}")
    private String adminPassword;

    @Value("${keycloak-admin.client-id}")
    private String adminClientId;

    private Keycloak keycloak;

    @PostConstruct
    public void init() {
        keycloak = KeycloakBuilder.builder()
            .serverUrl(serverUrl)
            .realm("master")
            .grantType(OAuth2Constants.PASSWORD)
            .clientId(adminClientId)
            .username(adminUsername)
            .password(adminPassword)
            .build();
    }

    public void addRoleToUser(String username, String roleName) {
        RealmResource realmResource = keycloak.realm(realm);
        var user = realmResource.users().search(username, 0, 1).get(0);
        String userId = user.getId();

        RoleRepresentation role = realmResource.roles().get(roleName).toRepresentation();
        realmResource.users().get(userId).roles().realmLevel().add(Collections.singletonList(role));
    }

    /**
     * Get user email address from Keycloak
     *
     * @param username The username to get email for
     * @return The user's email address or null if not found
     */
    public String getUserEmail(String username) {
        try {
            UserRepresentation user = getUserByUsername(username);
            return user != null ? user.getEmail() : null;
        } catch (Exception e) {
            LOG.error("Failed to get user email from Keycloak: {} - Error: {}", username, e.getMessage());
            return null;
        }
    }

    /**
     * Get user's full name from Keycloak
     *
     * @param username The username to get name for
     * @return The user's full name or null if not found
     */
    public String getUserFullName(String username) {
        try {
            UserRepresentation user = getUserByUsername(username);
            if (user != null) {
                String firstName = user.getFirstName();
                String lastName = user.getLastName();
                if (firstName != null && lastName != null) {
                    return firstName + " " + lastName;
                } else if (firstName != null) {
                    return firstName;
                } else if (lastName != null) {
                    return lastName;
                }
            }
            return null;
        } catch (Exception e) {
            LOG.error("Failed to get user name from Keycloak: {} - Error: {}", username, e.getMessage());
            return null;
        }
    }

    /**
     * Get user information from Keycloak by username
     *
     * @param username The username to search for
     * @return UserRepresentation or null if not found
     */
    public UserRepresentation getUserByUsername(String username) {
        try {
            RealmResource realmResource = keycloak.realm(realm);
            List<UserRepresentation> users = realmResource.users().search(username, 0, 1);

            return users.isEmpty() ? null : users.get(0);
        } catch (Exception e) {
            LOG.error("Failed to get user from Keycloak: {} - Error: {}", username, e.getMessage());
            return null;
        }
    }

    /**
     * Get user email address from Keycloak by user ID
     *
     * @param userId The user ID to get email for
     * @return The user's email address or null if not found
     */
    public String getUserEmailById(String userId) {
        try {
            UserRepresentation user = getUserById(userId);
            return user != null ? user.getEmail() : null;
        } catch (Exception e) {
            LOG.error("Failed to get user email from Keycloak by ID: {} - Error: {}", userId, e.getMessage());
            return null;
        }
    }

    /**
     * Get user's full name from Keycloak by user ID
     *
     * @param userId The user ID to get name for
     * @return The user's full name or null if not found
     */
    public String getUserFullNameById(String userId) {
        try {
            UserRepresentation user = getUserById(userId);
            if (user != null) {
                String firstName = user.getFirstName();
                String lastName = user.getLastName();
                if (firstName != null && lastName != null) {
                    return firstName + " " + lastName;
                } else if (firstName != null) {
                    return firstName;
                } else if (lastName != null) {
                    return lastName;
                }
            }
            return null;
        } catch (Exception e) {
            LOG.error("Failed to get user name from Keycloak by ID: {} - Error: {}", userId, e.getMessage());
            return null;
        }
    }

    /**
     * Get user information from Keycloak by user ID
     *
     * @param userId The user ID to search for
     * @return UserRepresentation or null if not found
     */
    public UserRepresentation getUserById(String userId) {
        try {
            RealmResource realmResource = keycloak.realm(realm);
            return realmResource.users().get(userId).toRepresentation();
        } catch (Exception e) {
            LOG.error("Failed to get user from Keycloak by ID: {} - Error: {}", userId, e.getMessage());
            return null;
        }
    }
}
