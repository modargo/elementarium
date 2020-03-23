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

public class IceblessedBlade extends CustomCard {
    public static final String ID = "Elementarium:IceblessedBlade";
    public static final String IMG = Elementarium.cardImage(ID);
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 1;
    private static final int BASE_DAMAGE = 9;
    private static final int UPGRADE_DAMAGE = 6;
    private static final int BASE_BENEFITS = 1;
    private static final int UPGRADE_BENEFITS = 1;

    public IceblessedBlade() {
        super(ID, NAME, IMG, COST, DESCRIPTION, CardType.ATTACK, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.ENEMY);
        this.baseDamage = BASE_DAMAGE;
        this.baseMagicNumber = BASE_BENEFITS;
        this.magicNumber = this.baseMagicNumber;
        this.exhaust = true;
        this.tags.add(CustomTags.ELEMENTAL_BLADE);
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        this.addToBot(new ApplyPowerAction(p, p, new ArtifactPower(p, this.magicNumber), this.magicNumber));
        this.addToBot(new ApplyPowerAction(p, p, new BufferPower(p, 1), 1));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(UPGRADE_DAMAGE);
            this.upgradeMagicNumber(UPGRADE_BENEFITS);
        }
    }

    public AbstractCard makeCopy() {
        return new IceblessedBlade();
    }
}
