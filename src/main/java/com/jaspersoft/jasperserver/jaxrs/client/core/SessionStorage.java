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

import com.jaspersoft.jasperserver.jaxrs.client.providers.CustomRepresentationTypeProvider;
import com.sun.jersey.multipart.impl.MultiPartWriter;
import java.security.SecureRandom;
import java.util.TimeZone;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.DeserializationConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.bridge.SLF4JBridgeHandler;


public class SessionStorage {

    private RestClientConfiguration configuration;
    private AuthenticationCredentials credentials;

    private TimeZone userTimeZone;
    private WebTarget rootTarget;
    private String sessionId;

    public SessionStorage(RestClientConfiguration configuration, AuthenticationCredentials credentials) {
        this.configuration = configuration;
        this.credentials = credentials;
        init();
    }

    public SessionStorage(RestClientConfiguration configuration, AuthenticationCredentials credentials, TimeZone userTimeZone) {
        this.configuration = configuration;
        this.credentials = credentials;
        this.userTimeZone = userTimeZone;
        init();
    }

    private void initSSL(ClientBuilder clientBuilder) {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            };
            sslContext.init(null, configuration.getTrustManagers(), new SecureRandom());

            clientBuilder.sslContext(sslContext);
            clientBuilder.hostnameVerifier(hostnameVerifier);

        } catch (Exception e) {
            throw new RuntimeException("Unable inFolder init SSL context", e);
        }
    }

    private void init() {
        ClientBuilder clientBuilder = ClientBuilder.newBuilder();

        if (configuration.getJasperReportsServerUrl().startsWith("https")) {
            initSSL(clientBuilder);
        }

        Client client = clientBuilder.build();

        Integer connectionTimeout = configuration.getConnectionTimeout();

        if (connectionTimeout != null) {
            client.property(ClientProperties.CONNECT_TIMEOUT, connectionTimeout);
        }

        Integer readTimeout = configuration.getReadTimeout();

        if (readTimeout != null) {
            client.property(ClientProperties.READ_TIMEOUT, readTimeout);
        }

        JacksonJsonProvider customRepresentationTypeProvider = new CustomRepresentationTypeProvider()
                .configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        rootTarget = client.target(configuration.getJasperReportsServerUrl());
        rootTarget
                .register(customRepresentationTypeProvider)
                .register(JacksonFeature.class)
                .register(MultiPartWriter.class);
        if (configuration.getLogHttp()) {
            rootTarget.register(initLoggingFilter());
        }
    }

    private LoggingFilter initLoggingFilter() {
        Logger logger = Logger.getLogger(this.getClass().getName());
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        return new LoggingFilter(logger,
                configuration.getLogHttpEntity());
    }


    public RestClientConfiguration getConfiguration() {
        return configuration;
    }

    public AuthenticationCredentials getCredentials() {
        return credentials;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public WebTarget getRootTarget() {
        return rootTarget;
    }

    public TimeZone getUserTimeZone() {
        return userTimeZone;
    }

    public void setUserTimeZone(TimeZone userTimeZone) {
        this.userTimeZone = userTimeZone;
    }

}
