package com.function.dao;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.BinaryData;
import com.azure.messaging.eventgrid.EventGridEvent;
import com.azure.messaging.eventgrid.EventGridPublisherClient;
import com.azure.messaging.eventgrid.EventGridPublisherClientBuilder;
import com.function.model.Rol;
import com.function.model.Usuario;
import com.function.utils.ResourceUtils;

//________________________________-

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.HttpsURLConnection;
import java.security.cert.X509Certificate;

//____

public class RolDAO {

    private static final String SELECT_USUARIOS_BY_ROL_SQL = """
        SELECT u.ID_USUARIO, u.USERNAME, u.PASS, u.NOMBRE, u.APELLIDO
        FROM ROL r
        JOIN USUARIO_ROL ur ON ur.ROL_ID_ROL = r.ID_ROL
        JOIN USUARIO u ON u.ID_USUARIO = ur.USUARIO_ID_USUARIO
        WHERE r.ID_ROL = ?
    """;
    private static final String INSERT_ROL_SQL = "INSERT INTO ROL (ID_ROL, DESCRIPCION, ESTADO) VALUES (?, ?, ?)";
    private static final String SELECT_ROL_BY_ID_SQL = "SELECT * FROM ROL WHERE ID_ROL = ?";
    private static final String SELECT_ALL_ROLES_SQL = "SELECT * FROM ROL";
    private static final String UPDATE_ROL_SQL = "UPDATE ROL SET DESCRIPCION = ?, ESTADO = ? WHERE ID_ROL = ?";
    private static final String DELETE_ROL_SQL = "DELETE FROM ROL WHERE ID_ROL = ?";

    private static final String DB_USER = "user_bdd_users";
    private static final String DB_PASS = "ActSum.S5_BDY";
    private static String WALLET_PATH;

    private static final String eventGridTopicEndpoint = "https://usuarioroleventgrid.eastus-1.eventgrid.azure.net/api/events";
    private static final String eventGridTopicKey = "2iHMJNambxhzRRcfxSgCTjek8W2DVb4hrzotRW4e2axJEKggT1s9JQQJ99BEACYeBjFXJ3w3AAABAZEGKkij";

    private static final Logger logger = Logger.getLogger(RolDAO.class.getName());

    static {
        try {
            WALLET_PATH = ResourceUtils.copyWalletToTemp();

            System.setProperty("oracle.net.tns_admin", WALLET_PATH);
            System.setProperty("javax.net.ssl.trustStoreType", "SSO");
            System.setProperty("oracle.net.ssl_server_dn_match", "true");

        } catch (IOException e) {
            logger.severe("Error al copiar wallet:");
            e.printStackTrace();
        }
    }

    private static Connection getConnection() throws SQLException {
        logger.info("Conectando a Oracle...");
        String url = "jdbc:oracle:thin:@tcps://adb.sa-santiago-1.oraclecloud.com:1522/" +
                     "g0201d765b4dc6a_csmzsq3zr41hpbvn_tp.adb.oraclecloud.com";

        Connection conn = DriverManager.getConnection(url, DB_USER, DB_PASS);
        logger.info("¡Conexión exitosa!");
        return conn;
    }

