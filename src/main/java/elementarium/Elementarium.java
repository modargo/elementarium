package elementarium;

import actlikeit.dungeons.CustomDungeon;
import basemod.BaseMod;
import basemod.ModPanel;
import basemod.helpers.RelicType;
import basemod.interfaces.EditCardsSubscriber;
import basemod.interfaces.EditRelicsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.TheBeyond;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.AbstractPower;
import elementarium.act.ElementariumAct;
import elementarium.act.Encounters;
import elementarium.cards.elementalblades.*;
import elementarium.cards.gilded.GildedDefend;
import elementarium.cards.gilded.GildedEssence;
import elementarium.cards.gilded.GildedForm;
import elementarium.cards.gilded.GildedStrike;
import elementarium.cards.tar.StickyTar;
import elementarium.cards.tar.Tar;
import elementarium.events.*;
import elementarium.monsters.*;
import elementarium.monsters.bosses.FirePhoenix;
import elementarium.monsters.bosses.GoldenDragon;
import elementarium.monsters.bosses.GolemEmperor;
import elementarium.monsters.bosses.IcePhoenix;
import elementarium.monsters.elites.ElementalPortal;
import elementarium.monsters.elites.Firelord;
import elementarium.monsters.elites.FlameHerald;
import elementarium.monsters.elites.WarGolem;
import elementarium.monsters.normals.*;
import elementarium.monsters.specials.GoldenEagle;
import elementarium.monsters.specials.GoldenLion;
import elementarium.monsters.specials.GoldenAngel;
import elementarium.relics.*;
import elementarium.subscribers.RemoveStickyTarPostEliteBattleSubscriber;
import elementarium.util.TextureLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;

import static com.megacrit.cardcrawl.core.Settings.*;

