package elementarium.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.DuplicationPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import elementarium.Elementarium;
import elementarium.util.TextureLoader;

public class GolemsHeart extends CustomRelic {
    public static final String ID = "Elementarium:GolemsHeart";
    private static final Texture IMG = TextureLoader.getTexture(Elementarium.relicImage(ID));
    private static final Texture OUTLINE = TextureLoader.getTexture(Elementarium.relicOutlineImage(ID));

    public GolemsHeart() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.HEAVY);
    }

    @Override
    public void atBattleStart() {
        this.flash();
        this.addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
        this.addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new DuplicationPower(AbstractDungeon.player, 1), 1));
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    public AbstractRelic makeCopy() {
        return new GolemsHeart();
    }
}
