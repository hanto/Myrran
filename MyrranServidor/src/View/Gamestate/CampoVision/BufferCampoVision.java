package View.Gamestate.CampoVision;// Created by Hanto on 24/07/2015.

import DTO.DTOsCampoVision;
import DTO.DTOsMapView;
import InterfacesEntidades.EntidadesTipos.MobI;
import InterfacesEntidades.EntidadesTipos.PCI;
import InterfacesEntidades.EntidadesTipos.ProyectilI;
import Interfaces.Network.ServidorI;
import Interfaces.Skill.SkillPersonalizadoI;
import Interfaces.Spell.SpellPersonalizadoI;
import Model.Skills.SkillMod;

import java.util.*;

public class BufferCampoVision
{
    //Buffer que almacena todos los cambios de las unidades observadas por el cambio de vision durante los updates
    //al final del update se envian todos los datos al player del campo de vision.

    private List<DTOsMapView.Mapa> listaDTOsMapa = new ArrayList<>();
    private List<Object> listaDTOsMisc = new ArrayList<>();
    private MapDTOs mapaDTOsPC = new MapDTOs();
    private MapDTOs mapaDTOsProyectiles = new MapDTOs();
    private MapDTOs mapaDTOsMobs = new MapDTOs();

    //DTOs para no tener que crearlos sin parar:
    private DTOsCampoVision.PCDTOs pcDTOs = new DTOsCampoVision.PCDTOs();
    private DTOsCampoVision.ProyectilDTOs proyectilDTOs = new DTOsCampoVision.ProyectilDTOs();
    private DTOsCampoVision.MobDTOs mobDTOs = new DTOsCampoVision.MobDTOs();
    private DTOsCampoVision.MiscDTOs miscDTOs = new DTOsCampoVision.MiscDTOs();


    //MANIPULACION DE LAS ESTRUCTURAS DE DATOS:
    //---------------------------------------------------------------------------------------------------------------

    private class MapDTOs
    {
        public Map<Integer, ArrayList<Object>> mapDTOs = new HashMap<>();

        public void set(Integer key, Class clase, Object dto)
        {
            if (mapDTOs.containsKey(key))
            {
                List<Object> lista = mapDTOs.get(key);
                for (int i=0; i< lista.size(); i++)
                {
                    if (clase.isInstance(lista.get(i)))
                    {   lista.set(i, dto); return; }
                }
                lista.add(dto);
            }
            else
            {
                ArrayList<Object> arrayDTOs = new ArrayList<>();
                arrayDTOs.add(dto);
                mapDTOs.put(key, arrayDTOs);
            }
        }

        public void add(Integer key, Object dto)
        {
            if (mapDTOs.containsKey(key))
            {   mapDTOs.get(key).add(dto); }
            else
            {
                ArrayList<Object> arrayDTOs = new ArrayList<>();
                arrayDTOs.add(dto);
                mapDTOs.put(key, arrayDTOs);
            }
        }

        public void remove(Integer key)
        {   mapDTOs.remove(key); }

        public void clear()
        {   mapDTOs.clear(); }
    }

    public BufferCampoVision() {}

    // PC:
    //------------------------------------------------------------------------------------------------------------------

    public void eliminarPC(PCI pc)
    {
        DTOsCampoVision.EliminarPC eliminarPC = new DTOsCampoVision.EliminarPC();
        mapaDTOsPC.remove(pc.getID());
        mapaDTOsPC.set(pc.getID(), DTOsCampoVision.EliminarPC.class, eliminarPC);
    }

    public void setPositionPC (PCI pc)
    {
        DTOsCampoVision.PosicionPC posicionPC = new DTOsCampoVision.PosicionPC(pc);
        mapaDTOsPC.set(pc.getID(), DTOsCampoVision.PosicionPC.class, posicionPC);
    }

    public void setNumAnimacionPC (PCI pc)
    {
        DTOsCampoVision.NumAnimacionPC numAnimacionPC = new DTOsCampoVision.NumAnimacionPC(pc);
        mapaDTOsPC.set(pc.getID(), DTOsCampoVision.NumAnimacionPC.class, numAnimacionPC);
    }

    // PC & PLAYER:
    //------------------------------------------------------------------------------------------------------------------

