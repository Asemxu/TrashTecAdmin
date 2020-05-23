package com.tesis.trashtecadmin.Models;

import java.util.HashMap;

public class User {
    public String PhoneNumber;
    public String Name;
    public String email;
    public String UID;
    public int Cantidad_points;
    public int cantidad_recojos;
    public int Cantidad_descuentos;
    public int Cantidad_publicaciones ;
    public boolean Tipo_cuenta;
    public boolean acepta_notificaciones;
    public HashMap<String,Publicacion> Publicaciones;
    public User(){

    }
}
