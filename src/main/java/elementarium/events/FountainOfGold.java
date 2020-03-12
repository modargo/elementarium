package elementarium.events;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import elementarium.Elementarium;
import elementarium.cards.gilded.GildedForm;

import java.text.MessageFormat;

public class FountainOfGold extends AbstractImageEvent {
    public static final String ID = "Elementarium:FountainOfGold";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = Elementarium.eventImage(ID);

    private static final float MAX_HEALTH_LOSS_PERCENTAGE = 0.12F;
    private static final float A15_MAX_HEALTH_LOSS_PERCENTAGE = 0.18F;

    private int maxHealthLoss;

    private int screenNum = 0;

    public FountainOfGold() {
        super(NAME, DESCRIPTIONS[0], IMG);
        if (AbstractDungeon.ascensionLevel >= 15) {
            maxHealthLoss = (int) ((float) AbstractDungeon.player.maxHealth * A15_MAX_HEALTH_LOSS_PERCENTAGE);
        } else {
            maxHealthLoss = (int) ((float) AbstractDungeon.player.maxHealth * MAX_HEALTH_LOSS_PERCENTAGE);
        }

        imageEventText.setDialogOption(MessageFormat.format(OPTIONS[0], this.maxHealthLoss), new GildedForm());
        imageEventText.setDialogOption(OPTIONS[1]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0: // Dive in
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        AbstractDungeon.player.decreaseMaxHealth(this.maxHealthLoss);
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new GildedForm(), (float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[1]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    case 1: // Leave
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[1]);
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
