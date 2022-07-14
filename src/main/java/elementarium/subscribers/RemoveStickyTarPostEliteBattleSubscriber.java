package elementarium.subscribers;

import basemod.interfaces.PostBattleSubscriber;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import elementarium.cards.tar.StickyTar;

public class RemoveStickyTarPostEliteBattleSubscriber implements PostBattleSubscriber {
    @Override
    public void receivePostBattle(AbstractRoom abstractRoom) {
        boolean isElite = abstractRoom.eliteTrigger || (abstractRoom.monsters != null
               && abstractRoom.monsters.monsters != null
               && abstractRoom.monsters.areMonstersDead() //No credit if you smoke bomb out
               && abstractRoom.monsters.monsters.stream().anyMatch(m -> m.type == AbstractMonster.EnemyType.ELITE));
        if (isElite) {
            AbstractCard stickyTarCard = AbstractDungeon.player.masterDeck.findCardById(StickyTar.ID);
            if (stickyTarCard != null ){
                AbstractDungeon.effectList.add(new PurgeCardEffect(stickyTarCard));
                AbstractDungeon.player.masterDeck.removeCard(stickyTarCard);
            }
        }
    }
}
