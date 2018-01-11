package com.android.gan091.gramaudenar;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import java.util.List;

/**
 * Created by SIB02 on 20/11/2017.
 */

public class viewHolderVentana extends RecyclerView.ViewHolder {

    EditText etAncho,etAlto,etNumPiso;
    List<Fuente> listaObjeto;

    public viewHolderVentana(View itemView, List<Fuente> datos) {
        super(itemView);

        etAncho = (EditText) itemView.findViewById(R.id.etAnchoV);
        etAlto = (EditText) itemView.findViewById(R.id.etAltoV);
        etNumPiso = (EditText) itemView.findViewById(R.id.etNumPiso);
        listaObjeto = datos;
    }
}
