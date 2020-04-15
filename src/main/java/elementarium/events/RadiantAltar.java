package elementarium.events;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.curses.Decay;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.GoldenIdol;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import elementarium.Elementarium;
import elementarium.relics.RadiantIdol;

import java.text.MessageFormat;

public class RadiantAltar extends AbstractImageEvent {
    public static final String ID = "Elementarium:RadiantAltar";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = Elementarium.eventImage(ID);

    private static final float HEALTH_LOSS_PERCENTAGE = 0.25F;
    private static final float A15_HEALTH_LOSS_PERCENTAGE = 0.35F;
    private static final int MAX_HEALTH_GAIN = 5;

    private int healthLoss;

    private int screenNum = 0;

    public RadiantAltar() {
        super(NAME, DESCRIPTIONS[0], IMG);

        if (AbstractDungeon.player.hasRelic(GoldenIdol.ID)) {
            this.imageEventText.setDialogOption(OPTIONS[0], new RadiantIdol());
        } else {
            this.imageEventText.setDialogOption(OPTIONS[1], true, new RadiantIdol());
        }

        if (AbstractDungeon.ascensionLevel >= 15) {
            this.healthLoss = (int)((float)AbstractDungeon.player.maxHealth * A15_HEALTH_LOSS_PERCENTAGE);
        } else {
            this.healthLoss = (int)((float)AbstractDungeon.player.maxHealth * HEALTH_LOSS_PERCENTAGE);
        }
        imageEventText.setDialogOption(MessageFormat.format(OPTIONS[2], MAX_HEALTH_GAIN, this.healthLoss));
        imageEventText.setDialogOption(OPTIONS[3]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0: // Offer
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        AbstractDungeon.player.loseRelic(GoldenIdol.ID);
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), new RadiantIdol());
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    case 1: // Sacrifice
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        AbstractDungeon.player.increaseMaxHp(MAX_HEALTH_GAIN, false);
                        AbstractDungeon.player.damage(new DamageInfo(null, this.healthLoss));
                        CardCrawlGame.sound.play("HEAL_3");
                        this.screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    case 2: // Desecrate
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        CardCrawlGame.sound.play("BLUNT_HEAVY");
                        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.MED, true);
                        AbstractCard curse = new Decay();
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(curse, (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
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
}
