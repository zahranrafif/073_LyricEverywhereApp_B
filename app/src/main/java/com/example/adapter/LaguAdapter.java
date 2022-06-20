package com.example.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lyriceverywhere.InputActivity;
import com.example.database.Lagu;
import com.example.lyriceverywhere.R;
import com.example.lyriceverywhere.TampilActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class LaguAdapter extends RecyclerView.Adapter<LaguAdapter.LaguViewHolder> {
    //memberikan akses informasi atas application state
    private Context context;
    //array list
    private ArrayList<Lagu> dataLagu;

    public LaguAdapter(Context context, ArrayList<Lagu> dataLagu) {
        this.context = context;
        this.dataLagu = dataLagu;
    }

    //untuk manyambungkan list kartu lagu pada MainActivity
    @NonNull
    @Override
    public LaguViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //digunakan karena menambahkan layout tambahan pada ActivityMain
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_lagu, parent, false);
        return new LaguViewHolder(view);
    }

    //menghubungkan data yang ada dengan objek ViewHolder
    @Override
    public void onBindViewHolder(@NonNull LaguViewHolder holder, int position) {

        Lagu tempLagu = dataLagu.get(position);
        holder.idLagu = tempLagu.getIdLagu();
        holder.tvJudul.setText(tempLagu.getJudul());
        holder.tvArtist.setText(tempLagu.getArtist());
        holder.lirikLagu = tempLagu.getLirikLagu();
        holder.gambar = tempLagu.getGambar();
        holder.album = tempLagu.getAlbum();
        holder.link = tempLagu.getLink();

        //input gambar album
        try {
            File file = new File(holder.gambar);
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file)); //bitmap untuk gambar
            holder.imgLagu.setImageBitmap(bitmap);
            holder.imgLagu.setContentDescription(holder.gambar);
            //Gagal mengambil gambar
        } catch (FileNotFoundException er) {
            er.printStackTrace();
            Toast.makeText(context, "Gagal mengambil gambar dari penyimpanan hp", Toast.LENGTH_SHORT).show();
        }
    }

    //RecyclerView memanggil metode ini untuk mendapatkan ukuran set data
    @Override
    public int getItemCount() {
        return dataLagu.size();
    }

    public class LaguViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        //deklarasi variabel gambar album
        private ImageView imgLagu;
        //deklarasi variabel textview
        private TextView tvJudul, tvArtist;
        //deklarasi variabel idlagu
        private int idLagu;
        //deklarasi variabel String
        private String gambar, lirikLagu, album, link;

        //untuk menampilkan data di halaman utama/card lagu
        public LaguViewHolder(@NonNull View itemView) {
            super(itemView);
            imgLagu =  itemView.findViewById(R.id.gmbr_lagu);
            tvJudul = itemView.findViewById(R.id.tv_judul);
            tvArtist = itemView.findViewById(R.id.tv_artist);
            //untuk sekali tekan
            itemView.setOnClickListener(this);
            //untuk tekan & tahan lama
            itemView.setOnLongClickListener(this);
        }

        @Override
        public String toString() {
            return super.toString();
        }

        //klik untuk melihat lirik
        @Override
        public void onClick(View v) {
            // intent pindah ke TampilActivity
            Intent bukaLagu = new Intent(context, TampilActivity.class);
            bukaLagu.putExtra("ID", idLagu);
            bukaLagu.putExtra("JUDUL", tvJudul.getText().toString());
            bukaLagu.putExtra("ARTIST", tvArtist.getText().toString());
            bukaLagu.putExtra("GAMBAR", gambar);
            bukaLagu.putExtra("ALBUM", album);
            bukaLagu.putExtra("LIRIK_LAGU", lirikLagu);
            bukaLagu.putExtra("LINK", link);
            //membuka TampilActivity
            context.startActivity(bukaLagu);
        }

        //tekan & tahan lama untuk edit lirik
        @Override
        public boolean onLongClick(View v) {
            // intent pindah ke InputActivity
            Intent bukaInput = new Intent(context, InputActivity.class);
            bukaInput.putExtra("OPERASI", "update"); //operasi update
            bukaInput.putExtra("ID", idLagu);
            bukaInput.putExtra("JUDUL", tvJudul.getText().toString());
            bukaInput.putExtra("ARTIST", tvArtist.getText().toString());
            bukaInput.putExtra("GAMBAR", gambar);
            bukaInput.putExtra("ALBUM", album);
            bukaInput.putExtra("LIRIK_LAGU", lirikLagu);
            bukaInput.putExtra("LINK", link);
            //membuka InputActivity
            context.startActivity(bukaInput);
            return true;
        }
    }
}
