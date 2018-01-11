package com.android.gan091.gramaudenar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by SIB02 on 23/10/2017.
 */

public class BaseDeDatos extends SQLiteOpenHelper {

    String sqlTablaFachada = "CREATE TABLE tblFachada(Latitud VARCHAR(20), Longitud VARCHAR(20), Ancho DOUBLE, Altura DOUBLE, Area DOUBLE, Area1 DOUBLE)";
    String sqlTablaVentana = "CREATE TABLE tblVentana(Latitud VARCHAR(20), Longitud VARCHAR(20), Ancho DOUBLE, Altura DOUBLE, Area DOUBLE, NumeroPiso INTEGER)";
    String sqlTablaPuerta = "CREATE TABLE tblPuerta(Latitud VARCHAR(20), Longitud VARCHAR(20), Ancho DOUBLE, Altura DOUBLE, Area DOUBLE)";
    String sqlTablaOndaChoqueEnterramiento = "CREATE TABLE tblOndaChoque(Latitud VARCHAR(20), Longitud VARCHAR(20), MaterialVentana VARCHAR(7), MarcoVentana VARCHAR(15), MaterialPiso VARCHAR(8), MaterialMuros VARCHAR(13), TipologiaOnda VARCHAR(12), TipologiaEnt VARCHAR(12), ObservacionesOCH VARCHAR(200))";
    String sqlTablaCasa = "CREATE TABLE tblCasa(Latitud VARCHAR(20), Longitud VARCHAR(20), Lugar VARCHAR(30), Tipo INTEGER, PRIMARY KEY(Latitud,Longitud))";
    String sqlTablaLahares = "CREATE TABLE tblLahares(Latitud VARCHAR(20), Longitud VARCHAR(20), Reforzado BOOL, MaterialMuros VARCHAR(16), EstadoEdificacion VARCHAR(8), TipologiaLahares VARCHAR(12), ObservacionesL VARCHAR(200))";
    String sqlTablaCeniza = "CREATE TABLE tblCeniza(Latitud VARCHAR(20), Longitud VARCHAR(20), TipoTecho VARCHAR(9), MaterialCobertura VARCHAR(16), MaterialApoyo VARCHAR(16), FormaCubierta VARCHAR(13), InclinacionCubierta VARCHAR(20), EstadoGeneralCubierta VARCHAR(8), TipologiaCeniza VARCHAR(12), ObservacionesC VARCHAR(200))";
    String sqlTablaZona = "CREATE TABLE tblZona(Id INTEGER PRIMARY KEY, Corregimiento VARCHAR(11), Sector INTEGER)";
    String sqlTablaCamara = "CREATE TABLE tblCamara(Id INTEGER PRIMARY KEY, Latitud VARCHAR(20), Longitud VARCHAR(20))";
    boolean inicio = true;
    Context contextLocal;

    public BaseDeDatos(Context context) {
        super(context, "GramaDB", null, 1);
        contextLocal = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(sqlTablaFachada);
        sqLiteDatabase.execSQL(sqlTablaVentana);
        sqLiteDatabase.execSQL(sqlTablaPuerta);
        sqLiteDatabase.execSQL(sqlTablaOndaChoqueEnterramiento);
        sqLiteDatabase.execSQL(sqlTablaCasa);
        sqLiteDatabase.execSQL(sqlTablaLahares);
        sqLiteDatabase.execSQL(sqlTablaCeniza);
        sqLiteDatabase.execSQL(sqlTablaZona);
        sqLiteDatabase.execSQL(sqlTablaCamara);

        if (inicio){
            sqLiteDatabase.execSQL("INSERT INTO tblZona (Id, Corregimiento, Sector) VALUES (1,'Rural',0)");
            sqLiteDatabase.execSQL("INSERT INTO tblCamara (Id, Latitud, Longitud) VALUES (1,'1.299568021668017','-77.39873830229044')");
            inicio = false;
        }
    }

    BaseDeDatos bdP;
    SQLiteDatabase db;

    public void abrirBD(){
        bdP = new BaseDeDatos(contextLocal);
        db = bdP.getWritableDatabase();
    }

