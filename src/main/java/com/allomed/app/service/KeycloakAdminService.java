package com.allomed.app.service;

import java.util.Collections;
import javax.annotation.PostConstruct;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KeycloakAdminService {

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
}
