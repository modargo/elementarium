package elementarium.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import elementarium.Elementarium;

import java.text.MessageFormat;

public class SorrowPower extends AbstractPower {
    public static final String POWER_ID = "Elementarium:Sorrow";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    private boolean wait;

    public SorrowPower(AbstractCreature owner, int amount, boolean wait) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.wait = wait;
        updateDescription();
        Elementarium.LoadPowerImage(this);
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (this.wait) {
            this.wait = false;
            return;
        }
        boolean hasFriends = false;
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            hasFriends = hasFriends || (m != null && m != this.owner && !m.isDying && m.id.equals(this.owner.id));
        }

        if (!hasFriends) {
            this.flash();
            this.addToTop(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, this.amount), this.amount));
            this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, this));
        }
    }

    @Override
    public void updateDescription() {
        this.description = MessageFormat.format(DESCRIPTIONS[0], this.amount, this.owner.name);
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}