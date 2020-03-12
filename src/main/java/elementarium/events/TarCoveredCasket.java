package elementarium.events;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import elementarium.Elementarium;
import elementarium.cards.tar.StickyTar;

import java.text.MessageFormat;

public class TarCoveredCasket extends AbstractImageEvent {
    public static final String ID = "Elementarium:TarCoveredCasket";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = Elementarium.eventImage(ID);

    private static final int CURSE_COUNT = 1;
    private static final int A15_CURSE_COUNT = 2;

    private int curseCount;

    private int screenNum = 0;

    public TarCoveredCasket() {
        super(NAME, DESCRIPTIONS[0], IMG);
        if (AbstractDungeon.ascensionLevel >= 15) {
            curseCount = A15_CURSE_COUNT;
        }
        else {
            curseCount = CURSE_COUNT;
        }

        imageEventText.setDialogOption(MessageFormat.format(OPTIONS[0], this.curseCount), new StickyTar());
        imageEventText.setDialogOption(OPTIONS[1]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0: // Open
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        for (int i = 0; i < this.curseCount; i++) {
                            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new StickyTar(), (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
                        }
                        AbstractRelic r = AbstractDungeon.returnRandomScreenlessRelic(AbstractDungeon.returnRandomRelicTier());
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), r);
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
