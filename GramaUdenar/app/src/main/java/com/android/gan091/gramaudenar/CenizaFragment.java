package com.android.gan091.gramaudenar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CenizaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CenizaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CenizaFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    boolean existe = false, cambioTipologia = false;
    String latitud,longitud,tipCenizaAnterior,tipCenizaActual;
    float pMatTcho,pEstGenCub,pMatElemApoyo,pIncCub,resCeniza;
    int matTecho,estGenCub,matElemApoyo,angIncCub;

    ArrayAdapter<String> aaMatCob,aaMatElemAp,aaMorfCub,aaIncCub,aaEstGenCub,aaTipCeniza;
    BaseDeDatos bdP;
    Button btnGenerar;
    Context context;
    EditText etPM,etPIC,etPMEA,etPEGC,etResCeniza,etObservaciones;
    Spinner spMatCob,spMatElemAp,spMorfCub,spIncCub,spEstGenCub,spTipCeniza;
    String [] opcMatCob = new String[] {"Policarbonato","Teja Barro","Teja Eternit","Teja Traslucida","Teja Zinc"};
    String [] opcMatElemAp = new String[] {"Vigas Concreto","Vigas Madera","Vigas Metalicas"};
    String [] opcMorfCub = new String[] {"A dos Aguas","Cobertizo","Con Faldones"};
    String [] opcIncCub = new String[] {"Levemente Inclinada","Muy Inclinada"};
    String [] opcEstGenCub = new String[] {"Bueno","Regular","Malo"};
    String [] opcTipCeniza = new String[] {"Tipologia_1","Tipologia_2","Tipologia_3","Tipologia_4"};
    String tipoTecho,tipCeniza;
    Switch swTecho;
    TextView tvCubiertas,tvEstGenCub,tvMatElemApoyo,tvAngInclCub,tvMorfCub;

    public CenizaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CenizaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CenizaFragment newInstance(String param1, String param2) {
        CenizaFragment fragment = new CenizaFragment();
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onClick(View view) {
        Toast toast;

        switch (view.getId()){
            case R.id.btnGenerarC:
                guardarBD();
                break;
            case R.id.tvMorfEst:
                toast= Toast.makeText(getActivity(),"Forma de Cubiertas segun el Escurrimiento",Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.tvMaterial:
                toast= Toast.makeText(getActivity(),"Porcentaje segun Material (Resistencia de la Teja)",Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.tvIncCub:
                toast=Toast.makeText(getActivity(),"Porcentaje segun Inclinacion de la Cubierta",Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.tvMEA:
                toast=Toast.makeText(getActivity(),"Porcentaje segun Material del Elemento de Apoyo",Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.tvEstGenC:
                toast=Toast.makeText(getActivity(),"Porcentaje segun Estado General de la Cubierta",Toast.LENGTH_SHORT);
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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ceniza, container, false);

        btnGenerar = (Button) v.findViewById(R.id.btnGenerarC);

        spMatCob = (Spinner) v.findViewById(R.id.spMatCob);
        spMatElemAp = (Spinner) v.findViewById(R.id.spMatEleApoyo);
        spMorfCub = (Spinner) v.findViewById(R.id.spMorfEstCub);
        spIncCub = (Spinner) v.findViewById(R.id.spInclinacionCub);
        spEstGenCub = (Spinner) v.findViewById(R.id.spEstadoGeneralCub);
        spTipCeniza = (Spinner) v.findViewById(R.id.spTipCeniza);

        tvMorfCub = (TextView) v.findViewById(R.id.tvMorfEst);
        tvCubiertas = (TextView) v.findViewById(R.id.tvMaterial);
        tvEstGenCub = (TextView) v.findViewById(R.id.tvIncCub);
        tvMatElemApoyo = (TextView) v.findViewById(R.id.tvMEA);
        tvAngInclCub = (TextView) v.findViewById(R.id.tvEstGenC);

        etPM = (EditText) v.findViewById(R.id.etPM);
        etPIC = (EditText) v.findViewById(R.id.etPIC);
        etPMEA = (EditText) v.findViewById(R.id.etPMEA);
        etPEGC = (EditText) v.findViewById(R.id.etPEGC);
        etResCeniza = (EditText) v.findViewById(R.id.etResultadoCeniza);
        etObservaciones = (EditText) v.findViewById(R.id.etObservacionesCeniza);

        aaTipCeniza = new ArrayAdapter<String>(this.getContext(),android.R.layout.simple_spinner_item,opcTipCeniza);
        aaMatCob = new ArrayAdapter<String>(this.getContext(),android.R.layout.simple_spinner_item,opcMatCob);
        aaMatElemAp = new ArrayAdapter<String>(this.getContext(),android.R.layout.simple_spinner_item,opcMatElemAp);
        aaIncCub = new ArrayAdapter<String>(this.getContext(),android.R.layout.simple_spinner_item,opcIncCub);
        aaEstGenCub = new ArrayAdapter<String>(this.getContext(),android.R.layout.simple_spinner_item,opcEstGenCub);
        aaMorfCub = new ArrayAdapter<String>(this.getContext(),android.R.layout.simple_spinner_item,opcMorfCub);

        spTipCeniza.setAdapter(aaTipCeniza);
        spMatCob.setAdapter(aaMatCob);
        spMatElemAp.setAdapter(aaMatElemAp);
        spIncCub.setAdapter(aaIncCub);
        spEstGenCub.setAdapter(aaEstGenCub);
        spMorfCub.setAdapter(aaMorfCub);

        //spTipCeniza.setOnItemSelectedListener(this);

        tvMorfCub.setOnClickListener(this);
        tvCubiertas.setOnClickListener(this);
        tvEstGenCub.setOnClickListener(this);
        tvMatElemApoyo.setOnClickListener(this);
        tvAngInclCub.setOnClickListener(this);

        btnGenerar.setOnClickListener(this);

        Bundle bundle = getActivity().getIntent().getExtras();

        latitud = bundle.getString("c1");
        longitud = bundle.getString("c2");

        habilitarCubierta();

        swTecho = (Switch) v.findViewById(R.id.swTecho);

        swTecho.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (swTecho.isChecked()){
                    inhabilitarCubierta();
                }
                else{
                    habilitarCubierta();
                }
            }
        });

        cargarDatos();

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);

        builder1.setTitle("Tipologia de Onda de Choque")
                .setMessage("¿Desea modificar el resultado de la Tipificacion de Onda de Choque?")
                .setPositiveButton("Si",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tipCenizaActual = spTipCeniza.getSelectedItem().toString();
                                cambioTipologia = true;
                                etObservaciones.setText("Cambio de Tipificacion de Caida de Ceniza de "+tipCenizaAnterior+" a "+tipCenizaActual);
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cambioTipologia = false;
                            }
                        });
        builder1.create().show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
        Cursor cursor = bdP.cargarDatosTablas(latitud,longitud,"tblCeniza");

        if (cursor.getCount()>0){
            cursor.moveToFirst();
            existe = true;
            if (cursor.getString(2).equals("Losas")){
                swTecho.setChecked(true);
                //spTipCeniza.setSelection(3);
                etObservaciones.setText(cursor.getString(9));
            }
            else{
                do {
                    setSpinner(cursor.getString(3),1);
                    setSpinner(cursor.getString(4),2);
                    setSpinner(cursor.getString(5),3);
                    setSpinner(cursor.getString(6),4);
                    setSpinner(cursor.getString(7),5);
                    etObservaciones.setText(cursor.getString(9));
                }while (cursor.moveToNext());
            }
            tipificacionCeniza();
        }
        bdP.cerrarBD();
    }

    public void guardarBD(){
        tipificacionCeniza();

        if (bdP.existeRegistro("tblCeniza",latitud,longitud)){
            try {
                bdP.eliminarRegistro("tblCeniza",latitud,longitud);
            }
            catch (Exception e){
                Toast toast;
                toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
                toast.show();
            }
        }

        String matCob="",matEle="",forma="",inclinacion="",estado="",observaciones;

        if (swTecho.isChecked()){

        }
        else{
            matCob = spMatCob.getSelectedItem().toString();
            matEle = spMatElemAp.getSelectedItem().toString();
            forma = spMorfCub.getSelectedItem().toString();
            inclinacion = spIncCub.getSelectedItem().toString();
            estado = spEstGenCub.getSelectedItem().toString();
        }

        observaciones = etObservaciones.getText().toString();

        try {
            bdP.insertarCeniza(latitud,longitud,tipoTecho,matCob,matEle,forma,inclinacion,estado,tipCeniza,observaciones);
            Toast toast;
            toast=Toast.makeText(context,"Tipologia Caida de Ceniza - Guardado Exitoso",Toast.LENGTH_SHORT);
            toast.show();
            existe = true;
        }
        catch (Exception e){
            Toast toast;
            toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void habilitarCubierta(){
        spMatCob.setEnabled(true);
        spMatElemAp.setEnabled(true);
        spMorfCub.setEnabled(true);
        spIncCub.setEnabled(true);
        spEstGenCub.setEnabled(true);
    }

    public void inhabilitarCubierta(){
        spMatCob.setEnabled(false);
        spMatElemAp.setEnabled(false);
        spMorfCub.setEnabled(false);
        spIncCub.setEnabled(false);
        spEstGenCub.setEnabled(false);
    }

    public void setSpinner(String opc, int tipo){

        switch (tipo){
            case 1:
                switch (opc){
                    case "Policarbonato":
                        spMatCob.setSelection(0);
                        break;
                    case "Teja Barro":
                        spMatCob.setSelection(1);
                        break;
                    case "Teja Eternit":
                        spMatCob.setSelection(2);
                        break;
                    case "Teja Traslucida":
                        spMatCob.setSelection(3);
                        break;
                    case "Teja Zinc":
                        spMatCob.setSelection(4);
                        break;
                }
                break;
            case 2:
                switch (opc){
                    case "Vigas Concreto":
                        spMatElemAp.setSelection(0);
                        break;
                    case "Vigas Madera":
                        spMatElemAp.setSelection(1);
                        break;
                    case "Vigas Metalicas":
                        spMatElemAp.setSelection(2);
                        break;
                }
                break;
            case 3:
                switch (opc){
                    case "A dos Aguas":
                        spMorfCub.setSelection(0);
                        break;
                    case "Cobertizo":
                        spMorfCub.setSelection(1);
                        break;
                    case "Con Faldones":
                        spMorfCub.setSelection(2);
                        break;
                }
                break;
            case 4:
                switch (opc){
                    case "Levemente Inclinada":
                        spIncCub.setSelection(0);
                        break;
                    case "Muy Inclinada":
                        spIncCub.setSelection(1);
                        break;
                }
                break;
            case 5:
                switch (opc){
                    case "Bueno":
                        spEstGenCub.setSelection(0);
                        break;
                    case "Regular":
                        spEstGenCub.setSelection(1);
                        break;
                    case "Malo":
                        spEstGenCub.setSelection(2);
                        break;
                }
                break;
        }
    }

    public void tipificacionCeniza(){

        if (swTecho.isChecked()){
            spTipCeniza.setSelection(3);
            tipoTecho = "Losas";
        }
        else{
            tipoTecho = "Cubierta";

            String matCob = spMatCob.getSelectedItem().toString();

            if(matCob.equals("Teja Traslucida") || matCob.equals("Teja Zinc") || matCob.equals("Policarbonato")){
                matTecho = 10;
            }
            else if(matCob.equals("Teja Eternit")){
                matTecho = 5;
            }else {
                matTecho = 1;
            }

            String incCub = spIncCub.getSelectedItem().toString();
            if(incCub.equals("Levemente Inclinada")){
                angIncCub = 10;
            }
            else {
                angIncCub = 5;
            }

            String matElemAp = spMatElemAp.getSelectedItem().toString();
            if(matElemAp.equals("Vigas Madera")){
                matElemApoyo = 10;
            }
            else if(matElemAp.equals("Vigas Metalicas")){
                matElemApoyo = 3;
            }
            else if(matElemAp.equals("Vigas Concreto")){
                matElemApoyo = 1;
            }

            String estGC = spEstGenCub.getSelectedItem().toString();
            if(estGC.equals("Malo")){
                estGenCub = 10;
            }
            else if(estGC.equals("Regular")){
                estGenCub = 5;
            }
            else {
                estGenCub = 1;
            }

            pMatTcho = (float) (matTecho*0.4);
            pIncCub = (float) (angIncCub*0.15);
            pMatElemApoyo = (float) (matElemApoyo*0.35);
            pEstGenCub = (float) (estGenCub *0.10);

            resCeniza = pMatTcho + pEstGenCub + pMatElemApoyo + pIncCub;

            etPM.setText(Float.toString(bdP.reducirFloat(pMatTcho)));
            etPIC.setText(Float.toString(bdP.reducirFloat(pIncCub)));
            etPMEA.setText(Float.toString(bdP.reducirFloat(pMatElemApoyo)));
            etPEGC.setText(Float.toString(bdP.reducirFloat(pEstGenCub)));
            etResCeniza.setText(Float.toString(bdP.reducirFloat(resCeniza)));

            if (cambioTipologia==false){
                if(resCeniza==3.25||resCeniza==4.1||resCeniza==5.05||resCeniza==5.65||resCeniza==5.5||resCeniza==4.8||resCeniza==6.75||resCeniza==7.25||resCeniza==4.85||resCeniza==5.55||resCeniza==6.1||(resCeniza>=6.3&&resCeniza<=10)){
                    spTipCeniza.setSelection(0);
                }
                else if(resCeniza==2.5||resCeniza==2.6||(resCeniza>=3.95&&resCeniza<=4.74)||resCeniza==5.15||resCeniza==5.2||resCeniza==5.6||resCeniza==5.9){
                    spTipCeniza.setSelection(1);
                }
                else if (resCeniza<=2.4||(resCeniza>=2.7&&resCeniza<=3.2)||(resCeniza>=3.6&&resCeniza<=3.9)||resCeniza==4.75){
                    spTipCeniza.setSelection(2);
                }
            }
        }
        tipCeniza = spTipCeniza.getSelectedItem().toString();
        tipCenizaAnterior = tipCeniza;
    }
}
