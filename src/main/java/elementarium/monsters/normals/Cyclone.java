package elementarium.monsters.normals;

import basemod.abstracts.CustomMonster;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.FastShakeAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.DrawPower;
import com.megacrit.cardcrawl.powers.RitualPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import elementarium.Elementarium;
import elementarium.powers.FierceWindsPower;

import java.util.ArrayList;

public class Cyclone extends CustomMonster
{
    public static final String ID = "Elementarium:Cyclone";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    private static final String IMG = Elementarium.monsterImage(ID);
    private boolean firstMove = true;
    private static final byte BLOW_AWAY_MOVE = 1;
    private static final byte INVIGORATING_WINDS_MOVE = 2;
    private static final byte TWISTER_ATTACK = 3;
    private static final int INVIGORATING_WINDS_RITUAL = 2;
    private static final int A17_INVIGORATING_WINDS_RITUAL = 3;
    private static final int INVIGORATING_WINDS_DRAW = 2;
    private static final int A17_INVIGORATING_WINDS_DRAW = 2;
    private static final int TWISTER_DAMAGE = 13;
    private static final int A2_TWISTER_DAMAGE = 15;
    private static final int FIERCE_WINDS_AMOUNT = 1;
    private static final int A17_FIERCE_WINDS_AMOUNT = 1;
    private static final int HP_MIN = 112;
    private static final int HP_MAX = 118;
    private static final int A7_HP_MIN = 117;
    private static final int A7_HP_MAX = 123;
    private int invigoratingWindsRitual;
    private int invigoratingWindsDraw;
    private int twisterDamage;
    private int fierceWindsAmount;

    public Cyclone() {
        this(0.0f, 0.0f);
    }

    public Cyclone(final float x, final float y) {
        super(Cyclone.NAME, ID, HP_MAX, -5.0F, 0, 280.0f, 300.0f, IMG, x, y);
        this.type = EnemyType.NORMAL;
        if (AbstractDungeon.ascensionLevel >= 7) {
            this.setHp(A7_HP_MIN, A7_HP_MAX);
        } else {
            this.setHp(HP_MIN, HP_MAX);
        }

        if (AbstractDungeon.ascensionLevel >= 2) {
            this.twisterDamage = A2_TWISTER_DAMAGE;
        } else {
            this.twisterDamage = TWISTER_DAMAGE;
        }

        if (AbstractDungeon.ascensionLevel >= 17) {
            this.invigoratingWindsRitual = A17_INVIGORATING_WINDS_RITUAL;
            this.invigoratingWindsDraw = A17_INVIGORATING_WINDS_DRAW;
            this.fierceWindsAmount = A17_FIERCE_WINDS_AMOUNT;
        }
        else {
            this.invigoratingWindsRitual = INVIGORATING_WINDS_RITUAL;
            this.invigoratingWindsDraw = INVIGORATING_WINDS_DRAW;
            this.fierceWindsAmount = FIERCE_WINDS_AMOUNT;
        }

        this.damage.add(new DamageInfo(this, this.twisterDamage));
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new FierceWindsPower(AbstractDungeon.player, this.fierceWindsAmount), this.fierceWindsAmount));
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            this.firstMove = false;
        }
        switch (this.nextMove) {
            case BLOW_AWAY_MOVE:
                AbstractDungeon.actionManager.addToBottom(new FastShakeAction(this, 1.0F, 0.2F));
                this.exhaustCostCards(0);
                break;
            case INVIGORATING_WINDS_MOVE:
                AbstractDungeon.actionManager.addToBottom(new FastShakeAction(this, 0.5F, 0.2F));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new RitualPower(this, this.invigoratingWindsRitual, false), this.invigoratingWindsRitual));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, this.invigoratingWindsRitual), this.invigoratingWindsRitual));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new DrawPower(AbstractDungeon.player, this.invigoratingWindsDraw), this.invigoratingWindsDraw));
                break;
            case TWISTER_ATTACK:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_HEAVY));
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
            this.setMove(MOVES[0], BLOW_AWAY_MOVE, Intent.STRONG_DEBUFF);
        }
        else if (this.lastMove(BLOW_AWAY_MOVE)) {
            this.setMove(MOVES[1], INVIGORATING_WINDS_MOVE, Intent.BUFF);
        }
        else {
            this.setMove(MOVES[2], TWISTER_ATTACK, Intent.ATTACK, this.twisterDamage);
        }
    }

    private void exhaustCostCards(int cost) {
        ArrayList<CardToExhaust> cards = new ArrayList<>();
        this.addCardsToExhaust(cards, AbstractDungeon.player.drawPile, cost);
        this.addCardsToExhaust(cards, AbstractDungeon.player.discardPile, cost);
        this.addCardsToExhaust(cards, AbstractDungeon.player.hand, cost);

        for (CardToExhaust cte : cards) {
            this.addToBot(new ExhaustSpecificCardAction(cte.card, cte.group));
        }
        this.addToBot(new MakeTempCardInDiscardAction(new Wound(), cards.size()));
    }

    private void addCardsToExhaust(ArrayList<CardToExhaust> cards, CardGroup group, int cost) {
        for (AbstractCard c : group.group){
            if (c.cost == cost && !c.isCostModified && c.type != AbstractCard.CardType.CURSE && c.type != AbstractCard.CardType.STATUS) {
                CardToExhaust cte = new CardToExhaust();
                cte.card = c;
                cte.group = group;
                cards.add(cte);
            }
        }
    }

    private class CardToExhaust {
        public AbstractCard card;
        public CardGroup group;
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = Cyclone.monsterStrings.NAME;
        MOVES = Cyclone.monsterStrings.MOVES;
    }
}