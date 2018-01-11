package com.android.gan091.gramaudenar;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import java.util.List;

/**
 * Created by SIB02 on 20/11/2017.
 */

public class viewHolderFachada extends RecyclerView.ViewHolder {

    EditText etAncho;
    List<Fuente> listaObjeto;

    public viewHolderFachada(View itemView, List<Fuente> datos) {
        super(itemView);

        etAncho = (EditText) itemView.findViewById(R.id.etAnchoF);
        listaObjeto = datos;
    }
}
