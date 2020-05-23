package com.tesis.trashtecadmin.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tesis.trashtecadmin.Models.User;
import com.tesis.trashtecadmin.R;

import java.util.List;

public class UsuarioAdapter extends ArrayAdapter {
    private Context  context;
    private int resource;
    private List<User> usuarios;
    public UsuarioAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.usuarios = objects;
    }

    @SuppressLint({"ViewHolder", "SetTextI18n"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater= LayoutInflater.from(context);
        convertView=inflater.inflate(resource,parent,false);
        User usuario = usuarios.get(position);
        TextView id = convertView.findViewById(R.id.id_usuario);
        TextView nombres = convertView.findViewById(R.id.nombres_usuario);

        id.setText("Id: "+usuario.UID);
        nombres.setText(usuario.Name);

        return convertView;
    }

    @Override
    public int getCount() {
        return usuarios.size();
    }
}
