package Model.Classes.Skill.BDebuff.TiposBDebuff;// Created by Hanto on 26/06/2014.

import Model.Settings;
import Interfaces.Misc.BDebuff.AuraI;
import Interfaces.EntidadesPropiedades.Misc.Vulnerable;
import Model.Skills.BDebuff.TipoBDebuff;

public class Bomba extends TipoBDebuff
{
    public final int STAT_Daño = 1;

    @Override public void inicializarSkillStats()
    {
        super.inicializarSkillStats();
        setNumSkillStats(2);
    }

    @Override public void actualizarTick(AuraI aura)
    {
        if (aura.getTicksAplicados() == (int)aura.getDuracionMax()/ Settings.BDEBUFF_DuracionTick)
        {
            float daño = aura.getDebuff().getValorTotal(aura.getCaster(), STAT_Daño) * aura.getStacks();

            if(aura.getTarget() instanceof Vulnerable)
            {((Vulnerable)aura.getTarget()).modificarHPs((int) -daño); }
        }
    }
}
