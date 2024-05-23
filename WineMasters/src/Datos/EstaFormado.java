package Datos;

public class EstaFormado {

    private int idVino;
    private int idTipoUva;
    private int porcentaje;

    // Constructor
    public EstaFormado(int idVino, int idTipoUva, int porcentaje) {
        this.idVino = idVino;
        this.idTipoUva = idTipoUva;
        this.porcentaje = porcentaje;
    }

    // Getters
    public int getIdVino() {
        return idVino;
    }

    public int getIdTipoUva() {
        return idTipoUva;
    }

    public int getPorcentaje() {
        return porcentaje;
    }

    // Setters
    public void setIdVino(int idVino) {
        this.idVino = idVino;
    }

    public void setIdTipoUva(int idTipoUva) {
        this.idTipoUva = idTipoUva;
    }

    public void setPorcentaje(int porcentaje) {
        this.porcentaje = porcentaje;
    }
}
