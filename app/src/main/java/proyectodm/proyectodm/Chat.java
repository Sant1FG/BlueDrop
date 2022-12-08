package proyectodm.proyectodm;

import android.annotation.SuppressLint;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Chat {
    private Date fecha;
    private String contacto;
    private ArrayList<Message> mensajes;

    public Chat (String username, Date fecha, ArrayList<Message> mensajes) {
        this.contacto = username;
        this.fecha = fecha;
        this.mensajes = mensajes;
    }

    public ArrayList<Message> getMensajes() { return this.mensajes; }
    public String getContacto() {
        return this.contacto;
    }
    public Date getFecha() {
        return this.fecha;
     }


     @SuppressLint("DefaultLocale")
     public String getFechaString() {
        return String.format("%td %tb %tY", this.fecha,
                this.fecha, this.fecha);
     }

     @SuppressLint("DefaultLocale")
     public String toString() {
        String toret;
        toret = String.format("%s \n %td %tb %tY",
            this.getContacto(), this.getFecha(), this.getFecha(),
            this.getFecha());
        return toret;
     }
}
