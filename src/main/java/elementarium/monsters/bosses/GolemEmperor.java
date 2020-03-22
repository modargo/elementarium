package elementarium.monsters.bosses;

import basemod.abstracts.CustomMonster;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.actions.utility.HideHealthBarAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.BufferPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.InflameEffect;
import elementarium.Elementarium;
import elementarium.actions.SummonGolemAction;
import elementarium.monsters.normals.MudGolem;
import elementarium.monsters.normals.RubyGolem;
import elementarium.monsters.normals.StoneGolem;
import elementarium.monsters.normals.TarGolem;
import elementarium.monsters.elites.WarGolem;
import elementarium.relics.ElementariumTrophy;

import java.util.Arrays;
import java.util.List;

public class GolemEmperor extends CustomMonster {
    public static final String ID = "Elementarium:GolemEmperor";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    private static final String IMG = Elementarium.monsterImage(ID);
    private boolean firstMove = true;
    private static final byte CALL_GOLEM_MOVE = 1;
    private static final byte GOLEM_SHIELD_ATTACK = 2;
    private static final byte LOOSE_MATERIALS_ATTACK = 3;
    private static final int CALL_GOLEM_STRENGTH = 3;
    private static final int A19_CALL_GOLEM_STRENGTH = 4;
    private static final float SUMMONED_GOLEM_HP_MULTIPLIER = 0.50F;
    private static final int CRUMBLING_AMOUNT = 3;
    private static final int CRUMBLING_STRENGTH_LOSS = 1;
    private static final int GOLEM_SHIELD_DAMAGE = 8;
    private static final int A4_GOLEM_SHIELD_DAMAGE = 10;
    private static final int LOOSE_MATERIALS_DAMAGE = 3;
    private static final int A4_LOOSE_MATERIALS_DAMAGE = 4;
    private static final int LOOSE_MATERIALS_HITS = 2;
    private static final int HP = 290;
    private static final int A9_HP = 310;

    private static final float[] xPositions = new float[]{-170.0F, -450.0F};
    private static final List<List<GolemInfo>> GOLEM_GROUPS = Arrays.asList(
            Arrays.asList(new GolemInfo(RubyGolem.ID, xPositions[0], -4)),
            Arrays.asList(new GolemInfo(RubyGolem.ID, xPositions[0], -4), new GolemInfo(StoneGolem.ID, xPositions[1], -4)),
            Arrays.asList(new GolemInfo(WarGolem.ID, xPositions[0], -10)),
            Arrays.asList(new GolemInfo(WarGolem.ID, xPositions[0], -10), new GolemInfo(RubyGolem.ID, xPositions[1], -4))
    );
    private static final List<List<GolemInfo>> A19_GOLEM_GROUPS = Arrays.asList(
            Arrays.asList(new GolemInfo(RubyGolem.ID, xPositions[0], -4), new GolemInfo(TarGolem.ID, xPositions[1], -2)),
            Arrays.asList(new GolemInfo(RubyGolem.ID, xPositions[0], -4), new GolemInfo(StoneGolem.ID, xPositions[1], -4)),
            Arrays.asList(new GolemInfo(MudGolem.ID, xPositions[0], -3), new GolemInfo(WarGolem.ID, xPositions[1], -10)),
            Arrays.asList(new GolemInfo(WarGolem.ID, xPositions[0], -10), new GolemInfo(RubyGolem.ID, xPositions[1], -4))
    );

    private int callGolemStrength;
    private int golemShieldDamage;
    private int looseMaterialsDamage;
    private int callGolemCounter = 0;

    public GolemEmperor() {
        this(0.0f, 0.0f);
    }

