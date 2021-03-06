package pl.aetas.android.smscode.smsprocessor;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import pl.aetas.android.smscode.exception.UnknownSenderException;
import pl.aetas.android.smscode.model.CodesRegularExpressions;
import pl.aetas.android.smscode.model.Sender;
import pl.aetas.android.smscode.parser.SMSCodeParser;
import pl.aetas.android.smscode.presenter.Clipboard;
import pl.aetas.android.smscode.presenter.SMSInfoPresenter;
import pl.aetas.android.smscode.resource.SendersResource;

public final class SMSProcessorFactory {
    private SMSProcessorFactory() {
        // private to enforce using static factory method
    }

    public static SMSProcessorFactory getInstance() {
        return new SMSProcessorFactory();
    }


    public SMSProcessor create(final Context context, final SendersResource sendersResource, final String senderName, final String smsBody) throws UnknownSenderException {
        final Sender sender = retrieveSender(sendersResource, senderName);
        final SMSCodeParser smsCodeParser = createSMSCodeParser(sender.getCodesRegularExpressions());
        final Clipboard clipboard = createClipboard(context);
        final SMSInfoPresenter smsInfoPresenter = createSMSInfoPresenter(clipboard, sender, smsBody, context);
        return new SMSProcessor(smsCodeParser, smsInfoPresenter);
    }

    private SMSInfoPresenter createSMSInfoPresenter(final Clipboard clipboard, final Sender sender, final String smsBody, final Context context) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final boolean autoSmsCodeCopy = preferences.getBoolean("autoSmsCodeCopy", true);
        final boolean presentSmsContent = preferences.getBoolean("presentSmsContent", true);

        if (autoSmsCodeCopy && presentSmsContent) {
            return SMSInfoPresenter.createPresenterWithAutoCopyAndFullSMSContent(context, clipboard, sender, smsBody);
        } else if (autoSmsCodeCopy && !presentSmsContent) {
            return SMSInfoPresenter.createPresenterWithAutoCopyAndBasicCodeInfo(context, clipboard, sender, smsBody);
        } else if (!autoSmsCodeCopy && presentSmsContent) {
            return SMSInfoPresenter.createPresenterWithAskingIfCopyAndFullSMSContent(context, clipboard, sender, smsBody);
        } else {
            return SMSInfoPresenter.createPresenterWithAskingIfCopyAndBasicCodeInfo(context, clipboard, sender, smsBody);
        }


    }

    private Clipboard createClipboard(final Context context) {
        return new Clipboard((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE));
    }

    private SMSCodeParser createSMSCodeParser(final CodesRegularExpressions codesRegularExpressions) {
        return new SMSCodeParser(codesRegularExpressions);
    }

    private Sender retrieveSender(final SendersResource sendersResource, final String senderName) throws UnknownSenderException {
        return sendersResource.getSenderBySenderId(senderName);
    }
}
