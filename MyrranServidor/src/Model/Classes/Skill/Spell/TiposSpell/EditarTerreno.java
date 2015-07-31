package Model.Classes.Skill.Spell.TiposSpell;
// @author Ivan Delgado Huerta

import Model.Settings;
import Interfaces.EntidadesPropiedades.Caster;
import Interfaces.GameState.MundoI;
import Interfaces.Spell.SpellI;
import Model.Classes.Skill.Spell.TipoSpell;

import static DTO.DTOsParametrosSpell.ParametrosEditarTerreno;

public class EditarTerreno extends TipoSpell
{
    @Override public void inicializarSkillStats() 
    {
        setID(this.getClass().getSimpleName().toUpperCase());
        setNumSkillStats(1);
    }

    @Override public void ejecutarCasteo(SpellI skill, Caster Caster, int targetX, int targetY, MundoI mundo)
    {
        int tileX = (targetX / Settings.TILESIZE);
        int tileY = (targetY / Settings.TILESIZE);

        int numCapa = 0;
        short iDTerreno = 0;

        if (Caster.getParametrosSpell() instanceof ParametrosEditarTerreno)
        {
            numCapa = ((ParametrosEditarTerreno) Caster.getParametrosSpell()).capaTerrenoSeleccionada;
            iDTerreno = ((ParametrosEditarTerreno) Caster.getParametrosSpell()).terrenoIDSeleccionado;
        }

        mundo.getMapa().setTerreno(tileX, tileY, numCapa, iDTerreno);
    }
}
