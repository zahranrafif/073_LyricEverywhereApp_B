package com.example.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.database.Lagu;
import com.example.lyriceverywhere.InputActivity;
import com.example.lyriceverywhere.R;

import java.util.ArrayList;
//Database dengan Sqlite
public class DatabaseHelper extends SQLiteOpenHelper {

    private final static String TABLE_LAGU = "t_lagu";
    private final static String KEY_ID_LAGU = "ID_Lagu";
    private final static String KEY_JUDUL = "Judul";
    private final static String KEY_ARTIST = "Artist";
    private final static String KEY_GAMBAR = "Gambar";
    private final static String KEY_ALBUM = "Album";
    private final static String KEY_LIRIK_LAGU = "Lirik_Lagu";
    private final static String KEY_LINK = "Link";
    //memberikan akses informasi atas application state
    private Context context;

    //database helper
    public DatabaseHelper(Context ctx) {
        super(ctx, "db_music", null, 3);
        this.context = ctx;
    }

    //membuat tabel bernama create table lagu untuk penyimpanan
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_LAGU = "CREATE TABLE " + TABLE_LAGU
                + "(" + KEY_ID_LAGU + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_JUDUL + " TEXT, " + KEY_ARTIST + " TEXT, "
                + KEY_GAMBAR + " TEXT, " + KEY_ALBUM + " TEXT, "
                + KEY_LIRIK_LAGU + " TEXT, " + KEY_LINK + " TEXT);";

