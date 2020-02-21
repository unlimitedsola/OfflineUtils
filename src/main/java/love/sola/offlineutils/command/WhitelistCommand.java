package love.sola.offlineutils.command;

import com.mojang.authlib.GameProfile;
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

import static net.minecraft.command.CommandBase.getListOfStringsMatchingLastWord;

@SuppressWarnings("NullableProblems")
public class WhitelistCommand implements ICommand {
    @Override
    public String getCommandName() {
        return "offline_whitelist_add";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/offline_whitelist_add <username>";
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.emptyList();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 1) {
            sender.addChatMessage(new TextComponentString(this.getCommandUsage(sender)));
            return;
        }
        String username = args[0];
        server.getPlayerList().addWhitelistedPlayer(new GameProfile(EntityPlayer.getOfflineUUID(username), username));
        sender.addChatMessage(new TextComponentString("Whitelist entry added."));
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canCommandSenderUseCommand(3, this.getCommandName());
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        return args.length == 1
                ? getListOfStringsMatchingLastWord(args, server.getPlayerProfileCache().getUsernames())
                : Collections.emptyList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0;
    }

    @Override
    public int compareTo(ICommand o) {
        return this.getCommandName().compareTo(o.getCommandName());
    }
}