@SpireInitializer
public class Elementarium implements
        PostInitializeSubscriber,
        EditCardsSubscriber,
        EditRelicsSubscriber,
        EditStringsSubscriber {
    private static final float X1 = -350.0F;
    private static final float X2 = 0.0F;
    
    public static final Logger logger = LogManager.getLogger(Elementarium.class.getName());

    public Elementarium() {
        BaseMod.subscribe(this);
    }

    public static void initialize() {
        new Elementarium();
    }

    @Override
    public void receivePostInitialize() {
        Texture badgeTexture = new Texture("elementarium/images/ElementariumBadge.png");
        BaseMod.registerModBadge(badgeTexture, "Elementarium", "modargo", "An alternate act 2 full of elementals and golems", new ModPanel());

        CustomDungeon.addAct(ElementariumAct.ACT_NUM, new ElementariumAct());
        addMonsters();
        addEvents();
        addRelics();

        BaseMod.subscribe(new RemoveStickyTarPostEliteBattleSubscriber());
    }

    private static void addMonsters() {
        //Weak encounters
        BaseMod.addMonster(Encounters.ELEMENTALS_2, () -> new MonsterGroup(generateElementalGroup(2)));
        BaseMod.addMonster(VoidCorruption.ID, (BaseMod.GetMonster)VoidCorruption::new);
        BaseMod.addMonster(Cyclone.ID, (BaseMod.GetMonster)Cyclone::new);
        BaseMod.addMonster(Encounters.MUD_AND_TAR_GOLEMS, () -> new MonsterGroup(
                new AbstractMonster[] {
                        new MudGolem(X1, 0.0F),
                        new TarGolem(X2, 0.0F)
                }));

        //Hard encounters
        BaseMod.addMonster(Encounters.ELEMENTALS_3, () -> new MonsterGroup(generateElementalGroup(3)));
        BaseMod.addMonster(RubyGolem.ID, (BaseMod.GetMonster)RubyGolem::new);
        BaseMod.addMonster(VoidBeast.ID, (BaseMod.GetMonster)VoidBeast::new);
        BaseMod.addMonster(Hydrostalker.ID, (BaseMod.GetMonster)Hydrostalker::new);
        BaseMod.addMonster(Encounters.STONE_GOLEMS, () -> new MonsterGroup(
                new AbstractMonster[] {
                        new StoneGolem(X1, 0.0F, false, false),
                        new StoneGolem(X2, 0.0F, false, false)
                }));
        BaseMod.addMonster(Encounters.VOID_CORRUPTION_AND_TAR_GOLEM, () -> new MonsterGroup(
                new AbstractMonster[] {
                        new TarGolem(X1, 0.0F),
                        new VoidCorruption(X2, 0.0F)
                }));
        BaseMod.addMonster(Encounters.VOID_CORRUPTION_AND_ORB_OF_FIRE, () -> new MonsterGroup(
                new AbstractMonster[] {
                        new OrbOfFire(X1, 125.0F),
                        new VoidCorruption(X2, 0.0F)
                }));
        BaseMod.addMonster(Encounters.CYCLONE_AND_LIVING_STORMCLOUD, () -> new MonsterGroup(
                new AbstractMonster[] {
                        new LivingStormcloud(X1, 125.0F),
                        new Cyclone(X2, 0.0F)
                }));
        BaseMod.addMonster(Encounters.CYCLONE_AND_ORB_OF_FIRE, () -> new MonsterGroup(
                new AbstractMonster[] {
                        new OrbOfFire(X1, 125.0F),
                        new Cyclone(X2, 0.0F)
                }));

        //Elites
        BaseMod.addMonster(WarGolem.ID, (BaseMod.GetMonster)WarGolem::new);
        BaseMod.addMonster(Firelord.ID, (BaseMod.GetMonster)Firelord::new);
        BaseMod.addMonster(FlameHerald.ID, (BaseMod.GetMonster)FlameHerald::new);
        BaseMod.addMonster(Encounters.FIRE_NOBILITY, () -> new MonsterGroup(
                new AbstractMonster[] {
                        new Firelord(-200.0F, 0.0F),
                        new FlameHerald(100.0F, 0.0F)
                }));
        BaseMod.addMonster(ElementalPortal.ID, () -> new ElementalPortal(150.0F, 0.0F));

        //Bosses
        BaseMod.addMonster(GolemEmperor.ID, () -> new GolemEmperor(150.0F, 0.0F));
        BaseMod.addBoss(ElementariumAct.ID, GolemEmperor.ID, "elementarium/images/map/bosses/GolemEmperor.png", "elementarium/images/map/bosses/GolemEmperorOutline.png");
        BaseMod.addMonster(Encounters.PHOENIXES, () -> new MonsterGroup(
                new AbstractMonster[] {
                        new FirePhoenix(X1, 125.0F),
                        new IcePhoenix(X2, 125.0F)
                }));
        BaseMod.addBoss(ElementariumAct.ID, Encounters.PHOENIXES, "elementarium/images/map/bosses/Phoenixes2.png", "elementarium/images/map/bosses/PhoenixesOutline.png");
        BaseMod.addMonster(GoldenDragon.ID, () -> new GoldenDragon(0.0F, 0.0F));
        BaseMod.addBoss(ElementariumAct.ID, GoldenDragon.ID, "elementarium/images/map/bosses/GoldenDragon.png", "elementarium/images/map/bosses/GoldenDragon.png");

        //Special fights
        BaseMod.addMonster(Encounters.FIRE_SANCTUM_HERALD, () -> new MonsterGroup(
                new AbstractMonster[] {
                        new OpulentOffering(X1, 125.0F),
                        new FlameHerald(X2, 0.0F)
                }));
        BaseMod.addMonster(Encounters.FIRE_SANCTUM_FIRELORD, () -> new MonsterGroup(
                new AbstractMonster[] {
                        new OrbOfFire(-500.0F, 125.0F),
                        new Firelord(-200.0F, 0.0F, true),
                        new OrbOfFire(100.0F, 125.0F)
                }));
        BaseMod.addMonster(Encounters.GOLDEN_STATUES, () -> new MonsterGroup(
                new AbstractMonster[] {
                        new GoldenEagle(-500.0F, 0.0F),
                        new GoldenLion(-200.0F, 0.0F),
                        new GoldenAngel(100.0F, 0.0F),
                }));
    }

    private static AbstractMonster[] generateElementalGroup(int groupSize) {
        if (groupSize != 2 && groupSize != 3) {
            logger.warn("Generated elemental group with groupSize other than two or three. groupSize: " + groupSize);
            groupSize = 3; //default to 3
        }
        float[] groupPositionsSize2 = {X1, X2};
        float[] groupPositionsSize3 = {-450.0F, -200.0F, 50.0F};
        ArrayList<String> monstersList = new ArrayList<>();
        monstersList.add(OrbOfFire.ID);
        monstersList.add(LivingStormcloud.ID);
        monstersList.add(OpulentOffering.ID);
        monstersList.add(ShimmeringMirage.ID);
        Collections.shuffle(monstersList, AbstractDungeon.monsterRng.random);

        float[] groupToUse;
        AbstractMonster[] monsters = new AbstractMonster[groupSize];
        if (groupSize == 2) {
            groupToUse = groupPositionsSize2;
        } else {
            groupToUse = groupPositionsSize3;
        }
        for (int i = 0; i < groupSize; i++) {
            monsters[i] = MonsterUtils.getElemental(monstersList.get(i), groupToUse[i], 125.0F);
        }

        return monsters;
    }

    private static void addEvents() {
        BaseMod.addEvent(OnrushingOffering.ID, OnrushingOffering.class, ElementariumAct.ID);
        BaseMod.addEvent(FountainOfGold.ID, FountainOfGold.class, ElementariumAct.ID);
        BaseMod.addEvent(GreatTreasure.ID, GreatTreasure.class, ElementariumAct.ID);
        BaseMod.addEvent(TarCoveredCasket.ID, TarCoveredCasket.class, ElementariumAct.ID);
        BaseMod.addEvent(RadiantAltar.ID, RadiantAltar.class, ElementariumAct.ID);
        BaseMod.addEvent(FireSanctum.ID, FireSanctum.class, ElementariumAct.ID);
        BaseMod.addEvent(ElementalBlades.ID, ElementalBlades.class, ElementariumAct.ID);
        BaseMod.addEvent(StickySituation.ID, StickySituation.class, ElementariumAct.ID);
        BaseMod.addEvent(BigGameHunter.ID, BigGameHunter.class, ElementariumAct.ID);
        BaseMod.addEvent(LostScrolls.ID, LostScrolls.class, ElementariumAct.ID);
        BaseMod.addEvent(ShatteredPortal.ID, ShatteredPortal.class, ElementariumAct.ID);
        BaseMod.addEvent(AbandonedFactory.ID, AbandonedFactory.class, ElementariumAct.ID);
        BaseMod.addEvent(GoldenStatues.ID, GoldenStatues.class, ElementariumAct.ID);
        BaseMod.addEvent(IncubationChamber.ID, IncubationChamber.class, ElementariumAct.ID);
        BaseMod.addEvent(SculptureGarden.ID, SculptureGarden.class, ElementariumAct.ID);
        BaseMod.addEvent(BladeSeller.ID, BladeSeller.class, ElementariumAct.ID);
        BaseMod.addEvent(HeartOfTheVoid.ID, HeartOfTheVoid.class, ElementariumAct.ID);
        BaseMod.addEvent(VoidShrine.ID, VoidShrine.class, ElementariumAct.ID);
        BaseMod.addEvent(OtherworldlyPassage.ID, OtherworldlyPassage.class, ElementariumAct.ID);

        // Events in other acts
        BaseMod.addEvent(ChestOfTheGoldenMirage.ID, ChestOfTheGoldenMirage.class, TheBeyond.ID);
    }

    private static void addRelics() {
        BaseMod.addRelic(new GolemsHeart(), RelicType.SHARED);
        BaseMod.addRelic(new HatchlingPhoenix(), RelicType.SHARED);
        BaseMod.addRelic(new HuntersBracer(), RelicType.SHARED);
        BaseMod.addRelic(new HuntersElixir(), RelicType.SHARED);
        BaseMod.addRelic(new HuntersSling(), RelicType.SHARED);
        BaseMod.addRelic(new GoldenMirage(), RelicType.SHARED);
        BaseMod.addRelic(new RadiantIdol(), RelicType.SHARED);
        BaseMod.addRelic(new FlickeringLantern(), RelicType.SHARED);
        BaseMod.addRelic(new ElementariumTrophy(), RelicType.SHARED);
    }

    @Override
    public void receiveEditCards() {
        BaseMod.addCard(new EarthblessedBlade());
        BaseMod.addCard(new FireblessedBlade());
        BaseMod.addCard(new IceblessedBlade());
        BaseMod.addCard(new VoidblessedBlade());
        BaseMod.addCard(new WindblessedBlade());
        BaseMod.addCard(new GildedDefend());
        BaseMod.addCard(new GildedEssence());
        BaseMod.addCard(new GildedForm());
        BaseMod.addCard(new GildedStrike());
        BaseMod.addCard(new StickyTar());
        BaseMod.addCard(new Tar());
    }

    @Override
    public void receiveEditRelics() {
    }

    private static String makeLocPath(Settings.GameLanguage language, String filename)
    {
        String ret = "localization/";
        switch (language) {
            default:
                ret += "eng";
                break;
        }
        return "elementarium/" + ret + "/" + filename + ".json";
    }

    private void loadLocFiles(GameLanguage language)
    {
        BaseMod.loadCustomStringsFile(CardStrings.class, makeLocPath(language, "Elementarium-Card-Strings"));
        BaseMod.loadCustomStringsFile(EventStrings.class, makeLocPath(language, "Elementarium-Event-Strings"));
        BaseMod.loadCustomStringsFile(MonsterStrings.class, makeLocPath(language, "Elementarium-Monster-Strings"));
        BaseMod.loadCustomStringsFile(RelicStrings.class, makeLocPath(language, "Elementarium-Relic-Strings"));
        BaseMod.loadCustomStringsFile(PowerStrings.class, makeLocPath(language, "Elementarium-Power-Strings"));
        BaseMod.loadCustomStringsFile(UIStrings.class, makeLocPath(language, "Elementarium-ui"));
    }

    @Override
    public void receiveEditStrings()
    {
        loadLocFiles(GameLanguage.ENG);
        if (language != GameLanguage.ENG) {
            loadLocFiles(language);
        }
    }

    public static String cardImage(String id) {
        return "elementarium/images/cards/" + removeModId(id) + ".png";
    }
    public static String eventImage(String id) {
        return "elementarium/images/events/" + removeModId(id) + ".png";
    }
    public static String relicImage(String id) {
        return "elementarium/images/relics/" + removeModId(id) + ".png";
    }
    public static String powerImage32(String id) {
        return "elementarium/images/powers/" + removeModId(id) + "32.png";
    }
    public static String powerImage84(String id) {
        return "elementarium/images/powers/" + removeModId(id) + "84.png";
    }
    public static String monsterImage(String id) {
        return "elementarium/images/monsters/" + removeModId(id) + "/" + removeModId(id) + ".png";
    }
    public static String relicOutlineImage(String id) {
        return "elementarium/images/relics/outline/" + removeModId(id) + ".png";
    }

    public static String removeModId(String id) {
        if (id.startsWith("Elementarium:")) {
            return id.substring(id.indexOf(':') + 1);
        } else {
            logger.warn("Missing mod id on: " + id);
            return id;
        }
    }

    public static void LoadPowerImage(AbstractPower power) {
        Texture tex84 = TextureLoader.getTexture(Elementarium.powerImage84(power.ID));
        Texture tex32 = TextureLoader.getTexture(Elementarium.powerImage32(power.ID));
        power.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        power.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);
    }

}