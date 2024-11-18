package com.userms.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.userms.entity.CustomUserDetails;
import com.userms.entity.UserEntity;
import com.userms.repository.IUserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

@Service
public class PermissionService {

    private static final Logger logger = LoggerFactory.getLogger(PermissionService.class);

    @Autowired
    private IUserRepo userRepository;

    private final ObjectMapper objectMapper;

    @Autowired
    public PermissionService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Value("${superAdmin.role}")
    private String superAdminRole;

    public boolean hasPermission(CustomUserDetails userDetails, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String requestMethod = request.getMethod();

        requestUri = requestUri.replaceAll("\\d+(?=$)", "").replaceAll("[a-zA-Z0-9._%+-]+@gmail\\.com", "").replaceAll("\\?.*", "");

        if (userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals(superAdminRole))) {
            logger.info("User {} has SUPERADMIN role, granting full access", userDetails.getUsername());
            return true;
        }

        UserEntity user = userRepository.findByEmailId(userDetails.getUsername());
        if (user == null || user.getPermission() == null) {
            logger.warn("User not found or permissions are not set for user: {}", userDetails.getUsername());
            return false;
        }

        String permissionName = user.getPermission().getPermissionName();
        String permissionsJson = user.getPermission().getPermission();

        try {
            JsonNode permissions = objectMapper.readTree(permissionsJson);
            return checkPermissions(userDetails, permissionName, permissions, requestUri, requestMethod);
        } catch (IOException e) {
            logger.error("Failed to parse permissions JSON for user: {}", userDetails.getUsername(), e);
            return false;
        }
    }

    private boolean checkPermissions(CustomUserDetails userDetails, String permissionName, JsonNode permissions, String requestUri, String requestMethod) {
        Iterator<Map.Entry<String, JsonNode>> moduleIterator = permissions.fields();
        while (moduleIterator.hasNext()) {
            Map.Entry<String, JsonNode> moduleEntry = moduleIterator.next();
            JsonNode moduleArray = moduleEntry.getValue();

            if (moduleArray.isArray()) {
                for (JsonNode module : moduleArray) {
                    JsonNode actionsNode = module.path("actions");
                    if (actionsNode.isArray()) {
                        for (JsonNode actionNode : actionsNode) {
                            String actionUrl = actionNode.path("pageUrl").asText().replaceAll("\\{[^}]*\\}", "");
                            String actionMethod = actionNode.path("action").asText();
                            if (requestUri.equals(actionUrl) && requestMethod.equalsIgnoreCase(actionMethod)) {
                                boolean writeAccess = actionNode.has("writeAccess") && actionNode.path("writeAccess").asBoolean();
                                boolean readAccess = actionNode.has("readAccess") && actionNode.path("readAccess").asBoolean();

                                boolean hasAccess = (requestMethod.equalsIgnoreCase("POST") && writeAccess)
                                        || (requestMethod.equalsIgnoreCase("GET") && readAccess)
                                        || (requestMethod.equalsIgnoreCase("PUT") && writeAccess)
                                        || (requestMethod.equalsIgnoreCase("DELETE") && writeAccess);

                                if (hasAccess) {
                                    logger.info("Access granted for user: {} for URI: {} and method: {}", userDetails.getUsername(), requestUri, requestMethod);
                                } else {
                                    logger.warn("Access denied for user: {} for URI: {} and method: {}", userDetails.getUsername(), requestUri, requestMethod);
                                }

                                logger.info("Authorities for user: {} are: {}", userDetails.getUsername(), userDetails.getAuthorities());

                                return hasAccess && userDetails.getAuthorities().stream()
                                        .anyMatch(auth -> auth.getAuthority().equals(permissionName));
                            }
                        }
                    }
                }
            }
        }
        logger.info("No matching permissions found for user: {} for URI: {} and method: {}", userDetails.getUsername(), requestUri, requestMethod);
        return false;
    }
}
