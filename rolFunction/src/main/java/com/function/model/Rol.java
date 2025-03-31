package com.function.model;


// import jakarta.persistence.*;

// @Entity
// @Table(name = "ROL", schema = "USER_BDD_USERS")
public class Rol {
    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @Column(name = "ID_ROL")
    private int id;
    // @Column(name = "DESCRIPCION", nullable = false, length = 255)
    private String descripcion;
    // @Column(name = "ESTADO", nullable = false, length = 1)
    private String estado;

    public Rol(int id, String descripcion, String estado ){
        this.id = id;
        this.descripcion=descripcion;
        this.estado = estado;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
