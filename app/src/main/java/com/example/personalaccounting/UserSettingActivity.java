package com.example.personalaccounting;

import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class UserSettingActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);

        myDB = new DatabaseHelper(this);

        budgetTextView = (TextView) findViewById(R.id.userSetting_budget_view);
        budgetInput = (EditText) findViewById(R.id.userSetting_budget_input);
        submitButton = (Button) findViewById(R.id.userSetting_submit_button);
        cancelButton = (Button) findViewById(R.id.userSetting_cancel_button);

        expenseTypeTable = (TableLayout) findViewById(R.id.userSetting_expenseType_table);
        incomeTypeTable = (TableLayout) findViewById(R.id.userSetting_incomeType_table);
        submitButton.setOnClickListener(new submitButtonOnClickListener());
        cancelButton.setOnClickListener(new cancelButtonOnClickListener());

    }

    @Override
    protected void onStart() {
        super.onStart();

        user = myDB.getUserInfo();
        budgetInput.setHint(Double.toString(user.getBudget()));


        expenseTypeList = createCheckBoxList(expenseTypeTable, myDB.getExpenseTypeList());
        incomeTypeList = createCheckBoxList(incomeTypeTable, myDB.getIncomeTypeList());

        addExpenseTypeImageButton = new ImageButton(this);
        addExpenseTypeImageButton.setImageResource(R.drawable.ic_baseline_add_box_24);
        addExpenseTypeImageButton.setOnClickListener(new addExpenseTypeImageButtonOnClickListener());
        expenseTypeTable.addView(addExpenseTypeImageButton);

        addIncomeTypeImageButton = new ImageButton(this);
        addIncomeTypeImageButton.setImageResource(R.drawable.ic_baseline_add_box_24);
        addIncomeTypeImageButton.setOnClickListener(new addIncomeTypeImageButtonOnClickListener());
        incomeTypeTable.addView(addIncomeTypeImageButton);
    }

    private ArrayList<Integer> createCheckBoxList(TableLayout tableLayout, ArrayList<String> stringArrayList) {
        ArrayList<Integer> result = new ArrayList<>();
        for(String string: stringArrayList){
            TableRow tableRow = new TableRow(this);
            CheckBox checkBox = new CheckBox(this);
            EditText budgetInput = new EditText(this);

            tableRow.setWeightSum(2.0f);

            TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT,1.0f);
            checkBox.setLayoutParams(rowParams);
            checkBox.setGravity(Gravity.CENTER);
            checkBox.setText(string);
            checkBox.setId(originalTypeId);
            result.add(originalTypeId++);
            checkBox.setOnCheckedChangeListener( new checkBoxOnCheckedChangeListener());

            budgetInput.setLayoutParams(rowParams);
            budgetInput.setHint(R.string.budget);
            budgetInput.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
            budgetInput.setId(originalTypeBudgetId++);

            tableRow.addView(checkBox);
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

            expenseType.setSingleLine();
            FrameLayout container = new FrameLayout(getApplicationContext());
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            layoutParams.width = 270;
            expenseType.setLayoutParams(layoutParams);
            expenseType.setHint(R.string.addNewType);
            container.addView(expenseType);

            imageButton.setImageResource(R.drawable.ic_baseline_indeterminate_check_box_24);
            imageButton.setOnClickListener(new removeExpenseTypeImageButtonOnClickListener());

            expenseBudget.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            expenseBudget.setHint(R.string.budget);
            expenseBudget.setSingleLine();
            FrameLayout budgetContainer = new FrameLayout(getApplicationContext());
            FrameLayout.LayoutParams budgetLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            budgetLayoutParams.width = 150;
            expenseBudget.setLayoutParams(layoutParams);
            budgetContainer.addView(expenseBudget);

            imageButton.setId(imageButtonId++);
            expenseType.setId(newTypeId++);
            tableRow.setId(tableRowId++);
            expenseBudget.setId(newTypeBudgetId++);
            addExpenseTypeEditTextList.add(expenseType);

            tableRow.addView(imageButton);
            tableRow.addView(container);
            tableRow.addView(budgetContainer);

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
            imageButton.setImageResource(R.drawable.ic_baseline_indeterminate_check_box_24);
            imageButton.setOnClickListener(new removeIncomeTypeImageButtonOnClickListener());

            EditText incomeType = new EditText(getApplicationContext());
            incomeType.setHint(R.string.addNewType);
            incomeType.setSingleLine();
            FrameLayout container = new FrameLayout(getApplicationContext());
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            layoutParams.width = 270;
            incomeType.setLayoutParams(layoutParams);
            container.addView(incomeType);

            EditText incomeTypeBudget = new EditText(getApplicationContext());
            incomeTypeBudget.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            incomeTypeBudget.setHint(R.string.budget);
            incomeTypeBudget.setSingleLine();
            FrameLayout budgetContainer = new FrameLayout(getApplicationContext());
            FrameLayout.LayoutParams budgetLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            budgetLayoutParams.width = 150;
            incomeTypeBudget.setLayoutParams(layoutParams);
            budgetContainer.addView(incomeTypeBudget);

            tableRow.setId(tableRowId++);
            imageButton.setId(imageButtonId++);
            incomeType.setId(newTypeId++);
            incomeTypeBudget.setId(newTypeBudgetId++);

            addIncomeTypeEditTextList.add(incomeType);

            tableRow.addView(imageButton);
            tableRow.addView(container);
            tableRow.addView(budgetContainer);

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
                }

            }
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
            Toast.makeText(UserSettingActivity.this, R.string.ChangeUserSettingSuccessMessage, Toast.LENGTH_SHORT).show();
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