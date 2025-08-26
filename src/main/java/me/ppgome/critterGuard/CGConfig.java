package me.ppgome.critterGuard;

public class CGConfig {

    private CritterGuard plugin;
    public String PREFIX;

    public CGConfig(CritterGuard plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
        PREFIX = plugin.getConfig().getString("prefix", "<gold>[</gold><green>CritterGuard</green><gold>]</gold>");
    }

}
