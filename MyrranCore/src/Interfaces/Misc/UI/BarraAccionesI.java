package Interfaces.Misc.UI;// Created by Hanto on 13/05/2014.

import Interfaces.EntidadesPropiedades.Propiedades.CasterPersonalizable;
import Interfaces.Misc.Observable.ModelI;

public interface BarraAccionesI extends ModelI
{
    public int getID();
    public int getNumFilas();
    public int getNumColumnas();
    public CasillaI getCasilla(int posX, int posY);
    public CasterPersonalizable getCaster();

    //Metodos:
    public void eliminarKeycode(int keycode);
    public void eliminar();
    public void eliminarFila(int numFilas);
    public void añadirFila(int numFilas);
    public void eliminarColumna(int numColumnas);
    public void añadirColumna(int numColumnas);
}
