package com.tactile.tact.services.accounts;

public class LocalOnboardingState {
    static final String TAG = "LocalOnboardingState";

    public enum TactUserLocalOnboardingState {
        None,
        Done,
        WaitingForServer,
        LocalSetUp,
        Salesforce,
        EmailSetUp,
        Blocked,
        UsernamePassword,
        DownloadingDB
    }

    LocalOnboardingState() {
        LocalOnboardingState.currentState = TactUserLocalOnboardingState.None;
    }

    private static TactUserLocalOnboardingState currentState;
    
    public static void setCurrentLocalOnboardingState(TactUserLocalOnboardingState state) {
        currentState = state;
    }

    public static TactUserLocalOnboardingState getCurrentLocalOnboardingState() {
    	return LocalOnboardingState.currentState;
    }
}
