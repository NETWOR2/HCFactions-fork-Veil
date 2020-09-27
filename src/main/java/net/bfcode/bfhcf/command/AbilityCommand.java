package net.bfcode.bfhcf.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.bfcode.bfbase.util.Ints;
import net.bfcode.bfhcf.abilities.AntiTrapper;
import net.bfcode.bfhcf.abilities.BelchBomb;
import net.bfcode.bfhcf.abilities.Cocaine;
import net.bfcode.bfhcf.abilities.FreezeGun;
import net.bfcode.bfhcf.abilities.Grappling;
import net.bfcode.bfhcf.abilities.Murasame;
import net.bfcode.bfhcf.abilities.NinjaStar;
import net.bfcode.bfhcf.abilities.NoFall;
import net.bfcode.bfhcf.abilities.PocketBard;
import net.bfcode.bfhcf.abilities.RawPotion;
import net.bfcode.bfhcf.abilities.Refill;
import net.bfcode.bfhcf.abilities.Rocket;
import net.bfcode.bfhcf.abilities.RotateStick;
import net.bfcode.bfhcf.abilities.SnowBall;
import net.bfcode.bfhcf.abilities.SuperAxe;
import net.bfcode.bfhcf.abilities.Switcher;
import net.bfcode.bfhcf.abilities.ThunderAxe;
import net.bfcode.bfhcf.config.AbilitysFile;
import net.bfcode.bfhcf.utils.CC;

public class AbilityCommand implements CommandExecutor {

