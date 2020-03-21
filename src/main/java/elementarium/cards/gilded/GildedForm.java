package elementarium.cards.gilded;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainGoldAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import elementarium.Elementarium;
import elementarium.cards.CustomTags;

public class GildedForm extends CustomCard {
    public static final String ID = "Elementarium:GildedForm";
    public static final String IMG = Elementarium.cardImage(ID);
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    private static final int COST = 3;
    private static final int BASE_MAGIC_NUMBER = 6;
    private static final int UPGRADE_MAGIC_NUMBER = 2;
    private static final int GOLD_GAIN = 20;
    private static final int DEXTERITY_LOSS = 1;

    public GildedForm() {
        super(ID, NAME, IMG, COST, DESCRIPTION, CardType.POWER, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.SELF);
        this.baseMagicNumber = BASE_MAGIC_NUMBER;
        this.magicNumber = BASE_MAGIC_NUMBER;
        this.isEthereal = true;
        this.tags.add(CustomTags.GILDED);
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new ApplyPowerAction(p, p, new PlatedArmorPower(p, this.magicNumber), this.magicNumber));
        this.addToBot(new ApplyPowerAction(p, p, new DexterityPower(p, -DEXTERITY_LOSS), -DEXTERITY_LOSS));
        AbstractDungeon.effectList.add(new RainingGoldEffect(this.magicNumber * 2, true));
        this.addToBot(new GainGoldAction(GOLD_GAIN));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(UPGRADE_MAGIC_NUMBER);
        }
    }

    public AbstractCard makeCopy() {
        return new GildedForm();
    }
}
