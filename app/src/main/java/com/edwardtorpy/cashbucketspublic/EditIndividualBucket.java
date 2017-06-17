package com.edwardtorpy.cashbucketspublic;


import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EditIndividualBucket extends AppCompatActivity {
    String tagFromMain;
    EditText maxCashText;
    EditText nameText;

    public void updateBucket(View view) {

        if (nameText.getText().toString().isEmpty()){
            Toast.makeText(this, "Enter name", Toast.LENGTH_SHORT).show();
        } else if (maxCashText.getText().toString().isEmpty()) {

            Toast.makeText(this, "Enter number", Toast.LENGTH_SHORT).show();
        } else {

            String bucketName = nameText.getText().toString();
            int maxCash = Integer.valueOf(maxCashText.getText().toString());
            //int currentCash = Integer.valueOf(currentCashText.getText().toString());
            String bucketID = tagFromMain;

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("ID", bucketID);
            intent.putExtra("name", bucketName);
            intent.putExtra("maxCash", maxCash);

            startActivity(intent);
            finish();
        }

    }

    public void deleteBucket (View view) {

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Delete bucket")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String bucketID = tagFromMain;

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("ID", bucketID);

                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_individual_bucket);

        // Adding spinner
        Spinner frequencySpinner = (Spinner) findViewById(R.id.frequencySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.frequency_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencySpinner.setAdapter(adapter);

        //ToDo Add background tint relative to which class of bucket

        Intent intent = getIntent();
        tagFromMain = intent.getStringExtra("tag");
        int bucketNumber = Integer.valueOf(tagFromMain) + 1;

        TextView bucketID = (TextView) findViewById(R.id.bucketIDTextView);

        TextView quoteTextView = (TextView) findViewById(R.id.quoteTextView);
        quoteTextView.setText(intent.getStringExtra("quote"));

        maxCashText = (EditText) findViewById(R.id.maxCashEditText);
        nameText = (EditText) findViewById(R.id.nameEditText);


        if (intent.getStringExtra("name") == null){
            bucketID.setText("Priority : " + bucketNumber);
        } else {
            bucketID.setText("Priority : " + bucketNumber + "\n" + intent.getStringExtra("name"));
            nameText.setText(intent.getStringExtra("name"));
            maxCashText.setText(Integer.valueOf(intent.getIntExtra("maxCash", 0)).toString());

        }

        nameText.requestFocus();

    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}
