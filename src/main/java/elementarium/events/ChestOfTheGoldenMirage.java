package elementarium.events;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import elementarium.Elementarium;
import elementarium.relics.GoldenMirage;

public class ChestOfTheGoldenMirage extends AbstractImageEvent {
    public static final String ID = "Elementarium:ChestOfTheGoldenMirage";
    private static final EventStrings eventStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    public static final String[] OPTIONS;
    public static final String IMG = Elementarium.eventImage(ID);
    private static final int GOLD_AMT = 222;
    private static final String DIALOG_1;
    private static final String MIRAGE_RESULT;
    private static final String RELIC_RESULT;

    public ChestOfTheGoldenMirage() {
        super(NAME, DIALOG_1, IMG);
        this.screenNum = 0;
        if (AbstractDungeon.player.hasRelic(GoldenMirage.ID)) {
            this.imageEventText.setDialogOption(OPTIONS[0]);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[1], true);
            this.imageEventText.setDialogOption(OPTIONS[2] + AbstractDungeon.player.gold + OPTIONS[3], new GoldenMirage());
        }

        this.imageEventText.setDialogOption(OPTIONS[4]);
    }

    protected void buttonEffect(int buttonPressed) {
        switch(this.screenNum) {
            case 0:
                if (buttonPressed == 0) {
                    AbstractDungeon.effectList.add(new RainingGoldEffect(GOLD_AMT));
                    AbstractDungeon.player.gainGold(GOLD_AMT);
                    logMetricGainGold(ID, "Presented the Golden Mirage", GOLD_AMT);
                    this.imageEventText.updateBodyText(MIRAGE_RESULT);
                    this.screenNum = 1;
                    this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                    this.imageEventText.clearRemainingOptions();
                } else if (buttonPressed == 1 && !AbstractDungeon.player.hasRelic(GoldenMirage.ID)) {
                    int gold = AbstractDungeon.player.gold;
                    AbstractRelic relic = new GoldenMirage();
                    AbstractDungeon.player.loseGold(gold);
                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), relic);
                    logMetricObtainRelicAtCost(ID, "Offer", relic, gold);
                    this.imageEventText.updateBodyText(RELIC_RESULT);
                    this.screenNum = 1;
                    this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                    this.imageEventText.clearRemainingOptions();
                } else {
                    logMetricIgnored(ID);
                    this.screenNum = 1;
                    this.openMap();
                }

                break;
            default:
                this.openMap();
                break;
        }
    }

    static {
        eventStrings = CardCrawlGame.languagePack.getEventString(ID);
        NAME = eventStrings.NAME;
        DESCRIPTIONS = eventStrings.DESCRIPTIONS;
        OPTIONS = eventStrings.OPTIONS;
        DIALOG_1 = DESCRIPTIONS[0];
        MIRAGE_RESULT = DESCRIPTIONS[1];
        RELIC_RESULT = DESCRIPTIONS[2];
    }
}
