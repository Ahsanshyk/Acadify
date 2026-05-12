import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ahsan.acadify.Fragments.HomeFragment
import com.ahsan.acadify.Fragments.IssuedFragment
import com.ahsan.acadify.Fragments.ReturnedFragment

class ViewPagerAdapter(fm:FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 3;
    }

    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> {
                return HomeFragment()
            }
            1 -> {
                return IssuedFragment()
            }
            2 -> {
                return ReturnedFragment()
            }
            else -> {
                return HomeFragment()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position) {
            0 -> {
                return "APPLIED"
            }
            1 -> {
                return "ISSUED"
            }
            2 -> {
                return "RETURNED"
            }
        }
        return super.getPageTitle(position)
    }

}
