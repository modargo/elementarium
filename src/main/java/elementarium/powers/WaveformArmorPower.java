package elementarium.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import elementarium.Elementarium;

public class WaveformArmorPower extends AbstractPower {
    public static final String POWER_ID = "Elementarium:WaveformArmor";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public WaveformArmorPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.updateDescription();
        this.priority = 50;
        Elementarium.LoadPowerImage(this);
    }

    @Override
    public void updateDescription() {
        if (AbstractDungeon.ascensionLevel >= 17) {
            this.description = DESCRIPTIONS[0];
        }
        else {
            this.description = DESCRIPTIONS[1];
        }
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.type == AbstractCard.CardType.SKILL || card.type == AbstractCard.CardType.POWER) {
            int armor = card.costForTurn * 2;
            if (armor > 0) {
                this.flash();
                this.addToTop(new ApplyPowerAction(this.owner, this.owner, new PlatedArmorPower(this.owner, armor), armor));
            }
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
        if (AbstractDungeon.ascensionLevel >= 17) {
            this.addToBot(new ApplyPowerAction(this.owner, this.owner, new WaveformStrengthPower(this.owner)));
        }
        else {
            this.addToBot(new ApplyPowerAction(this.owner, this.owner, new WaveformStillPower(this.owner)));
        }
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

