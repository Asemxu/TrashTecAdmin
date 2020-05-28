package com.tesis.trashtecadmin.Validation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tesis.trashtecadmin.Activitys.AgregarDescuentoActivity;
import com.tesis.trashtecadmin.Helper.Constantes;
import com.tesis.trashtecadmin.Helper.Firebase_Variables;
import com.tesis.trashtecadmin.Activitys.LoginActivity;
import com.tesis.trashtecadmin.Activitys.MainActivity;
import com.tesis.trashtecadmin.Models.UserAdmin;

public class Validation {
    private TextInputLayout username;
    private TextInputLayout pass;
    private TextInputLayout titulo;
    private TextInputLayout descripcion;
    private TextInputLayout cantidad_descuento;
    private LoginActivity loginActivity;
    private AgregarDescuentoActivity agregarDescuentoActivity;
    private String tipo_empresa;
    private Context context;
    private DatabaseReference databaseReference = Firebase_Variables.databaseReference;
    private int cantidad_errores = 0;
    public Validation(String tipo_empresa, Context context,LoginActivity loginActivity,TextInputLayout username,TextInputLayout pass){
        this.tipo_empresa = tipo_empresa;
        this.username = username;
        this.loginActivity = loginActivity;
        this.context = context;
        this.pass = pass;
    }
    public Validation(Context context, AgregarDescuentoActivity agregarDescuentoActivity,TextInputLayout titulo,
                      TextInputLayout descripcion,TextInputLayout cantidad_descuento){
        this.context = context;
        this.agregarDescuentoActivity = agregarDescuentoActivity;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.cantidad_descuento = cantidad_descuento;
    }
    public boolean IsValidoDescuento(){
        ValidarTitulo(titulo.getEditText().getText().toString());
        ValidarDescripcion(descripcion.getEditText().getText().toString());
        ValidarDescuento(cantidad_descuento.getEditText().getText().toString());
        return cantidad_errores == 0;
    }

    private void ValidarDescuento(String cantidad_descuento) {
        if(cantidad_descuento.isEmpty()) {
            cantidad_errores++;
            this.cantidad_descuento.setError("La cantidad de descuento es obligatoria");
        }else
            this.cantidad_descuento.setError(null);
    }

    private void ValidarDescripcion(String descripcion) {
        if(descripcion.isEmpty()){
            cantidad_errores++;
            this.descripcion.setError("La descripción no puede estar vacia");
        }else
            this.descripcion.setError(null);
    }

    private void ValidarTitulo(String titulo) {
        if(titulo.isEmpty()|| titulo.length()>50){
            cantidad_errores++;
            this.titulo.setError("El Titulo no puede estar vacio ni puede contener mas de 50 caracteres");
        }else
            this.titulo.setError(null);
    }

    public  boolean IsValido(){
        ValidarUsername(username.getEditText().getText().toString());
        ValidarContra(pass.getEditText().getText().toString());
        return cantidad_errores == 0;
    }

    private void ValidarContra(String pass) {
        if(IsEmptyorWhiteSpace(pass))
            this.pass.setError("La contraseña es obligatorio y no debe contener espacios");
        else
            this.pass.setError(null);
    }

    private boolean IsEmptyorWhiteSpace(String data) {
        if(data.isEmpty()) {
            cantidad_errores++;
            return true;
        }else{
            boolean hay_error = false;
            for(int i = 0; i<data.length();i++){
                char c = data.charAt(i);
                if(c==' ') {
                    cantidad_errores++;
                    hay_error = true;
                    break;
                }
            }
            return hay_error;
        }
    }

    private void ValidarUsername(String username) {
        if(IsEmptyorWhiteSpace(username))
            this.username.setError("Username es Obligatorio y no debe contener Espacios");
        else
            this.username.setError(null);
    }

    public void ValidarUsernameFirebase(final ProgressDialog dialog){
        databaseReference.child(tipo_empresa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username.setError(null);
                pass.setError(null);
                UserAdmin usuario = null;
                boolean encontradoo_ususario = false,valido_contraseña = false;
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    UserAdmin userAdmin = data.getValue(UserAdmin.class);
                    if(username.getEditText().getText().toString().equals(userAdmin.Usuario)){
                        encontradoo_ususario = true;
                        if(pass.getEditText().getText().toString().equals(userAdmin.Contraseña)){
                            valido_contraseña = true;
                            usuario = userAdmin;
                            break;
                        }
                    }
                }
                dialog.dismiss();
                if(!encontradoo_ususario)
                    username.setError("El Usuario " +username.getEditText().getText().toString() +" no esta Registrado");
                else
                    if (!valido_contraseña)
                        pass.setError("La contraseña no coincide con el Usuario Admin "+username.getEditText().getText().toString() );
                    else {
                        SharedPreferences preferences = loginActivity.getSharedPreferences("datos",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("logeado",true);
                        if(tipo_empresa.equals(Constantes.Empresas)){
                            if(username.getEditText().getText().toString().equals(Constantes.UsuarioAdmin)&&
                                pass.getEditText().getText().toString().equals(Constantes.ContraseñaAdmin)){
                                editor.putString("user_admin","Admin");
                                editor.putString("centro", "");
                                editor.putString("direccion","");
                            }else {
                                editor.putString("user_admin","NoAdmin");
                                editor.putString("centro", usuario.Centro);
                                editor.putString("direccion", usuario.direccion);
                            }
                        }
                        editor.putString("tipo_empresa",tipo_empresa);
                        editor.putString("Id_Empresa",usuario.Id);
                        editor.apply();
                        Intent mains_user_admin;
                        mains_user_admin = new Intent(loginActivity, MainActivity.class);
                        loginActivity.startActivity(mains_user_admin);
                        loginActivity.finish();
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context,"No se pudo Validar Intentelo de Nuevo",Toast.LENGTH_SHORT).show();
            }
        });
    }

}
