package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.api.services.drive.model.File;

import java.util.List;

class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {

    private List<File> fileList;
    private final OnFileClickListener listener;

    public interface OnFileClickListener {
        void onFileClick(String url);
    }

    public FileAdapter(List<File> fileList, OnFileClickListener listener) {
        this.fileList = fileList;
        this.listener = listener;
    }

    public void updateFiles(List<File> newFiles) {
        fileList = newFiles;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        File file = fileList.get(position);
        holder.bind(file);
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    class FileViewHolder extends RecyclerView.ViewHolder {
        private final TextView fileName;
        private final ImageView fileThumbnail;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.fileName);
            fileThumbnail = itemView.findViewById(R.id.fileThumbnail);
        }

        public void bind(File file) {
            fileName.setText(file.getName());

            // Načtení miniatury pomocí Glide (pokud existuje)
            String thumbnailUrl = file.getThumbnailLink();
            if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(thumbnailUrl)
                        .into(fileThumbnail);
            } else {
                fileThumbnail.setImageResource(R.drawable.file_ico); // Výchozí ikona
            }

            // Otevření souboru při kliknutí
            itemView.setOnClickListener(v -> listener.onFileClick(file.getWebViewLink()));
        }
    }
}
