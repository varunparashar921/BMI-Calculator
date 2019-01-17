package pl.edu.pwr.swim.marcinkiewicz.szymon.bmi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {
    //===================================================================
    private ShareActionProvider mShareActionProvider;
    private int indexOfCheckedItemInMenu;
    private boolean isItFirstBMICount;
    public static final int METRIC_SYSTEM_INDEX = 1;
    public static final int IMPERIAL_SYSTEM_INDEX = 2;

    //Shared preferences for save ability
    public static final String SHARED_PREFERENCES = "MyPreferencesFile";

    //Possible colors of BMI:
    @BindColor(R.color.colorNotNormalRed) int colorNotNormalRed;
    @BindColor(R.color.colorNotNormalYellow) int colorNotNormalYellow;
    @BindColor(R.color.colorNormalGreen) int colorNormalGreen;

    //Edit text's, text views and button
    @BindView(R.id.heightET) EditText heightET;
    @BindView(R.id.massET) EditText massET;

    @BindView(R.id.yourBMITV) TextView yourBMITV;
    @BindView(R.id.resultTV) TextView resultTV;
    @BindView(R.id.mainLayoutRL) RelativeLayout mainLayout;

    //Button and button onClick()
    @BindView(R.id.countBMIBT) Button countBMIBT;

    @OnClick(R.id.countBMIBT)
    public void onClick(){
        hideKeyboard();
        tryCountBMI();
    }
    //===================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        isItFirstBMICount = true;
        indexOfCheckedItemInMenu = METRIC_SYSTEM_INDEX;                               //App starts with metric system loaded

        restoreSavedData();
    }

    private void restoreSavedData(){
        SharedPreferences settings = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        String massString = settings.getString("massString", "");
        String heightString = settings.getString("heightString", "");
        int bmiCalculator = settings.getInt("bmiCalculator", METRIC_SYSTEM_INDEX);
        massET.setText(massString);
        heightET.setText(heightString);
        indexOfCheckedItemInMenu = bmiCalculator;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        String bmiValue = resultTV.getText().toString();
        int bmiColor = resultTV.getCurrentTextColor();
        savedInstanceState.putString("bmiValue", bmiValue);
        savedInstanceState.putInt("bmiColor", bmiColor);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        String bmiValue = savedInstanceState.getString("bmiValue");
        int bmiColor = savedInstanceState.getInt("bmiColor");
        resultTV.setTextColor(bmiColor);
        resultTV.setText(bmiValue);
    }
    //===================================================================
                            //Menu actions:
    //===================================================================

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.choice_menu, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(isBMICounted())
                    setShareIntent(createShareIntent());
                return false;
            }
        });
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        //Restore chosen calculator after saving
        restoreBMICalculatorInMenu(menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        //Requirement for share enabled: counted BMI in resultTV.
        menu.findItem(R.id.menu_item_share).setEnabled(isBMICounted());
        //Requirement for save enabled: filled and validated mass and height fields.
        menu.findItem(R.id.menu_item_save).setEnabled(areFieldsNotEmpty() && areFieldsValidated(getMass(), getHeight(), getBMICalc()));
        //When launching menu, hide keyboard
        hideKeyboard();
        return true;
    }

    private void restoreBMICalculatorInMenu(Menu menu){
        int systemIdItem = itemIdByIndexInMenu();
        MenuItem systemPd = menu.findItem(systemIdItem);
        systemPd.setChecked(true);
    }

    private int itemIdByIndexInMenu(){
        if(indexOfCheckedItemInMenu == METRIC_SYSTEM_INDEX){
            return R.id.metricSystemItemInMenu;
        }
        else{
            return R.id.imperialSystemItemInMenu;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_item_save:
                validateSaveItemClick();
                return true;
            case R.id.menu_item_author:
                startAuthorActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Choose measure system
    public void onGroupItemClick(MenuItem item){
        switch(item.getItemId()){
            case R.id.metricSystemItemInMenu:
                indexOfCheckedItemInMenu = METRIC_SYSTEM_INDEX;
                break;
            case R.id.imperialSystemItemInMenu:
                indexOfCheckedItemInMenu = IMPERIAL_SYSTEM_INDEX;
                break;
        }
        item.setChecked(true);
        setEditTextsHints(indexOfCheckedItemInMenu);
        clearMassAndHeightET();
    }

    private void saveData(){
        SharedPreferences settings = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("massString", massET.getText().toString());
        editor.putString("heightString", heightET.getText().toString());
        editor.putInt("bmiCalculator", indexOfCheckedItemInMenu);

        editor.commit();
    }

    private void validateSaveItemClick(){
        if(areFieldsNotEmptyMakeToast() && areFieldsValidatedShowAlert(getMass(), getHeight(), getBMICalc())){
            saveData();
            Toast.makeText(MainActivity.this, "Saved height and mass!", Toast.LENGTH_SHORT).show();
        }
    }

    //Set hints depending on measure system
    private void setEditTextsHints(int index){
        switch (index){
            case METRIC_SYSTEM_INDEX:
                massET.setHint(R.string.kg);
                heightET.setHint(R.string.m);
                break;
            case IMPERIAL_SYSTEM_INDEX:
                massET.setHint(R.string.lb);
                heightET.setHint(R.string.in);
                break;
        }
    }
    //===================================================================
                            //Share options:
    //===================================================================

    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    private Intent createShareIntent(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, createShareMessage());
        sendIntent.setType("text/plain");
        return sendIntent;
    }

    private String createShareMessage(){
        StringBuilder messageBuilder = new StringBuilder();
        float bmi = Float.parseFloat(resultTV.getText().toString());
        messageBuilder.append(getString(R.string.my_bmi_is_equal_to_message)+" ");
        messageBuilder.append(String.valueOf(bmi));
        messageBuilder.append("\n\n");
        if(bmi <= 16) messageBuilder.append(getString(R.string.critical_underweight_message));
        else if(bmi <= 18.5) messageBuilder.append(getString(R.string.underweight_message));
        else if(bmi <= 25) messageBuilder.append(getString(R.string.normal_weight_message));
        else if(bmi <= 30) messageBuilder.append(getString(R.string.overweight_message));
        else messageBuilder.append(getString(R.string.critical_overweight_message));
        messageBuilder.append("\n\n");
        messageBuilder.append(getString(R.string.app_info_message));

        return messageBuilder.toString();
    }

    //===================================================================
                             //BMI actions:
    //===================================================================

    private void tryCountBMI(){
        if(areFieldsNotEmptyMakeToast()){
            float mass = getMass();
            float height = getHeight();
            IBMICalc bmiCalc = getBMICalc();

            float countedBMI = countBMI(mass, height, bmiCalc);
            if(countedBMI != -1){
                setBMIOnResultTV(countedBMI);
                if(isItFirstBMICount){
                    showShareInfoFirstTime();
                }
            }
            else{                   //When wrong values were given, clear resultTV
                clearResultTV();
            }
        }
        else{
            clearResultTV();        //When mass or height were not given, clear resultTV
        }
    }

    private float getMass(){
        return Float.parseFloat(massET.getText().toString());
    }

    private float getHeight(){
        return Float.parseFloat(heightET.getText().toString());
    }

    //Depending on indexOfCheckedItemInMenu field return new BMI calculator
    private IBMICalc getBMICalc(){
        if(indexOfCheckedItemInMenu == METRIC_SYSTEM_INDEX){
            return new BMICalcForKg();
        }
        else if (indexOfCheckedItemInMenu == IMPERIAL_SYSTEM_INDEX){
            return new BMICalcForPound();
        }
        else{
            throw new IllegalArgumentException("Unknown checked index in menu");
        }
    }

    private boolean areFieldsValidated(float mass, float height, IBMICalc bmiCalc){
        return (bmiCalc.isValidMass(mass) && bmiCalc.isValidHeight(height));
    }

    private boolean areFieldsValidatedShowAlert(float mass, float height, IBMICalc bmiCalc){
        if(!(bmiCalc.isValidHeight(height)||bmiCalc.isValidMass(mass))){
            showNewAlertDialog(getString(R.string.invalid_mass_and_height));
            return false;
        }
        else if(!bmiCalc.isValidMass(mass)){
            showNewAlertDialog(getString(R.string.invalid_mass));
            return false;
        }
        else if(!bmiCalc.isValidHeight(height)){
            showNewAlertDialog(getString(R.string.invalid_height));
            return false;
        }
        else{
            return true;
        }
    }

    private float countBMI(float mass, float height, IBMICalc bmiCalc){
        if(areFieldsValidatedShowAlert(mass, height, bmiCalc)){
            float result = bmiCalc.countBMI(mass, height);
            return result;
        }
        else{
            return -1;
        }
    }

    private void setBMIOnResultTV(float bmi){
        yourBMITV.setText(getString(R.string.bmi));
        resultTV.setTextColor(countBMIColor(bmi));
        resultTV.setText(String.format(Locale.US, "%.2f",bmi));
    }

    private int countBMIColor(float bmi){
        int colorOfBMI;
        if(bmi <= 16) colorOfBMI = colorNotNormalRed;
        else if(bmi <= 18.5) colorOfBMI = colorNotNormalYellow;
        else if(bmi <= 25) colorOfBMI = colorNormalGreen;
        else if(bmi <= 30) colorOfBMI = colorNotNormalYellow;
        else colorOfBMI = colorNotNormalRed;
        return colorOfBMI;
    }

    private boolean areFieldsNotEmpty(){
        return (massET.length() != 0 && heightET.length() != 0);
    }

    private boolean areFieldsNotEmptyMakeToast(){
        if(massET.getText().length() == 0 && heightET.getText().length() == 0){
            Toast.makeText(MainActivity.this, getString(R.string.input_mass_and_height_toast), Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(massET.getText().length() == 0){
            Toast.makeText(MainActivity.this, getString(R.string.input_mass_toast), Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(heightET.getText().length() == 0){
            Toast.makeText(MainActivity.this, getString(R.string.input_height_toast), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    //===================================================================
                            //Other actions:
    //===================================================================

    //Change activity
    private void startAuthorActivity(){
        Intent authorActivityIntent = new Intent(this, AuthorActivity.class);
        startActivity(authorActivityIntent);
    }

    private boolean isBMICounted(){
        return resultTV.length()!=0;
    }

    private void showShareInfoFirstTime(){
        Toast.makeText(MainActivity.this, getString(R.string.share_bmi_with_your_friends_message), Toast.LENGTH_SHORT).show();
        isItFirstBMICount = false;
    }

    private void showNewAlertDialog(String message){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(getString(R.string.alert));
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void clearResultTV(){
        yourBMITV.setText("");
        resultTV.setText("");
    }

    private void clearMassAndHeightET(){
        massET.setText("");
        heightET.setText("");
    }

    private void hideKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
    }
}