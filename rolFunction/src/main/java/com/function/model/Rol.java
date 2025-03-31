package com.function.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Rol {
    private int id;
    private String descripcion;
    private String estado;

    @JsonCreator
    public Rol(@JsonProperty("id_rol") int id, 
               @JsonProperty("descripcion") String descripcion, 
               @JsonProperty("estado") String estado) {
        this.id = id;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public Rol() {}

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
