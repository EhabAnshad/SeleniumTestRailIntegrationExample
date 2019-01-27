package Utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.codepine.api.testrail.TestRail;
import com.codepine.api.testrail.model.Result;
import com.codepine.api.testrail.model.ResultField;
import com.codepine.api.testrail.model.Run;

public class TestRailController {

	private static TestRail testRailInstance;
	private static Run run;
	private final static String endPoint = "https://testapi.testrail.io/";
	private final static String username = "";
	private final static String password = "";
	private final static int projectId = 1;
	private final static int suiteId = 1;

	public static enum ResultStatus { 
        PASSED(1),
        BLOCKED(2),
        UNTESTED(3),
        RETEST(4),
        FAILED(5);

        private final int value; 

        ResultStatus(int val) { 
            value = val; 
        }

        public int getValue() { 
            return value; 
        }
    }
	
	public TestRailController() {
		if (testRailInstance == null) {
			testRailInstance = TestRail.builder(endPoint, username, password).build();
		}
	}

	public void createRun() {
		SimpleDateFormat format = new SimpleDateFormat("dd MMM yyy kk mm s");
		Date date = new Date();
		String dateString = format.format(date);
		String runName = "Automation " + dateString;
		try {
			run = new Run();
			run = testRailInstance.runs().add(projectId, run.setSuiteId(suiteId).setName(runName).setIncludeAll(false))
					.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/****
	 * 1 Passed 2 Blocked 3 Untested (not allowed when adding a result) 4 Retest 5
	 * Failed
	 * 
	 * @param comment
	 * @param caseId
	 * @param statusId
	 */
	public void addResult(String comment, int caseId, ResultStatus status) {
		// add the testcase to the newly created testrun
		updateRun(caseId);
		try {
			if (null != testRailInstance) {
				List<ResultField> customResultFields = testRailInstance.resultFields().list().execute();
				testRailInstance.results().addForCase(run.getId(), caseId,
						new Result().setStatusId(status.getValue()).setComment(comment), customResultFields).execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateRun(int caseId) {
		try {
			List<Integer> cases = run.getCaseIds();
			if (cases == null) {
				cases =  new ArrayList<Integer>();
			}
		    if (!cases.contains(caseId)) {
				cases.add(caseId);
				run.setCaseIds(cases);
				testRailInstance.runs().update(run).execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void closeRun() {
		try {
			testRailInstance.runs().close(run.getId()).execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
