package elementarium.monsters.bosses;

import basemod.abstracts.CustomMonster;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.FastShakeAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.*;
import elementarium.Elementarium;

public class GoldenDragon extends CustomMonster {
    public static final String ID = "Elementarium:GoldenDragon";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    private static final String IMG = Elementarium.monsterImage(ID);
    private static final byte TAIL_SLAM_ATTACK = 1;
    private static final byte BREATH_WEAPON_ATTACK = 2;
    private static final byte EMPOWERING_ROAR_ATTACK = 3;
    private static final byte CLAW_AND_BITE_ATTACK = 4;
    private static final byte TURN_TO_GOLD_DEBUFF = 5;
    private static final int TAIL_SLAM_DAMAGE = 18;
    private static final int A4_TAIL_SLAM_DAMAGE = 20;
    private static final int TAIL_SLAM_BLOCK = 15;
    private static final int A9_TAIL_SLAM_BLOCK = 20;
    private static final int BREATH_WEAPON_DAMAGE = 14;
    private static final int A4_BREATH_WEAPON_DAMAGE = 16;
    private static final int BREATH_WEAPON_DAZEDS = 2;
    private static final int A19_BREATH_WEAPON_DAZEDS = 3;
    private static final int EMPOWERING_ROAR_DAMAGE = 16;
    private static final int A4_EMPOWERING_ROAR_DAMAGE = 18;
    private static final int EMPOWERING_ROAR_ARTIFACT = 1;
    private static final int A19_EMPOWERING_ROAR_ARTIFACT = 2;
    private static final int EMPOWERING_ROAR_STRENGTH = 1;
    private static final int A19_EMPOWERING_ROAR_STRENGTH = 2;
    private static final int CLAW_AND_BITE_DAMAGE = 2;
    private static final int A4_CLAW_AND_BITE_DAMAGE = 2;
    private static final int TURN_TO_GOLD_WRAITH_FORM = 1;
    private static final int A19_TURN_TO_GOLD_WRAITH_FORM = 2;
    private static final int TURN_TO_GOLD_BIAS = 0;
    private static final int A19_TURN_TO_GOLD_BIAS = 1;
    private static final int ARTIFACT = 1;
    private static final int A19_ARTIFACT = 2;
    private static final int HP = 380;
    private static final int A9_HP = 400;

    private int tailSlamDamage;
    private int tailSlamBlock;
    private int breathWeaponDamage;
    private int breathWeaponDazeds;
    private int empoweringRoarDamage;
    private int empoweringRoarArtifact;
    private int empoweringRoarStrength;
    private int clawAndBiteDamage;
    private int turnToGoldWraithForm;
    private int turnToGoldBias;
    private int artifact;

    private final byte[] movePattern = { TAIL_SLAM_ATTACK, BREATH_WEAPON_ATTACK, EMPOWERING_ROAR_ATTACK, CLAW_AND_BITE_ATTACK };
    private int moveIndex = 0;

    public GoldenDragon() {
        this(0.0f, 0.0f);
    }

    public GoldenDragon(final float x, final float y) {
        super(GoldenDragon.NAME, ID, A9_HP, -5.0F, 0, 600.0f, 430.0f, IMG, x, y);
        this.type = EnemyType.BOSS;
        if (AbstractDungeon.ascensionLevel >= 9) {
            this.setHp(A9_HP);
            this.tailSlamBlock = A9_TAIL_SLAM_BLOCK;
        } else {
            this.setHp(HP);
            this.tailSlamBlock = TAIL_SLAM_BLOCK;
        }

        if (AbstractDungeon.ascensionLevel >= 4) {
            this.tailSlamDamage = A4_TAIL_SLAM_DAMAGE;
            this.breathWeaponDamage = A4_BREATH_WEAPON_DAMAGE;
            this.empoweringRoarDamage = A4_EMPOWERING_ROAR_DAMAGE;
            this.clawAndBiteDamage = A4_CLAW_AND_BITE_DAMAGE;
        } else {
            this.tailSlamDamage = TAIL_SLAM_DAMAGE;
            this.breathWeaponDamage = BREATH_WEAPON_DAMAGE;
            this.empoweringRoarDamage = EMPOWERING_ROAR_DAMAGE;
            this.clawAndBiteDamage = CLAW_AND_BITE_DAMAGE;
        }
        this.damage.add(new DamageInfo(this, this.tailSlamDamage));
        this.damage.add(new DamageInfo(this, this.breathWeaponDamage));
        this.damage.add(new DamageInfo(this, this.empoweringRoarDamage));
        this.damage.add(new DamageInfo(this, this.clawAndBiteDamage));

        if (AbstractDungeon.ascensionLevel >= 19) {
            this.breathWeaponDazeds = A19_BREATH_WEAPON_DAZEDS;
            this.empoweringRoarArtifact = A19_EMPOWERING_ROAR_ARTIFACT;
            this.empoweringRoarStrength = A19_EMPOWERING_ROAR_STRENGTH;
            this.turnToGoldWraithForm = A19_TURN_TO_GOLD_WRAITH_FORM;
            this.turnToGoldBias = A19_TURN_TO_GOLD_BIAS;
            this.artifact = A19_ARTIFACT;
        } else {
            this.breathWeaponDazeds = BREATH_WEAPON_DAZEDS;
            this.empoweringRoarArtifact = EMPOWERING_ROAR_ARTIFACT;
            this.empoweringRoarStrength = EMPOWERING_ROAR_STRENGTH;
            this.turnToGoldWraithForm = TURN_TO_GOLD_WRAITH_FORM;
            this.turnToGoldBias = TURN_TO_GOLD_BIAS;
            this.artifact = ARTIFACT;
        }
    }

