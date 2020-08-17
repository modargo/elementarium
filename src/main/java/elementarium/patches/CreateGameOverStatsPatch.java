package elementarium.patches;

import actlikeit.savefields.BreadCrumbs;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.TheCity;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.screens.GameOverStat;
import com.megacrit.cardcrawl.screens.VictoryScreen;

import java.util.ArrayList;
import java.util.Map;

public class CreateGameOverStatsPatch {
    private static String EliteScoreStringKey = "City Elites Killed";
    private static String EliteName = CardCrawlGame.languagePack.getScoreString(EliteScoreStringKey).NAME;
    private static int ActNum = 2;
    private static String ActID = TheCity.ID;

    public static void RemoveScoreEntries(ArrayList<GameOverStat> stats) {
        int elitesSlain = CardCrawlGame.elites1Slain;
        String statLabel = EliteName + " (" + elitesSlain + ")";
        if (!Settings.isEndless && elitesSlain == 0) {
            Map<Integer, String> breadCrumbs = BreadCrumbs.getBreadCrumbs();
            if (breadCrumbs != null && breadCrumbs.containsKey(ActNum) && !breadCrumbs.get(ActNum).equals(ActID)) {
                stats.removeIf(stat -> stat != null && stat.label != null && stat.label.equals(statLabel));
            }
        }
    }

    @SpirePatch(
            clz = VictoryScreen.class,
            method = "createGameOverStats"
    )
    public static class VictoryScreenPatch {
        @SpirePostfixPatch
        public static void VictoryScreenPatch(VictoryScreen __instance) {
            RemoveScoreEntries(__instance.stats);
        }
    }

    @SpirePatch(
            clz = DeathScreen.class,
            method = "createGameOverStats"
    )
    public static class DeathScreenPatch {
        @SpirePostfixPatch
        public static void DeathScreenPatch(DeathScreen __instance) {
            RemoveScoreEntries(__instance.stats);
        }
    }
}
