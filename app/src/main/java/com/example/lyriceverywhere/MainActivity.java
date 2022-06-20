package com.example.lyriceverywhere;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.adapter.LaguAdapter;
import com.example.database.DatabaseHelper;
import com.example.database.Lagu;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Lagu> dataLagu = new ArrayList<>();
    //RecyclerView memudahkan untuk menampilkan kumpulan data dalam jumlah besar secara efisien
    private RecyclerView rvLagu;
    private LaguAdapter laguAdapter;
    private DatabaseHelper dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //inisiasi rv (recyclerview)
        rvLagu = findViewById(R.id.rv_tampil_lagu);
        //database handler
        dbHandler = new DatabaseHelper(this);
        tampilDataLagu();
    }

    //untuk menampilkan card lagu di halaman utama
    private void tampilDataLagu() {
        //mengambil lagu dari database
        dataLagu = dbHandler.getAllLagu();
        laguAdapter = new LaguAdapter(this, dataLagu);
        RecyclerView.LayoutManager layManager = new LinearLayoutManager(MainActivity.this);
        rvLagu.setLayoutManager(layManager);
        rvLagu.setAdapter(laguAdapter);
    }

    //Method ini digunakan untuk menampilkan daftar menu yang sudah kita buat
    //menu + tambah untuk berpindah ke inputActivity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tambah_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    //Menthod ini digunakan untuk menangani kejadian saat OptionMenu diklik
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.item_menu_tambah) {
            //jika di klik menu tambah + akan langsung ke input activity
            Intent bukaInputact = new Intent(getApplicationContext(), InputActivity.class);
            bukaInputact.putExtra("OPERASI", "insert");
            startActivity(bukaInputact);
        }
        return super.onOptionsItemSelected(item);
    }

    //activity dibuka kembali
    @Override
    protected void onResume() {
        super.onResume();
        //mengambil card lagu
        tampilDataLagu();
    }
}