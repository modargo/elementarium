package elementarium.cards.gilded;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DexterityPower;
import elementarium.Elementarium;
import elementarium.cards.CustomTags;

public class GildedDefend extends CustomCard {
    public static final String ID = "Elementarium:GildedDefend";
    public static final String IMG = Elementarium.cardImage(ID);
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 1;
    private static final int BASE_BLOCK = 7;
    private static final int UPGRADE_BLOCK = 3;
    private static final int DEXTERITY_AMOUNT = 1;

    public GildedDefend() {
        super(ID, NAME, IMG, COST, DESCRIPTION, CardType.SKILL, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.SELF);
        this.baseBlock = BASE_BLOCK;
        this.baseMagicNumber = DEXTERITY_AMOUNT;
        this.tags.add(CustomTags.GILDED);
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new GainBlockAction(p, p, this.block));
        this.addToBot(new ApplyPowerAction(p, p, new DexterityPower(p, -this.magicNumber), -this.magicNumber));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBlock(UPGRADE_BLOCK);
        }
    }

    public AbstractCard makeCopy() {
        return new GildedDefend();
    }
}
