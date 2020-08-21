package elementarium.events;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import elementarium.Elementarium;
import elementarium.cards.tar.StickyTar;

import java.text.MessageFormat;

public class StickySituation extends AbstractImageEvent {
    public static final String ID = "Elementarium:StickySituation";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = Elementarium.eventImage(ID);

    private static final float HP_HEAL_PERCENT = 0.35F;
    private static final float A15_HP_HEAL_PERCENT = 0.25F;
    private static final int GOLD_AMOUNT = 50;
    private static final int A15_GOLD_AMOUNT = 40;

    private int healAmount;
    private int goldAmount;

    private int screenNum = 0;

    public StickySituation() {
        super(NAME, DESCRIPTIONS[0], IMG);
        if (AbstractDungeon.ascensionLevel >= 15) {
            this.healAmount = MathUtils.round((float)AbstractDungeon.player.maxHealth * A15_HP_HEAL_PERCENT);
            this.goldAmount = A15_GOLD_AMOUNT;
        } else {
            this.healAmount = MathUtils.round((float)AbstractDungeon.player.maxHealth * HP_HEAL_PERCENT);
            this.goldAmount = GOLD_AMOUNT;
        }

        this.imageEventText.setDialogOption(MessageFormat.format(OPTIONS[0], this.healAmount), new StickyTar());
        this.imageEventText.setDialogOption(MessageFormat.format(OPTIONS[1], this.goldAmount));
    }

    protected void buttonEffect(int buttonPressed) {
        switch(this.screenNum) {
            case 0:
                switch(buttonPressed) {
                    case 0: // Drink
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        AbstractDungeon.player.heal(this.healAmount, true);
                        AbstractCard curse = new StickyTar();
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(curse, (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
                        logMetricObtainCardAndHeal(ID, "Drink", curse, this.healAmount);
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[2]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    default: // Take
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        AbstractDungeon.player.gainGold(this.goldAmount);
                        logMetricGainGold(ID, "Take", this.goldAmount);
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[2]);
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
