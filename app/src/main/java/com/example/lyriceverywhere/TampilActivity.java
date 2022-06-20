package com.example.lyriceverywhere;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class TampilActivity extends AppCompatActivity {
    //deklarasi variabel imageview
    private ImageView imgLagu;
    //deklarasi variabel tv TextView
    private TextView tvJudul, tvArtist, tvAlbum, tvLirik;
    //deklarasi variabel String
    private String linkLagu;

    //muntuk menampilkan hasil simpan dari inputactivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tampil);

        //deklarasi untuk menerima data
        imgLagu = findViewById(R.id.gmbr_lagu);
        tvJudul = findViewById(R.id.tv_judul);
        tvArtist = findViewById(R.id.tv_artist);
        tvAlbum = findViewById(R.id.tv_album);
        tvLirik = findViewById(R.id.tv_lirik_lagu);

        Intent terimaData = getIntent();
        //untuk nerima data dari judul
        tvJudul.setText(terimaData.getStringExtra("JUDUL"));
        //untuk nerima data dari artis
        tvArtist.setText(terimaData.getStringExtra("ARTIST"));
        //untuk nerima data dari album
        tvAlbum.setText(terimaData.getStringExtra("ALBUM"));
        //untuk nerima data dari lirik
        tvLirik.setText(terimaData.getStringExtra("LIRIK_LAGU"));
        //untuk nerima data dari gambar
        String imgLocation = terimaData.getStringExtra("GAMBAR");
        try {
            File file = new File(imgLocation);
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            imgLagu.setImageBitmap(bitmap);
            imgLagu.setContentDescription(imgLocation);
        } catch (FileNotFoundException er) {
            er.printStackTrace();
            Toast.makeText(this, "Gagal mengambil gambar dari media penyimpanan", Toast.LENGTH_SHORT).show();
        }
        //untuk nerima data dari link
        linkLagu = terimaData.getStringExtra("LINK");
    }
    //Menghubungkan tombol bagikan link
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tampil_share, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //untuk menampilkan link agar bisa di share
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_bagikan) {
            Intent bagikanLagu = new Intent(Intent.ACTION_SEND);
            bagikanLagu.putExtra(Intent.EXTRA_SUBJECT, tvJudul.getText().toString());
            bagikanLagu.putExtra(Intent.EXTRA_TEXT, linkLagu);
            //menentukan tipe share link (Tipe Text)
            bagikanLagu.setType("text/plain");
            startActivity(Intent.createChooser(bagikanLagu, "Membagikan Lirik Lagu"));
        }
        return super.onOptionsItemSelected(item);
    }

}
