package com.rechs.turtleapp;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ControllerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ControllerFragment extends Fragment {
    private SharedViewModel viewModel; // viewModel variable

    private Handler mainHandler = new Handler(); // Handler for background UI changes

    // Variables for the dpad views
    private View mViewUpDPad;
    private View mViewRightDPad;
    private View mViewDownDPad;
    private View mViewLeftDPad;

    private View mViewElectromagnetPowerToggle;
    private View mViewElectromagnetOuterCircle;
    private View mViewElectromagnetInnerRectangle;

    private SwitchCompat mSwitchController;

    private boolean isMagnetOn = false;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ControllerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ControllerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ControllerFragment newInstance(String param1, String param2) {
        ControllerFragment fragment = new ControllerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // Set up Timber
        if (BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_controller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up viewModel using MainActivity Scope
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Set up dpad views
        mViewUpDPad = view.findViewById(R.id.view_up_dpad);
        mViewRightDPad = view.findViewById(R.id.view_right_dpad);
        mViewDownDPad = view.findViewById(R.id.view_down_dpad);
        mViewLeftDPad = view.findViewById(R.id.view_left_dpad);

        // Set up electromagnet views
        mSwitchController = view.findViewById(R.id.switch_controller_toggle);
        mViewElectromagnetPowerToggle = view.findViewById(R.id.view_electromagnet_toggle);
        mViewElectromagnetOuterCircle = view.findViewById(R.id.view_electromagnet_toggle_outer);
        mViewElectromagnetInnerRectangle = view.findViewById(R.id.view_electromagnet_inner_light);

        /**
         * Set up onTouchListeners for all dpad views
         */
        // If pressed down, they update viewModel with action (forward, right, etc.)-(1,2,3,4)
        // When finger is lifted, updated viewModel with "stop" (0)
        mViewUpDPad.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    Timber.tag("DIRECTION").e("1");
                    viewModel.setDirectionNum(1);
                    return true;
                case MotionEvent.ACTION_UP:
                    Timber.tag("DIRECTION").e("0");
                    viewModel.setDirectionNum(0);
                    return true;
            }
            return v.performClick();
        });

        mViewRightDPad.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    Timber.tag("DIRECTION").e("2");
                    viewModel.setDirectionNum(2);
                    return true;
                case MotionEvent.ACTION_UP:
                    Timber.tag("DIRECTION").e("0");
                    viewModel.setDirectionNum(0);
                    return true;
            }
            return v.performClick();
        });

        mViewDownDPad.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    Timber.tag("DIRECTION").e("3");
                    viewModel.setDirectionNum(3);
                    return true;
                case MotionEvent.ACTION_UP:
                    Timber.tag("DIRECTION").e("0");
                    viewModel.setDirectionNum(0);
                    return true;
            }
            return v.performClick();
        });

        mViewLeftDPad.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    Timber.tag("DIRECTION").e("4");
                    viewModel.setDirectionNum(4);
                    return true;
                case MotionEvent.ACTION_UP:
                    Timber.tag("DIRECTION").e("0");
                    viewModel.setDirectionNum(0);
                    return true;
            }
            return v.performClick();
        });

        /**
         * Set up electromagnet listener
         */
        mSwitchController.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                mViewUpDPad.setOnTouchListener((v, event) -> {
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            Timber.tag("E-DIRECTION").e("11");
                            viewModel.setElectromagnetDirectionNum(11);
                            return true;
                        case MotionEvent.ACTION_UP:
                            Timber.tag("E-DIRECTION").e("10");
                            viewModel.setElectromagnetDirectionNum(10);
                            return true;
                    }
                    return v.performClick();
                });

                mViewRightDPad.setOnTouchListener((v, event) -> {
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            Timber.tag("E-DIRECTION").e("12");
                            viewModel.setElectromagnetDirectionNum(12);
                            return true;
                        case MotionEvent.ACTION_UP:
                            Timber.tag("E-DIRECTION").e("10");
                            viewModel.setElectromagnetDirectionNum(10);
                            return true;
                    }
                    return v.performClick();
                });

                mViewDownDPad.setOnTouchListener((v, event) -> {
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            Timber.tag("E-DIRECTION").e("13");
                            viewModel.setElectromagnetDirectionNum(13);
                            return true;
                        case MotionEvent.ACTION_UP:
                            Timber.tag("E-DIRECTION").e("10");
                            viewModel.setElectromagnetDirectionNum(10);
                            return true;
                    }
                    return v.performClick();
                });

                mViewLeftDPad.setOnTouchListener((v, event) -> {
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            Timber.tag("E-DIRECTION").e("14");
                            viewModel.setElectromagnetDirectionNum(14);
                            return true;
                        case MotionEvent.ACTION_UP:
                            Timber.tag("E-DIRECTION").e("10");
                            viewModel.setElectromagnetDirectionNum(10);
                            return true;
                    }
                    return v.performClick();
                });
            } else {
                mViewUpDPad.setOnTouchListener((v, event) -> {
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            Timber.tag("DIRECTION").e("1");
                            viewModel.setDirectionNum(1);
                            return true;
                        case MotionEvent.ACTION_UP:
                            Timber.tag("DIRECTION").e("0");
                            viewModel.setDirectionNum(0);
                            return true;
                    }
                    return v.performClick();
                });

                mViewRightDPad.setOnTouchListener((v, event) -> {
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            Timber.tag("DIRECTION").e("2");
                            viewModel.setDirectionNum(2);
                            return true;
                        case MotionEvent.ACTION_UP:
                            Timber.tag("DIRECTION").e("0");
                            viewModel.setDirectionNum(0);
                            return true;
                    }
                    return v.performClick();
                });

                mViewDownDPad.setOnTouchListener((v, event) -> {
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            Timber.tag("DIRECTION").e("3");
                            viewModel.setDirectionNum(3);
                            return true;
                        case MotionEvent.ACTION_UP:
                            Timber.tag("DIRECTION").e("0");
                            viewModel.setDirectionNum(0);
                            return true;
                    }
                    return v.performClick();
                });

                mViewLeftDPad.setOnTouchListener((v, event) -> {
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            Timber.tag("DIRECTION").e("4");
                            viewModel.setDirectionNum(4);
                            return true;
                        case MotionEvent.ACTION_UP:
                            Timber.tag("DIRECTION").e("0");
                            viewModel.setDirectionNum(0);
                            return true;
                    }
                    return v.performClick();
                });
            }
        });

        mViewElectromagnetPowerToggle.setOnClickListener(v -> {
            if(isMagnetOn){
                viewModel.setElectromagnetToggleNum(20);
                isMagnetOn = false;

                mainHandler.post(() -> {
                   mViewElectromagnetOuterCircle.setBackgroundResource(R.drawable.electromagnet_outer_circle_red);
                   mViewElectromagnetInnerRectangle.setBackgroundResource(R.drawable.electromagnet_inner_rectangle_red);
                });
            } else {
                viewModel.setElectromagnetToggleNum(21);
                isMagnetOn = true;

                mainHandler.post(() -> {
                    mViewElectromagnetOuterCircle.setBackgroundResource(R.drawable.electromagnet_outer_circle_green);
                    mViewElectromagnetInnerRectangle.setBackgroundResource(R.drawable.electromagnet_inner_rectangle_green);
                });
            }
        });

    }

}