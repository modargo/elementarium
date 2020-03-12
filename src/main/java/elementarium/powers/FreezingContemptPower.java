package elementarium.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import elementarium.Elementarium;

import java.text.MessageFormat;

public class FreezingContemptPower extends AbstractPower {
    public static final String POWER_ID = "Elementarium:FreezingContempt";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    private static final int THRESHOLD = 3;

    private int skillsPlayed;

    public FreezingContemptPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.skillsPlayed = 0;
        this.updateDescription();
        this.priority = 50;
        Elementarium.LoadPowerImage(this);
    }

    @Override
    public void atStartOfTurn() {
        this.skillsPlayed = 0;
        this.updateDescription();
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.type == AbstractCard.CardType.SKILL) {
            this.skillsPlayed++;
            if (this.skillsPlayed == THRESHOLD) {
                this.flash();
                this.addToTop(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, this.amount), this.amount));
            }
        }
        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = MessageFormat.format(DESCRIPTIONS[0], this.amount, this.skillsPlayed);
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

