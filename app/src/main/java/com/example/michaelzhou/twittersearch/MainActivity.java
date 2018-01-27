package com.example.michaelzhou.twittersearch;

import static spark.Spark.*;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import java.util.Arrays;
import java.lang.String;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    JavaRDD<String> textFile = sc.textFile("hdfs://...");
    JavaPairRDD<String, Integer> counts = textFile
            .flatMap(s -> Arrays.asList(s.split(" ")).iterator())
            .mapToPair(word -> new Tuple2<>(word, 1))
            .reduceByKey((a, b) -> a + b);
    counts.saveAsTextFile("hdfs://...");
}