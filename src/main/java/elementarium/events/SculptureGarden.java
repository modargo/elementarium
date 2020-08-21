package elementarium.events;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import elementarium.Elementarium;

import java.text.MessageFormat;
import java.util.ArrayList;

public class SculptureGarden extends AbstractImageEvent {
    public static final String ID = "Elementarium:SculptureGarden";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = Elementarium.eventImage(ID);

    private static final int MAX_HEALTH = 8;
    private static final int A15_MAX_HEALTH = 6;

    private int maxHealth;

    private int screenNum = 0;

    public SculptureGarden() {
        super(NAME, DESCRIPTIONS[0], IMG);

        if (AbstractDungeon.ascensionLevel >= 15) {
            this.maxHealth = A15_MAX_HEALTH;
        }
        else {
            this.maxHealth = MAX_HEALTH;
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
                imageEventText.setDialogOption(MessageFormat.format(OPTIONS[1], this.maxHealth));
                imageEventText.setDialogOption(OPTIONS[2]);
                imageEventText.setDialogOption(OPTIONS[3]);
                break;
            case 1:
                switch (buttonPressed) {
                    case 0: // Graft
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        AbstractDungeon.player.increaseMaxHp(this.maxHealth, false);
                        this.logMetricMaxHPGain(ID, "Graft", this.maxHealth);
                        this.screenNum = 2;
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    case 1: // Meditate
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        this.Upgrade2CostCards();
                        this.screenNum = 2;
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                    default: // Leave
                        logMetricIgnored(ID);
                        this.openMap();
                        break;
                }
                break;
            default:
                this.openMap();
                break;
        }
    }

    private void Upgrade2CostCards() {
        ArrayList<String> upgradedCards = new ArrayList<>();
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.cost == 2 && c.canUpgrade()) {
                c.upgrade();
                AbstractDungeon.player.bottledCardUpgradeCheck(c);
                float x = MathUtils.random(0.1F, 0.9F) * (float) Settings.WIDTH;
                float y = MathUtils.random(0.2F, 0.8F) * (float)Settings.HEIGHT;
                AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy(), x, y));
                AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect(x, y));
                upgradedCards.add(c.cardID);
            }
        }
        logMetricUpgradeCards(ID, "Meditate", upgradedCards);
    }
}
