package com.tesis.trashtecadmin.Adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tesis.trashtecadmin.Activitys.ListaDescuentosActivity;
import com.tesis.trashtecadmin.Activitys.MainActivity;
import com.tesis.trashtecadmin.Helper.Firebase_Variables;
import com.tesis.trashtecadmin.Models.Descuentos;
import com.tesis.trashtecadmin.Models.User;
import com.tesis.trashtecadmin.R;

import java.util.List;

public class DescuentoAdapter extends ArrayAdapter {
    private int resource;
    private String UID;
    private DatabaseReference databaseReference = Firebase_Variables.databaseReference;
    private ListaDescuentosActivity activity;
    private Context context;
    private List<String> keys;
    private List<Descuentos> descuentos;
    private ProgressDialog dialog;
    public DescuentoAdapter(String UID,List<String> keys,@NonNull Context context, int resource, @NonNull List<Descuentos> descuentos,ListaDescuentosActivity activity) {
        super(context, resource, descuentos);
        this.context = context;
        this.activity = activity;
        this.resource = resource;
        this.UID = UID;
        this.keys = keys;
        this.descuentos =descuentos;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater= LayoutInflater.from(context);
        convertView=inflater.inflate(resource,parent,false);
        View nuevo = convertView;
        ImageView imgdescuento = nuevo.findViewById(R.id.imagen_descuento);
        TextView title = nuevo.findViewById(R.id.titulo_descuento);
        TextView contenido = nuevo.findViewById(R.id.contenido_descuento);
        TextView cantidad_descuento = nuevo.findViewById(R.id.cantidad_descuento);
        Button btn_eliminar_descuento = nuevo.findViewById(R.id.btn_eliminar_descuento);
        Descuentos descuento = descuentos.get(position);
        title.setText(descuento.Titulo);
        contenido.setText(descuento.Contenido);
        EliminarDescuento(btn_eliminar_descuento,keys.get(position));
        Picasso.get().load(descuento.Imagen).into(imgdescuento);
        cantidad_descuento.setText(""+descuento.descuento+"%");
        return nuevo;
    }

    private void EliminarDescuento(Button btn_eliminar_descuento, String key) {
        btn_eliminar_descuento.setOnClickListener(v->{
            Context context = new ContextThemeWrapper(activity,R.style.Theme_MaterialComponents);
            MaterialAlertDialogBuilder dialog_material = new MaterialAlertDialogBuilder(context);
            dialog_material.setTitle("Aviso");
            dialog_material.setIcon(R.drawable.delete);
            dialog_material.setMessage("Deseea realmente borrar la publicaciòn");
            dialog_material.setPositiveButton("Aceptar", (dialog_, which) -> {
                dialog = ProgressDialog.show(context,"Descuentos","Eliminando Descuento...",false,false);
                DeleteDescuento(key);
            }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            dialog_material.show();
        });

    }

    private void DeleteDescuento(String key) {
        databaseReference.child("Users").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                int cantidad_descuentos = user.Cantidad_descuentos - 1;
                databaseReference.child("Users").child(UID).child("Cantidad_descuentos").setValue(cantidad_descuentos);
                databaseReference.child("Descuentos_Usuarios").child(UID).child(key).removeValue();
                Toast.makeText(context,"Descuento Eliminado",Toast.LENGTH_LONG).show();
                Intent main_intent = new Intent(activity, MainActivity.class);
                activity.startActivity(main_intent);
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context,"No se pudo obtener la información",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getCount() {
        return descuentos.size();
    }
}
