package com.jaspersoft.jasperserver.jaxrs.client.core;

import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberMatcher;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.powermock.api.easymock.PowerMock.*;
import static org.powermock.api.support.membermodification.MemberMatcher.field;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNull;

/**
 * Unit tests for {@link RestClientConfiguration}
 *
 * @author Alexander Krasnyanskiy
 */
@PrepareForTest({RestClientConfiguration.class, Properties.class, X509TrustManager.class})
public class RestClientConfigurationTest extends PowerMockTestCase {

    private Properties fakeProps = new Properties() {{
        setProperty("url", "http://localhost:8080/jasperserver-pro/");
        setProperty("contentMimeType", "JSON");
        setProperty("acceptMimeType", "JSON");
        setProperty("connectionTimeout", "");
        setProperty("readTimeout", "");
        setProperty("authenticationType", "REST");
        setProperty("restrictedHttpMethods", "false");
        setProperty("logHttpEntity", "false");
        setProperty("logHttp", "false");
    }};

    @Test(testName = "RestClientConfiguration_parametrized_constructor")
    public void should_invoke_default_constructor_and_pass_valid_URL_to_setJasperReportsServerUrl_method() throws Exception {
        String fakedValidUrl = "http://localhost:8080/jasperserver-pro/";
        RestClientConfiguration config = new RestClientConfiguration(fakedValidUrl);
        assertNotNull(config.getTrustManagers());
        assertTrue(config.getTrustManagers().length > 0);
        assertNotNull(config.getTrustManagers()[0]);
        assertEquals(config.getJasperReportsServerUrl(), fakedValidUrl);
        assertEquals(config.getAuthenticationType(), AuthenticationType.REST);
        assertFalse(config.getRestrictedHttpMethods());
        assertFalse(config.getLogHttp());
        assertFalse(config.getLogHttpEntity());
    }

    @Test(testName = "RestClientConfiguration_parametrized_constructor", expectedExceptions = IllegalArgumentException.class)
    public void should_throw_exception_when_pass_invalid_URL_to_the_constructor() {
        new RestClientConfiguration("invalidURL");
    }

    @Test(testName = "RestClientConfiguration_constructor_without_parameters")
    public void should_create_an_instance_of_RestClientConfiguration_but_without_setting_jasperReportsServerUrl() {
        RestClientConfiguration config = new RestClientConfiguration();
        assertNotNull(config.getTrustManagers());
        assertTrue(config.getTrustManagers().length > 0);
        assertNull(config.getJasperReportsServerUrl()); // URL must be null
    }

    @Test(testName = "loadConfiguration")
    public void should_load_configuration_from_property_file() throws Exception {

        // Given
        mockStaticPartial(RestClientConfiguration.class, "loadProperties");
        Method[] methods = MemberMatcher.methods(RestClientConfiguration.class, "loadProperties");

        expectPrivate(RestClientConfiguration.class, methods[0], "superCoolPath").andReturn(fakeProps);
        replay(RestClientConfiguration.class);

        // When
        RestClientConfiguration configuration = RestClientConfiguration.loadConfiguration("superCoolPath");

        //Then
        assertEquals(configuration.getJasperReportsServerUrl(), fakeProps.getProperty("url"));
        assertEquals(configuration.getContentMimeType().toString(), fakeProps.getProperty("contentMimeType"));
        assertEquals(configuration.getAcceptMimeType().toString(), fakeProps.getProperty("acceptMimeType"));
        assertEquals(configuration.getAuthenticationType().toString(), fakeProps.getProperty("authenticationType"));
        assertEquals(configuration.getRestrictedHttpMethods().toString(), fakeProps.getProperty("restrictedHttpMethods"));
        assertEquals(configuration.getLogHttp().toString(), fakeProps.getProperty("logHttp"));
        assertEquals(configuration.getLogHttpEntity().toString(), fakeProps.getProperty("logHttpEntity"));

        assertSame(configuration.getConnectionTimeout(), null);
        assertSame(configuration.getReadTimeout(), null);

        assertNotNull(configuration.getTrustManagers());
    }

