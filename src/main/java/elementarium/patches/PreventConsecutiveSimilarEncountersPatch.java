package elementarium.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import elementarium.act.ElementariumAct;
import elementarium.act.Encounters;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpirePatch(clz = AbstractDungeon.class, method = "populateMonsterList")
public class PreventConsecutiveSimilarEncountersPatch {
    public static class PreventConsecutiveSimilarEncountersExprEditor extends ExprEditor {
        @Override
        public void edit(MethodCall methodCall) throws CannotCompileException {
            if (methodCall.getClassName().equals(ArrayList.class.getName()) && methodCall.getMethodName().equals("add")) {
                methodCall.replace(String.format("{ if (%1$s.shouldAdd($0, (String)$1)) { $_ = $proceed($$); } else { i--; $_ = true; } }", PreventConsecutiveSimilarEncountersPatch.class.getName()));
            }
        }
    }

    @SpireInstrumentPatch
    public static ExprEditor preventConsecutiveSimilarEncounters() {
        return new PreventConsecutiveSimilarEncountersExprEditor();
    }

    public static boolean shouldAdd(ArrayList<String> list, String toAdd) {
        if (!AbstractDungeon.id.equals(ElementariumAct.ID) || list != AbstractDungeon.monsterList) {
            return true;
        }
        return !lastEncounterSimilar(0, toAdd) && !lastEncounterSimilar(1, toAdd);
    }

    private static boolean lastEncounterSimilar(int previous, String s) {
        return AbstractDungeon.monsterList.size() > previous && similarEncounters(s, AbstractDungeon.monsterList.get(AbstractDungeon.monsterList.size() - (previous + 1)));
    }

    private static boolean similarEncounters(String s1, String s2) {
        List<String> cyclones = Arrays.asList(Encounters.CYCLONE_AND_LIVING_STORMCLOUD, Encounters.CYCLONE_AND_ORB_OF_FIRE);
        List<String> voids = Arrays.asList(Encounters.VOID_CORRUPTION_AND_TAR_GOLEM, Encounters.VOID_CORRUPTION_AND_ORB_OF_FIRE);
        return (cyclones.contains(s1) && cyclones.contains(s2)) || (voids.contains(s1) && voids.contains(s2));
    }
}
