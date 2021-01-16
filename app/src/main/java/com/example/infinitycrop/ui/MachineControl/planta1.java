package com.example.infinitycrop.ui.MachineControl;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.infinitycrop.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import me.sujanpoudel.wheelview.WheelView;

public class planta1 extends AppCompatActivity {
  private CheckBox checkYes, checkNo;
    private Button editInfo, saveChanges;
    private TextView mDisplayDate,mDisplayHour,infoTitle;
    private EditText nameplant;
    private FirebaseFirestore db;
    private List<String> nombres;
    private List<String> estado;
    private String id;
    private String idDocument;
    //Reloj
    int t2Hour, t2Minute;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planta1);
        db=FirebaseFirestore.getInstance();
        //bungle get extra
        Bundle extras = getIntent().getExtras();
        id = extras.getString("idMachine");
        //arrays
        nombres=new ArrayList<>();
        estado=new ArrayList<>();
        //firestore
        db.collection("Mediciones planta 1").document(id).collection("DatosPiso")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        for (DocumentSnapshot doc : snapshots) {
                                nombres.add(doc.getString("name"));
                                estado.add(doc.getId());
                        }
                        Toast.makeText(getApplicationContext(),nombres.toString(), Toast.LENGTH_LONG).show();

                        final WheelView wheelView = (WheelView) findViewById(R.id.wheelview);
                        wheelView.setTitles(nombres);
                        wheelView.setSelectListener(new Function1<Integer, Unit>() {
                            @Override
                            public Unit invoke(Integer integer) {
                                showToast(integer);
                                return Unit.INSTANCE;
                            }
                            private final  void showToast(Integer index) {
                               // Toast.makeText(planta1.this,""+wheelView.getTitles().get(index),Toast.LENGTH_SHORT).show();
                                idDocument=estado.get(index);
                                Toast.makeText(planta1.this,""+idDocument,Toast.LENGTH_SHORT).show();
                                itenInfo(idDocument);
                            }
                        });
                    }
                });





        //Nombre de la planta
        nameplant = (EditText)findViewById(R.id.editTextNobrep);
        nameplant.setEnabled(false);
        //Checkboxes
        checkYes=(CheckBox)findViewById(R.id.checkBoxSi);
        checkYes.setKeyListener(null);
        checkNo=(CheckBox)findViewById(R.id.checkBoxNo);
        checkNo.setKeyListener(null);

        //Boton guardar cambios
        saveChanges = (Button)findViewById(R.id.btn_save);
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editInfo.setVisibility(View.VISIBLE);
                saveChanges.setVisibility(View.GONE);
                mDisplayDate.setEnabled(false);
                mDisplayHour.setEnabled(false);
                nameplant.setEnabled(false);
                infoTitle.setText("Pulsa el botón para editar:");
            }
        });

        //Boton editar informacion
        editInfo = (Button) findViewById(R.id.btnedit);
        editInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges.setVisibility(View.VISIBLE);
                editInfo.setVisibility(View.GONE);
                mDisplayDate.setEnabled(true);
                mDisplayHour.setEnabled(true);
                nameplant.setEnabled(true);
                infoTitle.setText("Información completa:");

            }
        });
        infoTitle = (TextView) findViewById(R.id.textViewinfo);
        //HORA
        mDisplayHour =(TextView) findViewById(R.id.editHour);
        mDisplayHour.setEnabled(false);
        mDisplayHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        planta1.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        t2Hour = hourOfDay;
                        t2Minute = minute;
                        String time = t2Hour + ":" + t2Minute;
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);
                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            mDisplayHour.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                },12,0,false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(t2Hour,t2Minute);
                timePickerDialog.show();
            }
        });


        //CALENDARIO
        mDisplayDate = (TextView) findViewById(R.id.tvDate);
        mDisplayDate.setEnabled(false);
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(planta1.this,android.R.style.Theme_Holo_Dialog_MinWidth, mDateSetListener,year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }

        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month+1;
                String date = day +"/" + month +"/"+ year;
                mDisplayDate.setText(date);
            }
        };

    }

    private void itenInfo(String uiDoc){
        //hacer consulta a ese documento con uiDOc y sacar su info
        //luego set Text a all
    }

}



