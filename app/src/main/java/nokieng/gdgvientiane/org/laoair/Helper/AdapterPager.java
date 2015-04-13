package nokieng.gdgvientiane.org.laoair.Helper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import nokieng.gdgvientiane.org.laoair.FragmentDomestic;
import nokieng.gdgvientiane.org.laoair.FragmentHistory;
import nokieng.gdgvientiane.org.laoair.FragmentInternational;

/**
 * Created by kieng on 3/31/2015.
 */
public class AdapterPager extends FragmentPagerAdapter {

    private static final int PAGE_NUM = 3;
    private static final String TITLES[] = new String[]{"International", "Domestic", "History"};

    public AdapterPager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return FragmentInternational.newInstance();
            case 1:
                return FragmentDomestic.newInstance();
            case 2:
                return FragmentHistory.newInstance();
            default:
                return new Fragment();
        }
    }

    //This is the title of the page that will apppear on the "tab"
    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public int getCount() {
        return PAGE_NUM;
    }
}
