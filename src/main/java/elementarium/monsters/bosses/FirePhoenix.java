package elementarium.monsters.bosses;

import basemod.abstracts.CustomMonster;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.combat.RedFireballEffect;
import elementarium.Elementarium;
import elementarium.powers.BurningClawsPower;
import elementarium.powers.BurningDisdainPower;
import elementarium.powers.DelayedVulnerablePower;
import elementarium.powers.PhoenixRebirthPower;

import java.util.ArrayList;
import java.util.List;

public class FirePhoenix extends CustomMonster
{
    public static final String ID = "Elementarium:FirePhoenix";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    private static final String IMG = Elementarium.monsterImage(ID);
    private static final byte CLAW_SWIPE_ATTACK = 1;
    private static final byte WING_SWEEP_ATTACK = 2;
    private static final byte MOLTEN_FRENZY_ATTACK = 3;
    private static final byte BURNING_TEMPEST_DEBUFF = 4;
    private static final int CLAW_SWIPE_DAMAGE = 4;
    private static final int A4_CLAW_SWIPE_DAMAGE = 5;
    private static final int CLAW_SWIPE_HITS = 2;
    private static final int WING_SWEEP_DAMAGE = 12;
    private static final int A4_WING_SWEEP_DAMAGE = 14;
    private static final int WING_SWEEP_DEBUFF_AMOUNT = 1;
    private static final int MOLTEN_FRENZY_DAMAGE = 14;
    private static final int A4_MOLTEN_FRENZY_DAMAGE = 16;
    private static final int MOLTEN_FRENZY_STRENGTH = 1;
    private static final int BURNING_TEMPEST_VULNERABLE = 1;
    private static final int A19_BURNING_TEMPEST_VULNERABLE = 1;
    private static final int BURNING_TEMPEST_BURNS = 1;
    private static final int A19_BURNING_TEMPEST_BURNS = 2;
    private static final int BURNING_DISDAIN_STRENGTH = 1;
    private static final int A19_BURNING_DISDAIN_STRENGTH = 2;
    private static final int HP = 200;
    private static final int A9_HP = 220;

    private int clawSwipeDamage;
    private int wingSweepDamage;
    private int moltenFrenzyDamage;
    private int burningDisdainStrength;
    private int burningTempestVulnerable;
    private int burningTempestBurns;
    private boolean usedBurningTempest = false;

    public FirePhoenix() {
        this(0.0f, 0.0f);
    }

    public FirePhoenix(final float x, final float y) {
        super(FirePhoenix.NAME, ID, A9_HP, -5.0F, 0, 230.0f, 290.0f, IMG, x, y);
        this.type = EnemyType.BOSS;
        if (AbstractDungeon.ascensionLevel >= 9) {
            this.setHp(A9_HP);
        } else {
            this.setHp(HP);
        }

        if (AbstractDungeon.ascensionLevel >= 4) {
            this.clawSwipeDamage = A4_CLAW_SWIPE_DAMAGE;
            this.wingSweepDamage = A4_WING_SWEEP_DAMAGE;
            this.moltenFrenzyDamage = A4_MOLTEN_FRENZY_DAMAGE;
        } else {
            this.clawSwipeDamage = CLAW_SWIPE_DAMAGE;
            this.wingSweepDamage = WING_SWEEP_DAMAGE;
            this.moltenFrenzyDamage = MOLTEN_FRENZY_DAMAGE;
        }
        this.damage.add(new DamageInfo(this, this.clawSwipeDamage));
        this.damage.add(new DamageInfo(this, this.wingSweepDamage));
        this.damage.add(new DamageInfo(this, this.moltenFrenzyDamage));

        if (AbstractDungeon.ascensionLevel >= 19) {
            this.burningDisdainStrength = A19_BURNING_DISDAIN_STRENGTH;
            this.burningTempestVulnerable = A19_BURNING_TEMPEST_VULNERABLE;
            this.burningTempestBurns = A19_BURNING_TEMPEST_BURNS;
        } else {
            this.burningDisdainStrength = BURNING_DISDAIN_STRENGTH;
            this.burningTempestVulnerable = BURNING_TEMPEST_VULNERABLE;
            this.burningTempestBurns = BURNING_TEMPEST_BURNS;
        }
    }

