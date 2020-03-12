package elementarium.cards.elementalblades;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.*;
import elementarium.Elementarium;
import elementarium.cards.CustomTags;

public class VoidblessedBlade extends CustomCard {
    public static final String ID = "Elementarium:VoidblessedBlade";
    public static final String IMG = Elementarium.cardImage(ID);
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 1;
    private static final int BASE_DAMAGE = 6;
    private static final int BASE_HITS_STRENGTH = 2;
    private static final int UPGRADE_HITS_STRENGTH = 1;
    private static final int BLUR = 1;

    public VoidblessedBlade() {
        super(ID, NAME, IMG, COST, DESCRIPTION, CardType.ATTACK, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.ENEMY);
        this.baseDamage = BASE_DAMAGE;
        this.baseMagicNumber = BASE_HITS_STRENGTH;
        this.magicNumber = this.baseMagicNumber;
        this.exhaust = true;
        this.tags.add(CustomTags.ELEMENTAL_BLADE);
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        for (int i = 0; i < this.magicNumber; i++) {
            this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        }
        this.addToBot(new ApplyPowerAction(m, p, new StrengthPower(m, -this.magicNumber), -this.magicNumber));
        this.addToBot(new ApplyPowerAction(p, p, new BlurPower(p, BLUR), BLUR));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_HITS_STRENGTH);
        }
    }

    public AbstractCard makeCopy() {
        return new VoidblessedBlade();
    }
}
