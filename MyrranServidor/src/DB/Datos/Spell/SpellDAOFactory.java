package DB.Datos.Spell;// Created by Hanto on 17/04/2014.

import DAO.Spell.SpellDAOFactoryI;
import DAO.Spell.SpellXML;
import DAO.Spell.SpellDAO;

public enum SpellDAOFactory implements SpellDAOFactoryI
{
    XML("XML")
    {
        @Override
        public SpellDAO getSpellDAO()
        {   return new SpellXML(SpellXMLDB.get()); }
    };

    public abstract SpellDAO getSpellDAO();
    private SpellDAOFactory(String nombre)
    {}
}