    @Override
    public void usePreBattleAction() {
        CardCrawlGame.music.unsilenceBGM();
        AbstractDungeon.scene.fadeOutAmbiance();
        AbstractDungeon.getCurrRoom().playBgmInstantly("BOSS_CITY");

        this.addToBot(new ApplyPowerAction(this, this, new BurningDisdainPower(this, this.burningDisdainStrength), this.burningDisdainStrength));
        this.addToBot(new ApplyPowerAction(this, this, new BurningClawsPower(this)));
        this.addToBot(new ApplyPowerAction(this, this, new PhoenixRebirthPower(this)));
    }

    @Override
    public void takeTurn() {
        switch (this.nextMove) {
            case CLAW_SWIPE_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                for (int i = 0; i < CLAW_SWIPE_HITS; i++) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                }
                break;
            case WING_SWEEP_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, WING_SWEEP_DEBUFF_AMOUNT, true), WING_SWEEP_DEBUFF_AMOUNT));
                break;
            case MOLTEN_FRENZY_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(2), AbstractGameAction.AttackEffect.FIRE));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, MOLTEN_FRENZY_STRENGTH), MOLTEN_FRENZY_STRENGTH));
                break;
            case BURNING_TEMPEST_DEBUFF:
                CardCrawlGame.sound.playA("ATTACK_FIRE", 0.3F);
                CardCrawlGame.sound.playA("ATTACK_FLAME_BARRIER", 0.3F);
                float dst = 198.0F;
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new RedFireballEffect(AbstractDungeon.player.drawX - dst * Settings.scale, AbstractDungeon.player.drawY, AbstractDungeon.player.drawX + dst * Settings.scale, AbstractDungeon.player.drawY - 50.0F * Settings.scale, 6)));
                this.addToBot(new MakeTempCardInDiscardAction(new Burn(), this.burningTempestBurns));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new DelayedVulnerablePower(AbstractDungeon.player, this.burningTempestVulnerable), this.burningTempestVulnerable));
                this.usedBurningTempest = true;
                break;
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
    }

    @Override
    protected void getMove(final int num) {
        if (!this.usedBurningTempest && this.currentHealth <= this.maxHealth / 2) {
            this.setMove(MOVES[3], BURNING_TEMPEST_DEBUFF, Intent.STRONG_DEBUFF);
            return;
        }

        byte move;
        int s = this.moveHistory.size();
        int movesToCheck = s % 3;
        List<Byte> potentialMoves = new ArrayList<>();
        potentialMoves.add(CLAW_SWIPE_ATTACK);
        potentialMoves.add(WING_SWEEP_ATTACK);
        potentialMoves.add(MOLTEN_FRENZY_ATTACK);
        if (movesToCheck > 0 && s > 0) {
            potentialMoves.remove(this.moveHistory.get(s - 1));
        }
        if (movesToCheck > 1 && s > 1) {
            potentialMoves.remove(this.moveHistory.get(s - 2));
        }

        if (potentialMoves.size() == 1){
            move = potentialMoves.get(0);
        }
        else if (potentialMoves.size() == 2) {
            move = num < 50 ? potentialMoves.get(0) : potentialMoves.get(1);
        }
        else {
            move = num < 40 ? potentialMoves.get(0) : num < 80 ? potentialMoves.get(1) : potentialMoves.get(2);
        }

        switch (move) {
            case CLAW_SWIPE_ATTACK:
                this.setMove(MOVES[0], CLAW_SWIPE_ATTACK, Intent.ATTACK, this.clawSwipeDamage, CLAW_SWIPE_HITS, true);
                break;
            case WING_SWEEP_ATTACK:
                this.setMove(MOVES[1], WING_SWEEP_ATTACK, Intent.ATTACK_DEBUFF, this.wingSweepDamage);
                break;
            case MOLTEN_FRENZY_ATTACK:
                this.setMove(MOVES[2], MOLTEN_FRENZY_ATTACK, Intent.ATTACK_BUFF, this.moltenFrenzyDamage);
                break;
        }
    }

    @Override
    public void die() {
        super.die();
        if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            this.useFastShakeAnimation(5.0F);
            CardCrawlGame.screenShake.rumble(4.0F);
            this.onBossVictoryLogic();
        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = FirePhoenix.monsterStrings.NAME;
        MOVES = FirePhoenix.monsterStrings.MOVES;
    }
}