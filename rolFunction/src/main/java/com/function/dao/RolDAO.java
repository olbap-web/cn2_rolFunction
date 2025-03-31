// package com.function.dao;


// import com.function.model.Rol;
// import com.zaxxer.hikari.HikariConfig;
// import com.zaxxer.hikari.HikariDataSource;

// import java.sql.*;
// import java.util.ArrayList;
// import java.util.List;

// public class RolDAO {
//     private static final HikariDataSource dataSource;

//     static {
//         HikariConfig config = new HikariConfig();
//         config.setJdbcUrl(System.getenv("DB_URL"));
//         config.setUsername(System.getenv("DB_USER"));
//         config.setPassword(System.getenv("DB_PASSWORD"));
//         config.setMaximumPoolSize(10);
//         config.setDriverClassName("oracle.jdbc.OracleDriver");
        
//         // Configuraciones específicas para Oracle
//         config.addDataSourceProperty("cachePrepStmts", "true");
//         config.addDataSourceProperty("prepStmtCacheSize", "250");
//         config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        
//         dataSource = new HikariDataSource(config);
//     }

//     // CREATE
//     public Rol create(Rol rol) throws SQLException {
//         String sql = "INSERT INTO ROLES (ID, DESCRIPCION, ESTADO) VALUES (?, ?, ?)";
        
//         try (Connection conn = dataSource.getConnection();
//              PreparedStatement stmt = conn.prepareStatement(sql, 
//                  new String[] {"ID"})) {
            
//             stmt.setString(1, rol.getId());
//             stmt.setString(2, rol.getDescripcion());
//             stmt.setString(3, rol.getEstado());
            
//             int affectedRows = stmt.executeUpdate();
            
//             if (affectedRows == 0) {
//                 throw new SQLException("No se pudo crear el rol");
//             }
            
//             try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
//                 if (generatedKeys.next()) {
//                     rol.setId(generatedKeys.getString(1));
//                 }
//             }
            
//             return rol;
//         }
//     }

//     // READ (by ID)
//     public Rol getById(String id) throws SQLException {
//         String sql = "SELECT * FROM ROLES WHERE ID = ?";
        
//         try (Connection conn = dataSource.getConnection();
//              PreparedStatement stmt = conn.prepareStatement(sql)) {
            
//             stmt.setString(1, id);
            
//             try (ResultSet rs = stmt.executeQuery()) {
//                 if (rs.next()) {
//                     return mapRowToRol(rs);
//                 }
//                 return null;
//             }
//         }
//     }

//     // READ (all)
//     public List<Rol> getAll() throws SQLException {
//         String sql = "SELECT * FROM ROLES";
//         List<Rol> roles = new ArrayList<>();
        
//         try (Connection conn = dataSource.getConnection();
//              Statement stmt = conn.createStatement();
//              ResultSet rs = stmt.executeQuery(sql)) {
            
//             while (rs.next()) {
//                 roles.add(mapRowToRol(rs));
//             }
            
//             return roles;
//         }
//     }

//     // UPDATE
//     public Rol update(Rol rol) throws SQLException {
//         String sql = "UPDATE ROLES SET NOMBRE = ?, DESCRIPCION = ? WHERE ID = ?";
        
//         try (Connection conn = dataSource.getConnection();
//              PreparedStatement stmt = conn.prepareStatement(sql)) {
            
//             stmt.setString(1, rol.getId());
//             stmt.setString(2, rol.getDescripcion());
//             stmt.setString(3, rol.getEstado());
            
//             int affectedRows = stmt.executeUpdate();
            
//             if (affectedRows == 0) {
//                 throw new SQLException("No se encontró el rol con ID: " + rol.getId());
//             }
            
//             return rol;
//         }
//     }

//     // DELETE
//     public boolean delete(String id) throws SQLException {
//         String sql = "DELETE FROM ROLES WHERE ID = ?";
        
//         try (Connection conn = dataSource.getConnection();
//              PreparedStatement stmt = conn.prepareStatement(sql)) {
            
//             stmt.setString(1, id);
            
//             int affectedRows = stmt.executeUpdate();
//             return affectedRows > 0;
//         }
//     }

//     // Método auxiliar para mapear ResultSet a objeto Rol
//     private Rol mapRowToRol(ResultSet rs) throws SQLException {
//         Rol rol = new Rol();
//         rol.setId(rs.getString("ID"));
//         rol.setDescripcion(rs.getString("DESCRIPCION"));
//         rol.setEstado(rs.getString("ESTADO"));
//         return rol;
//     }

//     // Cierre del pool de conexiones (llamar al detener la aplicación)
//     public static void closeDataSource() {
//         if (dataSource != null && !dataSource.isClosed()) {
//             dataSource.close();
//         }
//     }
// }