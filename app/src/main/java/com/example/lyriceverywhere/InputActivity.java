package com.example.lyriceverywhere;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.database.DatabaseHelper;
import com.example.database.Lagu;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class InputActivity extends AppCompatActivity implements View.OnClickListener{

    //deklarasi EditText
    private EditText editNamaLagu, editNamaArtist, editAlbum, editLirikLagu,  editLink;
    //deklarasi imagevideo
    private ImageView ivLagu;
    //deklarasi databasehandler
    private DatabaseHelper dbHelper;
    //deklarasi EditText
    private boolean updateData = false;
    private int idLagu = 0;
    //deklarasi button dengan nama btnSimpan
    private Button btnSimpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        //deklarasi variabel dengan id
        editNamaLagu = findViewById(R.id.edit_judul);
        editNamaArtist = findViewById(R.id.edit_artist);
        editAlbum = findViewById(R.id.edit_album);
        editLirikLagu = findViewById(R.id.edit_lirik_lagu);
        editLink = findViewById(R.id.edit_link);
        ivLagu = findViewById(R.id.gmbr_lagu);
        btnSimpan = findViewById(R.id.btn_simpan);
        //bikin objek database
        dbHelper = new DatabaseHelper(this);
        ivLagu.setOnClickListener(this);
        btnSimpan.setOnClickListener(this);

        Intent terimaIntent = getIntent();
        Bundle data = terimaIntent.getExtras();
        //buat masukin data
        if (data.getString("OPERASI").equals("insert")) {
            updateData = false;
            //buat update data nya
        } else {
            updateData = true;
            idLagu = data.getInt("ID");
            editNamaLagu.setText(data.getString("JUDUL"));
            editNamaArtist.setText(data.getString("ARTIST"));
            editAlbum.setText(data.getString("ALBUM"));
            editLirikLagu.setText(data.getString("LIRIK_LAGU"));
            loadImageFromInternalStorage(data.getString("GAMBAR"));
            editLink.setText(data.getString("LINK"));
        }
    }

    //Untuk meng crop, memberikan aspek ratio pada gambar
    private void pickImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(3,3)
                .start(this);
    }

    //untuk bagian penginputan gambar, seperti crop image album
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                try {
                    Uri imageUri = result.getUri();
                    InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    String location = saveImageToInternalStorage(selectedImage, getApplicationContext());
                    loadImageFromInternalStorage(location);
                } catch (FileNotFoundException er) {
                    er.printStackTrace();
                    Toast.makeText(this, "Ada kesalahan nih dalam pemilihan gambar", Toast.LENGTH_SHORT).show();
                }
            }
            //peringatan gambar belum di dipilih
        } else {
            Toast.makeText(this, "Gambar belum dipilih nih!", Toast.LENGTH_SHORT).show();
        }
    }

    public static String saveImageToInternalStorage(Bitmap bitmap, Context ctx) {
        ContextWrapper ctxWrapper = new ContextWrapper(ctx);
        File file = ctxWrapper.getDir("images", MODE_PRIVATE);
        String uniqueID = UUID.randomUUID().toString();
        file = new File(file, "lagu-"+ uniqueID + ".jpg");
        try {
            OutputStream stream = null;
            stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.flush();
            stream.close();
        } catch (IOException er) {
            er.printStackTrace();
        }

        Uri savedImage = Uri.parse(file.getAbsolutePath());
        return savedImage.toString();
    }

    //menampilkan gambar di tampilan penginputan
    private void loadImageFromInternalStorage(String imageLocation) {
        try {
            File file = new File(imageLocation);
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            ivLagu.setImageBitmap(bitmap);
            ivLagu.setContentDescription(imageLocation);
            //pesan eror jika terjadi kesalahan
        } catch (FileNotFoundException er) {
            er.printStackTrace();
            Toast.makeText(this, "Gagal mengambil gambar dari media penyimpanan", Toast.LENGTH_SHORT).show();
        }
    }

    //untuk mengubah menu opsi dari peristiwa yang terjadi
    //selama siklus proses aktivitas
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.item_menu_hapus);
        if(updateData==true) {
            item.setEnabled(true);
            item.getIcon().setAlpha(255);
        } else {
            item.setEnabled(false);
            item.getIcon().setAlpha(130);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    //menyimpan data
    private void simpanData() {
        //variabel
        String judul, artist, gambar, album, lirikLagu, link;
        judul = editNamaLagu.getText().toString();
        artist = editNamaArtist.getText().toString();
        gambar = ivLagu.getContentDescription().toString();
        album = editAlbum.getText().toString();
        lirikLagu = editLirikLagu.getText().toString();
        link = editLink.getText().toString();

        //menyimpan di temporary
        Lagu tempLagu = new Lagu (idLagu, judul, artist, gambar, album, lirikLagu, link);

        //menyimpan lagu yang diedit
        if (updateData == true) {
            dbHelper.editLagu(tempLagu);
            Toast.makeText(this, "Data lagu diperbarui", Toast.LENGTH_SHORT).show();
        //menyimpan lagu yang baru ditambahkan
        } else {
            dbHelper.tambahLagu(tempLagu);
            Toast.makeText(this, "Data lagu ditambahkan", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    //menampilkan icon hapus (tempat sampah)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.item_menu_hapus) {
            hapusData();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //menghapus data dari database
    private void hapusData() {
        dbHelper.hapusLagu(idLagu);
        Toast.makeText(this, "Lirik dihapus", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        int idView = v.getId();

        if (idView == R.id.btn_simpan) {
            String namaLagu = editNamaLagu.getText().toString();
            String namaArtists = editNamaArtist.getText().toString();
            String namaAlbum = editAlbum.getText().toString();
            String lirikLagu = editLirikLagu.getText().toString();

            //peringatan tidak boleh kosong
            boolean isEmpty = false;
            if (TextUtils.isEmpty(namaLagu)){
                isEmpty = true;
                editNamaLagu.setError("Masukkan Judul Lagu");
                Toast.makeText(InputActivity.this, "Masukkan Judul Lagu", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(namaArtists)){
                isEmpty = true;
                editNamaArtist.setError("Masukkan Artists Lagu");
                Toast.makeText(InputActivity.this, "Masukkan Artist Lagu", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(namaAlbum)){
                isEmpty = true;
                editAlbum.setError("Masukkan Nama Album Lagu");
                Toast.makeText(InputActivity.this, "Masukkan Nama Album Lagu", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(lirikLagu)){
                isEmpty = true;
                editLirikLagu.setError("Masukkan Lirik Lagu");
                Toast.makeText(InputActivity.this, "Masukkan Lirik Lagu", Toast.LENGTH_SHORT).show();
            } else {
                simpanData();
            }
        } else if (idView == R.id.gmbr_lagu) {
            pickImage();
        }
    }
}
