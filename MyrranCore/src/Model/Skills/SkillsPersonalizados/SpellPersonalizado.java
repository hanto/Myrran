package Model.Skills.SkillsPersonalizados;// Created by Hanto on 29/06/2014.

import Interfaces.Misc.Spell.BDebuffI;
import Interfaces.Misc.Spell.SkillPersonalizadoI;
import Interfaces.Misc.Spell.SpellI;
import Interfaces.Misc.Spell.SpellPersonalizadoI;

import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Iterator;

public class SpellPersonalizado implements SpellPersonalizadoI
{
    private String id;

    private SkillPersonalizado customSpell;
    private SkillPersonalizado[] listaCustomDebuffs;

    @Override public String getID()                                             { return id; }
    @Override public SkillPersonalizado getCustomSpell()                        { return customSpell; }
    @Override public Iterator<SkillPersonalizadoI> getIteratorCustomDebuffs()   { return Arrays.<SkillPersonalizadoI>asList(listaCustomDebuffs).iterator(); }
    @Override public int getNumDebuffsQueAplica()                               { return listaCustomDebuffs.length; }


    //Constructor:
    public SpellPersonalizado(SpellI spell)
    {
        this.id = spell.getID();
        customSpell = new SkillPersonalizado(spell);
        listaCustomDebuffs = new SkillPersonalizado[spell.getNumDebuffsQueAplica()];

        int numDebuff =0;
        Iterator<BDebuffI> iterator = spell.getDebuffsQueAplica();
        while (iterator.hasNext())
        {
            listaCustomDebuffs[numDebuff] = new SkillPersonalizado(iterator.next());
            numDebuff++;
        }
    }

    @Override public SkillPersonalizado getDebuffPersonalizado(String debuffID)
    {
        for (SkillPersonalizado debuffP: listaCustomDebuffs)
        {   if (debuffP.getID().equals(debuffID)) return debuffP; }
        return null;
    }

    @Override public SkillPersonalizado getSkillPersonalizado(String skillID)
    {
        if (id.equals(skillID)) return customSpell;
        else return getDebuffPersonalizado(skillID);
    }

    @Override public int getCosteTotalTalentos()
    {
        int talentosTotales = sumarCosteTotalTalentos(customSpell);
        Iterator<SkillPersonalizadoI> skill = getIteratorCustomDebuffs();
        while (skill.hasNext())
        {   talentosTotales += sumarCosteTotalTalentos(skill.next()); }
        return talentosTotales;
    }

    @Override public void añadirObservador(PropertyChangeListener observador)
    {
        customSpell.añadirObservador(observador);
        Iterator<SkillPersonalizadoI> iterator = getIteratorCustomDebuffs();
        while (iterator.hasNext())
        {   iterator.next().añadirObservador(observador); }
    }

    @Override public void eliminarObservador(PropertyChangeListener observador)
    {
        customSpell.eliminarObservador(observador);
        Iterator<SkillPersonalizadoI> iterator = getIteratorCustomDebuffs();
        while (iterator.hasNext())
        {   iterator.next().eliminarObservador(observador); }
    }


    private int sumarCosteTotalTalentos(SkillPersonalizadoI skill)
    {
        int talentosTotales = 0;
        for (int statID=0; statID < skill.getNumSkillStats(); statID++)
        {   talentosTotales += skill.getNumTalentos(statID) * skill.getCosteTalento(statID); }
        return talentosTotales;
    }
}
