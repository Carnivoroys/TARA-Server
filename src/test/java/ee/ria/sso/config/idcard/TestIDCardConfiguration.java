package ee.ria.sso.config.idcard;

import ee.ria.sso.config.TaraResourceBundleMessageSource;
import ee.ria.sso.service.idcard.OCSPValidator;
import ee.ria.sso.statistics.StatisticsHandler;
import ee.ria.sso.utils.X509Utils;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;
import java.util.Map;

@EnableConfigurationProperties
@ComponentScan(basePackages = {
        "ee.ria.sso.config.idcard",
        "ee.ria.sso.service.idcard"
})
@Configuration
@Import(value = {
        TaraResourceBundleMessageSource.class,
        StatisticsHandler.class
})
public class TestIDCardConfiguration {

    @Autowired
    private ResourceLoader resourceLoader;

    @Bean
    X509Certificate mockIDCardUserCertificate2015() throws CertificateException, IOException {
        return loadCertificate("classpath:id-card/47101010033(TEST_of_ESTEID-SK_2015).pem");
    }

    @Bean
    X509Certificate mockIDCardUserCertificate2015withoutAiaExtension() throws CertificateException, IOException {
        return loadCertificate("classpath:id-card/37101010021(TEST_of_ESTEID-SK_2015)-no_aia_extension.pem");
    }

    @Bean
    X509Certificate mockIDCardUserCertificate2018() throws CertificateException, IOException {
        return loadCertificate("classpath:id-card/38001085718(TEST_of_ESTEID2018).pem");
    }

    @Bean
    X509Certificate mockIDCardUserCertificate2011() throws CertificateException, IOException {
        return loadCertificate("classpath:id-card/48812040138(TEST_of_ESTEID-SK_2011).pem");
    }

    private X509Certificate loadCertificate(String resourcePath) throws CertificateException, IOException {
        Resource resource = resourceLoader.getResource(resourcePath);
        if (!resource.exists()) {
            throw new IllegalArgumentException("Could not find resource " + resourcePath);
        }

        try (InputStream inputStream = resource.getInputStream()) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return (X509Certificate) cf.generateCertificate(inputStream);
        }
    }

    @Bean
    @Primary
    @ConditionalOnProperty("id-card.enabled")
    OCSPValidator mockOCSPValidator() {
        OCSPValidator ocspValidator = Mockito.mock(OCSPValidator.class);
        return ocspValidator;
    }
}
