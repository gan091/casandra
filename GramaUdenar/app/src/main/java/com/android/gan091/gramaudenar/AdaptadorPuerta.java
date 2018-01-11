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

public class AdaptadorPuerta extends RecyclerView.Adapter<viewHolderPuerta> {

    List<Fuente> listaObjeto;
    ArrayList<EditText> listaEA = new ArrayList<>();
    ArrayList<EditText> listaEAl = new ArrayList<>();

    public AdaptadorPuerta(List<Fuente> listaObjeto) {
        this.listaObjeto = listaObjeto;
    }

    @Override
    public viewHolderPuerta onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_puerta,parent,false);
        return new viewHolderPuerta(vista,listaObjeto);
    }

    @Override
    public void onBindViewHolder(viewHolderPuerta holder, int position) {
        holder.etAncho.setText(Float.toString(listaObjeto.get(position).getAncho()));
        holder.etAlto.setText(Float.toString(listaObjeto.get(position).getAlto()));
        listaEA.add(holder.etAncho);
        listaEAl.add(holder.etAlto);
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

    public void vaciarLista(){
        listaEA.clear();
        listaEAl.clear();
    }
}
