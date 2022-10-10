package elementarium.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import elementarium.cards.gilded.GildedDefend;
import elementarium.cards.gilded.GildedEssence;
import elementarium.cards.gilded.GildedStrike;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CardUtil {
    //Not supposed to be instantiated
    private CardUtil() {
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

    public static AbstractCard getOtherColorCard(AbstractCard.CardRarity rarity, List<AbstractCard.CardColor> excludedColors) {
        CardGroup anyCard = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        Iterator<Map.Entry<String, AbstractCard>> var2 = CardLibrary.cards.entrySet().iterator();

        while(true) {
            Map.Entry<String, AbstractCard> c;
            do {
                do {
                    do {
                        do {
                            if (!var2.hasNext()) {
                                anyCard.shuffle(AbstractDungeon.cardRng);
                                return anyCard.getRandomCard(true, rarity).makeCopy();
                            }

                            c = var2.next();
                        } while(c.getValue().rarity != rarity || excludedColors.contains(c.getValue().color));
                    } while(c.getValue().type == AbstractCard.CardType.CURSE);
                } while(c.getValue().type == AbstractCard.CardType.STATUS);
            } while(UnlockTracker.isCardLocked(c.getKey()) && !Settings.treatEverythingAsUnlocked());

            anyCard.addToBottom(c.getValue());
        }
    }
}
