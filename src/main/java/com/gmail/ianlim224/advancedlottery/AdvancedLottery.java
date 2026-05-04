package com.gmail.ianlim224.advancedlottery;

import com.gmail.ianlim224.advancedlottery.bungee.LotteryChannelListener;
import com.gmail.ianlim224.advancedlottery.commands.Executable;
import com.gmail.ianlim224.advancedlottery.commands.LotteryCommand;
import com.gmail.ianlim224.advancedlottery.commands.admin.*;
import com.gmail.ianlim224.advancedlottery.commands.player.*;
import com.gmail.ianlim224.advancedlottery.config.ConfigManager;
import com.gmail.ianlim224.advancedlottery.gui.ConfirmGUI;
import com.gmail.ianlim224.advancedlottery.gui.HelpGUI;
import com.gmail.ianlim224.advancedlottery.gui.LotteryGUI;
import com.gmail.ianlim224.advancedlottery.hooks.AdvancedLotteryExpansion;
import com.gmail.ianlim224.advancedlottery.hooks.VaultEcon;
import com.gmail.ianlim224.advancedlottery.items.MenuItems;
import com.gmail.ianlim224.advancedlottery.listeners.InventoryClick;
import com.gmail.ianlim224.advancedlottery.listeners.InventoryClose;
import com.gmail.ianlim224.advancedlottery.listeners.PlayerJoin;
import com.gmail.ianlim224.advancedlottery.listeners.PlayerQuit;
import com.gmail.ianlim224.advancedlottery.messages.Messages;
import com.gmail.ianlim224.advancedlottery.mysql.LotterySql;
import com.gmail.ianlim224.advancedlottery.object.LotteryTicket;
import com.gmail.ianlim224.advancedlottery.tasks.ItemTask;
import com.gmail.ianlim224.advancedlottery.time.LotteryTimer;
import com.gmail.ianlim224.advancedlottery.time.ReminderManager;
import com.gmail.ianlim224.advancedlottery.utils.*;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import static com.gmail.ianlim224.advancedlottery.utils.UpdateChecker.UpdateReason.UNRELEASED_VERSION;
import static com.gmail.ianlim224.advancedlottery.utils.UpdateChecker.UpdateReason.UP_TO_DATE;

public class AdvancedLottery extends JavaPlugin {
    static LotteryGrabber lotteryGrabber;

    private static AdvancedLottery instance;

    private final FileLogging fileLogging = new FileLogging(this);
    private final Random random = new Random();
    private final ReminderManager reminderManager = new ReminderManager(this);
    private final WinnerRegistry winnerRegistry = new WinnerRegistry(this);
    private final VaultEcon vaultEcon = new VaultEcon(this);
    private final LotteryConfig lotteryConfig = new LotteryConfig(this);
    private final ConfigManager messages = new ConfigManager(this, "", "messages.yml");
    private final ConfigManager items = new ConfigManager(this, "", "items.yml");
    private final PersistenceManager persistenceManager = new PersistenceManager(this);
    private final PluginMessageListener pluginMessageListener = new LotteryChannelListener();

    private LotteryTimer lotteryTimer;

    public static AdvancedLottery getInstance() {
        return instance;
    }

    public static LotteryGrabber getLotteryGrabber() {
        return lotteryGrabber;
    }

    public static List<String> replace(List<String> list, String token, String value) {
        List<String> result = new ArrayList<>(list.size());
        for (String s : list) result.add(s.replace(token, value));
        return result;
    }

