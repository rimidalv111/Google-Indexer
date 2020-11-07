package com.rimidalv111.googleindexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import com.twocaptcha.api.TwoCaptchaService;

public class BrowserThread extends Thread
{
	private BrowserHandler instance;
	private WebDriver driver;
	private Account googleAccount;
	private String siteKey = "";
	private String pageURL = "";

	public BrowserThread(BrowserHandler bhi, Account ac)
	{
		instance = bhi;
		googleAccount = ac;
		System.out.println("Initializing browser thread...");
	}

	public void run()
	{
		try
		{
			setUpBrowser();
		} catch(Exception io)
		{
			io.printStackTrace();
		}
	}

	public void setUpBrowser()
	{
		try
		{
			launchBrowser();

			//wait 3 seconds
			Thread.sleep(3000);

			//go to submit url login url
			navigateToLoginPage();

			//wait 1 seconds
			Thread.sleep(2000);

			//log us in
			if(login())
			{
				//do next step
				checkLoginContinue();
			} else
			{
				//login failed destroy thread and continue with new account
				//Wrong xpaths?
				System.out.println("Killed thread... (failed login)");
				killThread();
			}
		} catch(Exception io)
		{

		}
	}

	public void checkLoginContinue()
	{
		if(loginSuccess())
		{
			//if login was a success lets send a url to be indexed
			WebElement elementUrlField = driver.findElement(By.xpath("//*[@id='input-url']/div/input"));
			elementUrlField.sendKeys(instance.getUrls().get(0)); //change urls here

			//get ready for response
			buildResponse();

			getCaptchaResponse();

		} else
		{
			System.out.println("Login failed: " + googleAccount.getEmail() + " was blacklisted...");
			for(Account ac : instance.getAccounts())
			{
				if(ac.getEmail().equalsIgnoreCase(googleAccount.getEmail()))
				{
					ac.setBlacklisted(true);
					ac.setBl_reason("Failed Login");
				}
			}
			System.out.println("Killed thread...");
			killThread();
		}
	}

	public void getCaptchaResponse()
	{
		//get the site key and page url
		if(getSiteKey() && getPageUrl())
		{
			//ready to send to 2captcha
			TwoCaptchaService service = new TwoCaptchaService(instance.getCaptchaKey(), siteKey, pageURL);

			try
			{
				String responseToken = service.solveCaptcha();
				System.out.println("The response token is: " + responseToken);

				if(responseToken.contains("ERROR"))
				{
					//captcha error, cant be solved, wrong login, etc..
					System.out.println("Error Returned... Failed to Solve reCaptcha");
					
					//go to submit page
					navigateToSubmitPage();
					
					//sleep 2
					Thread.sleep(2000);
					
					//work page
					checkLoginContinue();
				} else
				{
					fillResponseAndSubmit(responseToken);
				}

			} catch(InterruptedException e)
			{
				System.out.println("ERROR case 1");
				e.printStackTrace();
			} catch(IOException e)
			{
				System.out.println("ERROR case 2");
				e.printStackTrace();
			}

		}
	}

	public void getGoogleAccount()
	{
		//prelaunch check and initialization
		googleAccount = instance.getWhiteListedAccount();
		if(googleAccount == null)
		{
			System.out.println("No whitelisted account found... Add more google accounts!");
			killThread();
		}
	}

	public void launchBrowser()
	{
		driver = new ChromeDriver();
		//driver.manage().window().setSize(new Dimension(600, 500));
	}

	public void navigateToLoginPage()
	{
		//go to submit url login url
		driver.get("https://accounts.google.com/ServiceLogin?service=sitemaps&passive=1209600&continue=https://www.google.com/webmasters/tools/submit-url&followup=https://www.google.com/webmasters/tools/submit-url#identifier");
	}

	public void navigateToSubmitPage()
	{
	    String originalHandle = driver.getWindowHandle();

		Actions newTab = new Actions(driver);
	    newTab.sendKeys(Keys.CONTROL + "t").perform();
		driver.get("https://www.google.com/webmasters/tools/submit-url");

	    for(String handle : driver.getWindowHandles()) {
	        if (!handle.equals(originalHandle)) {
	            driver.switchTo().window(handle);
	            driver.close();
	        }
	    }

	    driver.switchTo().window(originalHandle);
	    
	}
	
