package com.example.sudokusolver;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.ArrayList;
import java.util.List;

public class RulesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);

        darkModeHandler(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("darkmode", false));

        drawerLayout = findViewById(R.id.rulesLayout);
        AppBarLayout appBar = findViewById(R.id.appBarLayout);
        appBar.setOutlineProvider(null);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        NavigationView navigationView = findViewById(R.id.nav_view2);
        navigationView.setNavigationItemSelectedListener(this);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(IconRef.getMenuIcon());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ViewPager pager = findViewById(R.id.viewpager);
        setupViewPager(pager);

        WormDotsIndicator wormDotsIndicator = findViewById(R.id.worm_dots_indicator);
        wormDotsIndicator.setViewPager(pager);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)==Configuration.UI_MODE_NIGHT_YES)) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            MainActivity.setSystemBarTheme(this, false);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ColumnPage(), "RULE 1");
        adapter.addFragment(new RowPage(), "RULE 2");
        adapter.addFragment(new FieldPage(), "RULE 3");
        viewPager.setAdapter(adapter);
    }


    /**
     * Opens the navigation drawer then clicked on drawerToggle
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }

    /**
     * Listener for clicks on menu items in the nav drawer
     * @param menuItem
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.completeNav:
                startActivity(new Intent(this, SolvingActivity.class));
                break;
            case R.id.stepNav:
                Intent intent = new Intent(this, SolvingActivity.class);
                intent.putExtra("step", true);
                startActivity(intent);
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

    /**
     * switches the dark mode off/on
     * @param darkMode the desired dark mode value
     */
    public void darkModeHandler(boolean darkMode) {
        boolean curDarkMode = false;
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES: curDarkMode=true; break;
        }
        if(curDarkMode!=darkMode) {
            if (darkMode) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
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

    /**
     * Adapter for the tabbing layout which manages the view pager that controlls swipe gestures
     * @see android.support.v4.view.PagerAdapter
     */
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            System.out.println(true);
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
