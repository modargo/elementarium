package elementarium.monsters.elites;

import basemod.abstracts.CustomMonster;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.vfx.combat.InflameEffect;
import elementarium.Elementarium;

public class FlameHerald extends CustomMonster
{
    public static final String ID = "Elementarium:FlameHerald";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;
    private static final String IMG = Elementarium.monsterImage(ID);
    private static final byte GATHERING_FLAMES = 1;
    private static final byte SPEAR_THRUST_ATTACK = 2;
    private static final byte RITUAL_STRIKE_ATTACK = 3;
    private static final byte PROTECTING_SLAM_ATTACK = 4;
    private static final int GATHERING_FLAMES_RITUAL = 1;
    private static final int A18_GATHERING_FLAMES_RITUAL = 2;
    private static final int GATHERING_FLAMES_BLOCK = 3;
    private static final int A8_GATHERING_FLAMES_BLOCK = 5;
    private static final int GATHERING_FLAMES_HEAL = 3;
    private static final int A18_GATHERING_FLAMES_HEAL = 5;
    private static final int SPEAR_THRUST_DAMAGE = 9;
    private static final int A3_SPEAR_THRUST_DAMAGE = 10;
    private static final int RITUAL_STRIKE_DAMAGE = 5;
    private static final int A3_RITUAL_STRIKE_DAMAGE = 7;
    private static final int RITUAL_STRIKE_THORNS = 1;
    private static final int PROTECTING_SLAM_DAMAGE = 6;
    private static final int A3_PROTECTING_SLAM_DAMAGE = 8;
    private static final int PROTECTING_SLAM_PLATED_ARMOR = 1;
    private static final int A18_PROTECTING_SLAM_PLATED_ARMOR = 2;
    private static final int INITIAL_PLATED_ARMOR = 2;
    private static final int A8_INITIAL_PLATED_ARMOR = 3;
    private static final int INITIAL_THORNS = 1;
    private static final int A18_INITIAL_THORNS = 2;
    private static final int HP_MIN = 90;
    private static final int HP_MAX = 94;
    private static final int A8_HP_MIN = 92;
    private static final int A8_HP_MAX = 96;
    private int gatheringFlamesRitual;
    private int gatheringFlamesBlock;
    private int gatheringFlamesHeal;
    private int spearThrustDamage;
    private int ritualStrikeDamage;
    private int protectingSlamDamage;
    private int protectingSlamPlatedArmor;
    private int initialPlatedArmor;
    private int initialThorns;
    private boolean hasRitual = false;

    public FlameHerald() {
        this(0.0f, 0.0f);
    }

