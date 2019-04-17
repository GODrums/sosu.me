package com.example.sudokusolver;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

//import com.example.sudokusolver.imageProcessing.ImgManipulation;

//import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class SolvingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private int currentNumber;
    private int currentTarget;
    private int[][] array;
    private boolean isStep;
    private boolean isTarget;
    private String currentPhotoPath;

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    /*static {
        if (!OpenCVLoader.initDebug()) {
            System.out.println("Error initializing OpenSC");
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solving);

        array = new int[9][9];
        drawerLayout = findViewById(R.id.solving_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(IconRef.getMenuIcon());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        darkModeHandler(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("darkmode", false));
        init();
    }

    private void init() {
        Bundle extras = getIntent().getExtras();
        if (isStep = extras != null && extras.getBoolean("step")) {
            ((Button) findViewById(R.id.buttonComp)).setText(getResources().getString(R.string.step));
            findViewById(R.id.buttonTarget).setVisibility(View.VISIBLE);
        } else {
            ((Button) findViewById(R.id.buttonComp)).setText(getResources().getString(R.string.complete));
            findViewById(R.id.buttonTarget).setVisibility(View.GONE);
        }
        isTarget=false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)==Configuration.UI_MODE_NIGHT_YES)) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            MainActivity.setSystemBarTheme(this, false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }

    public void checked(View view) {
        isTarget=!isTarget;
        if(currentTarget!=0) {
            findViewById(currentTarget).setBackground(getResources().getDrawable(R.drawable.button_border));
        }
    }

    public void onPicture(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                errorDialog();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Bitmap image = BitmapFactory.decodeFile(currentPhotoPath);
                //ImgManipulation imgManipulation = new ImgManipulation(this, image);
                //array = imgManipulation.getSudokuGridNums();
                if(array==null) errorDialog();
                else System.out.println(Arrays.deepToString(array));
            }
            else if(resultCode == RESULT_CANCELED) {
                errorDialog();
            }
        }
    }

    /**
     * Opens a dialog that displays an error message
     * @usage currently not used
     */
    private void errorDialog() {
        new AlertDialog.Builder(this)
                .setMessage("There was an error in detecting your sudoku. Please try again or enter manually.")
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    /*private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        } else {
            return false;
        }
    }*/

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void onField(View view) {
        if(isTarget) {
            if(currentTarget!=0)
                findViewById(currentTarget).setBackground(getResources().getDrawable(R.drawable.button_border));
            if(currentTarget==view.getId()) {
                currentTarget=0;
                return;
            }
            currentTarget = view.getId();
            findViewById(currentTarget).setBackgroundColor(getResources().getColor(R.color.button_pressed));
            return;
        }
        int number = getNumber(currentNumber);
        if(number==0) return;
        Button button = (Button)findViewById(view.getId());
        button.setText(Integer.toString(number));
    }

    public void onClick(View view) {
        if(currentNumber!=0)
            findViewById(currentNumber).setBackgroundColor(getResources().getColor(R.color.button_default));
        if(currentNumber==view.getId()) {
            currentNumber=0;
            return;
        }
        currentNumber = view.getId();
        findViewById(currentNumber).setBackground(getResources().getDrawable(R.drawable.button_colored));
    }

    public void onComplete(View view) {
        if(view.getId()!=R.id.buttonComp) return;
        initArr();
        try {
            SolvingAlg.testOnErrors(array);
        } catch (WrongSudokuException re) {
            displayFieldError(re.getMessage());
            return;
        }
        SolvingAlg.completeSudoku(array);
        if(isStep) fillSelectedEmpty();
        else readArr();
    }
    private void initArr() {
        save(0,0,((Button)findViewById(R.id.b_1x1)).getText());
        save(0,1,((Button)findViewById(R.id.b_1x2)).getText());
        save(0,2,((Button)findViewById(R.id.b_1x3)).getText());
        save(0,3,((Button)findViewById(R.id.b_1x4)).getText());
        save(0,4,((Button)findViewById(R.id.b_1x5)).getText());
        save(0,5,((Button)findViewById(R.id.b_1x6)).getText());
        save(0,6,((Button)findViewById(R.id.b_1x7)).getText());
        save(0,7,((Button)findViewById(R.id.b_1x8)).getText());
        save(0,8,((Button)findViewById(R.id.b_1x9)).getText());
        save(1,0,((Button)findViewById(R.id.b_2x1)).getText());
        save(1,1,((Button)findViewById(R.id.b_2x2)).getText());
        save(1,2,((Button)findViewById(R.id.b_2x3)).getText());
        save(1,3,((Button)findViewById(R.id.b_2x4)).getText());
        save(1,4,((Button)findViewById(R.id.b_2x5)).getText());
        save(1,5,((Button)findViewById(R.id.b_2x6)).getText());
        save(1,6,((Button)findViewById(R.id.b_2x7)).getText());
        save(1,7,((Button)findViewById(R.id.b_2x8)).getText());
        save(1,8,((Button)findViewById(R.id.b_2x9)).getText());
        save(2,0,((Button)findViewById(R.id.b_3x1)).getText());
        save(2,1,((Button)findViewById(R.id.b_3x2)).getText());
        save(2,2,((Button)findViewById(R.id.b_3x3)).getText());
        save(2,3,((Button)findViewById(R.id.b_3x4)).getText());
        save(2,4,((Button)findViewById(R.id.b_3x5)).getText());
        save(2,5,((Button)findViewById(R.id.b_3x6)).getText());
        save(2,6,((Button)findViewById(R.id.b_3x7)).getText());
        save(2,7,((Button)findViewById(R.id.b_3x8)).getText());
        save(2,8,((Button)findViewById(R.id.b_3x9)).getText());
        save(3,0,((Button)findViewById(R.id.b_4x1)).getText());
        save(3,1,((Button)findViewById(R.id.b_4x2)).getText());
        save(3,2,((Button)findViewById(R.id.b_4x3)).getText());
        save(3,3,((Button)findViewById(R.id.b_4x4)).getText());
        save(3,4,((Button)findViewById(R.id.b_4x5)).getText());
        save(3,5,((Button)findViewById(R.id.b_4x6)).getText());
        save(3,6,((Button)findViewById(R.id.b_4x7)).getText());
        save(3,7,((Button)findViewById(R.id.b_4x8)).getText());
        save(3,8,((Button)findViewById(R.id.b_4x9)).getText());
        save(4,0,((Button)findViewById(R.id.b_5x1)).getText());
        save(4,1,((Button)findViewById(R.id.b_5x2)).getText());
        save(4,2,((Button)findViewById(R.id.b_5x3)).getText());
        save(4,3,((Button)findViewById(R.id.b_5x4)).getText());
        save(4,4,((Button)findViewById(R.id.b_5x5)).getText());
        save(4,5,((Button)findViewById(R.id.b_5x6)).getText());
        save(4,6,((Button)findViewById(R.id.b_5x7)).getText());
        save(4,7,((Button)findViewById(R.id.b_5x8)).getText());
        save(4,8,((Button)findViewById(R.id.b_5x9)).getText());
        save(5,0,((Button)findViewById(R.id.b_6x1)).getText());
        save(5,1,((Button)findViewById(R.id.b_6x2)).getText());
        save(5,2,((Button)findViewById(R.id.b_6x3)).getText());
        save(5,3,((Button)findViewById(R.id.b_6x4)).getText());
        save(5,4,((Button)findViewById(R.id.b_6x5)).getText());
        save(5,5,((Button)findViewById(R.id.b_6x6)).getText());
        save(5,6,((Button)findViewById(R.id.b_6x7)).getText());
        save(5,7,((Button)findViewById(R.id.b_6x8)).getText());
        save(5,8,((Button)findViewById(R.id.b_6x9)).getText());
        save(6,0,((Button)findViewById(R.id.b_7x1)).getText());
        save(6,1,((Button)findViewById(R.id.b_7x2)).getText());
        save(6,2,((Button)findViewById(R.id.b_7x3)).getText());
        save(6,3,((Button)findViewById(R.id.b_7x4)).getText());
        save(6,4,((Button)findViewById(R.id.b_7x5)).getText());
        save(6,5,((Button)findViewById(R.id.b_7x6)).getText());
        save(6,6,((Button)findViewById(R.id.b_7x7)).getText());
        save(6,7,((Button)findViewById(R.id.b_7x8)).getText());
        save(6,8,((Button)findViewById(R.id.b_7x9)).getText());
        save(7,0,((Button)findViewById(R.id.b_8x1)).getText());
        save(7,1,((Button)findViewById(R.id.b_8x2)).getText());
        save(7,2,((Button)findViewById(R.id.b_8x3)).getText());
        save(7,3,((Button)findViewById(R.id.b_8x4)).getText());
        save(7,4,((Button)findViewById(R.id.b_8x5)).getText());
        save(7,5,((Button)findViewById(R.id.b_8x6)).getText());
        save(7,6,((Button)findViewById(R.id.b_8x7)).getText());
        save(7,7,((Button)findViewById(R.id.b_8x8)).getText());
        save(7,8,((Button)findViewById(R.id.b_8x9)).getText());
        save(8,0,((Button)findViewById(R.id.b_9x1)).getText());
        save(8,1,((Button)findViewById(R.id.b_9x2)).getText());
        save(8,2,((Button)findViewById(R.id.b_9x3)).getText());
        save(8,3,((Button)findViewById(R.id.b_9x4)).getText());
        save(8,4,((Button)findViewById(R.id.b_9x5)).getText());
        save(8,5,((Button)findViewById(R.id.b_9x6)).getText());
        save(8,6,((Button)findViewById(R.id.b_9x7)).getText());
        save(8,7,((Button)findViewById(R.id.b_9x8)).getText());
        save(8,8,((Button)findViewById(R.id.b_9x9)).getText());
    }
    private void save(int x, int y, CharSequence cs) {
        if(cs.length()==0) return;
        array[x][y] = Integer.parseInt(cs.toString());
    }
    private void fillSelectedEmpty() {
        if(currentTarget==0) return;
        int arrayNumber=0;
        switch (currentTarget) {
            case R.id.b_1x1: arrayNumber = array[0][0]; break;
            case R.id.b_1x2: arrayNumber = array[0][1]; break;
            case R.id.b_1x3: arrayNumber = array[0][2]; break;
            case R.id.b_1x4: arrayNumber = array[0][3]; break;
            case R.id.b_1x5: arrayNumber = array[0][4]; break;
            case R.id.b_1x6: arrayNumber = array[0][5]; break;
            case R.id.b_1x7: arrayNumber = array[0][6]; break;
            case R.id.b_1x8: arrayNumber = array[0][7]; break;
            case R.id.b_1x9: arrayNumber = array[0][8]; break;
            case R.id.b_2x1: arrayNumber = array[1][0]; break;
            case R.id.b_2x2: arrayNumber = array[1][1]; break;
            case R.id.b_2x3: arrayNumber = array[1][2]; break;
            case R.id.b_2x4: arrayNumber = array[1][3]; break;
            case R.id.b_2x5: arrayNumber = array[1][4]; break;
            case R.id.b_2x6: arrayNumber = array[1][5]; break;
            case R.id.b_2x7: arrayNumber = array[1][6]; break;
            case R.id.b_2x8: arrayNumber = array[1][7]; break;
            case R.id.b_2x9: arrayNumber = array[1][8]; break;
            case R.id.b_3x1: arrayNumber = array[2][0]; break;
            case R.id.b_3x2: arrayNumber = array[2][1]; break;
            case R.id.b_3x3: arrayNumber = array[2][2]; break;
            case R.id.b_3x4: arrayNumber = array[2][3]; break;
            case R.id.b_3x5: arrayNumber = array[2][4]; break;
            case R.id.b_3x6: arrayNumber = array[2][5]; break;
            case R.id.b_3x7: arrayNumber = array[2][6]; break;
            case R.id.b_3x8: arrayNumber = array[2][7]; break;
            case R.id.b_3x9: arrayNumber = array[2][8]; break;
            case R.id.b_4x1: arrayNumber = array[3][0]; break;
            case R.id.b_4x2: arrayNumber = array[3][1]; break;
            case R.id.b_4x3: arrayNumber = array[3][2]; break;
            case R.id.b_4x4: arrayNumber = array[3][3]; break;
            case R.id.b_4x5: arrayNumber = array[3][4]; break;
            case R.id.b_4x6: arrayNumber = array[3][5]; break;
            case R.id.b_4x7: arrayNumber = array[3][6]; break;
            case R.id.b_4x8: arrayNumber = array[3][7]; break;
            case R.id.b_4x9: arrayNumber = array[3][8]; break;
            case R.id.b_5x1: arrayNumber = array[4][0]; break;
            case R.id.b_5x2: arrayNumber = array[4][1]; break;
            case R.id.b_5x3: arrayNumber = array[4][2]; break;
            case R.id.b_5x4: arrayNumber = array[4][3]; break;
            case R.id.b_5x5: arrayNumber = array[4][4]; break;
            case R.id.b_5x6: arrayNumber = array[4][5]; break;
            case R.id.b_5x7: arrayNumber = array[4][6]; break;
            case R.id.b_5x8: arrayNumber = array[4][7]; break;
            case R.id.b_5x9: arrayNumber = array[4][8]; break;
            case R.id.b_6x1: arrayNumber = array[5][0]; break;
            case R.id.b_6x2: arrayNumber = array[5][1]; break;
            case R.id.b_6x3: arrayNumber = array[5][2]; break;
            case R.id.b_6x4: arrayNumber = array[5][3]; break;
            case R.id.b_6x5: arrayNumber = array[5][4]; break;
            case R.id.b_6x6: arrayNumber = array[5][5]; break;
            case R.id.b_6x7: arrayNumber = array[5][6]; break;
            case R.id.b_6x8: arrayNumber = array[5][7]; break;
            case R.id.b_6x9: arrayNumber = array[5][8]; break;
            case R.id.b_7x1: arrayNumber = array[6][0]; break;
            case R.id.b_7x2: arrayNumber = array[6][1]; break;
            case R.id.b_7x3: arrayNumber = array[6][2]; break;
            case R.id.b_7x4: arrayNumber = array[6][3]; break;
            case R.id.b_7x5: arrayNumber = array[6][4]; break;
            case R.id.b_7x6: arrayNumber = array[6][5]; break;
            case R.id.b_7x7: arrayNumber = array[6][6]; break;
            case R.id.b_7x8: arrayNumber = array[6][7]; break;
            case R.id.b_7x9: arrayNumber = array[6][8]; break;
            case R.id.b_8x1: arrayNumber = array[7][0]; break;
            case R.id.b_8x2: arrayNumber = array[7][1]; break;
            case R.id.b_8x3: arrayNumber = array[7][2]; break;
            case R.id.b_8x4: arrayNumber = array[7][3]; break;
            case R.id.b_8x5: arrayNumber = array[7][4]; break;
            case R.id.b_8x6: arrayNumber = array[7][5]; break;
            case R.id.b_8x7: arrayNumber = array[7][6]; break;
            case R.id.b_8x8: arrayNumber = array[7][7]; break;
            case R.id.b_8x9: arrayNumber = array[7][8]; break;
            case R.id.b_9x1: arrayNumber = array[8][0]; break;
            case R.id.b_9x2: arrayNumber = array[8][1]; break;
            case R.id.b_9x3: arrayNumber = array[8][2]; break;
            case R.id.b_9x4: arrayNumber = array[8][3]; break;
            case R.id.b_9x5: arrayNumber = array[8][4]; break;
            case R.id.b_9x6: arrayNumber = array[8][5]; break;
            case R.id.b_9x7: arrayNumber = array[8][6]; break;
            case R.id.b_9x8: arrayNumber = array[8][7]; break;
            case R.id.b_9x9: arrayNumber = array[8][8]; break;
        }
        ((Button)findViewById(currentTarget)).setText(Integer.toString(arrayNumber));
    }
    private void displayFieldError(String error) {
        new AlertDialog.Builder(this)
                .setMessage("Error: "+error+".\nPlease fix existing issues by taking a look in the rules tab.")
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }
    private void readArr() {
        ((Button)findViewById(R.id.b_1x1)).setText(Integer.toString(array[0][0]));
        ((Button)findViewById(R.id.b_1x2)).setText(Integer.toString(array[0][1]));
        ((Button)findViewById(R.id.b_1x3)).setText(Integer.toString(array[0][2]));
        ((Button)findViewById(R.id.b_1x4)).setText(Integer.toString(array[0][3]));
        ((Button)findViewById(R.id.b_1x5)).setText(Integer.toString(array[0][4]));
        ((Button)findViewById(R.id.b_1x6)).setText(Integer.toString(array[0][5]));
        ((Button)findViewById(R.id.b_1x7)).setText(Integer.toString(array[0][6]));
        ((Button)findViewById(R.id.b_1x8)).setText(Integer.toString(array[0][7]));
        ((Button)findViewById(R.id.b_1x9)).setText(Integer.toString(array[0][8]));
        ((Button)findViewById(R.id.b_2x1)).setText(Integer.toString(array[1][0]));
        ((Button)findViewById(R.id.b_2x2)).setText(Integer.toString(array[1][1]));
        ((Button)findViewById(R.id.b_2x3)).setText(Integer.toString(array[1][2]));
        ((Button)findViewById(R.id.b_2x4)).setText(Integer.toString(array[1][3]));
        ((Button)findViewById(R.id.b_2x5)).setText(Integer.toString(array[1][4]));
        ((Button)findViewById(R.id.b_2x6)).setText(Integer.toString(array[1][5]));
        ((Button)findViewById(R.id.b_2x7)).setText(Integer.toString(array[1][6]));
        ((Button)findViewById(R.id.b_2x8)).setText(Integer.toString(array[1][7]));
        ((Button)findViewById(R.id.b_2x9)).setText(Integer.toString(array[1][8]));
        ((Button)findViewById(R.id.b_3x1)).setText(Integer.toString(array[2][0]));
        ((Button)findViewById(R.id.b_3x2)).setText(Integer.toString(array[2][1]));
        ((Button)findViewById(R.id.b_3x3)).setText(Integer.toString(array[2][2]));
        ((Button)findViewById(R.id.b_3x4)).setText(Integer.toString(array[2][3]));
        ((Button)findViewById(R.id.b_3x5)).setText(Integer.toString(array[2][4]));
        ((Button)findViewById(R.id.b_3x6)).setText(Integer.toString(array[2][5]));
        ((Button)findViewById(R.id.b_3x7)).setText(Integer.toString(array[2][6]));
        ((Button)findViewById(R.id.b_3x8)).setText(Integer.toString(array[2][7]));
        ((Button)findViewById(R.id.b_3x9)).setText(Integer.toString(array[2][8]));
        ((Button)findViewById(R.id.b_4x1)).setText(Integer.toString(array[3][0]));
        ((Button)findViewById(R.id.b_4x2)).setText(Integer.toString(array[3][1]));
        ((Button)findViewById(R.id.b_4x3)).setText(Integer.toString(array[3][2]));
        ((Button)findViewById(R.id.b_4x4)).setText(Integer.toString(array[3][3]));
        ((Button)findViewById(R.id.b_4x5)).setText(Integer.toString(array[3][4]));
        ((Button)findViewById(R.id.b_4x6)).setText(Integer.toString(array[3][5]));
        ((Button)findViewById(R.id.b_4x7)).setText(Integer.toString(array[3][6]));
        ((Button)findViewById(R.id.b_4x8)).setText(Integer.toString(array[3][7]));
        ((Button)findViewById(R.id.b_4x9)).setText(Integer.toString(array[3][8]));
        ((Button)findViewById(R.id.b_5x1)).setText(Integer.toString(array[4][0]));
        ((Button)findViewById(R.id.b_5x2)).setText(Integer.toString(array[4][1]));
        ((Button)findViewById(R.id.b_5x3)).setText(Integer.toString(array[4][2]));
        ((Button)findViewById(R.id.b_5x4)).setText(Integer.toString(array[4][3]));
        ((Button)findViewById(R.id.b_5x5)).setText(Integer.toString(array[4][4]));
        ((Button)findViewById(R.id.b_5x6)).setText(Integer.toString(array[4][5]));
        ((Button)findViewById(R.id.b_5x7)).setText(Integer.toString(array[4][6]));
        ((Button)findViewById(R.id.b_5x8)).setText(Integer.toString(array[4][7]));
        ((Button)findViewById(R.id.b_5x9)).setText(Integer.toString(array[4][8]));
        ((Button)findViewById(R.id.b_6x1)).setText(Integer.toString(array[5][0]));
        ((Button)findViewById(R.id.b_6x2)).setText(Integer.toString(array[5][1]));
        ((Button)findViewById(R.id.b_6x3)).setText(Integer.toString(array[5][2]));
        ((Button)findViewById(R.id.b_6x4)).setText(Integer.toString(array[5][3]));
        ((Button)findViewById(R.id.b_6x5)).setText(Integer.toString(array[5][4]));
        ((Button)findViewById(R.id.b_6x6)).setText(Integer.toString(array[5][5]));
        ((Button)findViewById(R.id.b_6x7)).setText(Integer.toString(array[5][6]));
        ((Button)findViewById(R.id.b_6x8)).setText(Integer.toString(array[5][7]));
        ((Button)findViewById(R.id.b_6x9)).setText(Integer.toString(array[5][8]));
        ((Button)findViewById(R.id.b_7x1)).setText(Integer.toString(array[6][0]));
        ((Button)findViewById(R.id.b_7x2)).setText(Integer.toString(array[6][1]));
        ((Button)findViewById(R.id.b_7x3)).setText(Integer.toString(array[6][2]));
        ((Button)findViewById(R.id.b_7x4)).setText(Integer.toString(array[6][3]));
        ((Button)findViewById(R.id.b_7x5)).setText(Integer.toString(array[6][4]));
        ((Button)findViewById(R.id.b_7x6)).setText(Integer.toString(array[6][5]));
        ((Button)findViewById(R.id.b_7x7)).setText(Integer.toString(array[6][6]));
        ((Button)findViewById(R.id.b_7x8)).setText(Integer.toString(array[6][7]));
        ((Button)findViewById(R.id.b_7x9)).setText(Integer.toString(array[6][8]));
        ((Button)findViewById(R.id.b_8x1)).setText(Integer.toString(array[7][0]));
        ((Button)findViewById(R.id.b_8x2)).setText(Integer.toString(array[7][1]));
        ((Button)findViewById(R.id.b_8x3)).setText(Integer.toString(array[7][2]));
        ((Button)findViewById(R.id.b_8x4)).setText(Integer.toString(array[7][3]));
        ((Button)findViewById(R.id.b_8x5)).setText(Integer.toString(array[7][4]));
        ((Button)findViewById(R.id.b_8x6)).setText(Integer.toString(array[7][5]));
        ((Button)findViewById(R.id.b_8x7)).setText(Integer.toString(array[7][6]));
        ((Button)findViewById(R.id.b_8x8)).setText(Integer.toString(array[7][7]));
        ((Button)findViewById(R.id.b_8x9)).setText(Integer.toString(array[7][8]));
        ((Button)findViewById(R.id.b_9x1)).setText(Integer.toString(array[8][0]));
        ((Button)findViewById(R.id.b_9x2)).setText(Integer.toString(array[8][1]));
        ((Button)findViewById(R.id.b_9x3)).setText(Integer.toString(array[8][2]));
        ((Button)findViewById(R.id.b_9x4)).setText(Integer.toString(array[8][3]));
        ((Button)findViewById(R.id.b_9x5)).setText(Integer.toString(array[8][4]));
        ((Button)findViewById(R.id.b_9x6)).setText(Integer.toString(array[8][5]));
        ((Button)findViewById(R.id.b_9x7)).setText(Integer.toString(array[8][6]));
        ((Button)findViewById(R.id.b_9x8)).setText(Integer.toString(array[8][7]));
        ((Button)findViewById(R.id.b_9x9)).setText(Integer.toString(array[8][8]));
    }

    private int getNumber(int id) {
        switch (id) {
            case R.id.b_1:
                return 1;
            case R.id.b_2:
                return 2;
            case R.id.b_3:
                return 3;
            case R.id.b_4:
                return 4;
            case R.id.b_5:
                return 5;
            case R.id.b_6:
                return 6;
            case R.id.b_7:
                return 7;
            case R.id.b_8:
                return 8;
            case R.id.b_9:
                return 9;
            default:
                return 0;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.completeNav:
                if(!isStep) break;
                getIntent().removeExtra("step");
                init();
                break;
            case R.id.stepNav:
                getIntent().putExtra("step", true);
                init();
                break;
            case R.id.rulesNav:
                startActivity(new Intent(this, RulesActivity.class));
                break;
            case R.id.settingNav:
                startActivity(new Intent(this, SettingsActivity.class));
                darkModeHandler(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("darkmode", false));
                recreate();
                break;
            case R.id.aboutNav:
                MainActivity.displayAbout(this);
                break;
        }
        drawerLayout.closeDrawers();
        return true;
    }

    public void darkModeHandler(boolean darkMode) {
        boolean curDarkMode = false;
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES: curDarkMode=true; break;
        }
        if(curDarkMode!=darkMode) {
            if (darkMode)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            }
            recreate();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        darkModeHandler(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("darkmode", false));
    }
}
