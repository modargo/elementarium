package elementarium.events;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.Colosseum;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import elementarium.Elementarium;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

// We extend the Colosseum event because ProceedButton.java specifically checks if an event is an instance of this type
// (or a few other types) in the logic for what happens when you click proceed. This is easier than a patch.
public class BeastsOfTheMenagerie extends Colosseum {
    public static final String ID = "Elementarium:BeastsOfTheMenagerie";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = Elementarium.eventImage(ID);
    private static final Logger logger = LogManager.getLogger(BeastsOfTheMenagerie.class.getName());

    private static final String Chimera = "Menagerie:Chimera";
    private static final String Suneater = "Menagerie:Suneater";
    private static final String Avatars = "Menagerie:AVATARS";

    private static final int STRENGTH = 1;
    private static final int A15_STRENGTH = 2;
    private static final int HP_MULTIPLIER = 20;
    private static final int A15_HP_MULTIPLIER = 30;

    private int screenNum = 0;
    private String bossID;
    private int strength;
    private int hpMultiplier;

    public BeastsOfTheMenagerie() {
        super();
        this.imageEventText.clear();
        this.roomEventText.clear();
        this.title = NAME;
        this.bossID = this.getRandomMenagerieBoss();
        this.body = DESCRIPTIONS[0] + " NL NL " + this.getTextForBoss(this.bossID);
        this.imageEventText.loadImage(IMG);
        type = EventType.IMAGE;
        this.noCardsInRewards = false;

        if (AbstractDungeon.ascensionLevel >= 15) {
            this.strength = A15_STRENGTH;
            this.hpMultiplier = A15_HP_MULTIPLIER;
        }
        else {
            this.strength = STRENGTH;
            this.hpMultiplier = HP_MULTIPLIER;
        }

        this.imageEventText.setDialogOption(OPTIONS[0].replace("{0}", this.strength + "").replace("{1}", this.hpMultiplier + ""));
        this.imageEventText.setDialogOption(OPTIONS[1]);
    }

    private String getRandomMenagerieBoss(){
        List<String> bosses = new ArrayList<>();
        bosses.add(Chimera);
        bosses.add(Suneater);
        bosses.add(Avatars);
        return bosses.get(AbstractDungeon.miscRng.random(bosses.size() - 1));
    }

    private String getTextForBoss(String bossID) {
        String text;
        switch (bossID) {
            case Chimera:
                text = DESCRIPTIONS[1];
                break;
            case Suneater:
                text = DESCRIPTIONS[2];
                break;
            default:
                text = DESCRIPTIONS[3];
                break;
        }
        return text;
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0: // Follow
                        this.screenNum = 1;
                        logger.info("Spawning boss: " + this.bossID);
                        AbstractDungeon.getCurrRoom().monsters = MonsterHelper.getEncounter(this.bossID);
                        AbstractDungeon.getCurrRoom().addGoldToRewards(AbstractDungeon.miscRng.random(25, 35));
                        AbstractDungeon.getCurrRoom().addRelicToRewards(AbstractRelic.RelicTier.RARE);
                        this.enterCombatFromImage();
                        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, m, new StrengthPower(m, this.strength), this.strength));
                            m.increaseMaxHp(m.maxHealth * this.hpMultiplier / 100, false);
                        }
                        AbstractDungeon.lastCombatMetricKey = this.bossID;
                        break;
                    default: // Leave
                        this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
                        this.screenNum = 2;
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

    @Override
    public void reopen() {
    }
}
