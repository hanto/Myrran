package View.Classes.UI.BarraAccionesView;// Created by Hanto on 13/07/2014.

import Interfaces.Misc.Controlador.ControladorBarraAccionI;
import View.Classes.UI.BarraAccionesView.CasillaView.CasillaView;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class BarraAccionesViewRebindListener extends InputListener
{
    private ConjuntoBarraAccionesView conjuntoBarraAccionesView;
    private CasillaView icono;
    private ControladorBarraAccionI controlador;


    public BarraAccionesViewRebindListener(CasillaView icono, ConjuntoBarraAccionesView conjuntoBarraAccionesView, ControladorBarraAccionI controlador)
    {
        this.conjuntoBarraAccionesView = conjuntoBarraAccionesView;
        this.icono = icono;
        this.controlador = controlador;
    }


    @Override public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
    {
        if (conjuntoBarraAccionesView.getRebindearSkills())
            icono.getApariencia().getStage().setKeyboardFocus(icono.getApariencia());
    }

    //Hacemos que deje de recibir eventos de teclado, puesto que el teclado ha salido
    @Override public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor)
    {
        if (conjuntoBarraAccionesView.getRebindearSkills())
            icono.getApariencia().getStage().setKeyboardFocus(null);
    }

    //Capturamos que tecla aprieta el player para rebindearla
    @Override public boolean keyDown(InputEvent event, int keycode)
    {   //Solo rebindeamos los skills, si esta activado el boton de rebindear
        controlador.barraAccionRebindear(icono.getCasilla(), keycode);
        return true;
    }

    @Override public boolean keyUp (InputEvent event, int keycode)
    {   return true; }
}
