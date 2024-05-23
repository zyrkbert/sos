package Datos;

import java.util.Date;

import jakarta.json.bind.annotation.JsonbProperty;

public class Usuario {

    private int id;
    private String nombre;
    private Date fechaN;
    private String correo;
    private String descripcion;

    // Constructor
    public Usuario(int id, String nombre, Date fechaN, String correo, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.fechaN = fechaN;
        this.correo = correo;
        this.descripcion = descripcion;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Date getFechaN() {
        return fechaN;
    }
    @JsonbProperty("correo_electronico")
    public String getCorreo() {
        return correo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setFechaN(Date fechaN) {
        this.fechaN = fechaN;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
