package elementarium.powers;

import com.megacrit.cardcrawl.actions.unique.LoseEnergyAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import elementarium.Elementarium;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnergyLimitPower extends AbstractPower {
    public static final String POWER_ID = "Elementarium:EnergyLimit";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    private static final int ENERGY_LIMIT = 3;

    private static final Logger logger = LogManager.getLogger(EnergyLimitPower.class.getName());

    public EnergyLimitPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.description = DESCRIPTIONS[0];
        this.type = PowerType.DEBUFF;
        this.priority = 10;
        Elementarium.LoadPowerImage(this);
    }

    public void atStartOfTurnPostDraw() {
        int energyToLose = AbstractDungeon.player.energy.energy - ENERGY_LIMIT;
        logger.info("Limiting energy. Player energy: " + AbstractDungeon.player.energy.energy + ". Player master energy: " + AbstractDungeon.player.energy.energyMaster + ". Amount to lose: " + energyToLose + ".");
        if (energyToLose > 0) {
            this.flash();
            this.addToBot(new LoseEnergyAction(energyToLose));
        }
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

