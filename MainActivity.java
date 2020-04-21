package com.ex;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private final static Random RANDOM = new Random();
    private final static int LEN = 4;
    private static int resultGenotype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void findRoots(View view) {
        String inputA = ((EditText) findViewById(R.id.editText2)).getText().toString();
        String inputB = ((EditText) findViewById(R.id.editText3)).getText().toString();
        String inputC = ((EditText) findViewById(R.id.editText4)).getText().toString();
        String inputD = ((EditText) findViewById(R.id.editText5)).getText().toString();
        String inputY = ((EditText) findViewById(R.id.editText6)).getText().toString();

        if (inputA.isEmpty() || inputB.isEmpty() || inputC.isEmpty()
                || inputD.isEmpty() || inputY.isEmpty()) {
            Toast toast = Toast.makeText(this, "Enter all numbers!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        int[][] population = getPopulation();

        int a = Integer.parseInt(inputA);
        int b = Integer.parseInt(inputB);
        int c = Integer.parseInt(inputC);
        int d = Integer.parseInt(inputD);
        int y = Integer.parseInt(inputY);
        int f;
        int[] deltas = new int[LEN];

        double[] timeOfGeneration = new double[100];
        int[][] answers = new int[100][LEN];

        for (int j = 0, currentMutationPercent = 0; j < timeOfGeneration.length; j++, currentMutationPercent++) {
            long time = System.nanoTime();
            while ((System.nanoTime() - time) / 1_000_000_000.0 < 3.0) {
                for (int i = 0; i < LEN; i++) {
                    f = a * population[i][0] + b * population[i][1] + c * population[i][2] + d * population[i][3];
                    deltas[i] = Math.abs(y - f);
                }

                if (checkDeltas(deltas)) break;

                population = newGeneration(population, getProbabilities(deltas), currentMutationPercent);
            }
            time = System.nanoTime() - time;
            timeOfGeneration[j] = time / 1_000_000_000.0;
            answers[j] = population[resultGenotype];
        }

        int answer = getMinValueIndex(timeOfGeneration);

        TextView textView = findViewById(R.id.textView17);
        textView.setText("Result: " + Arrays.toString(answers[answer]));

        TextView textView2 = findViewById(R.id.textView18);
        textView2.setText("Time(sec): " + timeOfGeneration[answer]);

        TextView textView3 = findViewById(R.id.textView20);
        textView3.setText("Mutation (%): " +  answer);
    }

    private int[][] getPopulation() {
        int[][] population = new int[LEN][LEN];

        for (int i = 0; i < LEN; i++)
            for (int j = 0; j < LEN; j++)
                population[i][j] = RANDOM.nextInt(10);

        return population;
    }

    private boolean checkDeltas(int[] deltas) {
        for (int i = 0; i < LEN; i++)
            if (deltas[i] == 0) {
                resultGenotype = i;
                return true;
            }

        return false;
    }

    private double[] getProbabilities(int[] deltas) {
        double sum = 0.0;
        double[] probabilities = new double[LEN];

        for (int i = 0; i < LEN; i++) {
            probabilities[i] = 1.0 / deltas[i];
            sum += probabilities[i];
        }

        for (int i = 0; i < LEN; i++)
            probabilities[i] /= sum;

        for (int i = 1; i < LEN; i++)
            probabilities[i] += probabilities[i-1];

        return probabilities;
    }

    private int[][] newGeneration(int[][] oldPopulation, double[] probabilities, int mutationPercent) {
        int[][] newGen = new int[LEN][LEN];

        for (int i = 0; i < LEN; i++) {
            int root1 = peekRoot(probabilities);
            int root2 = peekRoot(probabilities);
            newGen[i][0] = oldPopulation[root1][0];
            newGen[i][1] = oldPopulation[root1][1];
            newGen[i][2] = oldPopulation[root2][2];
            newGen[i][3] = oldPopulation[root2][3];
        }

        if (RANDOM.nextDouble() < mutationPercent / 100.0) {
            if (RANDOM.nextDouble() < 0.5) newGen[RANDOM.nextInt(LEN)][RANDOM.nextInt(LEN)]++;
            else newGen[RANDOM.nextInt(LEN)][RANDOM.nextInt(LEN)]--;
        }

        return newGen;
    }

    private int peekRoot(double[] probabilities) {
        double rand = RANDOM.nextDouble();
        if (rand < probabilities[0]) return 0;
        if (rand < probabilities[1]) return 1;
        if (rand < probabilities[2]) return 2;
        return 3;
    }

    private int getMinValueIndex(double[] numbers) {
        double minValue = numbers[0];
        int minIndex = 0;

        for(int i = 1; i < numbers.length; i++) {
            if(numbers[i] < minValue) {
                minValue = numbers[i];
                minIndex = i;
            }
        }

        return minIndex;
    }
}