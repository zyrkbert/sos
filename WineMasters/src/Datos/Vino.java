package Datos;

public class Vino {

    private int id;
    private String nombreBotella; 
    private String anadaBotella;
    private String tipoVino;
    private String bodega; 
    private String denominacionUOrigen;
    private String descripcion;

    // Constructor
    public Vino(int id, String nombreBotella, String anadaBotella, String tipoVino, String bodega, String denominacionUOrigen, String descripcion) {
        this.id = id;
        this.nombreBotella = nombreBotella;
        this.anadaBotella = anadaBotella;
        this.tipoVino = tipoVino;
        this.bodega = bodega;
        this.denominacionUOrigen = denominacionUOrigen;
        this.descripcion = descripcion;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNombreBotella() {
        return nombreBotella;
    }

    public String getAnadaBotella() {
        return anadaBotella;
    }

    public String getTipoVino() {
        return tipoVino;
    }

    public String getBodega() {
        return bodega;
    }

    public String getDenominacionUOrigen() {
        return denominacionUOrigen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setNombreBotella(String nombreBotella) {
        this.nombreBotella = nombreBotella;
    }

    public void setAnadaBotella(String anadaBotella) {
        this.anadaBotella = anadaBotella;
    }

    public void setTipoVino(String tipoVino) {
        this.tipoVino = tipoVino;
    }

    public void setBodega(String bodega) {
        this.bodega = bodega;
    }

    public void setDenominacionUOrigen(String denominacionUOrigen) {
        this.denominacionUOrigen = denominacionUOrigen;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
