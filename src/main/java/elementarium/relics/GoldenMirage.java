package elementarium.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import elementarium.Elementarium;
import elementarium.util.TextureLoader;

public class GoldenMirage extends CustomRelic {
    public static final String ID = "Elementarium:GoldenMirage";
    private static final Texture IMG = TextureLoader.getTexture(Elementarium.relicImage(ID));
    private static final Texture OUTLINE = TextureLoader.getTexture(Elementarium.relicOutlineImage(ID));

    public GoldenMirage() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.CLINK);
    }

    @Override
    public void onShuffle() {
        if (!this.grayscale) {
            this.addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            this.addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new IntangiblePlayerPower(AbstractDungeon.player, 1), 1));
            this.grayscale = true;
        }
    }

    @Override
    public void onVictory() {
        this.grayscale = false;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    public AbstractRelic makeCopy() {
        return new GoldenMirage();
    }
}
