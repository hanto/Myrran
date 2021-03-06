package Model.Classes.Skill.Spell.TiposSpell;// Created by Hanto on 17/06/2014.

import Interfaces.Misc.GameState.MundoI;
import Interfaces.Misc.Spell.SpellI;
import Interfaces.EntidadesPropiedades.Propiedades.Caster;
import Model.Skills.Spell.TipoSpell;

public class Heal extends TipoSpell
{
    @Override public void inicializarSkill()
    {
        getStats().setNumStats(2);
        getSpellSlots().setNumSlots(0);
        debuffSlots().setNumSlots(2);
    }

    @Override public void ejecutarCasteo(SpellI spell, Caster Caster, int targetX, int targetY, MundoI mundo) {}
}
