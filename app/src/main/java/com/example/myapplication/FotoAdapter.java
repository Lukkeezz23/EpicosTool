package com.example.myapplication;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class FotoAdapter extends RecyclerView.Adapter<FotoAdapter.FotoViewHolder> {
    private final List<Uri> seznamFotek;

    public FotoAdapter(List<Uri> seznamFotek) {
        this.seznamFotek = seznamFotek;
    }

    @NonNull
    @Override
    public FotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_foto, parent, false);
        return new FotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FotoViewHolder holder, int position) {
        Uri fotoUri = seznamFotek.get(position);
        Glide.with(holder.itemView.getContext()).load(fotoUri).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return seznamFotek.size();
    }

    static class FotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public FotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgFoto);
        }
    }
}
