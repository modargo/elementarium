package elementarium.powers;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import elementarium.Elementarium;
import elementarium.cards.CardUtil;
import elementarium.cards.CustomTags;

public class MidasAuraPower extends AbstractPower {
    public static final String POWER_ID = "Elementarium:MidasAura";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public MidasAuraPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.description = DESCRIPTIONS[0];
        this.priority = 50;
        Elementarium.LoadPowerImage(this);
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.upgraded && !card.hasTag(CustomTags.GILDED)) {
            AbstractCard gildedCard = CardUtil.gildCard(card);
            if (gildedCard != null) {
                this.flash();
                if(card.type != AbstractCard.CardType.POWER) {
                    action.exhaustCard = true;
                }
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(gildedCard, 1));
            }
        }
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}