    @Override
    public void usePreBattleAction() {
        CardCrawlGame.music.unsilenceBGM();
        AbstractDungeon.scene.fadeOutAmbiance();
        AbstractDungeon.getCurrRoom().playBgmInstantly("BOSS_CITY");

        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new ArtifactPower(this, this.artifact), this.artifact));
    }

    @Override
    public void takeTurn() {
        switch (this.nextMove) {
            case TAIL_SLAM_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this.tailSlamBlock));
                break;
            case BREATH_WEAPON_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.FIRE));
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new Dazed(), this.breathWeaponDazeds));
                break;
            case EMPOWERING_ROAR_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(2), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new ArtifactPower(this, this.empoweringRoarArtifact), this.empoweringRoarArtifact));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, this.empoweringRoarStrength), this.empoweringRoarStrength));
                break;
            case CLAW_AND_BITE_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(3), AbstractGameAction.AttackEffect.SLASH_VERTICAL));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(3), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(3), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(3), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(3), AbstractGameAction.AttackEffect.SLASH_HEAVY));
                break;
            case TURN_TO_GOLD_DEBUFF:
                AbstractDungeon.actionManager.addToBottom(new FastShakeAction(this, 0.5F, 0.2F));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WraithFormPower(AbstractDungeon.player, -this.turnToGoldWraithForm), -this.turnToGoldWraithForm));
                if (this.turnToGoldBias > 0 && !AbstractDungeon.player.orbs.isEmpty()) {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new BiasPower(AbstractDungeon.player, this.turnToGoldBias), this.turnToGoldBias));
                }
                break;
        }
        if (this.nextMove != TURN_TO_GOLD_DEBUFF) {
            this.moveIndex++;
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
    }

    @Override
    protected void getMove(final int num) {
        if (this.moveHistory.size() >= 2 && !this.lastMove(TURN_TO_GOLD_DEBUFF) && !AbstractDungeon.player.hasPower(WraithFormPower.POWER_ID)) {
            this.setMove(MOVES[4], TURN_TO_GOLD_DEBUFF, Intent.STRONG_DEBUFF);
        } else {
            switch (this.movePattern[this.moveIndex % this.movePattern.length]) {
                case TAIL_SLAM_ATTACK:
                    this.setMove(MOVES[0], TAIL_SLAM_ATTACK, Intent.ATTACK_DEFEND, this.tailSlamDamage);
                    break;
                case BREATH_WEAPON_ATTACK:
                    this.setMove(MOVES[1], BREATH_WEAPON_ATTACK, Intent.ATTACK_DEBUFF, this.breathWeaponDamage);
                    break;
                case EMPOWERING_ROAR_ATTACK:
                    this.setMove(MOVES[2], EMPOWERING_ROAR_ATTACK, Intent.ATTACK_BUFF, this.empoweringRoarDamage);
                    break;
                case CLAW_AND_BITE_ATTACK:
                    this.setMove(MOVES[3], CLAW_AND_BITE_ATTACK, Intent.ATTACK, this.clawAndBiteDamage, 5, true);
                    break;
            }
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
        NAME = GoldenDragon.monsterStrings.NAME;
        MOVES = GoldenDragon.monsterStrings.MOVES;
    }
}