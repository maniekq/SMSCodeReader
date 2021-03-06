package pl.aetas.android.smscode.smsprocessor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import pl.aetas.android.smscode.parser.SMSCodeParser;
import pl.aetas.android.smscode.presenter.SMSInfoPresenter;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class SMSProcessorTest {

    public static final String SMS_BODY = "This is SMS body";
    //SUT
    private SMSProcessor smsProcessor;

    @Mock
    private SMSInfoPresenter smsInfoPresenter;

    @Mock
    private SMSCodeParser smsCodeParser;

    @Before
    public void setUp() {
        initMocks(this);
        smsProcessor = new SMSProcessor(smsCodeParser, smsInfoPresenter);
    }

    @Test
    public void shouldPresentInfoToUserWhenSMSIsSMSWithCode() throws Exception {
        when(smsCodeParser.checkIfBodyContainsCode(SMS_BODY)).thenReturn(true);
        when(smsCodeParser.retrieveCodeFromSMSBody(SMS_BODY)).thenReturn("123456");

        smsProcessor.processSMS(SMS_BODY);

        verify(smsInfoPresenter).presentInfoToUser("123456");
    }

    @Test
    public void shouldNotTryToPresentInfoToUserWhenSMSInNotSMSWithCode() throws Exception {
        when(smsCodeParser.checkIfBodyContainsCode(SMS_BODY)).thenReturn(false);
        smsProcessor.processSMS(SMS_BODY);
        verify(smsInfoPresenter, never()).presentInfoToUser(anyString());
    }
}




