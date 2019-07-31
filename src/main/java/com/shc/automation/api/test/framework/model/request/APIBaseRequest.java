package com.shc.automation.api.test.framework.model.request;

import java.io.Serializable;

public class APIBaseRequest implements Serializable {
    protected String testName;
    protected String dataEnvironment;
    protected String reportFormat = "html";
    protected Integer invocationCount = 1;
    protected Integer threadPoolSize = -1;

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getDataEnvironment() {
        return dataEnvironment;
    }

    public void setDataEnvironment(String dataEnvironment) {
        this.dataEnvironment = dataEnvironment;
    }

    public String getReportFormat() {
        return reportFormat;
    }

    public void setReportFormat(String reportFormat) {
        this.reportFormat = reportFormat;
    }

    public Integer getInvocationCount() {
        return invocationCount;
    }

    public void setInvocationCount(Integer invocationCount) {
        this.invocationCount = invocationCount;
    }

    public Integer getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(Integer threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    public TestType getTestType() {
        return TestType.standalone;
    }
}
