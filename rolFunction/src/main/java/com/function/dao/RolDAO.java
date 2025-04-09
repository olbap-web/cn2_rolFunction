package com.function.dao;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.function.model.Rol;
import com.function.model.Usuario;

import graphql.GraphQL;

public class RolDAO {

    private static final String DB_USER = "user_bdd_users";
    private static final String DB_PASS = "ActSum.S5_BDY";
    private static final String WALLET_PATH = "C: /Wallet_CSMZSQ3ZR41HPBVN";

    // private static final GraphQL graphQl;

    static {
        System.setProperty("oracle.net.tns_admin", WALLET_PATH);
        System.setProperty("javax.net.ssl.trustStoreType", "SSO");
        System.setProperty("oracle.net.ssl_server_dn_match", "true");
    }

    private static Connection getConnection() throws SQLException {
        System.out.println("🟡 Conectando a Oracle...");
        String url = "jdbc:oracle:thin:@csmzsq3zr41hpbvn_high?TNS_ADMIN=" + WALLET_PATH;
        Connection conn = DriverManager.getConnection(url, DB_USER, DB_PASS);
        System.out.println("🟢 ¡Conexión exitosa!");
        return conn;
    }

    public static Rol crearRol(Rol r) {
        String sql = "INSERT INTO ROL (ID_ROL, DESCRIPCION, ESTADO) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getId());
            ps.setString(2, r.getDescripcion());
            ps.setString(3, r.getEstado());

            int result = ps.executeUpdate();
            System.out.println("✅ Filas insertadas: " + result);
            return r;
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar rol:");
            e.printStackTrace();
            return null;
        }
    }

    public static Rol obtenerRol(int id) {
        String sql = "SELECT * FROM ROL WHERE ID_ROL = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
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
            System.out.println("❌ Error al obtener rol:");
            e.printStackTrace();
        }
        return null;
    }

    public static List<Usuario> getUsuariosByRol(int id) {
        String sql = "SELECT  u.ID_USUARIO, u.USERNAME, u.PASS, u.NOMBRE, u.APELLIDO FROM ROL r  JOIN USUARIO_ROL ur on ur.ROL_ID_ROL = r.ID_ROL JOIN USUARIO u on u.ID_USUARIO = ur.USUARIO_ID_USUARIO WHERE r.ID_ROL = ?";
        
        List<Usuario> users = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                users.add(new Usuario(
                    rs.getInt("ID_USUARIO"),
                    rs.getString("USERNAME"),
                    rs.getString("PASS"),
                    rs.getString("NOMBRE"),
                    rs.getString("APELLIDO")
                    )
                );
            }
            return users;
            
        } catch (SQLException e) {
            System.out.println("❌ Error al obtener rol:");
            e.printStackTrace();
        }
        return null;
    }

    public static List<Rol> listarRoles() {
        String sql = "SELECT * FROM ROL";
        List<Rol> roles = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                roles.add(new Rol(
                        rs.getInt("ID_ROL"),
                        rs.getString("DESCRIPCION"),
                        rs.getString("ESTADO")
                ));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al listar roles:");
            e.printStackTrace();
        }
        return roles;
    }

    public static Rol actualizarRol(Rol r) {
        String sql = "UPDATE ROL SET DESCRIPCION = ?, ESTADO = ? WHERE ID_ROL = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getDescripcion());
            ps.setString(2, r.getEstado());
            ps.setInt(3, r.getId());

            ps.executeUpdate();
            System.out.println("✅ Rol actualizado: " + r.getId());
            return r;
        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar rol:");
            e.printStackTrace();
            return null;
        }
    }

    public static boolean eliminarRol(int id) {
        String sql = "DELETE FROM ROL WHERE ID_ROL = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            boolean deleted = ps.executeUpdate() > 0;
            System.out.println(deleted ? "✅ Rol eliminado: " + id : "⚠️ Rol no encontrado: " + id);
            return deleted;
        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar rol:");
            e.printStackTrace();
            return false;
        }
    }
}
