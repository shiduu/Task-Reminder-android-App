package com.example.taskreminder;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.ParseException;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;

import java.util.Locale;
import java.util.zip.Inflater;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.taskreminder.Model.Data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

public class HomeActivity extends AppCompatActivity
//        implements
//        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener
{

    private Toolbar toolbar;
    private FloatingActionButton fabBtn;

    Button stop;


    private TextView result;

    //update fileds
    private EditText titleup,noteup;
    private Button delete, update;

    private String title,note,post_key;

//    int day,month,year,hour,minute;
//    int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal;

    //firebase

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    //recycler
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String Uid = mUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("tasks").child(Uid);
        mDatabase.keepSynced(true);

        recyclerView = findViewById(R.id.recycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);



        final Context context = this;

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        toolbar = (Toolbar)findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Task Manager App");

        fabBtn = (FloatingActionButton)findViewById(R.id.fab_btn);

        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder mydialog = new AlertDialog.Builder(HomeActivity.this);

                LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);

                View myview = inflater.inflate(R.layout.custominputfield, null);
                mydialog.setView(myview);
                final AlertDialog dialog = mydialog.create();

                final EditText title = myview.findViewById(R.id.edt_title);
                final EditText task = myview.findViewById(R.id.edt_task);

                Button btnsave = myview.findViewById(R.id.btn_save);

                Button pick = myview.findViewById(R.id.pick);

                final TimePicker timePicker = myview.findViewById(R.id.timePicker);


                pick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar calendar = Calendar.getInstance();
                        if (android.os.Build.VERSION.SDK_INT >= 23) {
                            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                                    timePicker.getHour(), timePicker.getMinute(), 0);
                        } else {
                            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                                    timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                        }


                        setAlarm(calendar.getTimeInMillis());
                    }
                });




                btnsave.setOnClickListener(new View.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View view) {
                        String mTitle = title.getText().toString();
                        String mDescription = task.getText().toString();

                        if (TextUtils.isEmpty(mTitle))
                        {
                            title.setError("Required field");
                            return;
                        }
                        if (TextUtils.isEmpty(mDescription))
                        {
                            task.setError("Required field");
                            return;
                        }

                        String id = mDatabase.push().getKey();

                        String datee = DateFormat.getDateInstance().format(new Date());



                        Data data = new Data(mTitle,mDescription,datee,id);

                        mDatabase.child(id).setValue(data);

                        Toast.makeText(getApplicationContext(), "Task added successfully", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();


                    }
                });

//                picker = (Button)myview.findViewById(R.id.pick);
////                Button picker  = myview.findViewById(R.id.btn_pick);
//                result = (TextView)myview.findViewById(R.id.tv_result);
//                // TextView result = myview.findViewById(R.id.tv_result);
//
//                picker.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Calendar c = Calendar.getInstance();
//                        SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a");
//                        year = c.get(Calendar.YEAR);format.format( c.get(Calendar.YEAR));
//                        month = c.get(Calendar.MONTH);
//                        day = c.get(Calendar.DAY_OF_MONTH);
//
//                        DatePickerDialog datePickerDialog = new DatePickerDialog(HomeActivity.this, HomeActivity.this, year,month,day);
//                        datePickerDialog.show();
//                    }
//                });

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });


    }



    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Data, MyViewHolder>adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(
                Data.class,
                R.layout.item_data,
                MyViewHolder.class,
                mDatabase
        ) {

            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, final Data model, final int position) {

                viewHolder.setTitle(model.getTitle());
                viewHolder.setNote(model.getNote());
                viewHolder.setDate(model.getDate());

                viewHolder.myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        post_key = getRef(position).getKey();
                        title = model.getTitle();
                        note = model.getNote();


                        updateData();
                    }
                });

            }
        };

        recyclerView.setAdapter(adapter);


    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        View myView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myView = itemView;

        }
        public void setTitle(String title)
        {
            TextView mTitle = myView.findViewById(R.id.title);
            mTitle.setText(title);
        }
        public void setNote(String note)
        {
            TextView mNote = myView.findViewById(R.id.des);
            mNote.setText(note);
        }
        public void setDate(String date)
        {
            TextView mDate = myView.findViewById(R.id.date);
            mDate.setText(date);
        }
    }

//        @Override
//    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//
//        yearFinal = year;
//        monthFinal = month;
//        dayFinal = dayOfMonth;
//
//        Calendar c = Calendar.getInstance();
//        hour = c.get(Calendar.HOUR);
//        minute = c.get(Calendar.MINUTE);
//
//        TimePickerDialog timePickerDialog = new TimePickerDialog(HomeActivity.this, HomeActivity.this, hour, minute,DateFormat.is24HourFormat(this));
//        timePickerDialog.show();
//
//
//    }
//
//    @Override
//    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//        hourFinal = hourOfDay;
//        minuteFinal = minute;
//
//        result.setText("YEAR: " + yearFinal + "\n" +
//                "MONTH: " + monthFinal + "\n" +
//                "DAY: " + dayFinal + "\n" +
//                "HOUR: " + hourFinal + "\n" +
//                "MINUTE: " + minuteFinal + "\n" );
//
//    }

    public void updateData()
    {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);

        View myview = inflater.inflate(R.layout.updateinput,null);
        mydialog.setView(myview);

        final AlertDialog dialog = mydialog.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        titleup = myview.findViewById(R.id.edt_title_upd);
        noteup = myview.findViewById(R.id.edt_task_upd);

        titleup.setText(title);
        titleup.setSelection(title.length());

        noteup.setText(note);
        noteup.setSelection(note.length());

        delete = myview.findViewById(R.id.btn_delete);
        update = myview.findViewById(R.id.btn_upd);

        Button picker = myview.findViewById(R.id.picker);

        final TimePicker timePick = myview.findViewById(R.id.timePick);


        picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                            timePick.getHour(), timePick.getMinute(), 0);
                } else {
                    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                            timePick.getCurrentHour(), timePick.getCurrentMinute(), 0);
                }


                setAlarm(calendar.getTimeInMillis());
            }
        });




        update.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {

                title = titleup.getText().toString();
                note  = noteup.getText().toString();

                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(title,note,mDate,post_key);
                mDatabase.child(post_key).setValue(data);

               dialog.dismiss();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDatabase.child(post_key).removeValue();


                dialog.dismiss();
            }
        });

        dialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }




    private void setAlarm(long time) {
        //getting the alarm manager
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //creating a new intent specifying the broadcast receiver
        Intent i = new Intent(this, MyAlarm.class);

        //creating a pending intent using the intent
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);

        //setting the repeating alarm that will be fired every day
        am.setRepeating(AlarmManager.RTC, time, AlarmManager.INTERVAL_DAY, pi);
        Toast.makeText(this, "Time is set", Toast.LENGTH_SHORT).show();
    }
}
