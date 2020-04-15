package elementarium.events;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import elementarium.Elementarium;
import elementarium.relics.HuntersElixir;
import elementarium.relics.HuntersBracer;
import elementarium.relics.HuntersSling;

import java.text.MessageFormat;

public class BigGameHunter extends AbstractImageEvent {
    public static final String ID = "Elementarium:BigGameHunter";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = Elementarium.eventImage(ID);

    private static final int MIN_GOLD_COST = 110;
    private static final int MAX_GOLD_COST = 120;
    private static final int A15_MIN_GOLD_COST = 120;
    private static final int A15_MAX_GOLD_COST = 130;
    private static final int GOLD_TO_HEALTH_PERCENT_RATIO = 5;

    private int goldCost;
    private int maxHealthLoss;

    private int screenNum = 0;

    public BigGameHunter() {
        super(NAME, DESCRIPTIONS[0], IMG);
        if (AbstractDungeon.ascensionLevel >= 15) {
            this.goldCost = AbstractDungeon.miscRng.random(A15_MIN_GOLD_COST, A15_MAX_GOLD_COST);
        } else {
            this.goldCost = AbstractDungeon.miscRng.random(MIN_GOLD_COST, MAX_GOLD_COST);
        }

        int goldShortage = this.goldCost - AbstractDungeon.player.gold;
        if (goldShortage > 0) {
            int maxHealthLossPercentagePoints = (this.goldCost - AbstractDungeon.player.gold + GOLD_TO_HEALTH_PERCENT_RATIO - 1) / GOLD_TO_HEALTH_PERCENT_RATIO;
            float maxHealthLossPercentage = maxHealthLossPercentagePoints / 100.0F;
            this.maxHealthLoss = (int) ((float) AbstractDungeon.player.maxHealth * maxHealthLossPercentage);
            this.goldCost = AbstractDungeon.player.gold;
        }
        else {
            this.maxHealthLoss = 0;
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
                if (this.maxHealthLoss == 0) {
                    imageEventText.setDialogOption(MessageFormat.format(OPTIONS[1], this.goldCost), new HuntersSling());
                    imageEventText.setDialogOption(MessageFormat.format(OPTIONS[2], this.goldCost), new HuntersBracer());
                    imageEventText.setDialogOption(MessageFormat.format(OPTIONS[3], this.goldCost), new HuntersElixir());
                }
                else if (this.goldCost == 0) {
                    imageEventText.setDialogOption(MessageFormat.format(OPTIONS[4], this.maxHealthLoss), new HuntersSling());
                    imageEventText.setDialogOption(MessageFormat.format(OPTIONS[5], this.maxHealthLoss), new HuntersBracer());
                    imageEventText.setDialogOption(MessageFormat.format(OPTIONS[6], this.maxHealthLoss), new HuntersElixir());
                }
                else {
                    imageEventText.setDialogOption(MessageFormat.format(OPTIONS[7], this.goldCost, this.maxHealthLoss), new HuntersSling());
                    imageEventText.setDialogOption(MessageFormat.format(OPTIONS[8], this.goldCost, this.maxHealthLoss), new HuntersBracer());
                    imageEventText.setDialogOption(MessageFormat.format(OPTIONS[9], this.goldCost, this.maxHealthLoss), new HuntersElixir());
                }
                imageEventText.setDialogOption(OPTIONS[10]);
                break;
            case 1:
                switch (buttonPressed) {
                    case 0: // Sling
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.loseGoldAndHealth();
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), new HuntersSling());
                        this.screenNum = 2;
                        this.imageEventText.updateDialogOption(0, OPTIONS[10]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    case 1: // Bracer
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.loseGoldAndHealth();
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), new HuntersBracer());
                        this.screenNum = 2;
                        this.imageEventText.updateDialogOption(0, OPTIONS[10]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    case 2: // Elixir
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.loseGoldAndHealth();
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), new HuntersElixir());
                        this.screenNum = 2;
                        this.imageEventText.updateDialogOption(0, OPTIONS[10]);
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

    private void loseGoldAndHealth() {
        if (this.goldCost > 0) {
            AbstractDungeon.player.loseGold(this.goldCost);
        }
        if (this.maxHealthLoss > 0) {
            AbstractDungeon.player.decreaseMaxHealth(this.maxHealthLoss);
        }

    }
}
