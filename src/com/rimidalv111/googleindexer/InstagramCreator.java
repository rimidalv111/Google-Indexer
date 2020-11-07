package com.rimidalv111.googleindexer;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import com.twocaptcha.api.TwoCaptchaService;

public class InstagramCreator extends Thread
{
	private BrowserHandler instance;
	private WebDriver driver;
	private Account googleAccount;
	private String siteKey = "";
	private String pageURL = "";

	public InstagramCreator(BrowserHandler bhi, Account ac)
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

			//			//wait 1 seconds
			Thread.sleep(2000);

			signUp();

			//wait 5 seconds 
			Thread.sleep(5000);
			
			if(!successSubmit()) //if submit failed stop browser and ask for/get new ip
			{
				System.out.println("You Need To Change IP to Continue!");
				killThread();
			}
			
		} catch(Exception io)
		{

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

					//sleep 2
					Thread.sleep(2000);

					//work page
					//checkLoginContinue();
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
		//go to instagram url
		driver.get("https://www.instagram.com");

	}

	public void signUp()
	{
		try
		{
			//find and fillout email field
			WebElement elementEmail = driver.findElement(By.xpath("//*[@id='react-root']/section/main/article/div[2]/div[1]/div/form/div[2]/input"));

			Thread.sleep(1000);

			elementEmail.sendKeys("ag@aeropeans.com");

			Thread.sleep(1000);

			//find and fillout name field
			WebElement elementName = driver.findElement(By.xpath("//*[@id='react-root']/section/main/article/div[2]/div[1]/div/form/div[3]/input"));

			elementName.sendKeys("Freddrick Garbage");

			Thread.sleep(1000);

			//find and fillout username field
			WebElement elementUsername = driver.findElement(By.xpath("//*[@id='react-root']/section/main/article/div[2]/div[1]/div/form/div[4]/input"));

			elementUsername.sendKeys("Sgrambler18954");

			Thread.sleep(1000);

			//find and fillout password field
			WebElement elementFirstName = driver.findElement(By.xpath("//*[@id='react-root']/section/main/article/div[2]/div[1]/div/form/div[5]/input"));

			elementFirstName.sendKeys("M0narch71!");

			Thread.sleep(1000);

			//find and fillout firstname field
			WebElement elementSubmit = driver.findElement(By.xpath("//*[@id='react-root']/section/main/article/div[2]/div[1]/div/form/div[6]/span/button"));

			elementSubmit.click();

		} catch(Exception io)
		{
			io.printStackTrace();
		}
	}

	public boolean successSubmit()
	{
		//did url change to login url?
		if(driver.getCurrentUrl().toString().equalsIgnoreCase("https://www.instagram.com/"))
		{
			return false;
		} else
		{
			return true;
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
