package Model.Classes.Mobiles;// Created by Hanto on 07/04/2014.

import Core.Cuerpos.BodyFactory;
import Core.Cuerpos.Cuerpo;
import Core.Skills.SpellPersonalizado;
import DB.DAO;
import DTO.DTOsSkillPersonalizado;
import Interfaces.BDebuff.AuraI;
import Interfaces.EntidadesPropiedades.CasterPersonalizable;
import Interfaces.EntidadesPropiedades.Debuffeable;
import Interfaces.EntidadesTipos.MobPC;
import Interfaces.Geo.MapaI;
import Interfaces.Model.AbstractModel;
import Interfaces.Skill.SkillPersonalizadoI;
import Interfaces.Spell.SpellI;
import Interfaces.Spell.SpellPersonalizadoI;
import Model.GameState.Mundo;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

public class PC extends AbstractModel implements PropertyChangeListener, MobPC, CasterPersonalizable, Debuffeable
{
    protected Mundo mundo;
    protected int connectionID;
    protected MapaI mapa;

    protected float x;
    protected float y;
    protected int numAnimacion = 5;

    protected float velocidadMax = 80.0f;
    protected float velocidadMod = 1.0f;

    protected String nombre = "Hanto";
    protected int nivel = 1;

    protected float actualHPs=1;
    protected float maxHPs=2000;

    protected int targetX = 0;
    protected int targetY = 0;
    protected boolean castear = false;

    protected String spellIDSeleccionado = null;
    protected Object parametrosSpell;
    protected float actualCastingTime = 0.0f;
    protected float totalCastingTime = 0.0f;

    private List<AuraI>listaDeAuras = new ArrayList<>();
    private Map<String, SkillPersonalizadoI> listaSkillsPersonalizados = new HashMap<>();
    private Map<String, SpellPersonalizadoI> listaSpellsPersonalizados = new HashMap<>();

    protected Cuerpo cuerpo;
    protected PCNotificador notificador;
    protected Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

    //GET:
    @Override public int getConnectionID ()                             { return connectionID; }
    @Override public float getX()                                       { return x; }
    @Override public float getY()                                       { return y; }
    @Override public int getNumAnimacion()                              { return numAnimacion; }
    @Override public float getVelocidadMod()                            { return velocidadMod; }
    @Override public float getVelocidadMax()                            { return velocidadMax; }
    @Override public String getNombre()                                 { return nombre; }
    @Override public int getNivel()                                     { return nivel; }
    @Override public float getActualHPs()                               { return actualHPs; }
    @Override public float getMaxHPs()                                  { return maxHPs; }
    @Override public MapaI getMapa()                                    { return mapa; }
    @Override public boolean isCasteando()                              { if (actualCastingTime >0) return true; else return false; }
    @Override public float getActualCastingTime()                       { return actualCastingTime; }
    @Override public float getTotalCastingTime()                        { return totalCastingTime; }
    @Override public String getSpellIDSeleccionado()                    { return spellIDSeleccionado; }
    @Override public Object getParametrosSpell()                        { return parametrosSpell; }


    //SET:
    @Override public void setConnectionID (int connectionID)            { this.connectionID = connectionID; }
    @Override public void setDireccion(float x, float y)                { cuerpo.setDireccion(x, y); }
    @Override public void setDireccion(float grados)                    { cuerpo.setDireccion(grados); }
    @Override public void setVectorDireccion(float x, float y)          { cuerpo.setVectorDireccion(x, y); }
    @Override public void setTotalCastingTime(float castingTime)        { actualCastingTime = 0.01f; totalCastingTime = castingTime;}
    @Override public void setVelocidaMod(float velocidadMod)            { this.velocidadMod = velocidadMod; }
    @Override public void setVelocidadMax(float velocidadMax)           { this.velocidadMax = velocidadMax; }
    @Override public void setSpellIDSeleccionado(String spellID)        { spellIDSeleccionado = spellID; }
    @Override public void setParametrosSpell(Object parametros)         { parametrosSpell = parametros; }
    @Override public void setNombre(String nombre)                      { this.nombre = nombre; }
    @Override public void setNivel(int nivel)                           { this.nivel = nivel; }
    @Override public void añadirAura(AuraI aura)                        { listaDeAuras.add(aura); }
    @Override public void eliminarAura(AuraI aura)                      { listaDeAuras.remove(aura); }
    @Override public Iterator<AuraI> getAuras()                         { return listaDeAuras.iterator(); }
    @Override public void setMaxHPs(float HPs)                          { maxHPs = HPs; }
    @Override public void setActualHPs(float HPs)                       { modificarHPs(HPs - actualHPs); }
    @Override public SkillPersonalizadoI getSkillPersonalizado(String skillID){ return listaSkillsPersonalizados.get(skillID); }
    @Override public SpellPersonalizadoI getSpellPersonalizado(String spellID) { return listaSpellsPersonalizados.get(spellID); }
    public Iterator<SpellPersonalizadoI> getIteratorSpellPersonalizado(){ return listaSpellsPersonalizados.values().iterator(); }

