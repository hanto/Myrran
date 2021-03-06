package View.Gamestate.CampoVision;  //Created by Hanto on 14/04/2015.

import DTOs.*;
import Interfaces.EntidadesPropiedades.Espaciales.Espacial;
import Interfaces.EntidadesPropiedades.Espaciales.Orientable;
import Interfaces.EntidadesPropiedades.Propiedades.*;
import Interfaces.EntidadesTipos.CampoVisionI;
import Interfaces.EntidadesTipos.MobI;
import Interfaces.EntidadesTipos.PCI;
import Interfaces.EntidadesTipos.ProyectilI;
import Interfaces.Misc.GameState.MundoI;
import Interfaces.Misc.Network.ServidorI;
import Interfaces.Misc.Observable.AbstractModel;
import Interfaces.Misc.Spell.AuraI;
import Model.Settings;
import View.Gamestate.MundoView;
import com.badlogic.gdx.utils.Disposable;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CampoVision extends AbstractModel implements PropertyChangeListener, CampoVisionI
{
    protected MundoView mundoView;
    protected MundoI mundo;

    protected BufferCampoVision buffer;
    protected MapaView mapaView;
    protected targetLock targetLock;                    //target Espacial al que sigue el campo de vision
    protected int connectionID;                         //conexion a la que enviar los datos

    //UNIDADES QUE OBSERVAMOS:
    private List<PCI> listaPCsCercanos = new ArrayList<>();
    private List<ProyectilI> listaProyectilesCercanos = new ArrayList<>();
    private List<MobI> listaMobsCercanos = new ArrayList<>();

    @Override public int getConnectionID()              { return connectionID; }
    @Override public float getX()                       { return targetLock.getEspacial().getX(); }
    @Override public float getY()                       { return targetLock.getEspacial().getY(); }
    @Override public int getCuadranteX()                { return (int)(getX() / (float)(Settings.MAPTILE_NumTilesX * Settings.TILESIZE)); }
    @Override public int getCuadranteY()                { return (int)(getY() / (float)(Settings.MAPTILE_NumTilesY * Settings.TILESIZE)); }
    @Override public int getUltimoCuadranteX()          { return targetLock.getEspacial().getUltimoCuadranteX(); }
    @Override public int getUltimoCuadranteY()          { return targetLock.getEspacial().getUltimoCuadranteY(); }
    @Override public void setUltimoMapTile(int x, int y){  }
    @Override public void setPosition(float x, float y) {  }

    // CONSTRUCTOR:
    //------------------------------------------------------------------------------------------------------------------

    public CampoVision(Espacial targetCampoVision, int connectionID, MundoView mundoView)
    {
        this.mundoView = mundoView;
        this.mundo = mundoView.mundo;
        this.connectionID = connectionID;
        this.buffer = new BufferCampoVision();
        this.targetLock = new targetLock(targetCampoVision);
        this.mapaView = new MapaView(targetLock.getEspacial(), this.connectionID, mundo, buffer);
        radar();
    }

    @Override public void dispose()
    {
        for (PCI pc : listaPCsCercanos)
        {   pc.eliminarObservador(this); }
        for (MobI mob : listaMobsCercanos)
        {   mob.eliminarObservador(this); }
        for (ProyectilI pro : listaProyectilesCercanos)
        {   pro.eliminarObservador(this); }
        mapaView.dispose();
        targetLock.dispose();
        this.eliminarObservadores();
    }

    @Override public void setTargetLock(Espacial espacial)
    {
        for (PCI pc : listaPCsCercanos)
        {   pc.eliminarObservador(this); }
        listaPCsCercanos.clear();
        targetLock.setEspacial(espacial);
        radar();
    }

    // CODIGO DE RADAR:
    //-------------------------------------------------------------------------------------------------------------

    private boolean isVisiblePor(Espacial espacial)
    {
        if ( Math.abs(espacial.getX()-targetLock.getEspacial().getX()) <= (Settings.NETWORK_DistanciaVisionMobs * Settings.MAPTILE_Horizontal_Resolution/2) &&
             Math.abs(espacial.getY()-targetLock.getEspacial().getY()) <= (Settings.NETWORK_DistanciaVisionMobs * Settings.MAPTILE_Vertical_Resolution/2))
             return true;
        else return false;
    }

    private void comprobarVisiblidadPCsObservados()
    {
        Iterator<PCI> iterator = listaPCsCercanos.iterator();
        PCI pc;
        while (iterator.hasNext())
        {
            pc = iterator.next();
            if (!isVisiblePor(pc))
            {
                iterator.remove();
                pc.eliminarObservador(this);
                buffer.eliminarPC(pc);
            }
        }
    }

    private void comprobarVisibilidadMobsObservados()
    {
        Iterator<MobI> iterator = listaMobsCercanos.iterator();
        MobI mob;
        while (iterator.hasNext())
        {
            mob = iterator.next();
            if (!isVisiblePor(mob))
            {
                iterator.remove();
                mob.eliminarObservador(this);
                //TODO buffer.eliminarMob(mob);
            }
        }
    }

    private void comprobarVisibilidadProyectilesObservados()
    {
        Iterator<ProyectilI> iterator = listaProyectilesCercanos.iterator();
        ProyectilI pro;
        while (iterator.hasNext())
        {
            pro = iterator.next();
            if (!isVisiblePor(pro))
            {
                iterator.remove();
                pro.eliminarObservador(this);
                //TODO Buffer.EliminarProyectil(pro)
            }
        }
    }

    @Override public void radar()
    {
        comprobarVisiblidadPCsObservados();
        comprobarVisibilidadMobsObservados();
        comprobarVisibilidadProyectilesObservados();

        PCI pc;
        Iterator<PCI> iteratorPC = mundo.getIteratorPCs(getCuadranteX(), getCuadranteY());
        while (iteratorPC.hasNext())
        {
            pc = iteratorPC.next();
            if (isVisiblePor(pc)) añadirPC(pc);
        }

        MobI mob;
        Iterator<MobI>iteratorMob = mundo.getIteratorMobs(getCuadranteX(), getCuadranteY());
        while (iteratorMob.hasNext())
        {
            mob = iteratorMob.next();
            if (isVisiblePor(mob)) añadirMob(mob);
        }

        ProyectilI pro;
        Iterator<ProyectilI> iteratorPro = mundo.getIteratorProyectiles(getCuadranteX(), getCuadranteY());
        while (iteratorPro.hasNext())
        {
            pro = iteratorPro.next();
            if (isVisiblePor(pro)) añadirProyectil(pro);
        }
    }

    @Override public void enviarDTOs(ServidorI servidor)
    {   buffer.enviarDTOS(servidor, connectionID); }

    //PCS:
    //--------------------------------------------------------------------------------------------------------------

    public void añadirPC (PCI pc)
    {
        if (!listaPCsCercanos.contains(pc))
        {
            listaPCsCercanos.add(pc);
            pc.añadirObservador(this);
            buffer.setDatosCompletosPC(pc);
            if (pc.getID() != connectionID)
                buffer.setPositionPC(pc);
        }
    }

    private void eliminarPC (PCI pc)
    {
        if (listaPCsCercanos.contains(pc))
        {
            listaPCsCercanos.remove(pc);
            pc.eliminarObservador(this);
            if (pc.getID() != connectionID)
                buffer.eliminarPC(pc);
        }
    }

    private void posicionPC (PCI pc)
    {   if (pc.getID() != connectionID)
        buffer.setPositionPC(pc); }

    private void numAnimacionPC (PCI pc)
    {   if (pc.getID() != connectionID)
        buffer.setNumAnimacionPC(pc); }

    //PLAYER y PCs:
    //--------------------------------------------------------------------------------------------------------------

    private void modificarHPsPC (PCI pc, float hps)
    {   buffer.addModificarHPsPC(pc, hps); }

    private void añadirSpellPersonalizadoPC (PCI pc, String spellID)
    {   buffer.addAñadirSpellPersonalizado(pc, spellID); }

    private void numTalentosSkillPersonalizadoPC (PCI pc, String skillID, int statID, int valor)
    {   buffer.addNumTalentosSkillPersonalizadoPC(pc, skillID, statID, valor); }

    private void añadirAura(PCI pc, AuraI aura)
    {   buffer.addAñadirAura(pc, aura); }

    private void eliminarAura(PCI pc, AuraI aura)
    {   buffer.addEliminarAura(pc, aura); }

    private void modificarAura(PCI pc, AuraI aura)
    {   buffer.addAuraStacks(pc, aura); }

    // MOBS:
    //--------------------------------------------------------------------------------------------------------------

    private void añadirMob (MobI mob)
    {
        if (!listaMobsCercanos.contains(mob))
        {
            listaMobsCercanos.add(mob);
            mob.añadirObservador(this);
            buffer.setDatosCompletosMob(mob);
        }
    }

    private void eliminarMob (MobI mob)
    {
        if (listaMobsCercanos.contains(mob))
        {
            listaMobsCercanos.remove(mob);
            mob.eliminarObservador(this);
            //TODO buffer.eliminarMob(mob)
        }
    }

    private void posicionMob (MobI mob)
    {   buffer.setPosicionMob(mob); }

    private void orientacionMob (MobI mob)
    {   buffer.setOrientacionMob(mob); }

    private void modificarHpsMob (MobI mob, float hps)
    {   buffer.addModificarHpsMob(mob, hps); }

    private void añadirAura(MobI mob, AuraI aura)
    {   buffer.addAñadirAura(mob, aura); }

    private void eliminarAura(MobI mob, AuraI aura)
    {   buffer.addEliminarAura(mob, aura);}

    private void modificarAura(MobI mob,  AuraI aura)
    {   buffer.addAuraStacks(mob, aura);}

    // PROYECTILES:
    //--------------------------------------------------------------------------------------------------------------

    public void añadirProyectil(ProyectilI proyectil)
    {
        if (!listaProyectilesCercanos.contains(proyectil))
        {
            listaProyectilesCercanos.add(proyectil);
            proyectil.añadirObservador(this);
            if (proyectil.getOwner() instanceof PCI & ((IDentificable)proyectil.getOwner()).getID() == connectionID) {}
            else { buffer.setDatosCompletosProyectil(proyectil); }
        }
    }

    public void eliminarProyectil(ProyectilI proyectil)
    {
        if (listaProyectilesCercanos.contains(proyectil))
        {
            listaProyectilesCercanos.remove(proyectil);
            proyectil.eliminarObservador(this);
            buffer.eliminarProyectil(proyectil);
        }
    }

    //CAMPOS OBSERVADOS:
    //--------------------------------------------------------------------------------------------------------------

    @Override public void propertyChange(PropertyChangeEvent evt)
    {
        //ESPACIAL:
        if (evt.getNewValue() instanceof DTOsEspacial.Posicion)
        {
            Espacial espacial = ((DTOsEspacial.Posicion) evt.getNewValue()).espacial;
            if      (espacial instanceof PCI) this.posicionPC((PCI) espacial);
            else if (espacial instanceof MobI) this.posicionMob((MobI) espacial);
        }
        //ANIMABLE:
        else if (evt.getNewValue() instanceof DTOsAnimable.NumAnimacion)
        {
            Animable animable = ((DTOsAnimable.NumAnimacion) evt.getNewValue()).animable;
            if (animable instanceof PCI) this.numAnimacionPC((PCI) animable);
        }
        //ORIENTABLE:
        else if (evt.getNewValue() instanceof DTOsOrientable.Orientacion)
        {
            Orientable orientable = ((DTOsOrientable.Orientacion) evt.getNewValue()).orientable;
            if (orientable instanceof MobI) this.orientacionMob((MobI) orientable);
        }
        //VULNERABLE:
        else if (evt.getNewValue() instanceof DTOsVulnerable.ModificarHPs)
        {
            Vulnerable vulnerable = ((DTOsVulnerable.ModificarHPs) evt.getNewValue()).vulnerable;
            float hps = ((DTOsVulnerable.ModificarHPs) evt.getNewValue()).HPs;
            if      (vulnerable instanceof PCI) this.modificarHPsPC((PCI) vulnerable, hps);
            else if (vulnerable instanceof MobI) this.modificarHpsMob((MobI) vulnerable, hps);
        }
        //DEBUFFEABLE:
        else if (evt.getNewValue() instanceof DTOsDebuffeable.AñadirAura)
        {
            Debuffeable debuffeable = ((DTOsDebuffeable.AñadirAura) evt.getNewValue()).debuffeable;
            AuraI aura = ((DTOsDebuffeable.AñadirAura) evt.getNewValue()).aura;
            if (debuffeable instanceof MobI) this.añadirAura((MobI)debuffeable, aura);
            else if (debuffeable instanceof PCI) this.añadirAura((PCI)debuffeable, aura);
        }
        else if (evt.getNewValue() instanceof DTOsDebuffeable.EliminarAura)
        {
            Debuffeable debuffeable = ((DTOsDebuffeable.EliminarAura) evt.getNewValue()).debuffeable;
            AuraI aura = ((DTOsDebuffeable.EliminarAura) evt.getNewValue()).aura;
            if (debuffeable instanceof MobI) this.eliminarAura((MobI)debuffeable, aura);
            else if (debuffeable instanceof PCI) this.eliminarAura((PCI)debuffeable, aura);
        }
        else if (evt.getNewValue() instanceof DTOsDebuffeable.ModificarStacks)
        {
            Debuffeable debuffeable = ((DTOsDebuffeable.ModificarStacks) evt.getNewValue()).debuffeable;
            AuraI aura = ((DTOsDebuffeable.ModificarStacks) evt.getNewValue()).aura;
            if (debuffeable instanceof MobI) this.modificarAura((MobI) debuffeable, aura);
            else if (debuffeable instanceof PCI) this.modificarAura((PCI) debuffeable, aura);
        }
        //DISPOSABLE:
        else if (evt.getNewValue() instanceof DTOsDisposable.Dispose)
        {
            Disposable disposable = ((DTOsDisposable.Dispose) evt.getNewValue()).disposable;
            if      (disposable instanceof PCI) this.eliminarPC((PCI) disposable);
            else if (disposable instanceof MobI) this.eliminarMob((MobI) disposable);
            else if (disposable instanceof ProyectilI) this.eliminarProyectil((ProyectilI)disposable);
        }
        //CASTER PERSONALIZADO:
        else if (evt.getNewValue() instanceof DTOsCasterPersonalizable.AñadirSpellPersonalizado)
        {
            CasterPersonalizable caster = ((DTOsCasterPersonalizable.AñadirSpellPersonalizado) evt.getNewValue()).caster;
            if (caster instanceof PCI) this.añadirSpellPersonalizadoPC((PCI) caster, ((DTOsCasterPersonalizable.AñadirSpellPersonalizado) evt.getNewValue()).spellID);
        }

        else if (evt.getNewValue() instanceof DTOsCasterPersonalizable.SetNumTalentosSkillPersonalizado)
        {
            CasterPersonalizable caster = ((DTOsCasterPersonalizable.SetNumTalentosSkillPersonalizado) evt.getNewValue()).caster;
            String skillID = ((DTOsCasterPersonalizable.SetNumTalentosSkillPersonalizado) evt.getNewValue()).skillID;
            int statID = ((DTOsCasterPersonalizable.SetNumTalentosSkillPersonalizado) evt.getNewValue()).statID;
            int valor = ((DTOsCasterPersonalizable.SetNumTalentosSkillPersonalizado) evt.getNewValue()).valor;
            if (caster instanceof PCI) this.numTalentosSkillPersonalizadoPC((PCI) caster, skillID, statID, valor);
        }
    }

    //TARGET LOCK
    //-------------------------------------------------------------------------------------------------------------
    //La observacion del Espacial que hace de referencia del campo de vision, el punto que el campo de vision sigue
    //se hace desde una inner class para que los eventos no se mezclen y no tengamos que diferenciarlos con IFs guarros

    private class targetLock implements PropertyChangeListener, Disposable
    {
        private Espacial espacial;

        public targetLock(Espacial espacial)
        {
            this.espacial = espacial;
            this.espacial.añadirObservador(this);
        }

        @Override public void dispose()
        {   espacial.eliminarObservador(this); }

        public Espacial getEspacial()
        {   return espacial; }

        public void setEspacial(Espacial espacial)
        {
            this.espacial.eliminarObservador(this);
            this.espacial = espacial;
            this.espacial.añadirObservador(this);
        }
        
        //REACCION A EVENTOS:
        //---------------------------------------------------------------------------------------------------------
        
        private void posicion()
        {   mapaView.comprobarVistaMapa(); }

        private void eliminar()
        {   if (mundo.getPC(connectionID) != null) setTargetLock(mundo.getPC(connectionID)); }

        @Override public void propertyChange(PropertyChangeEvent evt)
        {
            if (evt.getNewValue() instanceof DTOsEspacial.Posicion) { posicion(); }
            if (evt.getNewValue() instanceof DTOsDisposable.Dispose) { eliminar(); }
        }
    }
}
