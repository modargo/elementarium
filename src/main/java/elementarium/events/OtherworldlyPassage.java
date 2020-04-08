package elementarium.events;

import actlikeit.dungeons.CustomDungeon;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.TheCity;
import com.megacrit.cardcrawl.events.city.Colosseum;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import elementarium.Elementarium;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

// We extend the Colosseum event because ProceedButton.java specifically checks if an event is an instance of this type
// (or a few other types) in the logic for what happens when you click proceed. This is easier than a patch.
public class OtherworldlyPassage extends Colosseum {
    public static final String ID = "Elementarium:OtherworldlyPassage";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = Elementarium.eventImage(ID);
    private static final Logger logger = LogManager.getLogger(OtherworldlyPassage.class.getName());

    private static final int STRENGTH_AMOUNT = 1;
    private static final int MAX_HP_LOSS = 1;
    private static final int A15_MAX_HP_LOSS = 2;

    private static final String GensokyoActId = "Gensokyo:Gensokyoer";

    private int screenNum = 0;
    private String actID;
    private int maxHpLoss;

    public OtherworldlyPassage() {
        super();
        this.imageEventText.clear();
        this.roomEventText.clear();
        this.title = NAME;
        this.body = DESCRIPTIONS[0];
        this.imageEventText.loadImage(IMG);
        type = EventType.IMAGE;
        this.noCardsInRewards = false;

        if (AbstractDungeon.ascensionLevel >= 15) {
            this.maxHpLoss = A15_MAX_HP_LOSS;
        }
        else {
            this.maxHpLoss = MAX_HP_LOSS;
        }

        this.imageEventText.setDialogOption(OPTIONS[0]);
    }

    private String getRandomAct() {
        List<String> acts = new ArrayList<>();
        if (Loader.isModLoaded("Gensokyo")) {
            acts.add(GensokyoActId);
        }
        // We always prioritize other modded acts over the base game, since crossover content is fun
        if (acts.isEmpty()){
            acts.add(TheCity.ID);
        }

        String act = acts.get(AbstractDungeon.miscRng.random(acts.size() - 1));
        logger.info("Determined random act to pull elite from: " + act);
        return act;
    }

    private String getRandomElite(String actID){
        List<String> elites = new ArrayList<>();
        // Rather than try to do something fancy to automatically pull elites, we'll just hardcode support for specific
        // alternate acts. There aren't many and it's easy to update.
        switch (actID) {
            case GensokyoActId:
                elites.add("Gensokyo:Reisen");
                elites.add("Gensokyo:Koishi");
                elites.add("Gensokyo:Tenshi");
                break;
            default:
                if (actID != TheCity.ID) {
                    logger.warn("Unknown act for getting random elite: " + actID);
                }
                elites.add("Gremlin Leader");
                elites.add("Slavers");
                elites.add("Book of Stabbing");
        }
        return elites.get(AbstractDungeon.miscRng.random(elites.size() - 1));
    }

    private String getTextForAct(String actID) {
        String text;
        switch (actID) {
            case GensokyoActId:
                text = DESCRIPTIONS[4];
                break;
            default:
                text = DESCRIPTIONS[3];
                break;
        }
        text += " NL NL " + DESCRIPTIONS[2];
        return text;
    }

    private String getNameForAct(String actID) {
        logger.info("Getting act name. Acts available: " + String.join(", ", CustomDungeon.dungeons.keySet()));
        if (CustomDungeon.dungeons.containsKey(actID)) {
            return CustomDungeon.dungeons.get(actID).name;
        }
        return TheCity.NAME;
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                this.actID = this.getRandomAct();
                this.imageEventText.updateBodyText(this.getTextForAct(this.actID));
                this.screenNum = 1;
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[1].replace("{0}", this.getNameForAct(this.actID)));
                this.imageEventText.setDialogOption(OPTIONS[2].replace("{0}", this.maxHpLoss + ""));
                break;
            case 1:
                switch (buttonPressed) {
                    case 0: // Enter
                        this.screenNum = 2;
                        String elite = this.getRandomElite(this.actID);
                        logger.info("Spawning elite: " + elite);
                        AbstractDungeon.getCurrRoom().monsters = MonsterHelper.getEncounter(elite);
                        AbstractDungeon.getCurrRoom().eliteTrigger = true;
                        this.enterCombatFromImage();
                        for (AbstractMonster monster : AbstractDungeon.getCurrRoom().monsters.monsters) {
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(monster, monster, new StrengthPower(monster, STRENGTH_AMOUNT), STRENGTH_AMOUNT));
                        }
                        AbstractDungeon.lastCombatMetricKey = elite;
                        break;
                    default: // Detour
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        AbstractDungeon.player.decreaseMaxHealth(this.maxHpLoss);
                        this.screenNum = 2;
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
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
