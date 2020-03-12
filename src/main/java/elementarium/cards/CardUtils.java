package elementarium.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import elementarium.cards.gilded.GildedDefend;
import elementarium.cards.gilded.GildedEssence;
import elementarium.cards.gilded.GildedStrike;

public class CardUtils {
    //Not supposed to be instantiated
    private CardUtils() {
        throw new AssertionError();
    }

    public static AbstractCard gildCard(AbstractCard card) {
        if (card == null) {
            return null;
        }
        if (card.type == AbstractCard.CardType.ATTACK){
            return new GildedStrike();
        }
        if (card.type == AbstractCard.CardType.SKILL){
            return new GildedDefend();
        }
        if (card.type == AbstractCard.CardType.POWER){
            return new GildedEssence();
        }
        return null;
    }
}