        //membuat table lagu
        db.execSQL(CREATE_TABLE_LAGU);
        inisialisasiLaguAwal(db);
        //membuat tabel users
        db.execSQL("create Table users(username TEXT primary key, password TEXT)");
    }

    //menghapus tabel data jika ada data yang sama/sudah ada
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_LAGU;
        db.execSQL(DROP_TABLE);
        onCreate(db);

        //db login
        db.execSQL("drop Table if exists users");
    }

    //database login menginput data
    public Boolean insertData (String username, String password) {
        //Membuat atau membuka database yang akan digunakan untuk membaca dan menulis.
        SQLiteDatabase db = getWritableDatabase();
        //Membuat kumpulan nilai kosong dengan ukuran awal default
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("password", password);
        long result = db.insert("users", null, cv);
        if (result == -1) return false;
        else
            return true;
    }

    //mengecek username saja di database (login)
    public Boolean checkusername (String username){
        //Membuat atau membuka database yang akan digunakan untuk membaca dan menulis.
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from users where username=?" , new String[] {username});
        if (cursor.getCount()>0)
            return true;
        else
            return false;
    }

    //mengecek username dan password didatabase (login)
    public Boolean checkusernamepassword (String username, String password){
        //Membuat atau membuka database yang akan digunakan untuk membaca dan menulis.
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from users where username = ? and password = ?", new String[] {username,password});
        if (cursor.getCount()>0)
            return true;
        else
            return false;
    }

    //menambah lagu
    public void tambahLagu(Lagu dataLagu) {
        //Membuat atau membuka database yang akan digunakan untuk membaca dan menulis.
        SQLiteDatabase db = getWritableDatabase();
        //Membuat kumpulan nilai kosong dengan ukuran awal default
        ContentValues cv = new ContentValues();
        cv.put(KEY_JUDUL, dataLagu.getJudul());
        cv.put(KEY_ARTIST, dataLagu.getArtist());
        cv.put(KEY_GAMBAR, dataLagu.getGambar());
        cv.put(KEY_ALBUM, dataLagu.getAlbum());
        cv.put(KEY_LIRIK_LAGU, dataLagu.getLirikLagu());
        cv.put(KEY_LINK, dataLagu.getLink());

        db.insert(TABLE_LAGU, null, cv);
        db.close();
    }

    //tambah lagu
    public void tambahLagu(Lagu dataLagu, SQLiteDatabase db) {
        //Membuat kumpulan nilai kosong dengan ukuran awal default
        ContentValues cv = new ContentValues();

        cv.put(KEY_JUDUL, dataLagu.getJudul());
        cv.put(KEY_ARTIST, dataLagu.getArtist());
        cv.put(KEY_GAMBAR, dataLagu.getGambar());
        cv.put(KEY_ALBUM, dataLagu.getAlbum());
        cv.put(KEY_LIRIK_LAGU, dataLagu.getLirikLagu());
        cv.put(KEY_LINK, dataLagu.getLink());

        db.insert(TABLE_LAGU, null, cv);
    }

    //edit lagu
    public void editLagu(Lagu dataLagu) {
        SQLiteDatabase db = getWritableDatabase();
        //Membuat kumpulan nilai kosong dengan ukuran awal default
        ContentValues cv = new ContentValues();

        cv.put(KEY_JUDUL, dataLagu.getJudul());
        cv.put(KEY_ARTIST, dataLagu.getArtist());
        cv.put(KEY_GAMBAR, dataLagu.getGambar());
        cv.put(KEY_ALBUM, dataLagu.getAlbum());
        cv.put(KEY_LIRIK_LAGU, dataLagu.getLirikLagu());
        cv.put(KEY_LINK, dataLagu.getLink());

        db.update(TABLE_LAGU, cv, KEY_ID_LAGU + "=?", new String[]{String.valueOf(dataLagu.getIdLagu())});
        db.close();
    }

    //menghapus lagu
    public void hapusLagu(int idLagu) {
        //Membuat kumpulan nilai kosong dengan ukuran awal default
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_LAGU, KEY_ID_LAGU + "=?", new String[]{String.valueOf(idLagu)}); //mencari id lagu
        db.close();
    }

    public ArrayList<Lagu> getAllLagu() {
        ArrayList<Lagu> dataLagu = new ArrayList<>();
        //mengambil data lagu dari Database Sqlite
        String query = "SELECT * FROM " + TABLE_LAGU;
        //memanggil database agar dapat dilihat isi database-nya
        SQLiteDatabase db = getReadableDatabase();
        //cursor
        Cursor csr = db.rawQuery(query, null);
        if (csr.moveToFirst()) {
            do {
                Lagu tempLagu = new Lagu(
                        csr.getInt(0),    //Daftar urutan lirik
                        csr.getString(1), //Judul
                        csr.getString(2), //Artis
                        csr.getString(3), //Gambar
                        csr.getString(4), //Album
                        csr.getString(5), //Lirik Lagu
                        csr.getString(6)  //Link
                );
                dataLagu.add(tempLagu);
            } while (csr.moveToNext());
        }
        return dataLagu;
    }

    //untuk menyimpan file gambar
    private String storeImageFile(int id) {
        String location;
        Bitmap image = BitmapFactory.decodeResource(context.getResources(), id);
        location = InputActivity.saveImageToInternalStorage(image, context);
        return location;
    }

    //menambahkan manual ke database
    private void inisialisasiLaguAwal(SQLiteDatabase db) {
        int idLagu = 0;
        //Data Lagu ke 1
        Lagu lagu1 = new Lagu(
                //id lagu didatabase
                idLagu,
                "Tujuh Belas",
                "Tulus",
                //mgambar lagu
                storeImageFile(R.drawable.manusia),
                "Manusia",
                "Masihkah kau mengingat di saat kita masih 17?\n" +
                        "Waktu di mana tanggal-tanggal merah terasa sungguh meriah\n" +
                        "Masihkah kauingat cobaan terberat kita, Matematika?\n" +
                        "Masihkah engkau ingat lagu di radio yang merdu mengudara?\n" +
                        "Kita masih sebebas itu\n" +
                        "Rasa takut yang tak pernah mengganggu\n" +
                        "Batas naluri bahaya\n" +
                        "Dulu tingginya lebihi logika\n" +
                        "Putaran Bumi dan waktu yang terus berjalan menempa kita\n" +
                        "Walau kini kita terpisah, namun, jiwaku tetap di sana (hey)\n" +
                        "oh, di masa\n" +
                        "Rasa takut yang tak pernah mengganggu\n" +
                        "Di masa naluri bahaya\n" +
                        "Dulu tingginya lebihi logika\n" +
                        "Muda jiwa, selamanya muda\n" +
                        "Kisah kita abadi selamanya\n" +
                        "kita masih sebebas itu\n" +
                        "(Rasa takut yang tak pernah mengganggu)\n" +
                        "Rasa takut yang tak pernah mengganggu\n" +
                        "(Batas naluri bahaya, oh-oh)\n" +
                        "(Dulu tingginya lebihi logika)\n" +
                        "Sederas apa pun arus di hidupmu\n" +
                        "Genggam terus kenangan tentang kita\n" +
                        "Seberapa pun dewasa mengujimu\n" +
                        "Takkan lebih dari yang engkau bisa\n" +
                        "Dan kisah kita abadi untuk s'lama-lamanya\n",
                "https://www.youtube.com/watch?v=cA7Gzh2ISL0" //untuk link

        );
        tambahLagu(lagu1, db);
        idLagu++;

        // Data Lagu ke 2
        Lagu lagu2 = new Lagu(
                idLagu,
                "Rantau",
                "Feby Putri",
                storeImageFile(R.drawable.riuh),
                "Riuh",
                "Beranjak 'tuk melihat apa kabarnya dunia\n" +
                        "Memulai lembaran baru\n" +
                        "Amat jauh berbedanya\n" +
                        "Dari nyaman yang s'lalu kubanggakan\n" +
                        "Berjejak di kota yang ramai jua s'lama ini\n" +
                        "Beberapa t'lah kupahami\n" +
                        "Masih ada turut serta\n" +
                        "Kesemogaan dari yang berarti\n" +
                        "Bernyanyilah\n" +
                        "Seirama\n" +
                        "Sya-la-la-la-la-la-la-la-la\n" +
                        "Sya-la-la-la-la-la-la-la-la\n" +
                        "Berpadu, banyak jiwa yang awal tak saling tahu\n" +
                        "Memulai cerita baru\n" +
                        "Kerap kali hilang risau\n" +
                        "Dalam lingkup yang menurutku utuh\n" +
                        "He-eh\n" +
                        "Hu-uh-uh\n" +
                        "Hu-uh-hu-hu\n" +
                        "Hu-uh-uh\n" +
                        "Bernyanyilah\n" +
                        "Seirama\n" +
                        "Sya-la-la-la-la-la-la-la-la\n" +
                        "Sya-la-la-la-la-la-la-la-la\n" +
                        "Bernyanyilah\n" +
                        "Menarilah\n" +
                        "Sya-la-la-la-la-la-la-la-la\n" +
                        "Sya-la-la-la-la-la-la-la-la\n" +
                        "Bernyanyilah\n" +
                        "Menarilah\n",
                "https://www.youtube.com/watch?v=CPSW6wSHovs" //untuk link

        );
        tambahLagu(lagu2, db);
        idLagu++;
    }
}
