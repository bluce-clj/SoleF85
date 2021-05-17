package com.dyaco.spiritbike.programs;

import androidx.fragment.app.Fragment;

public class AdjustCustom_2Fragment extends Fragment {

//
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    private String mParam1;
//    private String mParam2;
//
//    public AdjustCustom_2Fragment() {
//        // Required empty public constructor
//    }
//
//
//    public static AdjustCustom_2Fragment newInstance(String param1, String param2) {
//        AdjustCustom_2Fragment fragment = new AdjustCustom_2Fragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_adjust_custom_2, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        ((DashboardActivity) getActivity()).changeTopWidgetStyle(false);
//        ((DashboardActivity) getActivity()).changeSignoutToBack(true, false,0);
//        ((DashboardActivity) getActivity()).invisibleIndicator();
//
//
//        Button btNext_SetDate = view.findViewById(R.id.btNext_SetDate);
//        Button btBack = view.findViewById(R.id.btBack);
//
//        btNext_SetDate.setOnClickListener(v -> {
//            Bundle bundle = new Bundle();
//            //    bundle.putInt("ProgramId", programsEnum.getCode());
//            final FragmentManager fm = getActivity().getSupportFragmentManager();
//            NavHostFragment navHostFragment = (NavHostFragment) fm.findFragmentById(R.id.nhcDashboard);
//            navHostFragment.getNavController().navigate(R.id.adjustCustom_3Fragment, bundle);
//        });
//
//        btBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final FragmentManager fm = getActivity().getSupportFragmentManager();
//                NavHostFragment navHostFragment = (NavHostFragment) fm.findFragmentById(R.id.nhcDashboard);
//                navHostFragment.getNavController().popBackStack();
//            }
//        });
//
//
//    }
}