package Utilities;

import java.lang.annotation.Annotation;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import Utilities.TestRailController.ResultStatus;

public class TestBase 
{
	protected WebDriver driver;
	protected String baseUrl;
	protected TestRailController testrail;

	@BeforeSuite
	@Parameters({"URL" })
	public void initSuite(@Optional("http://www.google.com/") String URL) 
	{
		baseUrl = URL;
		testrail = new TestRailController();
		testrail.createRun();
	}

	@SuppressWarnings("deprecation")
	@BeforeTest
	@Parameters({ "browser" })
	public void getDriverForTest(@Optional("chrome") String WindowBrowser)
	{
		if (WindowBrowser.equalsIgnoreCase("firefox")) {
			driver = new FirefoxDriver();
		}
		else if (WindowBrowser.equalsIgnoreCase("chrome")) 
		{
			driver = new ChromeDriver();
		}
		else if (WindowBrowser.equalsIgnoreCase("ie")) 
		{
			DesiredCapabilities caps = DesiredCapabilities.internetExplorer();
			caps.setCapability(
			    InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
			    true);
			driver = new InternetExplorerDriver(caps);
		} 
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.navigate().to(baseUrl);
	}


	@AfterMethod
	public void logToTestRail(ITestResult result){
		
		Annotation annotation = result.getMethod().getConstructorOrMethod().getMethod().getAnnotation(TestcaseId.class);
		Integer testId = null; 
        if (null != annotation) {
            testId = Integer.parseInt(((TestcaseId) annotation).value()); 
        } 
		
        switch (result.getStatus())
        {
        case ITestResult.FAILURE:
			 testrail.addResult("My Comment", testId, ResultStatus.FAILED);
			 break;
        case ITestResult.SUCCESS:
        	testrail.addResult("My Comment", testId, ResultStatus.PASSED);
        	break;
        }
	}

	@AfterTest(alwaysRun=true)
	public void stopDriver() {
		driver.quit();
		testrail.closeRun();
	}

}