package com.ibicha.webrtc;

import org.webrtc.SdpObserver;

/**
 * Created by bhadriche on 8/17/2017.
 */

public class SessionDescription extends org.webrtc.SessionDescription implements SdpObserver {
    public SessionDescription(Type type, String description) {
        super(type, description);
    }

    @Override
    public void onCreateSuccess(org.webrtc.SessionDescription sessionDescription) {

    }

    @Override
    public void onSetSuccess() {

    }

    @Override
    public void onCreateFailure(String s) {

    }

    @Override
    public void onSetFailure(String s) {

    }
}