    public Cursor cargarDatos(){
        Cursor cursor = db.rawQuery("SELECT tblCasa.Latitud, tblCasa.Longitud, tblCasa.Lugar, tblOndaChoque.TipologiaOnda, tblOndaChoque.TipologiaEnt, tblLahares.TipologiaLahares, tblCeniza.TipologiaCeniza FROM tblCasa JOIN tblCeniza ON tblCasa.Latitud = tblCeniza.Latitud AND tblCasa.Longitud = tblCeniza.Longitud JOIN tblOndaChoque ON tblCeniza.Latitud = tblOndaChoque.Latitud AND tblCeniza.Longitud = tblOndaChoque.Longitud JOIN tblLahares ON tblLahares.Latitud = tblOndaChoque.Latitud AND tblLahares.Longitud = tblOndaChoque.Longitud",null);
        return cursor;
    }

    public Cursor cargarDatos(String tabla){
        Cursor cursor = db.rawQuery("SELECT * FROM "+tabla,null);
        return cursor;
    }

    public Cursor cargarDatosTablas(String lat, String lon, String tabla){
        Cursor cursor = db.rawQuery("SELECT * FROM "+tabla+" WHERE Latitud=" + "'" +lat + "'" + " AND Longitud=" + "'" + lon +"'",null);
        return cursor;
    }

    public Cursor cargarDatosTablas1(String lat, String lon, String tabla){
        Cursor cursor = db.rawQuery("SELECT * FROM "+tabla+" WHERE Latitud=" + "'" +lat + "'" + " AND Longitud=" + "'" + lon +"' AND NumeroPiso=1",null);
        return cursor;
    }

    public Cursor cargarDatos(String parametro1, String parametro2, String tabla){
        Cursor cursor = db.rawQuery("SELECT "+parametro1+", "+parametro2+" FROM "+tabla,null);
        return cursor;
    }

    public Cursor cargarDatos(String parametro1, String parametro2, String parametro3, String tabla){
        Cursor cursor = db.rawQuery("SELECT "+parametro1+", "+parametro2+", "+parametro3+" FROM "+tabla,null);
        return cursor;
    }

    public Cursor cargarDatos(String parametro1, String parametro2, String tabla, String lat, String lon){
        String sql = "SELECT "+parametro1+", "+parametro2+" FROM "+tabla+" WHERE Latitud=" + "'" +lat + "'" + " AND Longitud=" + "'" + lon +"'";
        Cursor cursor = db.rawQuery(sql,null);
        return cursor;
    }

    public Cursor cargarDatos(String parametro1, String parametro2, String parametro3, String tabla, String lat, String lon){
        String sql = "SELECT "+parametro1+", "+parametro2+", "+parametro3+" FROM "+tabla+" WHERE Latitud=" + "'" +lat + "'" + " AND Longitud=" + "'" + lon +"'";
        Cursor cursor = db.rawQuery(sql,null);
        return cursor;
    }

    public void cerrarBD(){
        db.close();
    }

    public void eliminarRegistro(String tabla, String lat, String lon) throws Exception{
        abrirBD();
        db.execSQL("DELETE FROM "+tabla+" WHERE Latitud=" + "'" +lat + "'" + " AND Longitud=" + "'" + lon +"'");
        cerrarBD();
    }

    public boolean existeRegistro(String tabla, String lat, String lon) {
        abrirBD();
        boolean existe = false;
        if (db != null) {
            Cursor cursor = db.rawQuery("SELECT * FROM "+tabla+" WHERE Latitud=" + "'" +lat + "'" + " AND Longitud=" + "'" + lon +"'", null);
            cursor.moveToFirst();

            if (cursor.getCount() > 0) {
                existe = true;
            }
        }cerrarBD();
        return existe;
    }

    public void insertarCamara(String lat, String lon) throws Exception{
        abrirBD();
        ContentValues values = new ContentValues();
        values.put("Latitud",lat);
        values.put("Longitud",lon);
        db.update("tblCamara",values,"id=1",null);
        cerrarBD();
    }

    public long insertarCasa(String lat, String lon, String zona, int tipo) throws Exception{
        ContentValues values = new ContentValues();
        values.put("Latitud",lat);
        values.put("Longitud",lon);
        values.put("Lugar",zona);
        values.put("Tipo",tipo);

        return db.insert("tblCasa",null,values);
    }

    public long insertar(String lat, String lon, float anchoP, float alturaP, float areaP) throws Exception{
        abrirBD();
        long registro=0;

        if(db!=null){
            ContentValues nuevoRegistro = new ContentValues();
            nuevoRegistro.put("Latitud",lat);
            nuevoRegistro.put("Longitud",lon);
            nuevoRegistro.put("Ancho",anchoP);
            nuevoRegistro.put("Altura",alturaP);
            nuevoRegistro.put("Area",areaP);

            registro = db.insert("tblPuerta",null,nuevoRegistro);
        }
        cerrarBD();
        return registro;
    }

