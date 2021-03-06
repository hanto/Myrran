package Interfaces.Misc.Spell;// Created by Hanto on 09/06/2014.

import Interfaces.EntidadesPropiedades.Propiedades.Caster;
import Interfaces.EntidadesPropiedades.Propiedades.Debuffeable;
import Interfaces.EntidadesPropiedades.Propiedades.IDentificable;
import Interfaces.Misc.Observable.ModelI;
import com.badlogic.gdx.utils.Disposable;

public interface AuraI extends IDentificable, Disposable, ModelI
{
//SET:
    public int getStacks();
    public int getTicksAplicados();
    public int getTicksRestantes();
    public int getMaxTicks();
    public float getDuracion();
    public float getDuracionMax();
    public Caster getCaster();
    public Debuffeable getTarget();
    public BDebuffI getDebuff();

    //GET:
    public void setStacks(int b);
    public void setTicksAplicados(int ticksAplicados);
    public void setDuracion(float f);
    public void setDuracionMax(float f);
    public void setCaster(Caster Caster);
    public void setTarget(Debuffeable target);
    public void setDebuff(BDebuffI debuff);

    //METODOS:
    public void resetDuracion();
    public void actualizarAura (float delta);

}
