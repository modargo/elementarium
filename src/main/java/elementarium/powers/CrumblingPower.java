package elementarium.powers;

import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.actions.utility.LoseBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.ExplosionSmallEffect;
import elementarium.Elementarium;

import java.text.MessageFormat;

public class CrumblingPower extends AbstractPower {
    public static final String POWER_ID = "Elementarium:Crumbling";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    private AbstractCreature target;
    private int strengthLoss;

    public CrumblingPower(AbstractCreature owner, int amount, AbstractCreature target, int strengthLoss) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.target = target;
        this.strengthLoss = strengthLoss;
        updateDescription();
        Elementarium.LoadPowerImage(this);
    }

    @Override
    public void updateDescription() {
        if (this.amount == 1) {
            this.description = MessageFormat.format(DESCRIPTIONS[1], this.target.name, this.strengthLoss);
        } else {
            this.description = MessageFormat.format(DESCRIPTIONS[0], this.amount, this.target.name, this.strengthLoss);
        }
    }

    @Override
    public void duringTurn() {
        if (this.amount == 1 && !this.owner.isDying) {
            this.addToBot(new RemoveAllBlockAction(this.owner, this.owner));
            this.addToBot(new VFXAction(new ExplosionSmallEffect(this.owner.hb.cX, this.owner.hb.cY), 0.1F));
            this.addToBot(new SuicideAction((AbstractMonster) this.owner));
            this.addToBot(new ApplyPowerAction(this.target, this.owner, new StrengthPower(this.target, -this.strengthLoss), -this.strengthLoss));
        } else {
            this.addToBot(new ReducePowerAction(this.owner, this.owner, POWER_ID, 1));
            this.updateDescription();
        }
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}