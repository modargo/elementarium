package elementarium.monsters.normals;

import basemod.abstracts.CustomMonster;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.FastShakeAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import elementarium.Elementarium;
import elementarium.powers.DelicatePower;

public class RubyGolem extends CustomMonster
{
    public static final String ID = "Elementarium:RubyGolem";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    private static final String IMG = Elementarium.monsterImage(ID);
    private static final byte RUBY_KNIVES_ATTACK = 1;
    private static final byte PRISM_DEBUFF = 2;
    private static final int RUBY_KNIVES_DAMAGE = 8;
    private static final int A2_RUBY_KNIVES_DAMAGE = 9;
    private static final int RUBY_KNIVES_HITS = 3;
    private static final int RUBY_KNIVES_BLOCK = 6;
    private static final int A7_RUBY_KNIVES_BLOCK = 9;
    private static final int PRISM_DEBUFF_AMOUNT = 2;
    private static final int DELICATE_STRENGTH = 3;
    private static final int A17_DELICATE_STRENGTH = 2;
    private static final int HP_MIN = 77;
    private static final int HP_MAX = 81;
    private static final int A7_HP_MIN = 80;
    private static final int A7_HP_MAX = 84;
    private int rubyKnivesDamage;
    private int rubyKnivesBlock;
    private int delicateStrength;

    public RubyGolem() {
        this(0.0f, 0.0f);
    }

    public RubyGolem(final float x, final float y) {
        super(RubyGolem.NAME, ID, HP_MAX, -5.0F, 0, 230.0f, 300.0f, IMG, x, y);
        this.type = EnemyType.NORMAL;
        if (AbstractDungeon.ascensionLevel >= 7) {
            this.setHp(A7_HP_MIN, A7_HP_MAX);
            this.rubyKnivesBlock = A7_RUBY_KNIVES_BLOCK;
        } else {
            this.setHp(HP_MIN, HP_MAX);
            this.rubyKnivesBlock = RUBY_KNIVES_BLOCK;
        }

        if (AbstractDungeon.ascensionLevel >= 17) {
            this.delicateStrength = A17_DELICATE_STRENGTH;
        }
        else {
            this.delicateStrength = DELICATE_STRENGTH;
        }

        if (AbstractDungeon.ascensionLevel >= 2) {
            this.rubyKnivesDamage = A2_RUBY_KNIVES_DAMAGE;
        } else {
            this.rubyKnivesDamage = RUBY_KNIVES_DAMAGE;
        }
        this.damage.add(new DamageInfo(this, this.rubyKnivesDamage));
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new DelicatePower(this, this.delicateStrength), this.delicateStrength));
    }

    @Override
    public void takeTurn() {
        switch (this.nextMove) {
            case RUBY_KNIVES_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                for (int i = 0; i < RUBY_KNIVES_HITS; i++) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                }
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this.rubyKnivesBlock));
                break;
            case PRISM_DEBUFF:
                AbstractDungeon.actionManager.addToBottom(new FastShakeAction(this, 0.5F, 0.2F));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, PRISM_DEBUFF_AMOUNT, true), PRISM_DEBUFF_AMOUNT));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new FrailPower(AbstractDungeon.player, PRISM_DEBUFF_AMOUNT, true), PRISM_DEBUFF_AMOUNT));
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
        if (!this.lastTwoMoves(RUBY_KNIVES_ATTACK) && (this.lastMove(PRISM_DEBUFF) || this.lastMoveBefore(PRISM_DEBUFF) || num < 70)) {
            this.setMove(MOVES[0], RUBY_KNIVES_ATTACK, Intent.ATTACK_DEFEND, this.rubyKnivesDamage, RUBY_KNIVES_HITS, true);
        }
        else {
            this.setMove(MOVES[1], PRISM_DEBUFF, Intent.DEBUFF);
        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = RubyGolem.monsterStrings.NAME;
        MOVES = RubyGolem.monsterStrings.MOVES;
    }
}