package elementarium.powers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import elementarium.Elementarium;
import elementarium.monsters.elites.ElementalPortal;

import java.text.MessageFormat;

public class SummoningPortalPower extends AbstractPower {
    public static final String POWER_ID = "Elementarium:SummoningPortal";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public SummoningPortalPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        updateDescription();
        Elementarium.LoadPowerImage(this);
    }

    @Override
    public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
        if (this.amount > 0) {
            damageAmount = 0;
        }

        return damageAmount;
    }

    @Override
    public void updateDescription() {
        this.description = MessageFormat.format(DESCRIPTIONS[0], ElementalPortal.MAX_ELEMENTALS_ON_FIELD, this.amount).replace("{1}", this.amount + "");
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}