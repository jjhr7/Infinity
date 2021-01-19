package com.example.infinitycrop.ui.MachineControl;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.dashboard.DashboardFragment;
import com.example.infinitycrop.ui.dashboard.GeneralFragment;
import com.example.infinitycrop.ui.logmail.LoginActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import me.sujanpoudel.wheelview.WheelView;

public class planta1 extends AppCompatActivity {
    private List<String> nombres;
    private List<String> estado;
    //Aviso al entrar
    private ConstraintLayout fondoR;
    private ImageView imgr,goBack;
    private TextView textR;
    //

    private int piso = 1;
    String one = String.valueOf(piso);
    private int stated = 1;
    String state = String.valueOf(stated);


    private CheckBox checkYes, checkNo;
    private Button editInfo, saveChanges;
    private TextView mDisplayDate,mDisplayHour,infoTitle;
    private EditText nameplant;
    private FirebaseFirestore db;

    private String idDocument;
    private String id;



    //Reloj
    int t2Hour, t2Minute;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planta1);

        //Aviso al entrar
        fondoR = findViewById(R.id.constraitR);
        imgr = findViewById(R.id.imageprevius);
        textR = findViewById(R.id.txtInfoD);
        CountDownTimer countDownTimer = new CountDownTimer(3000, 1000) {//Se pone la cuenta atras para que no se superponga con la animación
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {

                fondoR.setVisibility(View.GONE);
                imgr.setVisibility(View.GONE);
                textR.setVisibility(View.GONE);

            }
        }.start();
        //boton para volver
        goBack= findViewById(R.id.backDashboard);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });

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
                        nombres.clear();
                        for (DocumentSnapshot doc : snapshots) {
                                nombres.add(doc.getString("name"));
                                estado.add(doc.getId());
                        }
                       /* Toast.makeText(getApplicationContext(),nombres.toString(), Toast.LENGTH_LONG).show();*/



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
                saveNote(v);
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
                        Calendar c = Calendar.getInstance();
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
                        c.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        c.set(Calendar.MINUTE,minute);
                        c.set(Calendar.SECOND,0);
                        startAlarm(c);
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

    @Override
    protected void onStart() {
        super.onStart();


    }
    private void startAlarm(Calendar c){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

    }
    public void saveNote(View v){
        String cola = idDocument;
        String nombre = nameplant.getText().toString();
        String hora = mDisplayHour.getText().toString();
        String dia = mDisplayDate.getText().toString();
        String piso = one;

        Map<String, Object> note = new HashMap<>();
        note.put("name",nombre);
        note.put("fecha",dia);
        note.put("hora",hora);
        note.put("piso",piso);

        db.collection("Mediciones planta 1").document(id).collection("DatosPiso").document(cola).update(note).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            Toast.makeText(planta1.this,"va bien",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(planta1.this,"va mal",Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void itenInfo(String uiDoc){
        //hacer consulta a ese documento con uiDOc y sacar su info
        //luego set Text a all
        db.collection("Mediciones planta 1").document(id).collection("DatosPiso").document(uiDoc).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            String nombre = documentSnapshot.getString("name");
                            String fecha = documentSnapshot.getString("fecha");
                            String hora = documentSnapshot.getString("hora");

                           // Map<String,Object> note = documentSnapshot.getData();
                            nameplant.setText(nombre);
                            mDisplayHour.setText(hora);
                            mDisplayDate.setText(fecha);

                        }else{
                            Toast.makeText(planta1.this,"Document does exist",Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(planta1.this,"va mal",Toast.LENGTH_SHORT).show();
            }
        });
                /*.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                    }
                });*/


    }

}



