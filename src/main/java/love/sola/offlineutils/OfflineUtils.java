package love.sola.offlineutils;

import love.sola.offlineutils.command.LoginCommand;
import love.sola.offlineutils.command.RegisterCommand;
import love.sola.offlineutils.command.WhitelistCommand;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Mod(modid = OfflineUtils.MODID, name = OfflineUtils.NAME, version = OfflineUtils.VERSION, serverSideOnly = true, acceptableRemoteVersions = "*")
public class OfflineUtils {
    public static final String MODID = "offlineutils";
    public static final String NAME = "OfflineUtils";
    public static final String VERSION = "1.0";

    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) throws IOException {
        logger = event.getModLog();
        Database.load();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
    }

    @EventHandler
    public void onServerStart(FMLServerStartingEvent event) {
        if (event.getServer().isServerInOnlineMode()) {
            throw new IllegalStateException("Disable this mod if you're running online-mode!");
        }
        event.registerServerCommand(new WhitelistCommand());
        event.registerServerCommand(new LoginCommand());
        event.registerServerCommand(new RegisterCommand());
    }
}
