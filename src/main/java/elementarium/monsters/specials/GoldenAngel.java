package elementarium.monsters.specials;

import basemod.abstracts.CustomMonster;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.FastShakeAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.BarricadePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.powers.WraithFormPower;
import elementarium.Elementarium;

public class GoldenAngel extends CustomMonster
{
    public static final String ID = "Elementarium:GoldenAngel";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    private static final String IMG = Elementarium.monsterImage(ID);
    private boolean firstMove = true;
    private static final byte TURN_TO_GOLD_DEBUFF = 1;
    private static final byte ATTACK = 2;
    private static final byte ATTACK_DEBUFF = 3;
    private static final int TURN_TO_GOLD_AMOUNT = 1;
    private static final int A17_TURN_TO_GOLD_AMOUNT = 2;
    private static final int ATTACK_DAMAGE = 10;
    private static final int A2_ATTACK_DAMAGE = 12;
    private static final int ATTACK_DEBUFF_DAMAGE = 8;
    private static final int A2_ATTACK_DEBUFF_DAMAGE = 10;
    private static final int ATTACK_DEBUFF_WEAK = 2;
    private static final int HP = 40;
    private static final int A7_HP = 44;

    private int turnToGoldAmount;
    private int attackDamage;
    private int attackAndDebuffDamage;

    public GoldenAngel() {
        this(0.0f, 0.0f);
    }

    public GoldenAngel(final float x, final float y) {
        super(GoldenAngel.NAME, ID, HP, -5.0F, 0, 300.0f, 285.0f, IMG, x, y);
        this.type = EnemyType.NORMAL;
        if (AbstractDungeon.ascensionLevel >= 7) {
            this.setHp(A7_HP);
        } else {
            this.setHp(HP);
        }

        if (AbstractDungeon.ascensionLevel >= 2) {
            this.attackDamage = A2_ATTACK_DAMAGE;
            this.attackAndDebuffDamage = A2_ATTACK_DEBUFF_DAMAGE;
        } else {
            this.attackDamage = ATTACK_DAMAGE;
            this.attackAndDebuffDamage = ATTACK_DEBUFF_DAMAGE;
        }
		
        if (AbstractDungeon.ascensionLevel >= 17) {
            this.turnToGoldAmount = A17_TURN_TO_GOLD_AMOUNT;
        } else {
            this.turnToGoldAmount = TURN_TO_GOLD_AMOUNT;
        }
        this.damage.add(new DamageInfo(this, this.attackDamage));
        this.damage.add(new DamageInfo(this, this.attackAndDebuffDamage));
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new BarricadePower(this)));
    }
    
    @Override
    public void takeTurn() {
        if (this.firstMove) {
            this.firstMove = false;
        }
        switch (this.nextMove) {
            case TURN_TO_GOLD_DEBUFF:
                AbstractDungeon.actionManager.addToBottom(new FastShakeAction(this, 0.5F, 0.2F));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WraithFormPower(AbstractDungeon.player, -this.turnToGoldAmount), -this.turnToGoldAmount));
                break;
            case ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                break;
            case ATTACK_DEBUFF:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
				AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, -ATTACK_DEBUFF_WEAK, true), -ATTACK_DEBUFF_WEAK));
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
        if (this.firstMove) {
            this.setMove(MOVES[0], TURN_TO_GOLD_DEBUFF, Intent.STRONG_DEBUFF);
        }
        else if (!this.lastMove(ATTACK_DEBUFF)) {
            this.setMove(ATTACK_DEBUFF, Intent.ATTACK_DEBUFF, this.attackAndDebuffDamage);
        }
        else {
            this.setMove(ATTACK, Intent.ATTACK, this.attackDamage);
        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = GoldenAngel.monsterStrings.NAME;
        MOVES = GoldenAngel.monsterStrings.MOVES;
    }
}