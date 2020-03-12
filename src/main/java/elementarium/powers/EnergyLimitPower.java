package elementarium.powers;

import com.megacrit.cardcrawl.actions.unique.LoseEnergyAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import elementarium.Elementarium;

public class EnergyLimitPower extends AbstractPower {
    public static final String POWER_ID = "Elementarium:EnergyLimit";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    private static final int ENERGY_LIMIT = 3;

    public EnergyLimitPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.description = DESCRIPTIONS[0];
        this.type = PowerType.DEBUFF;
        this.priority = 10;
        Elementarium.LoadPowerImage(this);
    }

    public void onInitialApplication() {
        --AbstractDungeon.player.gameHandSize;
    }

    public void onRemove() {
        ++AbstractDungeon.player.gameHandSize;
    }

    public void atStartOfTurn() {
        int energyToLose = AbstractDungeon.player.energy.energy - ENERGY_LIMIT;
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

