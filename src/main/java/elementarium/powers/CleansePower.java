package elementarium.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;

import java.text.MessageFormat;

public class CleansePower extends AbstractPower {
    public static final String POWER_ID = "Elementarium:Cleanse";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    private boolean temporary;

    public CleansePower(AbstractCreature owner, int amount, boolean temporary) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.temporary = temporary;
        updateDescription();
        this.loadRegion("panache");
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        for (AbstractPower p : owner.powers) {
            if (p.type == PowerType.DEBUFF) {
                if (p.amount > 0 && p.ID != GainStrengthPower.POWER_ID) {
                    if (this.amount >= p.amount) {
                        this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, p));
                    }
                    else {
                        p.reducePower(this.amount);
                    }
                }
                else if (p.amount < 0) {
                    if (this.amount >= Math.abs(p.amount)) {
                        this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, p));
                    }
                    else {
                        p.stackPower(this.amount);
                    }
                }
            }
        }

        if (this.temporary) {
            this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, ID));
        }
    }

    @Override
    public void updateDescription() {
        this.description = MessageFormat.format(DESCRIPTIONS[0], this.amount) + (this.temporary ? " " + DESCRIPTIONS[1] : "");
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}