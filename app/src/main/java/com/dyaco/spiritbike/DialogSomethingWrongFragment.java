package com.dyaco.spiritbike;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class DialogSomethingWrongFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dialog_something_wrong, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ConstraintLayout clDialogSomethingWrong = view.findViewById(R.id.clDialogSomethingWrong);
        clDialogSomethingWrong.setBackgroundResource(R.drawable.background_popup_down);

        Button btCancel_DialogSomethingWrong = view.findViewById(R.id.btCancel_DialogSomethingWrong);
        Button btTryAgain_DialogSomethingWrong = view.findViewById(R.id.btTryAgain_DialogSomethingWrong);

        btCancel_DialogSomethingWrong.setOnClickListener(btDialogSomethingWrongOnClick);
        btTryAgain_DialogSomethingWrong.setOnClickListener(btDialogSomethingWrongOnClick);
    }

    private View.OnClickListener btDialogSomethingWrongOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btCancel_DialogSomethingWrong:
                    Navigation.findNavController(view).navigate(R.id.action_dialogDataLostFragment_to_startScreenFragment);
                    break;
                case R.id.btTryAgain_DialogSomethingWrong:
                    break;
            }
        }
    };
}