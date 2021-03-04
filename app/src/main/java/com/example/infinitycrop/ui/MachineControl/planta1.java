package com.example.infinitycrop.ui.MachineControl;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.AlarmClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.akaita.android.circularseekbar.CircularSeekBar;
import com.example.infinitycrop.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


import java.text.DecimalFormat;
import java.util.ArrayList;
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
    private ImageView imgr,goBack,imgEstado;
    private TextView textR;
    //SeekBar


    private int piso = 1;
    String one = String.valueOf(piso);
    private int stated = 1;
    String state = String.valueOf(stated);



    private Button editInfo, saveChanges, luminosidad;
    private TextView mDisplayHour,infoTitle,mStation,txtEstado;
    private EditText nameplant;
    private FirebaseFirestore db;

    private String idDocument;
    private String id;

    private float date;
    private  int intensi;
    private int intensidad;

    //Reloj
    int t2Hour, t2Minute;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planta1);
        luminosidad= findViewById(R.id.btnregular);
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
                        for (final DocumentSnapshot doc : snapshots) {
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
                                itenInfo(idDocument);


                            }
                        });

                    }
                });





            //Nombre de la planta
            nameplant = (EditText)findViewById(R.id.editTextNobrep);
            nameplant.setEnabled(false);
            mDisplayHour=(TextView) findViewById(R.id.editHour);
            mDisplayHour.setEnabled(false);
            mStation=(TextView)findViewById(R.id.txtStation);
            txtEstado=(TextView)findViewById(R.id.textoEstado);
            imgEstado=(ImageView)findViewById(R.id.colorEstado);

            //Boton guardar cambios
            saveChanges = (Button)findViewById(R.id.btn_save);
            saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote(v);
                editInfo.setVisibility(View.VISIBLE);
                saveChanges.setVisibility(View.GONE);
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
                mDisplayHour.setEnabled(true);
                nameplant.setEnabled(true);
                infoTitle.setText("Información completa:");



            }
        });
        infoTitle = (TextView) findViewById(R.id.textViewinfo);
        //HORA

        mDisplayHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
                if(intent.resolveActivity(getPackageManager())!=null){
                    startActivity(intent);
                }

        }
        });




        /*//CALENDARIO
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
            */
    }

    @Override
    protected void onStart() {
        super.onStart();


    }
    /*public void establecerAlarma(String mensaje,int dias,int hora,int minutos){
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_DAYS,dias)
                .putExtra(AlarmClock.EXTRA_MESSAGE,mensaje)
                .putExtra(AlarmClock.EXTRA_HOUR,hora)
                .putExtra(AlarmClock.EXTRA_MINUTES,minutos);
        if(intent.resolveActivity(getPackageManager())!=null){
            startActivity(intent);
        }
    }*/

    public void saveNote(View v){
        String cola = idDocument;
        String nombre = nameplant.getText().toString();

        String piso = one;

        Map<String, Object> note = new HashMap<>();
        note.put("name",nombre);
        note.put("piso",piso);

        db.collection("Mediciones planta 1").document(id).collection("DatosPiso").document(cola).update(note).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            Toast.makeText(planta1.this,"Datos guardados correctamente",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(planta1.this,"Error al guardar",Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void itenInfo(final String uiDoc){
        //hacer consulta a ese documento con uiDOc y sacar su info
        //luego set Text a all
        db.collection("Mediciones planta 1").document(id).collection("DatosPiso").document(uiDoc).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            String nombre = documentSnapshot.getString("name");
                            String estacion = documentSnapshot.getString("estacion");
                            intensidad = documentSnapshot.getLong("intensidad").intValue();
                            date=(float)intensidad;


                            int resultado =documentSnapshot.getLong("estado").intValue();
                            if(resultado==1){
                                txtEstado.setText("Ocupado");
                                imgEstado.setImageResource(R.drawable.circulo_pequenyo2);
                               /* checkYes.isSelected();
                                checkYes.setChecked(true);
                                checkYes.isChecked();
                                checkNo.setChecked(false);*/
                            }else{
                                txtEstado.setText("Libre");
                                imgEstado.setImageResource(R.drawable.circulo_pequenyo);
                                /*checkNo.isSelected();
                                checkNo.setChecked(true);
                                checkNo.isChecked();
                                checkYes.setChecked(false);*/
                            }


                           // Map<String,Object> note = documentSnapshot.getData();
                            nameplant.setText(nombre);
                            mStation.setText(estacion);



                            luminosidad.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(planta1.this);
                                    bottomSheetDialog.setContentView(R.layout.bottom_sheet_regular_luz);
                                    TextView textView = bottomSheetDialog.findViewById(R.id.texttitle);
                                    Button btacept = bottomSheetDialog.findViewById(R.id.btnaceptar);

                                    final CircularSeekBar seekBar= bottomSheetDialog.findViewById(R.id.seekbar2);
                                    seekBar.setProgressTextFormat(new DecimalFormat("###,###,##0.00"));
                                    seekBar.setProgress(date);
                                    seekBar.setOnCenterClickedListener(new CircularSeekBar.OnCenterClickedListener() {
                                        @Override
                                        public void onCenterClicked(CircularSeekBar seekBar, float progress) {
                                            Snackbar.make(seekBar,"Reset",Snackbar.LENGTH_SHORT)
                                                    .show();
                                            seekBar.setProgress(0);
                                        }
                                    });
                                    seekBar.setOnCircularSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {

                                        @Override
                                        public void onProgressChanged(CircularSeekBar seekBar, float progress, boolean fromUser) {
                                            if(progress<25){
                                                seekBar.setRingColor(Color.GREEN);
                                            }
                                            else if(progress<50){
                                                seekBar.setRingColor(Color.YELLOW);
                                            }
                                            else if(progress<75){
                                                seekBar.setRingColor(Color.RED);
                                            }
                                            date = progress;
                                        }

                                        @Override
                                        public void onStartTrackingTouch(CircularSeekBar seekBar) {

                                        }

                                        @Override
                                        public void onStopTrackingTouch(CircularSeekBar seekBar) {



                                        }
                                    });
                                    btacept.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Map<String, Object> note = new HashMap<>();
                                            intensi=(int) date;
                                            note.put("intensidad",intensi);
                                            db.collection("Mediciones planta 1").document(id).collection("DatosPiso").document(uiDoc).update(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(planta1.this,"Intensidad establecida correctamente",Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(planta1.this,"Error al guardar intensidad",Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            bottomSheetDialog.dismiss();
                                        }
                                    });
                                    bottomSheetDialog.show();
                                }

                            });

                        }else{
                            Toast.makeText(planta1.this,"Document does exist",Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(planta1.this,"Error al obtener piso",Toast.LENGTH_SHORT).show();
            }
        });
                /*.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                    }
                });*/


    }

}



