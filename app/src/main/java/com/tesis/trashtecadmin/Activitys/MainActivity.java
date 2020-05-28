package com.tesis.trashtecadmin.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tesis.trashtecadmin.Adapter.UsuarioAdapter;
import com.tesis.trashtecadmin.Helper.Constantes;
import com.tesis.trashtecadmin.Helper.Firebase_Variables;
import com.tesis.trashtecadmin.Models.Descuentos;
import com.tesis.trashtecadmin.Models.User;
import com.tesis.trashtecadmin.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText nombrea_apellidos;
    private ImageView btn_buscar;
    private ListView lista_usuarios;
    private LinearLayout opcioens_descuento;
    private UsuarioAdapter adapter;
    private List<User> usuarios;
    private boolean primera_vez = true;
    private boolean Guardar;
    private String  Id_Empresa;
    private Button btn_cerrar_sesion;
    private SharedPreferences preferences;
    private boolean isAdmin;
    private String tipo_empresa;
    private ProgressDialog dialog;
    private Button btn_agregar_descuento;
    private DatabaseReference databaseReference = Firebase_Variables.databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InstanciarViews();
        ClickBuscar();
        dialog = ProgressDialog.show(MainActivity.this,"Usuario","Buscando",false,false);
        GetData("", dialog);
        ClickUsuario();
        CerrarSesion();
        ClickAgregarDescuento();
        //Toast.makeText(getApplicationContext(),"Tipo: "+tipo_empresa,Toast.LENGTH_SHORT).show();
    }

    private void ClickAgregarDescuento() {
        btn_agregar_descuento.setOnClickListener(v->{
                Intent agregar_descuento_intent = new Intent(MainActivity.this,AgregarDescuentoActivity.class);
                startActivity(agregar_descuento_intent);
        });
    }

    private void CerrarSesion() {
        btn_cerrar_sesion.setOnClickListener(v->{
            SharedPreferences preferences = this.getSharedPreferences("datos",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("logeado",false);
            editor.apply();
            Intent login_intent = new Intent(this,LoginActivity.class);
            startActivity(login_intent);
        });
    }

    private void ClickUsuario() {
        lista_usuarios.setOnItemClickListener((parent, view, position, id) -> {
            Context context = new ContextThemeWrapper(MainActivity.this,R.style.Theme_MaterialComponents);
            MaterialAlertDialogBuilder dialog_material = new MaterialAlertDialogBuilder(context);
            dialog_material.setTitle("Escoger Opcion");
            dialog_material.setIcon(R.drawable.icon_basura);
            dialog_material.setMessage("Escoga una Opcion: ");
            User  usuario = usuarios.get(position);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("UID",usuario.UID);
            editor.putString("Name",usuario.Name);
            editor.apply();
            if(tipo_empresa.equals(Constantes.Empresas_Descuentos)) {
                dialog_material.setPositiveButton("Mis Descuentos", (dialog, which) -> {
                    Intent publicaciones_intent = new Intent(this,ListaDescuentosActivity.class);
                    startActivity(publicaciones_intent);
                });
            }else {
                if (isAdmin) {
                    dialog_material.setPositiveButton("Aprobar Publicaciones", (dialog, which) -> {
                        Intent publicaciones_intent = new Intent(this, ListaPublicacionesActivity.class);
                        editor.putBoolean("Aprobar", true);
                        editor.apply();
                        startActivity(publicaciones_intent);
                    });
                } else {
                    dialog_material.setPositiveButton("Asignar Puntos ", (dialog, which) -> {
                        Intent agregar_points = new Intent(MainActivity.this, AgregarPointsActivity.class);
                        startActivity(agregar_points);
                    });
                }
                dialog_material.setNegativeButton("Ver Publicaciones", (dialog, which) -> {
                    Intent publicaciones_intent = new Intent(this, ListaPublicacionesActivity.class);
                    editor.putBoolean("Aprobar", false);
                    editor.apply();
                    startActivity(publicaciones_intent);
                });
            }
            dialog_material.setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss());
            dialog_material.show();
        });
    }

    private void ClickBuscar() {
        primera_vez = false;
        btn_buscar.setOnClickListener(v -> {
            String nombre = nombrea_apellidos.getText().toString().trim();
            ProgressDialog dialog = ProgressDialog.show(MainActivity.this,"Usuario","Buscando",false,false);
            if(!nombre.isEmpty()){
                usuarios.clear();
                adapter.notifyDataSetChanged();
                GetData(nombre,dialog);
            }else {
                Toast.makeText(getApplicationContext(), "Ingrese un nombre", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void GetData(final String nombre,ProgressDialog dialog) {
        databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean encontro = false;
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    User user = data.getValue(User.class);
                    if(user.Name.toLowerCase().contains(nombre.toLowerCase())) {
                        usuarios.add(user);
                        encontro = true;
                    }else if(primera_vez) {
                        usuarios.add(user);
                    }
                }
                dialog.dismiss();
                if(!primera_vez) {
                    if (encontro) {
                        adapter = new UsuarioAdapter(getApplicationContext(), R.layout.item_lista_usuarios, usuarios);
                        lista_usuarios.setAdapter(adapter);
                    } else {
                        Toast.makeText(getApplicationContext(), "No se encontro el Usuario", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    adapter = new UsuarioAdapter(getApplicationContext(), R.layout.item_lista_usuarios, usuarios);
                    lista_usuarios.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"No se pudo Obtener la Información",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void InstanciarViews() {
        nombrea_apellidos = findViewById(R.id.nombres);
        btn_buscar = findViewById(R.id.btn_buscar);
        lista_usuarios = findViewById(R.id.lista_usuarios_encontrados);
        usuarios = new ArrayList<>();
        btn_cerrar_sesion = findViewById(R.id.btn_cerrar_sesion);
        Guardar = getIntent().getBooleanExtra("Guardar",false);
        opcioens_descuento = findViewById(R.id.opciones_empresa_descuento);
        preferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
        if(preferences.getString("user_admin","").equals("Admin"))
            isAdmin = true;
        tipo_empresa = preferences.getString("tipo_empresa","Empresas");
        if(tipo_empresa.equals(Constantes.Empresas_Descuentos)) {
            opcioens_descuento.setVisibility(View.VISIBLE);
            Id_Empresa = preferences.getString("Id_Empresa","");
            if(!Guardar)
                EvaluarNotificaciones();
        }
        btn_agregar_descuento = findViewById(R.id.agregar_descuento);
    }

    private void EvaluarNotificaciones() {
        databaseReference.child("Descuentos_Empresas").child(Id_Empresa).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean encontro_descuentos = false;
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    Descuentos descuento = data.getValue(Descuentos.class);
                    if(descuento.vista&&!descuento.notificacion){
                        encontro_descuentos = true;
                        databaseReference.child("Descuentos_Empresas").child(Id_Empresa).child(descuento.Id).child("notificacion").setValue(true);
                    }
                }
                if(encontro_descuentos)
                    Toast.makeText(getApplicationContext(),"Se aprobaron su Descuentos Recientemente Agregados",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("Info","Algo paso");
            }
        });
    }
}
