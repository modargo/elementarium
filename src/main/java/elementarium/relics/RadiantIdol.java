package elementarium.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import elementarium.Elementarium;
import elementarium.util.TextureLoader;

import java.text.MessageFormat;

public class RadiantIdol extends CustomRelic {
    public static final String ID = "Elementarium:RadiantIdol";
    private static final Texture IMG = TextureLoader.getTexture(Elementarium.relicImage(ID));
    private static final Texture OUTLINE = TextureLoader.getTexture(Elementarium.relicOutlineImage(ID));
    private static final int HEAL_AMOUNT = 6;

    public RadiantIdol() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.HEAVY);
    }

    @Override
    public void onObtainCard(AbstractCard c) {
        AbstractDungeon.player.heal(HEAL_AMOUNT);
    }

    @Override
    public String getUpdatedDescription() {
        return MessageFormat.format(DESCRIPTIONS[0], HEAL_AMOUNT);
    }

    public AbstractRelic makeCopy() {
        return new RadiantIdol();
    }
}
