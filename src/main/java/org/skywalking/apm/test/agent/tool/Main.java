package org.skywalking.apm.test.agent.tool;

import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.skywalking.apm.test.agent.tool.caseassert.assertor.DataAssert;
import org.skywalking.apm.test.agent.tool.caseassert.entity.Data;
import org.skywalking.apm.test.agent.tool.caseassert.exception.AssertFailedException;
import org.skywalking.apm.test.agent.tool.entity.Report;
import org.skywalking.apm.test.agent.tool.entity.Status;
import org.skywalking.apm.test.agent.tool.entity.TestCase;
import org.skywalking.apm.test.agent.tool.entity.TestCaseDesc;

public class Main {
    private static Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        // 校验参数
        logger.info("Begin to validate data.");
        String testDate = System.getProperty("testDate");
        String agentBranch = System.getProperty("agentBranch");
        String agentCommit = System.getProperty("agentCommit");
        String testCasePath = System.getProperty("testCasePath");
        String reportFilePath = System.getProperty("reportFilePath");
        Report report = new Report(testDate, agentBranch, agentCommit);

        File casesPath = new File(testCasePath);
        if (!casesPath.exists()) {
            report.setStatus(Status.ERROR);
        }

        // 校验所有插件
        File[] directories = new File(testCasePath).listFiles(file -> file.isDirectory() && !file.isHidden());

        for (File casePath : directories) {
            TestCaseDesc caseDesc = TestCaseDesc.Parser.parse(new File(casePath, "testcase.desc"));
            TestCase testCase = new TestCase(caseDesc.getCaseName(), caseDesc.getComponents());
            try {
                if (caseDesc.isValidate()) {
                    logger.info("start to assert data of test case[{}]", caseDesc.getCaseName());
                    File actualData = new File(casePath, "actualData.yaml");
                    File expectedData = new File(casePath, "expectedData.yaml");
                    if (actualData.exists() && expectedData.exists()) {
                        try {
                            DataAssert.assertEquals(Data.Loader.loadData(expectedData), Data.Loader.loadData(actualData));
                            testCase.setStatus(Status.PASSING);
                        } catch (AssertFailedException e) {
                            logger.error("assert failed.\n{}", e.getMessage());
                            testCase.setStatus(Status.FAILED);
                        }
                    } else {
                        logger.error("assert failed. because actual data {} and expected data {}", actualData.exists() ? "founded" : "not founded", actualData.exists() ? "founded" : "not founded");
                    }
                }
            } catch (Exception e) {
                logger.error("assert test case {} failed.", caseDesc.getCaseName(), e);
                testCase.setStatus(Status.ERROR);
            }

            if (testCase.faileOrError()) {
                report.setStatus(Status.FAILED);
            }

            report.addTestCase(testCase);
        }

        //生成报告
        try {
            report.generate(reportFilePath);
        } catch (Exception e) {
            logger.error("Failed to generate report file", e);
        }
    }
}