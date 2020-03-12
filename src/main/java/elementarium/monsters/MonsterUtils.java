package elementarium.monsters;

import com.megacrit.cardcrawl.monsters.AbstractMonster;
import elementarium.monsters.elites.WarGolem;
import elementarium.monsters.normals.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MonsterUtils {
    public static final Logger logger = LogManager.getLogger(MonsterUtils.class.getName());

    public static final String[] ElementalIDs = { OrbOfFire.ID, LivingStormcloud.ID, OpulentOffering.ID, ShimmeringMirage.ID };

    public static AbstractMonster getElemental(String elementalID, float x, float y) {
        switch (elementalID) {
            case OrbOfFire.ID:
                return new OrbOfFire(x, y);
            case LivingStormcloud.ID:
                return new LivingStormcloud(x, y);
            case OpulentOffering.ID:
                return new OpulentOffering(x, y);
            case ShimmeringMirage.ID:
                return new ShimmeringMirage(x, y);
            default:
                logger.warn("Didn't match any elemental. ElementalID:" + elementalID);
                return new OrbOfFire(x, y);
        }
    }

    public static AbstractMonster getGolem(String golemID, float x, float y) {
        switch (golemID) {
            case TarGolem.ID:
                return new TarGolem(x, y);
            case MudGolem.ID:
                return new MudGolem(x, y);
            case StoneGolem.ID:
                return new StoneGolem(x, y, true, true);
            case WarGolem.ID:
                return new WarGolem(x, y, true);
            case RubyGolem.ID:
                return new RubyGolem(x, y);
            default:
                logger.warn("Didn't match any golem. GolemID:" + golemID);
                return new TarGolem(x, y);
        }
    }
}
