package com.tesis.trashtecadmin.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.storage.StorageReference;
import com.tesis.trashtecadmin.Helper.Firebase_Variables;
import com.tesis.trashtecadmin.Models.Publicacion;
import com.tesis.trashtecadmin.R;


import java.io.File;
import java.util.List;

public class PublicacionAdapter extends ArrayAdapter {
   private Context context;
   private  int resource;
   private String UID;
   private List<Publicacion> publicaciones;
    public PublicacionAdapter(String UID,@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.publicaciones = objects;
        this.UID = UID;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater= LayoutInflater.from(context);
        convertView=inflater.inflate(resource,parent,false);
        Publicacion publicacion = publicaciones.get(position);
        TextView id = convertView.findViewById(R.id.id_publicacion);
        TextView fecha = convertView.findViewById(R.id.fecha_publicacion);
        TextView descripcion = convertView.findViewById(R.id.descripcion);
        ImageView imagen_publicacion = convertView.findViewById(R.id.imagen_publicacion);
        CargarImagen(publicacion.Id,imagen_publicacion);
        id.setText("Id: "+publicacion.Id);
        fecha.setText("Fecha: "+publicacion.Fecha_creacion);
        descripcion.setText(publicacion.Descripcion);

        return convertView;
    }
    private void CargarImagen(String id,final ImageView foto_publicacion) {
        StorageReference filePath = Firebase_Variables.GetReferenceImage(id,UID);
        try{
            final File file = File.createTempFile("image","png");
            filePath.getFile(file).addOnSuccessListener(taskSnapshot -> {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                foto_publicacion.setImageBitmap(bitmap);
            }).addOnFailureListener(e -> Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show());
        }catch (Exception ex){
            Log.i("Error", ex.getMessage());
        }
    }
    @Override
    public int getCount() {
        return publicaciones.size();
    }
}
