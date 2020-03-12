package elementarium.subscribers;

import basemod.interfaces.PostBattleSubscriber;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import elementarium.cards.tar.StickyTar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RemoveStickyTarPostEliteBattleSubscriber implements PostBattleSubscriber {
    public static final Logger logger = LogManager.getLogger(RemoveStickyTarPostEliteBattleSubscriber.class.getName());

    @Override
    public void receivePostBattle(AbstractRoom abstractRoom) {
        logger.info("Checking if the combat was an victorious elite combat");
        boolean isElite = abstractRoom.eliteTrigger || (abstractRoom.monsters != null
               && abstractRoom.monsters.monsters != null
               && abstractRoom.monsters.areMonstersDead() //No credit if you smoke bomb out
               && abstractRoom.monsters.monsters.stream().anyMatch(m -> m.type == AbstractMonster.EnemyType.ELITE));
        if (isElite) {
            logger.info("Checking for Sticky Tars to remove");
            AbstractCard stickyTarCard = AbstractDungeon.player.masterDeck.findCardById(StickyTar.ID);
            if (stickyTarCard != null ){
                logger.info("Found Sticky Tar, removing");
                AbstractDungeon.effectList.add(new PurgeCardEffect(stickyTarCard));
                AbstractDungeon.player.masterDeck.removeCard(stickyTarCard);
            }
        }
    }
}
