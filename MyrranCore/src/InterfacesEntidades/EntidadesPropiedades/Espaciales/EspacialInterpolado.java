package InterfacesEntidades.EntidadesPropiedades.Espaciales;// Created by Hanto on 06/08/2015.

public interface EspacialInterpolado extends Espacial
{
    public void copiarUltimaPosicion();
    public void actualizarFisicaPorInterpolacion(float alpha);
}
