package com.android.gan091.gramaudenar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.channels.FileChannel;

import static android.R.attr.x;
import static android.R.attr.y;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    boolean contains = false;
    char sector = 'a';
    String latitud,longitud;
    int corregimiento =10;
    private GoogleMap mMap;

    ArrayAdapter<String> aaSector,aaCorregimiento;
    BaseDeDatos bdP;
    Button btnBorrar,btnBackup, btnActualizar;
    Context context;
    EditText etContrasena;
    LatLng camara;

    PolygonOptions pOpFSA;
    PolygonOptions pOpRSA, pOpRSB, pOpRSC, pOpRSD,pOpRSE,pOpRSF,pOpRSG;
    PolygonOptions pOpRoSA, pOpRoSB, pOpRoSC, pOpRoSD,pOpRoSE,pOpRoSF,pOpRoSG,pOpRoSH,pOpRoSI;
    PolygonOptions pOpTSA, pOpTSB, pOpTSC, pOpTSD,pOpTSE;
    PolygonOptions pOpMSA, pOpMSB, pOpMSC, pOpMSD,pOpMSE,pOpMSF,pOpMSG,pOpMSH;

    Spinner spSector,spCorregimiento;

    String [] opcCorregimiento = new String[] {"Rural","La Florida","Matituy","Tunja","Robles","Rodeo"};
    String [] opcSecLaF = new String[] {"Sector FA","Sector FB","Sector FC"};
    String [] opcSecR = new String[] {"Sector RA","Sector RB","Sector RC","Sector RD","Sector RE","Sector RF","Sector RG"};
    String [] opcSecRo = new String[] {"Sector ROA","Sector ROB","Sector ROC","Sector ROD","Sector ROE","Sector ROF","Sector ROG","Sector ROH","Sector ROI"};
    String [] opcSecT = new String[] {"Sector TA","Sector TB","Sector TC","Sector TD","Sector TE"};
    String [] opcSecM = new String[] {"Sector MA","Sector MB","Sector MC","Sector MD","Sector ME","Sector MF","Sector MG","Sector MH"};
    String [] opcSecRu = new String[] {""};
    String opcCorr;
    String []archivoCad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        context = this;

        mPlanetTitles = getResources().getStringArray(R.array.planeta_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mPlanetTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(this);

        spSector = (Spinner) findViewById(R.id.spSector);
        spCorregimiento = (Spinner) findViewById(R.id.spCorregimiento);

        aaCorregimiento = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,opcCorregimiento);
        spCorregimiento.setAdapter(aaCorregimiento);

        etContrasena = (EditText) findViewById(R.id.etContrasena);
        btnBorrar = (Button) findViewById(R.id.btnFijar);
        btnBackup = (Button) findViewById(R.id.btnBackup);
        btnActualizar = (Button) findViewById(R.id.btnActualizar);

        spSector.setVisibility(View.INVISIBLE);
        btnBackup.setVisibility(View.INVISIBLE);
        spCorregimiento.setVisibility(View.INVISIBLE);
        btnActualizar.setVisibility(View.INVISIBLE);

        bdP = new BaseDeDatos(context);

        asignarCorregimiento("Rural");

        cargarZona();
        if(spCorregimiento.getSelectedItem().toString().equals("Rural")){

        }
        spSector.setOnItemSelectedListener(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //addLaFlorida();
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        //mMap.clear();
        extras();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnBloquear:
                spSector.setVisibility(View.INVISIBLE);
                btnBorrar.setVisibility(View.INVISIBLE);
                spCorregimiento.setVisibility(View.INVISIBLE);
                btnActualizar.setVisibility(View.INVISIBLE);
                btnBackup.setVisibility(View.INVISIBLE);
                break;
            case R.id.btnValidar:
                if (etContrasena.getText().toString().equals("0000")){
                    spSector.setVisibility(View.VISIBLE);
                    btnBorrar.setVisibility(View.VISIBLE);
                    spCorregimiento.setVisibility(View.VISIBLE);
                    btnActualizar.setVisibility(View.VISIBLE);
                    btnBackup.setVisibility(View.VISIBLE);
                    etContrasena.setText("");
                }
                break;
            case R.id.btnFijar:
                fijarZona();
                break;
            case R.id.btnBackup:
                generarArchivoT();
                generarArchivoF();
                generarArchivoV();
                generarArchivoP();
                generarArchivoOCH();
                generarArchivoL();
                generarArchivoC();
                backupDatabase();
                break;
            case R.id.btnActualizar:
                asignarCorregimiento(spCorregimiento.getSelectedItem().toString());
                break;
        }
    }

    public void addMarcador(){
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                switch (corregimiento){
                    case 0:
                        contains = true;
                        break;
                    case 10:
                        switch (sector){
                            case 'a':
                                contains = PolyUtil.containsLocation(latLng,pOpFSA.getPoints(),false);
                                break;
                        }
                        break;
                    case 20:
                        switch (sector){
                            case 'a':
                                contains = PolyUtil.containsLocation(latLng,pOpRSA.getPoints(),false);
                                break;
                            case 'b':
                                contains = PolyUtil.containsLocation(latLng,pOpRSB.getPoints(),false);
                                break;
                            case 'c':
                                contains = PolyUtil.containsLocation(latLng,pOpRSC.getPoints(),false);
                                break;
                            case 'd':
                                contains = PolyUtil.containsLocation(latLng,pOpRSD.getPoints(),false);
                                break;
                            case 'e':
                                contains = PolyUtil.containsLocation(latLng,pOpRSE.getPoints(),false);
                                break;
                            case 'f':
                                contains = PolyUtil.containsLocation(latLng,pOpRSF.getPoints(),false);
                                break;
                            case 'g':
                                contains = PolyUtil.containsLocation(latLng,pOpRSG.getPoints(),false);
                                break;
                        }
                        break;
                    case 30:
                        switch (sector) {
                            case 'a':
                                contains = PolyUtil.containsLocation(latLng, pOpRoSA.getPoints(), false);
                                break;
                            case 'b':
                                contains = PolyUtil.containsLocation(latLng, pOpRoSB.getPoints(), false);
                                break;
                            case 'c':
                                contains = PolyUtil.containsLocation(latLng, pOpRoSC.getPoints(), false);
                                break;
                            case 'd':
                                contains = PolyUtil.containsLocation(latLng, pOpRoSD.getPoints(), false);
                                break;
                            case 'e':
                                contains = PolyUtil.containsLocation(latLng, pOpRoSE.getPoints(), false);
                                break;
                            case 'f':
                                contains = PolyUtil.containsLocation(latLng, pOpRoSF.getPoints(), false);
                                break;
                            case 'g':
                                contains = PolyUtil.containsLocation(latLng, pOpRoSG.getPoints(), false);
                                break;
                            case 'h':
                                contains = PolyUtil.containsLocation(latLng, pOpRoSH.getPoints(), false);
                                break;
                            case 'i':
                                contains = PolyUtil.containsLocation(latLng, pOpRoSI.getPoints(), false);
                                break;
                        }
                        break;
                    case 40:
                        switch (sector) {
                            case 'a':
                                contains = PolyUtil.containsLocation(latLng, pOpTSA.getPoints(), false);
                                break;
                            case 'b':
                                contains = PolyUtil.containsLocation(latLng, pOpTSB.getPoints(), false);
                                break;
                            case 'c':
                                contains = PolyUtil.containsLocation(latLng, pOpTSC.getPoints(), false);
                                break;
                            case 'd':
                                contains = PolyUtil.containsLocation(latLng, pOpTSD.getPoints(), false);
                                break;
                            case 'e':
                                contains = PolyUtil.containsLocation(latLng, pOpTSE.getPoints(), false);
                                break;
                        }
                        break;
                    case 50:
                        switch (sector) {
                            case 'a':
                                contains = PolyUtil.containsLocation(latLng, pOpMSA.getPoints(), false);
                                break;
                            case 'b':
                                contains = PolyUtil.containsLocation(latLng, pOpMSB.getPoints(), false);
                                break;
                            case 'c':
                                contains = PolyUtil.containsLocation(latLng, pOpMSC.getPoints(), false);
                                break;
                            case 'd':
                                contains = PolyUtil.containsLocation(latLng, pOpMSD.getPoints(), false);
                                break;
                            case 'e':
                                contains = PolyUtil.containsLocation(latLng, pOpMSE.getPoints(), false);
                                break;
                            case 'f':
                                contains = PolyUtil.containsLocation(latLng, pOpMSF.getPoints(), false);
                                break;
                            case 'g':
                                contains = PolyUtil.containsLocation(latLng, pOpMSG.getPoints(), false);
                                break;
                            case 'h':
                                contains = PolyUtil.containsLocation(latLng, pOpMSH.getPoints(), false);
                                break;
                        }
                        break;
                }

                if(contains){
                    mMap.addMarker(new MarkerOptions()
                            .position(latLng));
                }
            }
        });
    }

    public void asignarCorregimiento(String opcCorr){
        switch (opcCorr){
            case "Rural":
                corregimiento = 0;
                aaSector = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,opcSecRu);
                break;
            case "La Florida":
                corregimiento = 10;
                aaSector = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,opcSecLaF);
                break;
            case "Robles":
                corregimiento = 20;
                aaSector = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,opcSecR);
                break;
            case "Rodeo":
                corregimiento = 30;
                aaSector = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,opcSecRo);
                break;
            case "Tunja":
                corregimiento = 40;
                aaSector = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,opcSecT);
                break;
            case "Matituy":
                corregimiento = 50;
                aaSector = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,opcSecM);
                break;
        }
        spSector.setAdapter(aaSector);
    }

    public void cargarDatos(){
        bdP.abrirBD();
        Cursor cursor = bdP.cargarDatos("Latitud","Longitud","Tipo","tblCasa");
        Cursor cOCh, cC, cL;

        try {
            if (cursor.moveToFirst()){
                do {
                    latitud = cursor.getString(0);
                    longitud = cursor.getString(1);

                    int res = 0;

                    cOCh = bdP.cargarDatosTablas(latitud,longitud,"tblOndaChoque");

                    if(cOCh.moveToFirst()){
                        res = res + 1;
                    }

                    cC = bdP.cargarDatosTablas(latitud,longitud,"tblCeniza");
                    if(cC.moveToFirst()){
                        res = res + 1;
                    }

                    cL = bdP.cargarDatosTablas(latitud,longitud,"tblLahares");
                    if(cL.moveToFirst()){
                        res = res + 1;
                    }

                    if (res == 3){
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitud))));
                    }else {
                        if (res > 0 && res < 3){
                            mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitud)))
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        }
                        else {
                            mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitud)))
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                        }
                    }

                    Log.i("Resultado--->",Integer.toString(res));


                    /*Cursor cursorTipologia = bdP.cargarDatosTablas(latitud,longitud,"tblCeniza");
                    if (cursorTipologia.moveToFirst()){
                        do{
                            String tip = cursorTipologia.getString(8);
                            if (tip.equals("Tipologia_3")){
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitud)))
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                            }
                            else {
                                if (tip.equals("Tipologia_2")){
                                    mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitud)))
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                                }
                                else{
                                        if (tip.equals("Tipologia_1")){
                                            Log.i("Coordenadas","Latitud: "+latitud+" Longitud: "+longitud);
                                            mMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitud)))
                                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                        }
                                        else {
                                            mMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitud)))
                                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                                        }
                                }
                            }
                        }while (cursorTipologia.moveToNext());
                    }*/
                    /*if (cursor.getInt(2) == 1){
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitud)))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    }
                    else {
                        if (cursor.getInt(2) == 2){
                            mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitud)))
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                        }
                        else {
                            mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitud))));
                        }
                    }*/
                }while (cursor.moveToNext());
            }
        }
        catch (Exception e){
            Log.e("Base de Datos","Error al leer la base de datos");
        }
        bdP.close();
    }

    public void extras(){
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                final Marker m = marker;
                latitud = Double.toString(setLatitud(m));
                longitud = Double.toString(setLongitud(m));

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                final CharSequence[] items = new CharSequence[4];

                items[0] = "Eliminar Marcador";
                items[1] = "Agregar Casa";
                items[2] = "Eliminar Casa";
                items[3] = "Fijar Camara";

                builder.setTitle("Opciones")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:
                                        m.remove();
                                        break;
                                    case 1:
                                        if(bdP.existeRegistro("tblCasa",latitud,longitud)){
                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);

                                            builder1.setTitle("Registro existente")
                                                    .setMessage("Ya hay una vivienda registrada con estas coordenadas ¿Desea modificar su registro?")
                                                    .setPositiveButton("Si",
                                                            new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    instanciarCasa(latitud,longitud);
                                                                }
                                                            })
                                                    .setNegativeButton("No",
                                                            new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {}
                                                            });
                                            builder1.create().show();
                                        }
                                        else {
                                            Toast toast;
                                            try {
                                                bdP.abrirBD();
                                                long insert = bdP.insertarCasa(latitud,longitud,zonas(),0);
                                                instanciarCasa(latitud,longitud);
                                                bdP.cerrarBD();
                                            } catch (Exception e) {
                                                toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
                                                toast.show();
                                            }

                                        }
                                        break;
                                    case 2:
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);

                                        builder1.setTitle("Eliminar Casa")
                                                .setMessage("¿Esta seguro que desea eliminar los registros de la casa seleccionada?")
                                                .setPositiveButton("Si",
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                try {
                                                                    if (bdP.existeRegistro("tblCasa",latitud,longitud)){
                                                                        bdP.eliminarRegistro("tblCasa",latitud,longitud);
                                                                        if (bdP.existeRegistro("tblOndaChoque",latitud,longitud)){
                                                                            bdP.eliminarRegistro("tblOndaChoque",latitud,longitud);
                                                                            if (bdP.existeRegistro("tblFachada",latitud,longitud)){
                                                                                bdP.eliminarRegistro("tblFachada",latitud,longitud);
                                                                            }
                                                                            if (bdP.existeRegistro("tblVentana",latitud,longitud)){
                                                                                bdP.eliminarRegistro("tblVentana",latitud,longitud);
                                                                            }
                                                                            if (bdP.existeRegistro("tblPuerta",latitud,longitud)){
                                                                                bdP.eliminarRegistro("tblPuerta",latitud,longitud);
                                                                            }
                                                                        }
                                                                        if (bdP.existeRegistro("tblCeniza",latitud,longitud)){
                                                                            bdP.eliminarRegistro("tblCeniza",latitud,longitud);
                                                                        }
                                                                    }
                                                                    m.remove();
                                                                } catch (Exception e) {
                                                                    Log.i("EliminarCasa",e.toString());
                                                                    Toast toast;
                                                                    toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
                                                                    toast.show();
                                                                }
                                                            }
                                                        })
                                                .setNegativeButton("No",
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {}
                                                        });
                                        builder1.create().show();
                                        break;
                                    case 3:
                                        try {
                                            bdP.insertarCamara(latitud,longitud);
                                        }
                                        catch (Exception e){
                                            Toast toast;
                                            toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
                                            toast.show();
                                        }
                                        break;
                                }
                            }
                        });

                builder.create();
                builder.show();

                return false;
            }
        });
    }

    public void instanciarCasa(String lat, String lon){
        Intent i = new Intent(MapsActivity.this,MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("c1",lat);
        bundle.putString("c2",lon);

        i.putExtras(bundle);
        startActivity(i);
    }

    public double setLatitud(Marker marker){
        return marker.getPosition().latitude;
    }

    public double setLongitud(Marker marker){
        return marker.getPosition().longitude;
    }

    public String zonas(){
        String zona="";
        switch(corregimiento){
            case 0:
                zona = "Rural";
                break;
            case 10:
                zona = "La Florida";
                break;
            case 20:
                zona = "Robles";
                break;
            case 30:
                zona = "Rodeo";
                break;
            case 40:
                zona = "Tunja";
                break;
            case 50:
                zona = "Matituy";
                break;
            default:
                zona = "Error";
                break;
        }
        return zona;
    }

    public void fijarZona(){
        try {
            bdP.insertar(spCorregimiento.getSelectedItem().toString(),spSector.getSelectedItemPosition());
        }
        catch (Exception e){
            Toast toast;
            toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    public void cargarZona(){
        bdP.abrirBD();
        Cursor cursor = bdP.cargarDatos("Corregimiento","Sector","tblZona");
        cursor.moveToFirst();
        String c;
        int s;
        do {
            c = cursor.getString(0);
            s = cursor.getInt(1);
        }while (cursor.moveToNext());
        bdP.cerrarBD();

        asignarCorregimiento(c);
        spSector.setSelection(s);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mMap.clear();
        cargarDatos();

        switch (spSector.getSelectedItem().toString()){
            case "":
                Cursor cursor = bdP.cargarDatos("Latitud","Longitud","tblCamara");

                if (cursor.moveToFirst()){
                    do {
                        camara = new LatLng(cursor.getDouble(0),cursor.getDouble(1));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,15));
                    }while (cursor.moveToNext());
                }
                break;
            case "Sector FA":
                pOpFSA = new PolygonOptions()
                        .add(new LatLng(1.297758, -77.404716),
                                new LatLng(1.299492, -77.404890),
                                new LatLng(1.299807, -77.404562),
                                new LatLng(1.299816, -77.402587),
                                new LatLng(1.298172, -77.402851),
                                new LatLng(1.297664, -77.404094),
                                new LatLng(1.297855, -77.404143))
                        .strokeColor(Color.RED)
                        .strokeWidth(3);
                camara = new LatLng(1.299816, -77.402587);
                mMap.addPolygon(pOpFSA);
                sector = 'a';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,16));
                break;
            case "Sector RA":
                pOpRSA = new PolygonOptions()
                        .add(new LatLng(1.3410625,-77.4189109),
                                new LatLng(1.3408158,-77.4193829),
                                new LatLng(1.3404136,-77.4196887),
                                new LatLng(1.3399792,-77.4199462),
                                new LatLng(1.3376892,-77.418809),
                                new LatLng(1.338086,-77.4179399),
                                new LatLng(1.3403921,-77.4183798))
                        .strokeColor(Color.BLUE)
                        .strokeWidth(3);
                camara = new LatLng(1.3394682268,-77.4189103679);
                mMap.addPolygon(pOpRSA);
                sector = 'a';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,17));
                break;
            case "Sector RB":
                pOpRSB = new PolygonOptions()
                        .add(new LatLng(1.3408694,-77.4193937),
                                new LatLng(1.342666,-77.4196887),
                                new LatLng(1.3424891,-77.420429),
                                new LatLng(1.342001,-77.4203324),
                                new LatLng(1.3408212,-77.4205363),
                                new LatLng(1.3401562,-77.4198872))
                        .strokeColor(Color.CYAN)
                        .strokeWidth(3);
                camara = new LatLng(1.3415374697,-77.4199251444);
                mMap.addPolygon(pOpRSB);
                sector = 'b';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,18));
                break;
            case "Sector RC":
                pOpRSC = new PolygonOptions()
                        .add(new LatLng(1.3425802,-77.4201232),
                                new LatLng(1.3427787,-77.4197853),
                                new LatLng(1.3451008,-77.4190342),
                                new LatLng(1.3463236,-77.4186587),
                                new LatLng(1.3477662,-77.4197048),
                                new LatLng(1.3475678,-77.4207079),
                                new LatLng(1.3468438,-77.4206436),
                                new LatLng(1.3466024,-77.4205953),
                                new LatLng(1.346404,-77.4197102),
                                new LatLng(1.3427572,-77.4205363),
                                new LatLng(1.3425588,-77.4202949))
                        .strokeColor(Color.MAGENTA)
                        .strokeWidth(3);
                camara = new LatLng(1.3452881125,-77.4195969187);
                mMap.addPolygon(pOpRSC);
                sector = 'c';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,17));
                break;
            case "Sector RD":
                pOpRSD = new PolygonOptions()
                        .add(new LatLng(1.3475249,-77.4207187),
                                new LatLng(1.3479888,-77.4209145),
                                new LatLng(1.3478439,-77.4213946),
                                new LatLng(1.3476669,-77.4218425),
                                new LatLng(1.3470905,-77.4217004),
                                new LatLng(1.3467419,-77.420665))
                        .strokeColor(Color.RED)
                        .strokeWidth(3);
                camara = new LatLng(1.3474422261,-77.4211804527);
                mMap.addPolygon(pOpRSD);
                sector = 'd';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,18));
                break;
            case "Sector RE":
                pOpRSE = new PolygonOptions()
                        .add(new LatLng(1.3487932,-77.4217781),
                                new LatLng(1.3485277,-77.4218827),
                                new LatLng(1.3476991,-77.4217808),
                                new LatLng(1.3479137,-77.4211586),
                                new LatLng(1.3486108,-77.4210164))
                        .strokeColor(Color.WHITE)
                        .strokeWidth(3);
                camara = new LatLng(1.3483590297,-77.4214654691);
                mMap.addPolygon(pOpRSE);
                sector = 'e';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,19));
                break;
            case "Sector RF":
                pOpRSF = new PolygonOptions()
                        .add(new LatLng(1.3487932,-77.4217057),
                                new LatLng(1.3486162,-77.4210003),
                                new LatLng(1.3494287,-77.4206704),
                                new LatLng(1.3523729,-77.4201393),
                                new LatLng(1.352558,-77.4208179),
                                new LatLng(1.3522335,-77.422092))
                        .strokeColor(Color.YELLOW)
                        .strokeWidth(3);
                camara = new LatLng(1.3507437258,-77.4210808658);
                mMap.addPolygon(pOpRSF);
                sector = 'f';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,17));
                break;
            case "Sector RG":
                pOpRSG = new PolygonOptions()
                        .add(new LatLng(1.3493536,-77.422049),
                                new LatLng(1.3480772,-77.4230736),
                                new LatLng(1.347187,-77.4225801),
                                new LatLng(1.3475141,-77.4218184),
                                new LatLng(1.3481523,-77.4218988),
                                new LatLng(1.3485224,-77.4219364),
                                new LatLng(1.3489192,-77.4217594))
                        .strokeColor(Color.LTGRAY)
                        .strokeWidth(3);
                camara = new LatLng(1.3482122129,-77.4222849837);
                mMap.addPolygon(pOpRSG);
                sector = 'g';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,18));
                break;
            case "Sector ROA":
                pOpRoSA = new PolygonOptions()
                        .add(new LatLng(1.3126441,-77.4355406),
                                new LatLng(1.3119093,-77.4360341),
                                new LatLng(1.311121,-77.4353957),
                                new LatLng(1.3115447,-77.4346823),
                                new LatLng(1.3123062,-77.434237),
                                new LatLng(1.313234,-77.4347252))
                        .strokeColor(Color.BLUE)
                        .strokeWidth(3);

                camara = new LatLng(1.3122145292,-77.4349648110);
                mMap.addPolygon(pOpRoSA);
                sector = 'a';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,18));
                break;
            case "Sector ROB":
                pOpRoSB = new PolygonOptions()
                        .add(new LatLng(1.3123062,-77.434237),
                                new LatLng(1.3115447,-77.4346823),
                                new LatLng(1.3102736,-77.4336416),
                                new LatLng(1.3111371,-77.4324614),
                                new LatLng(1.3123759,-77.4322093),
                                new LatLng(1.3131053,-77.4329603))
                        .strokeColor(Color.CYAN)
                        .strokeWidth(3);

                camara = new LatLng(1.3117131545,-77.4332049749);
                mMap.addPolygon(pOpRoSB);
                sector = 'b';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,17));
                break;
            case "Sector ROC":
                pOpRoSC = new PolygonOptions()
                        .add(new LatLng(1.3126119,-77.4318874),
                                new LatLng(1.3113677,-77.4322093),
                                new LatLng(1.3108636,-77.4327135),
                                new LatLng(1.3092225,-77.4337113),
                                new LatLng(1.3086325,-77.4323702),
                                new LatLng(1.3095443,-77.4308038),
                                new LatLng(1.3103487,-77.4306482),
                                new LatLng(1.3105793,-77.4305838),
                                new LatLng(1.3108207,-77.4305087),
                                new LatLng(1.3109762,-77.4304926),
                                new LatLng(1.3112122,-77.4304873),
                                new LatLng(1.3114106,-77.4304497),
                                new LatLng(1.3116466,-77.4304444),
                                new LatLng(1.3119093,-77.4304229),
                                new LatLng(1.3121614,-77.430439))
                        .strokeColor(Color.MAGENTA)
                        .strokeWidth(3);

                camara = new LatLng(1.3105038256,-77.4315769032);
                mMap.addPolygon(pOpRoSC);
                sector = 'c';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,17));
                break;
            case "Sector ROD":
                pOpRoSD = new PolygonOptions()
                        .add(new LatLng(1.3122419,-77.4304283),
                                new LatLng(1.3113918,-77.4303719),
                                new LatLng(1.3097373,-77.4289155),
                                new LatLng(1.3109708,-77.4279606),
                                new LatLng(1.3123759,-77.428894),
                                new LatLng(1.3127621,-77.4292481))
                        .strokeColor(Color.RED)
                        .strokeWidth(3);

                camara = new LatLng(1.3112927369,-77.4290443473);
                mMap.addPolygon(pOpRoSD);
                sector = 'd';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,17));
                break;
            case "Sector ROE":
                pOpRoSE = new PolygonOptions()
                        .add(new LatLng(1.3109708,-77.4279606),
                                new LatLng(1.309555,-77.4290335),
                                new LatLng(1.3060261,-77.4280787),
                                new LatLng(1.3069593,-77.4265873))
                        .strokeColor(Color.WHITE)
                        .strokeWidth(3);

                camara = new LatLng(1.3083330633,-77.4277680195);
                mMap.addPolygon(pOpRoSE);
                sector = 'e';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,17));
                break;
            case "Sector ROF":
                pOpRoSF = new PolygonOptions()
                        .add(new LatLng(1.310885,-77.4270594),
                                new LatLng(1.3101556,-77.4264801),
                                new LatLng(1.3095014,-77.4250317),
                                new LatLng(1.3107563,-77.4243128),
                                new LatLng(1.3134914,-77.4244845),
                                new LatLng(1.3126655,-77.4274671))
                        .strokeColor(Color.YELLOW)
                        .strokeWidth(3);

                camara = new LatLng(1.3115973722,-77.4254578159);
                mMap.addPolygon(pOpRoSF);
                sector = 'f';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,17));
                break;
            case "Sector ROG":
                pOpRoSG = new PolygonOptions()
                        .add(new LatLng(1.3097695,-77.4272418),
                                new LatLng(1.3078388,-77.4267375),
                                new LatLng(1.3066482,-77.4259651),
                                new LatLng(1.3071738,-77.4245274),
                                new LatLng(1.3092547,-77.424463),
                                new LatLng(1.310102,-77.427156))
                        .strokeColor(Color.LTGRAY)
                        .strokeWidth(3);

                camara = new LatLng(1.3083412288,-77.4255030401);
                mMap.addPolygon(pOpRoSG);
                sector = 'g';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,17));
                break;
            case "Sector ROH":
                pOpRoSH = new PolygonOptions()
                        .add(new LatLng(1.3094155,-77.4364042),
                                new LatLng(1.3073991,-77.4356639),
                                new LatLng(1.3064873,-77.4318552),
                                new LatLng(1.3075278,-77.4314904),
                                new LatLng(1.3083537,-77.4343765),
                                new LatLng(1.3098017,-77.4349773))
                        .strokeColor(Color.GREEN)
                        .strokeWidth(3);

                camara = new LatLng(1.3079417483,-77.4337338470);
                mMap.addPolygon(pOpRoSH);
                sector = 'h';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,16));
                break;
            case "Sector ROI":
                pOpRoSI = new PolygonOptions()
                        .add(new LatLng(1.3075439,-77.4314046),
                                new LatLng(1.3059939,-77.431941),
                                new LatLng(1.3024007,-77.4245167),
                                new LatLng(1.3042885,-77.4234116),
                                new LatLng(1.3059296,-77.4246669),
                                new LatLng(1.3043636,-77.426995))
                        .strokeColor(Color.parseColor("#6C3483"))
                        .strokeWidth(3);

                camara = new LatLng(1.3053338186,-77.4272064855);
                mMap.addPolygon(pOpRoSI);
                sector = 'i';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,15));
                break;
            case "Sector TA":
                pOpTSA = new PolygonOptions()
                        .add(new LatLng(1.3576527,-77.3159033),
                                new LatLng(1.3573041,-77.3154688),
                                new LatLng(1.3572425,-77.3155144),
                                new LatLng(1.3568563,-77.3148841),
                                new LatLng(1.3567491,-77.3149699),
                                new LatLng(1.3564219,-77.3145086),
                                new LatLng(1.3570172,-77.3140794),
                                new LatLng(1.3573176,-77.3145515),
                                new LatLng(1.3579289,-77.3141116),
                                new LatLng(1.3585725,-77.3149431))
                        .strokeColor(Color.BLUE)
                        .strokeWidth(3);

                camara = new LatLng(1.3575504606,-77.3146626446);
                mMap.addPolygon(pOpTSA);
                sector = 'a';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,18));
                break;
            case "Sector TB":
                pOpTSB = new PolygonOptions()
                        .add(new LatLng(1.3564702,-77.3172873),
                                new LatLng(1.3549525,-77.3163754),
                                new LatLng(1.3564434,-77.3145783),
                                new LatLng(1.3567491,-77.3149967),
                                new LatLng(1.356851,-77.3149243),
                                new LatLng(1.3572425,-77.3155466),
                                new LatLng(1.3573041,-77.3155037),
                                new LatLng(1.3575777,-77.3158604))
                        .strokeColor(Color.CYAN)
                        .strokeWidth(3);

                camara = new LatLng(1.3563947307,-77.3158753235);
                mMap.addPolygon(pOpTSB);
                sector = 'b';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,17));
                break;
            case "Sector TC":
                pOpTSC = new PolygonOptions()
                        .add(new LatLng(1.3564568,-77.3173168),
                                new LatLng(1.3570387,-77.3176575),
                                new LatLng(1.3555934,-77.3194385),
                                new LatLng(1.3551322,-77.3192346),
                                new LatLng(1.3551348,-77.3191944),
                                new LatLng(1.3539282,-77.3185319),
                                new LatLng(1.3541105,-77.3172122),
                                new LatLng(1.3549525,-77.3164022))
                        .strokeColor(Color.MAGENTA)
                        .strokeWidth(3);

                camara = new LatLng(1.3552557505,-77.3177579908);
                mMap.addPolygon(pOpTSC);
                sector = 'c';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,17));
                break;
            case "Sector TD":
                pOpTSD = new PolygonOptions()
                        .add(new LatLng(1.3554834,-77.3204416),
                                new LatLng(1.3549311,-77.3211604),
                                new LatLng(1.3543411,-77.3209646),
                                new LatLng(1.3543921,-77.3207742),
                                new LatLng(1.3532525,-77.3202753),
                                new LatLng(1.3539845,-77.3185855),
                                new LatLng(1.3551161,-77.3192024),
                                new LatLng(1.3551107,-77.31924),
                                new LatLng(1.3558642,-77.3195565))
                        .strokeColor(Color.RED)
                        .strokeWidth(3);

                camara = new LatLng(1.3545824123,-77.3198584042);
                mMap.addPolygon(pOpTSD);
                sector = 'd';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,17));
                break;
            case "Sector TE":
                pOpTSE = new PolygonOptions()
                        .add(new LatLng(1.3542526,-77.3229522),
                                new LatLng(1.3532632,-77.3226196),
                                new LatLng(1.3533061,-77.3217049),
                                new LatLng(1.3527001,-77.3215038),
                                new LatLng(1.3531238,-77.3202324),
                                new LatLng(1.3543411,-77.3207742),
                                new LatLng(1.3542714,-77.3210424),
                                new LatLng(1.354856,-77.3212624),
                                new LatLng(1.3546307,-77.3224533))
                        .strokeColor(Color.WHITE)
                        .strokeWidth(3);

                camara = new LatLng(1.3536829529,-77.3215266752);
                mMap.addPolygon(pOpTSE);
                sector = 'e';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,17));
                break;
            case "Sector MA":
                pOpMSA = new PolygonOptions()
                        .add(new LatLng(1.3744949,-77.3325115),
                                new LatLng(1.3737119,-77.3357946),
                                new LatLng(1.373122,-77.3365617),
                                new LatLng(1.3725804,-77.3360145),
                                new LatLng(1.3739801,-77.3322541))
                        .strokeColor(Color.BLUE)
                        .strokeWidth(3);

                camara = new LatLng(1.3740369080,-77.3344260710);
                mMap.addPolygon(pOpMSA);
                sector = 'a';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,16));
                break;
            case "Sector MB":
                pOpMSB = new PolygonOptions()
                        .add(new LatLng(1.373004,-77.3365134),
                                new LatLng(1.3727091,-77.3371196),
                                new LatLng(1.3718778,-77.337538),
                                new LatLng(1.3714649,-77.3366314),
                                new LatLng(1.3725911,-77.3360735))
                        .strokeColor(Color.CYAN)
                        .strokeWidth(3);

                camara = new LatLng(1.3722569088,-77.3367371520);
                mMap.addPolygon(pOpMSB);
                sector = 'b';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,18));
                break;
            case "Sector MC":
                pOpMSC = new PolygonOptions()
                        .add(new LatLng(1.3718778,-77.337538),
                                new LatLng(1.373181,-77.3368996),
                                new LatLng(1.3739479,-77.3372805),
                                new LatLng(1.3737226,-77.3386592),
                                new LatLng(1.3724785,-77.3390025),
                                new LatLng(1.3721299,-77.3380637))
                        .strokeColor(Color.MAGENTA)
                        .strokeWidth(3);

                camara = new LatLng(1.3730214330,-77.3378576761);
                mMap.addPolygon(pOpMSC);
                sector = 'c';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,17));
                break;
            case "Sector MD":
                pOpMSD = new PolygonOptions()
                        .add(new LatLng(1.3718778,-77.337538),
                                new LatLng(1.3709876,-77.3379564),
                                new LatLng(1.3705425,-77.3370552),
                                new LatLng(1.3710198,-77.3365831),
                                new LatLng(1.3714649,-77.3366314))
                        .strokeColor(Color.RED)
                        .strokeWidth(3);

                camara = new LatLng(1.3712122535,-77.3371690207);
                mMap.addPolygon(pOpMSD);
                sector = 'd';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,18));
                break;
            case "Sector ME":
                pOpMSE = new PolygonOptions()
                        .add(new LatLng(1.3718778,-77.337538),
                                new LatLng(1.3721299,-77.3380637),
                                new LatLng(1.3723819,-77.3387825),
                                new LatLng(1.3713952,-77.3389971),
                                new LatLng(1.3709876,-77.3379564))
                        .strokeColor(Color.WHITE)
                        .strokeWidth(3);

                camara = new LatLng(1.3716908106,-77.3382078399);
                mMap.addPolygon(pOpMSE);
                sector = 'e';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,18));
                break;
            case "Sector MF":
                pOpMSF = new PolygonOptions()
                        .add(new LatLng(1.3707462,-77.3374736),
                                new LatLng(1.3709876,-77.3379564),
                                new LatLng(1.3712423,-77.3385975),
                                new LatLng(1.3708428,-77.3386109),
                                new LatLng(1.370269,-77.3382273),
                                new LatLng(1.3702663,-77.3380369),
                                new LatLng(1.3700464,-77.3374978))
                        .strokeColor(Color.YELLOW)
                        .strokeWidth(3);

                camara = new LatLng(1.3705936308,-77.3379627252);
                mMap.addPolygon(pOpMSF);
                sector = 'f';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,18));
                break;
            case "Sector MG":
                pOpMSG = new PolygonOptions()
                        .add(new LatLng(1.3707462,-77.3374736),
                                new LatLng(1.3700464,-77.3374978),
                                new LatLng(1.3695771,-77.3374093),
                                new LatLng(1.3695181,-77.3366958),
                                new LatLng(1.370344,-77.3367012))
                        .strokeColor(Color.LTGRAY)
                        .strokeWidth(3);

                camara = new LatLng(1.3700800572,-77.3370406273);
                mMap.addPolygon(pOpMSG);
                sector = 'g';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,18));
                break;
            case "Sector MH":
                pOpMSH = new PolygonOptions()
                        .add(new LatLng(1.370344,-77.3367012),
                                new LatLng(1.3694967,-77.3366636),
                                new LatLng(1.3695637,-77.337428),
                                new LatLng(1.3694297,-77.3377955),
                                new LatLng(1.36877,-77.3378143),
                                new LatLng(1.3689711,-77.3364061),
                                new LatLng(1.3693841,-77.3359931),
                                new LatLng(1.3701081,-77.3355049),
                                new LatLng(1.3706068,-77.3363686))
                        .strokeColor(Color.GREEN)
                        .strokeWidth(3);

                camara = new LatLng(1.3693965894,-77.3366930573);
                mMap.addPolygon(pOpMSH);
                sector = 'h';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,17));
                break;
        }
        addMarcador();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    public void backupDatabase(){
        try {

            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            String packageName  = "com.android.gan091.gramaudenar";
            String sourceDBName = "GramaDB";
            String targetDBName = "Grama/gramaUdenar";
            if (sd.canWrite()) {
                //Date now = new Date();
                String currentDBPath = "data/" + packageName + "/databases/" + sourceDBName;
                //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
                String backupDBPath = targetDBName + ".db";

                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                Log.i("backup","backupDB=" + backupDB.getAbsolutePath());
                Log.i("backup","sourceDB=" + currentDB.getAbsolutePath());

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            }
        } catch (Exception e) {
            Log.i("Backup", e.toString());
        }
    }

    public void generarArchivoT(){
        int i=1;
        double x,y;
        bdP.abrirBD();
        Cursor cursor = bdP.cargarDatos();
        archivoCad = new String[cursor.getCount()+2];//5
        archivoCad[0] = "LATITUD LONGITUD LUGAR TONDADECHOQUE TENTERRAMIENTO TLAHARES TCAIDADECENIZA";

        if (cursor.moveToFirst()){
            do {
                x = degUtmLat(cursor.getDouble(0),cursor.getDouble(1),1);
                y = degUtmLat(cursor.getDouble(0),cursor.getDouble(1),2);
                archivoCad[i] = Double.toString(x)+" "+Double.toString(y)+" "+cursor.getString(2)+" "+cursor.getString(3)+" "+cursor.getString(4)+" "+cursor.getString(5)+" "+cursor.getString(6);
                i++;

            }while (cursor.moveToNext());
            guardarArchivo(archivoCad,"Tipologias");
        }
        bdP.cerrarBD();
    }

    public void generarArchivoF(){
        int i=1;
        double x,y;
        bdP.abrirBD();
        Cursor cursor = bdP.cargarDatos("tblFachada");
        archivoCad = new String[cursor.getCount()+2];
        archivoCad[0] = "LATITUD,LONGITUD,ANCHO,ALTURA,AREA,AREA1";

        if (cursor.moveToFirst()){
            do {
                x = degUtmLat(cursor.getDouble(0),cursor.getDouble(1),1);
                y = degUtmLat(cursor.getDouble(0),cursor.getDouble(1),2);
                archivoCad[i] = Double.toString(x)+","+Double.toString(y)+","+cursor.getString(2)+","+cursor.getString(3)+","+cursor.getString(4)+","+cursor.getString(5);
                i++;

            }while (cursor.moveToNext());
            guardarArchivo(archivoCad,"Fachada");
        }
        bdP.cerrarBD();
    }

    public void generarArchivoV(){
        int i=1;
        double x,y;
        bdP.abrirBD();
        Cursor cursor = bdP.cargarDatos("tblVentana");
        archivoCad = new String[cursor.getCount()+2];
        archivoCad[0] = "LATITUD,LONGITUD,ANCHO,ALTURA,AREA,NUMEROPISO";

        if (cursor.moveToFirst()){
            do {
                x = degUtmLat(cursor.getDouble(0),cursor.getDouble(1),1);
                y = degUtmLat(cursor.getDouble(0),cursor.getDouble(1),2);
                archivoCad[i] = Double.toString(x)+","+Double.toString(y)+","+cursor.getString(2)+","+cursor.getString(3)+","+cursor.getString(4)+","+cursor.getString(5);
                i++;

            }while (cursor.moveToNext());
            guardarArchivo(archivoCad,"Ventana");
        }
        bdP.cerrarBD();
    }

    public void generarArchivoP(){
        int i=1;
        bdP.abrirBD();
        Cursor cursor = bdP.cargarDatos("tblPuerta");
        archivoCad = new String[cursor.getCount()+2];
        archivoCad[0] = "LATITUD,LONGITUD,ANCHO,ALTURA,AREA";

        if(cursor.moveToFirst()){
            do {
                archivoCad[i] = cursor.getString(0)+","+cursor.getString(1)+","+cursor.getString(2)+","+cursor.getString(3)+","+cursor.getString(4);
                i++;

            }while (cursor.moveToNext());
            guardarArchivo(archivoCad,"Puerta");
        }
        bdP.cerrarBD();
    }

    public void generarArchivoL(){
        int i=1;
        double x,y;
        bdP.abrirBD();
        Cursor cursor = bdP.cargarDatos("tblLahares");
        archivoCad = new String[cursor.getCount()+2];
        archivoCad[0] = "LATITUD,LONGITUD,REFORZADO,MATERIAL MUROS,ESTADO EDIFICACION,TIPOLOGIA LAHARES,OBSERVACIONES";

        if (cursor.moveToFirst()){
            do {
                x = degUtmLat(cursor.getDouble(0),cursor.getDouble(1),1);
                y = degUtmLat(cursor.getDouble(0),cursor.getDouble(1),2);
                boolean reforzado;
                if (cursor.getInt(2)==1){
                    reforzado = true;
                }
                else {
                    reforzado = false;
                }
                archivoCad[i] = Double.toString(x)+","+Double.toString(y)+","+reforzado+","+cursor.getString(3)+","+cursor.getString(4)+","+cursor.getString(5)+","+cursor.getString(6);
                i++;

            }while (cursor.moveToNext());
            guardarArchivo(archivoCad,"Lahares");
        }
        bdP.cerrarBD();
    }

    public void generarArchivoOCH(){
        int i=1;
        double x,y;
        bdP.abrirBD();
        Cursor cursor = bdP.cargarDatos("tblOndaChoque");
        archivoCad = new String[cursor.getCount()+2];
        archivoCad[0] = "LATITUD,LONGITUD,MATERIAL VENTANA,MARCO VENTANA,MATERIAL PISO,MATERIAL MUROS,TIPOLOGIA ONDA DE CHOQUE,TIPOLOGIA ENTERRAMIENTO,OBSERVACIONES";

        if (cursor.moveToFirst()){
            do {
                x = degUtmLat(cursor.getDouble(0),cursor.getDouble(1),1);
                y = degUtmLat(cursor.getDouble(0),cursor.getDouble(1),2);
                archivoCad[i] = Double.toString(x)+","+Double.toString(y)+","+cursor.getString(2)+","+cursor.getString(3)+","+cursor.getString(4)+","+cursor.getString(5)+","+cursor.getString(6)+","+cursor.getString(7)+","+cursor.getString(8);
                i++;

            }while (cursor.moveToNext());
            guardarArchivo(archivoCad,"OndaChoqueEnterramiento");
        }
        bdP.cerrarBD();
    }

    public void generarArchivoC(){
        int i=1;
        double x,y;
        bdP.abrirBD();
        Cursor cursor = bdP.cargarDatos("tblCeniza");
        archivoCad = new String[cursor.getCount()+2];
        archivoCad[0] = "LATITUD,LONGITUD,TIPO TECHO,MATERIAL COBERTURA,MATERIAL APOYO,FORMA CUBIERTA,INCLINACION CUBIERTA,ESTADO GENERAL,TIPOLOGIA CENIZA,OBSERVACIONES";

        if (cursor.moveToFirst()){
            do {
                x = degUtmLat(cursor.getDouble(0),cursor.getDouble(1),1);
                y = degUtmLat(cursor.getDouble(0),cursor.getDouble(1),2);
                archivoCad[i] = Double.toString(x)+","+Double.toString(y)+","+cursor.getString(2)+","+cursor.getString(3)+","+cursor.getString(4)+","+cursor.getString(5)+","+cursor.getString(6)+","+cursor.getString(7)+","+cursor.getString(8)+","+cursor.getString(9);
                i++;
            }while (cursor.moveToNext());
            guardarArchivo(archivoCad,"CaidadeCeniza");
        }
        bdP.cerrarBD();
    }

    public double degUtmLat(double deglat,double deglon, int tipo){
        System.out.println("Longitud_Decimales= "+deglon);
        System.out.println("Latitud_Decimales= "+deglat);
        double radlat=Math.toRadians(deglat);
        double radlon=Math.toRadians(deglon);
        System.out.println("Longitud_Radianes= "+radlon);
        System.out.println("Latitud_Radianes= "+radlat);
        double cos_2_radlat=0.5+((Math.cos(2*radlat))/2);
        System.out.println("Cos2radlat= "+cos_2_radlat);
        double ex=0.08199189;
        double ex_sec=0.08226889;
        double ex_2=ex_sec*ex_sec;
        System.out.println("E2= "+ex_2);
        double c=6399936.608;
        double alpha=0.003367003;
        int hus= (int) ((deglon/6)+31);
        System.out.println("Huso= "+hus);
        double lambda_0=(hus*6)-183;
        System.out.println("Lambda_0= "+lambda_0);
        double delta_lambda=radlon-(Math.toRadians(lambda_0));
        System.out.println("Delta Lambda= "+delta_lambda);
        double A=Math.cos(radlat)*Math.sin(delta_lambda);
        System.out.println("A= "+A);
        double xi=0.5*(Math.log((1+A)/(1-A)));
        System.out.println("Xi= "+xi);
        double eta=Math.atan((Math.tan(radlat)/Math.cos(delta_lambda)))-radlat;
        System.out.println("eta= "+eta);
        double ni=(c/(Math.sqrt(1+(ex_2*cos_2_radlat))))*0.9996;
        System.out.println("ni= "+ni);
        double zeta=ex_2/2*Math.pow(xi,2)*cos_2_radlat;
        System.out.println("zeta= "+zeta);
        double A_1=Math.sin(2*radlat);
        System.out.println("A_1= "+A_1);
        double A_2=A_1*cos_2_radlat;
        System.out.println("A_2= "+A_2);
        double J_2=radlat+(A_1/2);
        System.out.println("J_2= "+J_2);
        double J_4=((3*J_2)+A_2)/4;
        System.out.println("J_4= "+J_4);
        double J_6=((5*J_4)+(A_2*cos_2_radlat))/3;
        System.out.println("J_6= "+J_6);
        double alpha_1=ex_2*3/4;
        System.out.println("alpha= "+alpha_1);
        double beta= (5*Math.pow(alpha_1,2))/3;
        System.out.println("beta= "+beta);
        double gamma= (35*Math.pow(alpha_1,3))/27;
        System.out.println("gamma= "+gamma);
        double B_0=0.9996*c*(radlat-(alpha_1*J_2)+(beta*J_4)-(gamma*J_6));
        System.out.println("B_0= "+B_0);
        double utmlon=(xi*ni*(1+(zeta/3)))+500000;
        double utmlat=(eta*ni*(1+zeta))+B_0;
        System.out.println("Lat= "+utmlat+" Lon= "+utmlon);
        if(tipo==1){
            return utmlat;
        }
        else{
            return utmlon;
        }
    }

    //fachada
    //ventana
    //Puerta
    //Tipologias
    //OndadeChoque
    //Lahares
    //Ceniza

    public void guardarArchivo(String[] cad, String tabla){
        File sd = Environment.getExternalStorageDirectory();

        try {
            File folder = new File(sd.getAbsolutePath()+"/Grama");
            folder.mkdir();
            FileWriter archivo = new FileWriter(folder.getAbsolutePath()+"/tbl"+tabla+" "+spSector.getSelectedItem().toString()+".txt");

            BufferedWriter bufferedWriter = new BufferedWriter(archivo);

            for (int i=0; i<cad.length;i++){
                bufferedWriter.write(cad[i]);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
            archivo.close();
        }
        catch (Exception e){
            Log.i("Escritura",e.toString());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}