    @Test(testName = "loadConfiguration")
    public void should_load_configuration_from_property_file_with_all_kind_of_setted_values() throws Exception {
        // Given
        Properties fakePropsWithNotEmptyConnectionTimeoutAndReadTimeout = new Properties() {{
            setProperty("url", "http://localhost:8080/jasperserver-pro/");
            setProperty("contentMimeType", "JSON");
            setProperty("acceptMimeType", "JSON");
            setProperty("connectionTimeout", "100");
            setProperty("readTimeout", "20");
            setProperty("authenticationType", "REST");
            setProperty("restrictedHttpMethods", "false");
            setProperty("logHttpEntity", "false");
            setProperty("logHttp", "false");
        }};
        mockStaticPartial(RestClientConfiguration.class, "loadProperties");
        Method[] methods = MemberMatcher.methods(RestClientConfiguration.class, "loadProperties");

        expectPrivate(RestClientConfiguration.class, methods[0], "superCoolPath").andReturn(fakePropsWithNotEmptyConnectionTimeoutAndReadTimeout);
        replay(RestClientConfiguration.class);

        // When
        RestClientConfiguration configuration = RestClientConfiguration.loadConfiguration("superCoolPath");

        // Then
        assertEquals(configuration.getJasperReportsServerUrl(), fakeProps.getProperty("url"));
        assertEquals(configuration.getContentMimeType().toString(), fakeProps.getProperty("contentMimeType"));
        assertEquals(configuration.getAcceptMimeType().toString(), fakeProps.getProperty("acceptMimeType"));
        assertSame(configuration.getConnectionTimeout(), Integer.valueOf(fakePropsWithNotEmptyConnectionTimeoutAndReadTimeout.getProperty("connectionTimeout"))); // should be 20
        assertSame(configuration.getReadTimeout(), Integer.valueOf(fakePropsWithNotEmptyConnectionTimeoutAndReadTimeout.getProperty("readTimeout"))); // should be 100

        assertEquals(configuration.getAuthenticationType().toString(), fakeProps.getProperty("authenticationType"));
        assertEquals(configuration.getRestrictedHttpMethods().toString(), fakeProps.getProperty("restrictedHttpMethods"));
        assertEquals(configuration.getLogHttp().toString(), fakeProps.getProperty("logHttp"));
        assertEquals(configuration.getLogHttpEntity().toString(), fakeProps.getProperty("logHttpEntity"));

        assertNotNull(configuration.getTrustManagers());
        assertEquals(configuration.getAuthenticationType(), AuthenticationType.REST);
        assertFalse(configuration.getRestrictedHttpMethods());
        assertFalse(configuration.getLogHttp());
        assertFalse(configuration.getLogHttpEntity());
    }

    @Test(testName = "setJrsVersion")
    public void should_set_setJrsVersion_field() throws IllegalAccessException {
        //given
        RestClientConfiguration config = new RestClientConfiguration();
        config.setJrsVersion(JRSVersion.v5_0_0);
        Field field = field(RestClientConfiguration.class, "jrsVersion");
        //when
        Object retrieved = field.get(config);
        //then
        assertNotNull(retrieved);
        assertEquals(retrieved, JRSVersion.v5_0_0);
    }

    @Test(testName = "setJrsVersion")
    public void should_get_not_null_setJrsVersion_field() throws IllegalAccessException {
        //given
        RestClientConfiguration config = new RestClientConfiguration();
        Field field = field(RestClientConfiguration.class, "jrsVersion");
        //when
        field.set(config, JRSVersion.v4_7_0);
        //then
        assertEquals(config.getJrsVersion(), JRSVersion.v4_7_0);
    }

    @Test(testName = "setTrustManagers")
    public void should_set_TrustManagers_with_proper_TrustManagers_object() throws Exception {
        // Given
        RestClientConfiguration config = new RestClientConfiguration();
        X509TrustManager expected = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };

        // When
        config.setTrustManagers(new TrustManager[]{expected});

