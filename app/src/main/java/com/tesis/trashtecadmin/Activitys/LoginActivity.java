package com.tesis.trashtecadmin.Activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.tesis.trashtecadmin.Helper.Constantes;
import com.tesis.trashtecadmin.R;
import com.tesis.trashtecadmin.Validation.Validation;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout contraseña;
    TextInputLayout username;
    Button btn_login;
    boolean logeado;
    ProgressDialog dialog;
    private String  tipo_login;
    SharedPreferences preferences;
    MaterialButtonToggleGroup btn_tipo_login;
    boolean selecciono_tipo;
    Validation validation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        InstanciarViews();
        ClickLogin();
        ClickTipoLogin();
        ValidarAutentificacion();
    }

    private void ClickTipoLogin() {
        btn_tipo_login.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            switch (checkedId){
                case R.id.btn_login_centro:
                    Toast.makeText(getApplicationContext(),"Escogio Logearese como Centro de Acopio",Toast.LENGTH_LONG).show();
                    tipo_login = Constantes.Empresas;
                    break;
                case R.id.btn_login_empresa:
                    Toast.makeText(getApplicationContext(),"Escogio Logearese como Empresas de descuento",Toast.LENGTH_LONG).show();
                    tipo_login = Constantes.Empresas_Descuentos;
                    break;
            }
            selecciono_tipo = true;
        });
    }

    private void ValidarAutentificacion() {
        if(logeado){
            Intent main_admin_intent = new Intent(this,MainActivity.class);
            startActivity(main_admin_intent);
        }
    }

    private void ClickLogin() {
        btn_login.setOnClickListener(v -> {
            if(selecciono_tipo) {
                validation = new Validation(tipo_login,getApplicationContext(), LoginActivity.this, username, contraseña);
                if (validation.IsValido()) {
                    dialog = ProgressDialog.show(LoginActivity.this, "Login", "Cargando..", false, false);
                    validation.ValidarUsernameFirebase(dialog);
                }
            }else
                Toast.makeText(getApplicationContext(),"Escoga un  Tipo de Login",Toast.LENGTH_SHORT).show();
        });
    }

    private void InstanciarViews() {
        contraseña = findViewById(R.id.contraseña);
        username = findViewById(R.id.username);
        btn_login = findViewById(R.id.btn_login);
        preferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
        logeado = preferences.getBoolean("logeado",false);
        btn_tipo_login = findViewById(R.id.btn_tipo_login);
    }
}
