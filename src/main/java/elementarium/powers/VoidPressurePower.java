package elementarium.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import elementarium.Elementarium;

import java.text.MessageFormat;
import java.util.ArrayList;

public class VoidPressurePower extends AbstractPower {
    public static final String POWER_ID = "Elementarium:VoidPressure";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    private AbstractCreature source;

    public VoidPressurePower(AbstractCreature owner, AbstractCreature source, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.source = source;
        this.amount = amount;
        // Deliberately not a debuff -- this is intended to simply be an effect for the Void Beast fight, but had to be coded as a player power
        this.description = MessageFormat.format(DESCRIPTIONS[0], this.amount);
        Elementarium.LoadPowerImage(this);
    }

    @Override
    public void onExhaust(AbstractCard card) {
        this.damagePlayer();
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.type == AbstractCard.CardType.POWER) {
            this.damagePlayer();
        }
    }

    public void damagePlayer() {
        this.flash();
        this.addToTop(new DamageAction(AbstractDungeon.player, new DamageInfo(this.source, this.amount, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        ArrayList<AbstractCard> cards = new ArrayList<>();
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (c.type != AbstractCard.CardType.CURSE && c.type != AbstractCard.CardType.STATUS && !c.isEthereal) {
                cards.add(c);
            }
        }
        if (!cards.isEmpty()) {
            AbstractCard card = cards.get(AbstractDungeon.cardRng.random(cards.size() - 1));
            this.addToTop(new WaitAction(0.1F));
            this.addToTop(new ExhaustSpecificCardAction(card, AbstractDungeon.player.hand));
            this.addToTop(new WaitAction(0.1F));
        }
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}