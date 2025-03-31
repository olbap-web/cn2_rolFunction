package com.function;

import com.fasterxml.jackson.databind.ObjectMapper;
// import java.sql.Connection;
// import java.sql.ResultSet;
// import java.sql.SQLException;
import com.function.dao.OracleDBManager;
import com.function.model.Rol;

import java.util.*;
// import java.sql.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    /**
     * This function listens at endpoint "/api/functionRol". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/functionRol
     * 2. curl {your host}/api/functionRol?name=HTTP%20Query
     */
    @FunctionName("rol")
    public HttpResponseMessage run(
        @HttpTrigger(
            name = "req", 
            methods = {
                HttpMethod.GET, 
                HttpMethod.POST
            }, 
            authLevel = AuthorizationLevel.FUNCTION
        ) 
        HttpRequestMessage<Optional<String>> request,
        final ExecutionContext context
    ) {

        context.getLogger().info("Java HTTP trigger processed a request for roles.");

        HttpMethod method = request.getHttpMethod();
        String idParam = request.getQueryParameters().get("id");
        ObjectMapper objectMapper = new ObjectMapper();

        if (method == HttpMethod.GET) {
            try {
                if (idParam == null || idParam.isEmpty()) {
                    List<Rol> roles = OracleDBManager.getRoles();
                    String jsonResponse = objectMapper.writeValueAsString(roles);
                    return request.createResponseBuilder(HttpStatus.OK)
                            .body(jsonResponse)
                            .header("Content-Type", "application/json")
                            .build();
                }

                int id = Integer.parseInt(idParam);
                Rol rol = OracleDBManager.getRolById(id);
                if (rol != null) {
                    String jsonResponse = objectMapper.writeValueAsString(rol);
                    return request.createResponseBuilder(HttpStatus.OK)
                            .body(jsonResponse)
                            .header("Content-Type", "application/json")
                            .build();
                } else {
                    return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                            .body("{\"error\": \"Rol no encontrado\"}")
                            .header("Content-Type", "application/json")
                            .build();
                }
            } catch (Exception e) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body("{\"error\": \"Error procesando la solicitud\"}")
                        .header("Content-Type", "application/json")
                        .build();
            }
        }

        else if (method == HttpMethod.POST) {
            try {
                String body = request.getBody().orElse("");
                if (body.isEmpty()) {
                    return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                            .body("{\"error\": \"El cuerpo de la solicitud está vacío\"}")
                            .header("Content-Type", "application/json")
                            .build();
                }

                Rol newRol = objectMapper.readValue(body, Rol.class);
                if (newRol.getId() <= 0 || newRol.getDescripcion() == null || newRol.getDescripcion().isEmpty()) {
                    return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                            .body("{\"error\": \"Faltan datos para el rol\"}")
                            .header("Content-Type", "application/json")
                            .build();
                }

                boolean added = OracleDBManager.addRol(newRol);
                if (added) {
                    return request.createResponseBuilder(HttpStatus.CREATED)
                            .body("{\"message\": \"Rol creado exitosamente\"}")
                            .header("Content-Type", "application/json")
                            .build();
                } else {
                    return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("{\"error\": \"Error al agregar el rol\"}")
                            .header("Content-Type", "application/json")
                            .build();
                }
            } catch (Exception e) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body("{\"error\": \"Error procesando la solicitud POST\"}")
                        .header("Content-Type", "application/json")
                        .build();
            }
        }

        else if (method == HttpMethod.PUT) {
            try {
                if (idParam == null || idParam.isEmpty()) {
                    return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                            .body("{\"error\": \"El parámetro 'id' es obligatorio en la query string.\"}")
                            .header("Content-Type", "application/json")
                            .build();
                }

                int id = Integer.parseInt(idParam);

                String body = request.getBody().orElse("");
                if (body.isEmpty()) {
                    return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                            .body("{\"error\": \"El cuerpo de la solicitud está vacío. Se requiere el nuevo nombre del rol.\"}")
                            .header("Content-Type", "application/json")
                            .build();
                }

                Rol updatedRol = objectMapper.readValue(body, Rol.class);

                // Actualizar el rol
                boolean updated = OracleDBManager.updateRol(id, updatedRol.getDescripcion());
                if (updated) {
                    return request.createResponseBuilder(HttpStatus.OK)
                            .body("{\"message\": \"Rol actualizado exitosamente\"}")
                            .header("Content-Type", "application/json")
                            .build();
                } else {
                    return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                            .body("{\"error\": \"Rol no encontrado para actualizar\"}")
                            .header("Content-Type", "application/json")
                            .build();
                }
            } catch (Exception e) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body("{\"error\": \"Error procesando la solicitud PUT\"}")
                        .header("Content-Type", "application/json")
                        .build();
            }
        }

        return request.createResponseBuilder(HttpStatus.METHOD_NOT_ALLOWED)
                .body("{\"error\": \"Método no permitido\"}")
                .header("Content-Type", "application/json")
                .build();

    }
    // /**
    //  * Método para obtener todos los roles de la base de datos.
    //  */
    // private HttpResponseMessage getAllRoles(HttpRequestMessage<Optional<String>> request) {
    //     String sql = "SELECT ID, DESCRIPCION FROM ROL;";
    //     List<Map<String, Object>> roles = new ArrayList<>();
        

    //     // try (Connection conn = OracleDBManager.getConnection();
    //     //      Statement stmt = conn.createStatement();
    //     //      ResultSet rs = stmt.executeQuery(sql)) {

    //     //     while (rs.next()) {
    //     //         Map<String, Object> fila = new HashMap<>();
    //     //         fila.put("id", rs.getInt("ID"));
    //     //         fila.put("descripcion", rs.getString("DESCRIPCION"));
    //     //         roles.add(fila);
    //     //     }

    //     // } catch (SQLException e) {
    //     //     return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
    //     //             .body("{\"error\": \"Error al consultar la base de datos.\"}")
    //     //             .build();
    //     // }

    //     return request.createResponseBuilder(HttpStatus.OK)
    //             .header("Content-Type", "application/json")
    //             .body(roles)
    //             .build();
    // }
}
