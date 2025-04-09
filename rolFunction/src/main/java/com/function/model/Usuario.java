package com.function.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Usuario {
    private int id;
    private String username;
    private String pass;
    private String nombre;
    private String apellido;

    @JsonCreator
    public Usuario(@JsonProperty("id_usuario") int id, 
               @JsonProperty("username") String username, 
               @JsonProperty("pass") String pass,
               @JsonProperty("nombre") String nombre,
               @JsonProperty("apellido") String apellido

        ) {
        this.id = id;
        this.username = username;
        this.pass = pass;
        this.nombre = nombre;
        this.apellido = apellido;
    }

    public Usuario() {}

    // public Rol(int id, String descripcion, String estado ){
    //     this.id = id;
    //     this.descripcion=descripcion;
    //     this.estado = estado;
    // }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String geUsername() {
        return username;
    }

    public void seUsername(String username) {
        this.username = username;
    }
    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    
}
