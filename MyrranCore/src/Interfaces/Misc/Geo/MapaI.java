package Interfaces.Misc.Geo;// Created by Hanto on 17/04/2014.

import Interfaces.Misc.Observable.ModelI;
import com.badlogic.gdx.utils.Disposable;

public interface MapaI extends ModelI, Disposable
{
    //GET:
    public TerrenoI getTerreno (int tileX, int tileY, int numCapa);
    public short getTerrenoID (int tileX, int tileY, int numCapa);

    //SET:
    public boolean setTerreno (int tileX, int tileY, int numCapa, TerrenoI terreno);
    public boolean setTerreno (int tileX, int tileY, int numCapa, short iDTerreno);
}
