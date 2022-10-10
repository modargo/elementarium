package elementarium.events;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import elementarium.Elementarium;
import elementarium.cards.elementalblades.*;

import java.text.MessageFormat;
import java.util.Arrays;

public class ElementalBlades extends AbstractImageEvent {
    public static final String ID = "Elementarium:ElementalBlades";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = Elementarium.eventImage(ID);

    private static final float MAX_HEALTH_LOSS_PERCENTAGE = 0.40F;
    private static final float A15_MAX_HEALTH_LOSS_PERCENTAGE = 0.50F;

    private final int maxHealthLoss;

    private int screenNum = 0;

    public ElementalBlades() {
        super(NAME, DESCRIPTIONS[0], IMG);
        if (AbstractDungeon.ascensionLevel >= 15) {
            maxHealthLoss = (int) ((float) AbstractDungeon.player.maxHealth * A15_MAX_HEALTH_LOSS_PERCENTAGE);
        } else {
            maxHealthLoss = (int) ((float) AbstractDungeon.player.maxHealth * MAX_HEALTH_LOSS_PERCENTAGE);
        }

        imageEventText.setDialogOption(OPTIONS[0], new FireblessedBlade());
        imageEventText.setDialogOption(OPTIONS[1], new WindblessedBlade());
        imageEventText.setDialogOption(OPTIONS[2], new EarthblessedBlade());
        imageEventText.setDialogOption(OPTIONS[3], new IceblessedBlade());
        imageEventText.setDialogOption(OPTIONS[4], new VoidblessedBlade());
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    default: // Examine
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.screenNum = 1;
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(MessageFormat.format(OPTIONS[5], this.maxHealthLoss));
                        this.imageEventText.setDialogOption(OPTIONS[6]);
                        break;
                }
                break;
            case 1:
                switch (buttonPressed) {
                    case 0: // Touch
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.screenNum = 2;

                        AbstractDungeon.player.decreaseMaxHealth(this.maxHealthLoss);
                        float widthPerCard = AbstractCard.IMG_WIDTH + 20.0F * Settings.scale;
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new FireblessedBlade(), (float) Settings.WIDTH / 2.0F - (2 * widthPerCard), (float) Settings.HEIGHT / 2.0F));
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new WindblessedBlade(), (float) Settings.WIDTH / 2.0F - widthPerCard, (float) Settings.HEIGHT / 2.0F));
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new EarthblessedBlade(), (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new IceblessedBlade(), (float) Settings.WIDTH / 2.0F + widthPerCard, (float) Settings.HEIGHT / 2.0F));
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new VoidblessedBlade(), (float) Settings.WIDTH / 2.0F + (2 * widthPerCard), (float) Settings.HEIGHT / 2.0F));
                        logMetricObtainCardsLoseMapHP(ID, "Touch", Arrays.asList(FireblessedBlade.ID, WindblessedBlade.ID, EarthblessedBlade.ID, IceblessedBlade.ID, VoidblessedBlade.ID), this.maxHealthLoss);

                        this.imageEventText.updateDialogOption(0, OPTIONS[7]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    default: // Leave
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        logMetricIgnored(ID);
                        this.screenNum = 2;
                        this.imageEventText.updateDialogOption(0, OPTIONS[7]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                }
                break;
            default:
                this.openMap();
                break;
        }
    }
}
