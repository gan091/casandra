package com.android.gan091.gramaudenar;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LaharesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LaharesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LaharesFragment extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    String latitud,longitud;
    float puntajeEst,puntajeMuros,puntajeEstEd,resTipologia;

    ArrayAdapter<String> aaMuros,aaEGE,aaTipLahares;
    BaseDeDatos bdP;
    Button btnLahares;
    CheckBox cbReforzado;
    Context context;
    EditText etEst,etMuros,etEstEd,etResTip,etObservaciones;
    Spinner spMuros,spEstadoGeneral,spTipLahares;
    String [] opcMuros = new String[] {"Ladrillo Macizo","Bloque","Tapia Pisada","Bahareque","Madera"};
    String [] opcEstadoGeneral = new String[] {"Bueno","Regular","Malo"};
    String [] opcTipLahares = new String[] {"Tipologia_1","Tipologia_2","Tipologia_3"};
    TextView tvEstructura,tvMuros,tvEstadoGeneral;

    public LaharesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LaharesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LaharesFragment newInstance(String param1, String param2) {
        LaharesFragment fragment = new LaharesFragment();
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        context = getContext();
        bdP = new BaseDeDatos(context);
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
            case R.id.tvEst:
                toast= Toast.makeText(getActivity(),"Porcentaje segun Estructura",Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.tvMur:
                toast=Toast.makeText(getActivity(),"Porcentaje segun Material de Muros",Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.tvEstado:
                toast=Toast.makeText(getActivity(),"Porcentaje segun Estado General de la Vivienda",Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.btnGenerarL:
                guardarBD();
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_lahares, container, false);

        spMuros = (Spinner) v.findViewById(R.id.spMuros);
        spEstadoGeneral = (Spinner) v.findViewById(R.id.spEstGenEd);
        spTipLahares = (Spinner) v.findViewById(R.id.spLahares);

        tvEstructura = (TextView) v.findViewById(R.id.tvEst);
        tvMuros = (TextView) v.findViewById(R.id.tvMur);
        tvEstadoGeneral = (TextView) v.findViewById(R.id.tvEstado);

        btnLahares = (Button) v.findViewById(R.id.btnGenerarL);

        cbReforzado = (CheckBox) v.findViewById(R.id.checkBox);

        etEst = (EditText) v.findViewById(R.id.etPE);
        etMuros = (EditText) v.findViewById(R.id.etPMM);
        etEstEd = (EditText) v.findViewById(R.id.etPEG);
        etResTip = (EditText) v.findViewById(R.id.etResultadoLahares);
        etObservaciones = (EditText) v.findViewById(R.id.etObservacionesLahares);

        aaMuros = new ArrayAdapter<String>(this.getContext(),android.R.layout.simple_spinner_item,opcMuros);
        aaEGE = new ArrayAdapter<String>(this.getContext(),android.R.layout.simple_spinner_item,opcEstadoGeneral);
        aaTipLahares = new ArrayAdapter<String>(this.getContext(),android.R.layout.simple_spinner_item,opcTipLahares);

        spMuros.setAdapter(aaMuros);
        spEstadoGeneral.setAdapter(aaEGE);
        spTipLahares.setAdapter(aaTipLahares);

        tvEstructura.setOnClickListener(this);
        tvMuros.setOnClickListener(this);
        tvEstadoGeneral.setOnClickListener(this);

        btnLahares.setOnClickListener(this);

        cbReforzado.isChecked();

        Bundle bundle = getActivity().getIntent().getExtras();

        latitud = bundle.getString("c1");
        longitud = bundle.getString("c2");

        cargarDatos();

        return v;
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

    public void guardarBD(){
        obtenerTipologia();

        if (bdP.existeRegistro("tblLahares",latitud,longitud)){
            try {
                bdP.eliminarRegistro("tblLahares",latitud,longitud);
            }
            catch (Exception e){
                Toast toast;
                toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
                toast.show();
            }
        }

        try {
            bdP.insertarLahares(latitud,longitud,cbReforzado.isChecked(),spMuros.getSelectedItem().toString(),spEstadoGeneral.getSelectedItem().toString(),spTipLahares.getSelectedItem().toString(),etObservaciones.getText().toString());
            Toast toast;
            toast=Toast.makeText(context,"Tipologia Lahares - Guardado Exitoso",Toast.LENGTH_SHORT);
            toast.show();
        }
        catch (Exception e){
            Toast toast;
            toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void obtenerTipologia(){
        if (cbReforzado.isChecked()){
            puntajeEst = (float) (0.1*0.5);
        }
        else {
            puntajeEst = (float) (1*0.5);
        }

        switch (spMuros.getSelectedItem().toString()){
            case "Ladrillo Macizo":
                puntajeMuros = (float) (0.1*0.3);
                break;
            case "Bloque":
                puntajeMuros = (float) (0.2*0.3);
                break;
            case "Tapia Pisada":
                puntajeMuros = (float) (0.5*0.3);
                break;
            case "Bahareque":
                puntajeMuros = (float) (0.8*0.3);
                break;
            case "Madera":
                puntajeMuros = (float) (1*0.3);
                break;
        }

        switch (spEstadoGeneral.getSelectedItem().toString()){
            case "Bueno":
                puntajeEstEd = (float) (0.1*0.2);
                break;
            case "Regular":
                puntajeEstEd = (float) (0.5*0.2);
                break;
            case "Malo":
                puntajeEstEd = (float) (1*0.2);
                break;
        }

        resTipologia = puntajeEst + puntajeMuros + puntajeEstEd;

        etEst.setText(Float.toString(bdP.reducirFloat(puntajeEst)));
        etMuros.setText(Float.toString(bdP.reducirFloat(puntajeMuros)));
        etEstEd.setText(Float.toString(bdP.reducirFloat(puntajeEstEd)));
        etResTip.setText(Float.toString(bdP.reducirFloat(resTipologia)));

        if (resTipologia < 0.18){
            spTipLahares.setSelection(2);
        }
        else if (resTipologia > 0.28){
            spTipLahares.setSelection(0);
        }
        else{
            spTipLahares.setSelection(1);
        }
    }

    public void cargarDatos(){
        bdP.abrirBD();
        Cursor cursor = bdP.cargarDatosTablas(latitud,longitud,"tblLahares");

        if (cursor.moveToFirst()){

            do {
                if (cursor.getInt(2)==1){
                    cbReforzado.setChecked(true);
                }

                setSpinner(cursor.getString(3),1);
                setSpinner(cursor.getString(4),2);
                etObservaciones.setText(cursor.getString(6));
            }while (cursor.moveToNext());

            obtenerTipologia();
        }
        bdP.cerrarBD();
    }

    public void setSpinner(String opc, int tipo){

        switch (tipo){
            case 1:
                switch (opc){
                    case "Ladrillo Macizo":
                        spMuros.setSelection(0);
                        break;
                    case "Bloque":
                        spMuros.setSelection(1);
                        break;
                    case "Tapia Pisada":
                        spMuros.setSelection(2);
                        break;
                    case "Bahareque":
                        spMuros.setSelection(3);
                        break;
                    case "Madera":
                        spMuros.setSelection(4);
                        break;
                }
                break;
            case 2:
                switch (opc){
                    case "Bueno":
                        spEstadoGeneral.setSelection(0);
                        break;
                    case "Regular":
                        spEstadoGeneral.setSelection(1);
                        break;
                    case "Malo":
                        spEstadoGeneral.setSelection(2);
                        break;
                }
                break;
        }
    }
}
