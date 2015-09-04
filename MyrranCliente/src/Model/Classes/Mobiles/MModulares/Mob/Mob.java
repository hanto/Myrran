package Model.Classes.Mobiles.MModulares.Mob;// Created by Hanto on 04/09/2015.

import InterfacesEntidades.EntidadesPropiedades.Debuffeable;
import InterfacesEntidades.EntidadesPropiedades.Espaciales.Colisionable;
import InterfacesEntidades.EntidadesPropiedades.IDentificable;
import InterfacesEntidades.EntidadesPropiedades.MobStats;
import InterfacesEntidades.EntidadesPropiedades.Vulnerable;
import Interfaces.GameState.MundoI;
import Model.Mobiles.Cuerpos.Cuerpo;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

public class Mob extends MobNotificador
{
    private IDentificable identificable;
    private Vulnerable vulnerable;
    private Debuffeable debuffeable;
    private MobStats mobStats;

    private Cuerpo cuerpo;

    protected Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

    public Mob(int iD, Cuerpo cuerpo,
               IDentificable identificable, Vulnerable vulnerable,
               Debuffeable debuffeable, MobStats mobStats)
    {
        this.identificable = identificable;
        this.vulnerable = vulnerable;
        this.debuffeable = debuffeable;
        this.mobStats = mobStats;
        this.cuerpo = cuerpo;


        this.identificable.setID(iD);
        this.setAncho(cuerpo.getAncho());
        this.setAlto(cuerpo.getAlto());
        this.setSeguible(false);
        this.vulnerable.setMaxHPs(10000);
        this.vulnerable.setActualHPs(10000);
    }

    @Override public void dispose()
    {   cuerpo.dispose(); }

    // IDENTIFICABLE:
    //------------------------------------------------------------------------------------------------------------------
    @Override public int getID()                                    {   return identificable.getID(); }
    @Override public void setID(int iD)                             {   identificable.setID(iD); }

    // DINAMICO:
    //------------------------------------------------------------------------------------------------------------------
    @Override public void setDireccion(float x, float y)            { }
    @Override public void setDireccion(float grados)                { }

    // VULNERABLE:
    //------------------------------------------------------------------------------------------------------------------
    @Override public float getActualHPs()                           {   return vulnerable.getActualHPs(); }
    @Override public float getMaxHPs()                              {   return vulnerable.getMaxHPs(); }
    @Override public void setActualHPs(float HPs)                   {   vulnerable.setActualHPs(HPs); }
    @Override public void setMaxHPs(float HPs)                      {   vulnerable.setMaxHPs(HPs); }

    // METODOS PROPIOS:
    //------------------------------------------------------------------------------------------------------------------

    @Override public void setPosition(float x, float y)
    {
        super.setPosition(x, y);
        cuerpo.setPosition(x, y);
        notificarSetPosition();
    }

    @Override public void setOrientacion(float radianes)
    {
        super.setOrientacion(radianes);
        cuerpo.getBody().setTransform(cuerpo.getBody().getPosition().x, cuerpo.getBody().getPosition().y, radianes);
        notificarSetOrientacion();
    }

    @Override public void modificarHPs(float HPs)
    {
        vulnerable.modificarHPs(HPs);
        notificarAddModificarHPs(HPs);
    }

    // ACTUALIZACION:
    //------------------------------------------------------------------------------------------------------------------

    @Override public void actualizarTimers(float delta) {}
    @Override public void actualizarFisica(float delta, MundoI mundo) {}
    @Override public void actualizarIA(float delta, MundoI mundo) {}

    // COLISION:
    //------------------------------------------------------------------------------------------------------------------

    @Override public void checkColisionesConMob(Colisionable colisionable) {}
    @Override public void checkColisionesConMuro() {}
}
