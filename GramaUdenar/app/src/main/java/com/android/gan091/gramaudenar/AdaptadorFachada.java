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

public class AdaptadorFachada extends RecyclerView.Adapter<viewHolderFachada> {

    List<Fuente> listaObjeto;
    ArrayList<EditText> listaE = new ArrayList<>();
    boolean cargar = false;

    public AdaptadorFachada(List<Fuente> listaObjeto) {
        this.listaObjeto = listaObjeto;
    }


    @Override
    public viewHolderFachada onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_fachada,parent,false);
        return new viewHolderFachada(vista,listaObjeto);
    }

    @Override
    public void onBindViewHolder(viewHolderFachada holder, int position) {
        //if (cargar){
            holder.etAncho.setText(Float.toString(listaObjeto.get(position).getAncho()));
        //}
        //else{
            //holder.etAncho.setText("");
        //}
        listaE.add(holder.etAncho);
    }

    @Override
    public int getItemCount() {
        return listaObjeto.size();
    }

    public ArrayList<EditText> getListaE(){
        return listaE;
    }

    public void vaciarLista(){
        listaE.clear();
    }

    public void setCargar(boolean cargar){
        this.cargar = cargar;
    }
}