    public static Rol crearRol(Rol r) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_ROL_SQL)) {
            ps.setInt(1, r.getId());
            ps.setString(2, r.getDescripcion());
            ps.setString(3, r.getEstado());

            int result = ps.executeUpdate();
            logger.info("Filas insertadas: " + result);

            sendEventToEventGrid("RolCreado.eventGridTrigger","Rol Creado: id: "+r.getId()+"| desc: "+r.getDescripcion() , logger);

            return r;
        } catch (SQLException e) {
            logger.severe("Error al insertar rol: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static Rol obtenerRol(int id) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ROL_BY_ID_SQL)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Rol(
                        rs.getInt("ID_ROL"),
                        rs.getString("DESCRIPCION"),
                        rs.getString("ESTADO")
                );
            }
        } catch (SQLException e) {
            logger.severe("Error al obtener rol: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static List<Usuario> getUsuariosByRol(int id) {
        List<Usuario> users = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_USUARIOS_BY_ROL_SQL)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                users.add(new Usuario(
                    rs.getInt("ID_USUARIO"),
                    rs.getString("USERNAME"),
                    rs.getString("PASS"),
                    rs.getString("NOMBRE"),
                    rs.getString("APELLIDO")
                ));
            }
            return users;

        } catch (SQLException e) {
            logger.severe("Error al obtener usuarios por rol: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static List<Rol> listarRoles() {
        List<Rol> roles = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_ROLES_SQL)) {
            while (rs.next()) {
                roles.add(new Rol(
                        rs.getInt("ID_ROL"),
                        rs.getString("DESCRIPCION"),
                        rs.getString("ESTADO")
                ));
            }
        } catch (SQLException e) {
            logger.severe("Error al listar roles: " + e.getMessage());
            e.printStackTrace();
        }
        return roles;
    }

    public static Rol actualizarRol(Rol r) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_ROL_SQL)) {
            ps.setString(1, r.getDescripcion());
            ps.setString(2, r.getEstado());
            ps.setInt(3, r.getId());

            ps.executeUpdate();
            logger.info("Rol actualizado: " + r.getId());

            sendEventToEventGrid("RolActualizado.eventGridTrigger", "Se actualiza ROL: id "+r.getId()+" | desc: "+r.getDescripcion(), logger);
            
            return r;
        } catch (SQLException e) {
            logger.severe("Error al actualizar rol: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static boolean eliminarRol(int id) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_ROL_SQL)) {
            ps.setInt(1, id);
            boolean deleted = ps.executeUpdate() > 0;
            logger.info(deleted ? "Rol eliminado: " + id : "Rol no encontrado: " + id);

            if (deleted) {
                sendEventToEventGrid("RolEliminado.eventGridTrigger", "Se eliminó el rol con ID: " + id, logger);
            }

            return deleted;
        } catch (SQLException e) {
            logger.severe("Error al eliminar rol: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // public static boolean sendEventToEventGrid(String eventType, String data, Logger logger) {
    //     try {
    //         EventGridPublisherClient<EventGridEvent> client = new EventGridPublisherClientBuilder()
    //                 .endpoint(eventGridTopicEndpoint)
    //                 .credential(new AzureKeyCredential(eventGridTopicKey))
    //                 .buildEventGridEventPublisherClient();


    //         EventGridEvent event = new EventGridEvent(
    //                 "../consumerEG-1.0-SNAPSHOT.jar",
    //                 eventType,
    //                 BinaryData.fromObject(data),
    //                 "1.0"
    //         );

    //         System.out.println(data);

    //         System.out.println(client.toString());


    //         client.sendEvent(event);

    //         logger.info(" Evento enviado correctamente: " + eventType);
    //         return true;
    //     } catch (Exception e) {
    //         logger.severe(" Error al enviar evento: " + e.getMessage());
    //         return false;
    //     }
    // }

    public static boolean sendEventToEventGrid(String eventType, String data, Logger logger) {
        try {
            // ⚠️ TrustManager que confía en todos los certificados (no recomendado en producción)
            TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
            };
    
            // Instala el TrustManager globalmente
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    
            EventGridPublisherClient<EventGridEvent> client = new EventGridPublisherClientBuilder()
                    .endpoint(eventGridTopicEndpoint)
                    .credential(new AzureKeyCredential(eventGridTopicKey))
                    .buildEventGridEventPublisherClient();
    
            EventGridEvent event = new EventGridEvent(
                    "../consumerEG-1.0-SNAPSHOT.jar",
                    eventType,
                    BinaryData.fromObject(data),
                    "1.0"
            );
    
            System.out.println(data);
            System.out.println(client.toString());
    
            client.sendEvent(event);
    
            logger.info("Evento enviado correctamente: " + eventType);
            return true;
        } catch (Exception e) {
            logger.severe("Error al enviar evento: " + e.getMessage());
            return false;
        }
    }
}