    //Constructor:
    public PC(int connectionID, Mundo mundo)
    {
        this.mundo = mundo;
        this.connectionID = connectionID;
        this.mapa = mundo.getMapa();
        this.notificador = new PCNotificador(this);

        cuerpo = new Cuerpo(mundo.getWorld(), 48, 48);
        BodyFactory.darCuerpo.RECTANGULAR.nuevo(cuerpo);
    }

    public void dispose()
    {
        //Dejamos de observar a cada uno de los Spells Personalizados:
        Iterator<SpellPersonalizadoI> iSpell = getIteratorSpellPersonalizado();
        while (iSpell.hasNext()) { iSpell.next().eliminarObservador(this); }
        //le decimos a la vista que desaparezca:
        notificador.setDispose();
    }

    @Override public void añadirSkillsPersonalizados(String spellID)
    {
        SpellI spell = DAO.spellDAOFactory.getSpellDAO().getSpell(spellID);
        if (spell == null) { logger.error("ERROR: añadirSkillsPersonalizados: spellID no encontrado: {}", spellID); return; }

        SpellPersonalizado spellPersonalizado = new SpellPersonalizado(spell);
        listaSpellsPersonalizados.put(spellPersonalizado.getID(), spellPersonalizado);

        listaSkillsPersonalizados.put(spellPersonalizado.getCustomSpell().getID(), spellPersonalizado.getCustomSpell());
        Iterator<SkillPersonalizadoI> iterator = spellPersonalizado.getIteratorCustomDebuffs();
        while(iterator.hasNext())
        {
            SkillPersonalizadoI skillPersonalizado = iterator.next();
            listaSkillsPersonalizados.put(skillPersonalizado.getID(), skillPersonalizado);
        }

        spellPersonalizado.añadirObservador(this);
        notificador.añadirSkillPersonalizado(spell.getID());
    }

    @Override public void setNumTalentosSkillPersonalizado(String skillID, int statID, int valor)
    {
        SkillPersonalizadoI skillPersonalizado = listaSkillsPersonalizados.get(skillID);
        if (skillPersonalizado == null) { logger.error("ERROR: setNumTalentosSkillPersonalizado, spellID no existe: {}", skillID); return; }
        else if (valor > 0 && valor < skillPersonalizado.getTalentoMaximo(statID))
            skillPersonalizado.setNumTalentos(statID, valor);
    }

    @Override public void modificarHPs(float HPs)
    {
        actualHPs += HPs;
        if (actualHPs > maxHPs) actualHPs = maxHPs;
        else if (actualHPs < 0) actualHPs = 0;
        notificador.añadirModificarHPs(HPs);
    }

    @Override public void setPosition(float x, float y)
    {
        this.x = x; this.y = y;
        notificador.setPosition(x, y);
    }

    @Override public void setNumAnimacion(int numAnimacion)
    {
        this.numAnimacion = numAnimacion;
        notificador.setNumAnimacion(numAnimacion);
    }

    public void setCastear (boolean castear, int targetX, int targetY)
    {
        this.castear = castear;
        this.targetX = targetX;
        this.targetY = targetY;
        if (castear) castear();
    }

    private void castear()
    {
        if (!isCasteando())
        {
            SpellI spell = DB.DAO.spellDAOFactory.getSpellDAO().getSpell(spellIDSeleccionado);
            if (spell != null)
            {
                spell.castear(this, targetX, targetY);
                //actualCastingTime += 0.01f;
            }
        }
    }

    private void actualizarCastingTime(float delta)
    {
        if (isCasteando())
        {
            actualCastingTime += delta;
            if (actualCastingTime >= totalCastingTime)
            {
                actualCastingTime = 0f;
                totalCastingTime = 0f;
            }
        }
    }

    public void actualizarAuras (float delta)
    {
        AuraI aura;
        Iterator<AuraI> aurasIteator = getAuras();
        while (aurasIteator.hasNext())
        {
            aura = aurasIteator.next();
            aura.actualizarAura(delta);
            if (aura.getDuracion() >= aura.getDuracionMax())
                aurasIteator.remove();
        }
    }

    public void actualizar(float delta)
    {
        actualizarCastingTime(delta);
        actualizarAuras(delta);
        if (castear) castear();
    }

    @Override public void propertyChange(PropertyChangeEvent evt)
    {
        if (evt.getNewValue() instanceof DTOsSkillPersonalizado.SetNumTalentos)
        {
            //Si alguien cambia mis Skills Personalizados de cualquier manera (hay varios metodos de hacerlo, se notifica
            //a la vista para que envie el mensaje al cliente:
            notificador.añadirNumTalentosSkillPersonalizado(
                ((DTOsSkillPersonalizado.SetNumTalentos) evt.getNewValue()).skillID,
                ((DTOsSkillPersonalizado.SetNumTalentos) evt.getNewValue()).statID,
                ((DTOsSkillPersonalizado.SetNumTalentos) evt.getNewValue()).valor);
        }

    }
}
