package com.rimidalv111.googleindexer;

public class Account
{
	private boolean blacklisted = false;
	private String bl_reason = "N/A";
	private String email;
	private String password;

	public boolean isBlacklisted()
	{
		return blacklisted;
	}

	public void setBlacklisted(boolean blacklisted)
	{
		this.blacklisted = blacklisted;
	}

	public String getBl_reason()
	{
		return bl_reason;
	}

	public void setBl_reason(String bl_reason)
	{
		this.bl_reason = bl_reason;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

}
