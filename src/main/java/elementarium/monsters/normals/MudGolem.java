package elementarium.monsters.normals;

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
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.MalleablePower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import elementarium.Elementarium;

public class MudGolem extends CustomMonster
{
    public static final String ID = "Elementarium:MudGolem";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    private static final String IMG = Elementarium.monsterImage(ID);
    private boolean firstMove = true;
    private static final byte MUD_ERUPTION_ATTACK = 1;
    private static final byte MUD_TORNADO_ATTACK = 2;
    private static final int MUD_ERUPTION_DAMAGE = 8;
    private static final int A2_MUD_ERUPTION_DAMAGE = 10;
    private static final int MUD_ERUPTION_STRENGTH = 1;
    private static final int A17_MUD_ERUPTION_STRENGTH = 1;
    private static final int MUD_TORNADO_DAMAGE = 2;
    private static final int MUD_TORNADO_HITS = 3;
    private static final int A2_MUD_TORNADO_HITS = 4;
    private static final int MUD_TORNADO_BLOCK = 2;
    private static final int A7_MUD_TORNADO_BLOCK = 3;
    private static final int MALLEABLE_AMOUNT = 1;
    private static final int A17_MALLEABLE_AMOUNT = 2;
    private static final int HP_MIN = 41;
    private static final int HP_MAX = 44;
    private static final int A7_HP_MIN = 43;
    private static final int A7_HP_MAX = 46;
    private int mudEruptionDamage;
    private int mudEruptionStrength;
    private int mudTornadoHits;
    private int mudTornadoBlock;
    private int malleableAmount;

    public MudGolem() {
        this(0.0f, 0.0f);
    }

    public MudGolem(final float x, final float y) {
        super(MudGolem.NAME, ID, HP_MAX, -5.0F, 0.0F, 235.0f, 250.0f, IMG, x, y);
        this.type = EnemyType.NORMAL;
        if (AbstractDungeon.ascensionLevel >= 7) {
            this.setHp(A7_HP_MIN, A7_HP_MAX);
            this.mudTornadoBlock = A7_MUD_TORNADO_BLOCK;
        } else {
            this.setHp(HP_MIN, HP_MAX);
            this.mudTornadoBlock = MUD_TORNADO_BLOCK;
        }

        if (AbstractDungeon.ascensionLevel >= 2) {
            this.mudEruptionDamage = A2_MUD_ERUPTION_DAMAGE;
            this.mudTornadoHits = A2_MUD_TORNADO_HITS;
        } else {
            this.mudEruptionDamage = MUD_ERUPTION_DAMAGE;
            this.mudTornadoHits = MUD_TORNADO_HITS;
        }
        this.damage.add(new DamageInfo(this, this.mudEruptionDamage));
        this.damage.add(new DamageInfo(this, MUD_TORNADO_DAMAGE));

        if (AbstractDungeon.ascensionLevel >= 17) {
            this.mudEruptionStrength = A17_MUD_ERUPTION_STRENGTH;
            this.malleableAmount = A17_MALLEABLE_AMOUNT;
        } else {
            this.mudEruptionStrength = MUD_ERUPTION_STRENGTH;
            this.malleableAmount = MALLEABLE_AMOUNT;
        }
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new MalleablePower(this, this.malleableAmount), this.malleableAmount));
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            this.firstMove = false;
        }
        switch (this.nextMove) {
            case MUD_ERUPTION_ATTACK: {
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (m == this || !m.isDying) {
                        // For this enemy, those to the left of it (which have already acted), and those that aren't attacking, give strength
                        // For enemies to the right of this enemy, give gain strength at end of turn, so their damage doesn't increase
                        if (m == this || m.drawX < this.drawX || m.getIntentDmg() == -1) {
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, this, new StrengthPower(m, this.mudEruptionStrength), this.mudEruptionStrength));
                        }
                        else {
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, this, new GainStrengthPower(m, this.mudEruptionStrength), this.mudEruptionStrength));
                        }
                    }
                }
                break;
            }
            case MUD_TORNADO_ATTACK: {
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                for (int i = 0; i < MUD_TORNADO_HITS; i++) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                }
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this.mudTornadoBlock));
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
        if (this.firstMove || this.lastMove(MUD_TORNADO_ATTACK)) {
            this.setMove(MOVES[0], MUD_ERUPTION_ATTACK, Intent.ATTACK_BUFF, this.mudEruptionDamage);
        }
        else {
            this.setMove(MOVES[0], MUD_TORNADO_ATTACK, Intent.ATTACK_DEFEND, MUD_TORNADO_DAMAGE, this.mudTornadoHits, true);
        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = MudGolem.monsterStrings.NAME;
        MOVES = MudGolem.monsterStrings.MOVES;
    }
}