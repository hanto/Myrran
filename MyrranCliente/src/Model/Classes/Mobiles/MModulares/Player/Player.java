package Model.Classes.Mobiles.MModulares.Player;// Created by Hanto on 04/09/2015.

import DB.DAO;
import Interfaces.BDebuff.AuraI;
import InterfacesEntidades.EntidadesPropiedades.*;
import InterfacesEntidades.EntidadesPropiedades.Espaciales.Colisionable;
import InterfacesEntidades.EntidadesTipos.PlayerI;
import Interfaces.GameState.MundoI;
import Interfaces.Input.PlayerIOI;
import Interfaces.Skill.SkillPersonalizadoI;
import Interfaces.Spell.SpellI;
import Interfaces.Spell.SpellPersonalizadoI;
import Model.AI.FSM.IO.PlayerIO;
import Model.AI.FSM.MaquinaEstados;
import Model.AI.FSM.MaquinaEstadosFactory;
import Model.Mobiles.Cuerpos.Cuerpo;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class Player extends PlayerNotificador implements PlayerI, Debuffeable
{
    private IDentificable identificable;
    private Caster caster;
    private CasterPersonalizable casterPersonalizado;
    private Vulnerable vulnerable;
    private Debuffeable debuffeable;
    private PCStats pcStats;
    private Animable animable;
    private Cuerpo cuerpo;

    private boolean castearInterrumpible = false;
    private MaquinaEstados fsm;
    private PlayerIO input = new PlayerIO();
    private PlayerIO output = new PlayerIO();

    protected Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

    public Player(Cuerpo cuerpo,
                  IDentificable identificable, Caster caster, CasterPersonalizable casterPersonalizado,
                  Vulnerable vulnerable, Debuffeable debuffeable, PCStats pcStats,
                  Animable animable)
    {
        this.identificable = identificable;
        this.caster = caster;
        this.casterPersonalizado = casterPersonalizado;
        this.vulnerable = vulnerable;
        this.debuffeable = debuffeable;
        this.pcStats = pcStats;
        this.animable = animable;
        this.cuerpo = cuerpo;
        this.fsm = MaquinaEstadosFactory.PLAYER.nuevo(this);

        this.setAncho(cuerpo.getAncho());
        this.setAlto(cuerpo.getAlto());
        this.setSeguible(false);
        this.cuerpo.setPosition(0, 0);
        this.cuerpo.setCalculosInterpolados(true);
        this.setVelocidadMax(80);
    }

    @Override public void dispose()
    {   cuerpo.dispose();   }

    // IDENTIFICABLE:
    //------------------------------------------------------------------------------------------------------------------
    public int getID()                                              {   return identificable.getID(); }
    public void setID(int iD)                                       {   identificable.setID(iD); }

    // DINAMICO:
    //------------------------------------------------------------------------------------------------------------------
    @Override public void setDireccion(float x, float y)            {   cuerpo.setDireccion(x, y); }
    @Override public void setDireccion(float grados)                {   cuerpo.setDireccion(grados); }

    // ANIMABLE:
    //------------------------------------------------------------------------------------------------------------------
    @Override public int getNumAnimacion()                          {   return animable.getNumAnimacion(); }

    // VULNERABLE:
    //------------------------------------------------------------------------------------------------------------------
    @Override public float getActualHPs()                           {   return vulnerable.getActualHPs(); }
    @Override public float getMaxHPs()                              {   return vulnerable.getMaxHPs(); }
    @Override public void setActualHPs(float HPs)                   {   vulnerable.setActualHPs(HPs); }

    // DEBUFFEABLE:
    //------------------------------------------------------------------------------------------------------------------
    @Override public void añadirAura(AuraI aura)                    {   debuffeable.añadirAura(aura); }
    @Override public void eliminarAura(AuraI aura)                  {   debuffeable.eliminarAura(aura); }
    @Override public Iterator<AuraI> getAuras()                     {   return debuffeable.getAuras(); }
    @Override public void actualizarAuras(float delta)              {   debuffeable.actualizarAuras(delta); }

    // PCSTATS:
    //------------------------------------------------------------------------------------------------------------------
    @Override public int getIDProyectiles()                         {   return pcStats.getIDProyectiles(); }
    @Override public String getNombre()                             {   return pcStats.getNombre(); }
    @Override public int getNivel()                                 {   return pcStats.getNivel(); }
    @Override public void setNivel(int nivel)                       {   pcStats.setNivel(nivel); }

    // CASTER PERSONALIZADO:
    //------------------------------------------------------------------------------------------------------------------
    @Override public SkillPersonalizadoI getSkillPersonalizado(String skillID)
    {   return casterPersonalizado.getSkillPersonalizado(skillID); }
    @Override public SpellPersonalizadoI getSpellPersonalizado(String spellID)
    {   return casterPersonalizado.getSpellPersonalizado(spellID); }
    @Override public Iterator<SpellPersonalizadoI> getIteratorSpellPersonalizado()
    {   return casterPersonalizado.getIteratorSpellPersonalizado(); }
    @Override public Iterator<SkillPersonalizadoI> getIteratorSkillPersonalizado()
    {   return casterPersonalizado.getIteratorSkillPersonalizado(); }
    @Override public void añadirSkillsPersonalizados(String spellID)
    {   casterPersonalizado.añadirSkillsPersonalizados(spellID); }

    // CASTER:
    //------------------------------------------------------------------------------------------------------------------
    @Override public boolean isCasteando()                          {   return caster.isCasteando(); }
    @Override public float getActualCastingTime()                   {   return caster.getActualCastingTime(); }
    @Override public float getTotalCastingTime()                    {   return caster.getTotalCastingTime(); }
    @Override public String getSpellIDSeleccionado()                {   return caster.getSpellIDSeleccionado(); }
    @Override public Object getParametrosSpell()                    {   return caster.getParametrosSpell(); }
    @Override public void setActualCastingTime(float castingTime)   {   caster.setActualCastingTime(castingTime); }
    @Override public void setTotalCastingTime(float castingTime)    {   caster.setTotalCastingTime(castingTime); }

    // CORPOREO:
    //------------------------------------------------------------------------------------------------------------------
    @Override public Cuerpo getCuerpo()                             { return cuerpo; }

    // INPUT:
    //------------------------------------------------------------------------------------------------------------------
    @Override public PlayerIOI getInput()                           {   return input; }
    @Override public PlayerIOI getOutput()                          {   return output; }


    // CODIGO PERSONALIZADO:
    //------------------------------------------------------------------------------------------------------------------

    // notificar vista:
    //------------------------------------------------------------------------------------------------------------------
    @Override public void modificarHPs(float HPs)
    {
        vulnerable.modificarHPs(HPs);
        notificarSetModificarHPs(HPs);
    }

    @Override public void setNombre (String nombre)
    {
        pcStats.setNombre(nombre);
        notificarSetNombre();
    }

    @Override public void setMaxHPs (float mHps)
    {
        vulnerable.setMaxHPs(mHps);
        notificarSetMaxHPs();
    }

    @Override public void actualizarCastingTime(float delta)
    {
        caster.actualizarCastingTime(delta);
        notificarSetCastingTime();
    }

    // notificar Servidor (y en algunos casos vista):
    //------------------------------------------------------------------------------------------------------------------
    @Override public void setNumAnimacion(int numAnimacion)
    {
        animable.setNumAnimacion(numAnimacion);
        notificarSetNumAnimacion();
    }

    @Override public void setParametrosSpell(Object parametros)
    {
        caster.setParametrosSpell(parametros);
        notificarSetParametrosSpell();
    }

    @Override public void setSpellIDSeleccionado(String spellID)
    {
        caster.setSpellIDSeleccionado(spellID);
        notificarSetSpellIDSeleccionado();
    }

    @Override public void copiarUltimaPosicion()
    {   cuerpo.copiarUltimaPosicion(); }

    @Override public void setPosition (float x, float y)
    {
        super.setPosition(x, y);
        cuerpo.setPosition(x, y);
        notificarSetPosition();
    }

    @Override public void setNumTalentosSkillPersonalizado(String skillID, int statID, int valor)
    {   notificarSetNumTalentosSkillPersonalizado(skillID, statID, valor); }

    @Override public void setNumTalentosSkillPersonalizadoFromServer(String skillID, int statID, int valor)
    {
        SkillPersonalizadoI skillPersonalizado = casterPersonalizado.getSkillPersonalizado(skillID);
        if (skillPersonalizado == null) { logger.error("ERROR: setNumTalentosSkillPersonalizado, spellID no existe: {}", skillID); return; }
        skillPersonalizado.setNumTalentos(statID, valor);
    }

    // ACTUALIZACION:
    //------------------------------------------------------------------------------------------------------------------

    @Override public void actualizarFisica(float delta, MundoI mundo)
    {    }

    @Override public void actualizarFisicaPorInterpolacion(float alpha)
    {
        cuerpo.interpolarPosicion(alpha);
        super.setPosition(cuerpo.getX(), cuerpo.getY());
        notificarSetPosition();
    }

    @Override public void actualizarTimers(float delta)
    {   actualizarCastingTime(delta); }

    @Override public void actualizarIA (float delta, MundoI mundo)
    {
        fsm.actualizar(delta);
        setNumAnimacion(output.getNumAnimacion());

        setRumbo();

        setSpellIDSeleccionado(output.getSpellID());
        if (output.getStartCastear()) startCastear(mundo);
        else if (output.getStopCastear()) stopCastear();
    }

    @Override public void setCastear(boolean castear, int screenX, int screenY)
    {
        output.setScreenX(screenX);
        output.setScreenY(screenY);
        if (castear) { output.setStartCastear(true); output.setStopCastear(false); }
        else { output.setStopCastear(true); output.setStartCastear(false); }
    }

    // METODOS PROPIOS:
    //------------------------------------------------------------------------------------------------------------------

    private void stopCastear()
    {
        if (castearInterrumpible)
        {
            castearInterrumpible = false;
            notificarSetStopCastear(output.mundoX, output.mundoY);
        }
    }

    private void startCastear(MundoI mundo)
    {
        if (!isCasteando())
        {
            SpellI spell = DAO.spellDAOFactory.getSpellDAO().getSpell(output.getSpellID());
            if (spell != null)
            {
                spell.castear(this, output.mundoX, output.mundoY, mundo);

                castearInterrumpible = true;
                notificarSetStartCastear(output.mundoX, output.mundoY);
            }
        }
    }

    private void setRumbo()
    {
        if      (output.irAbajo && !output.irDerecha && !output.irIzquierda)    { cuerpo.setDireccionNorVelocidad(0, -1, velocidadMax * velocidadMod); }                //Sur
        else if (output.irArriba && !output.irDerecha && !output.irIzquierda)   { cuerpo.setDireccionNorVelocidad(0, +1, velocidadMax * velocidadMod); }               //Norte
        else if (output.irDerecha && !output.irArriba && !output.irAbajo)       { cuerpo.setDireccionNorVelocidad(+1, 0, velocidadMax * velocidadMod); }               //Este
        else if (output.irIzquierda && !output.irArriba && !output.irAbajo)     { cuerpo.setDireccionNorVelocidad(-1, 0, velocidadMax * velocidadMod); }               //Oeste
        else if (output.irAbajo && output.irIzquierda)                          { cuerpo.setDireccionNorVelocidad(-0.707f, -0.707f, velocidadMax * velocidadMod); }    //SurOeste
        else if (output.irAbajo && output.irDerecha)                            { cuerpo.setDireccionNorVelocidad(+0.707f, -0.707f, velocidadMax * velocidadMod); }    //SurEste
        else if (output.irArriba && output.irIzquierda)                         { cuerpo.setDireccionNorVelocidad(-0.707f, +0.707f, velocidadMax * velocidadMod); }    //NorOeste
        else if (output.irArriba && output.irDerecha)                           { cuerpo.setDireccionNorVelocidad(+0.707f, +0.707f, velocidadMax * velocidadMod); }    //NorEste
        else { cuerpo.setVelocidad(0f); }
    }

    // COLISIONABLE:
    //-----------------------------------------------------------------------------------------------------------------

    @Override public void checkColisionesConMob(Colisionable colisionable)  {}
    @Override public void checkColisionesConMuro() {}
}

