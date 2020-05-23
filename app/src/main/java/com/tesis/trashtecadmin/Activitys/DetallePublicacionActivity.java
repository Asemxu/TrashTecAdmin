package com.tesis.trashtecadmin.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.tesis.trashtecadmin.Adapter.ImagenesAdapter;
import com.tesis.trashtecadmin.Helper.Firebase_Variables;
import com.tesis.trashtecadmin.Models.Publicacion;
import com.tesis.trashtecadmin.Models.User;
import com.tesis.trashtecadmin.R;

import java.util.ArrayList;
import java.util.List;

public class DetallePublicacionActivity extends AppCompatActivity {

    SharedPreferences preferences;
    String id_publicacion;
    Button btn_regresar;
    TabLayout indicador;
    String UID;
    TextView fecha_publicacion;
    TextView descripcion;
    Button btn_aprobar;
    ViewPager lista_imagenes;
    ProgressDialog dialog_2;
    ImagenesAdapter imagenesAdapter;
    ProgressDialog dialog;
    List<Integer> fotos;
    DatabaseReference databaseReference = Firebase_Variables.databaseReference;
    Button btn_eliminar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_publicacion);
        InstanciarViews();
        dialog = ProgressDialog.show(DetallePublicacionActivity.this,"Detalle de Publicación","Cargando...",false,false);
        Regresar();
        GetData();
        Aprobar();
        Eliminar();
    }

    private void Eliminar() {
        btn_eliminar.setOnClickListener(v->{
            Context context = new ContextThemeWrapper(DetallePublicacionActivity.this,R.style.Theme_MaterialComponents);
            MaterialAlertDialogBuilder dialog_material = new MaterialAlertDialogBuilder(context);
            dialog_material.setTitle("Aviso");
            dialog_material.setIcon(R.drawable.delete);
            dialog_material.setMessage("Deseea realmente borrar la publicaciòn");
            dialog_material.setPositiveButton("Aceptar", (dialog_, which) -> {
               DeletePublicacion();
            }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            dialog_material.show();

        });
    }

    private void DeletePublicacion() {
        dialog_2 = ProgressDialog.show(DetallePublicacionActivity.this,"Publicación","Eliminando...",false,false);
            databaseReference.child("Users").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User usuario = dataSnapshot.getValue(User.class);
                    int cantidad_publicaciones = usuario.Cantidad_publicaciones  - 1;
                    databaseReference.child("Users").child(UID).child("Cantidad_publicaciones").setValue(cantidad_publicaciones);
                    Publicacion publicacion = usuario.Publicaciones.get(id_publicacion);
                    for(int i = 1;i<=publicacion.Cantidad_fotos;i++){
                        int posicion = i;
                        StorageReference path = Firebase_Variables.storage.child("Fotos_Publicaciones").child(UID).child(id_publicacion).child("foto_"+i+".png");
                        path.delete().addOnCompleteListener(task -> {
                            if(posicion==publicacion.Cantidad_fotos){
                                databaseReference.child("Users").child(UID).child("Publicaciones").child(id_publicacion).removeValue();
                                dialog_2.dismiss();
                                Intent main_intent = new Intent(DetallePublicacionActivity.this,MainActivity.class);
                                startActivity(main_intent);
                                Toast.makeText(getApplicationContext(),"Publicación eliminada con Éxito",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(),"No se pudo Borrar Hubo un error intentelo más tarde",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
    }

    private void Aprobar() {
        btn_aprobar.setOnClickListener(v->{
            databaseReference.child("Users").child(UID).child("Publicaciones").child(id_publicacion).child("Estado").setValue(true);
            Toast.makeText(getApplicationContext(),"Aprobado",Toast.LENGTH_LONG).show();
            Intent main_intent = new Intent(DetallePublicacionActivity.this,MainActivity.class);
            startActivity(main_intent);
        });
    }

    private void GetData() {
        databaseReference.child("Users").child(UID).child("Publicaciones").child(id_publicacion).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Publicacion publicacion = dataSnapshot.getValue(Publicacion.class);
                int cantidad_fotos = publicacion.Cantidad_fotos;
                descripcion.setText("Descripción :"+publicacion.Descripcion);
                fecha_publicacion.setText("Fecha de Publicación: "+publicacion.Fecha_creacion);
                for(int i =1 ;i<=cantidad_fotos;i++) fotos.add(i);
                imagenesAdapter = new ImagenesAdapter(UID,id_publicacion,getApplicationContext(),fotos);
                lista_imagenes.setAdapter(imagenesAdapter);
                indicador.setupWithViewPager(lista_imagenes);
                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"No se pudo obtener la información",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Regresar() {
        btn_regresar.setOnClickListener(v->{
            Intent publicaciones_intent = new Intent(DetallePublicacionActivity.this,ListaPublicacionesActivity.class);
            startActivity(publicaciones_intent);
        });
    }

    private void InstanciarViews() {
        preferences = getSharedPreferences("datos",MODE_PRIVATE);
        id_publicacion = preferences.getString("Id_publicacion","");
        UID = preferences.getString("UID","");
        btn_regresar = findViewById(R.id.btn_regresar_menu);
        descripcion = findViewById(R.id.descripcion_publicacion);
        fecha_publicacion = findViewById(R.id.fecha_publicacion);
        lista_imagenes = findViewById(R.id.pager_fotos);
        fotos = new ArrayList<>();
        indicador = findViewById(R.id.indicator_imagenes);
        btn_aprobar = findViewById(R.id.btn_aprobar);
        btn_eliminar= findViewById(R.id.btn_eliminar);
    }
}
