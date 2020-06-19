package com.example.personalaccounting;

import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;

public class BudgetSettingActivity extends AppCompatActivity {
    DatabaseHelper myDB;
    User user;

    TextView budgetTextView;
    EditText budgetInput;
    Button submitButton;
    Button cancelButton;
    TableLayout expenseTypeTable;
    TableLayout incomeTypeTable;

    ArrayList<Integer> expenseTypeList;
    ArrayList<Integer> incomeTypeList;

    ArrayList<String> removeExpenseTypeList = new ArrayList<>();
    ArrayList<String> removeIncomeTypeList = new ArrayList<>();

    ImageButton addExpenseTypeImageButton;
    ImageButton addIncomeTypeImageButton;

    ArrayList<EditText> addExpenseTypeEditTextList = new ArrayList<>();
    ArrayList<EditText> addIncomeTypeEditTextList = new ArrayList<>();

    int originalTypeId = 100;
    int originalTypeBudgetId = 500;
    int newTypeId = 1000;
    int imageButtonId = 6000;
    int tableRowId = 11000;
    int newTypeBudgetId = 16000;
    int newTypeNameId = 21000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_setting);

        myDB = new DatabaseHelper(this);

        budgetTextView = (TextView) findViewById(R.id.budgetSetting_budget_view);
        budgetInput = (EditText) findViewById(R.id.budgetSetting_budget_input);
        submitButton = (Button) findViewById(R.id.budgetSetting_submit_button);
        cancelButton = (Button) findViewById(R.id.budgetSetting_cancel_button);

        expenseTypeTable = (TableLayout) findViewById(R.id.budgetSetting_expenseType_table);
        incomeTypeTable = (TableLayout) findViewById(R.id.budgetSetting_incomeType_table);
        submitButton.setOnClickListener(new submitButtonOnClickListener());
        cancelButton.setOnClickListener(new cancelButtonOnClickListener());
    }

    @Override
    protected void onStart() {
        super.onStart();

        user = myDB.getUserInfo();
        budgetInput.setHint(Double.toString(user.getBudget()));

        expenseTypeList = createCheckBoxList(expenseTypeTable, myDB.getExpenseTypeList(), myDB.getExpenseTypeBudgetMap());
        incomeTypeList = createCheckBoxList(incomeTypeTable, myDB.getIncomeTypeList(), myDB.getIncomeTypeBudgetMap());

        addExpenseTypeImageButton = new ImageButton(this);
        addExpenseTypeImageButton.setImageResource(R.drawable.ic_baseline_add_box_24);
        addExpenseTypeImageButton.setOnClickListener(new addExpenseTypeImageButtonOnClickListener());
        addExpenseTypeImageButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBackground));
        expenseTypeTable.addView(addExpenseTypeImageButton);

        addIncomeTypeImageButton = new ImageButton(this);
        addIncomeTypeImageButton.setImageResource(R.drawable.ic_baseline_add_box_24);
        addIncomeTypeImageButton.setOnClickListener(new addIncomeTypeImageButtonOnClickListener());
        addIncomeTypeImageButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBackground));
        incomeTypeTable.addView(addIncomeTypeImageButton);
    }

    private ArrayList<Integer> createCheckBoxList(TableLayout tableLayout, ArrayList<String> stringArrayList, HashMap<String, Double> budgetMap) {
        ArrayList<Integer> result = new ArrayList<>();

        for(String string: stringArrayList){
            TableRow tableRow = new TableRow(this);
            CheckBox checkBox = new CheckBox(this);
            EditText newTypeName = new EditText(this);
            EditText budgetInput = new EditText(this);

            tableRow.setWeightSum(3.0f);

            TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT,1.0f);
            checkBox.setLayoutParams(rowParams);
            checkBox.setGravity(Gravity.CENTER);
            checkBox.setText(string);
            checkBox.setId(originalTypeId);
            result.add(originalTypeId++);
            checkBox.setOnCheckedChangeListener( new checkBoxOnCheckedChangeListener());

            newTypeName.setLayoutParams(rowParams);
            newTypeName.setGravity(Gravity.CENTER);
            newTypeName.setHint(string);
            newTypeName.setId(newTypeNameId++);

            budgetInput.setLayoutParams(rowParams);
            budgetInput.setGravity(Gravity.CENTER);
            budgetInput.setGravity(Gravity.CENTER);
            budgetInput.setHint(String.valueOf(budgetMap.getOrDefault(string, 0.0)));
            budgetInput.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
            budgetInput.setId(originalTypeBudgetId++);

            tableRow.addView(checkBox);
            tableRow.addView(newTypeName);
            tableRow.addView(budgetInput);
            tableLayout.addView(tableRow);
        }

        return result;
    }

    private class addExpenseTypeImageButtonOnClickListener implements ImageButton.OnClickListener{

        @Override
        public void onClick(View view) {
            expenseTypeTable.removeView(addExpenseTypeImageButton);

            TableRow tableRow = new TableRow(getApplicationContext());
            ImageButton imageButton = new ImageButton(getApplicationContext());
            EditText expenseType = new EditText(getApplicationContext());
            EditText expenseBudget = new EditText(getApplicationContext());

            tableRow.setWeightSum(3f);
            TableRow.LayoutParams rowParams  = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1.0f);

            expenseType.setSingleLine();
            expenseType.setLayoutParams(rowParams);
            expenseType.setHint(R.string.addNewType);

            imageButton.setImageResource(R.drawable.ic_baseline_indeterminate_check_box_24);
            imageButton.setOnClickListener(new removeExpenseTypeImageButtonOnClickListener());
            imageButton.setLayoutParams(rowParams);

            expenseBudget.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            expenseBudget.setSingleLine();
            expenseBudget.setLayoutParams(rowParams);
            expenseBudget.setHint(R.string.budget);


            imageButton.setId(imageButtonId++);
            expenseType.setId(newTypeId++);
            tableRow.setId(tableRowId++);
            expenseBudget.setId(newTypeBudgetId++);
            addExpenseTypeEditTextList.add(expenseType);

            tableRow.addView(imageButton);
            tableRow.addView(expenseType);
            tableRow.addView(expenseBudget);

            expenseTypeTable.addView(tableRow);
            expenseTypeTable.addView(addExpenseTypeImageButton);

        }
    }

    private class removeExpenseTypeImageButtonOnClickListener implements ImageButton.OnClickListener{

        @Override
        public void onClick(View view) {
            int clickedImageButtonId = view.getId();
            EditText editText = findViewById(clickedImageButtonId - 5000);
            TableRow tableRow = findViewById(clickedImageButtonId + 5000);

            if(addExpenseTypeEditTextList.contains(editText)) {
                addExpenseTypeEditTextList.remove(editText);
            }
            expenseTypeTable.removeView(tableRow);

        }
    }

    private class addIncomeTypeImageButtonOnClickListener implements ImageButton.OnClickListener{

        @Override
        public void onClick(View view) {
            incomeTypeTable.removeView(addIncomeTypeImageButton);

            TableRow tableRow = new TableRow(getApplicationContext());
            ImageButton imageButton = new ImageButton(getApplicationContext());
            EditText incomeType = new EditText(getApplicationContext());
            EditText incomeTypeBudget = new EditText(getApplicationContext());

            tableRow.setWeightSum(3f);
            TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);

            incomeType.setHint(R.string.addNewType);
            incomeType.setSingleLine();
            incomeType.setLayoutParams(rowParams);

            imageButton.setImageResource(R.drawable.ic_baseline_indeterminate_check_box_24);
            imageButton.setOnClickListener(new removeIncomeTypeImageButtonOnClickListener());
            imageButton.setLayoutParams(rowParams);

            incomeTypeBudget.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            incomeTypeBudget.setHint(R.string.budget);
            incomeTypeBudget.setSingleLine();
            incomeTypeBudget.setLayoutParams(rowParams);


            tableRow.setId(tableRowId++);
            imageButton.setId(imageButtonId++);
            incomeType.setId(newTypeId++);
            incomeTypeBudget.setId(newTypeBudgetId++);

            addIncomeTypeEditTextList.add(incomeType);

            tableRow.addView(imageButton);
            tableRow.addView(incomeType);
            tableRow.addView(incomeTypeBudget);

            incomeTypeTable.addView(tableRow);
            incomeTypeTable.addView(addIncomeTypeImageButton);
        }
    }

    private class removeIncomeTypeImageButtonOnClickListener implements ImageButton.OnClickListener{

        @Override
        public void onClick(View view) {
            int clickedImageButtonId = view.getId();
            EditText editText = findViewById(clickedImageButtonId - 5000);
            TableRow tableRow = findViewById(clickedImageButtonId + 5000);

            if(addIncomeTypeEditTextList.contains(editText)){
                addExpenseTypeEditTextList.remove(editText);
            }

            incomeTypeTable.removeView(tableRow);
        }
    }

    private class checkBoxOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            int id = compoundButton.getId();
            CheckBox checkBox = findViewById(id);
            String typeName = checkBox.getText().toString();

            if(isChecked){
                if (expenseTypeList.contains(id)){
                    removeExpenseTypeList.add(typeName);
                }
                else{
                    removeIncomeTypeList.add(typeName);
                }
            }
            else{
                if(removeExpenseTypeList.contains(typeName)){
                    removeExpenseTypeList.remove(typeName);
                }
                else if(removeIncomeTypeList.contains(typeName)){
                    removeIncomeTypeList.remove(typeName);
                }
            }

        }
    }

    private class submitButtonOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            String entry = budgetInput.getText().toString();
            if (entry.length() > 0){
                myDB.updateUserBudget(Double.valueOf(entry));

            }
            //check delete type
            if(removeExpenseTypeList.size() > 0){
                myDB.deleteExpenseType(removeExpenseTypeList);
            }
            if(removeIncomeTypeList.size() > 0){
                myDB.deleteIncomeType(removeIncomeTypeList);
            }

            //check change budget
            for(int id : expenseTypeList){
                CheckBox checkBox = (CheckBox) findViewById(id);

                if(!removeExpenseTypeList.contains(checkBox.getText().toString())){
                    EditText budgetEditText = (EditText) findViewById(id + 400);
                    String budgetString = budgetEditText.getText().toString();
                    if(budgetString.length() > 0){
                        myDB.updateExpenseTypeBudget(checkBox.getText().toString(),Double.valueOf(budgetString));
                    }

                    EditText typeNameText = (EditText) findViewById(id + 20900);
                    String newTypeName = typeNameText.getText().toString();
                    if(newTypeName.length() > 0){
                        myDB.updateExpenseTypeName(checkBox.getText().toString(), newTypeName);
                    }

                }
            }

            for(int id : incomeTypeList){
                CheckBox checkBox = (CheckBox) findViewById(id);
                if(!removeIncomeTypeList.contains(checkBox.getText().toString())){
                    EditText budgetEditText = (EditText) findViewById(id + 400);
                    String budgetString = budgetEditText.getText().toString();
                    if(budgetString.length() > 0){
                        myDB.updateIncomeTypeBudget(checkBox.getText().toString(),Double.valueOf(budgetString));
                    }

                    EditText typeNameText = (EditText) findViewById(id + 20900);
                    String newTypeName = typeNameText.getText().toString();
                    if(newTypeName.length() > 0){
                        myDB.updateIncomeTypeName(checkBox.getText().toString(), newTypeName);
                    }
                }
            }
            //check change budget name

            //check add type
            if(addExpenseTypeEditTextList.size() > 0){
                for(EditText expenseType : addExpenseTypeEditTextList){
                    String typeName = expenseType.getText().toString();
                    if (typeName.length() > 0){
                        int typeId = expenseType.getId();
                        EditText budgetInput = findViewById(( typeId + 15000));
                        String budgetString = budgetInput.getText().toString();
                        if (budgetString.length() > 0){
                            myDB.addExpenseType(typeName, Double.valueOf(budgetString));
                        }
                        else{
                            myDB.addExpenseType(typeName, 0.0);
                        }

                    }
                }
            }
            if (addIncomeTypeEditTextList.size() > 0){
                for(EditText incomeType : addIncomeTypeEditTextList){
                    String typeName = incomeType.getText().toString();
                    if(typeName.length() > 0){
                        int typeId = incomeType.getId();
                        EditText budgetInput = findViewById(typeId + 15000);
                        String budgetString = budgetInput.getText().toString();
                        if(budgetString.length() > 0){
                            myDB.addIncomeType(typeName, Double.valueOf(budgetString));
                        }
                        else{
                            myDB.addIncomeType(typeName, 0.0);
                        }

                    }
                }
            }
            Toast.makeText(BudgetSettingActivity.this, R.string.ChangeBudgetSettingSuccessMessage, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private class cancelButtonOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            finish();
        }
    };
}