    public void setDatosCompletosPC (PCI pc)
    {
        DTOsCampoVision.DatosCompletosPC datosCompletosPC = new DTOsCampoVision.DatosCompletosPC(pc);
        mapaDTOsPC.set(pc.getID(), DTOsCampoVision.DatosCompletosPC.class, datosCompletosPC);

        Iterator<SpellPersonalizadoI> iteratorSpell = pc.getIteratorSpellPersonalizado();
        while (iteratorSpell.hasNext())
        {   addAñadirSpellPersonalizado(pc, iteratorSpell.next().getID()); }

        Iterator<SkillPersonalizadoI> iteratorSkill = pc.getIteratorSkillPersonalizado();
        while (iteratorSkill.hasNext())
        {
            SkillPersonalizadoI skill = iteratorSkill.next();
            Iterator<SkillMod> iteratorMod = skill.getIterator();
            while(iteratorMod.hasNext())
            {
                SkillMod skillmod = iteratorMod.next();
                if (skillmod.getNumTalentos()>0)
                {   addNumTalentosSkillPersonalizadoPC(pc, skill.getID(), skillmod.getID(), skillmod.getNumTalentos()); }
            }
        }
    }

    public void setNombrePC (PCI pc)
    {
        DTOsCampoVision.NombrePC nombre = new DTOsCampoVision.NombrePC(pc);
        mapaDTOsPC.set(pc.getID(), DTOsCampoVision.NombrePC.class, nombre);
    }

    public void setHPsPC (PCI pc)
    {
        DTOsCampoVision.HPsPC hpsPC = new DTOsCampoVision.HPsPC(pc);
        mapaDTOsPC.set(pc.getID(), DTOsCampoVision.HPsPC.class, hpsPC);
    }

    public void addModificarHPsPC (PCI pc, float hps)
    {
        DTOsCampoVision.ModificarHPsPC modificarHPsPC = new DTOsCampoVision.ModificarHPsPC(hps);
        mapaDTOsPC.add(pc.getID(), modificarHPsPC);
    }

    public void addAñadirSpellPersonalizado(PCI pc, String spellID)
    {
        DTOsCampoVision.AñadirSpellPersonalizadoPC añadirSpellPersonalizadoPC = new DTOsCampoVision.AñadirSpellPersonalizadoPC(spellID);
        mapaDTOsPC.add(pc.getID(), añadirSpellPersonalizadoPC);
    }

    public void addNumTalentosSkillPersonalizadoPC(PCI pc, String skillID, int statID, int valor)
    {
        DTOsCampoVision.NumTalentosSkillPersonalizadoPC numTalentosSkillPersonalizadoPC = new DTOsCampoVision.NumTalentosSkillPersonalizadoPC(skillID, statID, valor);
        mapaDTOsPC.add(pc.getID(), numTalentosSkillPersonalizadoPC);
    }

    // MOBS:
    //------------------------------------------------------------------------------------------------------------------

    public void setPosicionMob(MobI mob)
    {
        DTOsCampoVision.PosicionMob posicionMob = new DTOsCampoVision.PosicionMob(mob);
        mapaDTOsMobs.add(mob.getID(), posicionMob);
    }

    public void setOrientacionMob(MobI mob)
    {
        DTOsCampoVision.OrientacionMob orientacionMob = new DTOsCampoVision.OrientacionMob(mob);
        mapaDTOsMobs.add(mob.getID(), orientacionMob);
    }

    public void addModificarHpsMob (MobI mob, float hps)
    {
        DTOsCampoVision.ModificarHPsMob modificarHPsMob = new DTOsCampoVision.ModificarHPsMob(hps);
        mapaDTOsMobs.add(mob.getID(), modificarHPsMob);
    }

    // PROYECTILES:
    //------------------------------------------------------------------------------------------------------------------

    public void setDatosCompletosProyectil (ProyectilI proyectil)
    {
        DTOsCampoVision.DatosCompletosProyectil datosCompletos = new DTOsCampoVision.DatosCompletosProyectil(proyectil);
        mapaDTOsProyectiles.set(proyectil.getID(), DTOsCampoVision.DatosCompletosProyectil.class, datosCompletos);
    }

    public void eliminarProyectil (ProyectilI proyectil)
    {
        DTOsCampoVision.EliminarProyectil eliminarProyectil = new DTOsCampoVision.EliminarProyectil();
        mapaDTOsProyectiles.remove(proyectil.getID());
        mapaDTOsProyectiles.set(proyectil.getID(), DTOsCampoVision.EliminarProyectil.class, eliminarProyectil);
    }

