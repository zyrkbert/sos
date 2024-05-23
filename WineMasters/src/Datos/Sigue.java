package Datos;

public class Sigue {

    private int idSeguidor;
    private int idSeguido;

    // Constructor
    public Sigue(int idSeguidor, int idSeguido) {
        this.idSeguidor = idSeguidor;
        this.idSeguido = idSeguido;
    }

    // Getter para idSeguidor
    public int getIdSeguidor() {
        return idSeguidor;
    }

    // Setter para idSeguidor
    public void setIdSeguidor(int idSeguidor) {
        this.idSeguidor = idSeguidor;
    }

    // Getter para idSeguido
    public int getIdSeguido() {
        return idSeguido;
    }

    // Setter para idSeguido
    public void setIdSeguido(int idSeguido) {
        this.idSeguido = idSeguido;
    }

}
