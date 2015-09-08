package Model.Classes.Mobiles.PC;// Created by Hanto on 11/08/2015.

import DB.DAO;
import Model.Mobiles.Propiedades.*;

public enum PCFactory
{
    NUEVOPC
    {
        @Override public PC nuevo(int connectionID)
        {
            return new PC(connectionID, 48, 48, new IdentificableBase(), new CasterBase(),
                    new CasterPersonalizadoBase(DAO.spellDAOFactory), new VulnerableBase(), new Debuffeable(),
                    new PCStatsBase(), new AnimableBase());
        }
    };
    public abstract PC nuevo (int connectionID);
}