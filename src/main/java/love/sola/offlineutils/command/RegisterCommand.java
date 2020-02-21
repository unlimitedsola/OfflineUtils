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
public class RegisterCommand implements ICommand {
    @Override
    public String getCommandName() {
        return "register";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/register <password> <repeat>";
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
        if (args.length != 2) {
            sender.addChatMessage(new TextComponentString(this.getCommandUsage(sender)));
            return;
        }
        EntityPlayer player = ((EntityPlayer) sender);
        UUID id = player.getGameProfile().getId();
        String password = args[0];
        String passwordConfirm = args[1];
        User user = Database.get(id);
        if (!PlayerHandler.isPlayerAuthenticated(player)) {
            if (user != null) {
                sender.addChatMessage(new TextComponentString("Nice try."));
            } else {
                if (checkPasswordPolicy(sender, password, passwordConfirm)) {
                    String secret = Crypto.hash(password);
                    Database.put(new User(player.getName(), id, secret));
                    PlayerHandler.authorizePlayer(player);
                    sender.addChatMessage(new TextComponentString("Registered, enjoy!"));
                }
            }
        } else {
            if (checkPasswordPolicy(sender, password, passwordConfirm)) {
                String secret = Crypto.hash(password);
                Database.put(new User(player.getName(), id, secret));
                sender.addChatMessage(new TextComponentString("Password changed successfully."));
            }
        }
    }

    private boolean checkPasswordPolicy(ICommandSender sender, String password, String passwordConfirm) {
        if (password.length() < 6) {
            sender.addChatMessage(new TextComponentString("Dude, Really?"));
            return false;
        } else if (!password.equals(passwordConfirm)) {
            sender.addChatMessage(new TextComponentString("Aww, don't get too slippy."));
            return false;
        }
        return true;
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
