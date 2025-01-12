package sky_bai.bukkit.baiteam.listener;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import sky_bai.bukkit.baiteam.BaiTeam;
import sky_bai.bukkit.baiteam.config.ConfigType;
import sky_bai.bukkit.baiteam.gui.TeamGui;
import sky_bai.bukkit.baiteam.team.TeamManager;

public class BaiListener implements Listener {

	@EventHandler
	public void PlayerCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if (event.getMessage().contains("/dxl play")) {
			if (player.isOp()) {
				return;
			}
			event.setCancelled(true);
			
			// 无文档、未知指令执行操作！
			Bukkit.dispatchCommand(player, "/baiteam game play"+event.getMessage().replace("/dxl play", ""));
			return;
		}
	}

	@EventHandler
	public void PlayerSwapHand(PlayerSwapHandItemsEvent event) {
		Player player = event.getPlayer();
		List<String> worldNames = BaiTeam.getConfig(ConfigType.Config).getStringList("ButtonWorld");
		if (worldNames.contains(player.getWorld().getName())) {
			event.setCancelled(true);
			if (TeamManager.getTeam(player, false) != null) {
				TeamGui.getGui().openTeamInfoGui(TeamManager.getTeam(player, false), player);
				return;
			}
			Bukkit.dispatchCommand(player, "baiteam game gui all");
		}
	}

	@EventHandler
	public void playerQuitServer(PlayerQuitEvent event) {
		// 玩家引用并不止在离开服务器时需要移除
		// 在玩家死亡 / 切换世界等情况下都会重新生成新的 Player
		// 更好的方法是保存玩家的 UUID —— 754503921
		Player player = event.getPlayer();
		if (TeamManager.getTeam(player, true) != null) {
			TeamManager.getTeams().remove(TeamManager.getTeam(player, true));
		}
		if (TeamManager.getTeam(player, false) != null) {
			TeamManager.getTeam(player, false).delMember(player);
		}
	}
}
