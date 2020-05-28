package com.tesis.trashtecadmin.Database;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tesis.trashtecadmin.Activitys.AgregarDescuentoActivity;
import com.tesis.trashtecadmin.Activitys.AgregarPointsActivity;
import com.tesis.trashtecadmin.Activitys.MainActivity;
import com.tesis.trashtecadmin.Helper.Firebase_Variables;
import com.tesis.trashtecadmin.Models.Descuentos;
import com.tesis.trashtecadmin.Models.Recojo;

public class Firebase {

    private Context context;
    private AgregarPointsActivity activity;
    private Recojo recojo;
    private Descuentos descuento;
    private AgregarDescuentoActivity agregarDescuentoActivity;
    private DatabaseReference databaseReference = Firebase_Variables.databaseReference;
    public Firebase(Context context,Recojo recojo, AgregarPointsActivity activity){
        this.context = context;
        this.recojo = recojo;
        this.activity = activity;
    }
    public Firebase(Context context, Descuentos descuento, AgregarDescuentoActivity agregarDescuentoActivity){
        this.agregarDescuentoActivity = agregarDescuentoActivity;
        this.context = context;
        this.descuento = descuento;
    }
    public void GuardarRecojo(ProgressDialog dialog,String Id_publicacion){
        databaseReference.child("Users").child(recojo.UID).child("cantidad_recojos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int cantidad_recojos = dataSnapshot.getValue(Integer.class) + 1;

                databaseReference.child("Users").child(recojo.UID).child("Cantidad_points").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                        int cantidad_points = dataSnapshot2.getValue(Integer.class);
                        cantidad_points+=recojo.cantidad_points;
                        databaseReference.child("Recojos_Usuario").child(recojo.UID).push().setValue(recojo);
                        if(!Id_publicacion.isEmpty())
                            databaseReference.child("Users").child(recojo.UID).child("Publicaciones").child(Id_publicacion).child("Estado_Vista").setValue(false);
                        databaseReference.child("Users").child(recojo.UID).child("cantidad_recojos").setValue(cantidad_recojos);
                        databaseReference.child("Users").child(recojo.UID).child("Cantidad_points").setValue(cantidad_points);
                        Intent main_intent = new Intent(activity, MainActivity.class);
                        activity.startActivity(main_intent);
                        activity.finish();
                        Toast.makeText(context,"Puntos Añadidos",Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(context,"No se pudo obtener la informacion",Toast.LENGTH_SHORT).show();
                    }
                });
                }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context,"No se pudo obtener la informacion",Toast.LENGTH_SHORT).show();
            }
        });
        dialog.dismiss();
    }
    public void GuardarDescuento(ProgressDialog dialog,String Id_Empresa){
        databaseReference.child("Descuentos_Empresas").child(Id_Empresa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int cantidad = (int) dataSnapshot.getChildrenCount();
                cantidad+=1;
                descuento.Id = "Id_"+cantidad;
                databaseReference.child("Descuentos_Empresas").child(Id_Empresa).child("Id_"+cantidad).setValue(descuento);
                Toast.makeText(context,"Espere la aprobación del Descuento tarda algo de un día",Toast.LENGTH_LONG).show();
                dialog.dismiss();
                IrMainActivity();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context,"No se pudo obtener la informacion",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void IrMainActivity() {
        Intent main_intent = new Intent(agregarDescuentoActivity,MainActivity.class);
        main_intent.putExtra("Guardar",true);
        agregarDescuentoActivity.startActivity(main_intent);
    }
}
