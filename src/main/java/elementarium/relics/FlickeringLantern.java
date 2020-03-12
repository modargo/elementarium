package elementarium.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import elementarium.Elementarium;
import elementarium.util.TextureLoader;

public class FlickeringLantern extends CustomRelic {
    public static final String ID = "Elementarium:FlickeringLantern";
    private static final Texture IMG = TextureLoader.getTexture(Elementarium.relicImage(ID));
    private static final Texture OUTLINE = TextureLoader.getTexture(Elementarium.relicOutlineImage(ID));
    private static final int TURN_ACTIVATION = 3;

    public FlickeringLantern() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.SOLID);
        this.energyBased = true;
    }

    public String getUpdatedDescription() {
        return AbstractDungeon.player != null ? this.setDescription(AbstractDungeon.player.chosenClass) : this.setDescription(null);
    }

    private String setDescription(AbstractPlayer.PlayerClass c) {
        return this.DESCRIPTIONS[0];
    }

    public void updateDescription(AbstractPlayer.PlayerClass c) {
        this.description = this.setDescription(c);
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
    }

    public void atBattleStart() {
        this.counter = 0;
    }

    public void atTurnStart() {
        if (!this.grayscale) {
            ++this.counter;
        }

        if (this.counter == TURN_ACTIVATION) {
            this.flash();
            this.addToBot(new GainEnergyAction(1));
            this.addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            this.counter = -1;
            this.grayscale = true;
        }
    }

    public void onVictory() {
        this.counter = -1;
        this.grayscale = false;
    }

    public AbstractRelic makeCopy() {
        return new FlickeringLantern();
    }
}
