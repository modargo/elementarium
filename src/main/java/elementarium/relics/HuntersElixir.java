package elementarium.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.RegenPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import elementarium.Elementarium;
import elementarium.util.TextureLoader;

import java.text.MessageFormat;

public class HuntersElixir extends CustomRelic {
    public static final String ID = "Elementarium:HuntersElixir";
    private static final Texture IMG = TextureLoader.getTexture(Elementarium.relicImage(ID));
    private static final Texture OUTLINE = TextureLoader.getTexture(Elementarium.relicOutlineImage(ID));
    private static final int ARTIFACT = 1;
    private static final int REGEN = 4;

    public HuntersElixir() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.CLINK);
    }

    @Override
    public void atBattleStart() {
        if (AbstractDungeon.getCurrRoom().eliteTrigger) {
            this.flash();
            this.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            this.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new ArtifactPower(AbstractDungeon.player, ARTIFACT), ARTIFACT));
            this.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new RegenPower(AbstractDungeon.player, REGEN), REGEN));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return MessageFormat.format(DESCRIPTIONS[0], ARTIFACT, REGEN);
    }

    public AbstractRelic makeCopy() {
        return new HuntersElixir();
    }
}
