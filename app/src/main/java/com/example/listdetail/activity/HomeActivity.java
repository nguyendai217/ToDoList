package com.example.listdetail.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.listdetail.R;
import com.example.listdetail.adapter.DataAdapter;
import com.example.listdetail.model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText edtTitle, edtNote;
    private Button btnSave;
    private RecyclerView recyclerView;
    private FloatingActionButton btnFab;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mDatabase;
    ArrayList<Data> arrayList;
    DataAdapter dataAdapter;
    private EditText titleUpdate, noteUpdate;
    private Button btnUpdate, btnDelete;
    private String titleUp, noteUp, post_key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        init();
        controls();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        showAlertDialog();
    }

    private void init() {
        String uId = mUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Note").child(uId);
        mDatabase.keepSynced(true);
        btnFab = findViewById(R.id.btn_fab);
        toolbar = findViewById(R.id.toolbar_home);

        recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        arrayList = new ArrayList<>();
        //  dataAdapter=new DataAdapter(this,arrayList);
        // recyclerView.setAdapter(dataAdapter);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
    }
    private void controls() {
        btnFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog alerdialog = new AlertDialog.Builder(HomeActivity.this).create();
                final LayoutInflater layoutInflater = LayoutInflater.from(HomeActivity.this);
                final View view = layoutInflater.inflate(R.layout.input_note, null, false);
                edtTitle = view.findViewById(R.id.edt_title);
                edtNote = view.findViewById(R.id.edt_note);
                btnSave = view.findViewById(R.id.btn_save);
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String mtitle = edtTitle.getText().toString();
                        String mnote = edtNote.getText().toString();
                        if (mtitle == null && mnote == null) {
                            Toast.makeText(HomeActivity.this, "Add Failed !", Toast.LENGTH_SHORT).show();
                        } else {
//                       if (view.getParent()!=null){
//                           ((ViewGroup) view.getParent()).removeView(view);
//                       }
                            String id = mDatabase.push().getKey();
                            String mdate = DateFormat.getDateInstance().format(new Date());
                            Data data = new Data(id, mtitle, mnote, mdate);
                            mDatabase.child(id).setValue(data);
                            Toast.makeText(HomeActivity.this, "Add Successful !", Toast.LENGTH_SHORT).show();
                            alerdialog.dismiss();
                        }
                    }
                });
                alerdialog.setView(view);
                alerdialog.show();
            }
        });
    }

    public void showAlertDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Notification !");
        builder.setMessage("Are you want to exit ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(
                Data.class, R.layout.item_list, MyViewHolder.class, mDatabase
        ) {
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, final Data model, final int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDate(model.getDate());
                viewHolder.setNote(model.getNote());
                viewHolder.myview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        post_key = getRef(position).getKey();  // lay vi tri item khi click vao
                        titleUp = model.getTitle();  // gan bien moi tao vao du lieu khi click vao
                        noteUp = model.getNote();     // gan bien moi tao vao du lieu khi click vao
                        updateData();
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View myview;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myview = itemView;
        }

        public void setTitle(String title) {
            TextView txtTitle = myview.findViewById(R.id.txt_title);
            txtTitle.setText(title);
        }

        public void setNote(String note) {
            TextView txtNote = myview.findViewById(R.id.txt_note);
            txtNote.setText(note);
        }

        public void setDate(String date) {
            TextView txtDate = myview.findViewById(R.id.txt_date);
            txtDate.setText(date);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // update item
    public void updateData() {
        final AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
        final LayoutInflater layoutInflater = LayoutInflater.from(HomeActivity.this);
        final View view = layoutInflater.inflate(R.layout.update_note, null, false);

        titleUpdate = view.findViewById(R.id.edt_titleUp);
        noteUpdate = view.findViewById(R.id.edt_noteUp);
        btnUpdate = view.findViewById(R.id.btn_update);
        btnDelete = view.findViewById(R.id.btn_delete);

        titleUpdate.setText(titleUp);
        titleUpdate.setSelection(titleUp.length());// gan title moi vao text

        noteUpdate.setText(noteUp);
        noteUpdate.setSelection(noteUp.length());

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleUp = titleUpdate.getText().toString();
                noteUp = noteUpdate.getText().toString();
                String dateUpdate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(post_key, titleUp, noteUp, dateUpdate);
                mDatabase.child(post_key).setValue(data);
                Toast.makeText(HomeActivity.this, "Update Successful !", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteAlertDialog();
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(view);
        alertDialog.show();
    }

    public void DeleteAlertDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Notification !");
        builder.setMessage("Are you want to delete ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDatabase.child(post_key).removeValue();
                Toast.makeText(HomeActivity.this, "Delete successful !", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
