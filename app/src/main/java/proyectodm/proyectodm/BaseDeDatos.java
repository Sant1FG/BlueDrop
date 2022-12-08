package proyectodm.proyectodm;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseDeDatos extends  SQLiteOpenHelper{

    public static final String DBNAME = "BlueDrop.db";


    public BaseDeDatos(Context contextname) {
        super(contextname,"BlueDrop.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table usuarios(nombre TEXT primary key, contraseña TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop Table if exists users");
    }

    public Boolean registrar(String nombre, String contraseña){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nombre",nombre);
        contentValues.put("contraseña", contraseña);
        long result = db.insert("usuarios", null, contentValues);
        if(result==-1) return false;
        else
            return true;
    }

    public Boolean comprobarExistenciaUsuario(String nombre){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from usuarios where nombre = ?", new String[] {nombre});
        if(cursor.getCount()>0)
            return true;
        else
            return false;
    }

    public Boolean comprobarCredenciales(String nombre, String contraseña){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from usuarios where nombre = ? and contraseña = ?", new String[] {nombre, contraseña} );
        if(cursor.getCount()>0)
            return true;
        else
            return false;
    }
}