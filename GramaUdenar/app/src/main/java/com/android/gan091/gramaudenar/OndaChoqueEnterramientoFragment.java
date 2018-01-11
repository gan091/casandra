package com.android.gan091.gramaudenar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.Float2;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OndaChoqueEnterramientoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OndaChoqueEnterramientoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OndaChoqueEnterramientoFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    String latitud,longitud,tipOCHAnterior,tipOCHActual;
    boolean inicio = true;
    float areaAcFac,areaAcVen,areaAcP,porcentajeAberturas=-1,puntajeAb,puntajeCar,pAb,pCar,resultado;
    int nPisos;

    ArrayAdapter<String> aaMVentana,aaMatVentana,aaMatPisos,aaTO,aaTE;
    BaseDeDatos bdP;
    Button btnFac,btnVentana,btnPuerta,btnGenerar;
    Context context;
    EditText etPCE, etPAOCH, etPAE, etResultadoE,etObservaciones;
    Spinner spMarVen,spMatVen,spMatPisos,spTipOCH,spTipE;

    String [] opcMarVentana = new String[] {"Madera","Aluminio","Hierro Forjado"};
    String [] opcMatVentana = new String[] {"Vidrio","Madera"};
    String [] opcTipOCh = new String[] {"Tipologia_1","Tipologia_2","Tipologia_3"};
    String [] opcTipE = new String[] {"Tipologia_1","Tipologia_2"};
    String [] opcMatPisos = new String[] {"Tierra","Madera","Concreto","Enchape"};

    String materialVen,marcoVentana,materialPisos,materialMuros,tipOndaChoque,tipEnterramiento,observaciones;
    TextView tvA,tvA1,tvAlt;

    public OndaChoqueEnterramientoFragment() {

        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OndaChoqueEnterramientoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OndaChoqueEnterramientoFragment newInstance(String param1, String param2) {
        OndaChoqueEnterramientoFragment fragment = new OndaChoqueEnterramientoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onClick(View view) {
        Intent i;
        Toast toast;
        Bundle b1 = new Bundle();
        b1.putString("c1",latitud);
        b1.putString("c2",longitud);

        switch (view.getId()){
            case R.id.btnGenerar:
                guardarBD();
                break;
            case R.id.btnFachViv:
                i = new Intent(getActivity(), FachadaActivity.class);
                i.putExtras(b1);
                startActivity(i);
                break;
            case R.id.btnVen:
                i = new Intent(getActivity(), VentanaActivity.class);
                i.putExtras(b1);
                startActivity(i);
                break;
            case R.id.btnPuertas:
                i = new Intent(getActivity(), PuertaActivity.class);
                i.putExtras(b1);
                startActivity(i);
                break;
            case R.id.tvPAb:
                toast=Toast.makeText(getActivity(),"Porcentaje segun Caracteristicas",Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.tvAb1:
                toast=Toast.makeText(getActivity(),"Porcentaje segun Aberturas (Puertas y Ventanas)",Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.tvAlt:
                toast=Toast.makeText(getActivity(),"Porcentaje segun Altura (N° de Pisos)",Toast.LENGTH_SHORT);
                toast.show();
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        context = getContext();
        bdP = new BaseDeDatos(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_onda_choque_enterramiento, container, false);

        spMarVen = (Spinner) v.findViewById(R.id.spVentana);
        spMatVen = (Spinner) v.findViewById(R.id.spMatVentana);
        spTipOCH = (Spinner) v.findViewById(R.id.spTipOn);
        spTipE = (Spinner) v.findViewById(R.id.spTipEnt);
        spMatPisos = (Spinner) v.findViewById(R.id.spMatPiso);

        btnFac = (Button) v.findViewById(R.id.btnFachViv);
        btnVentana = (Button) v.findViewById(R.id.btnVen);
        btnPuerta = (Button) v.findViewById(R.id.btnPuertas);
        btnGenerar = (Button) v.findViewById(R.id.btnGenerar);

        tvA = (TextView) v.findViewById(R.id.tvPAb);
        tvA1 = (TextView) v.findViewById(R.id.tvAb1);
        tvAlt = (TextView) v.findViewById(R.id.tvAlt);

        aaMVentana = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,opcMarVentana);
        aaMatVentana = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,opcMatVentana);
        aaTO = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,opcTipOCh);
        aaTE = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,opcTipE);
        aaMatPisos = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,opcMatPisos);

        spMarVen.setAdapter(aaMVentana);
        spMatVen.setAdapter(aaMatVentana);
        spTipOCH.setAdapter(aaTO);
        spTipE.setAdapter(aaTE);
        spMatPisos.setAdapter(aaMatPisos);

        btnFac.setOnClickListener(this);
        btnVentana.setOnClickListener(this);
        btnPuerta.setOnClickListener(this);
        btnGenerar.setOnClickListener(this);

        tvA.setOnClickListener(this);
        tvA1.setOnClickListener(this);
        tvAlt.setOnClickListener(this);

        etPAOCH = (EditText) v.findViewById(R.id.etPAb);

        etPCE = (EditText) v.findViewById(R.id.etPCE);
        etPAE = (EditText) v.findViewById(R.id.etPAE);
        etResultadoE = (EditText) v.findViewById(R.id.etRE);
        etObservaciones = (EditText) v.findViewById(R.id.etObservaciones);

        Bundle bundle = getActivity().getIntent().getExtras();

        latitud = bundle.getString("c1");
        longitud = bundle.getString("c2");

        //btnPuerta.setEnabled(false);

        cargarDatos();

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void cargarDatos(){
        bdP.abrirBD();
        Cursor cursor = bdP.cargarDatosTablas(latitud,longitud,"tblOndaChoque");

        if (cursor.getCount()>0){
            cursor.moveToFirst();

            do {
                setSpinner(cursor.getString(2),1);
                setSpinner(cursor.getString(3),2);
                setSpinner(cursor.getString(4),3);
                etObservaciones.setText(cursor.getString(8));
            }while (cursor.moveToNext());

            tipificacionOndaChoque();
            tipificacionEnterramiento();
        }
        bdP.cerrarBD();
    }

    public void guardarBD(){
        //inicio = true;
        tipificacionOndaChoque();
        tipificacionEnterramiento();
        marcoVentana = spMarVen.getSelectedItem().toString();
        materialVen = spMatVen.getSelectedItem().toString();
        materialPisos = spMatPisos.getSelectedItem().toString();
        //materialMuros = spMatMuros.getSelectedItem().toString();
        observaciones = etObservaciones.getText().toString();

        if (bdP.existeRegistro("tblOndaChoque",latitud,longitud)){
            try {
                bdP.eliminarRegistro("tblOndaChoque",latitud,longitud);
            }
            catch (Exception e){
                Toast toast;
                toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
                toast.show();
            }
        }

        try {
            bdP.insertarOndaChoqueEnterramiento(latitud,longitud,materialVen,marcoVentana,materialPisos,materialMuros,tipOndaChoque,tipEnterramiento,observaciones);
            //Log.i("Tipologia",tipEnterramiento);
            inicio = false;
            Toast toast;
            toast=Toast.makeText(context,"Tipologia Onda de Choque - Guardado Exitoso",Toast.LENGTH_SHORT);
            toast.show();
        }
        catch (Exception e){
            Toast toast;
            toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void setSpinner(String opc, int tipo){

        switch (tipo){
            case 1:
                switch (opc){
                    case "Vidrio":
                        spMatVen.setSelection(0);
                        break;
                    case "Madera":
                        spMatVen.setSelection(1);
                        break;
                }
                break;
            case 2:
                switch (opc){
                    case "Madera":
                        spMarVen.setSelection(0);
                        break;
                    case "Aluminio":
                        spMarVen.setSelection(1);
                        break;
                    case "Hierro Forjado":
                        spMarVen.setSelection(2);
                        break;
                }
                break;
            case 3:
                switch (opc){
                    case "Tierra":
                        spMatPisos.setSelection(0);
                        break;
                    case "Madera":
                        spMatPisos.setSelection(1);
                        break;
                    case "Concreto":
                        spMatPisos.setSelection(2);
                        break;
                    case "Enchape":
                        spMatPisos.setSelection(3);
                        break;
                }
                break;
        }
    }

    public void tipificacionEnterramiento(){
        float alturaF=0,areaAcFac=0,areaAcP=0,areaAcVen=0;
        bdP.abrirBD();
        Cursor cursor = bdP.cargarDatosTablas(latitud,longitud,"tblFachada");

        Cursor cursor1 = bdP.cargarDatosTablas(latitud,longitud,"tblPuerta");
        cursor1.moveToFirst();
        Cursor cursor2 = bdP.cargarDatosTablas1(latitud,longitud,"tblVentana");

        if (cursor.moveToFirst() && cursor2.moveToFirst()){
            do {
                areaAcFac = areaAcFac + cursor.getFloat(5);
                alturaF = cursor.getFloat(3);
            }while (cursor.moveToNext());


            do {
                areaAcP = areaAcP + cursor1.getFloat(4);
            }while (cursor1.moveToNext());


            do {
                areaAcVen = areaAcVen + cursor2.getFloat(4);
            }while (cursor2.moveToNext());
        }

        nPisos = (int) (alturaF/2.8)+1;
        Log.i("nPisos---->",Integer.toString(nPisos));
        Log.i("aFachada---->",Float.toString(areaAcFac));
        Log.i("aVentana---->",Float.toString(areaAcVen));
        Log.i("aPuerta---->",Float.toString(areaAcP));

        porcentajeAberturas = ((areaAcVen+areaAcP)/areaAcFac)*100;

        Log.i("pAberturas---->",Float.toString(porcentajeAberturas));

        if(porcentajeAberturas<10){
            puntajeAb = (float) 1;
        }
        else if (porcentajeAberturas>50){
            puntajeAb = (float) 10;
        }
        else {
            if (porcentajeAberturas>=10 && porcentajeAberturas<=50){
                puntajeAb = (float) 5;
            }
            else {
                puntajeAb = 0;
            }
        }

        if(nPisos == 1){
            puntajeCar = (float) 10;
        }
        else{
            if (nPisos > 1){
                puntajeCar = (float) 5;
            }
            else {
                puntajeCar = (float) 0;
            }
        }

        pAb = (float)(puntajeAb * 0.7);
        pCar = (float)(puntajeCar * 0.3);

        resultado = pAb + pCar;

        etPCE.setText(Float.toString(bdP.reducirFloat(pCar)));
        etPAE.setText(Float.toString(bdP.reducirFloat(pAb)));
        etResultadoE.setText(Float.toString(bdP.reducirFloat(resultado)));

        if(resultado<5){
            spTipE.setSelection(1);
        }
        else if (resultado>10){

        }
        else {
            if (puntajeAb==0){
                spTipE.setSelection(1);
            }
            else {
                spTipE.setSelection(0);
            }
        }
        tipEnterramiento = spTipE.getSelectedItem().toString();
        bdP.cerrarBD();
    }

    public void tipificacionOndaChoque(){
        bdP.abrirBD();

        Cursor cursor = bdP.cargarDatosTablas(latitud,longitud,"tblFachada");
        Cursor cursor1 = bdP.cargarDatosTablas(latitud,longitud,"tblVentana");

        if(cursor.moveToFirst() && cursor1.moveToFirst()){
            do {
                areaAcFac = areaAcFac + cursor.getFloat(4);
            }while (cursor.moveToNext());

            do {
                areaAcVen = areaAcVen + cursor1.getFloat(4);
            }while (cursor1.moveToNext());
        }

        porcentajeAberturas = (areaAcVen/areaAcFac)*100;

        if (porcentajeAberturas>100){
            porcentajeAberturas=100;
        }

        etPAOCH.setText(Float.toString(bdP.reducirFloat(porcentajeAberturas)));

        if(porcentajeAberturas<10 && porcentajeAberturas>=0){
            spTipOCH.setSelection(2);
        }
        else{
            if (porcentajeAberturas>50) {
                spTipOCH.setSelection(0);
            }
            else{
                if (porcentajeAberturas>=10 && porcentajeAberturas<=50) {
                    spTipOCH.setSelection(1);
                }
                else {
                    spTipOCH.setSelection(2);
                }
            }
        }

        tipOndaChoque = spTipOCH.getSelectedItem().toString();
        tipOCHAnterior = tipOndaChoque;
        bdP.cerrarBD();
    }
}