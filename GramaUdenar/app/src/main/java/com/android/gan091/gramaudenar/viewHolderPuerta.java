package com.android.gan091.gramaudenar;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import java.util.List;

/**
 * Created by SIB02 on 20/11/2017.
 */

public class viewHolderPuerta extends RecyclerView.ViewHolder {

    EditText etAncho,etAlto;
    List<Fuente> listaObjeto;

    public viewHolderPuerta(View itemView, List<Fuente> datos) {
        super(itemView);

        etAncho = (EditText) itemView.findViewById(R.id.etAnchoP);
        etAlto = (EditText) itemView.findViewById(R.id.etAltoP);
        listaObjeto = datos;
    }
}
