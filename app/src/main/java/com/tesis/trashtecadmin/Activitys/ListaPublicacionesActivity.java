package com.tesis.trashtecadmin.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tesis.trashtecadmin.Adapter.PublicacionAdapter;
import com.tesis.trashtecadmin.Helper.Firebase_Variables;
import com.tesis.trashtecadmin.Models.Publicacion;
import com.tesis.trashtecadmin.R;

import java.util.ArrayList;
import java.util.List;

public class ListaPublicacionesActivity extends AppCompatActivity {

    String UID;
    DatabaseReference databaseReference = Firebase_Variables.databaseReference;
    ListView lista_publicaciones;
    private SharedPreferences.Editor editor;
    private boolean Aprobar;
    SharedPreferences preferences;
    ProgressDialog dialog;
    List<Publicacion> publicacions = new ArrayList<>();
    PublicacionAdapter adapter;
    Button btn_regresar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_publicaciones);
        InstanciarViews();
        SetDataPublicaciones();
        Regresar();
        SelectLista();
    }

    private void SelectLista() {
        lista_publicaciones.setOnItemClickListener((parent, view, position, id) -> {
            Intent agregar_intent;
            editor = preferences.edit();
            editor.putString("Id_publicacion",publicacions.get(position).Id);
            editor.apply();
            if(Aprobar)
                agregar_intent = new Intent(ListaPublicacionesActivity.this,DetallePublicacionActivity.class);
            else
                agregar_intent= new Intent(ListaPublicacionesActivity.this,AgregarPointsActivity.class);
            startActivity(agregar_intent);
        });
    }

    private void Regresar() {
        btn_regresar.setOnClickListener(v->{
            Intent main_intent = new Intent(ListaPublicacionesActivity.this,MainActivity.class);
            startActivity(main_intent);
            finish();
        });
    }

    private void SetDataPublicaciones() {
        dialog = ProgressDialog.show(ListaPublicacionesActivity.this,"Publicaciones","Cargando...",false,false);
        databaseReference.child("Users").child(UID).child("Publicaciones").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()>0){
                    for(DataSnapshot data:dataSnapshot.getChildren()){
                        Publicacion publicacion = data.getValue(Publicacion.class);
                        if(!Aprobar) {
                            if (publicacion.Estado && publicacion.Estado_Vista)
                                publicacions.add(publicacion);
                        }else{
                            if(!publicacion.Estado)
                                publicacions.add(publicacion);
                        }
                    }
                    adapter = new PublicacionAdapter(UID,ListaPublicacionesActivity.this,R.layout.item_publicacion,publicacions);
                    lista_publicaciones.setAdapter(adapter);
                    dialog.dismiss();
                }else
                    Toast.makeText(getApplicationContext(),"No Tiene ninguna publciación para asignar puntos",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"No se pudieron obtener las publicaciones",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void InstanciarViews() {
        lista_publicaciones = findViewById(R.id.lista_publicaciones);
        preferences = this.getSharedPreferences("datos",MODE_PRIVATE);
        UID = preferences.getString("UID","");
        btn_regresar = findViewById(R.id.btn_regresar_publicaciones);
        Aprobar = preferences.getBoolean("Aprobar",false);
    }
}
