package elementarium.monsters.normals;

import basemod.abstracts.CustomMonster;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.FastShakeAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.AddCardToDeckAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.WeakPower;
import elementarium.Elementarium;
import elementarium.cards.tar.StickyTar;
import elementarium.cards.tar.Tar;
import elementarium.powers.TarBodyPower;

public class TarGolem extends CustomMonster {
    public static final String ID = "Elementarium:TarGolem";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    private static final String IMG = Elementarium.monsterImage(ID);
    private boolean firstMove = true;
    private int stickyTarDebuffCount = 0;
    private static final byte TAR_SHIELD_ATTACK = 1;
    private static final byte TAR_SPRAY_ATTACK = 2;
    private static final byte STICKY_TAR_DEBUFF = 3;
    private static final int TAR_ATTACK_DAMAGE = 3;
    private static final int A2_TAR_ATTACK_DAMAGE = 4;
    private static final int TAR_ATTACK_HITS = 2;
    private static final int TAR_SHIELD_BLOCK = 3;
    private static final int A7_TAR_SHIELD_BLOCK = 5;
    private static final int STICKY_TAR_MAX = 1;
    private static final int A17_STICKY_TAR_MAX = 2;
    private static final int STICKY_TAR_DEBUFF_WEAK = 1;
    private static final int HP_MIN = 40;
    private static final int HP_MAX = 43;
    private static final int A7_HP_MIN = 42;
    private static final int A7_HP_MAX = 45;
    private int tarAttackDamage;
    private int tarShieldBlock;

    public TarGolem() {
        this(0.0f, 0.0f);
    }

    public TarGolem(final float x, final float y) {
        super(TarGolem.NAME, ID, HP_MAX, -5.0F, 0, 210.0f, 275.0f, IMG, x, y);
        this.type = EnemyType.NORMAL;
        if (AbstractDungeon.ascensionLevel >= 7) {
            this.setHp(A7_HP_MIN, A7_HP_MAX);
            this.tarShieldBlock = A7_TAR_SHIELD_BLOCK;
        } else {
            this.setHp(HP_MIN, HP_MAX);
            this.tarShieldBlock = TAR_SHIELD_BLOCK;
        }

        if (AbstractDungeon.ascensionLevel >= 2) {
            this.tarAttackDamage = A2_TAR_ATTACK_DAMAGE;
        } else {
            this.tarAttackDamage = TAR_ATTACK_DAMAGE;
        }
        this.damage.add(new DamageInfo(this, this.tarAttackDamage));
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new TarBodyPower(this)));
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            this.firstMove = false;
        }
        switch (this.nextMove) {
            case TAR_SHIELD_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                for (int i = 0; i < TAR_ATTACK_HITS; i++) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                }
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this.tarShieldBlock));
                break;
            case TAR_SPRAY_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                for (int i = 0; i < TAR_ATTACK_HITS; i++) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                }
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Tar(), 1, true, true));
                break;
            case STICKY_TAR_DEBUFF:
                AbstractDungeon.actionManager.addToBottom(new FastShakeAction(this, 0.5F, 0.2F));
                if (this.stickyTarDebuffCount < STICKY_TAR_MAX || AbstractDungeon.ascensionLevel >= 17) {
                    AbstractDungeon.actionManager.addToBottom(new AddCardToDeckAction(new StickyTar()));
                }
                if (this.stickyTarDebuffCount > 0) {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, STICKY_TAR_DEBUFF_WEAK, true), STICKY_TAR_DEBUFF_WEAK));
                }
                this.stickyTarDebuffCount++;
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
        if (!this.firstMove && this.stickyTarDebuffCount < A17_STICKY_TAR_MAX) {
            this.setMove(MOVES[2], STICKY_TAR_DEBUFF, this.stickyTarDebuffCount < STICKY_TAR_MAX || AbstractDungeon.ascensionLevel >= 17 ? Intent.STRONG_DEBUFF : Intent.DEBUFF);
        } else if ((this.firstMove && AbstractDungeon.ascensionLevel >= 17) || num < 50) {
            this.setMove(MOVES[0], TAR_SHIELD_ATTACK, Intent.ATTACK_DEFEND, this.tarAttackDamage, TAR_ATTACK_HITS, true);
        } else {
            this.setMove(MOVES[1], TAR_SPRAY_ATTACK, Intent.ATTACK_DEBUFF, this.tarAttackDamage, TAR_ATTACK_HITS, true);
        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = TarGolem.monsterStrings.NAME;
        MOVES = TarGolem.monsterStrings.MOVES;
    }
}