    public GolemEmperor(final float x, final float y) {
        super(GolemEmperor.NAME, ID, A9_HP, 0.0F, 0, 346.0f, 500.0f, IMG, x, y);
        this.type = EnemyType.BOSS;
        if (AbstractDungeon.ascensionLevel >= 9) {
            this.setHp(A9_HP);
        } else {
            this.setHp(HP);
        }

        if (AbstractDungeon.ascensionLevel >= 4) {
            this.golemShieldDamage = A4_GOLEM_SHIELD_DAMAGE;
            this.looseMaterialsDamage = A4_LOOSE_MATERIALS_DAMAGE;
        } else {
            this.golemShieldDamage = GOLEM_SHIELD_DAMAGE;
            this.looseMaterialsDamage = LOOSE_MATERIALS_DAMAGE;
        }
        this.damage.add(new DamageInfo(this, this.golemShieldDamage));
        this.damage.add(new DamageInfo(this, this.looseMaterialsDamage));

        if (AbstractDungeon.ascensionLevel >= 19) {
            this.callGolemStrength = A19_CALL_GOLEM_STRENGTH;
        } else {
            this.callGolemStrength = CALL_GOLEM_STRENGTH;
        }
    }

    @Override
    public void usePreBattleAction() {
        CardCrawlGame.music.unsilenceBGM();
        AbstractDungeon.scene.fadeOutAmbiance();
        AbstractDungeon.getCurrRoom().playBgmInstantly("BOSS_CITY");
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            this.firstMove = false;
        }
        switch (this.nextMove) {
            case CALL_GOLEM_MOVE:
                this.summonNextGolemGroup(false);
                if (this.callGolemCounter > 1) {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, this.callGolemStrength), this.callGolemStrength));
                }
                break;
            case GOLEM_SHIELD_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.SHIELD));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new BufferPower(this, 1), 1));
                break;
            case LOOSE_MATERIALS_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
                for (int i = 0; i < LOOSE_MATERIALS_HITS; i++) {
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                }
                break;

        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    private void summonNextGolemGroup(boolean firstTurn) {
        List<List<GolemInfo>> golemGroups = AbstractDungeon.ascensionLevel >= 19 ? A19_GOLEM_GROUPS : GOLEM_GROUPS;
        List<GolemInfo> golemGroup = golemGroups.get(Math.min(this.callGolemCounter, golemGroups.size() - 1));
        for (GolemInfo s : golemGroup) {
            this.addToBot(new SummonGolemAction(s.golemID, s.xPosition, 0.0F, s.strength, SUMMONED_GOLEM_HP_MULTIPLIER, CRUMBLING_AMOUNT, this, CRUMBLING_STRENGTH_LOSS, firstTurn));
        }

        this.callGolemCounter++;
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
    }

    @Override
    protected void getMove(final int num) {
        if (this.moveHistory.size() % CRUMBLING_AMOUNT == 0) {
            this.setMove(MOVES[0], CALL_GOLEM_MOVE, Intent.UNKNOWN);
        } else {
            if (this.lastMove(LOOSE_MATERIALS_ATTACK) || (num < 50 && !this.lastMove(GOLEM_SHIELD_ATTACK))) {
                this.setMove(MOVES[1], GOLEM_SHIELD_ATTACK, Intent.ATTACK_DEFEND, this.golemShieldDamage);
            } else {
                this.setMove(MOVES[2], LOOSE_MATERIALS_ATTACK, Intent.ATTACK, this.looseMaterialsDamage, LOOSE_MATERIALS_HITS, true);
            }
        }
    }

    @Override
    public void die() {
        this.useFastShakeAnimation(5.0F);
        CardCrawlGame.screenShake.rumble(4.0F);
        this.deathTimer++;
        super.die();
        this.onBossVictoryLogic();
        if (!AbstractDungeon.player.hasRelic(ElementariumTrophy.ID)) {
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), new ElementariumTrophy());
        }
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!m.isDead && !m.isDying) {
                AbstractDungeon.actionManager.addToTop(new HideHealthBarAction(m));
                AbstractDungeon.actionManager.addToTop(new SuicideAction(m));
                AbstractDungeon.actionManager.addToTop(new VFXAction(m, new InflameEffect(m), 0.2F));
            }
        }
    }

    public static class GolemInfo {
        public GolemInfo(String golemID, float xPosition, int strength) {
            this.golemID = golemID;
            this.xPosition = xPosition;
            this.strength = strength;
        }

        public String golemID;
        public float xPosition;
        public int strength;
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = GolemEmperor.monsterStrings.NAME;
        MOVES = GolemEmperor.monsterStrings.MOVES;
    }
}