	private String grappling = ChatColor.stripColor(Grappling.name);
	private String antiTrapper = ChatColor.stripColor(AntiTrapper.name);
	private String switcher = ChatColor.stripColor(Switcher.name);
	private String snowBall = ChatColor.stripColor(SnowBall.name);
	private String cocaine = ChatColor.stripColor(Cocaine.name);
	private String pocketBard = ChatColor.stripColor(PocketBard.name);
	private String rawPotion = ChatColor.stripColor(RawPotion.name);
	private String rocket = ChatColor.stripColor(Rocket.name);
	private String noFall = ChatColor.stripColor(NoFall.name);
	private String superAxe = ChatColor.stripColor(SuperAxe.name);
	private String ninjaStar = ChatColor.stripColor(NinjaStar.name);
	private String refill = ChatColor.stripColor(Refill.name);
	private String murasame = ChatColor.stripColor(Murasame.name);
	private String rotateStick = ChatColor.stripColor(RotateStick.name);
	private String belchBomb = ChatColor.stripColor(BelchBomb.name);
	private String freezeGun = ChatColor.stripColor(FreezeGun.name);
	private String thunderAxe = ChatColor.stripColor(ThunderAxe.name);

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("abilities.command.ability")) {
			sender.sendMessage(CC.translate("&cYou don't have permission."));
			return true;
		}
		if (args.length < 1) {
			this.getUsage(sender, label);
			return true;
		}
		if (args[0].equalsIgnoreCase("give")) {
			if (args.length < 4) {
				sender.sendMessage(CC.translate("&cUsage: /" + label + " give <player> <amount> <ability|all>"));
				return true;
			}
			Player target = Bukkit.getPlayer(args[1]);
			if (target == null) {
				sender.sendMessage(CC.translate("&cPlayer '" + args[1] + "' not found!"));
				return true;
			}
			Integer amount = Ints.tryParse(args[2]);
			if (amount == null) {
				sender.sendMessage(CC.translate("&c'" + args[2] + "' is not a valid number."));
				return true;
			}
			if (amount <= 0) {
				sender.sendMessage(CC.translate("&cThe amount must be positive!"));
				return true;
			}
			if (args[3].equalsIgnoreCase("all")) {
				Grappling.getGrappling(sender, target, amount);
				AntiTrapper.getAntiTrapper(sender, target, amount);
				Switcher.getSwitcher(sender, target, amount);
				SnowBall.getSnowball(sender, target, amount);
				Cocaine.getCocaine(sender, target, amount);
				PocketBard.getPocketBard(sender, target, amount);
				RawPotion.getRawPotion(sender, target, amount);
				Rocket.getRocket(sender, target, amount);
				NoFall.getNoFall(sender, target, amount);
				SuperAxe.getSuperAxe(sender, target, amount);
				NinjaStar.getNinjaStar(sender, target, amount);
				Refill.getRefill(sender, target, amount);
				Murasame.getMurasame(sender, target, amount);
				RotateStick.getRotateStick(sender, target, amount);
				BelchBomb.getBelchBomb(sender, target, amount);
				FreezeGun.getFreezeGun(sender, target, amount);
				ThunderAxe.getThunderAxe(sender, target, amount);
			} else if (args[3].equalsIgnoreCase(grappling)) {
				Grappling.getGrappling(sender, target, amount);
			} else if (args[3].equalsIgnoreCase(antiTrapper)) {
				AntiTrapper.getAntiTrapper(sender, target, amount);
			} else if (args[3].equalsIgnoreCase(switcher)) {
				Switcher.getSwitcher(sender, target, amount);
			} else if (args[3].equalsIgnoreCase(snowBall)) {
				SnowBall.getSnowball(sender, target, amount);
			} else if (args[3].equalsIgnoreCase(cocaine)) {
				Cocaine.getCocaine(sender, target, amount);
			} else if (args[3].equalsIgnoreCase(pocketBard)) {
				PocketBard.getPocketBard(sender, target, amount);
			} else if (args[3].equalsIgnoreCase(rawPotion)) {
				RawPotion.getRawPotion(sender, target, amount);
			} else if (args[3].equalsIgnoreCase(rocket)) {
				Rocket.getRocket(sender, target, amount);
			} else if (args[3].equalsIgnoreCase(noFall)) {
				NoFall.getNoFall(sender, target, amount);
			} else if (args[3].equalsIgnoreCase(superAxe)) {
				SuperAxe.getSuperAxe(sender, target, amount);
			} else if (args[3].equalsIgnoreCase(ninjaStar)) {
				NinjaStar.getNinjaStar(sender, target, amount);
			} else if (args[3].equalsIgnoreCase(refill)) {
				Refill.getRefill(sender, target, amount);
			} else if (args[3].equalsIgnoreCase(murasame)) {
				Murasame.getMurasame(sender, target, amount);
			} else if (args[3].equalsIgnoreCase(rotateStick)) {
				RotateStick.getRotateStick(sender, target, amount);
			} else if (args[3].equalsIgnoreCase(belchBomb)) {
				BelchBomb.getBelchBomb(sender, target, amount);
			} else if (args[3].equalsIgnoreCase(freezeGun)) {
				FreezeGun.getFreezeGun(sender, target, amount);
			} else if (args[3].equalsIgnoreCase(thunderAxe)) {
				ThunderAxe.getThunderAxe(sender, target, amount);
			} else {
				sender.sendMessage(ChatColor.RED + "The ability '" + args[3] + "' don't exist!");
				return true;
			}
		} else if (args[0].equalsIgnoreCase("list")) {
			this.getList(sender);
			return true;
		} else if (args[0].equalsIgnoreCase("cooldown")) {
			if (args.length < 2) {
				this.getUsage(sender, label);
				return true;
			}
			if (args[1].equalsIgnoreCase("set")) {
				if (args.length < 4) {
	                sender.sendMessage(CC.translate("&cUsage: /" + label + " cooldown set <player> <ability>"));
	                return true;
	            }
				Player target = Bukkit.getPlayer(args[2]);
				if (target == null) {
					sender.sendMessage(CC.translate("&cPlayer '" + args[2] + "' not found!"));
					return true;
				} if (args[3].equalsIgnoreCase(grappling)) {
					if (!Grappling.isOnCooldown(target)) {
						Grappling.cooldown.put(target.getName(),System.currentTimeMillis() + (AbilitysFile.getConfig().getInt("ABILITIES.GRAPPLING.COOLDOWN") * 1000));
						sender.sendMessage(CC.translate(Grappling.name + " &ecooldown of '" + target.getName() + "' has been set."));
						return true;
					}
					sender.sendMessage(CC.translate("&c&l" + target.getName() + " &chave " + Grappling.name + " &ccooldown."));
				} else if (args[3].equalsIgnoreCase(antiTrapper)) {
					if (!AntiTrapper.isOnCooldownDam(target)) {
						AntiTrapper.cooldowndam.put(target.getName(),System.currentTimeMillis() + (AbilitysFile.getConfig().getInt("ABILITIES.ANTI-TRAPPER.COOLDOWN") * 1000));
						sender.sendMessage(CC.translate(AntiTrapper.name + " &ecooldown of '" + target.getName() + "' has been set."));
						return true;
					}
					sender.sendMessage(CC.translate("&c&l" + target.getName() + " &chave " + AntiTrapper.name + " &ccooldown."));
				} else if (args[3].equalsIgnoreCase(switcher)) {
					if (!Switcher.isOnCooldown(target)) {
						Switcher.cooldown.put(target.getName(),System.currentTimeMillis() + (AbilitysFile.getConfig().getInt("ABILITIES.SWITCHER.COOLDOWN") * 1000));
						sender.sendMessage(CC.translate(Switcher.name + " &ecooldown of '" + target.getName() + "' has been set."));
						return true;
					}
					sender.sendMessage(CC.translate("&c&l" + target.getName() + " &chave " + Switcher.name + " &ccooldown."));
				} else if (args[3].equalsIgnoreCase(snowBall)) {
					if (!SnowBall.isOnCooldown(target)) {
						SnowBall.cooldown.put(target.getName(),System.currentTimeMillis() + (AbilitysFile.getConfig().getInt("ABILITIES.SNOWBALL.COOLDOWN") * 1000));
						sender.sendMessage(CC.translate(SnowBall.name + " &ecooldown of '" + target.getName() + "' has been set."));
						return true;
					}
					sender.sendMessage(CC.translate("&c&l" + target.getName() + " &chave " + SnowBall.name + " &ccooldown."));
				} else if (args[3].equalsIgnoreCase(cocaine)) {
					if (!Cocaine.isOnCooldown(target)) {
						Cocaine.cooldown.put(target.getName(),System.currentTimeMillis() + (AbilitysFile.getConfig().getInt("ABILITIES.COCAINE.COOLDOWN") * 1000));
						sender.sendMessage(CC.translate(Cocaine.name + " &ecooldown of '" + target.getName() + "' has been set."));
						return true;
					}
					sender.sendMessage(CC.translate("&c&l" + target.getName() + " &chave " + Cocaine.name + " &ccooldown."));
				} else if (args[3].equalsIgnoreCase(pocketBard)) {
					if (!PocketBard.isOnCooldown(target)) {
						PocketBard.cooldown.put(target.getName(),System.currentTimeMillis() + (AbilitysFile.getConfig().getInt("ABILITIES.POCKET-BARD.COOLDOWN") * 1000));
						sender.sendMessage(CC.translate(PocketBard.name + " &ecooldown of '" + target.getName() + "' has been set."));
						return true;
					}
					sender.sendMessage(CC.translate("&c&l" + target.getName() + " &chave " + PocketBard.name + " &ccooldown."));
				} else if (args[3].equalsIgnoreCase(rawPotion)) {
					if (!RawPotion.isOnCooldown(target)) {
						RawPotion.cooldown.put(target.getName(),System.currentTimeMillis() + (AbilitysFile.getConfig().getInt("ABILITIES.RAW-POTION.COOLDOWN") * 1000));
						sender.sendMessage(CC.translate(RawPotion.name + " &ecooldown of '" + target.getName() + "' has been set."));
						return true;
					}
					sender.sendMessage(CC.translate("&c&l" + target.getName() + " &chave " + RawPotion.name + " &ccooldown."));
				} else if (args[3].equalsIgnoreCase(rocket)) {
					if (!Rocket.isOnCooldown(target)) {
						Rocket.cooldown.put(target.getName(),System.currentTimeMillis() + (AbilitysFile.getConfig().getInt("ABILITIES.ROCKET.COOLDOWN") * 1000));
						sender.sendMessage(CC.translate(Rocket.name + " &ecooldown of '" + target.getName() + "' has been set."));
						return true;
					}
					sender.sendMessage(CC.translate("&c&l" + target.getName() + " &chave " + Rocket.name + " &ccooldown."));
				} else if (args[3].equalsIgnoreCase(noFall)) {
					sender.sendMessage(CC.translate(NoFall.name + " &cnot have cooldown."));
				} else if (args[3].equalsIgnoreCase(superAxe)) {
					if (!SuperAxe.isOnCooldown(target)) {
						SuperAxe.cooldown.put(target.getName(),System.currentTimeMillis() + (AbilitysFile.getConfig().getInt("ABILITIES.SUPER-AXE.COOLDOWN") * 1000));
						sender.sendMessage(CC.translate(SuperAxe.name + " &ecooldown of '" + target.getName() + "' has been set."));
						return true;
					}
					sender.sendMessage(CC.translate("&c&l" + target.getName() + " &chave " + SuperAxe.name + " &ccooldown."));
				} else if (args[3].equalsIgnoreCase(ninjaStar)) {
					if (!NinjaStar.isOnCooldown(target)) {
						NinjaStar.cooldown.put(target.getName(),System.currentTimeMillis() + (AbilitysFile.getConfig().getInt("ABILITIES.NINJA-STAR.COOLDOWN") * 1000));
						sender.sendMessage(CC.translate(NinjaStar.name + " &ecooldown of '" + target.getName() + "' has been set."));
						return true;
					}
					sender.sendMessage(CC.translate("&c&l" + target.getName() + " &chave " + NinjaStar.name + " &ccooldown."));
				} else if (args[3].equalsIgnoreCase(refill)) {
					if (!Refill.isOnCooldown(target)) {
						Refill.cooldown.put(target.getName(),System.currentTimeMillis() + (AbilitysFile.getConfig().getInt("ABILITIES.REFILL.COOLDOWN") * 1000));
						sender.sendMessage(CC.translate(Refill.name + " &ecooldown of '" + target.getName() + "' has been set."));
						return true;
					}
					sender.sendMessage(CC.translate("&c&l" + target.getName() + " &chave " + Refill.name + " &ccooldown."));
				} else if (args[3].equalsIgnoreCase(murasame)) {
					if (!Murasame.isOnCooldown(target)) {
						Murasame.cooldown.put(target.getName(),System.currentTimeMillis() + (AbilitysFile.getConfig().getInt("ABILITIES.MURASAME.COOLDOWN") * 1000));
						sender.sendMessage(CC.translate(Murasame.name + " &ecooldown of '" + target.getName() + "' has been set."));
						return true;
					}
					sender.sendMessage(CC.translate("&c&l" + target.getName() + " &chave " + Murasame.name + " &ccooldown."));
				} else if (args[3].equalsIgnoreCase(rotateStick)) {
					if (!RotateStick.isOnCooldown(target)) {
						RotateStick.cooldown.put(target.getName(),System.currentTimeMillis() + (AbilitysFile.getConfig().getInt("ABILITIES.ROTATE-STICK.COOLDOWN") * 1000));
						sender.sendMessage(CC.translate(RotateStick.name + " &ecooldown of '" + target.getName() + "' has been set."));
						return true;
					}
					sender.sendMessage(CC.translate("&c&l" + target.getName() + " &chave " + RotateStick.name + " &ccooldown."));
				} else if (args[3].equalsIgnoreCase(belchBomb)) {
					if (!BelchBomb.isOnCooldown(target)) {
						BelchBomb.cooldown.put(target.getName(),System.currentTimeMillis() + (AbilitysFile.getConfig().getInt("ABILITIES.BELCH-BOMB.COOLDOWN") * 1000));
						sender.sendMessage(CC.translate(BelchBomb.name + " &ecooldown of '" + target.getName() + "' has been set."));
						return true;
					}
					sender.sendMessage(CC.translate("&c&l" + target.getName() + " &chave " + BelchBomb.name + " &ccooldown."));
				} else if (args[3].equalsIgnoreCase(freezeGun)) {
					if (!FreezeGun.isOnCooldown(target)) {
						FreezeGun.cooldown.put(target.getName(),System.currentTimeMillis() + (AbilitysFile.getConfig().getInt("ABILITIES.FREEZE-GUN.COOLDOWN") * 1000));
						sender.sendMessage(CC.translate(FreezeGun.name + " &ecooldown of '" + target.getName() + "' has been set."));
						return true;
					}
					sender.sendMessage(CC.translate("&c&l" + target.getName() + " &chave " + FreezeGun.name + " &ccooldown."));
				} else if (args[3].equalsIgnoreCase(thunderAxe)) {
					if (!ThunderAxe.isOnCooldown(target)) {
						ThunderAxe.cooldown.put(target.getName(),System.currentTimeMillis() + (AbilitysFile.getConfig().getInt("ABILITIES.THUNDER-AXE.COOLDOWN") * 1000));
						sender.sendMessage(CC.translate(ThunderAxe.name + " &ecooldown of '" + target.getName() + "' has been set."));
						return true;
					}
					sender.sendMessage(CC.translate("&c&l" + target.getName() + " &chave " + ThunderAxe.name + " &ccooldown."));
				} else {
					sender.sendMessage(ChatColor.RED + "The ability '" + args[3] + "' don't exist!");
					return true;
				}
	            return true;
	        } else if (args[1].equalsIgnoreCase("remove")) {
	        	if (args.length < 4) {
	                sender.sendMessage(CC.translate("&cUsage: /" + label + " cooldown remove <player> <ability>"));
	                return true;
	            }
				Player target = Bukkit.getPlayer(args[2]);
				if (target == null) {
					sender.sendMessage(CC.translate("&cPlayer '" + args[2] + "' not found!"));
					return true;
				} if (args[3].equalsIgnoreCase(grappling)) {
					if (Grappling.isOnCooldown(target)) {
						Grappling.cooldown.remove(target.getName());
						sender.sendMessage(CC.translate(Grappling.name + " &ecooldown of '" + target.getName() + "' has been remove."));
						return true;
					}
					sender.sendMessage(CC.translate("&c&l" + target.getName() + " &cnot have " + Grappling.name + " &ccooldown."));
				} else if (args[3].equalsIgnoreCase(antiTrapper)) {
					if (AntiTrapper.isOnCooldownDam(target)) {
						AntiTrapper.cooldowndam.remove(target.getName());
						sender.sendMessage(CC.translate(AntiTrapper.name + " &ecooldown of '" + target.getName() + "' has been remove."));
						return true;
					}
					sender.sendMessage(CC.translate("&c&l" + target.getName() + " &cnot have " + AntiTrapper.name + " &ccooldown."));
				} else if (args[3].equalsIgnoreCase(switcher)) {
					if (Switcher.isOnCooldown(target)) {
						Switcher.cooldown.remove(target.getName());
						sender.sendMessage(CC.translate(Switcher.name + " &ecooldown of '" + target.getName() + "' has been remove."));
						return true;
					}
					sender.sendMessage(CC.translate("&c&l" + target.getName() + " &cnot have " + Switcher.name + " &ccooldown."));
				} else if (args[3].equalsIgnoreCase(snowBall)) {
					if (SnowBall.isOnCooldown(target)) {
						SnowBall.cooldown.remove(target.getName());
						sender.sendMessage(CC.translate(SnowBall.name + " &ecooldown of '" + target.getName() + "' has been remove."));
						return true;
					}
					sender.sendMessage(CC.translate("&c&l" + target.getName() + " &cnot have " + SnowBall.name + " &ccooldown."));
				} else if (args[3].equalsIgnoreCase(cocaine)) {
					if (Cocaine.isOnCooldown(target)) {
						Cocaine.cooldown.remove(target.getName());
						sender.sendMessage(CC.translate(Cocaine.name + " &ecooldown of '" + target.getName() + "' has been remove."));
						return true;
					}
					sender.sendMessage(CC.translate("&c&l" + target.getName() + " &cnot have " + Cocaine.name + " &ccooldown."));
				} else if (args[3].equalsIgnoreCase(pocketBard)) {
					if (PocketBard.isOnCooldown(target)) {
						PocketBard.cooldown.remove(target.getName());
						sender.sendMessage(CC.translate(PocketBard.name + " &ecooldown of '" + target.getName() + "' has been remove."));
						return true;
					}
					sender.sendMessage(CC.translate("&c&l" + target.getName() + " &cnot have " + PocketBard.name + " &ccooldown."));
				} else if (args[3].equalsIgnoreCase(rawPotion)) {
					if (RawPotion.isOnCooldown(target)) {
						RawPotion.cooldown.remove(target.getName());
						sender.sendMessage(CC.translate(RawPotion.name + " &ecooldown of '" + target.getName() + "' has been remove."));
						return true;
					}
					sender.sendMessage(CC.translate("&c&l" + target.getName() + " &cnot have " + RawPotion.name + " &ccooldown."));
				} else if (args[3].equalsIgnoreCase(rocket)) {
					if (Rocket.isOnCooldown(target)) {
						Rocket.cooldown.remove(target.getName());
						sender.sendMessage(CC.translate(Rocket.name + " &ecooldown of '" + target.getName() + "' has been remove."));
						return true;
					}
					sender.sendMessage(CC.translate("&c&l" + target.getName() + " &cnot have " + Rocket.name + " &ccooldown."));
				} else if (args[3].equalsIgnoreCase(noFall)) {
					sender.sendMessage(CC.translate(NoFall.name + " &cnot have cooldown."));
				} else if (args[3].equalsIgnoreCase(superAxe)) {
					if (SuperAxe.isOnCooldown(target)) {
						SuperAxe.cooldown.remove(target.getName());
						sender.sendMessage(CC.translate(SuperAxe.name + " &ecooldown of '" + target.getName() + "' has been remove."));
						return true;
					}
					sender.sendMessage(CC.translate("&c&l" + target.getName() + " &cnot have " + SuperAxe.name + " &ccooldown."));
				} else if (args[3].equalsIgnoreCase(ninjaStar)) {
					if (NinjaStar.isOnCooldown(target)) {
						NinjaStar.cooldown.remove(target.getName());
						sender.sendMessage(CC.translate(NinjaStar.name + " &ecooldown of '" + target.getName() + "' has been remove."));
						return true;
					}
					sender.sendMessage(CC.translate("&c&l" + target.getName() + " &cnot have " + NinjaStar.name + " &ccooldown."));
				} else if (args[3].equalsIgnoreCase(refill)) {
					if (Refill.isOnCooldown(target)) {
						Refill.cooldown.remove(target.getName());
						sender.sendMessage(CC.translate(Refill.name + " &ecooldown of '" + target.getName() + "' has been remove."));
						return true;
					}
					sender.sendMessage(CC.translate("&c&l" + target.getName() + " &cnot have " + Refill.name + " &ccooldown."));
				} else if (args[3].equalsIgnoreCase(murasame)) {
					if (Murasame.isOnCooldown(target)) {
						Murasame.cooldown.remove(target.getName());
						sender.sendMessage(CC.translate(Murasame.name + " &ecooldown of '" + target.getName() + "' has been remove."));
						return true;
					}
					sender.sendMessage(CC.translate("&c&l" + target.getName() + " &cnot have " + Murasame.name + " &ccooldown."));
				} else if (args[3].equalsIgnoreCase(rotateStick)) {
					if (RotateStick.isOnCooldown(target)) {
						RotateStick.cooldown.remove(target.getName());
						sender.sendMessage(CC.translate(RotateStick.name + " &ecooldown of '" + target.getName() + "' has been remove."));
						return true;
					}
					sender.sendMessage(CC.translate("&c&l" + target.getName() + " &cnot have " + RotateStick.name + " &ccooldown."));
				} else if (args[3].equalsIgnoreCase(belchBomb)) {
					if (BelchBomb.isOnCooldown(target)) {
						BelchBomb.cooldown.remove(target.getName());
						sender.sendMessage(CC.translate(BelchBomb.name + " &ecooldown of '" + target.getName() + "' has been remove."));
						return true;
					}
					sender.sendMessage(CC.translate("&c&l" + target.getName() + " &cnot have " + BelchBomb.name + " &ccooldown."));
				} else if (args[3].equalsIgnoreCase(freezeGun)) {
					if (FreezeGun.isOnCooldown(target)) {
						FreezeGun.cooldown.remove(target.getName());
						sender.sendMessage(CC.translate(FreezeGun.name + " &ecooldown of '" + target.getName() + "' has been remove."));
						return true;
					}
					sender.sendMessage(CC.translate("&c&l" + target.getName() + " &cnot have " + FreezeGun.name + " &ccooldown."));
				} else if (args[3].equalsIgnoreCase(thunderAxe)) {
					if (ThunderAxe.isOnCooldown(target)) {
						ThunderAxe.cooldown.remove(target.getName());
						sender.sendMessage(CC.translate(ThunderAxe.name + " &ecooldown of '" + target.getName() + "' has been remove."));
						return true;
					}
					sender.sendMessage(CC.translate("&c&l" + target.getName() + " &cnot have " + ThunderAxe.name + " &ccooldown."));
				} else {
					sender.sendMessage(ChatColor.RED + "The ability '" + args[3] + "' don't exist!");
					return true;
				}
	            return true;
	        } else {
	            sender.sendMessage(CC.translate("&cCooldown sub-command '" + args[1] + "' not found."));
	            return true;
	        }
		} else {
			sender.sendMessage(CC.translate("&cThe argument '" + args[0] + "' don't exist!"));
			return true;
		}
		return true;
	}

	public void getUsage(CommandSender sender, String label) {
		sender.sendMessage(CC.translate("&7&m-------------------------------------"));
		sender.sendMessage(CC.translate(AbilitysFile.getConfig().getString("COLOR.MAIN") + "Abilities Help &4\u2764"));
		sender.sendMessage(CC.translate(""));
		sender.sendMessage(CC.translate(" &7\u25CF " + AbilitysFile.getConfig().getString("COLOR.SECONDARY") + 
				"/" + label + " give <player> <amount> <ability|all>"));
		sender.sendMessage(CC.translate(" &7\u25CF " + AbilitysFile.getConfig().getString("COLOR.SECONDARY") + 
				"/" + label + " list"));
		sender.sendMessage(CC.translate(""));
		sender.sendMessage(CC.translate(" &7\u25CF " + AbilitysFile.getConfig().getString("COLOR.SECONDARY") + 
				"/" + label + " cooldown set <player> <ability>"));
		sender.sendMessage(CC.translate(" &7\u25CF " + AbilitysFile.getConfig().getString("COLOR.SECONDARY") + 
				"/" + label + " cooldown remove <player> <ability>"));
		sender.sendMessage(CC.translate("&7&m-------------------------------------"));
		
	}

	public void getList(CommandSender sender) {
		sender.sendMessage(CC.translate(AbilitysFile.getConfig().getString("COLOR.MAIN") + "Abilities List &7(17)"));
		sender.sendMessage(CC.translate(""));
		sender.sendMessage(CC.translate(" &7\u25CF " + Grappling.name));
		sender.sendMessage(CC.translate(" &7\u25CF " + AntiTrapper.name));
		sender.sendMessage(CC.translate(" &7\u25CF " + Switcher.name));
		sender.sendMessage(CC.translate(" &7\u25CF " + SnowBall.name));
		sender.sendMessage(CC.translate(" &7\u25CF " + Cocaine.name));
		sender.sendMessage(CC.translate(" &7\u25CF " + PocketBard.name));
		sender.sendMessage(CC.translate(" &7\u25CF " + RawPotion.name));
		sender.sendMessage(CC.translate(" &7\u25CF " + Rocket.name));
		sender.sendMessage(CC.translate(" &7\u25CF " + NoFall.name));
		sender.sendMessage(CC.translate(" &7\u25CF " + SuperAxe.name));
		sender.sendMessage(CC.translate(" &7\u25CF " + NinjaStar.name));
		sender.sendMessage(CC.translate(" &7\u25CF " + Refill.name));
		sender.sendMessage(CC.translate(" &7\u25CF " + Murasame.name));
		sender.sendMessage(CC.translate(" &7\u25CF " + RotateStick.name));
		sender.sendMessage(CC.translate(" &7\u25CF " + BelchBomb.name));
		sender.sendMessage(CC.translate(" &7\u25CF " + FreezeGun.name));
		sender.sendMessage(CC.translate(" &7\u25CF " + ThunderAxe.name));
		sender.sendMessage(CC.translate(""));
	}
}
