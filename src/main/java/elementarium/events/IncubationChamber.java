package elementarium.events;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import elementarium.Elementarium;
import elementarium.cards.CardUtil;
import elementarium.relics.HatchlingPhoenix;

import java.lang.reflect.Array;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class IncubationChamber extends AbstractImageEvent {
    public static final String ID = "Elementarium:IncubationChamber";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = Elementarium.eventImage(ID);

    private static final int NUM_POTIONS = 3;
    private static final int A15_NUM_POTIONS = 2;
    private static final int NUM_COLORLESS_CARDS = 1;
    private static final int NUM_OTHER_COLOR_CARDS = 1;

    private int numPotions;
    private AbstractRelic egg;
    private AbstractRelic hatched;

    private int screenNum = 0;

    public IncubationChamber() {
        super(NAME, DESCRIPTIONS[0], IMG);
        this.noCardsInRewards = true;

        if (AbstractDungeon.ascensionLevel >= 15) {
            this.numPotions = A15_NUM_POTIONS;
        }
        else {
            this.numPotions = NUM_POTIONS;
        }

        for (AbstractRelic r : AbstractDungeon.player.relics) {
            if (r.relicId.toLowerCase(Locale.ENGLISH).contains("egg")) {
                this.egg = r;
                break;
            }
        }
        this.hatched = new HatchlingPhoenix();

        this.imageEventText.setDialogOption(MessageFormat.format(OPTIONS[0], this.numPotions));
        this.imageEventText.setDialogOption(OPTIONS[1]);
        if (this.egg != null) {
            this.imageEventText.setDialogOption(MessageFormat.format(OPTIONS[2], this.egg.name, this.hatched.name), this.hatched);
        }
        else {
            this.imageEventText.setDialogOption(OPTIONS[3], true);
        }
        this.imageEventText.setDialogOption(OPTIONS[4]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.screenNum = 1;
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[4]);

                        logMetric(ID, "Tanks");
                        AbstractDungeon.getCurrRoom().rewards.clear();
                        for (int i = 0; i < this.numPotions; i++) {
                            AbstractDungeon.getCurrRoom().rewards.add(new RewardItem(PotionHelper.getRandomPotion()));
                        }

                        AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
                        AbstractDungeon.combatRewardScreen.open();
                        break;
                    case 1:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.screenNum = 1;
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[4]);

                        logMetric(ID, "Machinery");
                        AbstractDungeon.getCurrRoom().rewards.clear();
                        for (int i = 0; i < NUM_COLORLESS_CARDS; ++i) {
                            AbstractDungeon.getCurrRoom().addCardReward(new RewardItem(AbstractCard.CardColor.COLORLESS));
                        }
                        for (RewardItem reward : this.getOtherColorCardReward(NUM_OTHER_COLOR_CARDS)) {
                            AbstractDungeon.getCurrRoom().addCardReward(reward);
                        }

                        AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
                        AbstractDungeon.combatRewardScreen.open();
                        break;
                    case 2:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        this.screenNum = 1;
                        AbstractDungeon.player.loseRelic(this.egg.relicId);
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), this.hatched);
                        logMetricRelicSwap(ID, "Egg", this.hatched, this.egg);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[4]);
                        break;
                    default:
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

    private List<RewardItem> getOtherColorCardReward(int numRewards) {
        List<RewardItem> rewards = new ArrayList<>();
        for(int i = 0; i < numRewards; ++i) {
            RewardItem reward = new RewardItem();
            ArrayList<AbstractCard> cards = new ArrayList<>();
            //We see what was already generated and use that, to avoid advancing the rare counter further
            for (AbstractCard c : reward.cards) {
                AbstractCard.CardRarity rarity = c.rarity == AbstractCard.CardRarity.COMMON || c.rarity == AbstractCard.CardRarity.UNCOMMON || c.rarity == AbstractCard.CardRarity.RARE ? c.rarity : AbstractCard.CardRarity.COMMON;

                AbstractCard card = CardUtil.getOtherColorCard(rarity, Arrays.asList(AbstractDungeon.player.getCardColor(), AbstractCard.CardColor.COLORLESS));
                for (AbstractRelic r : AbstractDungeon.player.relics) {
                    r.onPreviewObtainCard(card);
                }
                cards.add(card);
            }
            reward.cards = cards;
            rewards.add(reward);
        }

        return rewards;
    }
}
