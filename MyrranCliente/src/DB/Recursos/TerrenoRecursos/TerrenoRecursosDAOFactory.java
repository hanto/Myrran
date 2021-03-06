package DB.Recursos.TerrenoRecursos;// Created by Ladrim on 24/04/2014.

import DB.Recursos.TerrenoRecursos.DAO.TerrenoRecursosDAO;
import DB.Recursos.TerrenoRecursos.DAO.TerrenoRecursosXML;

public enum TerrenoRecursosDAOFactory
{
    XML("XML")
    {
        @Override
        public TerrenoRecursosDAO getTerrenoRecursosDAO()
        {   return new TerrenoRecursosXML(TerrenoRecursosXMLDB.get()); }
    };

    public abstract TerrenoRecursosDAO getTerrenoRecursosDAO();
    private TerrenoRecursosDAOFactory(String nombre) {}
}
