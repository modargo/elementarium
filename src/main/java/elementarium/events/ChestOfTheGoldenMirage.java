package elementarium.events;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
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
    private int screen;

    public ChestOfTheGoldenMirage() {
        super(NAME, DIALOG_1, IMG);
        this.screen = 0;
        if (AbstractDungeon.player.hasRelic(GoldenMirage.ID)) {
            this.imageEventText.setDialogOption(OPTIONS[0]);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[1], true);
            this.imageEventText.setDialogOption(OPTIONS[2] + AbstractDungeon.player.gold + OPTIONS[3], new GoldenMirage());
        }

        this.imageEventText.setDialogOption(OPTIONS[4]);
    }

    protected void buttonEffect(int buttonPressed) {
        switch(this.screen) {
            case 0:
                if (buttonPressed == 0) {
                    AbstractDungeon.effectList.add(new RainingGoldEffect(GOLD_AMT));
                    AbstractDungeon.player.gainGold(GOLD_AMT);
                    this.imageEventText.updateBodyText(MIRAGE_RESULT);
                } else if (buttonPressed == 1 && !AbstractDungeon.player.hasRelic("Red Mask")) {
                    AbstractDungeon.player.loseGold(AbstractDungeon.player.gold);
                    AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), new GoldenMirage());
                    this.imageEventText.updateBodyText(RELIC_RESULT);
                } else {
                    this.openMap();
                }

                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[4]);
                this.screen = 1;
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
