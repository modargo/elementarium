package elementarium.events;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.*;
import elementarium.Elementarium;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GreatTreasure extends AbstractImageEvent {
    public static final String ID = "Elementarium:GreatTreasure";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = Elementarium.eventImage(ID);

    private static final int DAMAGE_LOOK = 3;
    private static final int DAMAGE_EXCHANGE = 8;
    private static final int A15_DAMAGE_EXCHANGE = 12;
    private static final float SACRIFICE_MAX_HEALTH_LOSS_PERCENTAGE = 0.50F;

    private int damageExchange;
    private int sacrificeMaxHealthLoss;
    private AbstractRelic offering;
    private AbstractRelic relic;

    private int screenNum = 0;

    public GreatTreasure() {
        super(NAME, DESCRIPTIONS[0], IMG);
        this.sacrificeMaxHealthLoss = (int) ((float) AbstractDungeon.player.maxHealth * SACRIFICE_MAX_HEALTH_LOSS_PERCENTAGE);
        if (AbstractDungeon.ascensionLevel >= 15) {
           this.damageExchange = A15_DAMAGE_EXCHANGE;
        }
        else {
            this.damageExchange = DAMAGE_EXCHANGE;
        }

        ArrayList<AbstractRelic> relics = new ArrayList<>();
        for (AbstractRelic r : AbstractDungeon.player.relics) {
            if (r.tier == AbstractRelic.RelicTier.BOSS && !this.isUsedUpBossRelic(r)) {
                relics.add(r);
            }
        }
        this.offering = relics.size() != 0 ? relics.get(AbstractDungeon.miscRng.random(relics.size() - 1)) : null;
        this.relic = AbstractDungeon.returnRandomRelicEnd(AbstractRelic.RelicTier.BOSS);

        this.imageEventText.setDialogOption(MessageFormat.format(OPTIONS[0], DAMAGE_LOOK));
        this.imageEventText.setDialogOption(OPTIONS[3]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        AbstractDungeon.player.damage(new DamageInfo(null, DAMAGE_LOOK, DamageInfo.DamageType.HP_LOSS));
                        this.screenNum = 1;
                        this.imageEventText.clearAllDialogs();
                        if (offering != null) {
                            this.imageEventText.setDialogOption(MessageFormat.format(OPTIONS[1], this.offering.name, this.relic.name, this.damageExchange), relic);
                        }
                        else {
                            this.imageEventText.setDialogOption(MessageFormat.format(OPTIONS[2], this.relic.name), relic);
                        }
                        this.imageEventText.setDialogOption(OPTIONS[3]);
                        break;
                    default:
                        this.openMap();
                        break;
                }
                break;
            case 1:
                switch (buttonPressed) {
                    case 0: // Offer/Sacrifice
                        if (this.offering != null) {
                            this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                            AbstractDungeon.player.loseRelic(this.offering.relicId);
                            AbstractDungeon.player.damage(new DamageInfo(null, this.damageExchange, DamageInfo.DamageType.HP_LOSS));
                        }
                        else {
                            // If you only have used up boss relic or get here without a boss relic, congratulations! You can get a boss relic, for the price of only half your max HP
                            this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                            AbstractDungeon.player.decreaseMaxHealth(this.sacrificeMaxHealthLoss);
                        }
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float) (Settings.WIDTH / 2), (float) (Settings.HEIGHT / 2), this.relic);
                        this.screenNum = 2;
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
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

    private boolean isUsedUpBossRelic(AbstractRelic r) {
        List<String> usedUpBossRelics = Arrays.asList(Astrolabe.ID, CallingBell.ID, EmptyCage.ID, PandorasBox.ID, TinyHouse.ID);
        return usedUpBossRelics.contains(r.relicId);
    }
}
