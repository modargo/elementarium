package elementarium.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import elementarium.Elementarium;
import elementarium.util.TextureLoader;

public class HatchlingPhoenix extends CustomRelic {
    public static final String ID = "Elementarium:HatchlingPhoenix";
    private static final Texture IMG = TextureLoader.getTexture(Elementarium.relicImage(ID));
    private static final Texture OUTLINE = TextureLoader.getTexture(Elementarium.relicOutlineImage(ID));

    public HatchlingPhoenix() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.CLINK);
    }

    @Override
    public void onEquip() {
        for (RewardItem reward : AbstractDungeon.combatRewardScreen.rewards) {
            for (AbstractCard c : reward.cards) {
                this.onPreviewObtainCard(c);
            }
        }
    }

    @Override
    public void onPreviewObtainCard(AbstractCard c) {
        this.onObtainCard(c);
    }

    @Override
    public void onObtainCard(AbstractCard c) {
        if (c.cost == 1 && c.canUpgrade() && !c.upgraded) {
            c.upgrade();
        }
    }

    public boolean canSpawn() {
        return Settings.isEndless || AbstractDungeon.floorNum <= 48;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    public AbstractRelic makeCopy() {
        return new HatchlingPhoenix();
    }
}
