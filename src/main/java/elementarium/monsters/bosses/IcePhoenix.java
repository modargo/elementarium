package elementarium.monsters.bosses;

import basemod.abstracts.CustomMonster;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import com.megacrit.cardcrawl.vfx.combat.BlizzardEffect;
import elementarium.Elementarium;
import elementarium.powers.FreezingClawsPower;
import elementarium.powers.FreezingContemptPower;
import elementarium.powers.PhoenixRebirthPower;
import elementarium.relics.ElementariumTrophy;

import java.util.ArrayList;
import java.util.List;

public class IcePhoenix extends CustomMonster
{
    public static final String ID = "Elementarium:IcePhoenix";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    private static final String IMG = Elementarium.monsterImage(ID);
    private static final byte CLAW_SWIPE_ATTACK = 1;
    private static final byte WING_SWEEP_ATTACK = 2;
    private static final byte GLACIAL_CALM_BUFF = 3;
    private static final byte FREEZING_TEMPEST_ATTACK = 4;
    private static final int CLAW_SWIPE_DAMAGE = 4;
    private static final int A4_CLAW_SWIPE_DAMAGE = 5;
    private static final int CLAW_SWIPE_HITS = 2;
    private static final int WING_SWEEP_DAMAGE = 11;
    private static final int A4_WING_SWEEP_DAMAGE = 13;
    private static final int WING_SWEEP_DEBUFF_AMOUNT = 1;
    private static final int GLACIAL_CALM_BLOCK = 8;
    private static final int A9_GLACIAL_CALM_BLOCK = 10;
    private static final int GLACIAL_CALM_METALLICIZE = 1;
    private static final int FREEZING_TEMPEST_DAMAGE = 3;
    private static final int FREEZING_TEMPEST_HITS = 4;
    private static final int FREEZING_TEMPEST_ARTIFACT = 1;
    private static final int A19_FREEZING_TEMPEST_ARTIFACT = 1;
    private static final int FREEZING_CONTEMPT_STRENGTH = 1;
    private static final int A19_FREEZING_CONTEMPT_STRENGTH = 2;
    private static final int FREEZING_CLAWS_METALLICIZE = 1;
    private static final int A19_FREEZING_CLAWS_METALLICIZE = 2;
    private static final int HP = 200;
    private static final int A9_HP = 220;

    private int clawSwipeDamage;
    private int wingSweepDamage;
    private int glacialCalmBlock;
    private int freezingTempestArtifact;
    private int freezingContemptStrength;
    private int freezingClawsMetallicize;
    private boolean usedFreezingTempest = false;

    public IcePhoenix() {
        this(0.0f, 0.0f);
    }

    public IcePhoenix(final float x, final float y) {
        super(IcePhoenix.NAME, ID, A9_HP, -5.0F, 0, 230.0f, 290.0f, IMG, x, y);
        this.type = EnemyType.BOSS;
        if (AbstractDungeon.ascensionLevel >= 9) {
            this.setHp(A9_HP);
            this.glacialCalmBlock = A9_GLACIAL_CALM_BLOCK;
        } else {
            this.setHp(HP);
            this.glacialCalmBlock = GLACIAL_CALM_BLOCK;
        }

        if (AbstractDungeon.ascensionLevel >= 4) {
            this.clawSwipeDamage = A4_CLAW_SWIPE_DAMAGE;
            this.wingSweepDamage = A4_WING_SWEEP_DAMAGE;
        } else {
            this.clawSwipeDamage = CLAW_SWIPE_DAMAGE;
            this.wingSweepDamage = WING_SWEEP_DAMAGE;
        }
        this.damage.add(new DamageInfo(this, this.clawSwipeDamage));
        this.damage.add(new DamageInfo(this, this.wingSweepDamage));
        this.damage.add(new DamageInfo(this, FREEZING_TEMPEST_DAMAGE));

        if (AbstractDungeon.ascensionLevel >= 19) {
            this.freezingContemptStrength = A19_FREEZING_CONTEMPT_STRENGTH;
            this.freezingClawsMetallicize = A19_FREEZING_CLAWS_METALLICIZE;
            this.freezingTempestArtifact = A19_FREEZING_TEMPEST_ARTIFACT;
        } else {
            this.freezingContemptStrength = FREEZING_CONTEMPT_STRENGTH;
            this.freezingClawsMetallicize = FREEZING_CLAWS_METALLICIZE;
            this.freezingTempestArtifact = FREEZING_TEMPEST_ARTIFACT;
        }
    }

