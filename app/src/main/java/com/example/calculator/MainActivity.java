package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText inputEdit;
    Button buttonMC, buttonMR, buttonMS, buttonClear, buttonResult,
        buttonRoot, buttonDivide, buttonMultiply, buttonSubtract, buttonAdd,
        button0, button1, button2, button3, button4, button5, button6, button7, button8, button9,
        buttonDecimal;

    List<Double> memoryList = new ArrayList<Double>();
    int memoryPointer = 0;

    private Map<Character, Integer> operationsPriority = new HashMap<Character, Integer>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        operationsPriority.put('-', 0);
        operationsPriority.put('+', 0);
        operationsPriority.put('*', 1);
        operationsPriority.put('/', 1);
        operationsPriority.put('√', 2);

        inputEdit = findViewById(R.id.inputEdit);
        buttonMC = findViewById(R.id.buttonMC);
        buttonMR = findViewById(R.id.buttonMR);
        buttonMS = findViewById(R.id.buttonMS);
        buttonClear = findViewById(R.id.buttonClear);
        buttonResult = findViewById(R.id.buttonResult);
        buttonRoot = findViewById(R.id.buttonRoot);
        buttonDivide = findViewById(R.id.buttonDivide);
        buttonMultiply = findViewById(R.id.buttonMultiply);
        buttonSubtract = findViewById(R.id.buttonSubtract);
        buttonAdd = findViewById(R.id.buttonAdd);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        button6 = findViewById(R.id.button6);
        button7 = findViewById(R.id.button7);
        button8 = findViewById(R.id.button8);
        button9 = findViewById(R.id.button9);
        buttonDecimal = findViewById(R.id.buttonDecimal);

        buttonMC.setOnClickListener(this);
        buttonMR.setOnClickListener(this);
        buttonMS.setOnClickListener(this);
        buttonClear.setOnClickListener(this);
        buttonResult.setOnClickListener(this);
        buttonRoot.setOnClickListener(this);
        buttonDivide.setOnClickListener(this);
        buttonMultiply.setOnClickListener(this);
        buttonSubtract.setOnClickListener(this);
        buttonAdd.setOnClickListener(this);
        button0.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
        button7.setOnClickListener(this);
        button8.setOnClickListener(this);
        button9.setOnClickListener(this);
        buttonDecimal.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String inputText = inputEdit.getText().toString();
        if (v.getId() == R.id.buttonAdd) {
            if (inputText.length() != 0 && !".-+/*√".contains(inputText.substring(inputText.length() - 1)))
                inputEdit.setText(inputText + "+");
        } else if (v.getId() == R.id.buttonClear) {
            inputEdit.setText("");
        } else if (v.getId() == R.id.buttonDecimal) {
            if (inputText.length() == 0 || !Character.isDigit(inputText.charAt(inputText.length() - 1)))
            {
                return;
            }


            for (int i = inputText.length() - 1; i >= 0; i--)
            {
                if(inputText.charAt(i) == '.')
                {
                    return;
                }

                if ("/*-+√".contains(inputText.substring(i, i )))
                {
                    break;
                }
            }

            inputEdit.setText(inputText + ".");
        } else if (v.getId() == R.id.buttonDivide) {
            if (inputText.length() != 0 && !".-+/*√".contains(inputText.substring(inputText.length() - 1)))
                inputEdit.setText(inputText + "/");
        } else if (v.getId() == R.id.buttonMC) {
            memoryList.clear();
            memoryPointer = 0;
        } else if (v.getId() == R.id.buttonMR) {
            if (memoryList.size() == 0){
                return;
            }
            if (memoryList.size() != 0 && memoryList.size() > memoryPointer) {
                inputEdit.setText(memoryList.get(memoryPointer).toString());
                memoryPointer++;
            } else {
                memoryPointer = 0;
                inputEdit.setText(memoryList.get(memoryPointer).toString());
                memoryPointer++;
            }
        } else if (v.getId() == R.id.buttonMS) {
            if (inputText.contains("+") || inputText.contains("-") ||
                    inputText.contains("*") || inputText.contains("/") ||
                    inputText.contains("√")) {
                return;
            }

            if (memoryList.size() > 3) {
                memoryList.remove(2);
            }

            memoryList.add(Double.parseDouble(inputEdit.getText().toString()));
        } else if (v.getId() == R.id.buttonMultiply) {
            if (inputText.length() != 0 && !".-+/*√".contains(inputText.substring(inputText.length() - 1)))
                inputEdit.setText(inputText + "*");
        } else if (v.getId() == R.id.buttonResult) {
            try {
                getPolishNotation();
            } catch (Exception ex) {
                Toast.makeText(this, "Ошибка", Toast.LENGTH_LONG).show();
            }
        } else if (v.getId() == R.id.buttonRoot) {
            if (inputText.length() == 0 || !".-+/*√".contains(inputText.substring(inputText.length() - 1)))
                inputEdit.setText(inputText + "√");
        } else if (v.getId() == R.id.buttonSubtract) {
            if (inputText.length() != 0 && !".-+/*√".contains(inputText.substring(inputText.length() - 1)))
                inputEdit.setText(inputText + "-");
        } else {
            inputEdit.setText(inputEdit.getText() + ((Button) v).getText().toString());
        }
    }

    private void calculatePolishNotation(List<String> polishString) {
        for (int i = 0; i < polishString.size(); i++) {
            if ("√".equals(polishString.get(i))) {
                polishString.set(i - 1, String.valueOf(Math.sqrt(Double.parseDouble(polishString.get(i - 1)))));
                polishString.remove(i);
                i = 0;
            } else if ("*".equals(polishString.get(i))) {
                polishString.set(i - 2, String.valueOf(Double.parseDouble(polishString.get(i - 2)) * Double.parseDouble(polishString.get(i - 1))));
                polishString.subList(i - 1, i + 1).clear();
                i = 0;
            } else if ("/".equals(polishString.get(i))) {
                polishString.set(i - 2, String.valueOf(Double.parseDouble(polishString.get(i - 2)) / Double.parseDouble(polishString.get(i - 1))));
                polishString.subList(i - 1, i + 1).clear();
                i = 0;
            } else if ("+".equals(polishString.get(i))) {
                polishString.set(i - 2, String.valueOf(Double.parseDouble(polishString.get(i - 2)) + Double.parseDouble(polishString.get(i - 1))));
                polishString.subList(i - 1, i + 1).clear();
                i = 0;
            } else if ("-".equals(polishString.get(i))) {
                polishString.set(i - 2, String.valueOf(Double.parseDouble(polishString.get(i - 2)) - Double.parseDouble(polishString.get(i - 1))));
                polishString.subList(i - 1, i + 1).clear();
                i = 0;
            }
        }

        inputEdit.setText(polishString.get(0));
    }

    private void getPolishNotation() {
        if (inputEdit.getText().toString() == "" || inputEdit.getText().length() == 0) {
            inputEdit.setText("0");
        }

        if ("/*-+.√".contains(inputEdit.getText().toString().substring(inputEdit.getText().length() - 1))) {
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
        }

        List<String> outputStr = new ArrayList<>();
        Stack<Character> stack = new Stack<>();
        String tmpDigit = "";

        for (int i = 0; i < inputEdit.getText().length(); i++) {
            if (Character.isDigit(inputEdit.getText().charAt(i)) || inputEdit.getText().charAt(i) == '.') {
                tmpDigit += inputEdit.getText().charAt(i);
            } else {
                if (!tmpDigit.isEmpty()) {
                    outputStr.add(tmpDigit);
                    tmpDigit = "";
                }

                if (stack.isEmpty() || getOperationsPriority(stack.peek()) < getOperationsPriority(inputEdit.getText().charAt(i))) {
                    stack.push(inputEdit.getText().charAt(i));
                } else {
                    boolean flag = true;
                    while (flag) {
                        if (!stack.isEmpty() && getOperationsPriority(stack.peek()) >= getOperationsPriority(inputEdit.getText().charAt(i))) {
                            outputStr.add(stack.pop().toString());
                        } else {
                            stack.push(inputEdit.getText().charAt(i));
                            flag = false;
                        }
                    }
                }
            }

            if (i == inputEdit.getText().length() - 1) {
                outputStr.add(tmpDigit);
                while (!stack.isEmpty()) {
                    outputStr.add(stack.pop().toString());
                }
            }
        }

        calculatePolishNotation(outputStr);
    }

    private int getOperationsPriority(char operator) {
        switch (operator) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            case '√':
                return 3;
            default:
                return 0;
        }
    }
}