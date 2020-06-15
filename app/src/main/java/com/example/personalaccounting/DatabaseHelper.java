package com.example.personalaccounting;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;


public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "pa.db";

    public static final String BILL_TABLE_NAME = "bill";
    public static final String BILL_TABLE_COL2 = "AMOUNT";
    public static final String BILL_TABLE_COL3 = "YEAR";
    public static final String BILL_TABLE_COL4 = "MONTH";
    public static final String BILL_TABLE_COL5 = "DAY_OF_MONTH";
    public static final String BILL_TABLE_COL6 = "TYPE";
    public static final String BILL_TABLE_COL7 = "REMARK";

    public static final String USER_TABLE_NAME = "user";
    public static final String USER_TABLE_COL2 = "BUDGET";
    public static final String USER_TABLE_COL3 = "MONTHLY_EXPENSE";
    public static final String USER_TABLE_COL4 = "MONTHLY_INCOME";
    public static final String USER_TABLE_COL5 = "START_YEAR";
    public static final String USER_TABLE_COL6 = "START_MONTH";
    public static final String USER_TABLE_COL7 = "START_DAY_OF_MONTH";
    public static final String USER_TABLE_COL8 = "LAST_ACTIVE_YEAR";
    public static final String USER_TABLE_COL9 = "LAST_ACTIVE_MONTH";
    public static final String USER_TABLE_COL10 = "LAST_ACTIVE_DAY_OF_MONTH";


    public static final String EXPENSE_TYPE_TABLE_NAME = "expenseType";
    public static final String EXPENSE_TYPE_TABLE_COL2 = "EXPENSE_TYPE";
    public static final String EXPENSE_TYPE_TABLE_COL3 = "BUDGET";

    public static final String INCOME_TYPE_TABLE_NAME = "incomeType";
    public static final String INCOME_TYPE_TABLE_COL2 = "INCOME_TYPE";
    public static final String INCOME_TYPE_TABLE_COL3 = "BUDGET";

    public DatabaseHelper(Context context){super(context, DATABASE_NAME,null,1);}

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            String createBillTable = "CREATE TABLE " + BILL_TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, "  +
                    BILL_TABLE_COL2 + " DECIMAL(38, 2), " +
                    BILL_TABLE_COL3 + " INTEGER, " +
                    BILL_TABLE_COL4 + " INTEGER, " +
                    BILL_TABLE_COL5 + " INTEGER, " +
                    BILL_TABLE_COL6 + " TEXT, " +
                    BILL_TABLE_COL7 + " TEXT)";
            sqLiteDatabase.execSQL(createBillTable);

            String createUserTable = "CREATE TABLE " + USER_TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    USER_TABLE_COL2 + " DECIMAL(38, 2), " +
                    USER_TABLE_COL3 + " DECIMAL(38, 2), " +
                    USER_TABLE_COL4 + " DECIMAL(38, 2), " +
                    USER_TABLE_COL5 + " INTEGER, " +
                    USER_TABLE_COL6 + " INTEGER, " +
                    USER_TABLE_COL7 + " INTEGER, " +
                    USER_TABLE_COL8 + " INTEGER, " +
                    USER_TABLE_COL9 + " INTEGER, " +
                    USER_TABLE_COL10 + " INTEGER) " ;
            sqLiteDatabase.execSQL(createUserTable);

            String createExpenseTypeTable = "CREATE TABLE " + EXPENSE_TYPE_TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    EXPENSE_TYPE_TABLE_COL2 + " TEXT, " +
                    EXPENSE_TYPE_TABLE_COL3 + " DECIMAL(38,2))";
            sqLiteDatabase.execSQL(createExpenseTypeTable);

            String createIncomeTypeTable = "CREATE TABLE " + INCOME_TYPE_TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    INCOME_TYPE_TABLE_COL2 + " TEXT, " +
                    INCOME_TYPE_TABLE_COL3 + " DECIMAL(38,2))";
            sqLiteDatabase.execSQL(createIncomeTypeTable);

        } catch (SQLException e){
            try{
                throw new IOException(e);
            } catch (IOException e1){
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BILL_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public long addBill(Bill bill) {
        SQLiteDatabase db = this.getWritableDatabase();

        String insertString = "INSERT INTO " + BILL_TABLE_NAME + " (" +
                BILL_TABLE_COL2 + "," + BILL_TABLE_COL3 + "," + BILL_TABLE_COL4 + "," +
                BILL_TABLE_COL5  + "," + BILL_TABLE_COL6 + "," + BILL_TABLE_COL7 + ") VALUES (?,?,?,?,?,?)";

        SQLiteStatement insertStatement =db.compileStatement(insertString);

        insertStatement.bindDouble(1, bill.getAmount());
        insertStatement.bindLong(2, bill.getYear());
        insertStatement.bindLong(3, bill.getMonth());
        insertStatement.bindLong(4, bill.getDayOfMonth());
        insertStatement.bindString(5, bill.getType());
        insertStatement.bindString(6, bill.getRemark());

        long result = insertStatement.executeInsert();

        if(Calendar.getInstance().get(Calendar.MONTH) == bill.getMonth()){
            if(bill.getAmount() > 0)
                increaseUserMonthlyIncome(bill.getAmount());
            else
                increaseUserMonthlyExpense(bill.getAmount());
        }

        return  result;

    }

    public Cursor getBillWithYearAndMonth(int year, int month){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] target = new String[]{String.valueOf(year), String.valueOf(month)};
        return db.rawQuery("SELECT * FROM " + BILL_TABLE_NAME + " WHERE " + BILL_TABLE_COL3 + " = ? AND " + BILL_TABLE_COL4 + " = ?" , target);

    }
    public Cursor getBillTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + BILL_TABLE_NAME, null);
        return data;
    }

    public Bill getBillById(long id){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] target = {Long.toString(id)};
        Cursor billInfo = db.rawQuery("SELECT * FROM " + BILL_TABLE_NAME + " WHERE ID = ? ", target);

        if(billInfo.moveToFirst()){
            return new Bill(billInfo.getInt(0), billInfo.getDouble(1), new GregorianCalendar(billInfo.getInt(2), billInfo.getInt(3),billInfo.getInt(4)),
                    billInfo.getString(5), billInfo.getString(6));
        }
        else
            return null;
    }

    public boolean updateBill(Bill updatedBill){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(BILL_TABLE_COL2, updatedBill.getAmount());
        contentValues.put(BILL_TABLE_COL3, updatedBill.getYear());
        contentValues.put(BILL_TABLE_COL4, updatedBill.getMonth());
        contentValues.put(BILL_TABLE_COL5, updatedBill.getDayOfMonth());
        contentValues.put(BILL_TABLE_COL6, updatedBill.getType());
        contentValues.put(BILL_TABLE_COL7, updatedBill.getRemark());

        checkAffectMonthlyBalance(updatedBill);
        return db.update(BILL_TABLE_NAME, contentValues," ID="+updatedBill.getId(), null) > 0;
    }

    private void  checkAffectMonthlyBalance(Bill updatedBill){
        //get original bill for check
        Bill originalBill = getBillById(updatedBill.getId());
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);

        double originalBillAmount = originalBill.getAmount();
        double updatedBillAmount = updatedBill.getAmount();

        if(originalBill.getMonth() != currentMonth){
            if(updatedBill.getMonth() == currentMonth){
                //bill change from other month to current month
                if(updatedBillAmount < 0){
                    increaseUserMonthlyExpense(updatedBillAmount);
                }
                else{
                    increaseUserMonthlyIncome(updatedBillAmount);
                }
            }
            //no need to handle bill remain other month
        }
        else{
            if(updatedBill.getMonth() == currentMonth){
                //bill remain current month
                if(originalBillAmount < 0){
                    if(updatedBillAmount < 0){
                        //change remain expense
                        increaseUserMonthlyExpense(updatedBillAmount - originalBillAmount);
                    }
                    else{
                        //change from expense to income
                        increaseUserMonthlyExpense(0 - originalBillAmount);
                        increaseUserMonthlyIncome(updatedBillAmount);
                    }
                }
                else{
                    if(updatedBillAmount < 0){
                        //change from income to expense
                        increaseUserMonthlyIncome(0 - originalBillAmount);
                        increaseUserMonthlyExpense(updatedBillAmount);
                    }
                    else{
                        //change remain income
                        increaseUserMonthlyIncome(updatedBillAmount - originalBillAmount);
                    }
                }
            }
            else{
                //bill change from current month to other month
                if(originalBillAmount < 0){
                    increaseUserMonthlyExpense(0 - originalBillAmount);
                }
                else{
                    increaseUserMonthlyIncome(0 - originalBillAmount);
                }
            }
        }
    }

    public boolean deleteBill(long billId){
        SQLiteDatabase db = this.getWritableDatabase();
        //check deleted bill affect monthly balance
        Bill deleteBill = getBillById(billId);
        if(deleteBill.getMonth() == Calendar.getInstance().get(Calendar.MONTH)){
            if ((deleteBill.getAmount() > 0)){
                increaseUserMonthlyIncome(0 - deleteBill.getAmount());
            }
            else{
                increaseUserMonthlyExpense(0 - deleteBill.getAmount());
            }
        }
        return db.delete(BILL_TABLE_NAME," ID= ?", new String[]{String.valueOf(billId)}) > 0;
    }

    public User addUserInfo(){
        User user = new User();
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_TABLE_COL2, user.getBudget());
        contentValues.put(USER_TABLE_COL3, user.getMonthlyExpense());
        contentValues.put(USER_TABLE_COL4, user.getMonthlyIncome());

        Calendar calendar = Calendar.getInstance();
        contentValues.put(USER_TABLE_COL5, calendar.get(Calendar.YEAR));
        contentValues.put(USER_TABLE_COL6, calendar.get(Calendar.MONTH));
        contentValues.put(USER_TABLE_COL7, calendar.get(Calendar.DATE));
        contentValues.put(USER_TABLE_COL8, calendar.get(Calendar.YEAR));
        contentValues.put(USER_TABLE_COL9, calendar.get(Calendar.MONTH));
        contentValues.put(USER_TABLE_COL10, calendar.get(Calendar.DATE));

        db.insert(USER_TABLE_NAME, null, contentValues);

        LinkedHashMap<String, Double> expenseMap = user.getExpenseTypeBudgetMap();
        LinkedHashMap<String, Double> incomeMap = user.getIncomeTypeBudgetMap();
        for(String string:expenseMap.keySet()){
            addExpenseType(string, expenseMap.get(string));
        }
        for(String string:incomeMap.keySet()){
            addIncomeType(string, incomeMap.get(string));
        }
        return user;
    }

    public User getUserInfo(){
        SQLiteDatabase db = this.getReadableDatabase();
        User user = new User();
        Cursor userData = db.rawQuery("SELECT * FROM " + USER_TABLE_NAME, null);
        if(userData.moveToFirst()) {
            user.setBudget(userData.getDouble(1));
            user.setMonthlyExpense(userData.getDouble(2));
            user.setMonthlyIncome(userData.getDouble(3));

            LinkedHashMap<String, Double> expenseMap = new LinkedHashMap<>();
            LinkedHashMap<String, Double> incomeMap = new LinkedHashMap<>();

            Cursor expenseTypeData = db.rawQuery("SELECT * FROM " + EXPENSE_TYPE_TABLE_NAME, null);
            while (expenseTypeData.moveToNext()) {
                expenseMap.put(expenseTypeData.getString(1), expenseTypeData.getDouble(2));
            }
            user.setExpenseTypeBudgetMap(expenseMap);
            Cursor incomeTypeData = db.rawQuery("SELECT * FROM " + INCOME_TYPE_TABLE_NAME, null);
            while (incomeTypeData.moveToNext()) {
                incomeMap.put(incomeTypeData.getString(1), incomeTypeData.getDouble(2));
            }
            user.setIncomeTypeBudgetMap(incomeMap);

            return user;
        }
        else{
            return addUserInfo();
        }
    }

    public Calendar getUserStartDate(){
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor data = db.rawQuery(" SELECT * FROM " + USER_TABLE_NAME, null);
            Calendar calendar = Calendar.getInstance();
            if(data.moveToFirst()){

                calendar.set(data.getInt(4), data.getInt(5), data.getInt(6));
            }
            return calendar;
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public Calendar getUserLastActiveDate(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery(" SELECT * FROM " + USER_TABLE_NAME, null);
        Calendar calendar = Calendar.getInstance();
        if(data.moveToFirst()){
            calendar.set(data.getInt(7), data.getInt(8), data.getInt(9));
        }
        return calendar;
    }

    public void setUserLastActiveDate(Calendar calendar){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_TABLE_COL8, calendar.get(Calendar.YEAR));
        contentValues.put(USER_TABLE_COL9, calendar.get(Calendar.MONTH));
        contentValues.put(USER_TABLE_COL10, calendar.get(Calendar.DATE));

        db.update(USER_TABLE_NAME, contentValues,null, null);
    }

    public boolean addIncomeType(String newIncomeType, double typeBudget){
        SQLiteDatabase db = this.getWritableDatabase();
        String insertString = "INSERT INTO " + INCOME_TYPE_TABLE_NAME + " (" + INCOME_TYPE_TABLE_COL2 + "," + INCOME_TYPE_TABLE_COL3 + ") VALUES (?,?)";
        SQLiteStatement insertStatement =db.compileStatement(insertString);
        insertStatement.bindString(1, newIncomeType);
        insertStatement.bindDouble(2, typeBudget);

        return insertStatement.executeInsert() != -1;
    }

    public boolean addExpenseType(String newExpenseType, double typeBudget){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EXPENSE_TYPE_TABLE_COL2, newExpenseType);
        contentValues.put(EXPENSE_TYPE_TABLE_COL3, typeBudget);

        long result = db.insert(EXPENSE_TYPE_TABLE_NAME, null, contentValues);
        return result != -1;
    }


    public double getUserMonthlyIncome(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor userInfo = db.rawQuery("SELECT * FROM " + USER_TABLE_NAME, null);
        if(userInfo.moveToFirst())
            return userInfo.getDouble(3);
        else
            return 0;
    }

    public double getUserMonthlyExpense(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor userInfo = db.rawQuery("SELECT * FROM " + USER_TABLE_NAME, null);
        if(userInfo.moveToFirst())
            return userInfo.getDouble(2);
        else
            return 0;
    }


    public ArrayList<String> getExpenseTypeList(){

        ArrayList<String> result = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + EXPENSE_TYPE_TABLE_NAME, null);
        while(data.moveToNext()){
            result.add(data.getString(1));
        }

        return result;
    }

    public ArrayList<String> getIncomeTypeList(){
        ArrayList<String> result = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + INCOME_TYPE_TABLE_NAME, null);
        while(data.moveToNext()){
            result.add(data.getString(1));
        }

        return result;
    }

    public void increaseUserMonthlyIncome(double income){
        updateUserMonthlyIncome(getUserMonthlyIncome() + income);
    }

    public void increaseUserMonthlyExpense(double expense){
        updateUserMonthlyExpense(getUserMonthlyExpense() + expense);
    }


    public LinkedHashMap<String, Double> getExpenseTypeBudgetMap(){
        LinkedHashMap<String, Double> result = new LinkedHashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + EXPENSE_TYPE_TABLE_NAME, null);
        while(data.moveToNext()){
            result.put(data.getString(1), data.getDouble(2));
        }

        return result;
    }

    public LinkedHashMap<String, Double> getIncomeTypeBudgetMap(){
        LinkedHashMap<String, Double> result = new LinkedHashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + INCOME_TYPE_TABLE_NAME, null);
        while(data.moveToNext()){
            result.put(data.getString(1), data.getDouble(2));
        }

        return result;
    }

    public void updateUserBudget(double newBudget){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_TABLE_COL2, newBudget);

        db.update(USER_TABLE_NAME, contentValues,null, null);
    }

    public void updateUserMonthlyIncome(double income){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_TABLE_COL4, income);
        db.update(USER_TABLE_NAME, contentValues,null, null);
    }

    public void updateUserMonthlyExpense(double expense){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_TABLE_COL3, expense);
        db.update(USER_TABLE_NAME, contentValues,null, null);
    }

    public void updateExpenseTypeBudget(String type, double budget){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EXPENSE_TYPE_TABLE_COL3, budget);
        String[] updateType = {type};
        db.update(EXPENSE_TYPE_TABLE_NAME, contentValues, EXPENSE_TYPE_TABLE_COL2 + " = ?" , updateType);
    }

    public void deleteExpenseType (ArrayList<String> expenseTypeToBeDeleteList){
        for (String string : expenseTypeToBeDeleteList) deleteExpenseType(string);
    }

    public void deleteExpenseType (String... expenseTypeToBeDelete){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(EXPENSE_TYPE_TABLE_NAME,EXPENSE_TYPE_TABLE_COL2 + " = ?", expenseTypeToBeDelete);
    }

    public void deleteIncomeType(ArrayList<String> incomeTypeToBeDeleteList){
        for (String string : incomeTypeToBeDeleteList) deleteIncomeType(string);
    }

    public void deleteIncomeType(String... incomeTypeToBeDelete){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(INCOME_TYPE_TABLE_NAME,INCOME_TYPE_TABLE_COL2 + " = ?", incomeTypeToBeDelete);
    }


}