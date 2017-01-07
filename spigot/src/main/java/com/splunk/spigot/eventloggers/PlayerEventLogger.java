package com.splunk.spigot.eventloggers;


import com.splunk.sharedmc.logger.AbstractEventLogger;
import com.splunk.sharedmc.logger.actions.PlayerEventAction;
import com.splunk.sharedmc.logger.events.LoggablePlayerEvent;
import com.splunk.sharedmc.logger.entities.LoggableLivingEntity;
import com.splunk.sharedmc.logger.utilities.Point3d;

import com.splunk.spigot.utilities.EntityUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;

import java.util.Properties;

public class PlayerEventLogger extends AbstractEventLogger implements Listener {


    public PlayerEventLogger(Properties properties) {
        super(properties);
    }

    @EventHandler
    public void captureLoginEvent(PlayerLoginEvent event) {
        logAndSend(getLoggablePlayerEvent(PlayerEventAction.PLAYER_LOGIN, event));

    }

    @EventHandler
    public void captureLogoutEvent(PlayerKickEvent event) {
        logAndSend(getLoggablePlayerEvent(PlayerEventAction.PLAYER_LOGOUT, event));
    }

    @EventHandler
    public void captureLogoutEvent(PlayerQuitEvent event) {
        logAndSend(getLoggablePlayerEvent(PlayerEventAction.PLAYER_LOGOUT, event));
    }

    @EventHandler
    public void captureTeleportEvent(PlayerTeleportEvent event) {
        logAndSend(getLoggablePlayerEvent(PlayerEventAction.TELEPORT, event));
    }

    @EventHandler
    public void captureMoveEvent(PlayerMoveEvent event) {
        logAndSend(getLoggablePlayerEvent(PlayerEventAction.MOVE, event));
    }


    private LoggablePlayerEvent getLoggablePlayerEvent(PlayerEventAction action, PlayerEvent event) {

        final Player player = event.getPlayer();
        final Location location = player.getLocation();
        final World world = player.getWorld();

        Point3d coordinates = new Point3d(location.getX(), location.getY(), location.getZ());

        LoggablePlayerEvent playerEvent = new LoggablePlayerEvent(world.getFullTime(), minecraft_server, world.getName(), action);

        LoggableLivingEntity eventPlayer = (LoggableLivingEntity)EntityUtil.getLoggableEntity(player);

        playerEvent.setPlayer(eventPlayer);

        if (event instanceof PlayerMoveEvent) {

            // Teleport is a child class so this is to allow us to identify the child class
            if (event instanceof PlayerTeleportEvent)
                playerEvent.setAction("teleport"); // Should use the action enum instead of the string.

            // The coordinates from the event and the destination of the move event are slightly different so this corrects that.
            Point3d destination = new Point3d(((PlayerMoveEvent) event).getTo().getX(), ((PlayerMoveEvent) event).getTo().getY(), ((PlayerMoveEvent) event).getTo().getZ());
            playerEvent.setDest(destination);

            Point3d source = new Point3d(((PlayerMoveEvent) event).getFrom().getX(), ((PlayerMoveEvent) event).getFrom().getY(), ((PlayerMoveEvent) event).getFrom().getZ());
            playerEvent.setSrc(source);


        }
        return playerEvent;

    }
}
