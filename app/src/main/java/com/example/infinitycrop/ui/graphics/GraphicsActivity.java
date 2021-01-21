package com.example.infinitycrop.ui.graphics;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.infinitycrop.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class GraphicsActivity  extends AppCompatActivity {

    private String s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphics);

        Bundle extras = getIntent().getExtras();
        s = extras.getString("idMachine"); //declaras como priv la s

        //volver a la anterior actividad
        ImageView backToProfile =findViewById(R.id.backToProfile3);
        backToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStop();
                finish();
            }
        });
        //Pestañas
        ViewPager2 viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new MiPagerAdapter(this));
        final TabLayout tabs = findViewById(R.id.tabs);
        final int[] tabIcons = {
                R.drawable.ic_baseline_multiline_chart_24,
                R.drawable.ic_baseline_bar_chart_24,
                //R.drawable.ic_baseline_pie_chart_24
        };

        new TabLayoutMediator(tabs, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position){
                        tab.setIcon(tabIcons[position]);
                    }
                }
        ).attach();

    }

    public class MiPagerAdapter extends FragmentStateAdapter {
        public MiPagerAdapter(FragmentActivity activity){
            super(activity);
        }
        @Override
        public int getItemCount() {
            return 2;
        }
        @Override @NonNull
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return new LineChart();
                case 1: return new BrChart();
                //case 2: return new PiChart();
            }
            return null;
        }
    }

    public String getmachineID(){
        return s;
    }


}
