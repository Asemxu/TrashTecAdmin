package com.tesis.trashtecadmin.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.google.firebase.storage.StorageReference;
import com.tesis.trashtecadmin.Helper.Firebase_Variables;
import com.tesis.trashtecadmin.R;

import java.io.File;
import java.util.List;

public class ImagenesAdapter extends PagerAdapter {
    Context mContext ;
    List<Integer> fotos;
    String UID;
    String id_publicacion;
    public ImagenesAdapter(String UID,String id_publicacion,Context mContext, List<Integer> mListScreen) {
        this.mContext = mContext;
        this.fotos = mListScreen;
        this.UID = UID;
        this.id_publicacion = id_publicacion;
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutScreen = inflater.inflate(R.layout.imagen_publicacion,null);
        ImageView imagen_publicacion = layoutScreen.findViewById(R.id.imagen_publicacion);
        CargarImagen(id_publicacion,imagen_publicacion,fotos.get(position));
        container.addView(layoutScreen);
        return layoutScreen;
    }
    private void CargarImagen(String id,final ImageView foto_publicacion,int position) {
        StorageReference filePath = Firebase_Variables.GetReferenceImages(id,UID,position);
        try{
            final File file = File.createTempFile("image","png");
            filePath.getFile(file).addOnSuccessListener(taskSnapshot -> {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                foto_publicacion.setImageBitmap(bitmap);
            }).addOnFailureListener(e -> Toast.makeText(mContext,e.getMessage(),Toast.LENGTH_LONG).show());
        }catch (Exception ex){
            Log.i("Error", ex.getMessage());
        }
    }
    @Override
    public int getCount() {
        return fotos.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

}
