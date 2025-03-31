package com.function.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.function.model.Rol;

public class OracleDBManager {
    private static final String DB_USER = "user_bdd_users";
    private static final String DB_PASSWORD = "ActSum.S5_BDY";
    private static final String DB_URL = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCPS)(HOST=adb.sa-santiago-1.oraclecloud.com)(PORT=1522))(CONNECT_DATA=(SERVICE_NAME=g0201d765b4dc6a_csmzsq3zr41hpbvn_tp.adb.oraclecloud.com))(SECURITY=(SSL_SERVER_DN_MATCH=YES)))?TNS_ADMIN=C:/Wallet_CSMZSQ3ZR41HPBVN"; 
    private static final String TNS_ADMIN = "C:/Wallet_CSMZSQ3ZR41HPBVN"; // Ruta donde está el wallet


    private static final List<Rol> roles = new ArrayList<>();

    static {
        roles.add(new Rol(1, "Administrador", "1"));
        roles.add(new Rol(2, "Usuario", "1"));
        roles.add(new Rol(3, "Invitado", "1"));
    }

    static {
        // Establecer propiedad del wallet
        System.setProperty("oracle.net.tns_admin", TNS_ADMIN);
    }


    public static List<Rol> getRoles() {
        return roles;
    }

    public static Rol getRolById(int id) {
        return roles.stream().filter(rol -> rol.getId() == id).findFirst().orElse(null);
    }

    public static boolean addRol(Rol rol) {
        return roles.add(rol);  
    }
    public static boolean updateRol(int id, String newName) {
        Optional<Rol> rolOptional = roles.stream().filter(rol -> rol.getId() == id).findFirst();
        if (rolOptional.isPresent()) {
            Rol rol = rolOptional.get();
            rol.setDescripcion(newName);  
            return true;
        }
        return false;  
    }


    /////////// BDDDDDD

    /**
     * Método para obtener una conexión a la base de datos.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Método para ejecutar una consulta SELECT.
     */
    public static void executeQuery(String sql) {
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("ID") + " | Nombre: " + rs.getString("NOMBRE"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para ejecutar una consulta INSERT, UPDATE o DELETE.
     */
    public static int executeUpdate(String sql) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            return stmt.executeUpdate(sql);

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // 🔹 Método para probar la conexión
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("✅ Conexión a Oracle establecida correctamente.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar con Oracle: " + e.getMessage());
        }
        return false;
    }

    public static void main(String[] args) {
        // testConnection();

        // Ejecutar una consulta
        // executeQuery("SELECT * FROM ROL");

        // // Insertar un nuevo registro
        // int result = executeUpdate("INSERT INTO EMPLEADOS (ID, NOMBRE) VALUES (100, 'Juan')");
        // if (result > 0) {
        //     System.out.println("Registro insertado con éxito.");
        // }
    }
}
