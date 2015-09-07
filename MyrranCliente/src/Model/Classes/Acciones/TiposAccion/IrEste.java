package Model.Classes.Acciones.TiposAccion;// Created by Hanto on 07/05/2014.

import Interfaces.EntidadesPropiedades.Misc.MaquinablePlayer;
import Interfaces.Misc.Model.ModelI;
import Model.Classes.Acciones.Accion;

public class IrEste extends Accion
{
    public IrEste()
    {   iD = getClass().getSimpleName(); }

    @Override public void accionKeyDown(MaquinablePlayer player, ModelI inputManager)
    {   player.getInput().setirDerecha(true); }

    @Override public void accionKeyUp(MaquinablePlayer player, ModelI inputManager)
    {   player.getInput().setirDerecha(false); }
}
