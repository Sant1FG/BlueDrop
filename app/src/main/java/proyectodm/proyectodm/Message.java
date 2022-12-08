package proyectodm.proyectodm;

import java.util.Date;

public class Message {
    private Date hora;
    private String mensaje;
    private String emisor;

    public Message(String usuario, Date hora, String mensaje) {
        this.hora = hora;
        this.mensaje = mensaje;
        this.emisor = usuario;
    }

    public String getMensaje() { return this.mensaje; }
    public Date getHora() { return this.hora; }
    public String getEmisor() { return this.emisor; }

    public String getHoraString() {
        return String.format("%tr", this.hora);
    }
}
