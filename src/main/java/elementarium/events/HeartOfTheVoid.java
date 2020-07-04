package elementarium.events;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.Apparition;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import elementarium.Elementarium;
import elementarium.relics.FlickeringLantern;

import java.text.MessageFormat;

public class HeartOfTheVoid extends AbstractImageEvent {
    public static final String ID = "Elementarium:HeartOfTheVoid";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = Elementarium.eventImage(ID);

    private static final float HEAL_PERCENT = 0.24F;
    private static final float A15_HEAL_PERCENT = 0.18F;
    private static final float MAX_HEALTH_LOSS_PERCENTAGE = 0.12F;
    private static final float A15_MAX_HEALTH_LOSS_PERCENTAGE = 0.16F;

    private AbstractRelic relic;
    private AbstractCard card;
    private int heal;
    private int maxHealthLoss;

    private int screenNum = 0;

    public HeartOfTheVoid() {
        super(NAME, DESCRIPTIONS[0], IMG);

        this.relic = new FlickeringLantern();
        this.card = new Apparition();
        if (AbstractDungeon.ascensionLevel >= 15) {
            this.heal = (int) ((float) AbstractDungeon.player.maxHealth * A15_HEAL_PERCENT);
            this.maxHealthLoss = (int) ((float) AbstractDungeon.player.maxHealth * A15_MAX_HEALTH_LOSS_PERCENTAGE);
        }
        else {
            this.heal = (int) ((float) AbstractDungeon.player.maxHealth * HEAL_PERCENT);
            this.maxHealthLoss = (int) ((float) AbstractDungeon.player.maxHealth * MAX_HEALTH_LOSS_PERCENTAGE);
        }

        imageEventText.setDialogOption(OPTIONS[0]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                this.screenNum = 1;
                this.imageEventText.clearAllDialogs();
                imageEventText.setDialogOption(MessageFormat.format(OPTIONS[1], this.heal));
                imageEventText.setDialogOption(MessageFormat.format(OPTIONS[2], this.relic.name), this.relic);
                imageEventText.setDialogOption(MessageFormat.format(OPTIONS[3], this.maxHealthLoss, this.card.name), this.card);
                break;
            case 1:
                switch (buttonPressed) {
                    case 0: // Rest
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        AbstractDungeon.player.heal(this.heal, true);
                        this.screenNum = 2;
                        this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    case 1: // Take
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float) (Settings.WIDTH / 2), (float) (Settings.HEIGHT / 2), this.relic);
                        this.screenNum = 2;
                        this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    case 2: // Touch
                        this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
                        AbstractDungeon.player.decreaseMaxHealth(this.maxHealthLoss);
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(this.card, (float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                        this.screenNum = 2;
                        this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    default: // Leave
                        this.openMap();
                        break;
                }
                break;
            default:
                this.openMap();
                break;
        }
    }
}
