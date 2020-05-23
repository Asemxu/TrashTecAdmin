package com.tesis.trashtecadmin.Models;


public class Recojo {
    public String UID;
    public String Fecha;
    public String Centro;
    public String direccion_centro;
    public String usuario;
    public String descripcion;
    public int cantidad_points;

    public Recojo(String UID, String Fecha, String Centro, String direccion_centro,
                  String usuario,String descripcion, int cantidad_points){
        this.UID = UID;
        this.Fecha = Fecha;
        this.Centro = Centro;
        this.direccion_centro = direccion_centro;
        this.usuario = usuario;
        this.descripcion = descripcion;
        this.cantidad_points  = cantidad_points;
    }
}
