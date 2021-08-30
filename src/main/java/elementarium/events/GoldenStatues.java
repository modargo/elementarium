package elementarium.events;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.RoomEventDialog;
import com.megacrit.cardcrawl.events.city.MaskedBandits;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import elementarium.act.Encounters;
import elementarium.cards.CardUtil;
import elementarium.cards.CustomTags;
import elementarium.cards.gilded.GildedDefend;
import elementarium.relics.GoldenMirage;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;

// We extend the MaskedBandits event because ProceedButton.java specifically checks if an event is an instance of this type
// (or a few other types) in the logic for what happens when you click proceed. This is easier than a patch.
public class GoldenStatues extends MaskedBandits {
    public static final String ID = "Elementarium:GoldenStatues";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private int screen = 0;
    private AbstractCard cardCost;
    private AbstractCard cardGain;

    public GoldenStatues() {
        super();
        this.roomEventText.clear();

        this.cardCost = this.getCardCost();
        this.cardGain = CardUtil.gildCard(this.cardCost);
        if (this.cardGain == null) {
            this.cardGain = new GildedDefend();
        }
        this.body = DESCRIPTIONS[0];
        if (this.cardCost != null) {
            this.roomEventText.addDialogOption(MessageFormat.format(OPTIONS[0], this.cardCost.name, this.cardGain.name), this.cardGain);
        }
        else {
            this.roomEventText.addDialogOption(MessageFormat.format(OPTIONS[1], this.cardGain.name), this.cardGain);
        }
        this.roomEventText.addDialogOption(OPTIONS[2]);
        this.hasDialog = true;
        this.hasFocus = true;
        AbstractDungeon.getCurrRoom().monsters = MonsterHelper.getEncounter(Encounters.GOLDEN_STATUES);
    }

    @Override
    public void update() {
        super.update();
        if (!RoomEventDialog.waitForInput) {
            this.buttonEffect(this.roomEventText.getSelectedOption());
        }
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch(this.screen) {
            case 0:
                switch(buttonPressed) {
                    case 0:
                        this.exchangeCards();
                        this.roomEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.roomEventText.updateDialogOption(0, OPTIONS[3]);
                        this.roomEventText.clearRemainingOptions();
                        this.screen = 1;
                        break;
                    case 1:
                        logMetric(ID, "Fight");
                        if (Settings.isDailyRun) {
                            AbstractDungeon.getCurrRoom().addGoldToRewards(AbstractDungeon.miscRng.random(30));
                        } else {
                            AbstractDungeon.getCurrRoom().addGoldToRewards(AbstractDungeon.miscRng.random(25, 35));
                        }

                        if (AbstractDungeon.player.hasRelic(GoldenMirage.ID)) {
                            AbstractDungeon.getCurrRoom().addRelicToRewards(new Circlet());
                        } else {
                            AbstractDungeon.getCurrRoom().addRelicToRewards(new GoldenMirage());
                        }

                        this.enterCombat();
                        AbstractDungeon.lastCombatMetricKey = Encounters.GOLDEN_STATUES;
                        break;
                    }
                    break;
            default:
                this.openMap();
                break;
        }
    }

    private AbstractCard getCardCost() {
        CardGroup cg = CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards());
        ArrayList<AbstractCard> list = new ArrayList<>();
        for (AbstractCard c : cg.group) {
            if (c.rarity != AbstractCard.CardRarity.BASIC && c.type != AbstractCard.CardType.CURSE && !c.hasTag(CustomTags.GILDED)) {
                list.add(c);
            }
        }

        if (list.isEmpty()) {
            return null;
        } else {
            Collections.shuffle(list, AbstractDungeon.miscRng.random);
            return list.get(0);
        }
    }

    private void exchangeCards() {
        if (this.cardCost != null) {
            AbstractDungeon.player.masterDeck.removeCard(this.cardCost);
        }
        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(this.cardGain, (float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));

        if (this.cardCost != null) {
            logMetricObtainCardAndLoseCard(ID, "Tear free", this.cardGain, this.cardCost);
        }
        else {
            logMetricObtainCard(ID, "Tear free", this.cardGain);
        }
    }
}
