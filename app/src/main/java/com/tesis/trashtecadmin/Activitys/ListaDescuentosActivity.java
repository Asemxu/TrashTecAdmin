package com.tesis.trashtecadmin.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.internal.constants.ListAppsActivityContract;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tesis.trashtecadmin.Adapter.DescuentoAdapter;
import com.tesis.trashtecadmin.Helper.Firebase_Variables;
import com.tesis.trashtecadmin.Models.Descuentos;
import com.tesis.trashtecadmin.R;

import java.util.ArrayList;
import java.util.List;

public class ListaDescuentosActivity extends AppCompatActivity {

    Button btn_regresar; 
    DatabaseReference databaseReference = Firebase_Variables.databaseReference;
    DescuentoAdapter adapter;
    ListView lista_descuentos;
    String UID;
    SharedPreferences preferences;
    private  List<Descuentos> mis_descuentos = new ArrayList<>();
    private List<String> id_misd_descuentos = new ArrayList<>();
    private List<String> keys_encontrados = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_descuentos);
        InstanciarViews();
        ClicRegresar();
        GetDescuentos();
    }

    private void ClicRegresar() {
        btn_regresar.setOnClickListener(v->{
            Intent main_intent = new Intent(ListaDescuentosActivity.this,MainActivity.class);
            startActivity(main_intent);
        });
    }

    private void GetDescuentos() {
        ProgressDialog dialog = ProgressDialog.show(ListaDescuentosActivity.this,"Mis Descuentos","Cargando...",false,false);
        databaseReference.child("Descuentos_Usuarios").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    List<String> keys_descuentos = new ArrayList<>();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        keys_descuentos.add(data.getKey());
                        String id_descuentos = data.getValue().toString();
                        id_misd_descuentos.add(id_descuentos);
                    }
                    if (id_misd_descuentos.size() > 0) {
                        databaseReference.child("Descuentos_Empresas").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                        Descuentos descuento = dataSnapshot2.getValue(Descuentos.class);
                                        int i = 0;
                                        for (String id_mi_descuento : id_misd_descuentos) {
                                            if (id_mi_descuento.equals(descuento.Id)) {
                                                mis_descuentos.add(descuento);
                                                keys_encontrados.add(keys_descuentos.get(i));
                                            }
                                            i++;
                                        }
                                    }
                                }
                                adapter = new DescuentoAdapter(UID, keys_encontrados, ListaDescuentosActivity.this, R.layout.item_descuento, mis_descuentos, ListaDescuentosActivity.this);
                                lista_descuentos.setAdapter(adapter);
                                dialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getApplicationContext(), "No se pudo Obtener la información", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                    }
                }else{
                    dialog.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"No se pudo Obtener la información",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

    }

    private void InstanciarViews() {
        btn_regresar = findViewById(R.id.btn_regresar_descuentos);
        preferences = getSharedPreferences("datos",MODE_PRIVATE);
        UID = preferences.getString("UID","");
        lista_descuentos = findViewById(R.id.lista_descuentos);
    }
}
