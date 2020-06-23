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
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Locale;


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
    public static final String USER_TABLE_COL2 = "BUDGET_EXPENSE";
    public static final String USER_TABLE_COL3 = "BUDGET_INCOME";
    public static final String USER_TABLE_COL4 = "MONTHLY_EXPENSE";
    public static final String USER_TABLE_COL5 = "MONTHLY_INCOME";
    public static final String USER_TABLE_COL6 = "START_YEAR";
    public static final String USER_TABLE_COL7 = "START_MONTH";
    public static final String USER_TABLE_COL8 = "START_DAY_OF_MONTH";
    public static final String USER_TABLE_COL9 = "LAST_ACTIVE_YEAR";
    public static final String USER_TABLE_COL10 = "LAST_ACTIVE_MONTH";
    public static final String USER_TABLE_COL11 = "LAST_ACTIVE_DAY_OF_MONTH";
    public static final String USER_TABLE_COL12 = "LANGUAGE";

    public static final String EXPENSE_TYPE_TABLE_NAME = "expenseType";
    public static final String EXPENSE_TYPE_TABLE_COL2 = "EXPENSE_TYPE";
    public static final String EXPENSE_TYPE_TABLE_COL3 = "BUDGET";

    public static final String INCOME_TYPE_TABLE_NAME = "incomeType";
    public static final String INCOME_TYPE_TABLE_COL2 = "INCOME_TYPE";
    public static final String INCOME_TYPE_TABLE_COL3 = "BUDGET";

    Context context;
    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME,null,1);
        this.context = context;
    }

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
                    USER_TABLE_COL5 + " DECIMAL(38, 2), " +
                    USER_TABLE_COL6 + " INTEGER, " +
                    USER_TABLE_COL7 + " INTEGER, " +
                    USER_TABLE_COL8 + " INTEGER, " +
                    USER_TABLE_COL9 + " INTEGER, " +
                    USER_TABLE_COL10 + " INTEGER, " +
                    USER_TABLE_COL11 + " INTEGER, " +
                    USER_TABLE_COL12 + " TEXT) " ;
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

    public double getMonthlyExpenseOfType(int year, int month, String type){
        SQLiteDatabase db = this.getReadableDatabase();
        double result = 0;
        Cursor data = db.rawQuery("SELECT " + BILL_TABLE_COL2 + " FROM " + BILL_TABLE_NAME + " WHERE " +
                BILL_TABLE_COL3 + " =? AND " + BILL_TABLE_COL4 + " =? AND " + BILL_TABLE_COL6 + "=?", new String[]{String.valueOf(year), String.valueOf(month), type});
        while(data.moveToNext()){
            if(data.getDouble(0 ) < 0)
                result += data.getDouble(0);
        }
        data.close();
        return result;
    }

    public double getMonthlyIncomeOfType(int year, int month, String type){
        SQLiteDatabase db = this.getReadableDatabase();
        double result = 0;
        Cursor data = db.rawQuery("SELECT " + BILL_TABLE_COL2 + " FROM " + BILL_TABLE_NAME + " WHERE " +
                BILL_TABLE_COL3 + " =? AND " + BILL_TABLE_COL4 + " =? AND " + BILL_TABLE_COL6 + "=?", new String[]{String.valueOf(year), String.valueOf(month), type});
        while(data.moveToNext()){
            if(data.getDouble(0 ) > 0)
                result += data.getDouble(0);
        }
        data.close();
        return result;
    }

    public Cursor getBillWithYear(int year){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + BILL_TABLE_NAME + " WHERE " + BILL_TABLE_COL3 + " = ? " , new String[]{String.valueOf(year)});
    }

    public Cursor getBillWithYearAndMonth(int year, int month){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] target = new String[]{String.valueOf(year), String.valueOf(month)};
        return db.rawQuery("SELECT * FROM " + BILL_TABLE_NAME + " WHERE " + BILL_TABLE_COL3 + " = ? AND " + BILL_TABLE_COL4 + " = ?" , target);

    }
    public Cursor getBillTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + BILL_TABLE_NAME, null);
    }

    public Bill getBillById(long id){
        Bill result = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String[] target = {Long.toString(id)};
        Cursor billInfo = db.rawQuery("SELECT * FROM " + BILL_TABLE_NAME + " WHERE ID = ? ", target);

        if(billInfo.moveToFirst()){
            result = new Bill(billInfo.getInt(0), billInfo.getDouble(1), new GregorianCalendar(billInfo.getInt(2), billInfo.getInt(3),billInfo.getInt(4)),
                    billInfo.getString(5), billInfo.getString(6));
        }
        billInfo.close();
        return result;
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
        User user = new User(context);
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_TABLE_COL2, user.getExpenseBudget());
        contentValues.put(USER_TABLE_COL4, user.getMonthlyExpense());
        contentValues.put(USER_TABLE_COL5, user.getMonthlyIncome());

        Calendar calendar = Calendar.getInstance();
        contentValues.put(USER_TABLE_COL6, calendar.get(Calendar.YEAR));
        contentValues.put(USER_TABLE_COL7, calendar.get(Calendar.MONTH));
        contentValues.put(USER_TABLE_COL8, calendar.get(Calendar.DATE));
        contentValues.put(USER_TABLE_COL9, calendar.get(Calendar.YEAR));
        contentValues.put(USER_TABLE_COL10, calendar.get(Calendar.MONTH));
        contentValues.put(USER_TABLE_COL11, calendar.get(Calendar.DATE));
        contentValues.put(USER_TABLE_COL12, Locale.getDefault().getDisplayLanguage());
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

    public String getUserLanguage(){
        String result = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor userData = db.rawQuery("SELECT * FROM " + USER_TABLE_NAME, null);
        if(userData.moveToFirst()) {
                result = userData.getString(10);
        }
        userData.close();
        return result;
    }

    public void setUserLanguage(String string){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_TABLE_COL12, string);
        db.update(USER_TABLE_NAME, contentValues,null, null);
        updateTypeLanguage(string);
    }

    private void updateTypeLanguage(String string){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues languageContentValues = new ContentValues();
        if(string.equals("English")){
            languageContentValues.put(EXPENSE_TYPE_TABLE_COL2, context.getResources().getString(R.string.clothing));
            db.update(EXPENSE_TYPE_TABLE_NAME, languageContentValues, EXPENSE_TYPE_TABLE_COL2 +" = ?", new String[]{"衣物"});

            languageContentValues.clear();
            languageContentValues.put(EXPENSE_TYPE_TABLE_COL2, context.getResources().getString(R.string.food));
            db.update(EXPENSE_TYPE_TABLE_NAME, languageContentValues, EXPENSE_TYPE_TABLE_COL2 +" = ?", new String[]{"飲食"});

            languageContentValues.clear();
            languageContentValues.put(EXPENSE_TYPE_TABLE_COL2, context.getResources().getString(R.string.housing));
            db.update(EXPENSE_TYPE_TABLE_NAME, languageContentValues, EXPENSE_TYPE_TABLE_COL2 +" = ?", new String[]{"住屋"});

            languageContentValues.clear();
            languageContentValues.put(EXPENSE_TYPE_TABLE_COL2, context.getResources().getString(R.string.transport));
            db.update(EXPENSE_TYPE_TABLE_NAME, languageContentValues, EXPENSE_TYPE_TABLE_COL2 +" = ?", new String[]{"交通"});

            languageContentValues.clear();
            languageContentValues.put(EXPENSE_TYPE_TABLE_COL2, context.getResources().getString(R.string.other));
            db.update(EXPENSE_TYPE_TABLE_NAME, languageContentValues, EXPENSE_TYPE_TABLE_COL2 +" = ?", new String[]{"其他"});


            languageContentValues.clear();
            languageContentValues.put(INCOME_TYPE_TABLE_COL2, context.getResources().getString(R.string.salary));
            db.update(INCOME_TYPE_TABLE_NAME, languageContentValues, INCOME_TYPE_TABLE_COL2 +" = ?", new String[]{"工資"});

            languageContentValues.clear();
            languageContentValues.put(INCOME_TYPE_TABLE_COL2, context.getResources().getString(R.string.other));
            db.update(INCOME_TYPE_TABLE_NAME, languageContentValues, INCOME_TYPE_TABLE_COL2 +" = ?", new String[]{"其他"});

        }
        else if(string.equals("中文")){
            languageContentValues.put(EXPENSE_TYPE_TABLE_COL2, context.getResources().getString(R.string.clothing));
            db.update(EXPENSE_TYPE_TABLE_NAME, languageContentValues, EXPENSE_TYPE_TABLE_COL2 +" = ?", new String[]{"Clothing"});

            languageContentValues.clear();
            languageContentValues.put(EXPENSE_TYPE_TABLE_COL2, context.getResources().getString(R.string.food));
            db.update(EXPENSE_TYPE_TABLE_NAME, languageContentValues, EXPENSE_TYPE_TABLE_COL2 +" = ?", new String[]{"Food"});

            languageContentValues.clear();
            languageContentValues.put(EXPENSE_TYPE_TABLE_COL2, context.getResources().getString(R.string.housing));
            db.update(EXPENSE_TYPE_TABLE_NAME, languageContentValues, EXPENSE_TYPE_TABLE_COL2 +" = ?", new String[]{"Housing"});

            languageContentValues.clear();
            languageContentValues.put(EXPENSE_TYPE_TABLE_COL2, context.getResources().getString(R.string.transport));
            db.update(EXPENSE_TYPE_TABLE_NAME, languageContentValues, EXPENSE_TYPE_TABLE_COL2 +" = ?", new String[]{"Transport"});

            languageContentValues.clear();
            languageContentValues.put(EXPENSE_TYPE_TABLE_COL2, context.getResources().getString(R.string.other));
            db.update(EXPENSE_TYPE_TABLE_NAME, languageContentValues, EXPENSE_TYPE_TABLE_COL2 +" = ?", new String[]{"Other"});


            languageContentValues.clear();
            languageContentValues.put(INCOME_TYPE_TABLE_COL2, context.getResources().getString(R.string.salary));
            db.update(INCOME_TYPE_TABLE_NAME, languageContentValues, INCOME_TYPE_TABLE_COL2 +" = ?", new String[]{"Salary"});

            languageContentValues.clear();
            languageContentValues.put(INCOME_TYPE_TABLE_COL2, context.getResources().getString(R.string.other));
            db.update(INCOME_TYPE_TABLE_NAME, languageContentValues, INCOME_TYPE_TABLE_COL2 +" = ?", new String[]{"Other"});
        }
    }


    public User getUserInfo(){
        SQLiteDatabase db = this.getReadableDatabase();
        User user = new User(context);
        Cursor userData = db.rawQuery("SELECT "+USER_TABLE_COL2 + ", " + USER_TABLE_COL3 + ", " + USER_TABLE_COL4 + ", " + USER_TABLE_COL5 +" FROM " + USER_TABLE_NAME, null);
        if(userData.moveToFirst()) {
            user.setExpenseBudget(userData.getDouble(0));
            user.setIncomeBudget(userData.getDouble(1));
            user.setMonthlyExpense(userData.getDouble(2));
            user.setMonthlyIncome(userData.getDouble(3));

            LinkedHashMap<String, Double> expenseMap = new LinkedHashMap<>();
            LinkedHashMap<String, Double> incomeMap = new LinkedHashMap<>();

            Cursor expenseTypeData = db.rawQuery("SELECT "+ EXPENSE_TYPE_TABLE_COL2 + ", " + EXPENSE_TYPE_TABLE_COL3 +" FROM " + EXPENSE_TYPE_TABLE_NAME, null);
            while (expenseTypeData.moveToNext()) {
                expenseMap.put(expenseTypeData.getString(0), expenseTypeData.getDouble(1));
            }
            user.setExpenseTypeBudgetMap(expenseMap);
            Cursor incomeTypeData = db.rawQuery("SELECT "+INCOME_TYPE_TABLE_COL2 + ", "+ INCOME_TYPE_TABLE_COL3 +" FROM " + INCOME_TYPE_TABLE_NAME, null);
            while (incomeTypeData.moveToNext()) {
                incomeMap.put(incomeTypeData.getString(0), incomeTypeData.getDouble(1));
            }
            user.setIncomeTypeBudgetMap(incomeMap);
            expenseTypeData.close();
            incomeTypeData.close();
            userData.close();
            return user;
        }
        else{
            return addUserInfo();
        }
    }

    public double getUserExpenseBudget(){
        SQLiteDatabase db = this.getReadableDatabase();
        double result = 0;
        Cursor data = db.rawQuery("SELECT " + USER_TABLE_COL2 + " FROM " + USER_TABLE_NAME, null);
        if(data.moveToFirst()){
            result = data.getDouble(0);
        }
        data.close();
        return result;
    }

    public double getUserIncomeBudget(){
        SQLiteDatabase db = this.getReadableDatabase();
        double result = 0;
        Cursor data = db.rawQuery("SELECT " + USER_TABLE_COL3 + " FROM " + USER_TABLE_NAME, null);
        if(data.moveToFirst()){
            result = data.getDouble(0);
        }
        data.close();
        return result;
    }


    public Calendar getUserStartDate(){
        Calendar result = Calendar.getInstance();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery(" SELECT "+ USER_TABLE_COL6 + ", " + USER_TABLE_COL7 + ", " + USER_TABLE_COL8 + " FROM " + USER_TABLE_NAME, null);
        if(data.moveToFirst()){
            result =  new GregorianCalendar(data.getInt(0), data.getInt(1), data.getInt(2));
        }
        data.close();
        return result;

    }

    public Calendar getUserLastActiveDate(){
        Calendar result = Calendar.getInstance();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery(" SELECT "+ USER_TABLE_COL9 + ", " + USER_TABLE_COL10 + ", " + USER_TABLE_COL11 +" FROM " + USER_TABLE_NAME, null);
        if(data.moveToFirst()){
            result =  new GregorianCalendar(data.getInt(0), data.getInt(1), data.getInt(2));
        }
        data.close();
        return result;
    }

    public void setUserLastActiveDate(Calendar calendar){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_TABLE_COL9, calendar.get(Calendar.YEAR));
        contentValues.put(USER_TABLE_COL10, calendar.get(Calendar.MONTH));
        contentValues.put(USER_TABLE_COL11, calendar.get(Calendar.DATE));

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
        double result = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor userInfo = db.rawQuery("SELECT "+ USER_TABLE_COL5 +" FROM " + USER_TABLE_NAME, null);
        if(userInfo.moveToFirst()) {
            result = userInfo.getDouble(0);
        }
        userInfo.close();
        return result;
    }

    public double getUserMonthlyExpense(){
        double result = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor userInfo = db.rawQuery("SELECT "+ USER_TABLE_COL4 +" FROM " + USER_TABLE_NAME, null);
        if(userInfo.moveToFirst()) {
            result = userInfo.getDouble(0);
        }
        userInfo.close();
        return result;
    }


    public ArrayList<String> getExpenseTypeList(){

        ArrayList<String> result = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + EXPENSE_TYPE_TABLE_NAME, null);
        while(data.moveToNext()){
            result.add(data.getString(1));
        }
        data.close();
        return result;
    }

    public ArrayList<String> getIncomeTypeList(){
        ArrayList<String> result = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + INCOME_TYPE_TABLE_NAME, null);
        while(data.moveToNext()){
            result.add(data.getString(1));
        }
        data.close();
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
        data.close();
        return result;
    }

    public LinkedHashMap<String, Double> getIncomeTypeBudgetMap(){
        LinkedHashMap<String, Double> result = new LinkedHashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + INCOME_TYPE_TABLE_NAME, null);
        while(data.moveToNext()){
            result.put(data.getString(1), data.getDouble(2));
        }
        data.close();
        return result;
    }

    public void updateUserExpenseBudget(double newBudget){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_TABLE_COL2, newBudget);

        db.update(USER_TABLE_NAME, contentValues,null, null);
    }

    public void updateUserIncomeBudget(double newBudget){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_TABLE_COL3, newBudget);

        db.update(USER_TABLE_NAME, contentValues,null, null);
    }

    public void updateUserMonthlyIncome(double income){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_TABLE_COL5, income);
        db.update(USER_TABLE_NAME, contentValues,null, null);
    }

    public void updateUserMonthlyExpense(double expense){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_TABLE_COL4, expense);
        db.update(USER_TABLE_NAME, contentValues,null, null);
    }

    public void updateExpenseTypeName(String originalName, String newName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EXPENSE_TYPE_TABLE_COL2, newName);
        db.update(EXPENSE_TYPE_TABLE_NAME, contentValues, EXPENSE_TYPE_TABLE_COL2 + "=?", new String[]{originalName});

        contentValues.clear();
        contentValues.put(BILL_TABLE_COL6, newName);
        db.update(BILL_TABLE_NAME, contentValues, BILL_TABLE_COL2 + "< 0 AND " + BILL_TABLE_COL6 + "=?", new String[]{originalName});
    }

    public double getExpenseTypeBudget(String type){
        double result = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT " + EXPENSE_TYPE_TABLE_COL3 + " FROM " + EXPENSE_TYPE_TABLE_NAME + " WHERE " + EXPENSE_TYPE_TABLE_COL2 + " =?", new String[]{type});
        if(data.moveToFirst()){
            result = data.getDouble(0);
        }
        data.close();
        return result;
    }

    public double getIncomeTypeBudget(String type){
        double result = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT " + INCOME_TYPE_TABLE_COL3 + " FROM " + INCOME_TYPE_TABLE_NAME + " WHERE " + INCOME_TYPE_TABLE_COL2 + " =?", new String[]{type});
        if(data.moveToFirst()){
            result = data.getDouble(0);
        }
        data.close();
        return result;
    }

    public void updateIncomeTypeName(String originalName, String newName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(INCOME_TYPE_TABLE_COL2, newName);
        db.update(INCOME_TYPE_TABLE_NAME, contentValues, INCOME_TYPE_TABLE_COL2 + "=?", new String[]{originalName});

        contentValues.clear();
        contentValues.put(BILL_TABLE_COL6, newName);
        db.update(BILL_TABLE_NAME, contentValues, BILL_TABLE_COL2 + "> 0 AND " + BILL_TABLE_COL6 + "=?", new String[]{originalName});
    }

    public void updateIncomeTypeBudget(String type, double budget){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(INCOME_TYPE_TABLE_COL3, budget);
        db.update(INCOME_TYPE_TABLE_NAME, contentValues, INCOME_TYPE_TABLE_COL2 + " = ?" , new String[]{type});

    }

    public void updateExpenseTypeBudget(String type, double budget){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EXPENSE_TYPE_TABLE_COL3, budget);
        db.update(EXPENSE_TYPE_TABLE_NAME, contentValues, EXPENSE_TYPE_TABLE_COL2 + " = ?" ,  new String[]{type});
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