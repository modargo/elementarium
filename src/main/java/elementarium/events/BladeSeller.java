package elementarium.events;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.Injury;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import elementarium.Elementarium;
import elementarium.cards.CustomTags;
import elementarium.cards.elementalblades.*;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

public class BladeSeller extends AbstractImageEvent {
    public static final String ID = "Elementarium:BladeSeller";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = Elementarium.eventImage(ID);

    private static final int CURSE_CHANCE = 30;
    private static final int A15_CURSE_CHANCE = 60;
    private static final int GOLD = 80;
    private static final int A15_GOLD = 70;

    private int curseChance;
    private int gold;
    private AbstractCard card;
    private boolean hasBlades = false;

    private int screenNum = 0;

    public BladeSeller() {
        super(NAME, DESCRIPTIONS[0], IMG);

        if (AbstractDungeon.ascensionLevel >= 15) {
            this.curseChance = A15_CURSE_CHANCE;
            this.gold = A15_GOLD;
        }
        else {
            this.curseChance = CURSE_CHANCE;
            this.gold = GOLD;

        }

        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            this.hasBlades = this.hasBlades || c.hasTag(CustomTags.ELEMENTAL_BLADE);
        }

        if (!this.hasBlades) {
            List<AbstractCard> blades = Arrays.asList(new FireblessedBlade(), new WindblessedBlade(), new EarthblessedBlade(), new IceblessedBlade(), new VoidblessedBlade());
            this.card = blades.get(AbstractDungeon.miscRng.random(0, blades.size() - 1));
        }

        imageEventText.setDialogOption(OPTIONS[3]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                this.screenNum = 1;
                this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                this.imageEventText.clearAllDialogs();
                if (!this.hasBlades) {
                    imageEventText.setDialogOption(MessageFormat.format(OPTIONS[0], this.card.name, this.curseChance), this.card);
                    imageEventText.setDialogOption(OPTIONS[1]);
                }
                else {
                    imageEventText.setDialogOption(OPTIONS[2]);
                }
                break;
            case 1:
                switch (buttonPressed) {
                    case 0: // Take/Punish
                        if (!this.hasBlades) {
                            if (AbstractDungeon.miscRng.randomBoolean(this.curseChance / 100.0F)) {
                                this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                                AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(this.card, (float)Settings.WIDTH / 2.0F - AbstractCard.IMG_WIDTH / 2.0F - 20.0F * Settings.scale, (float)(Settings.HEIGHT / 2)));
                                AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new Injury(), (float)Settings.WIDTH / 2.0F + AbstractCard.IMG_WIDTH / 2.0F + 20.0F * Settings.scale, (float)(Settings.HEIGHT / 2)));
                            }
                            else {
                                this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                                AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(this.card, (float)Settings.WIDTH / 2.0F, (float)(Settings.HEIGHT / 2)));
                            }
                            this.screenNum = 3;
                            this.imageEventText.updateDialogOption(0, OPTIONS[1]);
                            this.imageEventText.clearRemainingOptions();
                        }
                        else {
                            this.screenNum = 2;
                            this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
                            this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                            this.imageEventText.clearRemainingOptions();
                        }
                        break;
                    default: // Leave
                        this.openMap();
                        break;
                }
                break;
            case 2:
                AbstractDungeon.player.gainGold(this.gold);
                this.screenNum = 3;
                this.imageEventText.updateBodyText(DESCRIPTIONS[5]);
                this.imageEventText.updateDialogOption(0, OPTIONS[2]);
                this.imageEventText.clearRemainingOptions();
                break;
            default:
                this.openMap();
                break;
        }
    }
}
