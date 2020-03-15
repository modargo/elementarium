package elementarium.events;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.Colosseum;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic.RelicTier;
import elementarium.Elementarium;
import elementarium.act.Encounters;

// We extend the Colosseum event because ProceedButton.java specifically checks if an event is an instance of this type
// (or a few other types) in the logic for what happens when you click proceed. This is easier than a patch.
public class FireSanctum extends Colosseum {
    public static final String ID = "Elementarium:FireSanctum";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = Elementarium.eventImage(ID);
    private CurScreen screen;

    public FireSanctum() {
        super();
        this.imageEventText.clear();
        this.roomEventText.clear();
        this.title = NAME;
        this.body = DESCRIPTIONS[0];
        this.imageEventText.loadImage(IMG);
        type = EventType.IMAGE;
        this.noCardsInRewards = false;

        this.screen = CurScreen.INTRO;
        this.imageEventText.setDialogOption(OPTIONS[0]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch(this.screen) {
            case INTRO:
                this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                this.imageEventText.updateDialogOption(0, OPTIONS[1]);
                this.screen = CurScreen.FIGHT;
                break;
            case FIGHT:
                this.screen = CurScreen.POST_COMBAT;
                AbstractDungeon.getCurrRoom().monsters = MonsterHelper.getEncounter(Encounters.FIRE_SANCTUM_HERALD);
                AbstractDungeon.getCurrRoom().rewards.clear();
                AbstractDungeon.getCurrRoom().rewardAllowed = false;
                this.enterCombatFromImage();
                AbstractDungeon.lastCombatMetricKey = Encounters.FIRE_SANCTUM_HERALD;
                break;
            case POST_COMBAT:
                AbstractDungeon.getCurrRoom().rewardAllowed = true;
                switch(buttonPressed) {
                    case 1:
                        this.screen = CurScreen.LEAVE;
                        AbstractDungeon.getCurrRoom().monsters = MonsterHelper.getEncounter(Encounters.FIRE_SANCTUM_FIRELORD);
                        AbstractDungeon.getCurrRoom().rewards.clear();
                        AbstractDungeon.getCurrRoom().addRelicToRewards(RelicTier.RARE);
                        AbstractDungeon.getCurrRoom().addRelicToRewards(RelicTier.UNCOMMON);
                        AbstractDungeon.getCurrRoom().addGoldToRewards(100);
                        AbstractDungeon.getCurrRoom().eliteTrigger = true;
                        this.enterCombatFromImage();
                        AbstractDungeon.lastCombatMetricKey = Encounters.FIRE_SANCTUM_FIRELORD;
                        break;
                    default:
                        this.openMap();
                        break;
                }
                break;
            default:
                this.openMap();
                break;
        }
    }

    @Override
    public void logMetric(String actionTaken) {}

    @Override
    public void reopen() {
        if (this.screen != CurScreen.LEAVE) {
            AbstractDungeon.resetPlayer();
            AbstractDungeon.player.drawX = (float)Settings.WIDTH * 0.25F;
            AbstractDungeon.player.preBattlePrep();
            this.enterImageFromCombat();
            this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
            this.imageEventText.updateDialogOption(0, OPTIONS[2]);
            this.imageEventText.setDialogOption(OPTIONS[3]);
        }
    }

    private enum CurScreen {
        INTRO,
        FIGHT,
        LEAVE,
        POST_COMBAT
    }
}
