package ee.ria.sso.flow.action;

import ee.ria.sso.authentication.AuthenticationType;
import ee.ria.sso.service.smartid.SmartIDAuthenticationService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

@ConditionalOnProperty("smart-id.enabled")
@Component
public class SmartIDCheckCancelAction extends AbstractAuthenticationAction {

    private final SmartIDAuthenticationService authenticationService;

    public SmartIDCheckCancelAction(SmartIDAuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    protected Event doAuthenticationExecute(RequestContext requestContext) {
        return authenticationService.cancelCheckSmartIdAuthenticationSessionStatus(requestContext);
    }

    @Override
    protected AuthenticationType getAuthenticationType() {
        return AuthenticationType.SmartID;
    }
}
