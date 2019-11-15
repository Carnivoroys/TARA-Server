package ee.ria.sso.config.mobileid;

import ee.sk.mid.MidDisplayTextFormat;
import ee.sk.mid.MidHashType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

@Component
@ConditionalOnProperty("mobile-id.enabled")
@Configuration
@ConfigurationProperties(prefix = "mobile-id")
@Validated
@Getter
@Setter
@Slf4j
@ToString
public class MobileIDConfigurationProvider {

    private static final String DEFAULT_COUNTRY_CODE = "EE";
    private static final String DEFAULT_LANGUAGE = "EST";
    private static final String DEFAULT_AREA_CODE = "+372";

    private static final MidDisplayTextFormat DEFAULT_MESSAGE_TO_DISPLAY_ENCODING = MidDisplayTextFormat.GSM7;
    private static final MidHashType DEFAULT_AUTHENTICATION_HASH_TYPE = MidHashType.SHA256;

    private static final int DEFAULT_SESSION_STATUS_SOCKET_OPEN_DURATION = 1000;
    private static final int DEFAULT_TIMEOUT_BETWEEN_SESSION_STATUS_QUERIES = 5000;
    private static final int DEFAULT_READ_TIMEOUT = 30000;
    private static final int DEFAULT_CONNECTION_TIMEOUT = 5000;
    private static final int CONNECTION_DURATION_MARGIN = 1500;

    private boolean enabled;
    private boolean useDdsService = true;

    @NotBlank
    private String hostUrl;

    @NotBlank
    private String countryCode = DEFAULT_COUNTRY_CODE;

    @NotBlank
    private String language = DEFAULT_LANGUAGE;

    @NotBlank
    private String areaCode = DEFAULT_AREA_CODE;

    private String serviceName;

    @NotBlank
    private String messageToDisplay;

    @NotNull
    private MidDisplayTextFormat messageToDisplayEncoding = DEFAULT_MESSAGE_TO_DISPLAY_ENCODING;

    @NotNull
    private MidHashType authenticationHashType = DEFAULT_AUTHENTICATION_HASH_TYPE;

    private String relyingPartyUuid;
    private String relyingPartyName;

    @NotNull
    private Integer sessionStatusSocketOpenDuration = DEFAULT_SESSION_STATUS_SOCKET_OPEN_DURATION;

    @NotNull
    private Integer timeoutBetweenSessionStatusQueries = DEFAULT_TIMEOUT_BETWEEN_SESSION_STATUS_QUERIES;

    @NotNull
    private Integer readTimeout = DEFAULT_READ_TIMEOUT;

    @NotNull
    private Integer connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;

    @PostConstruct
    public void init() {
        if (connectionTimeout < (sessionStatusSocketOpenDuration + CONNECTION_DURATION_MARGIN)) {
            throw new IllegalArgumentException(
                    "Network connection timeout(<" + connectionTimeout + ">) should not be shorter than sum of session status check socket open" +
                            " duration(<" + sessionStatusSocketOpenDuration + ">) and connection duration margin (<" + CONNECTION_DURATION_MARGIN + ">)");
        }

        if (!useDdsService) {
            if (StringUtils.isBlank(relyingPartyUuid)) {
                throw new IllegalArgumentException("'mobile-id.relying-party-uuid' cannot be blank when using MID-REST protocol ('mobile-id.use-dds-service=false')");
            }
            if (StringUtils.isBlank(relyingPartyName)) {
                throw new IllegalArgumentException("'mobile-id.relying-party-name' cannot be blank when using MID-REST protocol ('mobile-id.use-dds-service=false')");
            }
        }

        if (sessionStatusSocketOpenDuration < DEFAULT_SESSION_STATUS_SOCKET_OPEN_DURATION) {
            sessionStatusSocketOpenDuration = DEFAULT_SESSION_STATUS_SOCKET_OPEN_DURATION;
        }

        if (useDdsService && StringUtils.isBlank(serviceName)) {
            throw new IllegalArgumentException("'mobile-id.service-name' cannot be blank, if DDS is used as Mobile-ID service ('mobile-id.use-dds-service=true' or not present)");
        }

        sessionStatusSocketOpenDuration = sessionStatusSocketOpenDuration / 1000;

        log.info("Using Mobile-ID configuration: {}" + this);
    }
}
