package View.Classes.UI.SpellView2;

import DB.RSC;
import Interfaces.Misc.Controlador.ControladorSkillStatsI;
import Interfaces.Misc.Spell.SkillI;
import Interfaces.Misc.Spell.SkillStatI;
import Model.Settings;
import View.Classes.Actores.Texto;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.utils.Align;

import static Model.Settings.df;

public class SkillStatView
{
    private SkillI skill;
    private SkillStatI stat;
    private ControladorSkillStatsI controlador;

    private TalentosView casillero;
    private Texto nombre;
    private Texto valorBase;
    private Texto total;
    private Texto talentos;
    private Texto costeTalento;
    private Texto bonoTalento;
    private Texto maxTalentos;

    public Container<TalentosView> cCasillero;
    public Container<Texto> cNombre;
    public Container<Texto> cValorBase;
    public Container<Texto> cTotal;
    public Container<Texto> cTalentos;
    public Container<Texto> cCosteTalento;
    public Container<Texto> cBonoTalento;
    public Container<Texto> cMaxTalentos;

    public SkillStatView(SkillI skill, int statID, ControladorSkillStatsI controlador)
    {
        this.skill = skill;
        this.stat = skill.getStats().getStat(statID);
        this.controlador = controlador;

        crearView();
    }

    // CREACION:
    //------------------------------------------------------------------------------------------------------------------

    private void crearView()
    {
        BitmapFont fuente14 = RSC.fuenteRecursosDAO.getFuentesRecursosDAO().getFuente(Settings.FUENTE_14);
        BitmapFont fuente11 = RSC.fuenteRecursosDAO.getFuentesRecursosDAO().getFuente(Settings.FUENTE_11);

        nombre        = new Texto( stat.getNombre(),
                        fuente11, Color.WHITE, Color.BLACK, Align.left, Align.bottom, 1);

        valorBase     = new Texto( df.format(stat.getValorBase()),
                        fuente14, Color.ORANGE, Color.BLACK, Align.left, Align.bottom, 1);

        casillero     = new TalentosView( skill, stat.getID(), controlador);

        total         = new Texto( df.format(stat.getTotal()),
                        fuente14, Color.GREEN, Color.BLACK, Align.left, Align.bottom, 1);

        talentos      = new Texto ( stat.getisMejorable() ? Integer.toString(stat.getNumTalentos()) : "-",
                        fuente11, Color.YELLOW, Color.BLACK, Align.left, Align.bottom, 1);

        costeTalento  = new Texto ( stat.getisMejorable() ? Integer.toString(stat.getCosteTalento()) : "-",
                        fuente11, Color.YELLOW, Color.BLACK, Align.left, Align.bottom, 1);

        bonoTalento   = new Texto ( stat.getisMejorable() ? df.format(stat.getBonoTalento()) : "-",
                        fuente11, Color.YELLOW, Color.BLACK, Align.left, Align.bottom, 1);

        maxTalentos   = new Texto ( stat.getisMejorable() ? Integer.toString(stat.getTalentosMaximos()) : "-",
                        fuente11, Color.YELLOW, Color.BLACK, Align.left, Align.bottom, 1);

        cNombre = new Container<>(nombre).left();
        cCasillero = new Container<>(casillero).left();
        cValorBase = new Container<>(valorBase).right();
        cTotal = new Container<>(total).right();
        cTalentos = new Container<>(talentos).right();
        cCosteTalento = new Container<>(costeTalento).right();
        cBonoTalento = new Container<>(bonoTalento).right();
        cMaxTalentos = new Container<>(maxTalentos).right();

    }

    public void setSkill (SkillI skill)
    {
        this.skill = skill;
        this.stat = skill.getStats().getStat(stat.getID());
        casillero.setSkill(skill);
    }

    // MODIFICACION VISTA:
    //------------------------------------------------------------------------------------------------------------------

    private void setCasillero(int numTalentos)  { casillero.setNumTalentos(numTalentos); }
    private void setValorBase(float nuevoValor) { valorBase.setTexto(df.format(nuevoValor)); cValorBase.invalidate();}
    private void setTotal(float nuevoValor)     { total.setTexto(df.format(nuevoValor)); cTotal.invalidate();}
    private void setNumTalentos(int talentos)   { this.talentos.setTexto(stat.getisMejorable() ? Integer.toString(talentos) : "-"); cTalentos.invalidate();}
    private void setCosteTalento(int coste)     { costeTalento.setTexto(stat.getisMejorable() ? Integer.toString(coste) : "-"); cCosteTalento.invalidate();}
    private void setBonoTalento(float bono)     { bonoTalento.setTexto(stat.getisMejorable() ? df.format(bono) : "-"); cBonoTalento.invalidate();}
    private void setMaxTalentos(int max)        { maxTalentos.setTexto(stat.getisMejorable() ? Integer.toString(max) : "-"); cMaxTalentos.invalidate();}

    public void actualizarTodo()
    {
        setCasillero(stat.getNumTalentos());
        setValorBase(stat.getValorBase());
        setTotal(stat.getTotal());
        setNumTalentos(stat.getNumTalentos());
        setCosteTalento(stat.getCosteTalento());
        setBonoTalento(stat.getBonoTalento());
        setMaxTalentos(stat.getTalentosMaximos());
    }
}
