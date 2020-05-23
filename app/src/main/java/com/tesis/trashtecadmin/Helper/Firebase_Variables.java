package com.tesis.trashtecadmin.Helper;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Firebase_Variables {
    public static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static final DatabaseReference databaseReference = database.getReference();
    public static final StorageReference storage= FirebaseStorage.getInstance().getReference();
    public static StorageReference GetReferenceImage(String id, String uid){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        return storage.getReferenceFromUrl("gs://trashtec-f143a.appspot.com/Fotos_Publicaciones/"+uid+"/").child(id).child("foto_1.png");
    }
    public static StorageReference GetReferenceImages(String id, String uid,int i){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        return storage.getReferenceFromUrl("gs://trashtec-f143a.appspot.com/Fotos_Publicaciones/"+uid+"/").child(id).child("foto_"+i+".png");
    }
}
