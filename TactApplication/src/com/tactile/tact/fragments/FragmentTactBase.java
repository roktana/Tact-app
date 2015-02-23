package com.tactile.tact.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.tactile.tact.activities.TactApplication;
import com.tactile.tact.database.model.FragmentMoveHandler;

import de.greenrobot.event.EventBus;

/*
 * Base Fragment for HomeActivity screens
 */
public abstract class FragmentTactBase extends Fragment {

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View getView() {
        return super.getView();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        //EventBus.getDefault().register(this);
        EventBus.getDefault().registerSticky(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public void setBackStack(FragmentMoveHandler fragmentMoveHandler) {
        TactApplication.getInstance().setFragmentBackTack(fragmentMoveHandler);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        tryRemoveKeyboard();
    }

    protected void tryRemoveKeyboard() {
        View v = getActivity().getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }

    public void onEvent(Object object){}
}
