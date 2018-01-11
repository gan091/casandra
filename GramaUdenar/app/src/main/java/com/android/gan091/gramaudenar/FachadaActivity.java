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

public class FachadaActivity extends AppCompatActivity {

    //Intancia de variables a utilizar

    boolean existe = false;
    String latitud,longitud;
    float ancho,areaLocal, areaLocal1;
    int altura;

    AdaptadorFachada adaptador;
    ArrayList<EditText> listaObjetos = new ArrayList<>();
    ArrayList<Fuente> lista = new ArrayList<>();
    BaseDeDatos bdP;
    Context context;
    EditText etAltura;
    RecyclerView contenedor;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fachada);

        context = this;
        bdP = new BaseDeDatos(context);

        Bundle bundle = getIntent().getExtras();

        latitud = bundle.getString("c1");
        longitud = bundle.getString("c2");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarF);
        setSupportActionBar(toolbar);

        etAltura = (EditText) findViewById(R.id.etAltura);

        if (cargarDatos()==false){
            lista.add(new Fuente(0));
        }

        contenedor = (RecyclerView) findViewById(R.id.contenedorFachada);
        contenedor.setHasFixedSize(true);
        LinearLayoutManager layout = new LinearLayoutManager(getApplicationContext());
        layout.setOrientation(LinearLayoutManager.VERTICAL);
        adaptador = new AdaptadorFachada(lista);
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
            case R.id.action_addF:
                adaptador.vaciarLista();
                adaptador.setCargar(false);
                lista.add(new Fuente(0));
                adaptador.notifyDataSetChanged();
                break;
            case R.id.action_saveF:
                obtenerArea();
                finish();
                break;
            case R.id.action_exitF:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_fachada, menu);
        return true;
    }

    public boolean cargarDatos(){
        boolean cargar = false;
        bdP.abrirBD();
        final Cursor cursor = bdP.cargarDatos("Ancho","Altura","tblFachada",latitud,longitud);

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
        float ancho;
        double alto;
        int x;
        adaptador.setCargar(true);
        do {
            ancho = (float) cursor.getDouble(0);
            lista.add(new Fuente(ancho));
            alto = cursor.getDouble(1);
            x = (int) (alto/2.8)+1;
            etAltura.setText(Integer.toString(x));
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

    public void obtenerArea() {
        if(existe){
            try {
                bdP.eliminarRegistro("tblFachada",latitud,longitud);
            }
            catch (Exception e){
                Toast toast;
                toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
                toast.show();
            }
        }
        //Calcula el area acumulativa de la Fachada
        altura = Integer.parseInt(etAltura.getText().toString());

        listaObjetos = adaptador.getListaE();

        for (int i = 0; i< listaObjetos.size(); i++){
            if (listaObjetos.get(i).getText().length()==0){
                ancho = 0;
            }
            else {
                ancho = Float.parseFloat(listaObjetos.get(i).getText().toString());
            }

            float aux = (float)(altura*2.8);
            areaLocal = ancho * (aux);
            areaLocal1 = (float)(ancho * 2.8);

            try {
                bdP.insertar(latitud,longitud,ancho,aux,areaLocal,areaLocal1);
            }
            catch (Exception e){
                Toast toast;
                toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
}