    // MISC:
    //------------------------------------------------------------------------------------------------------------------

    public void addCambioTerreno (int tileX, int tileY, int numCapa, short iDTerreno)
    {
        DTOsCampoVision.CambioTerrenoMisc cambioTerreno = new DTOsCampoVision.CambioTerrenoMisc(tileX, tileY, numCapa, iDTerreno);
        listaDTOsMisc.add(cambioTerreno);
    }

    public void addMapaAdyacencias(boolean[][] mapaAdyacencias)
    {
        DTOsCampoVision.MapTilesAdyacentes mapTilesAdyacentes = new DTOsCampoVision.MapTilesAdyacentes(mapaAdyacencias);
        listaDTOsMisc.add(mapTilesAdyacentes);
    }

    // MAPAS:
    //------------------------------------------------------------------------------------------------------------------

    public void addMapa (DTOsMapView.Mapa mapa)
    {   listaDTOsMapa.add(mapa); }

    //ENVIAR DATOS A CLIENTE:
    //------------------------------------------------------------------------------------------------------------------

    private void enviarDTOsPC (ServidorI servidor, int conID)
    {
        if (mapaDTOsPC.mapDTOs.size() > 0)
        {
            //DTOsCampoVision.PCDTOs pcDTOs = new DTOsCampoVision.PCDTOs();
            for (Map.Entry<Integer, ArrayList<Object>> array : mapaDTOsPC.mapDTOs.entrySet())
            {
                pcDTOs.connectionID = array.getKey();
                pcDTOs.listaDTOs = new Object[array.getValue().size()];
                pcDTOs.listaDTOs = array.getValue().toArray();
                servidor.enviarACliente(conID, pcDTOs);
            }
            mapaDTOsPC.clear();
        }
    }

    private void enviarDTOsProyectil (ServidorI servidor, int conID)
    {
        if (mapaDTOsProyectiles.mapDTOs.size() > 0)
        {
            //DTOsCampoVision.ProyectilDTOs proyectilDTOs = new DTOsCampoVision.ProyectilDTOs();
            for (Map.Entry<Integer, ArrayList<Object>> array : mapaDTOsProyectiles.mapDTOs.entrySet())
            {
                proyectilDTOs.iD = array.getKey();
                proyectilDTOs.listaDTOs = new Object[array.getValue().size()];
                proyectilDTOs.listaDTOs = array.getValue().toArray();
                servidor.enviarACliente(conID, proyectilDTOs);
            }
            mapaDTOsProyectiles.clear();
        }
    }

    private void enviarDTOsMobs (ServidorI servidor, int conID)
    {
        if (mapaDTOsMobs.mapDTOs.size() > 0)
        {
            //DTOsCampoVision.MobDTOs mobDTOs = new DTOsCampoVision.MobDTOs();
            for (Map.Entry<Integer, ArrayList<Object>> array : mapaDTOsMobs.mapDTOs.entrySet())
            {
                mobDTOs.iD = array.getKey();
                mobDTOs.listaDTOs = new Object[array.getValue().size()];
                mobDTOs.listaDTOs = array.getValue().toArray();
                servidor.enviarACliente(conID, mobDTOs);
            }
            mapaDTOsMobs.clear();
        }
    }

    private void enviarDTOsMisc (ServidorI servidor, int conID)
    {
        if (listaDTOsMisc.size() > 0)
        {
            //DTOsCampoVision.MiscDTOs miscDTOs = new DTOsCampoVision.MiscDTOs();
            miscDTOs.listaDTOs = new Object[listaDTOsMisc.size()];
            miscDTOs.listaDTOs = listaDTOsMisc.toArray();
            servidor.enviarACliente(conID, miscDTOs);
            listaDTOsMisc.clear();
        }
    }

    private void enviarDTOsMapa(ServidorI servidor, int conID)
    {
        if (listaDTOsMapa.size() > 0)
        {
            for (DTOsMapView.Mapa mapa : listaDTOsMapa)
            {   servidor.enviarACliente(conID, mapa); }
            listaDTOsMapa.clear();
        }
    }

    public void enviarDTOS (ServidorI servidor, int conID)
    {
        enviarDTOsMapa(servidor, conID);
        enviarDTOsPC(servidor, conID);
        enviarDTOsProyectil(servidor, conID);
        enviarDTOsMobs(servidor, conID);
        enviarDTOsMisc(servidor, conID);
    }
}
