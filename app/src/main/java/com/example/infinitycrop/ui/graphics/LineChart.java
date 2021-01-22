package com.example.infinitycrop.ui.graphics;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.infinitycrop.MainActivity;
import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.control_panel.Control_panelFragment;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

public class LineChart extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseFirestore db;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private LineDataSet ldataSet = new LineDataSet(null,null);
    private ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
    private LineData linedata;
    private String humedad;
    private int humedadA;
    private String temperatura;
    private String luminosidad;
    public Timestamp fecha;
    private String uid;


    private com.github.mikephil.charting.charts.LineChart chart;


    public LineChart() {
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
        View v= inflater.inflate(R.layout.fragment_linear_chart, container, false);

        GraphicsActivity myActivity = (GraphicsActivity) getActivity();
        uid=myActivity.getmachineID();

        chart = v.findViewById(R.id.chartLinear);
        chart.setTouchEnabled(true);
        chart.setPinchZoom(true);
        chart.setDrawGridBackground(false);
        // enable touch gestures
        chart.setTouchEnabled(true);

        //setting animation for y-axis, the bar will pop up from 0 to its value within the time we set
        chart.animateY(1000);
        //setting animation for x-axis, the bar will pop up separately within the time we set
        chart.animateX(1000);


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

                            ArrayList<Entry> values_Hum = new ArrayList<>();
                            ArrayList<Entry> values_Temp = new ArrayList<>();
                            ArrayList<Entry> values_lum = new ArrayList<>();

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

                                values_Hum.add(new Entry(contador, (float) humedad_int));

                                values_Temp.add(new Entry(contador, (float) temperatura_int));

                                values_lum.add(new Entry(contador, (float) luminosidad_int));

                                chart(values_Hum, values_Temp, values_lum);


                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

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



        return v;

    }

    public void chart(ArrayList<Entry> valuesHum, ArrayList<Entry> valuesTpa, ArrayList<Entry> valuesLum){

        LineDataSet set1, set2, set3;

            // create a dataset and give it a type
            set1 = new LineDataSet(valuesHum, "Humedad (%)");
            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setColor(ColorTemplate.COLORFUL_COLORS[1], 130);
            set1.setCircleColor(Color.RED);
            set1.setLineWidth(2f);
            set1.setCircleRadius(3f);
            set1.setFillAlpha(65);
            set1.setFillColor(ColorTemplate.getHoloBlue());
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setDrawCircleHole(false);


            set2 = new LineDataSet(valuesTpa, "Temperatura (º)");
            set2.setAxisDependency(YAxis.AxisDependency.RIGHT);
            set2.setColor(ColorTemplate.COLORFUL_COLORS[0], 130);
            set2.setCircleColor(Color.MAGENTA);
            set2.setLineWidth(2f);
            set2.setCircleRadius(3f);
            set2.setFillAlpha(65);
            set2.setFillColor(ColorTemplate.colorWithAlpha(Color.BLUE, 200));
            set2.setDrawCircleHole(false);
            set2.setHighLightColor(Color.rgb(244, 117, 117));



            set3 = new LineDataSet(valuesLum, "Luminosidad (%)");
            set3.setAxisDependency(YAxis.AxisDependency.RIGHT);
            set3.setColor(ColorTemplate.COLORFUL_COLORS[2], 130);
            set3.setCircleColor(Color.YELLOW);
            set3.setLineWidth(2f);
            set3.setCircleRadius(3f);
            set3.setFillAlpha(65);
            set3.setFillColor(ColorTemplate.colorWithAlpha(Color.BLUE, 200));
            set3.setDrawCircleHole(false);
            set3.setHighLightColor(Color.rgb(244, 117, 117));


            // create a data object with the data sets
            LineData data = new LineData(set1, set2, set3);
            data.setValueTextColor(Color.BLACK);
            data.setValueTextSize(9f);

            // set data
            chart.setData(data);
            chart.invalidate();
    }

}
