package com.tesis.trashtecadmin.Models;

public class Descuentos {
    public String Contenido;
    public String Imagen;
    public String Titulo;
    public String Id;
    public int descuento;
    public boolean vista;
    public boolean notificacion;
    public int puntos;
    public String fecha_creacion;
    public Descuentos(String contenido,String imagen, String titulo, String id, int descuento,int puntos,
                      String fecha_creacion,boolean vista,boolean notificacion){
        this.Contenido = contenido;
        this.Imagen = imagen;
        this.Titulo = titulo;
        this.Id = id;
        this.descuento = descuento;
        this.puntos = puntos;
        this.fecha_creacion = fecha_creacion;
        this.vista = vista;
        this.notificacion = notificacion;
    }
    public Descuentos(){}
}
