package com.android.gan091.gramaudenar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class VentanaActivity extends AppCompatActivity {

    boolean existe = false;
    String latitud,longitud;
    float ancho,alto,areaLocal;
    int numeroPiso;

    AdaptadorVentana adaptador;
    ArrayList<EditText> listaObjetosA = new ArrayList<>();
    ArrayList<EditText> listaObjetosAl = new ArrayList<>();
    ArrayList<EditText> listaObjetosNumP = new ArrayList<>();
    ArrayList<Fuente> lista = new ArrayList<>();
    BaseDeDatos bdP;
    Context context;
    RecyclerView contenedor;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventana);

        context = this;
        bdP = new BaseDeDatos(context);

        Bundle bundle = getIntent().getExtras();

        latitud = bundle.getString("c1");
        longitud = bundle.getString("c2");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarV);
        setSupportActionBar(toolbar);

        if (cargarDatos()==false){
            lista.add(new Fuente(0,0,0));
        }

        contenedor = (RecyclerView) findViewById(R.id.contenedorVentana);
        contenedor.setHasFixedSize(true);
        LinearLayoutManager layout = new LinearLayoutManager(getApplicationContext());
        layout.setOrientation(LinearLayoutManager.VERTICAL);
        adaptador = new AdaptadorVentana(lista);
        contenedor.setAdapter(adaptador);
        contenedor.setLayoutManager(layout);

        loadSwipe();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_addV:
                adaptador.vaciarLista();
                lista.add(new Fuente(0,0,0));
                adaptador.notifyDataSetChanged();
                break;
            case R.id.action_saveV:
                obtenerArea();
                finish();
                break;
            case R.id.action_exitV:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_ventana, menu);
        return true;
    }

    public boolean cargarDatos(){
        boolean cargar = false;
        bdP.abrirBD();
        final Cursor cursor = bdP.cargarDatos("Ancho","Altura","NumeroPiso","tblVentana",latitud,longitud);

        try {
            if (cursor.moveToFirst()){
                existe = true;
                cargar = true;
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);

                builder1.setTitle("Registro Existente")
                        .setMessage("Ya hay registros de Fachada de esta casa ¿Desea modificar los registros?")
                        .setPositiveButton("Si",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        inicializar(cursor);
                                    }
                                })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                builder1.create().show();
            }
            else {
            }
        }
        catch (Exception e){
            Toast toast;
            toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
            toast.show();
        }
        bdP.cerrarBD();
        return cargar;
    }

    public void inicializar(Cursor cursor){
        float ancho, alto;
        int numP;
        do {
            ancho = (float) cursor.getDouble(0);
            alto = (float) cursor.getDouble(1);
            numP = cursor.getInt(2);
            lista.add(new Fuente(ancho,alto,numP));
        }while (cursor.moveToNext());
        adaptador.notifyDataSetChanged();
    }

    public void loadSwipe(){
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                lista.remove(viewHolder.getAdapterPosition());
                adaptador.vaciarLista();
                adaptador.notifyDataSetChanged();
            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(contenedor);
    }

    public void obtenerArea(){
        if(existe){
            try {
                bdP.eliminarRegistro("tblVentana",latitud,longitud);
            }
            catch (Exception e){
                Toast toast;
                toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
                toast.show();
            }
        }

        listaObjetosA = adaptador.getListaEA();
        listaObjetosAl = adaptador.getListaEAl();
        listaObjetosNumP = adaptador.getListaENumP();

        for (int i=0;i<listaObjetosA.size();i++){
            ancho = Float.parseFloat(listaObjetosA.get(i).getText().toString());
            alto = Float.parseFloat(listaObjetosAl.get(i).getText().toString());
            numeroPiso = Integer.parseInt(listaObjetosNumP.get(i).getText().toString());
            areaLocal = ancho * alto;

            try {
                bdP.insertar(latitud,longitud,ancho,alto,areaLocal,numeroPiso);
            }
            catch (Exception e){
                Toast toast;
                toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
}
