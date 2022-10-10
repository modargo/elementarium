package elementarium.actions;

import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MinionPower;
import com.megacrit.cardcrawl.powers.SlowPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import elementarium.monsters.MonsterUtils;
import elementarium.powers.CrumblingPower;

public class SummonGolemAction extends AbstractGameAction {
    private final AbstractMonster m;
    private final int strength;
    private final int crumbling;
    private final AbstractCreature crumblingTarget;
    private final int crumblingStrengthLoss;

    public SummonGolemAction(String golemID, float x, float y, int strength, float hpMultiplier, int crumbling, AbstractCreature crumblingTarget, int crumblingStrengthLoss, boolean firstTurn) {
        this.actionType = ActionType.SPECIAL;
        if (Settings.FAST_MODE) {
            this.startDuration = Settings.ACTION_DUR_FAST;
        } else {
            this.startDuration = Settings.ACTION_DUR_LONG;
        }

        this.duration = this.startDuration;
        this.strength = strength;
        this.crumbling = crumbling;
        this.crumblingTarget = crumblingTarget;
        this.crumblingStrengthLoss = crumblingStrengthLoss;
        this.m = MonsterUtils.getGolem(golemID, x, y);
        this.m.currentHealth = (int)((float)this.m.maxHealth * hpMultiplier);
        if (!firstTurn) {
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                r.onSpawnMonster(this.m);
            }
        }
    }

    private int getSmartPosition() {
        int position = 0;

        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (this.m.drawX <= m.drawX) {
                break;
            }
        }

        return position;
    }

    public void update() {
        if (this.duration == this.startDuration) {
            this.m.animX = 1200.0F * Settings.scale;
            this.m.init();
            this.m.applyPowers();
            AbstractDungeon.getCurrRoom().monsters.addMonster(this.getSmartPosition(), this.m);
            if (ModHelper.isModEnabled("Lethality")) {
                this.addToBot(new ApplyPowerAction(this.m, this.m, new StrengthPower(this.m, 3), 3));
            }

            if (ModHelper.isModEnabled("Time Dilation")) {
                this.addToBot(new ApplyPowerAction(this.m, this.m, new SlowPower(this.m, 0)));
            }
            if (strength != 0) {
                this.addToBot(new ApplyPowerAction(this.m, this.m, new StrengthPower(this.m, this.strength), this.strength));
            }
            if (crumbling != 0) {
                this.addToBot(new ApplyPowerAction(this.m, this.m, new CrumblingPower(this.m, this.crumbling, this.crumblingTarget, this.crumblingStrengthLoss), this.crumbling));
            }

            this.addToBot(new ApplyPowerAction(this.m, this.m, new MinionPower(this.m)));
        }

        this.tickDuration();
        if (this.isDone) {
            this.m.animX = 0.0F;
            this.m.showHealthBar();
            this.m.usePreBattleAction();
        } else {
            this.m.animX = Interpolation.fade.apply(0.0F, 1200.0F * Settings.scale, this.duration);
        }
    }
}
