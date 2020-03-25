package elementarium.monsters.elites;

import basemod.abstracts.CustomMonster;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
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
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import elementarium.Elementarium;
import elementarium.act.ElementariumAct;
import elementarium.actions.SummonElementalAction;
import elementarium.monsters.normals.OrbOfFire;
import elementarium.powers.CleansePower;
import elementarium.powers.FlourishingFlamePower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class Firelord extends CustomMonster
{
    public static final String ID = "Elementarium:Firelord";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;
    private static final String IMG = Elementarium.monsterImage(ID);
    private boolean firstMove = true;
    private int summonActionsTaken = 0;
    private boolean unrestrictedSummoning;
    private static final byte HAMMER_OF_THE_FIRELORD_ATTACK = 1;
    private static final byte FIERY_WRATH_ATTACK = 2;
    private static final byte RAIN_OF_FIRE_ATTACK = 3;
    private static final byte SUMMON_ORB = 4;
    private static final int HAMMER_OF_THE_FIRELORD_DAMAGE = 13;
    private static final int A3_HAMMER_OF_THE_FIRELORD_DAMAGE = 15;
    private static final int HAMMER_OF_THE_FIRELORD_BURNS = 1;
    private static final int A18_HAMMER_OF_THE_FIRELORD_BURNS = 2;
    private static final int FIERY_WRATH_DAMAGE = 5;
    private static final int A3_FIERY_WRATH_DAMAGE = 6;
    private static final int FIERY_WRATH_HITS = 2;
    private static final int RAIN_OF_FIRE_DAMAGE = 2;
    private static final int A3_RAIN_OF_FIRE_DAMAGE = 2;
    private static final int RAIN_OF_FIRE_HITS = 4;
    private static final int A18_SUMMON_STRENGTH_SCALING = 0;
    private static final int A18_SUMMON_BLOCK_SCALING = 0;
    private static final int CLEANSE_AMOUNT = 1;
    private static final int A18_CLEANSE_AMOUNT = 2;
    private static final int FLOURISHING_FLAME_AMOUNT = 1;
    private static final int HP_MIN = 100;
    private static final int HP_MAX = 104;
    private static final int A8_HP_MIN = 104;
    private static final int A8_HP_MAX = 108;
    private int hammerOfTheFirelordDamage;
    private int hammerOfTheFirelordBurns;
    private int fieryWrathDamage;
    private int rainOfFireDamage;
    private int cleanseAmount;

    private static final Logger logger = LogManager.getLogger(Firelord.class.getName());

    public Firelord() {
        this(0.0f, 0.0f);
    }
    public Firelord(final float x, final float y) { this(x, y, false); }

    public Firelord(final float x, final float y, boolean unrestrictedSummoning) {
        super(Firelord.NAME, ID, HP_MAX, -5.0F, 0, 355, 300.0f, IMG, x, y);
        this.unrestrictedSummoning = unrestrictedSummoning;
        this.type = EnemyType.ELITE;
        this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;

        if (AbstractDungeon.ascensionLevel >= 8) {
            this.setHp(A8_HP_MIN, A8_HP_MAX);
        } else {
            this.setHp(HP_MIN, HP_MAX);
        }

        if (AbstractDungeon.ascensionLevel >= 3) {
            this.hammerOfTheFirelordDamage = A3_HAMMER_OF_THE_FIRELORD_DAMAGE;
            this.fieryWrathDamage = A3_FIERY_WRATH_DAMAGE;
            this.rainOfFireDamage = A3_RAIN_OF_FIRE_DAMAGE;
        } else {
            this.hammerOfTheFirelordDamage = HAMMER_OF_THE_FIRELORD_DAMAGE;
            this.fieryWrathDamage = FIERY_WRATH_DAMAGE;
            this.rainOfFireDamage = RAIN_OF_FIRE_DAMAGE;
        }
        this.damage.add(new DamageInfo(this, this.hammerOfTheFirelordDamage));
        this.damage.add(new DamageInfo(this, this.fieryWrathDamage));
        this.damage.add(new DamageInfo(this, this.rainOfFireDamage));

        if (AbstractDungeon.ascensionLevel >= 18) {
            this.hammerOfTheFirelordBurns = A18_HAMMER_OF_THE_FIRELORD_BURNS;
            this.cleanseAmount = A18_CLEANSE_AMOUNT;
        }
        else {
            this.hammerOfTheFirelordBurns = HAMMER_OF_THE_FIRELORD_BURNS;
            this.cleanseAmount = CLEANSE_AMOUNT;
        }
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new CleansePower(this, this.cleanseAmount, false), this.cleanseAmount));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new FlourishingFlamePower(this, FLOURISHING_FLAME_AMOUNT), FLOURISHING_FLAME_AMOUNT));
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            this.firstMove = false;
        }
        switch (this.nextMove) {
            case HAMMER_OF_THE_FIRELORD_ATTACK: {
                AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[0]));
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.SMASH));
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new Burn(), this.hammerOfTheFirelordBurns));
                break;
            }
            case FIERY_WRATH_ATTACK: {
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                for (int i = 0; i < FIERY_WRATH_HITS; i++) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.FIRE));
                }
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new CleansePower(this, 1, false), 1));

                break;
            }
            case RAIN_OF_FIRE_ATTACK: {
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                for (int i = 0; i < RAIN_OF_FIRE_HITS; i++) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(2), AbstractGameAction.AttackEffect.FIRE));
                }
                break;
            }
            case SUMMON_ORB: {
                ArrayList<Float> xPositions = this.getOpenPositions();
                int strength = AbstractDungeon.ascensionLevel >= 18 ? this.summonActionsTaken * A18_SUMMON_STRENGTH_SCALING : 0;
                int block = AbstractDungeon.ascensionLevel >= 18 ? this.summonActionsTaken * A18_SUMMON_BLOCK_SCALING : 0;
                for (Float x : xPositions) {
                    AbstractDungeon.actionManager.addToBottom(new SummonElementalAction(OrbOfFire.ID, x, 125.0F, strength, block));
                }
                this.summonActionsTaken++;
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
        if (this.firstMove) {
            this.setMove(MOVES[0], HAMMER_OF_THE_FIRELORD_ATTACK, Intent.ATTACK_DEBUFF, this.hammerOfTheFirelordDamage);
        } else {
            boolean canSummon = this.getOpenPositions().size() > 0;
            int option1;
            int option2;
            int move;
            if (this.lastMove(HAMMER_OF_THE_FIRELORD_ATTACK)) {
                option1 = FIERY_WRATH_ATTACK;
                option2 = canSummon ? SUMMON_ORB : FIERY_WRATH_ATTACK;
            }
            else if (this.lastMoveBefore(HAMMER_OF_THE_FIRELORD_ATTACK)) {
                int onlyOption = this.lastMove(FIERY_WRATH_ATTACK) && canSummon ? SUMMON_ORB : FIERY_WRATH_ATTACK;
                option1 = onlyOption;
                option2 = onlyOption;
            }
            else if ((this.lastMove(FIERY_WRATH_ATTACK) || this.lastMove(SUMMON_ORB))
                    && (this.lastMoveBefore(FIERY_WRATH_ATTACK) || this.lastMoveBefore(SUMMON_ORB))) {
                option1 = RAIN_OF_FIRE_ATTACK;
                option2 = RAIN_OF_FIRE_ATTACK;
            }
            else if (this.lastTwoMoves(RAIN_OF_FIRE_ATTACK)) {
                option1 = canSummon ? SUMMON_ORB : FIERY_WRATH_ATTACK;
                option2 = FIERY_WRATH_ATTACK;
            }
            else if (this.lastMove(RAIN_OF_FIRE_ATTACK) && this.lastMoveBefore(SUMMON_ORB)) {
                option1 = FIERY_WRATH_ATTACK;
                option2 = RAIN_OF_FIRE_ATTACK;
            }
            else if (this.lastMove(RAIN_OF_FIRE_ATTACK)) {
                option1 = canSummon ? SUMMON_ORB : RAIN_OF_FIRE_ATTACK;
                option2 = RAIN_OF_FIRE_ATTACK;
            }
            else {
                option1 = FIERY_WRATH_ATTACK;
                option2 = RAIN_OF_FIRE_ATTACK;
            }

            if (num < 50) {
                move = option1;
            } else {
                move = option2;
            }

            switch (move) {
                case FIERY_WRATH_ATTACK:
                    this.setMove(Firelord.MOVES[1], FIERY_WRATH_ATTACK, Intent.ATTACK_BUFF, this.fieryWrathDamage, FIERY_WRATH_HITS, true);
                    break;
                case RAIN_OF_FIRE_ATTACK:
                    this.setMove(Firelord.MOVES[2], RAIN_OF_FIRE_ATTACK, Intent.ATTACK, this.rainOfFireDamage, RAIN_OF_FIRE_HITS, true);
                    break;
                case SUMMON_ORB:
                    this.setMove(Firelord.MOVES[3], SUMMON_ORB, Intent.UNKNOWN);
                    break;
            }
        }
    }

    private ArrayList<Float> getOpenPositions() {
        ArrayList<Float> xPositions = new ArrayList<>();
        boolean hasPosition1 = false;
        boolean hasPosition2 = false;
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            hasPosition1 = hasPosition1 || (!m.isDying && m.drawX < this.drawX);
            hasPosition2 = hasPosition2 || (!m.isDying && m.drawX > this.drawX);
        }
        if (!hasPosition1) {
            xPositions.add(-500.0F);
        }
        if (!hasPosition2) {
            xPositions.add(100.0F);
        }

        if (xPositions.size() > 1 && this.summonActionsTaken < 2 && !this.unrestrictedSummoning) {
            xPositions.remove(1);
        }

        return xPositions;
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = Firelord.monsterStrings.NAME;
        MOVES = Firelord.monsterStrings.MOVES;
        DIALOG = Firelord.monsterStrings.DIALOG;
    }
}