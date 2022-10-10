package elementarium.events;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import elementarium.Elementarium;
import elementarium.cards.gilded.GildedDefend;
import elementarium.cards.gilded.GildedEssence;
import elementarium.cards.gilded.GildedStrike;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;

public class OnrushingOffering extends AbstractImageEvent {
    public static final String ID = "Elementarium:OnrushingOffering";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = Elementarium.eventImage(ID);

    private static final float HEALTH_LOSS_PERCENTAGE = 0.20F;
    private static final float A15_HEALTH_LOSS_PERCENTAGE = 0.25F;
    private static final int GOLD_GAIN_AMOUNT = 50;
    private static final int A15_GOLD_GAIN_AMOUNT = 40;
    private static final int GOLD_LOSS_AMOUNT = 10;
    private static final int A15_GOLD_LOSS_AMOUNT = 20;
    private static final int GILDED_STRIKE_COUNT = 4;

    private final int healthLoss;
    private final int goldGain;
    private final int goldLoss;

    private int screenNum = 0;

    public OnrushingOffering() {
        super(NAME, DESCRIPTIONS[0], IMG);
        if (AbstractDungeon.ascensionLevel >= 15) {
            healthLoss = (int) ((float) AbstractDungeon.player.maxHealth * HEALTH_LOSS_PERCENTAGE);
            goldGain = A15_GOLD_GAIN_AMOUNT;
            goldLoss = A15_GOLD_LOSS_AMOUNT;
        } else {
            healthLoss = (int) ((float) AbstractDungeon.player.maxHealth * A15_HEALTH_LOSS_PERCENTAGE);
            goldGain = GOLD_GAIN_AMOUNT;
            goldLoss = GOLD_LOSS_AMOUNT;
        }

        imageEventText.setDialogOption(MessageFormat.format(OPTIONS[0], GILDED_STRIKE_COUNT, this.healthLoss), new GildedStrike());
        imageEventText.setDialogOption(MessageFormat.format(OPTIONS[1], this.goldGain), new GildedDefend());
        if (AbstractDungeon.player.gold >= this.goldLoss) {
            imageEventText.setDialogOption(MessageFormat.format(OPTIONS[2], this.goldLoss), new GildedEssence());
        }
        else {
            imageEventText.setDialogOption(MessageFormat.format(OPTIONS[3], this.goldLoss), true);
        }
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0: // Strike
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        AbstractDungeon.player.damage(new DamageInfo(null, this.healthLoss));
                        this.replaceAttacks();
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    case 1: // Defend
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        AbstractCard gildedDefend = new GildedDefend();
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(gildedDefend, (float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                        AbstractDungeon.effectList.add(new RainingGoldEffect(this.goldGain));
                        AbstractDungeon.player.gainGold(this.goldGain);
                        logMetricGainGoldAndCard(ID, "Defend", gildedDefend, this.goldGain);
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    case 2: // Toss
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        AbstractCard gildedEssence = new GildedEssence();
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(gildedEssence, (float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                        AbstractDungeon.player.loseGold(this.goldLoss);
                        logMetric(ID, "Toss", Collections.singletonList(gildedEssence.cardID), null, null, null, null, null, null, 0, 0, 0, 0, 0, this.goldLoss);
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                }
                break;
            default:
                this.openMap();
                break;
        }
    }

    private void replaceAttacks() {
        ArrayList<AbstractCard> masterDeck = AbstractDungeon.player.masterDeck.group;

        ArrayList<AbstractCard> strikes = new ArrayList<>();
        for (AbstractCard card : masterDeck) {
            if (card.tags.contains(AbstractCard.CardTags.STARTER_STRIKE)) {
                strikes.add(card);
            }
        }
        for (AbstractCard card : strikes) {
            AbstractDungeon.player.masterDeck.removeCard(card);
        }

        ArrayList<String> gildedStrikes = new ArrayList<>();
        for (int i = 0; i < GILDED_STRIKE_COUNT; i++) {
            AbstractCard c = new GildedStrike();
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c, (float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
            gildedStrikes.add(c.cardID);
        }
        logMetric(ID, "Strike", gildedStrikes, null, null, null, null, null, null, this.healthLoss, 0, 0, 0, 0, 0);
    }
}
