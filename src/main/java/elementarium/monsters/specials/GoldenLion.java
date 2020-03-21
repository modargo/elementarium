package elementarium.monsters.specials;

import basemod.abstracts.CustomMonster;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.BarricadePower;
import elementarium.Elementarium;

public class GoldenLion extends CustomMonster
{
    public static final String ID = "Elementarium:GoldenLion";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    private static final String IMG = Elementarium.monsterImage(ID);
    private static final byte ATTACK_AND_BLOCK = 1;
    private static final byte ATTACK = 2;
    private static final int ATTACK_AND_BLOCK_DAMAGE = 3;
    private static final int A2_ATTACK_AND_BLOCK_DAMAGE = 5;
    private static final int ATTACK_AND_BLOCK_BLOCK = 2;
    private static final int A17_ATTACK_AND_BLOCK_BLOCK = 3;
    private static final int ATTACK_DAMAGE = 10;
    private static final int A2_ATTACK_DAMAGE = 12;
    private static final int HP = 37;
    private static final int A7_HP = 41;

    private int attackAndBlockDamage;
    private int attackAndBlockBlock;
    private int attackDamage;

    public GoldenLion() {
        this(0.0f, 0.0f);
    }

    public GoldenLion(final float x, final float y) {
        super(GoldenLion.NAME, ID, HP, -5.0F, 0, 230.0f, 225.0f, IMG, x, y);
        this.type = EnemyType.NORMAL;
        if (AbstractDungeon.ascensionLevel >= 7) {
            this.setHp(A7_HP);
        } else {
            this.setHp(HP);
        }

        if (AbstractDungeon.ascensionLevel >= 2) {
            this.attackAndBlockDamage = A2_ATTACK_AND_BLOCK_DAMAGE;
            this.attackDamage = A2_ATTACK_DAMAGE;
        } else {
            this.attackAndBlockDamage = ATTACK_AND_BLOCK_DAMAGE;
            this.attackDamage = ATTACK_DAMAGE;
        }

        this.damage.add(new DamageInfo(this, this.attackAndBlockDamage));
        this.damage.add(new DamageInfo(this, this.attackDamage));
		
        if (AbstractDungeon.ascensionLevel >= 17) {
            this.attackAndBlockBlock = A17_ATTACK_AND_BLOCK_BLOCK;
        } else {
            this.attackAndBlockBlock = ATTACK_AND_BLOCK_BLOCK;
        }
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new BarricadePower(this)));
    }
    
    @Override
    public void takeTurn() {
        switch (this.nextMove) {
            case ATTACK_AND_BLOCK:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (m == this || !m.isDying) {
                        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(m, this, this.attackAndBlockBlock));
                    }
                }
                break;
            case ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.SLASH_VERTICAL));
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
        if (this.lastTwoMoves(ATTACK_AND_BLOCK)) {
            this.setMove(ATTACK, Intent.ATTACK, this.attackDamage);
        }
        else {
            this.setMove(ATTACK_AND_BLOCK, Intent.ATTACK_DEFEND, this.attackAndBlockDamage);
        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = GoldenLion.monsterStrings.NAME;
        MOVES = GoldenLion.monsterStrings.MOVES;
    }
}