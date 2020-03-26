package elementarium.cards.elementalblades;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;
import elementarium.Elementarium;
import elementarium.cards.CustomTags;

public class WindblessedBlade extends CustomCard {
    public static final String ID = "Elementarium:WindblessedBlade";
    public static final String IMG = Elementarium.cardImage(ID);
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 1;
    private static final int BASE_DAMAGE = 4;
    private static final int UPGRADE_DAMAGE = 1;
    private static final int HITS = 3;
    private static final int BASE_BLOCK = 4;
    private static final int BLOCK_TIMES = 2;
    private static final int UPGRADE_BLOCK = 2;
    private static final int BASE_WEAK = 2;
    private static final int UPGRADE_WEAK = 1;

    public WindblessedBlade() {
        super(ID, NAME, IMG, COST, DESCRIPTION, CardType.ATTACK, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.ENEMY);
        this.baseDamage = BASE_DAMAGE;
        this.baseBlock = BASE_BLOCK;
        this.baseMagicNumber = BASE_WEAK;
        this.magicNumber = this.baseMagicNumber;
        this.exhaust = true;
        this.tags.add(CustomTags.ELEMENTAL_BLADE);
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        for (int i = 0; i < HITS; i++) {
            this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        }
        for (int i = 0; i < BLOCK_TIMES; i++) {
            this.addToBot(new GainBlockAction(p, this.block));
        }
        this.addToBot(new ApplyPowerAction(m, p, new WeakPower(m, this.magicNumber, false), this.magicNumber));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(UPGRADE_DAMAGE);
            this.upgradeBlock(UPGRADE_BLOCK);
            this.upgradeMagicNumber(UPGRADE_WEAK);
        }
    }

    public AbstractCard makeCopy() {
        return new WindblessedBlade();
    }
}