    public FlameHerald(final float x, final float y) {
        super(FlameHerald.NAME, ID, HP_MAX, -5.0F, 0, 200.0f, 275.0f, IMG, x, y);
        this.type = EnemyType.ELITE;
        this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;

        if (AbstractDungeon.ascensionLevel >= 8) {
            this.setHp(A8_HP_MIN, A8_HP_MAX);
            this.initialPlatedArmor = A8_INITIAL_PLATED_ARMOR;
            this.gatheringFlamesBlock = A8_GATHERING_FLAMES_BLOCK;
        } else {
            this.setHp(HP_MIN, HP_MAX);
            this.initialPlatedArmor = INITIAL_PLATED_ARMOR;
            this.gatheringFlamesBlock = GATHERING_FLAMES_BLOCK;
        }

        if (AbstractDungeon.ascensionLevel >= 3) {
            this.spearThrustDamage = A3_SPEAR_THRUST_DAMAGE;
            this.ritualStrikeDamage = A3_RITUAL_STRIKE_DAMAGE;
            this.protectingSlamDamage = A3_PROTECTING_SLAM_DAMAGE;
        } else {
            this.spearThrustDamage = SPEAR_THRUST_DAMAGE;
            this.ritualStrikeDamage = RITUAL_STRIKE_DAMAGE;
            this.protectingSlamDamage = PROTECTING_SLAM_DAMAGE;
        }
        this.damage.add(new DamageInfo(this, this.spearThrustDamage));
        this.damage.add(new DamageInfo(this, this.ritualStrikeDamage));
        this.damage.add(new DamageInfo(this, this.protectingSlamDamage));

        if (AbstractDungeon.ascensionLevel >= 18) {
            this.gatheringFlamesRitual = A18_GATHERING_FLAMES_RITUAL;
            this.gatheringFlamesHeal = A18_GATHERING_FLAMES_HEAL;
            this.protectingSlamPlatedArmor = A18_PROTECTING_SLAM_PLATED_ARMOR;
            this.initialThorns = A18_INITIAL_THORNS;
        } else {
            this.gatheringFlamesRitual = GATHERING_FLAMES_RITUAL;
            this.gatheringFlamesHeal = GATHERING_FLAMES_HEAL;
            this.protectingSlamPlatedArmor = PROTECTING_SLAM_PLATED_ARMOR;
            this.initialThorns = INITIAL_THORNS;
        }
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new ThornsPower(this, this.initialThorns), this.initialThorns));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new PlatedArmorPower(this, this.initialPlatedArmor), this.initialPlatedArmor));
    }
    
    @Override
    public void takeTurn() {
        switch (this.nextMove) {
            case GATHERING_FLAMES: {
                if (!this.hasRitual) {
                    this.hasRitual = true;
                    AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[0]));
                    AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new InflameEffect(this), 0.25F));
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new RitualPower(this, this.gatheringFlamesRitual, false), this.gatheringFlamesRitual));
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, this.gatheringFlamesRitual), this.gatheringFlamesRitual));
                }
                else {
                    AbstractDungeon.actionManager.addToBottom(new HealAction(this, this, this.gatheringFlamesHeal));
                    for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                        if (m != this && !m.isDying) {
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, this, new StrengthPower(m, 1), 1));
                        }
                    }
                }
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, this.gatheringFlamesBlock));
                break;
            }
            case SPEAR_THRUST_ATTACK: {
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                break;
            }
            case RITUAL_STRIKE_ATTACK: {
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new ThornsPower(this, RITUAL_STRIKE_THORNS), RITUAL_STRIKE_THORNS));
                break;
            }
            case PROTECTING_SLAM_ATTACK: {
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(2), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new PlatedArmorPower(this, this.protectingSlamPlatedArmor), this.protectingSlamPlatedArmor));
                break;
            }
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
    }

    @Override
    protected void getMove(final int num) {
        if ((this.lastMove(GATHERING_FLAMES) || this.lastMove(SPEAR_THRUST_ATTACK))
            && (this.lastMoveBefore(GATHERING_FLAMES) || this.lastMoveBefore(SPEAR_THRUST_ATTACK))) {
            if (this.lastMove(PROTECTING_SLAM_ATTACK) || (num < 50 && !this.lastMove(RITUAL_STRIKE_ATTACK))) {
                this.setMove(FlameHerald.MOVES[2], RITUAL_STRIKE_ATTACK, Intent.ATTACK_BUFF, this.ritualStrikeDamage);
            }
            else {
                this.setMove(FlameHerald.MOVES[3], PROTECTING_SLAM_ATTACK, Intent.ATTACK_DEFEND, this.protectingSlamDamage);
            }
        }
        else {
            if (this.lastMove(SPEAR_THRUST_ATTACK) || (num < 50 && !this.lastMove(GATHERING_FLAMES))) {
                this.setMove(FlameHerald.MOVES[0], GATHERING_FLAMES, Intent.DEFEND_BUFF);
            }
            else {
                this.setMove(FlameHerald.MOVES[1], SPEAR_THRUST_ATTACK, Intent.ATTACK, this.spearThrustDamage);
            }
        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = FlameHerald.monsterStrings.NAME;
        MOVES = FlameHerald.monsterStrings.MOVES;
        DIALOG = FlameHerald.monsterStrings.DIALOG;
    }
}