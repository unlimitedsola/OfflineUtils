package love.sola.offlineutils.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import static net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

@EventBusSubscriber
public class PlayerHandler {

    private static final String WELCOME = TextFormatting.RED.toString() + "Type /login <password> to login or /register <password> <repeat> to register.";

    public static final Map<UUID, PlayerPos> unauthenticatedPlayers = new HashMap<>();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onJoin(PlayerLoggedInEvent event) {
        EntityPlayer entity = event.player;
        // avoid kick
        entity.capabilities.allowFlying = true;
        BlockPos pos = entity.getPosition();
        float yaw = entity.rotationYaw, pitch = entity.rotationPitch;
        PlayerPos pp = new PlayerPos(pos, yaw, pitch);
        unauthenticatedPlayers.put(entity.getGameProfile().getId(), pp);
        entity.addChatMessage(new TextComponentString(WELCOME));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLeave(PlayerLoggedOutEvent event) {
        unauthenticatedPlayers.remove(event.player.getGameProfile().getId());
    }

    @SubscribeEvent
    public static void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
        final UUID id = event.player.getGameProfile().getId();
        if (unauthenticatedPlayers.containsKey(id)) {
            PlayerPos pp = unauthenticatedPlayers.get(id);
            ((EntityPlayerMP) event.player).connection.setPlayerLocation(pp.pos.getX(), pp.pos.getY(), pp.pos.getZ(), pp.yaw, pp.pitch);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerEvent(PlayerEvent event) {
        EntityPlayer entity = event.player;
        if (event.isCancelable() && !isPlayerAuthenticated(entity)) {
            event.setCanceled(true);
            entity.addChatMessage(new TextComponentString(WELCOME));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onCommand(CommandEvent event) {
        String name = event.getCommand().getCommandName();
        if (event.isCancelable()
                && event.getSender() instanceof EntityPlayer
                && !(name.equals("register") || name.equals("login"))
                && !isPlayerAuthenticated((EntityPlayer) event.getSender())
        ) {
            event.setCanceled(true);
            event.getSender().addChatMessage(new TextComponentString(WELCOME));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onChatEvent(ServerChatEvent event) {
        EntityPlayerMP entity = event.getPlayer();
        if (event.isCancelable() && !isPlayerAuthenticated(entity)) {
            event.setCanceled(true);
            entity.addChatMessage(new TextComponentString(WELCOME));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onTossEvent(ItemTossEvent event) {
        EntityPlayer entity = event.getPlayer();
        if (event.isCancelable() && !isPlayerAuthenticated(entity)) {
            event.setCanceled(true);
            entity.inventory.addItemStackToInventory(event.getEntityItem().getEntityItem());
            entity.addChatMessage(new TextComponentString(WELCOME));
        }
    }

    /*
    This is the list of the different LivingEvents we want to block
    We cannot block every single LivingEvent because of LivingUpdateEvent (defined in LivingEvent)
     */
    private static void handleLivingEvents(LivingEvent event, Entity entity) {
        if (entity instanceof EntityPlayer && event.isCancelable() && !isPlayerAuthenticated((EntityPlayer) entity)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingAttackEvent(LivingAttackEvent event) {
        handleLivingEvents(event, event.getEntity());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingDeathEvent(LivingDeathEvent event) {
        handleLivingEvents(event, event.getEntity());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingEntityUseItemEvent(LivingEntityUseItemEvent event) {
        handleLivingEvents(event, event.getEntity());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHealEvent(LivingHealEvent event) {
        handleLivingEvents(event, event.getEntity());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurtEvent(LivingHurtEvent event) {
        handleLivingEvents(event, event.getEntity());
    }

    public static void authorizePlayer(EntityPlayer player) {
        player.capabilities.allowFlying = false;
        unauthenticatedPlayers.remove(player.getGameProfile().getId());
    }

    public static boolean isPlayerAuthenticated(EntityPlayer player) {
        return player instanceof FakePlayer || !unauthenticatedPlayers.containsKey(player.getGameProfile().getId());
    }

    private static class PlayerPos {

        private final BlockPos pos;
        private final float yaw;
        private final float pitch;

        private PlayerPos(BlockPos pos, float yaw, float pitch) {
            this.pos = pos;
            this.yaw = yaw;
            this.pitch = pitch;
        }
    }
}
