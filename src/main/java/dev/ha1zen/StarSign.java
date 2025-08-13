package dev.ha1zen;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StarSign extends JavaPlugin implements CommandExecutor, Listener {

    @Override
    public void onEnable() {
        this.getCommand("sign").setExecutor(this);
        this.getCommand("unsign").setExecutor(this);

        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Эта команда доступна только игрокам!");
            return true;
        }

        Player player = (Player) sender;

        switch (command.getName().toLowerCase()) {
            case "sign":
                if (!player.hasPermission("starsign.sign")) {
                    player.sendMessage(ChatColor.RED + "У вас нет разрешения на использование этой команды!");
                    return true;
                }

                sign(player, args);
                break;
            case "unsign":
                if (!player.hasPermission("starsign.unsign") && !player.hasPermission("starsign.adminunsign")) {
                    player.sendMessage(ChatColor.RED + "У вас нет разрешения на использование этой команды!");
                    return true;
                }

                unsign(player);
                break;
        }

        return true;
    }

    private void sign(Player player, String[] args) {
        ItemStack itemHand = player.getInventory().getItemInMainHand();
        ItemStack itemInOffHand = player.getInventory().getItemInOffHand();

        if (itemHand.getType().isAir()) {
            player.sendMessage(ChatColor.RED + "Вы должны держать предмет в руке!");
            return;
        }

        ItemMeta meta = itemHand.getItemMeta();
        if (meta == null) {
            player.sendMessage(ChatColor.RED + "Этот предмет не может быть подписан!");
            return;
        }

        ChatColor color = ChatColor.WHITE;

        if (itemInOffHand != null && !itemInOffHand.getType().isAir()) {
            color = getcolor(itemInOffHand);
            if (color == ChatColor.GRAY) {
                player.sendMessage(ChatColor.YELLOW + "Этот предмет не может быть использован как краситель. Подпись будет выполнена без цвета.");
                color = ChatColor.WHITE;
            } else {
                int amount = itemInOffHand.getAmount();
                if (amount > 1) {
                    itemInOffHand.setAmount(amount - 1);
                } else {
                    player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
                }
            }
        }

        List<String> lore = meta.getLore() != null ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        String author = player.getName();
        String date = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date());
        String text = args.length > 0 ? String.join(" ", args) : "";

        boolean replaced = false;
        for (int i = 0; i < lore.size(); i++) {
            String line = lore.get(i);
            if (line.startsWith(ChatColor.GRAY + "Автор: " + author)) {
                int start = i;
                while (i < lore.size() && lore.get(i).startsWith(ChatColor.GRAY.toString())) {
                    i++;
                }
                List<String> newSignLines = new ArrayList<>();
                newSignLines.add(color + "Автор: " + author);
                newSignLines.add(color + "Дата: " + date);
                if (!text.isEmpty()) {
                    newSignLines.add(color + "Подпись: " + text);
                }
                lore.subList(start, i).clear();
                lore.addAll(start, newSignLines);
                replaced = true;
                break;
            }
        }

        if (!replaced) {
            List<String> newSignLines = new ArrayList<>();
            newSignLines.add(color + "Автор: " + author);
            newSignLines.add(color + "Дата: " + date);
            if (!text.isEmpty()) {
                newSignLines.add(color + "Подпись: " + text);
            }
            lore.addAll(newSignLines);
        }

        meta.setLore(lore);
        itemHand.setItemMeta(meta);

        player.sendMessage(ChatColor.GREEN + "Предмет успешно подписан!");
    }

    private void unsign(Player player) {
        ItemStack itemHand = player.getInventory().getItemInMainHand();
        if (itemHand.getType().isAir()) {
            player.sendMessage(ChatColor.RED + "Вы должны держать предмет в руке!");
            return;
        }

        ItemMeta meta = itemHand.getItemMeta();
        if (meta == null || meta.getLore() == null) {
            player.sendMessage(ChatColor.RED + "На этом предмете нет подписи!");
            return;
        }
        meta.setLore(null);


        itemHand.setItemMeta(meta);

        player.sendMessage(ChatColor.GREEN + "Подпись успешно удалена!");
    }

    public static ChatColor getcolor(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return ChatColor.GRAY;
        }

        switch (itemStack.getType()) {
            case BLUE_DYE:
                return ChatColor.BLUE;
            case CYAN_DYE:
                return ChatColor.DARK_AQUA;
            case LIGHT_BLUE_DYE:
                return ChatColor.AQUA;
            case GREEN_DYE:
                return ChatColor.GREEN;
            case LIME_DYE:
                return ChatColor.GREEN;
            case BROWN_DYE:
                return ChatColor.GRAY;
            case BLACK_DYE:
                return ChatColor.BLACK;
            case GRAY_DYE:
                return ChatColor.GRAY;
            case LIGHT_GRAY_DYE:
                return ChatColor.GRAY;
            case WHITE_DYE:
                return ChatColor.WHITE;
            case PURPLE_DYE:
                return ChatColor.DARK_PURPLE;
            case MAGENTA_DYE:
                return ChatColor.MAGIC;
            case PINK_DYE:
                return ChatColor.LIGHT_PURPLE;
            case ORANGE_DYE:
                return ChatColor.BOLD;
            case RED_DYE:
                return ChatColor.RED;
            case YELLOW_DYE:
                return ChatColor.YELLOW;
            default:
                return ChatColor.GRAY;
        }
    }

}