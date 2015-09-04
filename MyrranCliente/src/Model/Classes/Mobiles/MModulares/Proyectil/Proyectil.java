package Model.Classes.Mobiles.MModulares.Proyectil;// Created by Hanto on 04/09/2015.

import InterfacesEntidades.EntidadesPropiedades.Caster;
import InterfacesEntidades.EntidadesPropiedades.Consumible;
import InterfacesEntidades.EntidadesPropiedades.Espaciales.Colisionable;
import InterfacesEntidades.EntidadesPropiedades.IDentificable;
import InterfacesEntidades.EntidadesPropiedades.ProyectilStats;
import InterfacesEntidades.EntidadesTipos.MobI;
import InterfacesEntidades.EntidadesTipos.PCI;
import Interfaces.GameState.MundoI;
import Interfaces.Spell.SpellI;
import Model.Mobiles.Cuerpos.Cuerpo;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

public class Proyectil extends ProyectilNotificador
{
    protected IDentificable identificable;
    protected ProyectilStats proyectilStats;
    protected Consumible consumible;
    protected Cuerpo cuerpo;

    protected Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

    public Proyectil(Cuerpo cuerpo,
                     IDentificable identificable, ProyectilStats proyectilStats,
                     Consumible consumible)
    {
        this.identificable = identificable;
        this.proyectilStats = proyectilStats;
        this.consumible = consumible;
        this.cuerpo = cuerpo;

        this.setAncho(cuerpo.getAncho());
        this.setAlto(cuerpo.getAlto());
        this.setSeguible(false);
        this.cuerpo.setCalculosInterpolados(true);
    }

    @Override public void dispose()
    {
        cuerpo.dispose();
        this.eliminarObservadores();
    }

    // IDENTIFICABLE:
    //------------------------------------------------------------------------------------------------------------------
    @Override public int getID()                                    {   return identificable.getID(); }

    // PROYECTILSTATS:
    //------------------------------------------------------------------------------------------------------------------
    @Override public Caster getOwner()                              {   return proyectilStats.getOwner(); }
    @Override public SpellI getSpell()                              {   return proyectilStats.getSpell(); }
    @Override public float getDaño()                                {   return proyectilStats.getDaño(); }
    @Override public void setSpell(SpellI spell)                    {   proyectilStats.setSpell(spell); }
    @Override public void setDaño(float daño)                       {   proyectilStats.setDaño(daño); }
    @Override public void setOwner(Caster caster)                   {   proyectilStats.setOwner(caster); }

    // CONSUMIBLE:
    //------------------------------------------------------------------------------------------------------------------
    @Override public float getDuracionActual()                      {   return consumible.getDuracionActual(); }
    @Override public float getDuracionMaxima()                      {   return consumible.getDuracionMaxima(); }
    @Override public void setDuracionMaxima(float duracionMaxima)   {   consumible.setDuracionMaxima(duracionMaxima); }
    @Override public void setDuracionActual(float duracionActual)   {   consumible.setDuracionActual(duracionActual); }
    @Override public boolean actualizarDuracion(float delta)        {   return consumible.actualizarDuracion(delta); }

    // CORPOREO:
    //------------------------------------------------------------------------------------------------------------------
    @Override public Cuerpo getCuerpo()
    {   return cuerpo; }

    // CODIGO PERSONALIZADO:
    //------------------------------------------------------------------------------------------------------------------

    @Override public void setID(int iD)
    {
        if (proyectilStats.getOwner() == null)
        {   logger.error("ERROR: no se puede generar el ID del proyectil con el Owner NULL"); }

        if (proyectilStats.getOwner() instanceof PCI)
        {   //el int en java como tiene signo, tiene 31 bits para datos:
            //Destino un bit para determinar si es de player o de mob:
            double hashcode = Math.pow(2, 31 - 1);
            //Destino 10 bits para dejar sitio para 1,025 players 2^10
            hashcode = hashcode + ((PCI) proyectilStats.getOwner()).getID() * Math.pow(2, 31 - 1 - 10);
            //Cada player podra disparar 2^20 = 1,048,576 pepos antes de tener que reiniciar el contador
            hashcode = hashcode + ((PCI) proyectilStats.getOwner()).getIDProyectiles();
            identificable.setID((int)hashcode);
        }
        if (proyectilStats.getOwner() instanceof MobI)
        {   identificable.setID((int)0); }
    }

    @Override public void copiarUltimaPosicion()
    {   cuerpo.copiarUltimaPosicion(); }

    @Override public void setPosition(float x, float y)
    {
        super.setPosition(x, y);
        cuerpo.setPosition(x, y);
        notificarSetPosition();
    }

    @Override public void setDireccion(float x, float y)
    {   cuerpo.setDireccion(x, y); }

    @Override public void setDireccion(float grados)
    {   cuerpo.setDireccion(grados); }

    @Override public void setVelocidadMax(float velocidadMax)
    {
        super.setVelocidadMax(velocidadMax);
        cuerpo.setVelocidad(velocidadMax);
    }

    // ACTUALIZACION:
    //------------------------------------------------------------------------------------------------------------------

    @Override public void actualizarFisicaPorInterpolacion(float alpha)
    {
        cuerpo.interpolarPosicion(alpha);
        super.setPosition(cuerpo.getX(), cuerpo.getY());
        notificarSetPosition();
    }

    @Override public void actualizarFisica(float delta, MundoI mundo) {}

    // COLISIONABLE:
    //------------------------------------------------------------------------------------------------------------------

    @Override public void checkColisionesConMob(Colisionable colisionable) {}
    @Override public void checkColisionesConMuro() {}
}