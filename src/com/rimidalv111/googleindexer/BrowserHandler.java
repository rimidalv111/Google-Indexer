package com.rimidalv111.googleindexer;

import java.util.ArrayList;

public class BrowserHandler
{
	public GoogleIndexer instance;

	private String captchaKey;
	private ArrayList<String> urls = new ArrayList<String>();
	private ArrayList<String> completedUrls = new ArrayList<String>();
	private ArrayList<Account> accounts = new ArrayList<Account>();

	public BrowserHandler(GoogleIndexer in)
	{
		instance = in;
	}

	public void start()
	{
		if(parseAccounts() && parseUrls() && parseCaptcha())
		{
			//update stats
			instance.getAccountsCount().setText("Accounts: " + accounts.size());
			instance.getAccountsCount().revalidate();
			instance.getAccountsCount().repaint();
			
			instance.getUrlsCount().setText("URLs: " + urls.size());
			instance.getUrlsCount().revalidate();
			instance.getUrlsCount().repaint();
			
			//start up the browser
			Account acc = getWhiteListedAccount();
			
			if(acc != null)
			{
				(new BrowserThread(this, getWhiteListedAccount())).start();
				//(new InstagramCreator(this, getWhiteListedAccount())).start();
			}

		} else
		{
			System.out.println("Please fill out fields accordingly...");
		}
	}

	public void stop()
	{
		urls.clear();
		accounts.clear();
	}

	public boolean parseAccounts()
	{
		try
		{
			for(String line : instance.getGoogleAccountsTextArea().getText().split("\\n"))
			{
				Account googleAccount = new Account();
				googleAccount.setEmail(line.split(":")[0]);
				googleAccount.setPassword(line.split(":")[1]);
				accounts.add(googleAccount);
			}
		} catch(Exception io)
		{
			System.out.println("Could not detect accounts...");
			//io.printStackTrace();
			return false;
		}

		System.out.println("Accounts Count: " + accounts.size());
		return true;
	}

	public Account getWhiteListedAccount()
	{
		for(Account account : accounts)
		{
			if(!account.isBlacklisted())
			{
				return account;
			}
		}
		return null;
	}
	
	public boolean parseUrls()
	{
		try
		{
			for(String line : instance.getURLSTextArea().getText().split("\\n"))
			{
				urls.add(line);
			}
		} catch(Exception io)
		{
			System.out.println("Could not detect urls...");
			//io.printStackTrace();
			return false;
		}

		System.out.println("URL's Count: " + urls.size());
		return true;
	}

	public boolean parseCaptcha()
	{
		try
		{
			captchaKey = instance.getCaptchaAPIKeyField().getText();
		} catch(Exception io)
		{
			System.out.println("Could not parse 2captcha api key...");
			//io.printStackTrace();
			return false;
		}
		return true;
	}

	public GoogleIndexer getInstance()
	{
		return instance;
	}

	public String getCaptchaKey()
	{
		return captchaKey;
	}

	public ArrayList<String> getUrls()
	{
		return urls;
	}

	public ArrayList<Account> getAccounts()
	{
		return accounts;
	}

	public void setInstance(GoogleIndexer instance)
	{
		this.instance = instance;
	}

	public void setCaptchaKey(String captchaKey)
	{
		this.captchaKey = captchaKey;
	}

	public void setUrls(ArrayList<String> urls)
	{
		this.urls = urls;
	}

	public void setAccounts(ArrayList<Account> accounts)
	{
		this.accounts = accounts;
	}

	public ArrayList<String> getCompletedUrls()
    {
	    return completedUrls;
    }

	public void setCompletedUrls(ArrayList<String> completedUrls)
    {
	    this.completedUrls = completedUrls;
    }
}
