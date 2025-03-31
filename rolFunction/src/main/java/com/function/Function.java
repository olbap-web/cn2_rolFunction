
package com.function;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.function.dao.RolDAO;
import com.function.model.Rol;

import java.util.*;
import java.sql.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

/**
 * Azure Functions with HTTP Trigger for Role CRUD operations.
 */
public class Function {
    @FunctionName("rol")
    public HttpResponseMessage run(
        @HttpTrigger(
            name = "req", 
            methods = { HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE },
            authLevel = AuthorizationLevel.FUNCTION
        ) HttpRequestMessage<Optional<String>> request,
        final ExecutionContext context
    ) {
        context.getLogger().info("Processing role request.");

        HttpMethod method = request.getHttpMethod();
        // ObjectMapper objectMapper = new ObjectMapper()
        //         .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) 
        //         .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE); 

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); 
        
        String requestBody = request.getBody().orElse("");

        try {
            switch (method) {
                case GET:
                    String idParam = request.getQueryParameters().get("id");
                    if (idParam != null) {
                        int id = Integer.parseInt(idParam);
                        return getRolById(request, id);
                    }
                    return getRoles(request);
                case POST:
                    Rol newRol = objectMapper.readValue(requestBody, Rol.class);
                    return createRole(request, newRol);
                case PUT:
                    Rol updateRol = objectMapper.readValue(requestBody, Rol.class);
                    return updateRole(request, updateRol);
                case DELETE:
                    int id = Integer.parseInt(request.getQueryParameters().get("id"));
                    return deleteRole(request, id);
                default:
                    return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                            .body("{\"error\": \"Unsupported HTTP method.\"}")
                            .build();
            }
        } catch (Exception e) {
            context.getLogger().severe("Error processing request: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Server error occurred: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    private HttpResponseMessage getRoles(HttpRequestMessage<Optional<String>> request) {
        List<Rol> roles = RolDAO.listarRoles();
        return request.createResponseBuilder(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(roles)
                .build();
    }

    private HttpResponseMessage getRolById(HttpRequestMessage<Optional<String>> request, int id) {
        Rol rol = RolDAO.obtenerRol(id);
        if (rol != null) {
            return request.createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(rol)
                    .build();
        } else {
            return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"Role not found.\"}")
                    .build();
        }
    }

    private HttpResponseMessage createRole(HttpRequestMessage<Optional<String>> request, Rol rol) {
        RolDAO.crearRol(rol);
        return request.createResponseBuilder(HttpStatus.CREATED)
                .body("{\"message\": \"Role created successfully.\"}")
                .build();
    }

    private HttpResponseMessage updateRole(HttpRequestMessage<Optional<String>> request, Rol rol) {
        RolDAO.actualizarRol(rol);
        return request.createResponseBuilder(HttpStatus.OK)
                .body("{\"message\": \"Role updated successfully.\"}")
                .build();
    }

    private HttpResponseMessage deleteRole(HttpRequestMessage<Optional<String>> request, int id) {
        boolean deleted = RolDAO.eliminarRol(id);
        if (deleted) {
            return request.createResponseBuilder(HttpStatus.OK)
                    .body("{\"message\": \"Role deleted successfully.\"}")
                    .build();
        } else {
            return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"Role not found.\"}")
                    .build();
        }
    }
}

