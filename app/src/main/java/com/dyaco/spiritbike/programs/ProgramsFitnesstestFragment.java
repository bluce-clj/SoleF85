package com.dyaco.spiritbike.programs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.dyaco.spiritbike.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProgramsFitnesstestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProgramsFitnesstestFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ProgramsFitnesstestFragment() {
        // Required empty public constructor
    }

    public static ProgramsFitnesstestFragment newInstance(String param1, String param2) {
        ProgramsFitnesstestFragment fragment = new ProgramsFitnesstestFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_programs_fitnesstest, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btAirForce_Programs = view.findViewById(R.id.btAirForce_Programs);
        Button btArmy_Programs = view.findViewById(R.id.btArmy_Programs);
        Button btCoastGuard_Programs = view.findViewById(R.id.btCoastGuard_Programs);
        Button btGerkin_Programs = view.findViewById(R.id.btGerkin_Programs);
        Button btPeb_Programs = view.findViewById(R.id.btPeb_Programs);
        Button btMarineCorps_Programs = view.findViewById(R.id.btMarineCorps_Programs);
        Button btNavy_Programs = view.findViewById(R.id.btNavy_Programs);

        btAirForce_Programs.setOnClickListener(btProgramsOnClick);
        btArmy_Programs.setOnClickListener(btProgramsOnClick);
        btCoastGuard_Programs.setOnClickListener(btProgramsOnClick);
        btGerkin_Programs.setOnClickListener(btProgramsOnClick);
        btPeb_Programs.setOnClickListener(btProgramsOnClick);
        btMarineCorps_Programs.setOnClickListener(btProgramsOnClick);
        btNavy_Programs.setOnClickListener(btProgramsOnClick);
    }

    private final View.OnClickListener btProgramsOnClick = view -> {

        Bundle bundle = new Bundle();

        switch (view.getId()) {
            case R.id.btAirForce_Programs:
                bundle.putInt("item", R.string.air_force);
                bundle.putInt("list", R.id.clListFitnessTests_Programs);
                break;
            case R.id.btArmy_Programs:
                bundle.putInt("item", R.string.army);
                bundle.putInt("list", R.id.clListFitnessTests_Programs);
                break;
            case R.id.btCoastGuard_Programs:
                bundle.putInt("item", R.string.coast_guard);
                bundle.putInt("list", R.id.clListFitnessTests_Programs);
                break;
            case R.id.btGerkin_Programs:
                bundle.putInt("item", R.string.gerkin);
                bundle.putInt("list", R.id.clListFitnessTests_Programs);
                break;
            case R.id.btPeb_Programs:
                bundle.putInt("item", R.string.peb);
                bundle.putInt("list", R.id.clListFitnessTests_Programs);
                break;
            case R.id.btMarineCorps_Programs:
                bundle.putInt("item", R.string.marine_corps);
                bundle.putInt("list", R.id.clListFitnessTests_Programs);
                break;
            case R.id.btNavy_Programs:
                bundle.putInt("item", R.string.navy);
                bundle.putInt("list", R.id.clListFitnessTests_Programs);
                break;
        }
        Navigation.findNavController(view).navigate(R.id.action_programsFragment_to_programsDetailsFragment, bundle);

    };
}