package elementarium.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardTarget;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import elementarium.Elementarium;

import java.text.MessageFormat;
import java.util.Arrays;

public class FlourishingFlamePower extends AbstractPower {
    public static final String POWER_ID = "Elementarium:FlourishingFlame";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public FlourishingFlamePower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.description = MessageFormat.format(DESCRIPTIONS[0], this.amount);
        Elementarium.LoadPowerImage(this);
    }

    public void onUseCard(AbstractCard card, UseCardAction action) {
        CardTarget[] multiTargetValues = { CardTarget.ALL, CardTarget.ALL_ENEMY };
        if (Arrays.asList(multiTargetValues).contains(card.target)
            && AbstractDungeon.getCurrRoom().monsters.monsters.stream().anyMatch(m -> m != null && m != this.owner && !m.isDying)) {
            this.flash();
            this.addToBot(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, this.amount), this.amount));
        }
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}