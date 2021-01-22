package com.example.infinitycrop.ui.graphics;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.control_panel.Control_panelFragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class BrChart extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String uid;
    private FirebaseFirestore db;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private String humedad;
    private int humedadA;
    private String temperatura;
    private String luminosidad;
    public Timestamp fecha;

    private com.github.mikephil.charting.charts.BarChart chart;


    public BrChart() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment control_panel.
     */
    // TODO: Rename and change types and number of parameters
    public static Control_panelFragment newInstance(String param1, String param2) {
        Control_panelFragment fragment = new Control_panelFragment();
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
        View v= inflater.inflate(R.layout.fragment_bar_chart, container, false);

        chart = v.findViewById(R.id.chartBar);
        chart.setTouchEnabled(true);
        chart.setPinchZoom(true);
        chart.setDrawGridBackground(false);
        // enable touch gestures
        chart.setTouchEnabled(false);

        GraphicsActivity myActivity = (GraphicsActivity) getActivity();
        uid=myActivity.getmachineID();

        database = FirebaseDatabase.getInstance();

        myRef = database.getReference("Mediciones");

        db = FirebaseFirestore.getInstance();

        db.collection("Mediciones")
                .whereEqualTo("machineId", uid)
                .limit(5)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            int contador = 0;

                            ArrayList<BarEntry> values_Hum = new ArrayList<>();
                            ArrayList<BarEntry> values_Temp = new ArrayList<>();
                            ArrayList<BarEntry> values_Lum = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {


                                contador = contador + 1;

                                Machine_pojo machine = document.toObject(Machine_pojo.class);

                                humedad = machine.getHumedad();
                                temperatura = machine.getTemperatura();
                                luminosidad = machine.getLuminosidad();
                                fecha = machine.getFecha();

                                double temperatura_int = Double.valueOf(temperatura);

                                double humedad_int = Double.valueOf(humedad);

                                double luminosidad_int = Double.valueOf(luminosidad);


                                values_Hum.add(new BarEntry(contador, (float) humedad_int));

                                values_Temp.add(new BarEntry(contador, (float) temperatura_int));

                                values_Lum.add(new BarEntry(contador, (float) luminosidad_int));


                                BarDataSet set1, set2, set3 ;

                                // create a dataset and give it a type
                                set1 = new BarDataSet(values_Hum, "Humedad (%)");
                                set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                                set1.setColor(ColorTemplate.COLORFUL_COLORS[1], 130);
                                set1.setHighLightColor(Color.rgb(244, 117, 117));


                                set2 = new BarDataSet(values_Temp, "Temperatura (º)");
                                set2.setAxisDependency(YAxis.AxisDependency.LEFT);
                                set2.setColor(ColorTemplate.COLORFUL_COLORS[3], 130);
                                set2.setHighLightColor(Color.rgb(244, 117, 117));


                                set3 = new BarDataSet(values_Lum, "Luminosidad (%)");
                                set3.setAxisDependency(YAxis.AxisDependency.LEFT);
                                set3.setColor(ColorTemplate.COLORFUL_COLORS[2], 130);
                                set3.setHighLightColor(Color.rgb(244, 117, 117));

                                //data
                                float groupSpace = 0.16f;
                                float barSpace = 0.01f; // x2 dataset

                                int start = 0;


                                BarData data = new BarData(set1, set2, set3);
                                data.setValueTextSize(0f);
                                data.setBarWidth(0.9f);
                                data.setValueTextColor(Color.BLACK);
                                data.setValueTextSize(9f);

                                // get the legend (only possible after setting data)
                                Legend l = chart.getLegend();

                                // draw legend entries as lines
                                l.setForm(Legend.LegendForm.LINE);


                                YAxis yl = chart.getAxisLeft();
                                yl.setSpaceTop(30f);
                                yl.setSpaceBottom(30f);
                                yl.setDrawZeroLine(false);

                                chart.getAxisRight().setEnabled(false);

                                XAxis xl = chart.getXAxis();
                                xl.setPosition(XAxis.XAxisPosition.BOTTOM);

                                //setting animation for y-axis, the bar will pop up from 0 to its value within the time we set
                                chart.animateY(2000);
                                //setting animation for x-axis, the bar will pop up separately within the time we set
                                chart.animateX(2000);

                                chart.setData(data);
                                chart.groupBars(start, groupSpace, barSpace);

                                chart.invalidate();


                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });



        return v;
    }

}

