package love.sola.offlineutils.command;

import love.sola.offlineutils.Crypto;
import love.sola.offlineutils.Database;
import love.sola.offlineutils.domain.User;
import love.sola.offlineutils.event.PlayerHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("NullableProblems")
public class LoginCommand implements ICommand {
    @Override
    public String getName() {
        return "login";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/login <password>";
    }

    @Override
    public List<String> getAliases() {
        return Collections.emptyList();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayer)) {
            return;
        }
        if (args.length != 1) {
            sender.sendMessage(new TextComponentString(this.getUsage(sender)));
            return;
        }
        EntityPlayer player = ((EntityPlayer) sender);
        if (PlayerHandler.isPlayerAuthenticated(player)) {
            sender.sendMessage(new TextComponentString("Already logged in."));
            return;
        }
        UUID id = player.getGameProfile().getId();
        String password = args[0];
        User user = Database.get(id);
        if (user == null) {
            sender.sendMessage(new TextComponentString("You haven't registered yet. Type /register <password> <repeat> to register."));
            return;
        }
        if (Crypto.check(password, user.getSecret())) {
            PlayerHandler.authorizePlayer(player);
            sender.sendMessage(new TextComponentString("Logged in."));
        } else {
            sender.sendMessage(new TextComponentString("Nice try."));
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        return Collections.emptyList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return this.getName().compareTo(o.getName());
    }
}
