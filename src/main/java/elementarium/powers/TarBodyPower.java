package elementarium.powers;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import elementarium.Elementarium;
import elementarium.cards.tar.Tar;

public class TarBodyPower extends AbstractPower {
    public static final String POWER_ID = "Elementarium:TarBody";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public TarBodyPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.description = DESCRIPTIONS[0];
        Elementarium.LoadPowerImage(this);
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (damageAmount > 0 && info.type == DamageInfo.DamageType.NORMAL) {
            this.flash();
            AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new Tar(), 1));
        }
        return damageAmount;
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