    @Override
    public void onEnable() {
        instance = this;

        if (!vaultEcon.setupEconomy()) {
            getLogger().severe("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info("Hooked into Vault.");

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            getLogger().info("Hooked into PlaceholderAPI.");
            new AdvancedLotteryExpansion(this).register();
            Messages.setPlaceholderFunction(s -> PlaceholderAPI.setPlaceholders(null, s));
        }

        loadMessages();
        loadItems();

        lotteryConfig.loadDefault();
        lotteryConfig.loadGrabber();

        SpigotCommons.setMoneyFormat(lotteryGrabber.getMoneyFormat(), this);

        if (lotteryGrabber.shouldUpdate()) {
            checkForUpdates();
        }

        registerCommands();
        registerListeners();

        LotterySql sql = LotterySql.getInstance(this);
        sql.initDatabase();

        HelpGUI.getInstance(this).reload();
        LotteryGUI.getInstance().load(this);

        lotteryTimer = new LotteryTimer(this);
        lotteryTimer.start();

        reminderManager.read();
        winnerRegistry.read();

        getServer().getScheduler().runTaskTimer(this, new ItemTask(this), 0L, 20L);

        UUIDFetcher.reload();

        if (persistenceManager.loadPreviousLotterySnapshot()) {
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<green>Resuming previous lottery..."));
        }

        if (lotteryGrabber.isBungeeSync()) {
            getLogger().info("Enabling BungeeCord sync.");
            Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord", pluginMessageListener);
        }
    }

    @Override
    public void onDisable() {
        if (getConfig().getBoolean("save_lottery_progress_on_stop")) {
            getLogger().info("Saving current lottery state for next startup.");
            persistenceManager.saveCurrentLotterySnapshot();
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<green>Saving current lottery progress..."));
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Messages.TICKET_MONEY_REFUNDED.send(player);
            }
            for (Entry<UUID, Integer> entry : LotteryTicket.getInstance(this).getWhoBought().entrySet()) {
                vaultEcon.payMoney(
                        entry.getValue() * (double) lotteryGrabber.getBuyPrice(),
                        Bukkit.getOfflinePlayer(entry.getKey()));
            }
        }

        LotterySql sql = LotterySql.getInstance(this);
        if (sql.isEnabled()) sql.close();

        winnerRegistry.save();
        reminderManager.save();
        reminderManager.clean();

        if (lotteryGrabber.isBungeeSync()) {
            Bukkit.getMessenger().unregisterOutgoingPluginChannel(this);
            Bukkit.getMessenger().unregisterIncomingPluginChannel(this);
        }
    }

    private void checkForUpdates() {
        UpdateChecker.init(this, 43668);
        UpdateChecker.get().requestUpdateCheck().whenCompleteAsync((result, ex) -> {
            if (result.requiresUpdate()) {
                getLogger().info("New version available: v" + result.getNewestVersion()
                        + " (running v" + getDescription().getVersion() + ")");
                getLogger().info("Download: https://www.spigotmc.org/resources/lottery.43668/");
            } else if (result.getReason() == UP_TO_DATE) {
                getLogger().info("Running the latest version.");
            } else if (result.getReason() == UNRELEASED_VERSION) {
                getLogger().info("Running an unreleased version.");
            } else {
                getLogger().warning("Could not check for updates.");
            }
        });
    }

    private void registerCommands() {
        PluginCommand mainCmd = getCommand("lottery");

        List<Executable> commands = List.of(
                new HelpCommand(),
                new TimeCommand(),
                new BuyCommand(),
                new MenuCommand(),
                new StatsCommand(),
                new EndCommand(),
                new ReloadCommand(),
                new ReminderCommand(),
                new SetTimeCommand(),
                new AddPotCommand(),
                new AddTicketsCommand()
        );

        mainCmd.setExecutor(new LotteryCommand(this, commands));
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new InventoryClick(this), this);
        pm.registerEvents(new ConfirmGUI(this), this);
        pm.registerEvents(new UUIDFetcher(), this);
        pm.registerEvents(new PlayerQuit(), this);
        pm.registerEvents(new InventoryClose(), this);
        pm.registerEvents(new PlayerJoin(this), this);
    }

    private void loadMessages() {
        Messages.setFc(messages.getConfig());
        for (Messages msg : Messages.values()) {
            messages.getConfig().addDefault(msg.getPath(), msg.getDefaultValue());
        }
        messages.getConfig().options().copyDefaults(true);
        messages.saveConfig();
    }

    private void loadItems() {
        MenuItems.setFc(items.getConfig());

        items.getConfig().options().header(
                "Lottery items configuration — controls GUI item names, lore, and materials.\n"
                        + "Use MiniMessage formatting: https://docs.advntr.dev/minimessage/format.html");

        for (MenuItems i : MenuItems.values()) {
            if (i.getDefaultListValue().size() > 1) {
                items.getConfig().addDefault(i.getPath(), i.getDefaultListValue());
            } else {
                items.getConfig().addDefault(i.getPath(), i.getDefaultStringValue());
            }
        }

        MenuItems.loadConfigValues();
        items.getConfig().options().copyDefaults(true);
        items.saveConfig();
        ItemGrabber.reload(this);
    }

    public ConfigManager getMessagesManager() { return messages; }
    public ConfigManager getItemsManager()    { return items; }

    public Random getRandom()                 { return random; }
    public ReminderManager getReminderManager() { return reminderManager; }
    public WinnerRegistry getWinnerRegistry() { return winnerRegistry; }
    public VaultEcon getVaultEcon()           { return vaultEcon; }
    public LotteryTimer getLotteryTimer()     { return lotteryTimer; }
    public LotteryConfig getLotteryConfig()   { return lotteryConfig; }
    public FileLogging getFileLogging()       { return fileLogging; }
}
