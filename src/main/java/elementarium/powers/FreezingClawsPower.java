package elementarium.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import elementarium.Elementarium;

import java.text.MessageFormat;

public class FreezingClawsPower extends AbstractPower {
    public static final String POWER_ID = "Elementarium:FreezingClaws";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public FreezingClawsPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.description = MessageFormat.format(DESCRIPTIONS[0], this.amount);
        Elementarium.LoadPowerImage(this);
    }

    @Override
    public void onInflictDamage(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (damageAmount > 0 && info.type != DamageType.THORNS) {
            this.addToBot(new ApplyPowerAction(this.owner, this.owner, new MetallicizePower(this.owner, this.amount), this.amount));
        }
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}
