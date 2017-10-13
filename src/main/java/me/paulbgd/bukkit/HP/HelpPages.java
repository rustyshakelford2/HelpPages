package me.paulbgd.bukkit.HP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class HelpPages extends JavaPlugin implements Listener {
   public void onEnable() {

      if ((!new File(getDataFolder() + File.separator + ".config.yml").exists())
            || (!getConfig().isSet("BypassOtherPlugins"))) {
         getConfig().set("GetMainTxTAutomatically", Boolean.valueOf(false));
         getConfig().set("BypassOtherPlugins", Boolean.valueOf(true));
      }
      if (!getConfig().isSet("Auto-Update"))
         getConfig().set("Auto-Update", false);
      saveConfig();
      File file = new File(getDataFolder() + File.separator + "Pages");
      if (!file.exists())
         file.mkdir();
      if ((!new File(getDataFolder() + File.separator + "Pages/Main.txt").exists())
            && (getConfig().getBoolean("GetMainTxTAutomatically")))
         try {
            String fileURL = "http://dev.bukkit.org/paste/7929.txt";
            String downloadedFileName = fileURL.substring(fileURL.lastIndexOf("/") + 1);
            URL url = new URL(fileURL);
            InputStream is = url.openStream();
            FileOutputStream fos = new FileOutputStream(getDataFolder() + File.separator + "Pages//"
                  + downloadedFileName);
            byte[] buffer = new byte[4096];
            int bytesRead = 0;
            while ((bytesRead = is.read(buffer)) != -1) {
               fos.write(buffer, 0, bytesRead);
            }
            fos.close();
            is.close();
            new File(getDataFolder() + File.separator + "Pages/7929.txt").renameTo(new File(getDataFolder()
                  + File.separator + "Pages/Main.txt"));
         } catch (IOException e) {
            getLogger().severe("Could not download Main.txt! Is your internet not working?");
            e.printStackTrace();
         }
   }

   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      if ((cmd.getName().equalsIgnoreCase("help")) && ((sender instanceof Player))) {
         Player p = (Player) sender;
         if (args.length < 1) {
            if ((p.hasPermission("helppages.all")) || (p.hasPermission("helppages.page.Main")))
               try {
                  readFileToPlayer("Main", p);
               } catch (IOException e) {
                  e.printStackTrace();
               }
            else
               p.sendMessage(color("&4You do not have permission to view this page."));
         } else if (args.length == 1) {
            if ((p.hasPermission("helppages.all")) || (p.hasPermission("helppages.page." + args[0])))
               try {
                  readFileToPlayer(args[0], p);
               } catch (IOException e) {
                  e.printStackTrace();
               }
            else
               p.sendMessage(color("&4You do not have permission to view this page."));
         } else if (args.length > 1) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
               sb.append(args[i]).append(" ");
            }
            String allArgs = sb.toString().trim();
            allArgs = allArgs.replaceAll(" ", "-");
            if ((p.hasPermission("helppages.all")) || (p.hasPermission("helppages.page." + allArgs)))
               try {
                  readFileToPlayer(allArgs, p);
               } catch (IOException e) {
                  e.printStackTrace();
               }
            else {
               p.sendMessage(color("&4You do not have permission to view this page."));
            }
         }
         return true;
      }
      return false;
   }

   @EventHandler
   public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
      if ((event.getMessage().toLowerCase().startsWith("/help "))
            || ((event.getMessage().toLowerCase().equals("/help")) && (getConfig().isSet("BypassOtherPlugins")))) {
         String msg = event.getMessage().toLowerCase();
         Player p = event.getPlayer();
         if (!msg.contains(" ")) {
            if ((p.hasPermission("helppages.all")) || (p.hasPermission("helppages.page.Main"))) {
               event.setCancelled(true);
               try {
                  readFileToPlayer("main", p);
               } catch (IOException e) {
                  e.printStackTrace();
               }
            } else {
               p.sendMessage(color("&4You do not have permission to view this page."));
            }
         } else {
            msg = msg.replaceFirst("/help ", "").replaceAll(" ", "-");
            if ((p.hasPermission("helppages.all")) || (p.hasPermission("helppages.page." + msg))) {
               event.setCancelled(true);
               try {
                  readFileToPlayer(msg, p);
               } catch (IOException e) {
                  e.printStackTrace();
               }
            } else {
               p.sendMessage(color("&4You do not have permission to view this page."));
            }
         }
      }
   }

   public void readFileToPlayer(String filename, Player p) throws IOException {
      filename = filename.replaceAll("/help ", "").replaceAll("/help", "");
      boolean isTrue = false;
      for (String string : new File(getDataFolder() + File.separator + "Pages").list()) {
         if (string.toLowerCase().replaceFirst(".txt", "")
               .equalsIgnoreCase(filename.toLowerCase().replaceFirst(".txt", ""))) {
            File file = new File(getDataFolder() + File.separator + "Pages" + File.separator + string);

            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
            String s;
            while ((s = in.readLine()) != null) {
               p.sendMessage(color(s));
            }
            in.close();
            /*
            Path path = FileSystems.getDefault().getPath(getDataFolder() + File.separator + "Pages/" + string,
                  new String[0]);
            List<String> lines = Files.readAllLines(path, Charset.defaultCharset());
            for (String line : lines){
               p.sendMessage(color(line));
            }*/
            isTrue = true;
         }
      }
      if (!isTrue)
         p.sendMessage(color("&7This page does not exist!"));
   }

   public String color(String msg) {
      return ChatColor.translateAlternateColorCodes('&', msg);
   }
}
