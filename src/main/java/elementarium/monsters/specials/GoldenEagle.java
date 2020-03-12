package elementarium.monsters.specials;

import basemod.abstracts.CustomMonster;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.BarricadePower;
import elementarium.Elementarium;

public class GoldenEagle extends CustomMonster
{
    public static final String ID = "Elementarium:GoldenEagle";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    private static final String IMG = Elementarium.monsterImage(ID);
    private static final byte ATTACK = 1;
    private static final int ATTACK_DAMAGE = 5;
    private static final int A2_ATTACK_DAMAGE = 6;
    private static final int ATTACK_HITS = 2;
    private static final int HP = 30;
    private static final int A7_HP = 34;

    private int attackDamage;

    public GoldenEagle() {
        this(0.0f, 0.0f);
    }

    public GoldenEagle(final float x, final float y) {
        super(GoldenEagle.NAME, ID, HP, -5.0F, 0, 200.0f, 185.0f, IMG, x, y);
        this.type = EnemyType.NORMAL;
        if (AbstractDungeon.ascensionLevel >= 7) {
            this.setHp(A7_HP);
        } else {
            this.setHp(HP);
        }

        if (AbstractDungeon.ascensionLevel >= 2) {
            this.attackDamage = A2_ATTACK_DAMAGE;
        } else {
            this.attackDamage = ATTACK_DAMAGE;
        }

        this.damage.add(new DamageInfo(this, this.attackDamage, DamageInfo.DamageType.NORMAL));
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new BarricadePower(this)));
    }
    
    @Override
    public void takeTurn() {
        switch (this.nextMove) {
            case ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                for (int i = 0; i < ATTACK_HITS; i++) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                }
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
        this.setMove(ATTACK, Intent.ATTACK, this.attackDamage, ATTACK_HITS, true);
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = GoldenEagle.monsterStrings.NAME;
        MOVES = GoldenEagle.monsterStrings.MOVES;
    }
}