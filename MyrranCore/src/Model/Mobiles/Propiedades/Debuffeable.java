package Model.Mobiles.Propiedades;// Created by Hanto on 03/09/2015.

import Interfaces.EntidadesPropiedades.Propiedades.Caster;
import Interfaces.EntidadesPropiedades.Propiedades.DebuffeableI;
import Interfaces.Misc.Spell.AuraI;
import Interfaces.Misc.Spell.BDebuffI;
import Model.Settings;
import Model.Skills.BDebuff.Aura;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Debuffeable implements DebuffeableI
{
    protected List<AuraI> listaDeAuras = new ArrayList<>();
    protected DeBuffeableNotificadorI notificador;
    protected int auraID = 0;
    public boolean isServidor = true;

    public void setNotificador(DeBuffeableNotificadorI notificador)
    {   this.notificador = notificador; }

    public AuraI getAura(int auraID)
    {
        for (AuraI aura : listaDeAuras)
        {
            if (aura.getID() == auraID)
            {   return aura;}
        }
        return null;
    }

    public void añadirAura(BDebuffI debuff, Caster caster, DebuffeableI target)
    {
        AuraI aura = auraExisteYEsDelCaster(debuff, caster);
        if (aura != null)
        {
            incrementarStackYRestearDuracion(aura, debuff);
            notificador.notificarIncrementarStack(aura);
        }
        else
        {
            aura = new Aura(auraID++, debuff, caster, target);
            listaDeAuras.add(aura);
            notificador.notificarAñadirAura(aura);
        }
    }

    public void añadirAura(int iDAura, BDebuffI debuff, Caster caster, DebuffeableI target)
    {
        AuraI aura;/* = auraExisteYEsDelCaster(debuff, caster);
        if (aura != null)
        {
            incrementarStackYRestearDuracion(aura, debuff);
            notificador.notificarIncrementarStack(aura);
        }
        else*/
        {
            aura = new Aura(iDAura, debuff, caster, target);
            listaDeAuras.add(aura);
            notificador.notificarAñadirAura(aura);
        }
    }

    public void eliminarAura(int iDAura)
    {
        AuraI aura;
        Iterator<AuraI> iterator = listaDeAuras.iterator();
        while(iterator.hasNext())
        {
            aura = iterator.next();
            if(aura.getID() == iDAura)
            {
                notificador.notificarEliminarAura(aura);
                iterator.remove();
            }
        }
    }

    public void actualizarAuras (float delta)
    {
        AuraI aura;
        Iterator<AuraI> iterator = listaDeAuras.iterator();
        while (iterator.hasNext())
        {
            aura = iterator.next();
            aura.actualizarAura(delta);
            if (isServidor)
            {
                if (aura.getDuracion() >= aura.getDuracionMax()) {
                    notificador.notificarEliminarAura(aura);
                    iterator.remove();
                }
            }
        }
    }


    // METODOS PROPIOS:
    //------------------------------------------------------------------------------------------------------------------

    private void incrementarStackYRestearDuracion(AuraI aura, BDebuffI debuff)
    {
        //incrementamos Stacks:
        if (aura.getStacks() < debuff.getStacksMaximos()) aura.setStacks(aura.getStacks()+1);
        //reseteamos duracion: (mantenemos resto)
        aura.setTicksAplicados(0);
        aura.setDuracion(aura.getDuracion() % Settings.BDEBUFF_DuracionTick);
    }

    private AuraI auraExisteYEsDelCaster(BDebuffI debuff, Caster caster)
    {
        for (AuraI aura : listaDeAuras)
        {
            if (aura.getDebuff().getID().equals(debuff.getID()) && aura.getCaster() == caster)
                return aura;
        }
        return null;
    }
}