	public boolean login()
	{
		try
		{
			//find and fillout email field
			WebElement elementEmail = driver.findElement(By.xpath("//*[@id='identifierId']"));

			Thread.sleep(2000);
			
			elementEmail.sendKeys(googleAccount.getEmail());

			Thread.sleep(2000);
			
			//click next
			WebElement elementNext = driver.findElement(By.xpath("//*[@id='identifierNext']/content/span"));
			elementNext.click();

			//wait 3 seconds for page load
			Thread.sleep(5000);

			//find and fill out password field
			WebElement elementPassword = driver.findElement(By.xpath("//*[@id='password']/div[1]/div/div[1]/input"));
			
			Thread.sleep(2000);
			
			elementPassword.sendKeys(googleAccount.getPassword());

			//wait 2 second
			Thread.sleep(2000);

			//click login
			WebElement elementLogin = driver.findElement(By.xpath("//*[@id='passwordNext']/content/span"));

			elementLogin.click();
			
			//wait 2 second
//			Thread.sleep(1000);
			
//			driver.get("https://www.google.com/webmasters/tools/submit-url");
//			//figure out how to fix glich here
//			//wait 2 second
			Thread.sleep(2000);
			((JavascriptExecutor)driver).executeScript("window.open()");
			
			Thread.sleep(2000);

			ArrayList<String> tabs = new ArrayList<String> (driver.getWindowHandles());

			driver.switchTo().window(tabs.get(1));
			driver.get("https://www.google.com/webmasters/tools/submit-url");

			
//			//wait 2 second
//			Thread.sleep(2000);
//			driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL + "w");

		
//			
//			elementLogin.click();

			return true;

		} catch(Exception io)
		{
			System.out.println("Login Failed... (missing a login element?)");
			return false;
		}
	}

	public boolean loginSuccess()
	{
		//did login fail?
		if(driver.getCurrentUrl().startsWith("https://www.google.com/webmasters/tools/submit-url"))
		{
			WebElement elementImnorobotCheck = driver.findElement(By.xpath("//*[@id='recaptcha-anchor']/div[5]"));
			elementImnorobotCheck.click();
			
			return true;
		} else
		{
			System.out.println("Could not verify submit page...");
			return false;
		}
	}

	public boolean getSiteKey()
	{
		String rawSiteKey = "";

		for(String line : driver.getPageSource().toString().split("\\n"))
		{
			if(line.contains("sitekey"))
			{
				rawSiteKey = line;
			}
		}

		//          'sitekey' : "6LfLgwgTAAAAAFgpAIOgNmfzKi5Ko2ZnNeIE2uLR"
		siteKey = rawSiteKey.substring(23, rawSiteKey.length() - 1);
		System.out.println("SITE KEY: " + siteKey);

		if(rawSiteKey.isEmpty())
		{
			return false;
		} else
		{
			return true;
		}
	}

	public boolean getPageUrl()
	{
		pageURL = driver.getCurrentUrl();
		System.out.println("PAGE URL: " + pageURL);
		if(pageURL.isEmpty())
		{
			return false;
		} else
		{
			return true;
		}
	}

	public void buildResponse()
	{
		//String textarea = "<textarea id='g-recaptcha-response' name='g-recaptcha-response' class='g-recaptcha-response' style='width: 250px; height: 40px; border: 1px solid rgb(193, 193, 193); margin: 10px 25px; padding: 0px; resize: none; '></textarea>";

		//*[@id="g-recaptcha-response"]

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("document.getElementById('g-recaptcha-response').setAttribute('style', 'width: 250px; height: 40px; border: 1px solid rgb(193, 193, 193); margin: 10px 25px; padding: 0px; resize: none;')");

	}

	public void fillResponseAndSubmit(String response)
	{
		try
		{
			System.out.println("Submitting recaptcha");
			WebElement elementResponse = driver.findElement(By.xpath("//*[@id='g-recaptcha-response']"));
			elementResponse.sendKeys(response);

			//wait 2 seconds
			Thread.sleep(2000);

			WebElement elementSubmit = driver.findElement(By.xpath("//*[@id='save-input-button']"));
			elementSubmit.click();
		} catch(Exception io)
		{
			System.out.println("Could not submit response.... (wrong xpath?)");
			killThread();
		}
	}

	public void killThread()
	{
		driver.close();
		this.interrupt();
	}
}
