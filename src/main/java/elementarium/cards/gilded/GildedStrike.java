package elementarium.cards.gilded;

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
import com.megacrit.cardcrawl.powers.VulnerablePower;
import elementarium.Elementarium;
import elementarium.cards.CustomTags;

public class GildedStrike extends CustomCard {
    public static final String ID = "Elementarium:GildedStrike";
    public static final String IMG = Elementarium.cardImage(ID);
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 1;
    private static final int BASE_DAMAGE = 6;
    private static final int UPGRADE_DAMAGE = 3;
    private static final int VULNERABLE_AMOUNT = 1;
    private static final int UPGRADE_VULNERABLE = 1;

    public GildedStrike() {
        super(ID, NAME, IMG, COST, DESCRIPTION, CardType.ATTACK, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.ENEMY);
        this.baseDamage = BASE_DAMAGE;
        this.baseMagicNumber = VULNERABLE_AMOUNT;
        this.magicNumber = this.baseMagicNumber;
        this.tags.add(CardTags.STRIKE);
        this.tags.add(CustomTags.GILDED);
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        this.addToBot(new ApplyPowerAction(m, p, new VulnerablePower(m, this.magicNumber, false), this.magicNumber));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(UPGRADE_DAMAGE);
            this.upgradeMagicNumber(UPGRADE_VULNERABLE);
        }
    }

    public AbstractCard makeCopy() {
        return new GildedStrike();
    }
}
