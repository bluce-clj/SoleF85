package com.dyaco.spiritbike.programs;

import com.dyaco.spiritbike.support.BaseFragment;

public class AdjustCustom_1Fragment extends BaseFragment {

//    private SeekBar sb1,sb2,sb3,sb4,sb5,sb6,sb7,sb8,sb9,sb10,sb11,sb12,sb13,sb14,sb15,sb16,sb17,sb18,sb19,sb20;
//    private TextView sb1Text,sb2Text;
//
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    private String mParam1;
//    private String mParam2;
//
//    public AdjustCustom_1Fragment() {
//        // Required empty public constructor
//    }
//
//
//    public static AdjustCustom_1Fragment newInstance(String param1, String param2) {
//        AdjustCustom_1Fragment fragment = new AdjustCustom_1Fragment();
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
//        return inflater.inflate(R.layout.fragment_adjust_custom_1, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        ((DashboardActivity) mActivity).changeTopWidgetStyle(false);
//        ((DashboardActivity) mActivity).changeSignoutToBack(true, false,0);
//        ((DashboardActivity) mActivity).invisibleIndicator();
//
//
//        Button btNext_SetDate = view.findViewById(R.id.btNext_SetDate);
//
//        btNext_SetDate.setOnClickListener(v -> {
//            Bundle bundle = new Bundle();
//            //    bundle.putInt("ProgramId", programsEnum.getCode());
//            final FragmentManager fm = getActivity().getSupportFragmentManager();
//            NavHostFragment navHostFragment = (NavHostFragment) fm.findFragmentById(R.id.nhcDashboard);
//            navHostFragment.getNavController().navigate(R.id.adjustCustom_2Fragment, bundle);
//        });
//
//        initView(view);
//
//    }
//
//    private void initView(View view) {
//        sb1 = view.findViewById(R.id.sb1);
//        sb1Text = view.findViewById(R.id.sb1_text);
//
//        sb1Text.setText(String.valueOf(sb1.getProgress() + 1));
//        sb1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) { }
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) { }
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                sb1Text.setText(String.valueOf(progress+1));
//            }
//        });
//
//    }
}