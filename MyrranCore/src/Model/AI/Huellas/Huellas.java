package Model.AI.Huellas;

import Interfaces.EntidadesPropiedades.Espaciales.Espacial;
import Model.Settings;

import java.util.ArrayDeque;
import java.util.Iterator;

public class Huellas implements Iterable<Huella>
{
    private ArrayDeque<Huella> listaHuellas;
    private float tiempoDecayHuellas = 20f;

    public Huellas()
    {   this.listaHuellas = new ArrayDeque<>(); }

    //------------------------------------------------------------------------------------------------------------------

    @Override public Iterator<Huella> iterator()
    {   return listaHuellas.iterator(); }

    public void setTiempoDecayHuellas (float tiempoDecayHuellas)
    {   this.tiempoDecayHuellas = tiempoDecayHuellas; }

    public void añadirHuella(Espacial espacial)
    {
        Huella huella = listaHuellas.peekFirst();
        if (mismoTile(espacial, huella)) huella.duracion = 0f;
        else listaHuellas.addFirst(new Huella(espacial));
    }

    public void añadirHuella(float x, float y)
    {
        Huella huella = listaHuellas.peekFirst();
        if (mismoTile(x, y, huella)) huella.duracion = 0f;
        else listaHuellas.addFirst(new Huella(x, y));
    }

    public void actualizar (float delta)
    {
        Huella huella;
        Iterator<Huella> iterator = listaHuellas.descendingIterator();
        while (iterator.hasNext())
        {
            huella = iterator.next();
            huella.duracion += delta;
            if (huella.duracion > tiempoDecayHuellas) iterator.remove();
        }
    }

    private boolean mismoTile (Espacial espacial, Huella huella)
    {
        if (huella == null) return false;

        if ( huella.x / Settings.TILESIZE == espacial.getX() / Settings.TILESIZE &&
             huella.y / Settings.TILESIZE == espacial.getY() / Settings.TILESIZE ) return true;

        else return false;
    }

    private boolean mismoTile (float x, float y, Huella huella)
    {
        if (huella == null) return false;

        if ( huella.x / Settings.TILESIZE == x / Settings.TILESIZE &&
             huella.y / Settings.TILESIZE == y / Settings.TILESIZE ) return true;
        else return false;
    }
}
