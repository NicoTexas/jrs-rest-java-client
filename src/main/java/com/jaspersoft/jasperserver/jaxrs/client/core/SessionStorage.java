/*
* Copyright (C) 2005 - 2014 Jaspersoft Corporation. All rights  reserved.
* http://www.jaspersoft.com.
*
* Unless you have purchased  a commercial license agreement from Jaspersoft,
* the following license terms  apply:
*
* This program is free software: you can redistribute it and/or  modify
* it under the terms of the GNU Affero General Public License  as
* published by the Free Software Foundation, either version 3 of  the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero  General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public  License
* along with this program.&nbsp; If not, see <http://www.gnu.org/licenses/>.
*/

package com.jaspersoft.jasperserver.jaxrs.client.core;

import com.jaspersoft.jasperserver.dto.permissions.RepositoryPermission;
import com.jaspersoft.jasperserver.dto.reports.ReportExecutionRequest;
import com.jaspersoft.jasperserver.dto.reports.ReportParameters;
import com.jaspersoft.jasperserver.jaxrs.client.builder.jobs.JobsParameter;
import com.jaspersoft.jasperserver.jaxrs.client.builder.permissions.PermissionMask;
import com.jaspersoft.jasperserver.jaxrs.client.builder.reporting.ReportOutputFormat;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import com.jaspersoft.jasperserver.jaxrs.client.dto.jobs.calendars.WeeklyCalendar;
import com.jaspersoft.jasperserver.jaxrs.client.dto.jobs.reportjobmodel.ReportJobModel;
import com.jaspersoft.jasperserver.jaxrs.client.dto.jobs.wrappers.JobSummaryListWrapper;
import com.jaspersoft.jasperserver.jaxrs.client.dto.reports.ReportExecutionDescriptor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class SessionStorage {

    private RestClientConfiguration configuration;
    private AuthenticationCredentials credentials;
    private String sessionId;

    public RestClientConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(RestClientConfiguration configuration) {
        this.configuration = configuration;
    }

    public AuthenticationCredentials getCredentials() {
        return credentials;
    }

    public void setCredentials(AuthenticationCredentials credentials) {
        this.credentials = credentials;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        if (sessionId != null)
            this.sessionId = sessionId;
    }

    public static void main(String[] args) {
        RestClientConfiguration configuration = RestClientConfiguration.loadConfiguration("url.properties");


    }
}
