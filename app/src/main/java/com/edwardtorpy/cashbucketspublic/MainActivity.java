package com.edwardtorpy.cashbucketspublic;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Intent userIntent;

    EditText totalCashEditText;

    ArrayList<Bucket> buckets;
    SQLiteDatabase bucketData;
    SharedPreferences savedTotalCash;
    int reserveCash;

    public void openEditBucketActivity(View view) {

        Intent editBucketIntent = new Intent(getApplicationContext(), EditIndividualBucket.class);
        editBucketIntent.putExtra("tag", view.getTag().toString());
        editBucketIntent.putExtra("name", buckets.get(Integer.valueOf(view.getTag().toString())).bucketName);
        editBucketIntent.putExtra("maxCash", buckets.get(Integer.valueOf(view.getTag().toString())).maxCash);
        editBucketIntent.putExtra("quote", buckets.get(Integer.valueOf(view.getTag().toString())).getQuote());

        startActivity(editBucketIntent);

        finish();

    }

    public void enterTotalCash(View view) {
        EditText totalCashText = (EditText) findViewById(R.id.totalCashEditText);

        String testString = totalCashText.getText().toString();
        if (testString.isEmpty()) {
            Toast.makeText(this, "Enter a number!", Toast.LENGTH_SHORT).show();
        } else {
            savedTotalCash.edit().putInt("totalCash", Integer.valueOf(totalCashText.getText().toString())).apply();
        }

        calculateResults();
    }

    public void calculateResults() {

        int totalCash = savedTotalCash.getInt("totalCash", 0);

        for (int bucket = 0; bucket < buckets.size() - 1; bucket++) {

            if (totalCash > buckets.get(bucket).maxCash) {

                buckets.get(bucket).currentCash = buckets.get(bucket).maxCash;
                totalCash = totalCash - buckets.get(bucket).currentCash;

            } else {
                if (totalCash > 0) {

                    buckets.get(bucket).currentCash = totalCash;
                    totalCash = 0;

                } else {

                    buckets.get(bucket).currentCash = 0;
                }
            }

        }

        reserveCash = totalCash;

        editEveryBucketText();
    }

    public void editEveryBucketText() {

        for (int i = 0; i < 16; i++) {
            int textViewID = getResources().getIdentifier("textView" + i, "id", getPackageName());

            String bucketText = "  " + buckets.get(i).bucketName +
                    "\n  Limit:\n $" + buckets.get(i).maxCash +
                    "\n  Available:\n  $" + buckets.get(i).currentCash;

            TextView text = (TextView) findViewById(textViewID);
            text.setText(bucketText);

            if (buckets.get(i).bucketName == null) {
                text.setAlpha(0.4f);
                //ToDo add humorous phrases and randomize them on startup
                text.setText("\n  Press\n\n  me");
            }
        }
        setReserveCashText(reserveCash);

    }

    public void setReserveCashText(int reserveCashAmount){
        TextView reserveCashText = (TextView) findViewById(R.id.reserveTextView);
        String reserveCash = Integer.toString(reserveCashAmount);
        String reserveCashString = "";

        for (int character = 0; character < reserveCash.length(); character++){
            reserveCashString += reserveCash.charAt(character) + "\n";

        }

        reserveCashText.setText("$\n" + reserveCashString);

    }

    public void updateSQLwithUserInput() {

        int idint = Integer.valueOf(userIntent.getStringExtra("ID")) + 1; // Adding one because SQL id starts at 1 and the bucket array starts at 0

        if (userIntent.hasExtra("name")) {

            String bucketName = userIntent.getStringExtra("name");
            int maxCash = userIntent.getIntExtra("maxCash", 0);

            String updateNameCommand = "UPDATE bucketTable SET bucketName = '" + bucketName + "' WHERE id = " + idint;
            String updateMaxCashCommand = "UPDATE bucketTable SET maxCash = " + maxCash + " WHERE id = " + idint;

            bucketData.execSQL(updateNameCommand);
            bucketData.execSQL(updateMaxCashCommand);

        } else {

            String updateNameCommand = "UPDATE bucketTable SET bucketName = " + null + " WHERE id = " + idint;
            String updateMaxCashCommand = "UPDATE bucketTable SET maxCash = 0 WHERE id = " + idint;

            bucketData.execSQL(updateNameCommand);
            bucketData.execSQL(updateMaxCashCommand);
        }
    }

    public void resetBucketsToZero() {
        for(int bucket = 0; bucket <buckets.size(); bucket++){
            buckets.get(bucket).bucketName = null;
            buckets.get(bucket).currentCash = 0;
            buckets.get(bucket).maxCash = 0;
        }
    }

    public void resetAllToZero () {
        Toast.makeText(this, "reset function in progress", Toast.LENGTH_SHORT).show();

        for (int sqlBucket = 1; sqlBucket < buckets.size(); sqlBucket++){
            System.out.println(sqlBucket);

            String updateNameCommand = "UPDATE bucketTable SET bucketName = " + null + " WHERE id = " + sqlBucket;
            String updateMaxCashCommand = "UPDATE bucketTable SET maxCash = 0 WHERE id = " + sqlBucket;

            bucketData.execSQL(updateNameCommand);
            bucketData.execSQL(updateMaxCashCommand);
        }

        savedTotalCash.edit().putInt("totalCash", 0).apply();
        totalCashEditText.setHint("Enter total funds available");

        resetBucketsToZero();
        calculateResults();
        editEveryBucketText();
    }

    public void createBuckets(ArrayList<Bucket> buckets) {

        // In later versions a greater number of buckets will be able to be created.

        for (int bucketNumber = 0; bucketNumber < 6; bucketNumber++) {
            EssentialBucket bucket = new EssentialBucket();
            buckets.add(bucketNumber, bucket);
        }

        for (int bucketNumber = 6; bucketNumber < 12; bucketNumber++) {
            RegularBucket bucket = new RegularBucket();
            buckets.add(bucketNumber, bucket);
        }

        for (int bucketNumber = 12; bucketNumber < 17; bucketNumber++) {
            SingleUseBucket bucket = new SingleUseBucket();
            buckets.add(bucketNumber, bucket);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        super.onOptionsItemSelected(menuItem);

        switch (menuItem.getItemId()) {
            case R.id.help:
                //ToDo Write help file and switch to help activity
                Toast.makeText(this, "Help!!", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.reset:
                //ToDo create method to reset all buckets
                resetAllToZero();
                return true;

            default:
                return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        savedTotalCash = this.getSharedPreferences("com.edwardtorpy.cashbuckets", Context.MODE_PRIVATE);
        totalCashEditText = (EditText) findViewById(R.id.totalCashEditText);
        if (savedTotalCash.getInt("totalCash", 0) == 0){
            totalCashEditText.setHint("Enter total funds available");
        } else {
            totalCashEditText.setHint("  $" + savedTotalCash.getInt("totalCash", 0) + " total");
        }

        buckets = new ArrayList<>();
        createBuckets(buckets);

        try {
            bucketData = this.openOrCreateDatabase("BucketData", MODE_PRIVATE, null);
            bucketData.execSQL("CREATE TABLE IF NOT EXISTS bucketTable (id INTEGER PRIMARY KEY, bucketName VARCHAR, maxCash INTEGER(8), currentCash INTEGER(8), date INTEGER(4))");

            userIntent = getIntent();
            if (userIntent.hasExtra("ID")) {
                updateSQLwithUserInput();

            }

            Cursor cursor = bucketData.rawQuery("SELECT * FROM bucketTable", null);

            int bucketNameindex = cursor.getColumnIndex("bucketName");
            int maxCashIndex = cursor.getColumnIndex("maxCash");
            int currentCashIndex = cursor.getColumnIndex("currentCash");

            if (cursor.moveToFirst()){

                buckets.get(0).currentCash = cursor.getInt(currentCashIndex);
                buckets.get(0).maxCash = cursor.getInt(maxCashIndex);
                buckets.get(0).bucketName = cursor.getString(bucketNameindex);

                int counter = 1;
                while (cursor.moveToNext()) {

                    buckets.get(counter).currentCash = cursor.getInt(currentCashIndex);
                    buckets.get(counter).maxCash = cursor.getInt(maxCashIndex);
                    buckets.get(counter).bucketName = cursor.getString(bucketNameindex);
                    counter++;
                }

            } else {
                // This code should only be run on first use
                for (int bucketID = 1; bucketID <17; bucketID++){

                    bucketData.execSQL("INSERT INTO bucketTable (maxCash, currentCash) VALUES (0,0)");

                }
            }

            cursor.close();

        }catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Database Not Working", Toast.LENGTH_SHORT).show();
        }

        calculateResults();
        editEveryBucketText();


    }


    @Override
    public void onBackPressed() {
        finish();
    }
}

class Bucket {
    String bucketName;
    int currentCash;
    int maxCash;

    public String getQuote () {
        String quote1 = "generic bucket";
        return quote1;
    }
}

class EssentialBucket extends Bucket {
    Date nextPaymentDue;

    @Override
    public String getQuote () {
        ArrayList<String> quotes = new ArrayList<>();
        quotes.add(0, "first random essential quote!!");
        quotes.add(1, "2 essential quote!!");
        quotes.add(2, "3 essential quote!!");
        quotes.add(3, "4 random essential quote!!");
        quotes.add(4, "5 essential quote!!");
        quotes.add(5, "6 essential quote!!");
        quotes.add(6, "7 random essential quote!!");
        quotes.add(7, "8 essential quote!!");
        quotes.add(8, "9 essential quote!!");
        quotes.add(9, "10 random essential quote!!");
        quotes.add(10, "11 essential quote!!");
        quotes.add(11, "12 essential quote!!");

        Random rand = new Random();
        return quotes.get(rand.nextInt(quotes.size()));
    }
}

class SingleUseBucket extends Bucket {
    //ToDo implement specific Single Use methods
    @Override
    public String getQuote () {
        ArrayList<String> quotes = new ArrayList<>();
        quotes.add(0, "first single essential quote!!");
        quotes.add(1, "2 single quote!!");
        quotes.add(2, "3 single quote!!");
        quotes.add(3, "4 single essential quote!!");
        quotes.add(4, "5 single quote!!");
        quotes.add(5, "6 single quote!!");
        quotes.add(6, "7 single essential quote!!");
        quotes.add(7, "8 single quote!!");
        quotes.add(8, "9 single quote!!");
        quotes.add(9, "10  single quote!!");
        quotes.add(10, "11 single quote!!");
        quotes.add(11, "12 single quote!!");

        Random rand = new Random();
        return quotes.get(rand.nextInt(quotes.size()));
    }
}

class RegularBucket extends Bucket {
    //ToDo implement specific Single Use methods
    @Override
    public String getQuote () {
        ArrayList<String> quotes = new ArrayList<>();
        quotes.add(0, "first regular quote!!");
        quotes.add(1, "2 regular quote!!");
        quotes.add(2, "3 regular quote!!");
        quotes.add(3, "4 regular essential quote!!");
        quotes.add(4, "5 regular quote!!");
        quotes.add(5, "6 regular quote!!");
        quotes.add(6, "7 regular essential quote!!");
        quotes.add(7, "8 regular quote!!");
        quotes.add(8, "9 regular quote!!");
        quotes.add(9, "10  regular quote!!");
        quotes.add(10, "11 regular quote!!");
        quotes.add(11, "12 regular quote!!");

        Random rand = new Random();
        return quotes.get(rand.nextInt(quotes.size()));
    }
}

class ReserveCash {
    int currentCash;
    int maxCash;
}