    @Override
    public void usePreBattleAction() {
        this.addToBot(new ApplyPowerAction(this, this, new FreezingContemptPower(this, this.freezingContemptStrength), this.freezingContemptStrength));
        this.addToBot(new ApplyPowerAction(this, this, new FreezingClawsPower(this, this.freezingClawsMetallicize)));
        this.addToBot(new ApplyPowerAction(this, this, new PhoenixRebirthPower(this)));
    }

    @Override
    public void takeTurn() {
        switch (this.nextMove) {
            case CLAW_SWIPE_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                for (int i = 0; i < CLAW_SWIPE_HITS; i++) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                }
                break;
            case WING_SWEEP_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new FrailPower(AbstractDungeon.player, WING_SWEEP_DEBUFF_AMOUNT, true), WING_SWEEP_DEBUFF_AMOUNT));
                break;
            case GLACIAL_CALM_BUFF:
                for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (m != null && !m.isDying) {
                        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(m, this.glacialCalmBlock));
                    }
                }
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new MetallicizePower(this, GLACIAL_CALM_METALLICIZE), GLACIAL_CALM_METALLICIZE));
                break;
            case FREEZING_TEMPEST_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new VFXAction(new BlizzardEffect(30, true), 1.0F));
                for (int i = 0; i < FREEZING_TEMPEST_HITS; i++){
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(2), AbstractGameAction.AttackEffect.NONE));
                }
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new ArtifactPower(this, this.freezingTempestArtifact), this.freezingTempestArtifact));
                this.usedFreezingTempest = true;
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
        if (!this.usedFreezingTempest && this.currentHealth <= this.maxHealth / 2) {
            this.setMove(MOVES[3], FREEZING_TEMPEST_ATTACK, Intent.ATTACK_BUFF, FREEZING_TEMPEST_DAMAGE, FREEZING_TEMPEST_HITS, true);
            return;
        }

        byte move;
        int s = this.moveHistory.size();
        int movesToCheck = s % 3;
        List<Byte> potentialMoves = new ArrayList<>();
        potentialMoves.add(CLAW_SWIPE_ATTACK);
        potentialMoves.add(WING_SWEEP_ATTACK);
        potentialMoves.add(GLACIAL_CALM_BUFF);
        if (movesToCheck > 0 && s > 0) {
            potentialMoves.remove(this.moveHistory.get(s - 1));
        }
        if (movesToCheck > 1 && s > 1) {
            potentialMoves.remove(this.moveHistory.get(s - 2));
        }

        if (potentialMoves.size() == 1){
            move = potentialMoves.get(0);
        }
        else if (potentialMoves.size() == 2) {
            move = num < 50 ? potentialMoves.get(0) : potentialMoves.get(1);
        }
        else {
            move = num < 40 ? potentialMoves.get(0) : num < 80 ? potentialMoves.get(1) : potentialMoves.get(2);
        }

        switch (move) {
            case CLAW_SWIPE_ATTACK:
                this.setMove(MOVES[0], CLAW_SWIPE_ATTACK, Intent.ATTACK, this.clawSwipeDamage, CLAW_SWIPE_HITS, true);
                break;
            case WING_SWEEP_ATTACK:
                this.setMove(MOVES[1], WING_SWEEP_ATTACK, Intent.ATTACK_DEBUFF, this.wingSweepDamage);
                break;
            case GLACIAL_CALM_BUFF:
                this.setMove(MOVES[2], GLACIAL_CALM_BUFF, Intent.DEFEND_BUFF);
                break;
        }
    }

    @Override
    public void die() {
        super.die();
        if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            this.useFastShakeAnimation(5.0F);
            CardCrawlGame.screenShake.rumble(4.0F);
            this.onBossVictoryLogic();
            if (!AbstractDungeon.player.hasRelic(ElementariumTrophy.ID)) {
                AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), new ElementariumTrophy());
            }
        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = IcePhoenix.monsterStrings.NAME;
        MOVES = IcePhoenix.monsterStrings.MOVES;
    }
}