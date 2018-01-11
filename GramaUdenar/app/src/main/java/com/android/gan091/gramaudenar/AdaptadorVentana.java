package com.android.gan091.gramaudenar;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SIB02 on 20/11/2017.
 */

public class AdaptadorVentana extends RecyclerView.Adapter<viewHolderVentana> {

    List<Fuente> listaObjeto;
    ArrayList<EditText> listaEA = new ArrayList<>();
    ArrayList<EditText> listaEAl = new ArrayList<>();
    ArrayList<EditText> listaENumP = new ArrayList<>();

    public AdaptadorVentana(List<Fuente> listaObjeto) {
        this.listaObjeto = listaObjeto;
    }

    @Override
    public viewHolderVentana onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_ventana,parent,false);
        return new viewHolderVentana(vista,listaObjeto);
    }

    @Override
    public void onBindViewHolder(viewHolderVentana holder, int position) {
        holder.etAncho.setText(Float.toString(listaObjeto.get(position).getAncho()));
        holder.etAlto.setText(Float.toString(listaObjeto.get(position).getAlto()));
        holder.etNumPiso.setText(Integer.toString(listaObjeto.get(position).getNumPiso()));
        listaEA.add(holder.etAncho);
        listaEAl.add(holder.etAlto);
        listaENumP.add(holder.etNumPiso);
    }

    @Override
    public int getItemCount() {
        return listaObjeto.size();
    }

    public ArrayList<EditText> getListaEA(){
        return listaEA;
    }

    public ArrayList<EditText> getListaEAl(){
        return listaEAl;
    }

    public ArrayList<EditText> getListaENumP(){
        return listaENumP;
    }

    public void vaciarLista(){
        listaEA.clear();
        listaEAl.clear();
        listaENumP.clear();
    }
}
