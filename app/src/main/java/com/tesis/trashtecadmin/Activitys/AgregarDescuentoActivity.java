package com.tesis.trashtecadmin.Activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.tesis.trashtecadmin.Database.Firebase;
import com.tesis.trashtecadmin.Helper.Constantes;
import com.tesis.trashtecadmin.Models.Descuentos;
import com.tesis.trashtecadmin.R;
import com.tesis.trashtecadmin.Validation.Validation;

import java.security.IdentityScope;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;

public class AgregarDescuentoActivity extends AppCompatActivity {

    MaterialSpinner spinner_points;
    Constantes constantes;
    List<Integer> lista_points = new ArrayList<>();
    ArrayAdapter<Integer> points_adapter;
    Button btn_regresar;
    Button btn_agregar_descuento;
    String Id_empresa;
    TextInputLayout titulo;
    TextInputLayout descripcion;
    SharedPreferences preferences;
    boolean select_points;
    TextInputLayout cantidad_descuento;
    int cantidad_points_selected;
    //String format = "yyyy-MM-dd";
    //SimpleDateFormat objSDF = new SimpleDateFormat(format);
    Date fecha_actual = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_descuento);
        InstanciarViews();
        ClickRegresar();
        ClickAgregarDescuento();
        ClickCantidadPuntos();
    }

    private void ClickCantidadPuntos() {
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

    private void ClickAgregarDescuento() {
        btn_agregar_descuento.setOnClickListener(v->{
            ProgressDialog agregando = ProgressDialog.show(AgregarDescuentoActivity.this,"Descuento","Cargando..",false,false);
            Validation validation = new Validation(getApplicationContext(),AgregarDescuentoActivity.this,titulo,descripcion, cantidad_descuento);
           if(select_points) {
               if (validation.IsValidoDescuento()) {
                   Firebase firebase = new Firebase(getApplicationContext(),getDescuento(),AgregarDescuentoActivity.this);
                   firebase.GuardarDescuento(agregando,Id_empresa);
               }else
                   agregando.dismiss();
           }else {
               Toast.makeText(getApplicationContext(), "Seleccione cantidad de puntos a asignar al descuento", Toast.LENGTH_SHORT).show();
               agregando.dismiss();
           }
        });
    }

    private Descuentos getDescuento() {
        return new Descuentos(descripcion.getEditText().getText().toString(),"",titulo.getEditText().getText().toString()
        ,"",Integer.parseInt(cantidad_descuento.getEditText().getText().toString()),cantidad_points_selected,fecha_actual.toString(),false,false);
    }

    private void ClickRegresar() {
        btn_regresar.setOnClickListener(v->{
            Intent main_intent = new Intent(AgregarDescuentoActivity.this,MainActivity.class);
            startActivity(main_intent);
        });
    }


    private void InstanciarViews() {
        constantes = new Constantes();
        preferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
        Id_empresa = preferences.getString("Id_Empresa","");
        spinner_points = findViewById(R.id.spinner_points_descuento);
        for(int i = 0; i<constantes.getPoints().length;i++){
            lista_points.add(constantes.getPoints()[i]);
        }
        points_adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,lista_points);
        points_adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner_points.setAdapter(points_adapter);
        btn_regresar = findViewById(R.id.btn_regresar_descuento);
        btn_agregar_descuento = findViewById(R.id.btn_agregar_descuento);
        titulo = findViewById(R.id.Titulo);
        descripcion = findViewById(R.id.descripcion_descuento);
        cantidad_descuento = findViewById(R.id.cantidad_descuento_puntos);
    }
}
