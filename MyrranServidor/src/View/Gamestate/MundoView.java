package View.Gamestate;// Created by Hanto on 16/07/2015.

import DTO.DTOsMundo;
import InterfacesEntidades.EntidadesTipos.CampoVisionI;
import InterfacesEntidades.EntidadesTipos.PCI;
import Interfaces.Network.ServidorI;
import Model.GameState.Mundo;
import View.Gamestate.CampoVision.CampoVision;
import com.badlogic.gdx.utils.Disposable;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MundoView implements PropertyChangeListener, Disposable
{
    public Mundo mundo;

    protected List<CampoVisionI> listaCampoVisiones = new ArrayList<>();


    //Constructor:
    public MundoView(Mundo mundo)
    {
        mundo.añadirObservador(this);
        this.mundo = mundo;
    }

    @Override public void dispose()
    {   mundo.eliminarObservador(this); }

    public void añadirCampoVision(PCI pc)
    {
        CampoVisionI campoVision = new CampoVision(pc, pc.getID(), this);
        listaCampoVisiones.add(campoVision);
    }

    public void eliminarCampoVision(int connectionID)
    {
        CampoVisionI campoVision;
        Iterator<CampoVisionI>iterator = listaCampoVisiones.iterator();
        while (iterator.hasNext())
        {
            campoVision = iterator.next();
            if (campoVision.getConnectionID() == connectionID)
            {
                campoVision.dispose();
                iterator.remove();
            }
        }
    }

    public void radar()
    {
        for (CampoVisionI campoVision : listaCampoVisiones)
        {   campoVision.radar(); }
    }

    public void enviarDTOs(ServidorI servidor)
    {
        for (CampoVisionI campoVision : listaCampoVisiones)
        {   campoVision.enviarDTOs(servidor); }
    }

    //CAMPOS OBSERVADOS:
    //-------------------------------------------------------------------------------------------------------------
    @Override public void propertyChange (PropertyChangeEvent evt)
    {
        //OBSERVAR MUNDO:
        if (evt.getNewValue() instanceof DTOsMundo.AñadirPC)
        {   añadirCampoVision(((DTOsMundo.AñadirPC) evt.getNewValue()).pc); }

        if (evt.getNewValue() instanceof DTOsMundo.EliminarPC)
        {   eliminarCampoVision(((DTOsMundo.EliminarPC) evt.getNewValue()).pc.getID());}
    }
}
