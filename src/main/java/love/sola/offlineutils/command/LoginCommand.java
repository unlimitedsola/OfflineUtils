package love.sola.offlineutils.command;

import love.sola.offlineutils.Crypto;
import love.sola.offlineutils.Database;
import love.sola.offlineutils.domain.User;
import love.sola.offlineutils.event.PlayerHandler;
import net.minecraft.command.CommandException;
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
    public String getCommandName() {
        return "login";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/login <password>";
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.emptyList();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayer)) {
            return;
        }
        if (args.length != 1) {
            sender.addChatMessage(new TextComponentString(this.getCommandUsage(sender)));
            return;
        }
        EntityPlayer player = ((EntityPlayer) sender);
        if (PlayerHandler.isPlayerAuthenticated(player)) {
            sender.addChatMessage(new TextComponentString("Already logged in."));
            return;
        }
        UUID id = player.getGameProfile().getId();
        String password = args[0];
        User user = Database.get(id);
        if (user == null) {
            sender.addChatMessage(new TextComponentString("You haven't registered yet. Type /register <password> <repeat> to register."));
            return;
        }
        if (Crypto.check(password, user.getSecret())) {
            PlayerHandler.authorizePlayer(player);
            sender.addChatMessage(new TextComponentString("Logged in."));
        } else {
            sender.addChatMessage(new TextComponentString("Nice try."));
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        return Collections.emptyList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return this.getCommandName().compareTo(o.getCommandName());
    }
}
