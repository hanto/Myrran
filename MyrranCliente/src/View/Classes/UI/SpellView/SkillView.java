package View.Classes.UI.SpellView;// Created by Hanto on 27/06/2014.

import DB.RSC;
import DTOs.DTOsSkillPersonalizado;
import Interfaces.EntidadesPropiedades.Propiedades.CasterPersonalizable;
import Interfaces.Misc.Spell.SkillPersonalizadoI;
import Model.Settings;
import View.Classes.Actores.Texto;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class SkillView implements PropertyChangeListener, Disposable
{
    //Model:
    private SkillPersonalizadoI skill;
    private CasterPersonalizable caster;

    private static class SkillStatsView
    {
        public Texto nombre;
        public Texto valorBase;
        public CasilleroTalentos casillero;
        public Texto valorTotal;
        public Texto talentos;
        public Texto costeTalento;
        public Texto bonoTalento;
        public Texto maxTalentos;
    }

    private Texto nombreSkill;
    private SkillStatsView[] skillStat;

    public int getNumSkillStats()                                               { return skillStat.length; }
    public Texto getNombreSkill()                                               { return nombreSkill; }
    public Texto getNombre(int statID)                                          { return skillStat[statID].nombre; }
    public Texto getValorBase(int statID)                                       { return skillStat[statID].valorBase; }
    public CasilleroTalentos getCasilleroTalentos(int statID)                   { return skillStat[statID].casillero; }
    public Texto getValorTotal(int statID)                                      { return skillStat[statID].valorTotal; }
    public Texto getTalentos(int statID)                                        { return skillStat[statID].talentos; }
    public Texto getCosteTalento(int statID)                                    { return skillStat[statID].costeTalento; }
    public Texto getBonoTalento(int statID)                                     { return skillStat[statID].bonoTalento; }
    public Texto getMaxTalentos(int statID)                                     { return skillStat[statID].maxTalentos; }

    public void setNombreSkill(Texto nombre)                                    { nombreSkill = nombre; }
    public void setNombre(int statID, Texto nombre)                             { skillStat[statID].nombre = nombre; }
    public void setValorBase(int statID, Texto valor)                           { skillStat[statID].valorBase = valor; }
    public void setCasilleroTalentos(int statID, CasilleroTalentos casillero)   { skillStat[statID].casillero = casillero; }
    public void setValorTotal(int statID, Texto valorTotal)                     { skillStat[statID].valorTotal = valorTotal; }
    public void setTalentos(int statID, Texto talentos)                         { skillStat[statID].talentos = talentos; }
    public void setCosteTalento(int statID, Texto costeTalento)                 { skillStat[statID].costeTalento = costeTalento; }
    public void setBonoTalentos(int statID, Texto bonoTalento)                  { skillStat[statID].bonoTalento = bonoTalento; }
    public void setMaxTalentos(int statID, Texto maxTalentos)                   { skillStat[statID].maxTalentos = maxTalentos; }

    public SkillView (SkillPersonalizadoI skill, CasterPersonalizable caster)
    {
        this.skill = skill;
        this.caster = caster;

        skillStat = new SkillStatsView[skill.getNumSkillStats()];
        for (int i=0; i<skill.getNumSkillStats(); i++) skillStat[i] = new SkillStatsView();

        crearView();
        skill.añadirObservador(this);
    }

    @Override public void dispose()
    {   skill.eliminarObservador(this); }

    private void crearView()
    {
        BitmapFont cabecera = RSC.fuenteRecursosDAO.getFuentesRecursosDAO().getFuente(Settings.FUENTE_20);
        BitmapFont normal = RSC.fuenteRecursosDAO.getFuentesRecursosDAO().getFuente(Settings.FUENTE_14);
        BitmapFont mini = RSC.fuenteRecursosDAO.getFuentesRecursosDAO().getFuente(Settings.FUENTE_11);
        BitmapFont smini = RSC.fuenteRecursosDAO.getFuentesRecursosDAO().getFuente(Settings.FUENTE_10);
        BitmapFont fuenteNombre;
        Texto texto;

        DecimalFormat df = new DecimalFormat("0.00");
        DecimalFormatSymbols symbols = df.getDecimalFormatSymbols();

        symbols.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(symbols);

        if (skill.isDebuff())   { fuenteNombre = smini; }
        else                    { fuenteNombre = cabecera; }

        texto = new Texto(skill.getNombre(), fuenteNombre, Color.ORANGE, Color.BLACK, Align.left, Align.bottom, 1);
        setNombreSkill(texto);

        for (int statID=0; statID< skill.getNumSkillStats(); statID++)
        {
            //NOMBRE:
            texto = new Texto(skill.getNombre(statID),
                    mini, Color.WHITE, Color.BLACK, Align.left, Align.bottom, 1);
            setNombre(statID, texto);

            //VALOR BASE:
            texto = new Texto(df.format(skill.getValorBase(statID)),
                    normal, Color.ORANGE, Color.BLACK, Align.left, Align.bottom, 1);
            setValorBase(statID, texto);

            //CASILLERO
            setCasilleroTalentos(statID, new CasilleroTalentos(caster, skill.getID(), statID, skill.getNumTalentos(statID)));

            //VALOR TOTAL: (redondeamos a 2 decimales maximo)
            texto = new Texto(df.format(skill.getValorTotal(statID)),
                    normal, Color.GREEN, Color.BLACK, Align.left, Align.bottom, 1);
            setValorTotal(statID, texto);

            //NUMTALENTOS:
            texto = new Texto(skill.getIsMejorable(statID) ? Integer.toString(skill.getNumTalentos(statID)) : "-",
                    mini, Color.YELLOW, Color.BLACK, Align.left, Align.bottom, 1);
            setTalentos(statID, texto);

            //COSTE TALENTO:
            texto = new Texto(skill.getIsMejorable(statID) ? Integer.toString(skill.getCosteTalento(statID)) : "-",
                    mini, Color.YELLOW, Color.BLACK, Align.left, Align.bottom, 1);
            setCosteTalento(statID, texto);

            //BONO TALENTO:
            texto = new Texto(skill.getIsMejorable(statID) ? df.format(skill.getBonoTalento(statID)) : "-",
                    mini, Color.YELLOW, Color.BLACK, Align.left, Align.bottom, 1);
            setBonoTalentos(statID, texto);

            //NUMTALENTOS MAXIMOS:
            texto = new Texto(skill.getIsMejorable(statID) ? Integer.toString(skill.getTalentoMaximo(statID)): "-",
                    mini, Color.YELLOW, Color.BLACK, Align.left, Align.bottom, 1);
            setMaxTalentos(statID, texto);
        }
    }


    private void modificarNumTalentos(int statID, int valor)
    {
        DecimalFormat df = new DecimalFormat("0.00");
        DecimalFormatSymbols symbols = df.getDecimalFormatSymbols();

        symbols.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(symbols);

        getTalentos(statID).setTexto(Integer.toString(valor));
        getCasilleroTalentos(statID).setNumTalentos(valor);
        getValorTotal(statID).setTexto(df.format(skill.getValorTotal(statID)));
    }


    @Override public void propertyChange(PropertyChangeEvent evt)
    {
        if (evt.getNewValue() instanceof DTOsSkillPersonalizado.SetNumTalentos)
        {
            int statID = ((DTOsSkillPersonalizado.SetNumTalentos) evt.getNewValue()).statID;
            int valor = ((DTOsSkillPersonalizado.SetNumTalentos) evt.getNewValue()).valor;
            modificarNumTalentos(statID, valor);
        }
    }
}
