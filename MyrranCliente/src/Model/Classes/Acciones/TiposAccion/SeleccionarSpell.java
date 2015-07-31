package Model.Classes.Acciones.TiposAccion;// Created by Hanto on 05/05/2014.

import DB.DAO;
import Model.Settings;
import Interfaces.EntidadesPropiedades.MaquinablePlayer;
import Interfaces.Spell.SpellI;
import Interfaces.UI.ControladorUI;
import Model.Classes.Acciones.Accion;

public class SeleccionarSpell extends Accion
{
    public SeleccionarSpell(SpellI spell)
    {
        iD = spell.getID();
        parametros = spell.getTipoSpell().getID();
    }

    public SeleccionarSpell(String idSpell)
    {
        SpellI spell = DAO.spellDAOFactory.getSpellDAO().getSpell(idSpell);
        iD = spell.getID();
        parametros = spell.getTipoSpell().getID();
    }

    @Override public void accionKeyDown(MaquinablePlayer player, ControladorUI controlador)
    {
        if (parametros.equals(Settings.BARRATERRENOS_EditarTerrenoID)) { controlador.mostrarBarraTerrenos(); /*player.setParametrosSpell(new ParametrosEditarTerreno());*/}

        player.getInput().setSpellID(iD);
    }

    @Override public void accionKeyUp(MaquinablePlayer player, ControladorUI controlador)
    {
        if (parametros.equals(Settings.BARRATERRENOS_EditarTerrenoID)) controlador.ocultarBarraTerrenos();
    }
}
