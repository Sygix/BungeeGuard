package fr.greenns.BungeeGuard.Mute;

import java.sql.SQLException;
import java.util.UUID;

import fr.greenns.BungeeGuard.BungeeGuard;

public class Mute {
	UUID UUID;
	String Pseudo;
	long time;
	String reason;
	String adminName;
	String adminUUID;

	public Mute(UUID UUID, String Pseudo, long time, String reason, String adminName, String adminUUID) {
		this.UUID = UUID;
		this.Pseudo = Pseudo;
		this.time = time;
		this.reason = reason;
		this.adminName = adminName;
		this.adminUUID = adminUUID;
		BungeeGuard.mutes.add(this);
	}

	public String getPseudo() {
		return Pseudo;
	}

	public void setPseudo(String pseudo) {
		Pseudo = pseudo;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public String getAdminUUID() {
		return adminUUID;
	}

	public void setAdminUUID(String adminUUID) {
		this.adminUUID = adminUUID;
	}

	public void setUUID(UUID uUID) {
		UUID = uUID;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public UUID getUUID() {
		return UUID;
	}

	public long getTime() {
		return time;
	}

	public String getReason() {
		return reason;
	}

	public boolean isMute() {
		return (System.currentTimeMillis() <= time);
	}

	/**
	 * 
	 * STATUS 0 = PAS BANNIS
	 * 		  1 = BANNIS
	 * 
	 * */

	public void addToBdd() {
		try
		{
			if (BungeeGuard.plugin.sql.getConnection().isClosed())
			{
				BungeeGuard.plugin.sql.open();
			}

			if(BungeeGuard.plugin.sql.getConnection() == null)
			{
				System.out.println("[MYSQL] Connection error ...");
			}

			BungeeGuard.plugin.sql.query("UPDATE `BungeeGuard_Mute` SET status='0', unmuteReason='ReMute-By-"+getAdminName()+"', unmute='"+System.currentTimeMillis()+"' WHERE uuidMute='"+getUUID()+"' AND status='1'");

			BungeeGuard.plugin.sql.query("INSERT INTO `BungeeGuard_Mute` "
					+ "(`id`, `nameMute`, `nameAdmin` , `uuidMute` , `uuidAdmin` , `mute`, `unmute`, `reason`, `unmuteReason`, `unmuteName`, `status`) VALUES "
					+ "(NULL, '"+getPseudo()+"', '" + getAdminName() + "', '" + getUUID() + "', '" + getAdminUUID() + "' , '"+System.currentTimeMillis()+"', '"+getTime()+"', '"+getReason()+"', '', '', '1');");
		}
		catch (final SQLException ex)
		{
			System.out.println("SQL problem (exception) when add mute player to BDD : " + ex );
		}
		finally
		{
			try
			{
				if (!BungeeGuard.plugin.sql.getConnection().isClosed())
				{
					BungeeGuard.plugin.sql.close();
				}
			}
			catch (SQLException ex)
			{
				System.out.println(ex);
			}
		}

	}

	public void removeFromBDD(String unbanReason, String unbanName) {
		try
		{
			if (BungeeGuard.plugin.sql.getConnection().isClosed())
			{
				BungeeGuard.plugin.sql.open();
			}

			if(BungeeGuard.plugin.sql.getConnection() == null)
			{
				System.out.println("[MYSQL] Connection error ...");
			}

			BungeeGuard.plugin.sql.query("UPDATE `BungeeGuard_Mute` SET status='0', unmuteReason='"+unbanReason+"', unmuteName='"+unbanName+"' WHERE uuidMute='"+getUUID()+"' AND status='1'");
			BungeeGuard.mutes.remove(this);
		}
		catch (final SQLException ex)
		{
			System.out.println("SQL problem (exception) when remove mute player to BDD : " + ex );
		}
		finally
		{
			try
			{
				if (!BungeeGuard.plugin.sql.getConnection().isClosed())
				{
					BungeeGuard.plugin.sql.close();
				}
			}
			catch (SQLException ex)
			{
				System.out.println(ex);
			}
		}
	}
}
