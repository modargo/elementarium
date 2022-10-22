package elementarium.events;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import elementarium.Elementarium;
import elementarium.util.CollectionsUtil;

import java.text.MessageFormat;
import java.util.ArrayList;

public class ShatteredPortal extends AbstractImageEvent {
    public static final String ID = "Elementarium:ShatteredPortal";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = Elementarium.eventImage(ID);

    private static final int MIN_GOLD_COST = 85;
    private static final int MAX_GOLD_COST = 105;
    private static final int A15_MIN_GOLD_COST = 90;
    private static final int A15_MAX_GOLD_COST = 110;
    private static final float HEALTH_LOSS_PERCENTAGE = 0.20F;
    private static final float A15_HEALTH_LOSS_PERCENTAGE = 0.25F;
    private static final int HEALTH_LOSS_MINIMUM = 12;
    private static final int A15_HEALTH_LOSS_MINIMUM = 15;
    private static final float MAX_HEALTH_LOSS_PERCENTAGE = 0.10F;
    private static final float A15_MAX_HEALTH_LOSS_PERCENTAGE = 0.14F;
    private static final int MAX_HEALTH_LOSS_MINIMUM = 5;
    private static final int A15_MAX_HEALTH_LOSS_MINIMUM = 7;

    private final int goldCost;
    private int healthCost;
    private final int maxHealthCost;
    private final AbstractCard cardCost;

    private int screenNum = 0;
    private int choice = -1;

    public ShatteredPortal() {
        super(NAME, DESCRIPTIONS[0], IMG);
        if (AbstractDungeon.ascensionLevel >= 15) {
            this.goldCost = AbstractDungeon.miscRng.random(A15_MIN_GOLD_COST, A15_MAX_GOLD_COST);
            this.healthCost = (int) ((float) AbstractDungeon.player.maxHealth * A15_HEALTH_LOSS_PERCENTAGE);
            this.healthCost = Math.max(this.healthCost, A15_HEALTH_LOSS_MINIMUM);
            this.maxHealthCost = Math.max((int) ((float) AbstractDungeon.player.maxHealth * A15_MAX_HEALTH_LOSS_PERCENTAGE), A15_MAX_HEALTH_LOSS_MINIMUM);
        } else {
            this.goldCost = AbstractDungeon.miscRng.random(MIN_GOLD_COST, MAX_GOLD_COST);
            this.healthCost = (int) ((float) AbstractDungeon.player.maxHealth * HEALTH_LOSS_PERCENTAGE);
            this.healthCost = Math.max(this.healthCost, HEALTH_LOSS_MINIMUM);
            this.maxHealthCost = Math.max((int) ((float) AbstractDungeon.player.maxHealth * MAX_HEALTH_LOSS_PERCENTAGE), MAX_HEALTH_LOSS_MINIMUM);
        }

        this.cardCost = this.getRandomRareCard();

        imageEventText.setDialogOption(OPTIONS[0]);
    }

    private AbstractCard getRandomRareCard() {
        ArrayList<AbstractCard> list = new ArrayList<>();
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.rarity == AbstractCard.CardRarity.RARE) {
                list.add(c);
            }
        }

        if (list.isEmpty()) {
            return null;
        } else {
            CollectionsUtil.shuffle(list, AbstractDungeon.miscRng);
            return list.get(0);
        }
    }

    private void reward() {
        AbstractRelic relic = AbstractDungeon.returnRandomScreenlessRelic(AbstractDungeon.returnRandomRelicTier());
        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), relic);
        switch (this.choice) {
            case 0:
                logMetricObtainRelicAtCost(ID, "Gold", relic, this.goldCost);
                break;
            case 1:
                logMetricObtainRelicAndDamage(ID, "Blood", relic, this.healthCost);
                break;
            case 2:
                logMetricObtainRelicAndLoseMaxHP(ID, "Essence", relic, this.maxHealthCost);
                break;
            case 3:
                logMetricRemoveCardAndObtainRelic(ID, "Knowledge", this.cardCost, relic);
                break;
        }
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                this.screenNum = 1;
                this.imageEventText.clearAllDialogs();
                if (this.goldCost <= AbstractDungeon.player.gold) {
                    imageEventText.setDialogOption(MessageFormat.format(OPTIONS[1], this.goldCost));
                }
                else {
                    imageEventText.setDialogOption(MessageFormat.format(OPTIONS[5], this.goldCost), true);
                }
                if (this.healthCost <= AbstractDungeon.player.currentHealth) {
                    imageEventText.setDialogOption(MessageFormat.format(OPTIONS[2], this.healthCost));
                }
                else {
                    imageEventText.setDialogOption(MessageFormat.format(OPTIONS[6], this.healthCost), true);
                }
                if (this.healthCost <= AbstractDungeon.player.maxHealth) {
                    imageEventText.setDialogOption(MessageFormat.format(OPTIONS[3], this.maxHealthCost));
                }
                else {
                    imageEventText.setDialogOption(MessageFormat.format(OPTIONS[7], this.maxHealthCost), true);
                }
                if (this.cardCost != null) {
                    imageEventText.setDialogOption(MessageFormat.format(OPTIONS[4], this.cardCost.name), this.cardCost.makeStatEquivalentCopy());
                }
                else {
                    imageEventText.setDialogOption(OPTIONS[8], true);
                }
                imageEventText.setDialogOption(OPTIONS[9]);
                break;
            case 1:
                this.choice = buttonPressed;
                switch (buttonPressed) {
                    case 0: // Gold
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        AbstractDungeon.player.loseGold(this.goldCost);
                        this.screenNum = 2;
                        this.imageEventText.updateDialogOption(0, OPTIONS[0]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    case 1: // Blood
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        AbstractDungeon.player.damage(new DamageInfo(null, this.healthCost));
                        this.screenNum = 2;
                        this.imageEventText.updateDialogOption(0, OPTIONS[0]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    case 2: // Essence
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        AbstractDungeon.player.decreaseMaxHealth(this.maxHealthCost);
                        this.screenNum = 2;
                        this.imageEventText.updateDialogOption(0, OPTIONS[0]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    case 3: // Knowledge
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(this.cardCost, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                        AbstractDungeon.player.masterDeck.removeCard(this.cardCost);
                        this.screenNum = 2;
                        this.imageEventText.updateDialogOption(0, OPTIONS[0]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    default: // Leave
                        logMetricIgnored(ID);
                        this.openMap();
                        break;
                }
                break;
            case 2:
                this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                this.reward();
                this.screenNum = 3;
                this.imageEventText.updateDialogOption(0, OPTIONS[9]);
                this.imageEventText.clearRemainingOptions();
                break;
            default:
                this.openMap();
                break;
        }
    }
}
