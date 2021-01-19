package com.example.infinitycrop.ui.graphics;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.infinitycrop.MainActivity;
import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.control_panel.Control_panelFragment;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BubbleChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleDataSet;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
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
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class PiChart extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseFirestore db;
    private String uid;

    private int humedad;
    private int humedadA;
    private String temperatura;
    private int luminosidad;
    public Timestamp fecha;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private BubbleChart chart;


    public PiChart() {
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
        View v= inflater.inflate(R.layout.fragment_pie_chart, container, false);

        chart = v.findViewById(R.id.pieChart);
        chart.getDescription().setEnabled(false);

        chart.setDrawGridBackground(false);

        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        chart.setMaxVisibleValueCount(200);
        chart.setPinchZoom(false);

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


        GraphicsActivity myActivity = (GraphicsActivity) getActivity();
        uid=myActivity.getmachineID();


        database = FirebaseDatabase.getInstance();

        myRef = database.getReference("Mediciones");


        db = FirebaseFirestore.getInstance();

        db.collection("Mediciones")
                .whereEqualTo("machineID", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            int contador = 0;

                            ArrayList<BubbleEntry> values_Hum = new ArrayList<>();
                            ArrayList<BubbleEntry> values_HumA = new ArrayList<>();
                            ArrayList<BubbleEntry> values_Temp = new ArrayList<>();
                            ArrayList<BubbleEntry> values_Lum = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {


                                contador = contador + 1;

                                Machine_pojo machine = document.toObject(Machine_pojo.class);

                                humedad = machine.getHumedad();
                                humedadA = machine.getHumedadA();
                                temperatura = machine.getTemperatura();
                                luminosidad = machine.getLuminosidad();
                                fecha = machine.getFecha();

                                int temperatura_int = Integer.valueOf(temperatura);

                                values_Hum.add(new BubbleEntry(contador, humedad, 1));

                                values_HumA.add(new BubbleEntry(contador, humedadA, 1));

                                values_Lum.add(new BubbleEntry(contador, luminosidad, 1));

                                values_Temp.add(new BubbleEntry(contador, temperatura_int, 1));

                                // create a dataset and give it a type
                                BubbleDataSet set1 = new BubbleDataSet(values_Hum, "Humedad");
                                set1.setDrawIcons(false);
                                set1.setValueTextSize(10f);
                                set1.setValueTextColor(R.color.black);
                                set1.setColor(ColorTemplate.COLORFUL_COLORS[0], 130);
                                set1.setDrawValues(true);

                                BubbleDataSet set2 = new BubbleDataSet(values_HumA, "Humedad amb");
                                set2.setDrawIcons(false);
                                set2.setValueTextSize(10f);
                                set2.setValueTextColor(R.color.black);
                                set2.setIconsOffset(new MPPointF(0, 15));
                                set2.setColor(ColorTemplate.COLORFUL_COLORS[1], 130);
                                set2.setDrawValues(true);

                                BubbleDataSet set3 = new BubbleDataSet(values_Lum, "Luminosidad");
                                set3.setColor(ColorTemplate.COLORFUL_COLORS[2], 130);
                                set3.setValueTextSize(10f);
                                set3.setValueTextColor(R.color.black);
                                set3.setDrawValues(true);

                                BubbleDataSet set4 = new BubbleDataSet(values_Temp, "Temperatura");
                                set4.setDrawIcons(false);
                                set4.setValueTextSize(10f);
                                set4.setValueTextColor(R.color.black);
                                set4.setIconsOffset(new MPPointF(0, 15));
                                set4.setColor(ColorTemplate.COLORFUL_COLORS[3], 130);
                                set4.setDrawValues(true);

                                ArrayList<IBubbleDataSet> dataSets = new ArrayList<>();
                                dataSets.add(set1); // add the data sets
                                dataSets.add(set2);
                                dataSets.add(set3);
                                dataSets.add(set4);

                                // create a data object with the data sets
                                BubbleData data = new BubbleData(dataSets);
                                data.setDrawValues(false);
                                data.setValueTextSize(8f);
                                data.setValueTextColor(Color.BLACK);
                                data.setHighlightCircleWidth(1.5f);

                                chart.setData(data);
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
