package Datos;

import java.util.Date;

public class Anade {

    private int idUsuario;
    private int idVino;
    private Date fechaAdicion;
    private int puntuacion;

    // Primer constructor
    public Anade(int idUsuario, int idVino, Date fechaAdicion) {
        this.idUsuario = idUsuario;
        this.idVino = idVino;
        this.fechaAdicion = fechaAdicion;
    }

    // Segundo constructor con puntuación
    public Anade(int idUsuario, int idVino, Date fechaAdicion, int puntuacion) {
        this.idUsuario = idUsuario;
        this.idVino = idVino;
        this.fechaAdicion = fechaAdicion;
        this.puntuacion = puntuacion;
    }

    // Getters y Setters
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdVino() {
        return idVino;
    }

    public void setIdVino(int idVino) {
        this.idVino = idVino;
    }

    public Date getFechaAdicion() {
        return fechaAdicion;
    }

    public void setFechaAdicion(Date fechaAdicion) {
        this.fechaAdicion = fechaAdicion;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }
}
