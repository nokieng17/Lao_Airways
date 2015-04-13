package nokieng.gdgvientiane.org.laoair;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import com.viewpagerindicator.TabPageIndicator;

import nokieng.gdgvientiane.org.laoair.Helper.AdapterPager;


public class MainActivity extends ActionBarActivity {

    private ViewPager pager;
    private TabPageIndicator indicator;
    private AdapterPager adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new AdapterPager(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setCurrentItem(0);


        indicator = (TabPageIndicator) findViewById(R.id.titlePageIndicator);
        indicator.setViewPager(pager);

        pager.setOnPageChangeListener(indicator);

        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
