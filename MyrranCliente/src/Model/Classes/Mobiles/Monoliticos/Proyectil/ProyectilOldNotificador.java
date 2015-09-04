package Model.Classes.Mobiles.Monoliticos.Proyectil;// Created by Hanto on 13/08/2015.

import DTO.DTOsProyectil;
import InterfacesEntidades.EntidadesTipos.ProyectilI;
import Model.Mobiles.Steerables.SteerableAgentOld;

public abstract class ProyectilOldNotificador extends SteerableAgentOld implements ProyectilI
{
    private DTOsProyectil.PosicionProyectil posicionDTO;

    public ProyectilOldNotificador()
    {
        posicionDTO = new DTOsProyectil.PosicionProyectil(this);
    }

    // NOTIFICADOR LOCAL:
    //------------------------------------------------------------------------------------------------------------------

    public void notificarSetPosition()
    {
        if (posicionDTO.posX != (int)getX() || posicionDTO.posY != (int)getY())
        {
            posicionDTO.posX = (int) getX();
            posicionDTO.posY = (int) getY();
            notificarActualizacion("posicionProyectil", null, posicionDTO);
        }
    }
}