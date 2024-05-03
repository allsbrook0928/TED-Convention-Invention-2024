package com.rechs.turtleapp;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.BuildConfig;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SensorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SensorFragment extends Fragment {
    private Handler mainHandler = new Handler();

    private SharedViewModel viewModel;

    private TextView mTextViewTemperature;
    private TextView mTextviewHumidity;
    private TextView mTextViewDecibels;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SensorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SensorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SensorFragment newInstance(String param1, String param2) {
        SensorFragment fragment = new SensorFragment();
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

        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sensor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Activity act = requireActivity();
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        mTextViewTemperature = view.findViewById(R.id.textview_temperature_text);
        mTextviewHumidity = view.findViewById(R.id.textview_humidity_text);
        mTextViewDecibels = view.findViewById(R.id.textview_vibration_text);


        /**** Setting observers to update text when new info arrives *****/
        viewModel.getTemperatureReading().observe(getViewLifecycleOwner(), item -> {
            mTextViewTemperature.setText(String.format(Locale.US, "%.1f°F", item));
        });

        viewModel.getHumidityReading().observe(getViewLifecycleOwner(), item -> {
            mTextviewHumidity.setText(String.format(Locale.US, "%3d%%", item));
        });

        viewModel.getDecibelReading().observe(getViewLifecycleOwner(), item -> {
            mainHandler.post(() -> {
                mTextViewDecibels.setText(String.format(Locale.US, "%3d%%", item));
            });
        });

    }
}