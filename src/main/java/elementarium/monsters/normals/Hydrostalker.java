package elementarium.monsters.normals;

import basemod.abstracts.CustomMonster;
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
import com.megacrit.cardcrawl.powers.ArtifactPower;
import elementarium.Elementarium;
import elementarium.powers.WaveformStrengthPower;

public class Hydrostalker extends CustomMonster
{
    public static final String ID = "Elementarium:Hydrostalker";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    private static final String IMG = Elementarium.monsterImage(ID);
    private boolean firstMove = true;
    private static final byte GUSH_ATTACK = 1;
    private static final byte SURGE_ATTACK = 2;
    private static final byte FLOOD_ATTACK = 3;
    private static final int GUSH_DAMAGE = 12;
    private static final int A2_GUSH_DAMAGE = 14;
    private static final int SURGE_DAMAGE = 8;
    private static final int A2_SURGE_DAMAGE = 9;
    private static final int SURGE_ARTIFACT = 1;
    private static final int FLOOD_DAMAGE = 2;
    private static final int A2_FLOOD_DAMAGE = 3;
    private static final int FLOOD_HITS = 2;
    private static final int HP_MIN = 147;
    private static final int HP_MAX = 155;
    private static final int A7_HP_MIN = 152;
    private static final int A7_HP_MAX = 160;
    private int gushDamage;
    private int surgeDamage;
    private int floodDamage;

    public Hydrostalker() {
        this(0.0f, 0.0f);
    }

    public Hydrostalker(final float x, final float y) {
        super(Hydrostalker.NAME, ID, HP_MAX, -5.0F, 0, 295.0f, 325.0f, IMG, x, y);
        this.type = EnemyType.NORMAL;
        if (AbstractDungeon.ascensionLevel >= 7) {
            this.setHp(A7_HP_MIN, A7_HP_MAX);
        } else {
            this.setHp(HP_MIN, HP_MAX);
        }

        if (AbstractDungeon.ascensionLevel >= 2) {
            this.gushDamage = A2_GUSH_DAMAGE;
            this.surgeDamage = A2_SURGE_DAMAGE;
            this.floodDamage = A2_FLOOD_DAMAGE;
        } else {
            this.gushDamage = GUSH_DAMAGE;
            this.surgeDamage = SURGE_DAMAGE;
            this.floodDamage = FLOOD_DAMAGE;
        }

        this.damage.add(new DamageInfo(this, this.gushDamage));
        this.damage.add(new DamageInfo(this, this.surgeDamage));
        this.damage.add(new DamageInfo(this, this.floodDamage));
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new WaveformStrengthPower(this)));
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            this.firstMove = false;
        }
        switch (this.nextMove) {
            case GUSH_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_HEAVY));
                break;
            case SURGE_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new ArtifactPower(this, SURGE_ARTIFACT), SURGE_ARTIFACT));
                break;
            case FLOOD_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                for(int i = 0; i < FLOOD_HITS; i++) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(2), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                }
                break;
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.moveHistory.size() >= 3 && !this.lastMove(FLOOD_ATTACK) && !this.lastMoveBefore(FLOOD_ATTACK) && this.moveHistory.get(this.moveHistory.size() - 3) != FLOOD_ATTACK) {
            this.setMove(MOVES[2], FLOOD_ATTACK, Intent.ATTACK, this.floodDamage, FLOOD_HITS, true);
        }
        else if (this.firstMove || this.lastTwoMoves(SURGE_ATTACK) || (!this.lastTwoMoves(GUSH_ATTACK) && num < 50)) {
            this.setMove(MOVES[0], GUSH_ATTACK, Intent.ATTACK, this.gushDamage);
        }
        else {
            this.setMove(MOVES[1], SURGE_ATTACK, Intent.ATTACK_BUFF, this.surgeDamage);
        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = Hydrostalker.monsterStrings.NAME;
        MOVES = Hydrostalker.monsterStrings.MOVES;
    }
}