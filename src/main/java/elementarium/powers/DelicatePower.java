package elementarium.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import elementarium.Elementarium;

import java.text.MessageFormat;

public class DelicatePower extends AbstractPower {
    public static final String POWER_ID = "Elementarium:Delicate";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    private boolean used = false;

    public DelicatePower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.updateDescription();
        this.priority = 50;
        Elementarium.LoadPowerImage(this);
    }

    @Override
    public void updateDescription() {
        this.description = MessageFormat.format(DESCRIPTIONS[0], this.amount);
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (!this.used && info.type != DamageType.THORNS && damageAmount > 0) {
            this.addToTop(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, -this.amount), -this.amount));
            if (!this.owner.hasPower(ArtifactPower.POWER_ID)) {
                this.addToTop(new ApplyPowerAction(this.owner, this.owner, new GainStrengthPower(this.owner, this.amount), this.amount));
            }

            this.flash();
            this.used = true;
        }

        return damageAmount;
    }

    public void atEndOfRound() {
        this.used = false;
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

