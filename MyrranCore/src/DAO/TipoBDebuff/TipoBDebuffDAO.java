package DAO.TipoBDebuff;// Created by Hanto on 10/06/2014.

import Interfaces.Misc.Spell.TipoBDebuffI;

public interface TipoBDebuffDAO
{
    public boolean añadirTipoBDebuff(TipoBDebuffI debuff);
    public void salvarTipoBDebuff(TipoBDebuffI debuff);
    public void eliminarTipoBDebuff(String debuffID);
    public TipoBDebuffI getTipoBDebuff(String debuffID);
}
