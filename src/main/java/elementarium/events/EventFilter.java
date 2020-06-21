package elementarium.events;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.beyond.TombRedMask;
import elementarium.relics.ElementariumTrophy;

import java.util.ArrayList;

public class EventFilter {
    //Not supposed to be instantiated
    private EventFilter() {
        throw new AssertionError();
    }

    public static ArrayList<String> FilterEvents(ArrayList<String> events) {
        ArrayList<String> eventsToRemove = new ArrayList<>();
        for (String event : events) {
            if (event.equals(FireSanctum.ID)) {
                if (!(AbstractDungeon.currMapNode != null && AbstractDungeon.currMapNode.y > AbstractDungeon.map.size() / 2)) {
                    eventsToRemove.add(event);
                }
            }

            if (event.equals(BigGameHunter.ID)) {
                if (!(AbstractDungeon.currMapNode != null && AbstractDungeon.currMapNode.y <= AbstractDungeon.map.size() / 2)) {
                    eventsToRemove.add(event);
                }
            }

            if (event.equals(TombRedMask.ID)) {
                if (AbstractDungeon.player.hasRelic(ElementariumTrophy.ID)) {
                    eventsToRemove.add(event);
                }
            }

            if (event.equals(ChestOfTheGoldenMirage.ID)) {
                if (!AbstractDungeon.player.hasRelic(ElementariumTrophy.ID)) {
                    eventsToRemove.add(event);
                }
            }

            if (event.equals(BeastsOfTheMenagerie.ID)) {
                if (!(Loader.isModLoaded("Menagerie") && AbstractDungeon.currMapNode != null && AbstractDungeon.currMapNode.y <= AbstractDungeon.map.size() / 2)) {
                    eventsToRemove.add(event);
                }
            }
        }
        return eventsToRemove;
    }
}