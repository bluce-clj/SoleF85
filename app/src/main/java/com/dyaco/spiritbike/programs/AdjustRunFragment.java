package com.dyaco.spiritbike.programs;

import androidx.fragment.app.Fragment;

public class AdjustRunFragment extends Fragment {
//
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    private String mParam1;
//    private String mParam2;
//
//    public AdjustRunFragment() {
//        // Required empty public constructor
//    }
//
//
//    public static AdjustRunFragment newInstance(String param1, String param2) {
//        AdjustRunFragment fragment = new AdjustRunFragment();
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
//        return inflater.inflate(R.layout.fragment_adjust_run, container, false);
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
////        Bundle bundle = getArguments();
////        if (bundle != null) {
////            m_nItem = bundle.getInt("item");
////        }
//
//
//        Button btNext_SetDate = view.findViewById(R.id.btNext_SetDate);
//
//        btNext_SetDate.setOnClickListener(v -> {
//         //   Intent intent = new Intent(getActivity(), WorkoutCountdownActivity.class);
//            Intent intent = new Intent(getActivity(), WorkoutDashboardActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putBoolean("Open_Templates", false);
//            intent.putExtras(bundle);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            startActivity(intent);
//          //  getActivity().finish();
//        });
//
//
//    }
}