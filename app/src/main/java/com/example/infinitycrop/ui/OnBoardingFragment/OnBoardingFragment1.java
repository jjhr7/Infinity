package com.example.infinitycrop.ui.OnBoardingFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.list_machine.AddMachine;

public class OnBoardingFragment1 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,Bundle savedInstanceState) {
        final ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_on_boarding1,container,false);


        TextView button = (TextView) root.findViewById(R.id.btn_skip);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent((getActivity().getApplication()), AddMachine.class);
                TextView editText = (TextView) root.findViewById(R.id.btn_skip);
                startActivity(intent);

            }
        });
        return root;
    }


}
