package elementarium.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import elementarium.Elementarium;
import elementarium.util.TextureLoader;

import java.text.MessageFormat;

public class HuntersSling extends CustomRelic {
    public static final String ID = "Elementarium:HuntersSling";
    private static final Texture IMG = TextureLoader.getTexture(Elementarium.relicImage(ID));
    private static final Texture OUTLINE = TextureLoader.getTexture(Elementarium.relicOutlineImage(ID));
    private static final int STATS = 1;

    public HuntersSling() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.CLINK);
    }

    public void atBattleStart() {
        if (AbstractDungeon.getCurrRoom().eliteTrigger) {
            this.flash();
            this.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            this.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new StrengthPower(AbstractDungeon.player, STATS), STATS));
            this.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new DexterityPower(AbstractDungeon.player, STATS), STATS));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return MessageFormat.format(DESCRIPTIONS[0], STATS);
    }

    public AbstractRelic makeCopy() {
        return new HuntersSling();
    }
}
