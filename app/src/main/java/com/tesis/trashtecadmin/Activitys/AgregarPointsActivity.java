package com.tesis.trashtecadmin.Activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.tesis.trashtecadmin.Database.Firebase;
import com.tesis.trashtecadmin.Helper.Constantes;
import com.tesis.trashtecadmin.Models.Recojo;
import com.tesis.trashtecadmin.Models.User;
import com.tesis.trashtecadmin.Models.UserAdmin;
import com.tesis.trashtecadmin.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;

public class AgregarPointsActivity extends AppCompatActivity {

    User user;
    UserAdmin userAdmin;
    SharedPreferences preferences;
    MaterialSpinner spinner_points;
    TextView direccion;
    TextView usuario;
    Button cerrar_sesion;
    Constantes constantes;
    TextView Fecha;
    String Id_publicacion;
    TextView Centro;
    Firebase firebase;
    TextInputLayout descripcion;
    Button agregar_points;
    boolean select_points;
    Button regresar;
    int cantidad_points_selected;
    List<Integer> lista_points = new ArrayList<>();
    ArrayAdapter<Integer> points_adapter;
    String format = "yyyy-MM-dd";
    SimpleDateFormat objSDF = new SimpleDateFormat(format);
    Date fecha_actual = new Date();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_points);
        InstanciarViews();
        SelectData();
        Regresar();
        AgregarPoints();
        CerrarSesion();
        if(Id_publicacion.isEmpty())
            Toast.makeText(getApplicationContext(),"No ha seleccionado Publicacion",Toast.LENGTH_SHORT).show();
    }
    private void CerrarSesion() {
        cerrar_sesion.setOnClickListener(v->{
            SharedPreferences preferences = this.getSharedPreferences("datos", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("logeado",false);
            editor.apply();
            Intent login_intent = new Intent(this,LoginActivity.class);
            startActivity(login_intent);
        });
    }
    private void AgregarPoints() {
        agregar_points.setOnClickListener(v->{
            if(select_points){
                if(descripcion.getEditText().getText().toString().isEmpty())
                    Toast.makeText(getApplicationContext(),"Añada una descripción  por favor",Toast.LENGTH_SHORT).show();
                else{
                    ProgressDialog dialog = ProgressDialog.show(AgregarPointsActivity.this,"Registrando","Cargando...",false,false);
                    Recojo recojo = GetRecojo();
                    firebase = new Firebase(getApplicationContext(),recojo,this);
                    firebase.GuardarRecojo(dialog,Id_publicacion);
                }
            }else
                Toast.makeText(getApplicationContext(),"Asigne Puntos por favor",Toast.LENGTH_SHORT).show();
        });
    }

    private Recojo GetRecojo() {
        return new Recojo(user.UID,objSDF.format(fecha_actual),userAdmin.Centro,userAdmin.direccion,user.Name,descripcion.getEditText().getText().toString(),
                cantidad_points_selected);
    }

    private void Regresar() {
        regresar.setOnClickListener(v->{
            Intent main_intent = new Intent(this,MainActivity.class);
            startActivity(main_intent);
        });
    }

    private void SelectData() {

        Fecha.setText("Fecha: "+objSDF.format(fecha_actual));
        usuario.setText("Usuario :" +user.Name);
        if(!userAdmin.direccion.isEmpty())
            direccion.setText("Dirección: "+userAdmin.direccion);
        else
            direccion.setVisibility(View.GONE);
        if(!userAdmin.Centro.isEmpty())
            Centro.setText("Centro de Acopio: "+userAdmin.Centro);
        else
            Centro.setVisibility(View.GONE);
        points_adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,lista_points);
        points_adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner_points.setAdapter(points_adapter);
        spinner_points.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=-1) {
                    select_points = true;
                    cantidad_points_selected = lista_points.get(position);
                }else {
                    select_points = false;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void InstanciarViews() {
        user = new User();
        userAdmin = new UserAdmin();
        constantes = new Constantes();
        preferences = this.getSharedPreferences("datos",MODE_PRIVATE);
        userAdmin.Centro = preferences.getString("centro","");
        userAdmin.direccion = preferences.getString("direccion","");
        user.UID = preferences.getString("UID","");
        user.Name = preferences.getString("Name","");
        Id_publicacion = preferences.getString("Id_publicacion","");
        spinner_points = findViewById(R.id.spinner_points);
        direccion = findViewById(R.id.direccion);
        usuario = findViewById(R.id.usuario);
        Fecha = findViewById(R.id.fecha_recojo);
        Centro = findViewById(R.id.centro);
        cerrar_sesion = findViewById(R.id.btn_cerrar_sesion_add);
        agregar_points = findViewById(R.id.btn_asignar_puntos);
        regresar = findViewById(R.id.btn_regresar);
        descripcion = findViewById(R.id.descripcion);
        for(int i = 0; i<constantes.getPoints().length;i++){
            lista_points.add(constantes.getPoints()[i]);
        }

    }
}
