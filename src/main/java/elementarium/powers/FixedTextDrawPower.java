package elementarium.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

// This class only exists to add a Math.abs around the number in the description for negative draw amounts
public class FixedTextDrawPower extends AbstractPower {
    public static final String POWER_ID = "Elementarium:Draw";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public FixedTextDrawPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.updateDescription();
        this.loadRegion("draw");
        if (amount < 0) {
            this.type = PowerType.DEBUFF;
            this.loadRegion("draw2");
        } else {
            this.type = PowerType.BUFF;
            this.loadRegion("draw");
        }

        this.isTurnBased = false;
        AbstractDungeon.player.gameHandSize += amount;
    }

    public void onRemove() {
        AbstractDungeon.player.gameHandSize -= this.amount;
    }

    public void reducePower(int reduceAmount) {
        this.fontScale = 8.0F;
        this.amount -= reduceAmount;
        if (this.amount == 0) {
            this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
        }
    }

    public void updateDescription() {
        if (this.amount > 0) {
            if (this.amount == 1) {
                this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
            } else {
                this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[3];
            }

            this.type = PowerType.BUFF;
        } else {
            if (this.amount == -1) {
                this.description = DESCRIPTIONS[0] + Math.abs(this.amount) + DESCRIPTIONS[2];
            } else {
                this.description = DESCRIPTIONS[0] + Math.abs(this.amount) + DESCRIPTIONS[4];
            }

            this.type = PowerType.DEBUFF;
        }
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

