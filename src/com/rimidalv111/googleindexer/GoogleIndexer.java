package com.rimidalv111.googleindexer;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class GoogleIndexer extends JFrame
{
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField captchaAPIKeyField;
	private JScrollPane GoogleAccountsScrollPane;
	private JTextArea googleAccountsTextArea;
	private JLabel captchaAPIlbl;
	private JLabel GoogleAccountsLabel;
	private JLabel lblUrls;
	private JScrollPane URLSScrollPane;
	private JTextArea URLSTextArea;
	private JButton startButton;
	private JButton stopButton;

	private BrowserHandler browserHandler;

	private JLabel accountsCount;
	private JLabel urlsCount;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					GoogleIndexer frame = new GoogleIndexer();
					frame.setVisible(true);
				} catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GoogleIndexer()
	{
		System.setProperty("webdriver.chrome.driver", "./src/com/rimidalv111/browser/chromedriver.exe");

		setTitle("Google Indexer [^]");
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		URL iconURL = getClass().getResource("favicon.png");
		ImageIcon icon = new ImageIcon(iconURL);
		setIconImage(icon.getImage());

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 345, 480);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		captchaAPIlbl = new JLabel("2Captcha API:");
		captchaAPIlbl.setBounds(10, 11, 87, 14);
		contentPane.add(captchaAPIlbl);

		captchaAPIKeyField = new JTextField();
		captchaAPIKeyField.setText("eb7c74f7d9f86842117cc0af7aa4324b");
		captchaAPIKeyField.setBackground(SystemColor.window);
		captchaAPIKeyField.setBounds(97, 9, 232, 18);
		captchaAPIKeyField.setBorder(new EmptyBorder(2, 8, 2, 8));
		captchaAPIKeyField.setFont(new Font("Monospaced", Font.PLAIN, 11));
		contentPane.add(captchaAPIKeyField);
		captchaAPIKeyField.setColumns(20);

		GoogleAccountsScrollPane = new JScrollPane();
		GoogleAccountsScrollPane.setBounds(10, 56, 319, 66);
		contentPane.add(GoogleAccountsScrollPane);

		googleAccountsTextArea = new JTextArea();
		googleAccountsTextArea.setText("rimidalv111@live.com:M0narch71!");
		googleAccountsTextArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
		googleAccountsTextArea.setBackground(SystemColor.window);
		GoogleAccountsScrollPane.setViewportView(googleAccountsTextArea);
		googleAccountsTextArea.setToolTipText("username:password");
		googleAccountsTextArea.setBorder(new EmptyBorder(5, 5, 5, 5));

		GoogleAccountsLabel = new JLabel("Google Accounts:");
		GoogleAccountsLabel.setBounds(10, 35, 276, 14);
		contentPane.add(GoogleAccountsLabel);

		lblUrls = new JLabel("URLs:");
		lblUrls.setBounds(10, 133, 319, 14);
		contentPane.add(lblUrls);

		URLSScrollPane = new JScrollPane();
		URLSScrollPane.setBounds(10, 158, 319, 154);
		contentPane.add(URLSScrollPane);

		URLSTextArea = new JTextArea();
		URLSTextArea.setText("http://www.yahoo.com/someurls");
		URLSTextArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
		URLSTextArea.setBackground(SystemColor.window);
		URLSTextArea.setBorder(new EmptyBorder(5, 5, 5, 5));
		URLSScrollPane.setViewportView(URLSTextArea);

		startButton = new JButton("Start");
		startButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(!running)
				{
					System.out.println("Starting Google Indexer...");
					running = true;
					browserHandler.start();
				} else
				{
					System.out.println("Google Indexer Already Running...");
				}
			}
		});
		startButton.setForeground(new Color(192, 192, 192));
		startButton.setBackground(new Color(0, 128, 0));
		startButton.setBounds(141, 323, 89, 23);
		contentPane.add(startButton);

		stopButton = new JButton("Stop");
		stopButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(running)
				{
					System.out.println("Stopping Google Indexer...");
					running = false;
					browserHandler.stop();
				} else
				{
					System.out.println("Google Indexer Not Running...");
				}
			}
		});
		stopButton.setForeground(new Color(192, 192, 192));
		stopButton.setBackground(new Color(128, 0, 0));
		stopButton.setBounds(240, 323, 89, 23);
		contentPane.add(stopButton);

		//count labels
		accountsCount = new JLabel("");
		accountsCount.setBounds(10, 316, 89, 23);
		contentPane.add(accountsCount);

		urlsCount = new JLabel("");
		urlsCount.setBounds(10, 330, 89, 23);
		contentPane.add(urlsCount);
		
		//progress bars
		
		mainBarLabel = new JLabel("");
		mainBarLabel.setBounds(15, 400, 303, 25);
		contentPane.add(mainBarLabel);
		
		mainProgressBar = new JProgressBar();
		mainProgressBar.setBounds(10, 400, 318, 25);
		contentPane.add(mainProgressBar);
		
		currentBarLabel = new JLabel("");
		currentBarLabel.setBounds(15, 360, 303, 25);
		contentPane.add(currentBarLabel);
		
		currentBrowserBar = new JProgressBar();
		currentBrowserBar.setBounds(10, 360, 318, 25);
		contentPane.add(currentBrowserBar);
		
		browserHandler = new BrowserHandler(this);
		
		updateMainBar("Testing main bar loading");
		updateCurrentBar("Testing current bar loading");
	}

	private JLabel mainBarLabel;
	private JProgressBar mainProgressBar;
	private JLabel currentBarLabel;
	private JProgressBar currentBrowserBar;
	
	public void updateMainBar(String s)
	{
		mainProgressBar.setMinimum(0);
		mainProgressBar.setMaximum(browserHandler.getUrls().size());
		
		mainProgressBar.setValue(browserHandler.getCompletedUrls().size());
		
		mainBarLabel.setText(s);
		mainBarLabel.revalidate();
		mainBarLabel.repaint();
	}
	
	public void updateCurrentBar(String s)
	{
		currentBrowserBar.setMinimum(0);
		currentBrowserBar.setMaximum(browserHandler.getUrls().size());
		
		//currentBrowserBar.setValue(browserHandler.getCompletedUrls().size());
		
		currentBarLabel.setText(s);
		currentBarLabel.revalidate();
		currentBarLabel.repaint();
	}
	
	public boolean running = false;

	public JTextField getCaptchaAPIKeyField()
	{
		return captchaAPIKeyField;
	}

	public JTextArea getGoogleAccountsTextArea()
	{
		return googleAccountsTextArea;
	}

	public JTextArea getURLSTextArea()
	{
		return URLSTextArea;
	}

	public boolean isRunning()
	{
		return running;
	}

	public void setCaptchaAPIKeyField(JTextField captchaAPIKeyField)
	{
		this.captchaAPIKeyField = captchaAPIKeyField;
	}

	public void setURLSTextArea(JTextArea uRLSTextArea)
	{
		URLSTextArea = uRLSTextArea;
	}

	public void setRunning(boolean running)
	{
		this.running = running;
	}

	public JLabel getAccountsCount()
	{
		return accountsCount;
	}

	public JLabel getUrlsCount()
	{
		return urlsCount;
	}

	public void setAccountsCount(JLabel accountsCount)
	{
		this.accountsCount = accountsCount;
	}

	public void setUrlsCount(JLabel urlsCount)
	{
		this.urlsCount = urlsCount;
	}
}
