package elementarium.events;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
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
        }
        return eventsToRemove;
    }
}