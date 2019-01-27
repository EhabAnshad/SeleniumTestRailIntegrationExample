package SeleniumTestRailMaven.SeleniumTestRailMaven;

import org.testng.Assert;
import org.testng.annotations.Test;

import Utilities.TestBase;
import Utilities.TestcaseId;

public class TestCase extends TestBase {
	
	 @Test
	 @TestcaseId("1")
	 public void TestPass() {
		 Assert.assertEquals(true, false);
		 
	 }
	 
	 

}
