package elementarium.act;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.MonsterInfo;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.scenes.AbstractScene;
import com.megacrit.cardcrawl.scenes.TheCityScene;
import elementarium.monsters.normals.Cyclone;
import elementarium.monsters.normals.RubyGolem;
import elementarium.monsters.elites.ElementalPortal;
import elementarium.monsters.normals.VoidBeast;
import elementarium.monsters.normals.VoidCorruption;
import elementarium.monsters.elites.WarGolem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class ElementariumAct extends CustomDungeon {
    public static final String ID = "Elementarium:Elementarium";
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] TEXT = uiStrings.TEXT;
    public static final String NAME = TEXT[0];
    private static final Logger logger = LogManager.getLogger(ElementariumAct.class.getName());

    public ElementariumAct() {
        super(NAME, ID, "images/ui/event/panel.png", false, 2, 12, 10);
        logger.info("ElementariumAct constructor");
    }

    public ElementariumAct(CustomDungeon cd, AbstractPlayer p, ArrayList<String> emptyList) {
        super(cd, p, emptyList);
    }
    public ElementariumAct(CustomDungeon cd, AbstractPlayer p, SaveFile sf) {
        super(cd, p, sf);
    }

    @Override
    public AbstractScene DungeonScene() {
        logger.info("ElementariumAct DungeonScene");
        return new TheCityScene();
    }

    @Override
    public String getBodyText() {
        return TEXT[2];
    }

    @Override
    public String getOptionText() {
        return TEXT[3];
    }

    @Override
    protected void initializeLevelSpecificChances() {
        //These are all deliberately the same as The City
        shopRoomChance = 0.05F;
        restRoomChance = 0.12F;
        treasureRoomChance = 0.0F;
        eventRoomChance = 0.22F;
        eliteRoomChance = 0.08F;
        smallChestChance = 50;
        mediumChestChance = 33;
        largeChestChance = 17;
        commonRelicChance = 50;
        uncommonRelicChance = 33;
        rareRelicChance = 17;
        colorlessRareChance = 0.3F;
        if (AbstractDungeon.ascensionLevel >= 12) {
            cardUpgradedChance = 0.125F;
        } else {
            cardUpgradedChance = 0.25F;
        }
    }

    @Override
    protected void generateMonsters() {
        generateWeakEnemies(weakpreset);
        generateStrongEnemies(strongpreset);
        generateElites(elitepreset);
    }

    @Override
    protected void generateWeakEnemies(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList<>();
        monsters.add(new MonsterInfo(Encounters.ELEMENTALS_2, 40.0F));
        monsters.add(new MonsterInfo(VoidCorruption.ID, 20.0F));
        monsters.add(new MonsterInfo(Cyclone.ID, 20.0F));
        monsters.add(new MonsterInfo(Encounters.MUD_AND_TAR_GOLEMS, 20.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateMonsterList(monsters, count, false);
    }

    @Override
    protected void generateStrongEnemies(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList<>();
        monsters.add(new MonsterInfo(Encounters.ELEMENTALS_3, 30.0F));
        monsters.add(new MonsterInfo(Encounters.STONE_GOLEMS, 14.0F));
        monsters.add(new MonsterInfo(RubyGolem.ID, 14.0F));
        monsters.add(new MonsterInfo(VoidBeast.ID, 14.0F));
        monsters.add(new MonsterInfo(Encounters.VOID_CORRUPTION_AND_ORB_OF_FIRE, 7.0F));
        monsters.add(new MonsterInfo(Encounters.VOID_CORRUPTION_AND_TAR_GOLEM, 7.0F));
        monsters.add(new MonsterInfo(Encounters.CYCLONE_AND_LIVING_STORMCLOUD, 7.0F));
        monsters.add(new MonsterInfo(Encounters.CYCLONE_AND_ORB_OF_FIRE, 7.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateFirstStrongEnemy(monsters, this.generateExclusions());
        this.populateMonsterList(monsters, count, false);
    }

    @Override
    protected void generateElites(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList<>();
        monsters.add(new MonsterInfo(WarGolem.ID, 1.0F));
        monsters.add(new MonsterInfo(Encounters.FIRE_NOBILITY, 1.0F));
        monsters.add(new MonsterInfo(ElementalPortal.ID, 1.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateMonsterList(monsters, count, true);
    }

    @Override
    protected ArrayList<String> generateExclusions() {
        ArrayList<String> retVal = new ArrayList<>();
        switch (monsterList.get(monsterList.size() - 1))
        {
            case Encounters.ELEMENTALS_2:
                retVal.add(Encounters.ELEMENTALS_3);
                retVal.add(Encounters.VOID_CORRUPTION_AND_ORB_OF_FIRE);
                retVal.add(Encounters.CYCLONE_AND_LIVING_STORMCLOUD);
                retVal.add(Encounters.CYCLONE_AND_ORB_OF_FIRE);
                break;
            case VoidCorruption.ID:
                retVal.add(Encounters.VOID_CORRUPTION_AND_ORB_OF_FIRE);
                retVal.add(Encounters.VOID_CORRUPTION_AND_TAR_GOLEM);
                break;
            case Cyclone.ID:
                retVal.add(Encounters.CYCLONE_AND_LIVING_STORMCLOUD);
                retVal.add(Encounters.CYCLONE_AND_ORB_OF_FIRE);
                break;
            case Encounters.MUD_AND_TAR_GOLEMS:
                retVal.add(Encounters.VOID_CORRUPTION_AND_TAR_GOLEM);
                break;
        }

        return retVal;
    }

    @Override
    protected void initializeShrineList() {
        shrineList.clear();
        //shrineList.add(GoldShrine.ID);
        //shrineList.add(Transmogrifier.ID);
        //shrineList.add(PurificationShrine.ID);
        //shrineList.add(UpgradeShrine.ID);
        //shrineList.add(Bonfire.ID);
        //shrineList.add(Duplicator.ID);
        //shrineList.add(FaceTrader.ID);
        //shrineList.add(FountainOfCurseRemoval.ID);
        //shrineList.add(Designer.ID);
    }

    @Override
    protected void initializeEventList() {
        // Events are added via BaseMod in Elementarium.addEvents()
        // We clear the one time events in addition to the shrines because we want only Elementarium-specific events
        specialOneTimeEventList.clear();
    }
}
