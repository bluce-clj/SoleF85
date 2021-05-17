package com.dyaco.spiritbike.newprofile;

import androidx.fragment.app.Fragment;


public class FirstLaunchSetTime12Fragment extends Fragment {

//    private Typeface        m_typeFace;
//    private TextView        m_tvAm_SetTime12;
//    private TextView        m_tvPm_SetTime12;
//    private RadioButton     m_rbAm_SetTime12;
//    private RadioButton     m_rbPm_SetTime12;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_first_launch_set_time12, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        m_typeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Serpentine-BoldOblique.otf");
//
//        TextView tvHour_SetTime12 = view.findViewById(R.id.tvHour_SetTime12);
//        tvHour_SetTime12.setTypeface(m_typeFace);
//
//        TextView tvMin_SetTime12 = view.findViewById(R.id.tvMin_SetTime12);
//        tvMin_SetTime12.setTypeface(m_typeFace);
//
//        TextView tvColon_SetTime12 = view.findViewById(R.id.tvColon_SetTime12);
//        tvColon_SetTime12.setTypeface(m_typeFace);
//
//        m_tvAm_SetTime12 = view.findViewById(R.id.tvAM_SetTime12);
//        m_tvPm_SetTime12 = view.findViewById(R.id.tvPM_SetTime12);
//
//        m_rbAm_SetTime12 = view.findViewById(R.id.rbAm_SetTime12);
//        m_rbPm_SetTime12 = view.findViewById(R.id.rbPm_SetTime12);
//
//        m_rbAm_SetTime12.setOnClickListener(rbFormatOnClick);
//        m_rbPm_SetTime12.setOnClickListener(rbFormatOnClick);
//
//        Button btBack_SetTime12 = view.findViewById(R.id.btBack_SetTime12);
//        Button btFormat_SetTime12 = view.findViewById(R.id.btFormat_SetTime12);
//        Button btGetStarted_SetTime12 = view.findViewById(R.id.btGetStarted_SetTime12);
//
//        btBack_SetTime12.setOnClickListener(btSetTime12OnClick);
//        btFormat_SetTime12.setOnClickListener(btSetTime12OnClick);
//        btGetStarted_SetTime12.setOnClickListener(btSetTime12OnClick);
//    }
//
//    private View.OnClickListener btSetTime12OnClick = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            switch (view.getId()) {
//                case R.id.btBack_SetTime12:
//                    Navigation.findNavController(view).navigate(R.id.action_firstLaunchSetTime12Fragment_to_firstLaunchSetDateFragment);
//                    break;
//                case R.id.btFormat_SetTime12:
//                    Navigation.findNavController(view).navigate(R.id.action_firstLaunchSetTime12Fragment_to_firstLaunchSetTimeFragment);
//                    break;
//                case R.id.btGetStarted_SetTime12:
//                    Navigation.findNavController(view).navigate(R.id.action_firstLaunchSetTime12Fragment_to_startScreenFragment);
//                    break;
//            }
//        }
//    };
//
//    private View.OnClickListener rbFormatOnClick = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            m_tvAm_SetTime12.setTextColor(m_rbAm_SetTime12.isChecked() ?
//                    ContextCompat.getColor(getContext(), R.color.color9D2227) : ContextCompat.getColor(getContext(), R.color.color2F3031));
//            m_tvPm_SetTime12.setTextColor(m_rbPm_SetTime12.isChecked() ?
//                    ContextCompat.getColor(getContext(), R.color.color9D2227) : ContextCompat.getColor(getContext(), R.color.color2F3031));
//        }
//
//    };
}