package elementarium.monsters.normals;

import basemod.abstracts.CustomMonster;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import elementarium.Elementarium;
import elementarium.powers.SorrowPower;

public class StoneGolem extends CustomMonster
{
    public static final String ID = "Elementarium:StoneGolem";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    private static final String IMG = Elementarium.monsterImage(ID);
    private boolean firstMove = true;
    private static final byte STONE_BASH_ATTACK = 1;
    private static final byte SOLID_STRIKE_ATTACK = 2;
    private static final byte STOMP_AND_SLAM_ATTACK = 3;
    private static final int STONE_BASH_DAMAGE = 7;
    private static final int A2_STONE_BASH_DAMAGE = 8;
    private static final int STONE_BASH_DEBUFF_DURATION = 1;
    private static final int A17_STONE_BASH_DEBUFF_DURATION = 1;
    private static final int SOLID_STRIKE_DAMAGE = 6;
    private static final int A2_SOLID_STRIKE_DAMAGE = 7;
    private static final int SOLID_STRIKE_PLATED_ARMOR = 1;
    private static final int A7_SOLID_STRIKE_PLATED_ARMOR = 2;
    private static final int STOMP_AND_SLAM_DAMAGE = 4;
    private static final int A2_STOMP_AND_SLAM_DAMAGE = 5;
    private static final int STOMP_AND_SLAM_HITS = 2;
    private static final int STARTING_PLATED_ARMOR = 4;
    private static final int A7_STARTING_PLATED_ARMOR = 5;
    private static final int SORROW_STRENGTH = 5;
    private static final int A17_SORROW_STRENGTH = 8;
    private static final int HP_MIN = 49;
    private static final int HP_MAX = 52;
    private static final int A7_HP_MIN = 51;
    private static final int A7_HP_MAX = 54;
    private int stoneBashDamage;
    private int stoneBashDebuffDuration;
    private int solidStrikeDamage;
    private int solidStrikePlatedArmor;
    private int stompAndSlamDamage;
    private int startingPlatedArmor;
    private int sorrowStrength;
    private boolean skipStartingBlock;
    private boolean wait;

    public StoneGolem() {
        this(0.0f, 0.0f, false, false);
    }

    public StoneGolem(final float x, final float y, boolean skipStartingBlock, boolean wait) {
        super(StoneGolem.NAME, ID, HP_MAX, -5.0F, 0, 200.0f, 250.0f, IMG, x, y);
        this.type = EnemyType.NORMAL;
        this.wait = wait;
        this.skipStartingBlock = skipStartingBlock;
        if (AbstractDungeon.ascensionLevel >= 7) {
            this.setHp(A7_HP_MIN, A7_HP_MAX);
            this.solidStrikePlatedArmor = A7_SOLID_STRIKE_PLATED_ARMOR;
            this.startingPlatedArmor = A7_STARTING_PLATED_ARMOR;
        } else {
            this.setHp(HP_MIN, HP_MAX);
            this.solidStrikePlatedArmor = SOLID_STRIKE_PLATED_ARMOR;
            this.startingPlatedArmor = STARTING_PLATED_ARMOR;
        }

        if (AbstractDungeon.ascensionLevel >= 2) {
            this.stoneBashDamage = A2_STONE_BASH_DAMAGE;
            this.solidStrikeDamage = A2_SOLID_STRIKE_DAMAGE;
            this.stompAndSlamDamage = A2_STOMP_AND_SLAM_DAMAGE;
        } else {
            this.stoneBashDamage = STONE_BASH_DAMAGE;
            this.solidStrikeDamage = SOLID_STRIKE_DAMAGE;
            this.stompAndSlamDamage = STOMP_AND_SLAM_DAMAGE;
        }
        this.damage.add(new DamageInfo(this, this.stoneBashDamage));
        this.damage.add(new DamageInfo(this, this.solidStrikeDamage));
        this.damage.add(new DamageInfo(this, this.stompAndSlamDamage));

        if (AbstractDungeon.ascensionLevel >= 17) {
            this.stoneBashDebuffDuration = A17_STONE_BASH_DEBUFF_DURATION;
            this.sorrowStrength = A17_SORROW_STRENGTH;
        } else {
            this.stoneBashDebuffDuration = STONE_BASH_DEBUFF_DURATION;
            this.sorrowStrength = SORROW_STRENGTH;
        }
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new PlatedArmorPower(this, this.startingPlatedArmor), this.startingPlatedArmor));
        if (!this.skipStartingBlock) {
            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this.startingPlatedArmor));
        }
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new SorrowPower(this, this.sorrowStrength, this.wait), this.sorrowStrength));
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            this.firstMove = false;
        }
        switch (this.nextMove) {
            case STONE_BASH_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new FrailPower(AbstractDungeon.player, this.stoneBashDebuffDuration, true), this.stoneBashDebuffDuration));
                break;
            case SOLID_STRIKE_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new PlatedArmorPower(this, this.solidStrikePlatedArmor), this.solidStrikePlatedArmor));
                break;
            case STOMP_AND_SLAM_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                for (int i = 0; i < STOMP_AND_SLAM_HITS; i++) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(2), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                }
                break;

        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        byte move;
        if (this.firstMove) {
            move = AbstractDungeon.ascensionLevel >= 17 ? STONE_BASH_ATTACK :
                num < 50 ? SOLID_STRIKE_ATTACK : STOMP_AND_SLAM_ATTACK;
        }
        else {
            if (this.lastMove(STONE_BASH_ATTACK)) {
                move = num < 50 ? SOLID_STRIKE_ATTACK : STOMP_AND_SLAM_ATTACK;
            }
            else if (this.lastTwoMoves(SOLID_STRIKE_ATTACK)) {
                move = num < 65 ? STOMP_AND_SLAM_ATTACK : STONE_BASH_ATTACK;
            }
            else if (this.lastTwoMoves(STOMP_AND_SLAM_ATTACK)) {
                move = num < 65 ? SOLID_STRIKE_ATTACK : STONE_BASH_ATTACK;
            }
            else {
                move = num < 20 ? STONE_BASH_ATTACK :
                        num < 60 ? SOLID_STRIKE_ATTACK : STOMP_AND_SLAM_ATTACK;
            }
        }
        switch (move) {
            case STONE_BASH_ATTACK:
                this.setMove(MOVES[0], STONE_BASH_ATTACK, Intent.ATTACK_DEBUFF, this.stoneBashDamage);
                break;
            case SOLID_STRIKE_ATTACK:
                this.setMove(MOVES[1], SOLID_STRIKE_ATTACK, Intent.ATTACK_BUFF, this.solidStrikeDamage);
                break;
            case STOMP_AND_SLAM_ATTACK:
                this.setMove(MOVES[2], STOMP_AND_SLAM_ATTACK, Intent.ATTACK, this.stompAndSlamDamage, STOMP_AND_SLAM_HITS, true);
                break;
        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = StoneGolem.monsterStrings.NAME;
        MOVES = StoneGolem.monsterStrings.MOVES;
    }
}