    public long insertar(String lat,String lon, float anchoF, float alturaF, float areaF, float areaF1) throws Exception{
        abrirBD();
        long registro=0;
        if(db!=null){
            ContentValues values = new ContentValues();
            values.put("Latitud",lat);
            values.put("Longitud",lon);
            values.put("Ancho",anchoF);
            values.put("Altura",alturaF);
            values.put("Area",areaF);
            values.put("Area1",areaF1);

            registro = db.insert("tblFachada",null,values);
        }
        cerrarBD();
        return registro;
    }

    public long insertar(String lat, String lon, float anchoV, float altoV, float areaV, int numPiso) throws Exception{
        abrirBD();
        long registro=0;

        if(db!=null){
            ContentValues nuevoRegistro = new ContentValues();
            nuevoRegistro.put("Latitud",lat);
            nuevoRegistro.put("Longitud",lon);
            nuevoRegistro.put("Ancho",anchoV);
            nuevoRegistro.put("Altura",altoV);
            nuevoRegistro.put("Area",areaV);
            nuevoRegistro.put("NumeroPiso",numPiso);

            registro = db.insert("tblVentana",null,nuevoRegistro);
        }
        cerrarBD();
        return registro;
    }

    public void insertar(String corregimiento, int sector) throws Exception{
        abrirBD();
        ContentValues values = new ContentValues();
        values.put("Corregimiento",corregimiento);
        values.put("Sector",sector);
        db.update("tblZona",values,"id=1",null);
        cerrarBD();
    }

    public long insertarCeniza(String lat, String lon, String tipoTecho, String matCob, String matElemApoyo, String formaCubierta, String inclCub, String estadoGen, String tipCeniza, String obvs) throws Exception{
        abrirBD();
        long registro = 0;

        if(db!=null){
            ContentValues nuevoRegistro = new ContentValues();
            nuevoRegistro.put("Latitud",lat);
            nuevoRegistro.put("Longitud",lon);
            nuevoRegistro.put("TipoTecho",tipoTecho);
            nuevoRegistro.put("MaterialCobertura",matCob);
            nuevoRegistro.put("MaterialApoyo",matElemApoyo);
            nuevoRegistro.put("FormaCubierta",formaCubierta);
            nuevoRegistro.put("InclinacionCubierta",inclCub);
            nuevoRegistro.put("EstadoGeneralCubierta",estadoGen);
            nuevoRegistro.put("TipologiaCeniza",tipCeniza);
            nuevoRegistro.put("ObservacionesC",obvs);

            registro = db.insert("tblCeniza",null,nuevoRegistro);
        }
        cerrarBD();
        return registro;
    }

    public long insertarLahares(String lat, String lon, boolean reforzado, String matMur, String estGeneral, String tipLahares, String obvs) throws Exception{
        abrirBD();
        long registro = 0;

        if(db!=null){
            ContentValues nuevoRegistro = new ContentValues();
            nuevoRegistro.put("Latitud",lat);
            nuevoRegistro.put("Longitud",lon);
            nuevoRegistro.put("Reforzado",reforzado);
            nuevoRegistro.put("MaterialMuros",matMur);
            nuevoRegistro.put("EstadoEdificacion",estGeneral);
            nuevoRegistro.put("TipologiaLahares",tipLahares);
            nuevoRegistro.put("ObservacionesL",obvs);

            registro = db.insert("tblLahares",null,nuevoRegistro);
        }
        cerrarBD();
        return registro;
    }

    public long insertarOndaChoqueEnterramiento(String lat, String lon, String matVen, String marVen, String matPiso, String matMur, String tipOndaCh, String tipEnt, String obvs) throws Exception{
        abrirBD();
        long registro = 0;

        if(db!=null){
            ContentValues values = new ContentValues();
            values.put("Latitud",lat);
            values.put("Longitud",lon);
            values.put("MaterialVentana",matVen);
            values.put("MarcoVentana",marVen);
            values.put("MaterialPiso",matPiso);
            values.put("MaterialMuros",matMur);
            values.put("TipologiaOnda",tipOndaCh);
            values.put("TipologiaEnt",tipEnt);
            values.put("ObservacionesOCH",obvs);

            registro = db.insert("tblOndaChoque",null,values);
        }
        cerrarBD();
        return registro;
    }

    public  float reducirFloat(float f){
        float f1 = (float)(Math.round(f*100.0)/100.0);
        return f1;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}