        // Then
        assertNotNull(config.getTrustManagers()[0]);
        assertEquals(expected, config.getTrustManagers()[0]);
    }

    @Test
    public void should_invoke_private_method() throws Exception {
        //given
        Properties propertiesSpy = PowerMockito.spy(new Properties());

        propertiesSpy.setProperty("url", "http://localhost:8080/jasperserver-pro/");
        propertiesSpy.setProperty("contentMimeType", "JSON");
        propertiesSpy.setProperty("acceptMimeType", "JSON");
        propertiesSpy.setProperty("connectionTimeout", "");
        propertiesSpy.setProperty("readTimeout", "");

        PowerMockito.whenNew(Properties.class).withNoArguments().thenReturn(propertiesSpy);
        PowerMockito.doNothing().when(propertiesSpy).load(any(InputStream.class));
        PowerMockito.suppress(method(Properties.class, "load", InputStream.class));
        //when
        RestClientConfiguration retrieved = RestClientConfiguration.loadConfiguration("path");
        //then
        AssertJUnit.assertNotNull(retrieved);
        Mockito.verify(propertiesSpy, times(1)).load(any(InputStream.class));
    }

    @Test
    public void should_return_trusted_manager() throws Exception {
        //given
        RestClientConfiguration config = Mockito.spy(new RestClientConfiguration());
        //when
        TrustManager[] managers = config.getTrustManagers();
        //then
        assertNotNull(managers);
        assertTrue(managers.length == 1);

        ((X509TrustManager) managers[0]).checkClientTrusted(null, "abc");
        X509Certificate[] retrieved = ((X509TrustManager) managers[0]).getAcceptedIssuers();

        Assert.assertNull(retrieved);
    }


    @Test
    public void should_throw_an_exception_while_loading_props_from_wrong_path() throws Exception {
        try {
            RestClientConfiguration.loadConfiguration("path");
        } catch (Exception e) {
            assertNotNull(e);
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), null);
            assertEquals(e.getCause(), null);
        }
    }

    @Test
    public void should_throw_an_exception_with_wrong_contentMimetype() throws Exception {
        // Given
        Properties fakePropsWithUnproperContentMimeType = new Properties() {{
            setProperty("url", "http://localhost:8080/jasperserver-pro/");
            setProperty("contentMimeType", "UnacceptableContentMimeType");
        }};

        mockStaticPartial(RestClientConfiguration.class, "loadProperties");
        Method[] methods = MemberMatcher.methods(RestClientConfiguration.class, "loadProperties");

        expectPrivate(RestClientConfiguration.class, methods[0], "superCoolPath").andReturn(fakePropsWithUnproperContentMimeType);
        replay(RestClientConfiguration.class);

        // When

        try {
            RestClientConfiguration.loadConfiguration("superCoolPath");
        } catch (Exception e) {
            assertNotNull(e);
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), null);
            assertEquals(e.getCause(), null);
        }
    }

    @Test
    public void should_throw_an_exception_with_wrong_acceptMimetype() throws Exception {
        // Given
        Properties fakePropsWithUnproperAcceptMimeType = new Properties() {{
            setProperty("url", "http://localhost:8080/jasperserver-pro/");
            setProperty("acceptMimeType", "UnacceptableAcceptMimeType");
        }};

        mockStaticPartial(RestClientConfiguration.class, "loadProperties");
        Method[] methods = MemberMatcher.methods(RestClientConfiguration.class, "loadProperties");

        expectPrivate(RestClientConfiguration.class, methods[0], "superCoolPath").andReturn(fakePropsWithUnproperAcceptMimeType);
        replay(RestClientConfiguration.class);

        // When

        try {
            RestClientConfiguration.loadConfiguration("superCoolPath");
        } catch (Exception e) {
            assertNotNull(e);
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), null);
            assertEquals(e.getCause(), null);
        }
    }

    @Test
    public void should_throw_an_exception_with_wrong_jrs_version() throws Exception {
        // Given
        Properties fakePropsWithUnproperJrsVersion = new Properties() {{
            setProperty("url", "http://localhost:8080/jasperserver-pro/");
            setProperty("jrsVersion", "6.1");
        }};
        mockStaticPartial(RestClientConfiguration.class, "loadProperties");
        Method[] methods = MemberMatcher.methods(RestClientConfiguration.class, "loadProperties");

        expectPrivate(RestClientConfiguration.class, methods[0], "superCoolPath").andReturn(fakePropsWithUnproperJrsVersion);
        replay(RestClientConfiguration.class);

        // When

        try {
            RestClientConfiguration.loadConfiguration("superCoolPath");
        } catch (Exception e) {
            assertNotNull(e);
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), null);
            assertEquals(e.getCause(), null);
        }
    }

    @Test
    public void should_throw_an_exception_with_wrong_authintication_type() throws Exception {
        // Given
        Properties fakePropsWithUnproperJrsVersion = new Properties() {{
            setProperty("url", "http://localhost:8080/jasperserver-pro/");
            setProperty("authenticationType", "authenticationType");
        }};
        mockStaticPartial(RestClientConfiguration.class, "loadProperties");
        Method[] methods = MemberMatcher.methods(RestClientConfiguration.class, "loadProperties");

        expectPrivate(RestClientConfiguration.class, methods[0], "superCoolPath").andReturn(fakePropsWithUnproperJrsVersion);
        replay(RestClientConfiguration.class);

        // When

        try {
            RestClientConfiguration.loadConfiguration("superCoolPath");
        } catch (Exception e) {
            assertNotNull(e);
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), null);
            assertEquals(e.getCause(), null);
        }
    }

    @Test
    public void should_load_configuration_from_property_file_with_true_in_boolean_values() throws Exception {
        // Given
        Properties fakePropsWithUnproperJrsVersion = new Properties() {{
            setProperty("url", "http://localhost:8080/jasperserver-pro/");
            setProperty("restrictedHttpMethods", "true");
            setProperty("logHttpEntity", "true");
            setProperty("logHttp", "true");
        }};
        mockStaticPartial(RestClientConfiguration.class, "loadProperties");
        Method[] methods = MemberMatcher.methods(RestClientConfiguration.class, "loadProperties");

        expectPrivate(RestClientConfiguration.class, methods[0], "superCoolPath").andReturn(fakePropsWithUnproperJrsVersion);
        replay(RestClientConfiguration.class);

        // When
        RestClientConfiguration configuration = RestClientConfiguration.loadConfiguration("superCoolPath");
        assertTrue(configuration.getRestrictedHttpMethods());
        assertTrue(configuration.getLogHttp());
        assertTrue(configuration.getLogHttpEntity());


    }
}