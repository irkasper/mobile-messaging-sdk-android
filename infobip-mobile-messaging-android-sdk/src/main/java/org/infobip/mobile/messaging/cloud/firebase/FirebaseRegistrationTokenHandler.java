package org.infobip.mobile.messaging.cloud.firebase;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.infobip.mobile.messaging.MobileMessagingCore;
import org.infobip.mobile.messaging.cloud.RegistrationTokenHandler;
import org.infobip.mobile.messaging.logging.MobileMessagingLogger;
import org.infobip.mobile.messaging.platform.Broadcaster;
import org.infobip.mobile.messaging.util.StringUtils;

import java.io.IOException;

/**
 * @author sslavin
 * @since 03/09/2018.
 */
public class FirebaseRegistrationTokenHandler extends RegistrationTokenHandler {

    private static final String TAG = FirebaseRegistrationTokenHandler.class.getSimpleName();

    private final Broadcaster broadcaster;

    public FirebaseRegistrationTokenHandler(MobileMessagingCore mobileMessagingCore, Broadcaster broadcaster) {
        super(mobileMessagingCore);
        this.broadcaster = broadcaster;
    }

    public void handleNewToken(String senderId, String token) {
        MobileMessagingLogger.v(TAG, "RECEIVED TOKEN", token);
        broadcaster.tokenReceived(token);
        sendRegistrationToServer(token);
    }

    public void cleanupToken(String senderId) {
        if (StringUtils.isBlank(senderId)) {
            return;
        }

        try {
            FirebaseInstanceId.getInstance().deleteToken(senderId, FirebaseMessaging.INSTANCE_ID_SCOPE);
        } catch (IOException e) {
            MobileMessagingLogger.e(TAG, "Error while deleting token", e);
        }
    }

    public void acquireNewToken(String senderId) {
        try {
            String token = FirebaseInstanceId
                    .getInstance()
                    .getToken(senderId, FirebaseMessaging.INSTANCE_ID_SCOPE);
            handleNewToken(senderId, token);
        } catch (IOException e) {
            MobileMessagingLogger.e(TAG, "Error while acquiring token", e);
        }
    }
}
